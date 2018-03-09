package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.Advertisement;
import com.system.Model.User;
import com.system.Model.Voucher;
import com.system.Properties.FileProperties;
import com.system.Repository.AdvertisementRepository;
import com.system.Repository.UserRepository;
import com.system.Repository.VoucherRepository;
import com.system.utils.UploadUtil;
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class AdvertisementService {

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
        User user = userRepository.findOneById(data.get("user_id").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        Advertisement advertisement = new Advertisement();
        advertisement.setUser(user);
        advertisement.setLocation(data.get("location").textValue());
        advertisement.setLng(data.get("lng").floatValue());
        advertisement.setLat(data.get("lat").floatValue());
        advertisement.setMoney(data.get("money").intValue());
        advertisement.setQuantity(data.get("quantity").intValue());
        advertisement.setRemaining(data.get("remaining").intValue());
        advertisement.setTitle(data.get("title").textValue());
        advertisement.setContent(data.get("content").textValue());
        advertisement.setPics(data.get("pics").textValue());
        Date create_time = new Date();
        advertisement.setCreate_time(create_time);
        advertisementRepository.save(advertisement);
    }

    public void update(JsonNode data) throws Exception{
        Advertisement advertisement = advertisementRepository.findOneById(data.get("adID").intValue());
        if (advertisement == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }
        advertisement.setRemaining(data.get("remaining").intValue());
        advertisement.setStatus(data.get("status").intValue());
        advertisementRepository.save(advertisement);

    }

    public ObjectNode get(String orderBy,String order,Integer pageSize,Integer pageNo,String keyword,Integer status,Integer adID,String startTime,String endTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(Advertisement.class,"advertisement");
        criteria.createAlias("advertisement.user","user", JoinType.LEFT_OUTER_JOIN);
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
                criteria.add(Restrictions.eq("advertisement."+key,value));
            }
        }
        if(adID!=null){
            criteria.add(Restrictions.eq("advertisement.id",adID));
        }
        if(status!=null){
            criteria.add(Restrictions.eq("advertisement.status",status));
        }
        if(startTime!=null&&endTime!=null){
            criteria.add(Restrictions.between("advertisement.create_time",format.parse(startTime),format.parse(endTime)));
        }
        Number totalNumber = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
        int total = totalNumber.intValue();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

        if(order!=null&&orderBy!=null){
            if(order.equals("desc")){
                criteria.addOrder(Order.desc("advertisement."+order));
            }else if(order.equals("asc")){
                criteria.addOrder(Order.asc("advertisement."+order));
            }
        }

        if(pageSize!=null&&pageNo!=null){
            criteria.setFirstResult((pageNo-1)*pageSize);
            criteria.setMaxResults(pageSize);
        }

        List<Advertisement> advertisements = criteria.list();
        ArrayNode array = mapper.createArrayNode();
        for(Advertisement item: advertisements){
            ObjectNode node = mapper.createObjectNode();
            node.put("id",item.getId());
            node.put("user_id",item.getUser().getId());
            node.put("location",item.getLocation());
            node.put("lng",item.getLng());
            node.put("lat",item.getLat());
            node.put("money",item.getMoney());
            node.put("title",item.getTitle());
            node.put("content",item.getContent());
            node.put("pics",item.getPics());
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
        Advertisement advertisement = advertisementRepository.findOneById(data.get("adID").intValue());
        if (advertisement == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }else {
            advertisementRepository.delete(advertisement);
            return true;
        }
    }

    /**
     * 上传广告图片
     * @param stream
     * @param contentType
     * @return
     * @throws Exception
     */
    public String uploadAdImage(InputStream stream, String contentType) throws Exception{
        String name = UploadUtil.generatorName();
        String out = fileProperties.getAdvertisementPath() + UploadUtil.separatar + name + "." + contentType;
        UploadUtil.upload(stream, out);
        String link = fileProperties.getAdvertisementUrl() +"/"+ name + "." + contentType;
        return link;

    }
}
