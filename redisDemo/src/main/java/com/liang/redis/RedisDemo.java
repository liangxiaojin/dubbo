package com.liang.redis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

/**
 * Created by setup on 2017/6/14.
 * jedis简单使用
 */
public class RedisDemo {
    public static void main(String[] args){
        /*简单jedis使用*/
        Jedis jedis = new Jedis("175.25.23.125",6381);
        jedis.auth("xiuzhenyuan");
        System.out.println(" "+jedis.ping());
        System.out.println(" yy = "+jedis.get("zz"));
        jedis.close();




    }

}
