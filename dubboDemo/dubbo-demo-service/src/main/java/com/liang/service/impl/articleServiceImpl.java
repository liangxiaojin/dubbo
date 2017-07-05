package com.liang.service.impl;

import com.liang.service.ArticleService;
import com.liang.util.JedisUtil;

import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by setup on 2017/6/21.
 */
public class articleServiceImpl implements ArticleService {
    @Autowired
    private JedisUtil jedisUtil;


    public String get() {
        return "success";
    }
    public void set(){
        System.out.println("============");
        for(int i=0;i<10;i++){
            jedisUtil.valueSet("keykeykeykey"+i,"valuevalue"+i);
        }
    }
}
