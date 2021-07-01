package cn.lsu.community.redis;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class RedisService {


    @Autowired
    JedisPool jedisPool;

    /**
     * 获取某个对象
     */
    public <T> T get(RedisKey prefix, String key, Class<T> clazz) {
        log.warn("【Redis】获取值，键：" + prefix.getPrefix()+":"+ key);
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
            String str = jedis.get(realKey);
            T t = (T) str;
            log.warn("【Redis】结果："+t);
            return t;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 设置定时
     */
    public Long expice(RedisKey prefix, String key, int exTime) {
		log.warn("【Redis】设置定时，键：" + prefix.getPrefix() + key);
		Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            // 生成真正的Key
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
            result = jedis.expire(realKey, exTime);
			log.warn("【Redis】结果："+result);
			return result;
        } finally {
            returnToPool(jedis);
        }
    }



    /**
     * 删除对象
     */
    public Long del(RedisKey prefix, String key) {
		log.warn("【Redis】删除对象，键：" + prefix.getPrefix() +":"+ key);
		Jedis jedis = null;
        Long result = null;
        try {
            jedis = jedisPool.getResource();
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
            result = jedis.del(realKey);
			log.warn("【Redis】结果："+result + ",realKey："+realKey);
			return result;
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 判断key是否存在
     */
    public <T> boolean exists(RedisKey prefix, String key) {
		log.warn("【Redis】是否存在，键：" + prefix.getPrefix() + key);
		Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
			log.warn("【Redis】结果："+jedis.exists(realKey));
			return jedis.exists(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 增加值
     */
    public <T> Long incr(RedisKey prefix, String key) {
		log.warn("【Redis】增加值，键：" + prefix.getPrefix() + key);
		Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
			return jedis.incr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }

    /**
     * 减少值
     */
    public <T> Long decr(RedisKey prefix, String key) {
		log.warn("【Redis】减少值，键：" + prefix.getPrefix() + key);
		Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            //生成真正的key
            String realKey = ObjectUtils.isEmpty(key) ? prefix.getPrefix() : prefix.getPrefix() + ":" + key;
			return jedis.decr(realKey);
        } finally {
            returnToPool(jedis);
        }
    }


    /**
     * 关闭当前连接对象，返回到连接池
     */
    private void returnToPool(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
    
    /**
     * 清空数据库
     */
    public void flush(){
        log.warn("【Redis】清空数据库");
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.flushDB();
        } finally {
            returnToPool(jedis);
        }
    }

}
