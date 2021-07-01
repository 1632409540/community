package cn.lsu.community.redis;

public class RedisKey {
    private String prefix;

    public RedisKey(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className+":" + prefix;
    }

    public static RedisKey getById = new RedisKey("id");
    public static RedisKey getByTitle = new RedisKey("name");
    public static RedisKey getIndex = new RedisKey("index");
}
