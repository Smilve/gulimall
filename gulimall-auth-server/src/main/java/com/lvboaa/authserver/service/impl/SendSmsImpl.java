package com.lvboaa.authserver.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.lvboaa.authserver.component.SmsComponent;
import com.lvboaa.authserver.service.SendSms;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/23 14:45
 */
@Service
@Slf4j
public class SendSmsImpl implements SendSms {

    @Autowired
    SmsComponent smsComponent;

    @Override
    public String  send(String phone) {
        String code = UUID.randomUUID().toString().substring(0,6);
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", code);
        try{
            smsComponent.send(phone,map);
        }catch (Exception e){
            log.error(e.getMessage());
        }

        return code;
    }
}
