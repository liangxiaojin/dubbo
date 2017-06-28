package com.liang.redis.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Created by setup on 2017/6/23.
 */
@Component("redisUtil")
public class JedisListUtil {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 简单的set
     * @param k
     * @param v
     */
    public void valueSet(String k,String v){
        stringRedisTemplate.opsForValue().set(k,v);
    }

    /**
     * 简单的get
     * @param k
     */
    public String valueGet(String k){
        return stringRedisTemplate.opsForValue().get(k);
    }



    /**
     * map put操作
     * @param key
     * @param hk
     * @param hv
     */
    public void mapPut(String key, Object hk,Object hv ){
        stringRedisTemplate.opsForHash().put(key, hk, hv);
    }

    /**
     * map get 操作
     * @param key
     * @param hk
     * @return
     */
    public Object mapGet(String key,String hk){
       return stringRedisTemplate.opsForHash().get(key,hk);
    }


    public void mapPutAll(String key, Map map){
        stringRedisTemplate.opsForHash().putAll(key,map);
    }

    public List mapGetValueByHk(String k){
        return stringRedisTemplate.opsForHash().values(k);
    }
    public Map<Object, Object> mapGetEntriesByHk(String k){
        return stringRedisTemplate.opsForHash().entries(k);
    }


    /**
     * 压栈
     *
     * @param key
     * @param value
     * @return
     */
    public Long push(String key, String value) {
        return stringRedisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 出栈
     *
     * @param key
     * @return
     */
    public String pop(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 入队
     *
     * @param key
     * @param value
     * @return
     */
    public Long in(String key, String value) {
        return stringRedisTemplate.opsForList().rightPush(key, value);
    }
//    public Long in2(String key, Object obj) {
//        return stringRedisTemplate.opsForList().rightPush(key, obj);
//    }

    /**
     * 出队
     *
     * @param key
     * @return
     */
    public String out(String key) {
        return stringRedisTemplate.opsForList().leftPop(key);
    }

    /**
     * 栈/队列长
     *
     * @param key
     * @return
     */
    public Long length(String key) {
        return stringRedisTemplate.opsForList().size(key);
    }

    /**
     * 范围检索
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> range(String key, int start, int end) {
        return stringRedisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 移除
     *
     * @param key
     * @param i
     * @param value
     */
    public void remove(String key, long i, String value) {
        stringRedisTemplate.opsForList().remove(key, i, value);
    }

    /**
     * 检索
     *
     * @param key
     * @param index
     * @return
     */
    public String index(String key, long index) {
        return stringRedisTemplate.opsForList().index(key, index);
    }

    /**
     * 置值
     *
     * @param key
     * @param index
     * @param value
     */
    public void set(String key, long index, String value) {
        stringRedisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 裁剪
     *
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, long start, int end) {
        stringRedisTemplate.opsForList().trim(key, start, end);
    }
}
