package com.system.utils;

import com.system.Service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定期任务
 */
@Component
public class ScheduleJob {

    @Autowired
    private RecordService recordService;

    @Scheduled(cron = "0 0 20 * * *")
    public void execute(){
        try{
            recordService.getSendItem();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
