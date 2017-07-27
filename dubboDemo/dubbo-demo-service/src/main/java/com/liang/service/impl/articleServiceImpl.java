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
        return "yes";
    }
    public void set(){
        System.out.println("============");
        for(int i=0;i<10;i++){
            jedisUtil.valueSet("keykeykeykey"+i,"valuevalue"+i);
        }
    }

    public static void main(String[] args) throws Exception {
//        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:provider.xml");
//        context.start();
//        System.in.read(); // 按任意键退出
        int[] a = {1,2,3};
        StringBuilder sb = new StringBuilder("(");
        int i = 0;
        for(int aE : a){
            if(i==a.length-1){
                sb.append(aE+")");
                System.out.println(" sb = "+sb);
                return;
            }
            sb.append(aE+",");
            i++;
        }



    }
}
