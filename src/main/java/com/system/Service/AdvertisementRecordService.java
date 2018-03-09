package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.Advertisement;
import com.system.Model.AdvertisementRecord;
import com.system.Model.User;
import com.system.Properties.FileProperties;
import com.system.Repository.AdvertisementRecordRepository;
import com.system.Repository.AdvertisementRepository;
import com.system.Repository.UserRepository;
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
public class AdvertisementRecordService {

    @Autowired
    private AdvertisementRecordRepository advertisementRecordRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SessionFactory sessionFactory;


    public void create(JsonNode data) throws Exception{
        AdvertisementRecord advertisementRecord = advertisementRecordRepository.findOneById(data.get("id").intValue());
        if (advertisementRecord != null){
            throw new SystemException(ResultEnum.OBJECT_ALREADY_EXIST.getCode(),ResultEnum.OBJECT_ALREADY_EXIST.getMsg());
        }
        User user = userRepository.findOneById(data.get("receiverID").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        Advertisement advertisement = advertisementRepository.findOneById(data.get("adID").intValue());
        if (advertisement == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }
        advertisementRecord = new AdvertisementRecord();
        advertisementRecord.setUser(user);
        advertisementRecord.setAdvertisement(advertisement);
        Date create_time = new Date();
        advertisementRecord.setCreate_time(create_time);
        advertisementRecordRepository.save(advertisementRecord);
    }

    public ObjectNode get(String orderBy,String order,Integer pageSize,Integer pageNo,String keyword,Integer status,Integer id,Integer adID,Integer receiverID,String startTime,String endTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(AdvertisementRecord.class,"advertisementRecord");
        criteria.createAlias("advertisementRecord.user","receiver", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("advertisementRecord.advertisement","advertisement", JoinType.LEFT_OUTER_JOIN);
        //输入的keyword是这种格式 id = xxxx or username = xxx or tel = xxxx
        if(keyword!=null){
            String[] keywords = keyword.split("or");
            for (int i =0;i<keywords.length;i++){
                String key = keywords[i].split("=")[0];
                String value = keywords[i].split("=")[1];
                if(key.equals("adID")){
                    criteria.add(Restrictions.eq("advertisement.id",value));
                    continue;
                }
                if(key.equals("receiverID")){
                    criteria.add(Restrictions.eq("receiver.id",value));
                    continue;
                }
                criteria.add(Restrictions.eq("advertisementRecord."+key,value));
            }
        }
        if(id!=null){
            criteria.add(Restrictions.eq("advertisementRecord.id",id));
        }
        if(adID!=null){
            criteria.add(Restrictions.eq("voucher.id",adID));
        }
        if(receiverID!=null){
            criteria.add(Restrictions.eq("receiver.id",receiverID));
        }
        if(status!=null){
            criteria.add(Restrictions.eq("advertisementRecord.status",status));
        }
        if(startTime!=null&&endTime!=null){
            criteria.add(Restrictions.between("advertisementRecord.create_time",format.parse(startTime),format.parse(endTime)));
        }
        Number totalNumber = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
        int total = totalNumber.intValue();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

        if(order!=null&&orderBy!=null){
            if(order.equals("desc")){
                criteria.addOrder(Order.desc("advertisementRecord."+order));
            }else if(order.equals("asc")){
                criteria.addOrder(Order.asc("advertisementRecord."+order));
            }
        }

        if(pageSize!=null&&pageNo!=null){
            criteria.setFirstResult((pageNo-1)*pageSize);
            criteria.setMaxResults(pageSize);
        }

        List<AdvertisementRecord> advertisementRecords = criteria.list();
        ArrayNode array = mapper.createArrayNode();
        for(AdvertisementRecord item: advertisementRecords){
            ObjectNode node = mapper.createObjectNode();
            node.put("id",item.getId());
            node.put("advertisement_id",item.getAdvertisement().getId());
            node.put("receiver_id",item.getUser().getId());
            node.put("create_time",format.format(item.getCreate_time()));
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
        AdvertisementRecord advertisementRecord = advertisementRecordRepository.findOneById(data.get("id").intValue());
        if (advertisementRecord == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }else {
            advertisementRecordRepository.delete(advertisementRecord);
            return true;
        }
    }
}
