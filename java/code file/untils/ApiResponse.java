package com.mrbai.untils;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: MrBai
 * @date: 2021-03-31 14:58
 **/
public class ApiResponse implements Serializable {
    private int code = 200;
    private String msg = "请求完成";
    private Object data;

    public ApiResponse() {
        this.setData(null);
    }

    public ApiResponse(Object data) {
        this.setData(data);
    }

    public ApiResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.setData(data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        if (data == null) {
            this.data = new HashMap<>();
        } else {
            this.data = data;
        }
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
