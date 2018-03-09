package com.system.Exception;

public class SystemException extends RuntimeException{
    //    错误码
    private Integer code;

    public SystemException(Integer code, String msg){
        super(msg);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

}
