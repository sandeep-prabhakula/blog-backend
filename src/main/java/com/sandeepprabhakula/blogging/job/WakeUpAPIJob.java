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

    @Scheduled(cron = "0 */13 * * * ?")
    public void getBlog(){

        String url = "https://codeversechronicles-is8u4959.b4a.run/blog/659981849a56337a2cd1a744";
        String response = restTemplate.getForObject(url,String.class);
        System.out.println(response + new Date());
    }
}
