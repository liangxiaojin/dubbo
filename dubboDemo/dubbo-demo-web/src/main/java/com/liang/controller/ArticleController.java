package com.liang.controller;

import com.liang.service.ArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by setup on 2017/6/22.
 */
@Controller
public class ArticleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private ArticleService articleService;

    @RequestMapping(value = "/article/info")
    String get(Model model){
        LOGGER.info("jjjjj");
        articleService.set();
        model.addAttribute("info","success");
        return "/info";
    }
}
