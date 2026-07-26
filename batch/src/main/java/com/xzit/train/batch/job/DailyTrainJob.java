package com.xzit.train.batch.job;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.xzit.train.common.feign.BusinessFeignClient;
import com.xzit.train.common.resp.CommonResp;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

@Slf4j
public class DailyTrainJob implements Job {
    @Resource
    private BusinessFeignClient businessFeignClient;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.info("十五天后每日车次生成开始");
        DateTime now = DateTime.now();
        DateTime dateTime = DateUtil.offsetDay(now, 15);
        Date jdkDate = dateTime.toJdkDate();
        CommonResp<Object> objectCommonResp = businessFeignClient.genDaily(jdkDate);

        log.info("每日车次生成结束:{}", objectCommonResp);
    }
}
