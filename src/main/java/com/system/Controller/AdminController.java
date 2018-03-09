package com.system.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Service.AdminService;
import com.system.utils.AuthCheckUtil;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    AdminService adminService;


    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Object login(@RequestBody Map<String,Object> request, HttpServletResponse httpresponse) throws Exception{
        ObjectNode result = adminService.login(request);
        //设置Cookie
//        String userid = URLEncoder.encode(result.get("userid").toString(),"UTF-8");
//        httpresponse.addHeader("Set-Cookie", "token="+userid+"; Max-Age=259200;Path=/");
//        String superuser = URLEncoder.encode(result.get("superuser").toString(),"UTF-8");
//        httpresponse.addHeader("Set-Cookie", "superuser="+superuser+"; Max-Age=259200;Path=/");
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object register(@RequestBody JsonNode body,HttpSession session) throws Exception{
        AuthCheckUtil.check(session);
        adminService.create(body);
        return ResultUtil.success();
    }

}
