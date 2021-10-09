package com.lvboaa.gulimall.order.service.impl;

import com.lvboaa.gulimall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lvboaa.common.utils.PageUtils;
import com.lvboaa.common.utils.Query;

import com.lvboaa.gulimall.order.dao.OrderItemDao;
import com.lvboaa.gulimall.order.entity.OrderItemEntity;
import com.lvboaa.gulimall.order.service.OrderItemService;


@Service("orderItemService")
//@RabbitListener(queues = {"hello-java-queue"})
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 参数可以写的参数
     * 1.Message:原生消息详细信息，头+体
     *  T : 直接是发送消息的实体内容
     *  Channel channel: 当前传输数据的通道
     *
     * 2.可以有很多人来监听这个队列；只要收到消息，队列就会删除这个消息，而且只能有一个能收到消息
     *  1)订单服务启动多个，同一个消息，只能有一个客户端收到
     *  2)只有一个消息完全处理完，方法运行结束，才会接收到下一个消息
     * 3.@RabbitListener(queues = {"hello-java-queue"})：可以放在类、方法上  @RabbitHandler放在方法上
     *  当@RabbitListener放在类上，使用@RabbitHandler 该方法就监听队列，多个方法加这个注解 类似重载，处理不同的实体，区分不同的场景
     * @param message
     */
//    @RabbitListener(queues = {"hello-java-queue"})
//    @RabbitHandler
    public void recieveMessage(Message message, OrderReturnReasonEntity orderReturnReasonEntity, Channel channel){
        System.out.println("接收到消息："+orderReturnReasonEntity);

        // channel内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        // 签收货物 multiple代表是否批量签收
        try {
            if (deliveryTag % 2 == 0){
                channel.basicAck(deliveryTag,false);
                System.out.println("签收了货物..."+deliveryTag);
            }else {
                // 退货  channel.basicReject();
                // requeue 拒收了消息是否重新入队  true:入队 false:丢弃
                channel.basicNack(deliveryTag,false,true);

                System.out.println("退回了货物..."+deliveryTag);
            }

        } catch (IOException e) {
            // 网络中断异常信息
            e.printStackTrace();
        }
    }

}