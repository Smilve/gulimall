package com.lvboaa.gulimall.order.exception;

import com.lvboaa.common.exception.RRException;
import com.lvboaa.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.ExecutionException;

/**
 * Description:
 *
 * @author lv.bo
 * @date 2021/9/23 10:49
 */
@RestControllerAdvice
@Slf4j
public class RRExceptionHandler {

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RRException.class)
    public R handleRRException(RRException e){
        R r = new R();
        r.put("code", e.getCode());
        r.put("msg", e.getMessage());

        return r;
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public R handleDuplicateKeyException(DuplicateKeyException e){
        log.error(e.getMessage(), e);
        return R.error("数据库中已存在该记录:"+e.getMessage());
    }

    @ExceptionHandler(ExecutionException.class)
    public R handleExecutionException(ExecutionException e){
        log.error(e.getMessage(), e);
        return R.error("线程池异常："+e.getMessage());
    }

    @ExceptionHandler(InterruptedException.class)
    public R handleInterruptedException(InterruptedException e){
        log.error(e.getMessage(), e);
        return R.error("阻塞中断异常："+e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public R handleInterruptedException(AccessDeniedException e){
        log.error(e.getMessage(), e);
        return R.error("你没有这个权限："+e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R handleException(Exception e){
        log.error(e.getMessage(), e);
        return R.error();
    }
}
