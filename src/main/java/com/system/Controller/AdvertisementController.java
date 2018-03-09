package com.system.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.Advertisement;
import com.system.Service.AdvertisementService;
import com.system.Service.VoucherService;
import com.system.utils.AuthCheckUtil;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.util.Map;

/**
 * 用户API接口
 */
@RestController
@RequestMapping(value = "/advertisement")
public class AdvertisementController {

    @Autowired
    AdvertisementService advertisementService;


    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object create(@RequestBody JsonNode body, HttpServletResponse httpresponse) throws Exception{
        advertisementService.create(body);
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
        Integer adID = request.containsKey("adID")?(Integer)request.get("adID"):null;
        String startTime = request.containsKey("startTime")?(String)request.get("startTime"):null;
        String endTime = request.containsKey("endTime")?(String)request.get("endTime"):null;
        if(orderBy==null||order==null||pageSize==null||pageNo==null){
            throw new SystemException(ResultEnum.PARAM_NOT_FOUND.getCode(),ResultEnum.PARAM_NOT_FOUND.getMsg());
        }
        if(!order.equals("desc")&&!order.equals("asc")){
            throw new SystemException(ResultEnum.INPUT_ILLEGAL.getCode(),ResultEnum.INPUT_ILLEGAL.getMsg());
        }
        Object result = advertisementService.get(orderBy,order,pageSize,pageNo,keyword,status,adID,startTime,endTime);
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Object uodate(@RequestBody JsonNode body,HttpSession session) throws Exception{
        AuthCheckUtil.check(session);
        advertisementService.update(body);
        return ResultUtil.success();
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Object delete(@RequestBody JsonNode body,HttpSession session)throws Exception{
        AuthCheckUtil.check(session);
        advertisementService.delete(body);
        return ResultUtil.success();
    }

    @PostMapping(value = "/upload")
    public Object upload(@RequestParam("file") MultipartFile picture, HttpSession session) throws Exception{
        if(picture.isEmpty()){
            throw new SystemException(ResultEnum.PARAM_NOT_FOUND.getCode(),ResultEnum.PARAM_NOT_FOUND.getMsg());
        }
        //getContentType返回的是image/png...
        if(!picture.getContentType().startsWith("image")){
            throw new SystemException(ResultEnum.FILE_TYPE_ERROR.getCode(), ResultEnum.FILE_TYPE_ERROR.getMsg());
        }
        // getSize 函数返回的是字节数
        if(picture.getSize()>20*1024*1024){
            throw new SystemException(ResultEnum.FILE_SIZE_ERROR.getCode(),ResultEnum.FILE_SIZE_ERROR.getMsg());
        }
       // String userid = (String)session.getAttribute("userid");
        InputStream file = picture.getInputStream();
        String contentType = picture.getContentType().split("/")[1];
        String filePath = advertisementService.uploadAdImage(file,contentType);
        return ResultUtil.success(filePath);
    }

}
