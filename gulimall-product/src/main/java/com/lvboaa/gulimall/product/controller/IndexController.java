package com.lvboaa.gulimall.product.controller;

import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/13 15:30
 */
@RestController
public class IndexController {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 测试redisson分布式锁
     */
    @RequestMapping("/test")
    public String testRedisson(){
        RLock lock = redisson.getLock("myLock");
        lock.lock(); // 阻塞式等待  默认过期时间30s
        // 解决了两个问题： 锁的自动续期，如果业务超长，运行期间会自动给锁续上30s，不用担心业务过长，锁被删除
        //                加锁的业务一旦完成，就不会给当前锁续期，即使不手动解锁，锁也会在30s之后自动删除

        // 设置过期时间和不设置走的不同的方法(源码)
        // 如果设置了过期时间，就给redis执行脚本，进行占锁，默认时间就是我们指定的时间
        // 看门狗机制：如果没有设置，就是用30*1000 [lockWatchdogTimeout,看门狗默认时间]
        // 只要占锁成功，就会启动一个定时任务(重新设置过期时间，也是看门狗时间，在经过看门狗时间/ 3 的时候续期，也就是每隔10秒续期到30s)
        try{
            System.out.println("加锁成功，执行业务"+Thread.currentThread().getId());
            Thread.sleep(30000);
        }catch (Exception e){

        }finally {
            System.out.println("释放锁"+Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    @RequestMapping("/write")
    public String write(){

        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = readWriteLock.writeLock();
        String s = "";
        try {
            // 该数据加写锁，读数据加读锁：保证一定能读到最新数据
            // 写锁是一个排他锁(互斥锁)。读锁是一个共享锁，写锁没释放，读必须等待
            rLock.lock();
            s= UUID.randomUUID().toString();
            Thread.sleep(10000);
            redisTemplate.opsForValue().set("write",s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            rLock.unlock();
        }
        return s;
    }

    @RequestMapping("/read")
    public String read(){
        RReadWriteLock readWriteLock = redisson.getReadWriteLock("rw-lock");
        // 读锁
        RLock rLock = readWriteLock.readLock();
        String s = "";
        rLock.lock();
        try {
            s= redisTemplate.opsForValue().get("write");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
        }
        return s;
    }

    /**
     * 车库停车
     *
     */
    @GetMapping("/park")
    public String park() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        boolean acquire = park.tryAcquire();// 获取信号，获取值，占一个车位  阻塞方法
        if (acquire){
            // 业务方法
        } else {
            return "error";
        }

        return "ok";
    }

    @GetMapping("/go")
    public String go() throws InterruptedException {
        RSemaphore park = redisson.getSemaphore("park");
        park.release();// 释放一个车位
        return "ok";
    }

    /**
     * 放假，锁门，需要所有同学走完
     * 闭锁
     *
     */
    @GetMapping("/lockDoor")
    public String lockDoor() throws InterruptedException {
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.trySetCount(5);
        door.await(); // 等待闭锁完成，计数为0
        return "放假了。。";
    }

    @GetMapping("/gogogo/{id}")
    public String gogogo(@PathVariable("id") Long id){
        RCountDownLatch door = redisson.getCountDownLatch("door");
        door.countDown(); // 计数减一
        return id+"班人走了";
    }
}
