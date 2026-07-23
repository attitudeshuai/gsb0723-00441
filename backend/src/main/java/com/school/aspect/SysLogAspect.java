package com.school.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.school.annotation.SysLog;
import com.school.mapper.SysLogMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * 操作日志切面
 * 记录用户操作日志到数据库
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SysLogAspect {

    private final SysLogMapper sysLogMapper;
    private final ObjectMapper objectMapper;

    /**
     * 切入点：所有带有 @SysLog 注解的方法
     */
    @Pointcut("@annotation(com.school.annotation.SysLog)")
    public void logPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Throwable exception = null;

        try {
            result = point.proceed();
            return result;
        } catch (Throwable e) {
            exception = e;
            throw e;
        } finally {
            long costTime = System.currentTimeMillis() - startTime;
            saveLog(point, costTime, exception);
        }
    }

    /**
     * 保存操作日志
     */
    private void saveLog(ProceedingJoinPoint point, long costTime, Throwable exception) {
        try {
            MethodSignature signature = (MethodSignature) point.getSignature();
            SysLog sysLogAnnotation = signature.getMethod().getAnnotation(SysLog.class);

            com.school.entity.SysLog sysLog = new com.school.entity.SysLog();

            // 设置操作描述和模块
            if (sysLogAnnotation != null) {
                sysLog.setOperation(sysLogAnnotation.value());
                sysLog.setModule(sysLogAnnotation.module());
            }

            // 设置方法信息
            String className = point.getTarget().getClass().getName();
            String methodName = signature.getName();
            sysLog.setMethod(className + "." + methodName + "()");

            // 设置请求参数
            try {
                Object[] args = point.getArgs();
                // 过滤掉不能序列化的参数
                Object[] filteredArgs = Arrays.stream(args)
                        .filter(arg -> arg != null && !(arg instanceof HttpServletRequest))
                        .toArray();
                if (filteredArgs.length > 0) {
                    String params = objectMapper.writeValueAsString(filteredArgs);
                    // 限制参数长度
                    if (params.length() > 2000) {
                        params = params.substring(0, 2000) + "...";
                    }
                    sysLog.setParams(params);
                }
            } catch (Exception e) {
                log.warn("序列化请求参数失败: {}", e.getMessage());
            }

            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                sysLog.setIp(getIpAddress(request));
                sysLog.setUserId((Long) request.getAttribute("userId"));
                sysLog.setUsername((String) request.getAttribute("username"));
            }

            // 设置执行时间和状态
            sysLog.setCostTime(costTime);
            sysLog.setStatus(exception == null ? 1 : 0);
            if (exception != null) {
                String errorMsg = exception.getMessage();
                if (errorMsg != null && errorMsg.length() > 500) {
                    errorMsg = errorMsg.substring(0, 500);
                }
                sysLog.setErrorMsg(errorMsg);
            }

            sysLog.setCreateTime(LocalDateTime.now());

            // 异步保存日志（避免影响主业务）
            sysLogMapper.insert(sysLog);

        } catch (Exception e) {
            log.error("保存操作日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
