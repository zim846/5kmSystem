package com.system.Controller;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.LabsException;
import com.system.Service.RecordService;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Created by arlex on 2017/12/1.
 */
@RestController
@RequestMapping(value = "/lend")
public class LendController {

    @Autowired
    RecordService recordService;


    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Object add(@RequestBody Map<String,Object> request, HttpSession session) throws Exception{
        String itemName = request.containsKey("itemName")?(String)request.get("itemName"):null;
        String borrowedTime = request.containsKey("borrowedTime")?(String)request.get("borrowedTime"):null;
        String name = request.containsKey("name")?(String)request.get("name"):null;
        String phone = request.containsKey("phone")?(String)request.get("phone"):null;
        String email = request.containsKey("email")?(String)request.get("email"):null;
        Integer number = request.containsKey("number")?(Integer)request.get("number"):null;
        if(number<=0){
            throw new LabsException(ResultEnum.INPUT_ILLEGAL.getCode(),ResultEnum.INPUT_ILLEGAL.getMsg());
        }
        String userid = (String)session.getAttribute("userid");
        recordService.add(itemName,borrowedTime,name,phone,email,number,2,userid);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public Object edit(@RequestBody Map<String,Object> request,HttpSession session) throws Exception{
        Integer id = request.containsKey("id")?(Integer)request.get("id"):null;
        String name = request.containsKey("name")?(String)request.get("name"):null;
        String phone = request.containsKey("phone")?(String)request.get("phone"):null;
        String email = request.containsKey("email")?(String)request.get("email"):null;
        Integer number = request.containsKey("number")?(Integer)request.get("number"):null;
        if(number<=0){
            throw new LabsException(ResultEnum.INPUT_ILLEGAL.getCode(),ResultEnum.INPUT_ILLEGAL.getMsg());
        }
        String userid = (String)session.getAttribute("userid");
        recordService.edit(id,name,phone,email,userid,number);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/getAll", method = RequestMethod.POST)
    public Object register(@RequestBody Map<String,Object> request) throws Exception{
        String name = request.containsKey("name")?(String)request.get("name"):null;
        ArrayNode records = recordService.getAll(2,name);
        return ResultUtil.success(records);

    }


}
