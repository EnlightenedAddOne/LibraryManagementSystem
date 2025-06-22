package com.example.library.response;

public class Response<T> {
    private int code; // 状态码
    private String message; // 消息
    private T data; // 数据

    public Response(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    // 静态方法用于快速创建响应对象
    public static <T> Response<T> success(T data) {
        return new Response<>(200, "Success", data);
    }

    public static <T> Response<T> error(String message) {
        return new Response<>(500, message, null);
    }
}