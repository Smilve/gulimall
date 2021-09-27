package com.lvboaa.common.utils;

import com.lvboaa.common.constant.AuthServerConstant;
import com.lvboaa.common.exception.BizCodeEnum;
import com.lvboaa.common.exception.RRException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

@Slf4j
public class JwtUtils {

    //过期时间设置为一个小时
    private static final long EXPTRE_TIME= 60 * 60 * 1000;
    /**
     * token私钥
     */
    private static final String TOKEN_SECRET="8ae0d24822ef59d9e75745449b3501bc";
    /**
     * 生成签名
     */
    public static String createToken(Map<String,Object> map){

        //当前时间
        long now = System.currentTimeMillis();
        //过期时间
        Date date = new Date(now + EXPTRE_TIME);
        String token="";
        try{
            token = Jwts.builder()
                    .setIssuedAt(new Date(now))
                    .setExpiration(date)
                    .signWith(SignatureAlgorithm.HS256,TOKEN_SECRET)
                    .setClaims(map)
                    .compact();
        }catch (Exception e){
            log.debug("token生成有问题");
            throw new RRException(BizCodeEnum.CREATE_TOKEN_ERROR);
        }
        return token;
    }

    /**
     * token解码+提取内容
     */
    public static String parseJwt(String token){
        try{
            Claims claims = (Claims) Jwts.parser()
                    .setSigningKey(TOKEN_SECRET)
                    .parse(token)
                    .getBody();
            return claims.get(AuthServerConstant.LOGIN_USER).toString();
        }catch (Exception e){
            log.debug("token解析有问题");
            throw new RRException(BizCodeEnum.TOKEN_VERIFY_ERROR);
        }
    }
}
