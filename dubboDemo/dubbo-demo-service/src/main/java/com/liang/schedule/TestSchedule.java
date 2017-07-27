package com.liang.schedule;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by setup on 2017/7/25.
 */
@Component
public class TestSchedule {

    @Scheduled(cron = "0/30 0/1 * * * ?")
    public void add(){
        StringBuilder stringBuilder = new StringBuilder();
        for(int i=0;i<20;i++){
            stringBuilder.append(" i="+i);
        }
        System.out.println(stringBuilder);
    }

}
