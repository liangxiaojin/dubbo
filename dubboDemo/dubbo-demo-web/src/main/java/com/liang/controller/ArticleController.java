package com.liang.controller;

import com.liang.service.ArticleService;
import com.liang.service.Userservice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by setup on 2017/6/22.
 */
@Controller
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private Userservice userservice;

    @RequestMapping(value = "/article")
    String set(Model model){
        LOGGER.info(" get start ");
        String result = userservice.get();
        model.addAttribute("info",result);
        return "/info";
    }
}
