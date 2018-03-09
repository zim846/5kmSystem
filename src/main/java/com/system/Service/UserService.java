package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.User;
import com.system.Properties.FileProperties;
import com.system.Repository.UserRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Horac on 2017/5/15.
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SessionFactory sessionFactory;


    public void create(JsonNode data) throws Exception{
        User user = new User();
        user.setTel(data.get("tel").textValue());
        user.setUsername(data.get("username").textValue());
        user.setIntroduction(data.get("introduction").textValue());
        userRepository.save(user);
    }

    public void update(JsonNode data) throws Exception{
        User user = userRepository.findOneById(data.get("userID").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        user.setTel(data.get("tel").textValue());
        user.setUsername(data.get("username").textValue());
        user.setIntroduction(data.get("introduction").textValue());
        user.setBalance(data.get("balance").floatValue());
        userRepository.save(user);

    }

    public ObjectNode get(String orderBy,String order,Integer pageSize,Integer pageNo,String keyword,Integer userID) throws Exception{
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(User.class,"user");
        //输入的keyword是这种格式 id = xxxx or username = xxx or tel = xxxx
        if(keyword!=null){
            String[] keywords = keyword.split("or");
            for (int i =0;i<keywords.length;i++){
                String key = keywords[i].split("=")[0];
                String value = keywords[i].split("=")[1];
                criteria.add(Restrictions.eq("user."+key,value));
            }
        }
        if(userID!=null){
            criteria.add(Restrictions.eq("user.id",userID));
        }
        Number totalNumber = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
        int total = totalNumber.intValue();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

        if(order!=null&&orderBy!=null){
            if(order.equals("desc")){
                criteria.addOrder(Order.desc("user."+order));
            }else if(order.equals("asc")){
                criteria.addOrder(Order.asc("user."+order));
            }
        }

        if(pageSize!=null&&pageNo!=null){
            criteria.setFirstResult((pageNo-1)*pageSize);
            criteria.setMaxResults(pageSize);
        }

        List<User> users = criteria.list();
        ArrayNode array = mapper.createArrayNode();
        for(User item: users){
            ObjectNode node = mapper.createObjectNode();
            node.put("id",item.getId());
            node.put("tel",item.getTel());
            node.put("username",item.getUsername());
            node.put("introduction",item.getIntroduction());
            node.put("balance",item.getBalance());
            array.addPOJO(node);
        }
        ObjectNode result = mapper.createObjectNode();
        result.put("orderBy",orderBy);
        result.put("order",order);
        result.put("pageSize",pageSize);
        result.put("pageNo",pageNo);
        result.put("totalCount",totalNumber+"");
        result.putPOJO("list",array);

        return result;
    }


    public Boolean delete(JsonNode data) throws Exception{
        User user = userRepository.findOne(data.get("userID").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }else {
            userRepository.delete(user);
            return true;
        }
    }
}
