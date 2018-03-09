package com.system.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Service.UserService;
import com.system.utils.AuthCheckUtil;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户API接口
 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    UserService userService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(@RequestBody JsonNode body, HttpServletResponse httpresponse) throws Exception{
        userService.create(body);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/get", method = RequestMethod.POST)
    public Object register(@RequestBody Map<String,Object> request,HttpSession session) throws Exception{
        AuthCheckUtil.check(session);
        String orderBy = request.containsKey("orderBy")?(String)request.get("orderBy"):null;
        String order = request.containsKey("order")?(String)request.get("order"):null;
        Integer pageSize = request.containsKey("pageSize")?(Integer)request.get("pageSize"):null;
        Integer pageNo = request.containsKey("pageNo")?(Integer)request.get("pageNo"):null;
        String keyword = request.containsKey("keyword")?(String)request.get("keyword"):null;
        Integer userID = request.containsKey("userID")?(Integer)request.get("userID"):null;
        if(orderBy==null||order==null||pageSize==null||pageNo==null){
            throw new SystemException(ResultEnum.PARAM_NOT_FOUND.getCode(),ResultEnum.PARAM_NOT_FOUND.getMsg());
        }
        if(!order.equals("desc")&&!order.equals("asc")){
            throw new SystemException(ResultEnum.INPUT_ILLEGAL.getCode(),ResultEnum.INPUT_ILLEGAL.getMsg());
        }
        Object result = userService.get(orderBy,order,pageSize,pageNo,keyword,userID);
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object uodate(@RequestBody JsonNode body,HttpSession session) throws Exception{
        AuthCheckUtil.check(session);
        userService.update(body);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(@RequestBody JsonNode body,HttpSession session)throws Exception{
        AuthCheckUtil.check(session);
        userService.delete(body);
        return ResultUtil.success();
    }

}
