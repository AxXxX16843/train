//package com.xzit.train.batch.config;
//
//import com.xzit.train.batch.job.TestJob;
//import org.quartz.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class QuartzConfig {
//
//    @Bean
//    public JobDetail jobDetail() {
//        return JobBuilder.newJob(TestJob.class).
//                withIdentity("test","test")
//                .storeDurably().build();
//    }
//    @Bean
//    public Trigger trigger() {
//        return TriggerBuilder.newTrigger().forJob("test","test")
//                .withIdentity("trigger","trigger")
//                .startNow()
//                .withSchedule(CronScheduleBuilder.cronSchedule("*/2 * * * * ?")).build();
//    }
//
//}
