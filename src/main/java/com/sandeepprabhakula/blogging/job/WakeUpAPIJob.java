package com.sandeepprabhakula.blogging.job;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Component
public class WakeUpAPIJob {
    @Autowired
    RestTemplate restTemplate;

    @Scheduled(cron = "0 */12 * * * ?")
    public void getBlog(){
        try{
            String url = "https://codeverse-chronicles.onrender.com/blog/659981849a56337a2cd1a744";
            String response = restTemplate.getForObject(url, String.class);
            System.out.println("I'm awake " + new Date());
        }catch (Exception e){
            System.out.println(e.getMessage() + new Date());
        }
    }
}
