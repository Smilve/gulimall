package com.lvboaa.search.thread;

import java.util.concurrent.*;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/17 17:58
 */
public class ThreadTest {
    private static ExecutorService service = Executors.newFixedThreadPool(10);


    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("查询商品图片信息");
            return "hello.jpg";
        }, service);
        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("查询商品属性");
            return "黑色+256G";
        },service);

//        CompletableFuture<Void> future = CompletableFuture.allOf(future01, future02);
//        future.get(); //阻塞等待，等待所有任务都执行完
//        System.out.println("main end ："+future01.get()+future02.get());

        CompletableFuture<Object> anyOf = CompletableFuture.anyOf(future01, future02);
        System.out.println("main end ："+anyOf.get()); // 只要有一个任务完成就行
    }

    public static void testOneTask(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        CompletableFuture<Object> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程：" + Thread.currentThread().getId());
            int i = 10 / 4;
            System.out.println("任务1结束：" + i);
            return i+"";
        }, service);
        CompletableFuture<Object> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程：" + Thread.currentThread().getId());
            try {
                Thread.sleep(3000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("任务2结束");
            return "hello";
        },service);

//        future01.runAfterEitherAsync(future02, () -> {
//            System.out.println("任务3开始");
//        }, service);

//        future01.acceptEitherAsync(future02,(res)->{
//            System.out.println("任务3："+res);
//        },service);

        CompletableFuture<Object> future = future01.applyToEitherAsync(future02, (res) -> {
            System.out.println("任务3：" + res);
            return "da" + res;
        }, service);

        System.out.println("main end " +future.get());
    }

    public static void testTwoTask(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务1线程：" + Thread.currentThread().getId());
            int i = 10 / 2;
            System.out.println("任务1结束：" + i);
            return i;
        }, service);
        CompletableFuture<String> future02 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务2线程：" + Thread.currentThread().getId());
            System.out.println("任务2结束");
            return "hello";
        },service);

//        future01.runAfterBothAsync(future02,()->{
//            System.out.println("任务3开始");
//        },service);

//        future01.thenAcceptBothAsync(future02,(res1,res2)->{
//            System.out.println("任务3开始，之前的结果"+res1+" "+res2);
//        },service);

        CompletableFuture<String> future = future01.thenCombineAsync(future02, (res1, res2) -> {
            return res1 + res2;
        }, service);

        System.out.println("main end " + future.get());
    }

    // 使用测试用例测试不出多线程的效果
    public static void testHandle(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start");
//        CompletableFuture.runAsync(()->{
//            int i = 10/2;
//            System.out.println("当前结果："+i);
//        },service);
//        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//            System.out.println("sada");
//            return 10 / 2;
//        }, service).whenComplete((res,exception)->{ // 能得到异常信息，但是不能修改返回值
//            System.out.println("异步任务执行成功，结果是"+res+"，异常是"+exception);
//        }).exceptionally(throwable -> {  // 可以感知异常，同时返回默认值
//            return 10;
//        });
        CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("sada");
            return 10 / 2;
        }, service).handle((res,thr)->{ // 方法执行完成后的处理
            if (res != null){
                return res*2;
            }
            if (thr != null){
                return 0;
            }
            return 0;
        });
        // 会等待线程执行完成 阻塞
        System.out.println(future.get());
        System.out.println("main end");
    }

    public void threadFutureTask() throws ExecutionException, InterruptedException {
        Integer a=12;
        FutureTask<Integer> futureTask1 = new FutureTask<>(new Runnable() {
            @Override
            public void run() {
            }
        }, a);
        new Thread(futureTask1).start();
        System.out.println(futureTask1.get());
    }
}
