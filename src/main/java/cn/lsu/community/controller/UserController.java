package cn.lsu.community.controller;

import cn.hutool.crypto.digest.MD5;
import cn.lsu.community.base.BaseController;
import cn.lsu.community.dto.PaginationDTO;
import cn.lsu.community.dto.ResultDTO;
import cn.lsu.community.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.UUID;

@Controller
public class UserController extends BaseController {



    @GetMapping("/userSetting/{section}")
    public String profile(@PathVariable(name = "section")String section,
                          Model model,
                          @RequestParam(name = "page",defaultValue = "1")Integer page,
                          @RequestParam(name = "size",defaultValue = "10")Integer size){
        request.getSession().setAttribute("navbarStatus","");
        User user= (User) request.getSession().getAttribute("user");
        if(user==null){
            return "redirect:/";
        }
        if("userMessage".contains(section)){
            model.addAttribute("section","userMessage");
            model.addAttribute("sectionName","基本资料");
            PaginationDTO paginationDTO=questionService.list(user.getId(),page,size,1);
            model.addAttribute("paginationDTO",paginationDTO);
        }
        return "userSetting";
    }

    @GetMapping("/users")
    public String index(@RequestParam(name ="search",required = false) String search,
                        @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                        @RequestParam(name = "size",defaultValue = "6",required = false)Integer size,
                        HttpServletRequest request,
                        Model model) {

        User user= (User) request.getSession().getAttribute("user");
        PaginationDTO paginationDTO=userService.list(user,search,page,size);
        model.addAttribute("search", search);
        model.addAttribute("paginationDTO",paginationDTO);
        request.getSession().setAttribute("navbarStatus","users");
        return "users";
    }

    @GetMapping("/likeUser")
    public String likeQuestion(@RequestParam(name ="search",required = false) String search,
                               @RequestParam(name = "page",defaultValue = "1",required = false)Integer page,
                               @RequestParam(name = "size",defaultValue = "6",required = false)Integer size,
                               @RequestParam(name ="userId") Long userId){
        userService.changeLikeUser(loginUser.getId(),userId);
        return "redirect:/users?search="+search+"&page="+page;
    }

    @PostMapping("/user/update")
    @ResponseBody
    public ResultDTO updateAdmin(User user,String birthdayStr,String flag,String newPassword){
        // 为修改的对象赋值ID
        user.setId(loginUser.getId());
        // 修改基本信息
        if ("basic".equals(flag)){
            boolean nameEquals = loginUser.getName().equals(user.getName());
            boolean bioEquals = loginUser.getBio().equals(user.getBio());
            boolean addressEquals = loginUser.getAddress().equals(user.getAddress());
            boolean sexEquals = loginUser.getSex().equals(user.getSex());
            boolean careerEquals = loginUser.getCareer().equals(user.getCareer());
            boolean birEquals = new SimpleDateFormat("yyyy年MM月dd日").format(loginUser.getBirthday()).equals(birthdayStr);
            // 只更新修改过的属性
            user.setName(nameEquals?null:user.getName());
            user.setBio(bioEquals?null:user.getBio());
            user.setAddress(addressEquals?null:user.getAddress());
            user.setSex(sexEquals?null:user.getSex());
            user.setCareer(careerEquals?null:user.getCareer());

            try {
                user.setBirthday(birEquals?null:new SimpleDateFormat("yyyy年MM月dd日").parse(birthdayStr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (nameEquals && addressEquals && sexEquals && careerEquals && birEquals){
                return ResultDTO.errorOf(501,"【失败】没有修改任何信息");
            }
        }
        // 修改联系方式
        if ("contact".equals(flag)){
            boolean emailEquals = loginUser.getEmail().equals(user.getEmail());
            boolean phoneEquals = loginUser.getPhone().equals(user.getPhone());
            boolean qqEquals = loginUser.getQq().equals(user.getQq());
            user.setEmail(emailEquals?null:user.getEmail());
            user.setPhone(phoneEquals?null:user.getPhone());
            user.setQq(qqEquals?null:user.getQq());
            if (emailEquals && phoneEquals && qqEquals){
                return ResultDTO.errorOf(501,"【失败】没有修改任何信息");
            }
        }

        // 修改联系方式
        if ("password".equals(flag)){
            User dbUser = userService.findById(loginUser.getId());
            // 获取Md5加密对象
            MD5 md5 = new MD5(dbUser.getSalt().getBytes());
            // 进行判断
            if (dbUser.getPassword().equals(md5.digestHex16(user.getPassword()))){
                //密码正确
                UUID uuid = UUID.randomUUID();
                String substring = uuid.toString().substring(0, 8);
                user.setSalt(substring);
                // 获取Md5加密对象
                MD5 md52 = new MD5(user.getSalt().getBytes());
                user.setPassword(md52.digestHex16(newPassword));
            }else {
                return ResultDTO.errorOf(501,"【失败】密码错误");
            }

        }
        // 修改资料
        if (userService.updateById(user)) {

            // 更新session
            User user1 = userService.findById(user.getId());
            request.getSession().setAttribute("user",user1);
            response.addCookie(new Cookie("token",user1.getToken()));
            return ResultDTO.successOf();
        }else {
            return ResultDTO.errorOf(500,"【失败】服务器异常");
        }

    }
}
