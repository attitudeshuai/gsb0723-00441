package com.school.annotation;

import java.lang.annotation.*;

/**
 * 权限验证注解
 * 用于标记需要特定权限才能访问的接口
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 权限标识
     */
    String[] value() default {};

    /**
     * 是否需要管理员权限
     */
    boolean requireAdmin() default false;

    /**
     * 多个权限之间的关系：true=AND, false=OR
     */
    boolean logical() default false;
}
