package com.lvboaa.gulimall.seckill.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Description: rabbit配置；消息确认机制(保证消息不丢失)
 *
 * @author lv.bo
 * @date 2021/9/30 16:38
 */
@Configuration
public class RabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

//    /**
//     * 定制RabbitTemplate
//     * 1.服务器收到消息就回调
//     *      1.spring.rabbitmq.publisher-confirms=true
//     *      2.设置消息回调ConfirmCallback
//     * 2.消息正确抵达队列进行回调
//     *      1.spring.rabbitmq.publisher-returns=true
//     *        spring.rabbitmq.templates.mandatory=true
//     *      2.设置投递失败异步回调ReturnCallback
//     * 3.消费端确认(保证每个消息正确被消费，此时消息才可以被broker删除)
//     *      1.默认是自动确认的，只要消息接收到，客户端自动确认，服务端就会移除这个消息
//     *      问题：我们收到很多消息，自动回复给服务器ack,只有一个消息处理成功，然后宕机，就会发生消息丢失
//     *      解决：手动确认，设置spring.rabbitmq.listener.direct.acknowledge-mode=manual
//     *          (不进行手动确认(消息状态一直为unacked)，队列就不会移除这个消息，宕机消息也不会丢失，状态变为ready)
//     *      2.如何签收
//     *         签收：channel.basicAck(deliveryTag,false);  业务成功完成
//     *         退回(拒签)：channel.basicNack(deliveryTag,false,true); channel.basicReject();  业务处理失败
//     */
//    @PostConstruct  // RabbitConfig对象创建完成以后，执行构造方法以后就执行这个方法
//    public void initRabbitTemplate(){
//
//        //设置确认回调
//        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//            /**
//             *
//             * @param correlationData  当前消息的唯一关联数据(这个是消息的唯一id)
//             * @param b     (消息是否成功)   只要消息抵达Broker(服务代理)，这个就为true
//             * @param s     (失败的原因)
//             */
//            @Override
//            public void confirm(CorrelationData correlationData, boolean b, String s) {
//                // rabbitmq服务器收到了
//                // 修改消息的状态
////                System.out.println("confirmcallback:"+correlationData+" "+b+" "+s);
//            }
//        });
//
//        //设置消息抵达队列的确认回调
//        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//            /**
//             *  只有消息没有投递给指定的队列才会触发这个失败回调
//             * @param message   投递失败的消息的详细信息
//             * @param i         回复的状态码
//             * @param s         回复的文本内容
//             * @param s1        当时消息发给哪个交换机
//             * @param s2        当时消息使用哪个路由键
//             */
//            @Override
//            public void returnedMessage(Message message, int i, String s, String s1, String s2) {
//                // 报错，修改数据库当前消息的状态->错误
////                System.out.println("Fail Message:"+message+" "+i+" "+s+" "+s1+" "+s2);
//            }
//        });
//    }


}
