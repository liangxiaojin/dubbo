package com.liang.redis;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.support.incrementer.SybaseAnywhereMaxValueIncrementer;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

/**
 * Created by setup on 2017/6/26.
 * jedis分片测试。需要手动处理事务
 * 分片测试成功,结果：redis1存储的数据总会少于redis2，不知是不是redis1机器本身配置低的问题
 */
public class ShardJedisPoolDemo {
    private ApplicationContext app;
    private ShardedJedisPool shardedJedisPool;

    @Before
    public void before(){
        app = new ClassPathXmlApplicationContext("classpath:spring-jedispool.xml");
        shardedJedisPool = (ShardedJedisPool) app.getBean("shardedJedisPool");
    }

    @Test
    public void test(){
        //切片的写
//        for(int i=0;i<10;i++){
//            ShardedJedis jedis =  shardedJedisPool.getResource();
//            jedis.set("test2"+i,"test2"+i);
//            jedis.close();
//
//        }
        //切片的读
        Long start = System.currentTimeMillis();
        ShardedJedis jedis =  shardedJedisPool.getResource();
        System.out.println(" test1 = "+ jedis.get("test1"));
        Long end = System.currentTimeMillis();
        Long period = end-start;
        System.out.println(" time = "+period);
        shardedJedisPool.destroy();
    }

}
