package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.User;
import com.system.Model.Voucher;
import com.system.Properties.FileProperties;
import com.system.Repository.UserRepository;
import com.system.Repository.VoucherRepository;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SessionFactory sessionFactory;


    public void create(JsonNode data) throws Exception{
        User user = userRepository.findOneById(data.get("user_id").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        Voucher voucher = new Voucher();
        voucher.setUser(user);
        voucher.setLocation(data.get("location").textValue());
        voucher.setLng(data.get("lng").floatValue());
        voucher.setLat(data.get("lat").floatValue());
        voucher.setOrigin_price(data.get("origin_price").intValue());
        voucher.setDiscount_price(data.get("discount_price").intValue());
        voucher.setQuantity(data.get("quantity").intValue());
        voucher.setRemaining(data.get("remaining").intValue());
        Date create_time = new Date();
        voucher.setCreate_time(create_time);
        voucherRepository.save(voucher);
    }

    public void update(JsonNode data) throws Exception{
        Voucher voucher = voucherRepository.findOneById(data.get("voucherID").intValue());
        if (voucher == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }
        voucher.setRemaining(data.get("remaining").intValue());
        voucher.setStatus(data.get("status").intValue());
        voucherRepository.save(voucher);

    }

    public ObjectNode get(String orderBy,String order,Integer pageSize,Integer pageNo,String keyword,Integer status,Integer voucherID,String startTime,String endTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Voucher.class,"voucher");
        criteria.createAlias("voucher.user","user", JoinType.LEFT_OUTER_JOIN);
        //输入的keyword是这种格式 id = xxxx or username = xxx or tel = xxxx
        if(keyword!=null){
            String[] keywords = keyword.split("or");
            for (int i =0;i<keywords.length;i++){
                String key = keywords[i].split("=")[0];
                String value = keywords[i].split("=")[1];
                if(key.equals("user_id")){
                    criteria.add(Restrictions.eq("user.user_id",value));
                    continue;
                }
                criteria.add(Restrictions.eq("voucher."+key,value));
            }
        }
        if(voucherID!=null){
            criteria.add(Restrictions.eq("voucher.id",voucherID));
        }
        if(status!=null){
            criteria.add(Restrictions.eq("voucher.status",status));
        }
        if(startTime!=null&&endTime!=null){
            criteria.add(Restrictions.between("voucher.create_time",format.parse(startTime),format.parse(endTime)));
        }
        Number totalNumber = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
        int total = totalNumber.intValue();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

        if(order!=null&&orderBy!=null){
            if(order.equals("desc")){
                criteria.addOrder(Order.desc("voucher."+order));
            }else if(order.equals("asc")){
                criteria.addOrder(Order.asc("voucher."+order));
            }
        }

        if(pageSize!=null&&pageNo!=null){
            criteria.setFirstResult((pageNo-1)*pageSize);
            criteria.setMaxResults(pageSize);
        }

        List<Voucher> vouchers = criteria.list();
        ArrayNode array = mapper.createArrayNode();
        for(Voucher item: vouchers){
            ObjectNode node = mapper.createObjectNode();
            node.put("id",item.getId());
            node.put("user_id",item.getUser().getId());
            node.put("location",item.getLocation());
            node.put("lng",item.getLng());
            node.put("lat",item.getLat());
            node.put("origin_price",item.getOrigin_price());
            node.put("discount_price",item.getDiscount_price());
            node.put("quantity",item.getQuantity());
            node.put("remaining",item.getRemaining());
            node.put("create_time",format.format(item.getCreate_time()));
            node.put("status",item.getStatus());
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
        Voucher voucher = voucherRepository.findOne(data.get("voucherID").intValue());
        if (voucher == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }else {
            voucherRepository.delete(voucher);
            return true;
        }
    }
}
