package com.system.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.system.Enum.ResultEnum;
import com.system.Exception.SystemException;
import com.system.Model.User;
import com.system.Model.Voucher;
import com.system.Model.VoucherRecord;
import com.system.Properties.FileProperties;
import com.system.Repository.UserRepository;
import com.system.Repository.VoucherRecordRepository;
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
public class VoucherRecordService {

    @Autowired
    private VoucherRecordRepository voucherRecordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoucherRepository voucherRepository;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private FileProperties fileProperties;

    @Autowired
    private SessionFactory sessionFactory;


    public void create(JsonNode data) throws Exception{
        VoucherRecord voucherRecord = voucherRecordRepository.findOneById(data.get("id").intValue());
        if (voucherRecord != null){
            throw new SystemException(ResultEnum.OBJECT_ALREADY_EXIST.getCode(),ResultEnum.OBJECT_ALREADY_EXIST.getMsg());
        }
        User user = userRepository.findOneById(data.get("receiverID").intValue());
        if (user == null){
            throw new SystemException(ResultEnum.USER_NOT_FOUND.getCode(),ResultEnum.USER_NOT_FOUND.getMsg());
        }
        Voucher voucher = voucherRepository.findOneById(data.get("voucherID").intValue());
        if (voucher == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }
        voucherRecord = new VoucherRecord();
        voucherRecord.setVoucher(voucher);
        voucherRecord.setUser(user);
        Date create_time = new Date();
        voucherRecord.setCreate_time(create_time);
        Date valid_time = new Date(create_time.getTime()+259200000);
        voucherRecord.setValid_time(valid_time);
        voucherRecordRepository.save(voucherRecord);
    }

    public void update(JsonNode data) throws Exception{
        VoucherRecord voucherRecord = voucherRecordRepository.findOneById(data.get("id").intValue());
        if (voucherRecord == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }
        voucherRecord.setStatus(data.get("status").intValue());
        voucherRecordRepository.save(voucherRecord);

    }

    public ObjectNode get(String orderBy,String order,Integer pageSize,Integer pageNo,String keyword,Integer status,Integer id,Integer voucherID,Integer receiverID,String startTime,String endTime) throws Exception{
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria(VoucherRecord.class,"voucherRecord");
        criteria.createAlias("voucherRecord.user","receiver", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("voucherRecord.voucher","voucher", JoinType.LEFT_OUTER_JOIN);
        //输入的keyword是这种格式 id = xxxx or username = xxx or tel = xxxx
        if(keyword!=null){
            String[] keywords = keyword.split("or");
            for (int i =0;i<keywords.length;i++){
                String key = keywords[i].split("=")[0];
                String value = keywords[i].split("=")[1];
                if(key.equals("voucherID")){
                    criteria.add(Restrictions.eq("voucher.id",value));
                    continue;
                }
                if(key.equals("receiverID")){
                    criteria.add(Restrictions.eq("receiver.id",value));
                    continue;
                }
                criteria.add(Restrictions.eq("voucherRecord."+key,value));
            }
        }
        if(id!=null){
            criteria.add(Restrictions.eq("voucherRecord.id",id));
        }
        if(voucherID!=null){
            criteria.add(Restrictions.eq("voucher.id",voucherID));
        }
        if(receiverID!=null){
            criteria.add(Restrictions.eq("receiver.id",receiverID));
        }
        if(status!=null){
            criteria.add(Restrictions.eq("voucherRecord.status",status));
        }
        if(startTime!=null&&endTime!=null){
            criteria.add(Restrictions.between("voucherRecord.create_time",format.parse(startTime),format.parse(endTime)));
        }
        Number totalNumber = (Number) criteria.setProjection(Projections.rowCount()).uniqueResult();
        int total = totalNumber.intValue();
        criteria.setProjection(null);
        criteria.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);

        if(order!=null&&orderBy!=null){
            if(order.equals("desc")){
                criteria.addOrder(Order.desc("voucherRecord."+order));
            }else if(order.equals("asc")){
                criteria.addOrder(Order.asc("voucherRecord."+order));
            }
        }

        if(pageSize!=null&&pageNo!=null){
            criteria.setFirstResult((pageNo-1)*pageSize);
            criteria.setMaxResults(pageSize);
        }

        List<VoucherRecord> voucherRecords = criteria.list();
        ArrayNode array = mapper.createArrayNode();
        for(VoucherRecord item: voucherRecords){
            ObjectNode node = mapper.createObjectNode();
            node.put("id",item.getId());
            node.put("voucher_id",item.getVoucher().getId());
            node.put("receiver_id",item.getUser().getId());
            node.put("create_time",format.format(item.getCreate_time()));
            node.put("valid_time",format.format(item.getValid_time()));
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
        VoucherRecord voucherRecord = voucherRecordRepository.findOneById(data.get("id").intValue());
        if (voucherRecord == null){
            throw new SystemException(ResultEnum.OBJECT_NOT_FOUND.getCode(),ResultEnum.OBJECT_NOT_FOUND.getMsg());
        }else {
            voucherRecordRepository.delete(voucherRecord);
            return true;
        }
    }
}
