package cn.lsu.community.enums;

public enum NotificationType {
    REPLAY_QUESTION(1,"回复了你的问题"),
    REPLAY_COMMENT(2,"回复了你的评论"),
    LIKE_YOU(3,"关注了你"),
    YOUR_LIKED_QUESTION_HAVE_NEW_COMMENT(4,"你关注的问题有新的回复"),
    YOUR_LIKED_USER_HAVE_NEW_QUESTION(5,"你关注的用户发布了新的问题");

    private Integer type;
    private String name;

    public Integer getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    NotificationType(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
    public static String getName(Integer type) {
        for (NotificationType value : NotificationType.values()) {
            if(type==value.getType()){
                return value.getName();
            }
        }
        return "";
    }
    public static NotificationType getNotificationType(Integer type) {
        for (NotificationType value : NotificationType.values()) {
            if(type==value.getType()){
                return value;
            }
        }
        return null;
    }
}
