package com.system.Controller;

import com.system.Service.SystemService;
import com.system.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by arlex on 2017/12/1.
 */
@RestController
@RequestMapping(value = "/system")
public class SystemController {

    @Autowired
    SystemService systemService;

    /**
     * 删除冗余的图片
     * @throws Exception
     */
    @GetMapping(value = "/slim")
    public Object slim() throws Exception{
        systemService.slim();
        return ResultUtil.success();
    }

}
