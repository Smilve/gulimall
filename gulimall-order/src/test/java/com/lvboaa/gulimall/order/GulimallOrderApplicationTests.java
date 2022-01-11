package com.lvboaa.gulimall.order;

import com.lvboaa.gulimall.order.entity.OrderEntity;
import com.lvboaa.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

@RunWith(value= SpringJUnit4ClassRunner.class)
@SpringBootTest(classes={GulimallOrderApplication.class})
@Slf4j
public class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage(){
        // 如果发送的消息是一个对象，实体需要实现Serializable接口
        // 默认使用自己的编码，在RabbitConfig中配置了 转成json的编码器
        for (int i=0;i<10;i++){
            OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
            orderReturnReasonEntity.setName("name："+i);
            rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity, new CorrelationData(UUID.randomUUID().toString()));

        }
    }

    /**
     * 创建Exchange、Queue、Binding
     *
     * 收发消息
     */
    @Test
    public void createExchange(){
        //  public DirectExchange(String name, boolean durable, boolean autoDelete)
        //  durable:是否持久化   autoDelete:是否自动删除
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功","hello-java-exchange");
    }

    @Test
    public void createQueue(){
        // public Queue(String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments)
        //  durable:是否持久化 exclusive:是否排它(只要有一条连接连上了这个队列，其他连接都连不上这个队列，一般false) autoDelete:是否自动删除
        Queue queue = new Queue("hello-java-queue", true,false,false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功","hello-java-queue");
    }

    @Test
    public void createBinding(){
        // public Binding(String destination, Binding.DestinationType destinationType, String exchange, String routingKey, Map<String, Object> arguments) {
        // destination:目的地 destinationType:目的地类型 exchange:交换机 routingKey:路由Key
        Binding binding = new Binding("hello-java-queue",Binding.DestinationType.QUEUE,
                "hello-java-exchange","hello.java",null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功","hello-java-binding");
    }
}
