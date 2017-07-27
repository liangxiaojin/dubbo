package com.liang.service.impl;

import com.liang.service.ArticleService;
import com.liang.service.Userservice;
import com.liang.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by setup on 2017/6/21.
 */
public class userServiceImpl implements Userservice {

    @Autowired
    private ArticleService articleService;
    public String get() {
        return articleService.get();
    }


}
