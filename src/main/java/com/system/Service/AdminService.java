package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Model.Admin;
import com.system.Properties.FileProperties;
import com.system.Repository.AdminRepository;
import com.system.Exception.SystemException;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
;
import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Horac on 2017/5/15.
 */
@Service
@Transactional
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SessionFactory sessionFactory;


    public ObjectNode login( Map<String,Object> data) throws Exception{
        Admin admin = adminRepository.findOneByAccount((Integer) data.get("account"));
        if (admin == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        if(admin.getPassword().equals((String)data.get("password"))){
            ObjectNode node = mapper.createObjectNode();
            node.put("userid",admin.getId());
            node.put("authority",admin.getAuthority());
            return node;
        }
        else {
            throw new SystemException(ResultEnum.USER_PASSWORD_ERROR.getCode(), ResultEnum.USER_PASSWORD_ERROR.getMsg());
        }
    }


    public Boolean create(JsonNode data) throws Exception{
        Admin admin = adminRepository.findOneByAccount(data.get("account").intValue());
        if (admin != null){
            throw new SystemException(ResultEnum.USER_ALREADY_EXIST.getCode(),ResultEnum.USER_ALREADY_EXIST.getMsg());
        }
        admin = new Admin();
        admin.setAccount(data.get("account").intValue());
        admin.setPassword(data.get("password").textValue());
        admin.setAuthority(data.get("authority").intValue());
        adminRepository.save(admin);
        return true;

    }
}
