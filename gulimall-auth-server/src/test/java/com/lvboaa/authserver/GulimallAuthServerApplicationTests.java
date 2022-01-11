package com.lvboaa.authserver;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

@SpringBootTest
class GulimallAuthServerApplicationTests {

    @Test
    void contextLoads() {
        DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI5t6r5by5mhjfyqSpJhoa", "3BC8w6cFLd2WewPuUtPOBsTxhXwZHO");//自己账号的AccessKey信息
        IAcsClient client = new DefaultAcsClient(profile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");//短信服务的服务接入地址
        request.setSysVersion("2017-05-25");//API的版本号
        request.setSysAction("SendSms");//API的名称
        request.putQueryParameter("PhoneNumbers", "19822964684");//接收短信的手机号码
        request.putQueryParameter("SignName", "橘子科技");//短信签名名称
        request.putQueryParameter("TemplateCode", "SMS_224900336");//短信模板ID
        HashMap<String, Object> map = new HashMap<>();
        map.put("code",2233);
        request.putQueryParameter("TemplateParam", JSONObject.toJSONString(map));//短信模板变量对应的实际值
        try {
            CommonResponse response = client.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

}
