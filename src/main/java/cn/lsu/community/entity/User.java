package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;
import com.baomidou.mybatisplus.annotations.TableField;

import java.util.Date;

public class User extends BaseEntity<User> {

    private String accountId;

    private String name;

    private String token;

    private String bio;

    private String avatarUrl;

    /**
     * 登录密码
     */
    private String password;

    /**
     * 加密盐
     */
    private String salt;

    /**
     * 性别：男、女、保密
     */
    private String sex;

    /**
     * 出生年月日
     */
    private Date birthday;

    /**
     * 地区
     */
    private String address;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * QQ号码
     */
    private String qq;

    /**
     * 职业
     */
    private String career;

    @TableField(exist = false)
    private boolean myLike;

    @TableField(exist = false)
    private Integer questionCount;

    @TableField(exist = false)
    private Integer likeCount;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId == null ? null : accountId.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token == null ? null : token.trim();
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio == null ? null : bio.trim();
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl == null ? null : avatarUrl.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public boolean isMyLike() {
        return myLike;
    }

    public void setMyLike(boolean myLike) {
        this.myLike = myLike;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }
}