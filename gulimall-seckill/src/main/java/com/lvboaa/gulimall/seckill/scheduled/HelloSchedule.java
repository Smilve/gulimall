package com.lvboaa.gulimall.seckill.scheduled;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/11/2 17:21
 */

@Slf4j
@Component
public class HelloSchedule {

    /**
     *  开启一个定时任务
     *  1、Spring中由6位组成，不允许第7位的年
     *  2、在周几的位置，1-7代表周日到周六，MON-SUN也可以
     *  3、定时任务不应该阻塞(当前任务执行很长，也应该自己执行自己的，下一个定时任务自动启动)
     *      1).把业务的调用用异步调用，放入线程池 CompletableFuture.runAsync()
     *      2).Spring支持定时任务线程池:spring.task.scheduling；不是很好使 TaskSchedulingAutoConfiguration
     *      3).直接开启异步任务:@EnableAsync (开启)，@Async 标注在方法上 TaskExecutionAutoConfiguration
     *      解决：使用异步+定时任务来完成定时任务不阻塞的问题
     */
    @Async
//    @Scheduled(cron = "* * * * * ?")
    public void hello() throws InterruptedException {
        log.info("hello");
        Thread.sleep(3000);
    }
}
