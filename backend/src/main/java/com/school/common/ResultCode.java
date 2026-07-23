package com.school.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    // 认证相关 1001-1099
    LOGIN_FAILED(1001, "用户名或密码错误"),
    TOKEN_INVALID(1002, "Token无效或已过期"),
    TOKEN_EXPIRED(1003, "Token已过期"),
    ACCOUNT_DISABLED(1004, "账号已禁用"),
    PASSWORD_ERROR(1005, "原密码错误"),

    // 用户相关 1101-1199
    USER_NOT_FOUND(1101, "用户不存在"),
    USERNAME_EXISTS(1102, "用户名已存在"),
    PHONE_EXISTS(1103, "手机号已存在"),

    // 学生相关 1201-1299
    STUDENT_NOT_FOUND(1201, "学生不存在"),
    STUDENT_NO_EXISTS(1202, "学号已存在"),

    // 教师相关 1301-1399
    TEACHER_NOT_FOUND(1301, "教师不存在"),
    TEACHER_NO_EXISTS(1302, "工号已存在"),

    // 班级相关 1401-1499
    CLASS_NOT_FOUND(1401, "班级不存在"),
    CLASS_EXISTS(1402, "班级已存在"),
    CLASS_HAS_STUDENTS(1403, "班级下存在学生，无法删除"),

    // 成绩相关 1501-1599
    SCORE_NOT_FOUND(1501, "成绩记录不存在"),
    SCORE_EXISTS(1502, "成绩记录已存在"),

    // 考勤相关 1601-1699
    ATTENDANCE_NOT_FOUND(1601, "考勤记录不存在"),
    ATTENDANCE_EXISTS(1602, "考勤记录已存在"),

    // 文件相关 1701-1799
    FILE_UPLOAD_ERROR(1701, "文件上传失败"),
    FILE_TYPE_ERROR(1702, "文件类型不支持"),
    FILE_PARSE_ERROR(1703, "文件解析失败");

    private final Integer code;
    private final String message;
}
