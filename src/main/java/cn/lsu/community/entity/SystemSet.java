package cn.lsu.community.entity;

import cn.lsu.community.base.BaseEntity;

public class SystemSet extends BaseEntity<SystemSet> {

    private String title;
    private String systemLogo;
    private String background;
    private String publicTitle;
    private String publicWechat;
    private String publicMicroblog;
    private String publicQq;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSystemLogo() {
        return systemLogo;
    }

    public void setSystemLogo(String systemLogo) {
        this.systemLogo = systemLogo;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getPublicTitle() {
        return publicTitle;
    }

    public void setPublicTitle(String publicTitle) {
        this.publicTitle = publicTitle;
    }

    public String getPublicWechat() {
        return publicWechat;
    }

    public void setPublicWechat(String publicWechat) {
        this.publicWechat = publicWechat;
    }

    public String getPublicMicroblog() {
        return publicMicroblog;
    }

    public void setPublicMicroblog(String publicMicroblog) {
        this.publicMicroblog = publicMicroblog;
    }

    public String getPublicQq() {
        return publicQq;
    }

    public void setPublicQq(String publicQq) {
        this.publicQq = publicQq;
    }
}
