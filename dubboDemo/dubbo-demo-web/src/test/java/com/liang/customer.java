package com.liang;

import com.liang.service.ArticleService;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by setup on 2017/6/21.
 */
public class customer {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:customer.xml");
        context.start();

        ArticleService articleService = (ArticleService)context.getBean("articleService"); // 获取远程服务代理
        String hello = articleService.get(); // 执行远程方法

        System.out.println( hello ); // 显示调用结果
    }
}
