package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.User;
import cn.lsu.community.utils.ImageUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Controller
@Slf4j
public class UserController extends BaseController {


    @GetMapping("/userSetting/{section}")
    public String profile(@PathVariable(name = "section") String section,
                          Model model,
                          @RequestParam(name = "page", defaultValue = "1") Integer page,
                          @RequestParam(name = "size", defaultValue = "10") Integer size) {
        request.getSession().setAttribute("navbarStatus", "");
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            return "redirect:/";
        }
        if ("userMessage".contains(section)) {
            model.addAttribute("section", "userMessage");
            model.addAttribute("sectionName", "基本资料");
            PaginationDTO paginationDTO = questionService.list(user.getId(), page, size, 1);
            model.addAttribute("paginationDTO", paginationDTO);
        }
        return "userSetting";
    }

    @GetMapping("/users")
    public String index(@RequestParam(name = "search", required = false) String search,
                        @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                        @RequestParam(name = "size", defaultValue = "6", required = false) Integer size,
                        HttpServletRequest request,
                        Model model) {

        User user = (User) request.getSession().getAttribute("user");
        PaginationDTO paginationDTO = userService.list(user, search, page, size);
        model.addAttribute("search", search);
        model.addAttribute("paginationDTO", paginationDTO);
        request.getSession().setAttribute("navbarStatus", "users");
        return "users";
    }

    @GetMapping("/likeUser")
    public String likeQuestion(@RequestParam(name = "search", required = false) String search,
                               @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
                               @RequestParam(name = "size", defaultValue = "6", required = false) Integer size,
                               @RequestParam(name = "userId") Long userId) {
        userService.changeLikeUser(loginUser.getId(), userId);
        return "redirect:/users?search=" + search + "&page=" + page;
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ResultDTO updateAdmin(User user, String birthdayStr, String flag, String newPassword) {
        // 为修改的对象赋值ID
        user.setId(loginUser.getId());

        // 修改基本信息
        if ("basic".equals(flag)) {
            boolean nameEquals = loginUser.getName().equals(user.getName());
            boolean bioEquals = loginUser.getBio().equals(user.getBio());
            boolean addressEquals = loginUser.getAddress().equals(user.getAddress());
            boolean sexEquals = loginUser.getSex().equals(user.getSex());
            boolean careerEquals = loginUser.getCareer().equals(user.getCareer());
            boolean birEquals = new SimpleDateFormat("yyyy年MM月dd日").format(loginUser.getBirthday()).equals(birthdayStr);
            // 只更新修改过的属性
            user.setName(nameEquals ? null : user.getName());
            user.setBio(bioEquals ? null : user.getBio());
            user.setAddress(addressEquals ? null : user.getAddress());
            user.setSex(sexEquals ? null : user.getSex());
            user.setCareer(careerEquals ? null : user.getCareer());
            try {
                user.setBirthday(birEquals ? null : new SimpleDateFormat("yyyy年MM月dd日").parse(birthdayStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (nameEquals && addressEquals && sexEquals && careerEquals && birEquals) {
                return ResultDTO.errorOf(501, "【失败】没有修改任何信息");
            }
        }
        // 修改联系方式
        if ("contact".equals(flag)) {
            boolean emailEquals = loginUser.getEmail().equals(user.getEmail());
            boolean phoneEquals = loginUser.getPhone().equals(user.getPhone());
            boolean qqEquals = loginUser.getQq().equals(user.getQq());
            user.setEmail(emailEquals ? null : user.getEmail());
            user.setPhone(phoneEquals ? null : user.getPhone());
            user.setQq(qqEquals ? null : user.getQq());
            if (emailEquals && phoneEquals && qqEquals) {
                return ResultDTO.errorOf(501, "【失败】没有修改任何信息");
            }
        }

        // 修改联系方式
        if ("password".equals(flag)) {
            User dbUser = userService.findById(loginUser.getId());
            // 获取Md5加密对象
            MD5 md5 = new MD5(dbUser.getSalt().getBytes());
            // 进行判断
            if (dbUser.getPassword().equals(md5.digestHex16(user.getPassword()))) {
                //密码正确
                UUID uuid = UUID.randomUUID();
                String substring = uuid.toString().substring(0, 8);
                user.setSalt(substring);
                // 获取Md5加密对象
                MD5 md52 = new MD5(user.getSalt().getBytes());
                user.setPassword(md52.digestHex16(newPassword));
            } else {
                return ResultDTO.errorOf(501, "【失败】密码错误");
            }

        }
        // 修改资料
        if (userService.updateById(user)) {

            // 更新session
            User user1 = userService.findById(user.getId());
            request.getSession().setAttribute("user", user1);
            response.addCookie(new Cookie("token", user1.getToken()));
            return ResultDTO.successOf();
        } else {
            return ResultDTO.errorOf(500, "【失败】服务器异常");
        }

    }

    @GetMapping("/admin/userSet")
    public String toAdminIndex(Model model) {
        if (ObjectUtils.isEmpty(admin) || admin.getStatus() != 2) {
            return "redirect:/toAdminLogin";
        }
        PaginationDTO paginationDTO = userService.selectAll();
        model.addAttribute("paginationDTO", paginationDTO);
        return "admin/userSet";
    }

    /**
     * 添加用户
     */
    @PostMapping("/addUser")
    @ResponseBody
    public ResultDTO addUser(User user) {
        // 检查是否已经创建过这个用户
        boolean nameExist = userService.judgeNameExist(user.getName());
        if (nameExist) {
            log.warn("【失败】添加用户，该名称已被占用");
            return ResultDTO.errorOf(501, "【失败】添加用户，该用户名已被占用");
        }
        // 给用户密码进行加密
        UUID uuid = UUID.randomUUID();
        String substring = uuid.toString().substring(0, 8);
        user.setSalt(substring);
        // 获取Md5加密对象
        MD5 md52 = new MD5(user.getSalt().getBytes());
        user.setPassword(md52.digestHex16(user.getPassword()));
        user.setAvatarUrl(ImageUtils.getRandomImg());
        if (userService.insertUser(user)) {
            log.warn("【成功】添加用户");
            return ResultDTO.errorOf(200, "【成功】添加用户");
        } else {
            log.warn("【失败】添加用户");
            return ResultDTO.errorOf(500, "【失败】添加用户");
        }
    }

    /**
     * 前往 编辑用户页面
     */
    @GetMapping("/admin/user/{id}")
    public String adminUserSet(@PathVariable(name = "id") Long id, Model model) {
        log.warn("请求【编辑用户】页面");
        // 如果id为空那么重定向到所有用户页面
        if (id == null) {
            return "redirect:/admin/userSet";
        }
        // 获取编辑用户信息
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/userSet";
        }
        model.addAttribute("editedUser", user);
        return "admin/userUpdate";
    }

    @PostMapping("/admin/user/update")
    @ResponseBody
    public ResultDTO updateUser(User user, String birthdayStr, String flag) {

        User dbUser = userService.findById(user.getId());
        if (ObjectUtils.isEmpty(dbUser)) {
            return ResultDTO.errorOf(502, "【失败】用户不存在");
        }
        // 修改基本信息
        if ("basic".equals(flag)) {
            boolean nameEquals = dbUser.getName().equals(user.getName());
            MD5 md5 = new MD5(dbUser.getSalt().getBytes());
            boolean passwordEquals = dbUser.getPassword().equals(md5.digestHex16(user.getPassword()));
            boolean bioEquals = dbUser.getBio().equals(user.getBio());
            boolean addressEquals = dbUser.getAddress().equals(user.getAddress());
            boolean sexEquals = dbUser.getSex().equals(user.getSex());
            boolean careerEquals = dbUser.getCareer().equals(user.getCareer());
            boolean birEquals = new SimpleDateFormat("yyyy年MM月dd日").format(dbUser.getBirthday()).equals(birthdayStr);
            // 只更新修改过的属性
            user.setName(nameEquals ? null : user.getName());
            user.setBio(bioEquals ? null : user.getBio());
            user.setAddress(addressEquals ? null : user.getAddress());
            user.setSex(sexEquals ? null : user.getSex());
            user.setCareer(careerEquals ? null : user.getCareer());
            try {
                user.setBirthday(birEquals ? null : new SimpleDateFormat("yyyy年MM月dd日").parse(birthdayStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (!passwordEquals) {
                // 获取Md5加密对象
                user.setPassword(md5.digestHex16(user.getPassword()));
            }
            if (nameEquals && addressEquals && sexEquals && careerEquals && birEquals && passwordEquals) {
                return ResultDTO.errorOf(501, "【失败】没有修改任何信息");
            }
        }
        // 修改联系方式
        if ("contact".equals(flag)) {
            boolean emailEquals = dbUser.getEmail().equals(user.getEmail());
            boolean phoneEquals = dbUser.getPhone().equals(user.getPhone());
            boolean qqEquals = dbUser.getQq().equals(user.getQq());
            user.setEmail(emailEquals ? null : user.getEmail());
            user.setPhone(phoneEquals ? null : user.getPhone());
            user.setQq(qqEquals ? null : user.getQq());
            if (emailEquals && phoneEquals && qqEquals) {
                return ResultDTO.errorOf(501, "【失败】没有修改任何信息");
            }
        }

        // 修改资料
        if (userService.updateById(user)) {

            // 更新session
            User user1 = userService.findById(user.getId());
            request.getSession().setAttribute("user", user1);
            response.addCookie(new Cookie("token", user1.getToken()));
            return ResultDTO.successOf();
        } else {
            return ResultDTO.errorOf(500, "【失败】服务器异常");
        }

    }

    /**
     * 修改用户状态
     */
    @GetMapping("/admin/updateUserStatus")
    public String updateUserStatus(@RequestParam(name = "id") Long id,@RequestParam(name = "status") Integer status, Model model) {
        log.warn("请求修改用户状态");
        // 如果id为空那么重定向到所有用户页面
        if (id == null) {
            return "redirect:/admin/userSet";
        }
        // 获取修改用户信息
        User user = userService.findById(id);
        if (user == null) {
            return "redirect:/admin/userSet";
        }
        user.setStatus(status);
        userService.updateById(user);
        return "redirect:/admin/userSet";
    }
}
