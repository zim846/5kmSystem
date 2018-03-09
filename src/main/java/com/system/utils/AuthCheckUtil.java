package com.system.utils;

import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;

import javax.servlet.http.HttpSession;

public class AuthCheckUtil {

    /**
     * 检查权限
     * @return
     * @throws Exception
     */
    public static void check(HttpSession session) throws Exception{
        String superuser = (String)session.getAttribute("superuser");
        if(!superuser.equals("1")){
            throw new SystemException(ResultEnum.AUTH_NOT_FOUND.getCode(),ResultEnum.AUTH_NOT_FOUND.getMsg());
        }
    }

}
