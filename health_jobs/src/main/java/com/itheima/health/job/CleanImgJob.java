package com.itheima.health.job;

import com.alibaba.dubbo.config.annotation.Reference;
import com.itheima.health.service.SetmealService;
import com.itheima.health.utils.QiNiuUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author mao
 * @version 1.8
 * @时间 2020/11/26  20:55
 */
@Component
public class CleanImgJob {
    //日志对象
    private static final Logger log = LoggerFactory.getLogger(CleanImgJob.class);
    //订阅 alibaba
    @Reference
    private SetmealService setmealService;
    // 发布时应该  @Scheduled(cron = "0 0 2 * * ? *")
    @Scheduled(initialDelay = 3000,fixedDelay = 1000*60*60*24)
    //
    public void cleanImg(){
        //日志记录
        log.info("准备清理");
        //用工具类查询七牛上的图片
        List<String> img7Niu = QiNiuUtils.listFile();
        //记录重要数据用debug
        log.debug("七牛上有{}张图片",null==img7Niu?0:img7Niu.size());
        //调用业务层方法查询数据库的图片文件
        List<String> imgInDB = setmealService.findImgs();
        log.debug("数据库有{}张图片",null==imgInDB?0:imgInDB.size());
        // 执行removeAll        查出的数据都是垃圾图片（七牛有数据库没用上）
        img7Niu.removeAll(imgInDB);
        log.debug("要删除的垃圾图片有{}张",img7Niu.size());
        //把垃圾图片名转成数组
        String[] need2Delete = img7Niu.toArray(new String[]{});
        //调用工具类方法删除垃圾图片
        QiNiuUtils.removeFiles(need2Delete);
        log.info("删除{}张垃圾图片成功",img7Niu.size());
    }
}
