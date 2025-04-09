package com.example.springaichat.entity.vo;

import lombok.Data;

@Data
public class Result {
    private Integer ok;
    private String msg;

    public Result(Integer ok, String msg) {
        this.ok = ok;
        this.msg = msg;
    }
    public static Result ok(String msg) {
        return new Result(1, msg);
    }
    public static Result fail(String msg) {
        return new Result(0, msg);
    }
}
