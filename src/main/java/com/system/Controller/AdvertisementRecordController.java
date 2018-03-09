package com.system.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Service.VoucherRecordService;
import com.system.utils.AuthCheckUtil;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 用户API接口
 */
@RestController
@RequestMapping(value = "/adRecord")
public class AdvertisementRecordController {

    @Autowired
    VoucherRecordService voucherRecordService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(@RequestBody JsonNode body, HttpServletResponse httpresponse) throws Exception{
        voucherRecordService.create(body);
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
        Integer status = request.containsKey("status")?(Integer)request.get("status"):null;
        Integer id = request.containsKey("id")?(Integer)request.get("id"):null;
        Integer receiverID = request.containsKey("receiverID")?(Integer)request.get("receiverID"):null;
        Integer voucherID = request.containsKey("voucherID")?(Integer)request.get("voucherID"):null;
        String startTime = request.containsKey("startTime")?(String)request.get("startTime"):null;
        String endTime = request.containsKey("endTime")?(String)request.get("endTime"):null;
        if(orderBy==null||order==null||pageSize==null||pageNo==null){
            throw new SystemException(ResultEnum.PARAM_NOT_FOUND.getCode(),ResultEnum.PARAM_NOT_FOUND.getMsg());
        }
        if(!order.equals("desc")&&!order.equals("asc")){
            throw new SystemException(ResultEnum.INPUT_ILLEGAL.getCode(),ResultEnum.INPUT_ILLEGAL.getMsg());
        }
        Object result = voucherRecordService.get(orderBy,order,pageSize,pageNo,keyword,status,id,voucherID,receiverID,startTime,endTime);
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object uodate(@RequestBody JsonNode body,HttpSession session) throws Exception{
        AuthCheckUtil.check(session);
        voucherRecordService.update(body);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(@RequestBody JsonNode body,HttpSession session)throws Exception{
        AuthCheckUtil.check(session);
        voucherRecordService.delete(body);
        return ResultUtil.success();
    }

}
