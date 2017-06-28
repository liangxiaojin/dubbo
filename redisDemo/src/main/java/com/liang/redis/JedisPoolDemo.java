package com.liang.redis;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/**
 * Created by setup on 2017/6/26.
 * jedispool使用，需要手动管理事务
 */
public class JedisPoolDemo {
    private ApplicationContext app;
    private JedisPool jedisPool;

    @Before
    public void before(){
        app = new ClassPathXmlApplicationContext("classpath:spring-jedispool.xml");
        jedisPool = (JedisPool) app.getBean("jedisPool");
    }

    @Test
    public void test(){
        Jedis jedis =  jedisPool.getResource();
        jedis.auth("xiuzhenyuan");
        System.out.println(" name = "+jedis.get("name"));
        jedis.close();
    }

}
