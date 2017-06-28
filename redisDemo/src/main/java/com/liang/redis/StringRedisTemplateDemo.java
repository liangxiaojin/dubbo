package com.liang.redis;

import com.liang.redis.util.JedisListUtil;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by mirror on 2017/6/23.
 * spring stringRedisTemplate demo
 */
public class StringRedisTemplateDemo {
    private ApplicationContext app;
    private JedisListUtil listOps;
    private String key = "queue";

    @Before
    public void before() throws Exception {
        app = new ClassPathXmlApplicationContext("classpath:spring-stringredistemplat.xml");
        listOps = (JedisListUtil) app.getBean("redisUtil");

        System.out.println("------------IN---------------");

    }

    //@After
    public void after() {
        // ------------OUT---------------
        System.out.println("------------OUT---------------");
        long length = listOps.length(key);
        for (long i = 0; i < length; i++) {
            String uid = listOps.out(key);
            System.out.println(uid);
        }
    }
    @Test
    public void map(){

         for (int i = 0; i < 100; i++) {
            String uid = "hjdhjdhjf" + i;
            System.out.println(uid);
            listOps.valueSet(uid, uid);
        }
 //       listOps.valueSet("school", "hei");
//        HashMap userMap = new HashMap();
//        userMap.put("uid","009");
//        userMap.put("name","haha");
//        userMap.put("mobile","1655454515");
//        listOps.mapPutAll("005",userMap);
//        List li = listOps.mapGetValueByHk("4");
//        System.out.println(" "+li);
//        Map map = listOps.mapGetEntriesByHk("4");
//        System.out.println(" "+map);
//
//        String a = (String)listOps.mapGet("4","name");
//        System.out.println(" a = "+a);

    }

    @Test
    public void stack() {
        // ------------PUSH---------------
        String key = "stack";
        int len = 5;
        System.out.println("------------PUSH---------------");
        for (int i = 0; i < len; i++) {
            String uid = "u" + System.currentTimeMillis();
            System.out.println(uid);
            listOps.push(key, uid);
        }

        long length = listOps.length(key);
       // assertEquals(len, length);

        // ------------POP---------------
//        System.out.println("------------POP---------------");
//        for (long i = 0; i < length; i++) {
//            String uid = listOps.pop(key);
//            System.out.println(uid);
//        }
        System.out.println("------------map---------------");


    }



    @Test
    public void index() {

        // -------------INDEX-------------
        String value = listOps.index(key, 3);

        assertEquals("u3", value);
    }

    @Test
    public void range() {
        // -------------RANGE-------------
        List<String> list = listOps.range(key, 3, 5);
        boolean result1 = list.contains("u3");
        assertEquals(true, result1);

        boolean result2 = list.contains("u1");
        assertEquals(false, result2);
    }

    @Test
    public void trim() {
        // ------------TRIM---------------
        List<String> list = listOps.range(key, 3, 5);
        listOps.trim(key, 3, 5);
        boolean result3 = list.contains("u1");
        assertEquals(false, result3);
    }

    @Test
    public void set() {
        // ------------SET-----------------
        List<String> list = listOps.range(key, 3, 5);
        listOps.set(key, 4, "ux4");
        boolean result4 = list.contains("u4");

        assertEquals(true, result4);

    }

    @Test
    public void remove() {
        // ------------REMOVE-----------------
        listOps.remove(key, 4, "u4");
        String value = listOps.index(key, 4);
        System.out.println(value);


        //assertEquals(null, value);

    }
}
