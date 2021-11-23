# gulimall

## 框架工具及业务实现
> 整合第三方服务
支付宝沙箱-->支付
阿里云OSS-->存储文件
阿里云短信服务-->验证码

>Nginx代理
实现父域名及子域名访问
实现动静分离(静态文件放在服务器中)


>整合ES实现商品搜索

>使用缓存与分布式锁
缓存商品三级目录(缓存-->redis)
分布式锁(防止请求过多，压垮数据库)-->Redisson

>异步与线程池
实现异步编排-->CompletableFuture

>登录
实现微博社交登录
使用分布式session共享实现单点登录(把session放入redis中)

>购物车
设计数据结构(存入购物车数据-->redis)
分为用户登录购物车和未登录临时购物车
用户信息存入`ThreadLocal`中，在该线程任意地方可获取当前登录(线程)用户

>接口幂等性处理
防从提交
使用token机制

>分布式事务
SpringCloudAlibaba Seata-->并发量小时可以使用
RabbitMQ-->使用`死信队列`实现分布式事务(如订单超时，订单服务及库存服务都回滚)

>秒杀
定时任务实现秒杀商品上架(设计数据结构存入redis)
需要进行接口幂等性处理(一个人只能买固定件数)

>SpringCloud Alibaba-Nacos
服务注册及配置管理
>SpringCloud Openfeign
远程调用
>SpringCloud Alibaba-Sentinel
服务限流、熔断、降级
>Sleuth+ZipKin 
服务链路追踪

# 2021-11-18
>整合SpringSecurity+JWT 实现单点登录
```bash
gulimall-auth-server
gulimall-order
gulimall-member(查询用户信息)
# 由于gateway和oauth2不兼容，导致未在网关层进行权限验证
```
