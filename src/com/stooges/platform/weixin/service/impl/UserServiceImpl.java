/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.AppEmailService;
import com.stooges.platform.weixin.dao.UserDao;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.service.UserService;
import com.stooges.platform.weixin.service.UserTagService;

/**
 * 描述 微信用户业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 16:30:21
 */
@Service("weixinUserService")
public class UserServiceImpl extends BaseServiceImpl implements UserService {

    /**
     * 所引入的dao
     */
    @Resource(name="weixinUserDao")
    private UserDao dao;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    /**
     * 
     */
    @Resource
    private AppEmailService appEmailService;
    /**
     * 
     */
    @Resource
    private UserTagService userTagService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取用户信息
     * @param openId
     * @param sourceId
     * @return
     */
    public Map<String,Object> getUserInfo(String openId,String sourceId){
        String token = publicService.getToken(sourceId);
        String url = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=ACCESS_TOKEN&openid=OPENID&lang=zh_CN";
        url = url.replace("ACCESS_TOKEN", token).replace("OPENID", openId);
        String jsonResult = PlatHttpUtil.httpsRequest(url,"GET", null);
        return JSON.parseObject(jsonResult, Map.class);
    }
    
    /**
     * 保存用户信息
     * @param openId
     * @param sourceId
     */
    public void saveUserInfo(String openId,String sourceId){
        //获取公众号信息
        Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_SOURCEID"}, new Object[]{sourceId});
        Map<String,Object> userInfo = this.getUserInfo(openId, sourceId);
        Map<String,Object> newUser = new HashMap<String,Object>();
        newUser.put("OPEN_ID", openId);
        newUser.put("NICK_NAME", userInfo.get("nickname"));
        newUser.put("SEX", userInfo.get("sex"));
        newUser.put("CITY", userInfo.get("city"));
        newUser.put("COUNTRY", userInfo.get("country"));
        newUser.put("PROVINCE", userInfo.get("province"));
        newUser.put("HEADURL", userInfo.get("headimgurl"));
        newUser.put("CREATETIME", PlatDateTimeUtil.formatDate(
                new Date(), "yyyy-MM-dd HH:mm:ss"));
        newUser.put("REMARK", userInfo.get("remark"));
        newUser.put("GROUP_ID", userInfo.get("groupid"));
        newUser.put("PUBLIC_ID",publicInfo.get("PUBLIC_ID"));
        newUser.put("STATUS",1);
        dao.saveOrUpdate("PLAT_WEIXIN_USER",newUser,
                AllConstants.IDGENERATOR_UUID,null);
    }
    
    /**
     * 获取所有关注用户列表
     * @return
     */
    public List<Map<String,Object>> findUserList(String publicId){
        StringBuffer sql = new StringBuffer("SELECT T.* FROM ");
        sql.append("PLAT_WEIXIN_USER T WHERE T.PUBLIC_ID=? ORDER BY T.CREATETIME ASC");
        return dao.findBySql(sql.toString(),new Object[]{publicId}, null);
    }
    
    /**
     * 获取用户列表
     * @param publicId
     * @param tagId
     * @return
     */
    public List<Map<String,Object>> findUserList(String publicId,String tagId){
        StringBuffer sql = new StringBuffer("SELECT T.* FROM ");
        sql.append("PLAT_WEIXIN_USER T WHERE T.PUBLIC_ID=? ");
        sql.append(" AND T.USER_ID IN (SELECT G.WEIUSER_ID FROM PLAT_WEIXIN_UANDTAG G");
        sql.append(" WHERE G.TAG_ID=? ) ");
        sql.append(" ORDER BY T.CREATETIME ASC");
        return dao.findBySql(sql.toString(),new Object[]{publicId,tagId}, null);
    }
    
    /**
     * 发送邮件提醒给用户
     * @param subject
     * @param content
     * @param sourceId
     */
    public void sendEmailToUsers(String subject,String content,String sourceId){
        //获取公众号信息
        Map<String,Object> publicInfo = this.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_SOURCEID"},new Object[]{sourceId});
        String PUBLIC_ID = (String) publicInfo.get("PUBLIC_ID");
        List<Map<String,Object>> userList = this.findUserList(PUBLIC_ID);
        List<String> emailList = new ArrayList<String>();
        for(Map<String,Object> user:userList){
            String EMAIL = (String) user.get("EMAIL");
            if(StringUtils.isNotEmpty(EMAIL)){
                emailList.add(EMAIL);
            }
        }
        if(emailList.size()!=0){
            appEmailService.sendSimpleMail(subject, content, 
                    emailList.toArray(new String[emailList.size()]));
        }
       
    }
    
    /**
     * 更新用户的微信备注
     * @param userId
     */
    public void updateUserRemark(String userId,String remark){
        Map<String,Object> user = dao.getRecord("PLAT_WEIXIN_USER",
                new String[]{"USER_ID"}, new Object[]{userId});
        String OPEN_ID = (String) user.get("OPEN_ID");
        String PUBLIC_ID = (String) user.get("PUBLIC_ID");
        //获取公众号信息
        Map<String,Object> publicInfo = this.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_ID"},new Object[]{PUBLIC_ID});
        String PUBLIC_SOURCEID = (String) publicInfo.get("PUBLIC_SOURCEID");
        String token = publicService.getToken(PUBLIC_SOURCEID);
        String url = "https://api.weixin.qq.com/cgi-bin/user/info/updateremark?access_token=ACCESS_TOKENPOST";
        url = url.replace("ACCESS_TOKENPOST", token);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("openid", OPEN_ID);
        params.put("remark", remark);
        PlatHttpUtil.postParams(url, JSON.toJSONString(params));
    }
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo){
        StringBuffer sql = new StringBuffer("select C.PUBLIC_NAME,T.USER_ID,T.OPEN_ID,T.NICK_NAME,T.SEX,");
        sql.append("T.COUNTRY,T.PROVINCE,T.CITY,T.CREATETIME,T.REMARK,T.HEADURL");
        sql.append(" from PLAT_WEIXIN_USER T LEFT JOIN PLAT_WEIXIN_PUBLIC");
        sql.append(" C ON T.PUBLIC_ID = C.PUBLIC_ID WHERE T.STATUS=1 ");
        List<Object> params = new ArrayList<Object>();
        String TAG_ID = filter.getRequest().getParameter("TAG_ID");
        if(StringUtils.isNotEmpty(TAG_ID)){
            sql.append(" AND T.USER_ID IN (SELECT UT.WEIUSER_ID ");
            sql.append("FROM PLAT_WEIXIN_UANDTAG UT WHERE UT.TAG_ID=?) ");
            params.add(TAG_ID);
        }
        String exeSql = dao.getQuerySql(filter, sql.toString(), params);
        List<Map<String, Object>> list = dao.findBySql(exeSql,
                params.toArray(), filter.getPagingBean());
        return list;
    }
    
    /**
     * 获取用户被打的标签IDS
     * @param userId
     * @return
     */
    public List<String> getUserTagIds(String userId){
        return dao.getUserTagIds(userId);
    }
    
    /**
     * 更新用户的标签
     * @param userId
     * @param tagIds
     */
    public void updateUserTags(String userId,String tagIds){
        //将用户的标签进行清除
        this.deleteRecords("PLAT_WEIXIN_UANDTAG","WEIUSER_ID",new String[]{userId});
        Map<String,Object> userInfo = this.getRecord("PLAT_WEIXIN_USER"
                ,new String[]{"USER_ID"},new Object[]{userId});
        String publicId = (String) userInfo.get("PUBLIC_ID");
        String openId = (String) userInfo.get("OPEN_ID");
        //获取现有的微信用户标签列表
        List<Map> tagList = userTagService.findWeixinTagList(publicId);
        for(Map tag:tagList){
            int tagId = Integer.parseInt(tag.get("id").toString());
            userTagService.unsignUsersTag(new String[]{openId},tagId, publicId);
        }
        if(StringUtils.isNotEmpty(tagIds)){
            userTagService.saveSignUsersTags(userId, tagIds);
        }
    }
    
    /**
     * 获取公众号关注的用户OPENIDS
     * @param publicId
     * @return
     */
    public List<String> getWeixinOpenIds(String publicId){
        String token = publicService.getTokenByPublicId(publicId);
        String url = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String jsonResult = PlatHttpUtil.httpsRequest(url,"GET", null);
        Map result = JSON.parseObject(jsonResult,Map.class);
        int total = Integer.parseInt(result.get("total").toString());
        if(total!=0){
            Map data = (Map) result.get("data");
            List idList = (List) data.get("openid");
            List<String> openIds = new ArrayList<String>();
            for(Object id:idList){
                openIds.add(id.toString());
            }
            return openIds;
        }else{
            return null;
        }
    }
    
    /**
     * 保存本地用户信息
     * @param userInfo
     * @param publicId
     * @return
     */
    private Map<String,Object> saveLocalUser(Map<String,Object> userInfo,String publicId){
        //获取openId
        String openid = (String) userInfo.get("openid");
        String country = (String) userInfo.get("country");
        String city = (String) userInfo.get("city");
        String sex =  userInfo.get("sex").toString();
        String remark = (String) userInfo.get("remark");
        String province = (String) userInfo.get("province");
        String nickname = (String) userInfo.get("nickname");
        String headimgurl = (String) userInfo.get("headimgurl");
        //获取用户信息
        Map<String,Object> localUser = this.getRecord("PLAT_WEIXIN_USER",
                new String[]{"OPEN_ID","PUBLIC_ID"},new Object[]{openid,publicId});
        if(localUser==null){
            localUser = new HashMap<String,Object>();
            localUser.put("STATUS", 1);
            localUser.put("CREATETIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        }
        localUser.put("OPEN_ID", openid);
        localUser.put("NICK_NAME", nickname);
        localUser.put("SEX", sex);
        localUser.put("CITY", city);
        localUser.put("COUNTRY", country);
        localUser.put("PROVINCE", province);
        localUser.put("HEADURL", headimgurl);
        localUser.put("REMARK", remark);
        localUser.put("PUBLIC_ID", publicId);
        localUser = dao.saveOrUpdate("PLAT_WEIXIN_USER",localUser,AllConstants.IDGENERATOR_UUID,null);
        return localUser;
    }
    
    /**
     * 重新刷新微信用户信息
     * @param publicId
     */
    public void refreshWeixinUserInfo(String publicId){
        List<String> openIds = this.getWeixinOpenIds(publicId);
        if(openIds!=null&&openIds.size()>0){
            //获取公众号信息
            Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                    new String[]{"PUBLIC_ID"}, new Object[]{publicId});
            String PUBLIC_SOURCEID = (String) publicInfo.get("PUBLIC_SOURCEID");
            for(String openId:openIds){
                Map<String,Object> userInfo = this.getUserInfo(openId, PUBLIC_SOURCEID);
                Map<String,Object> localUser = this.saveLocalUser(userInfo, publicId);
                String USER_ID = (String) localUser.get("USER_ID");
                List<String> localTagIdList = userTagService.findLocalUserWeixinTagId(USER_ID);
                for(String tagId:localTagIdList){
                    userTagService.signUsersTag(new String[]{openId},
                            Integer.parseInt(tagId), publicId);
                }
            }
            
        }
    }
    
    /**
     * 同步微信用户标签和用户信息
     * @param publicId
     */
    public void refreshWeixinUserAndTag(String publicId){
        this.userTagService.refreshWeixinTag(publicId);
        this.refreshWeixinUserInfo(publicId);
    }
    
    /**
     * 拉黑微信用户
     * @param publicId
     * @param openIds
     * @return
     */
    public boolean batchWeixinBlackUsers(String publicId,String[] openIds){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchblacklist?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("openid_list", openIds);
        String result = PlatHttpUtil.postParams(url,JSON.toJSONString(param));
        Map json = JSON.parseObject(result,Map.class);
        String errcode = json.get("errcode").toString();
        if(errcode.equals("0")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 取消拉黑微信用户
     * @param publicId
     * @param openIds
     * @return
     */
    public boolean unWeixinBlackUsers(String publicId,String[] openIds){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchunblacklist?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("openid_list", openIds);
        String result = PlatHttpUtil.postParams(url,JSON.toJSONString(param));
        Map json = JSON.parseObject(result,Map.class);
        String errcode = json.get("errcode").toString();
        if(errcode.equals("0")){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 更新用户的状态
     * @param publicId
     * @param userIds
     * @param status
     */
    public void updateUserStatus(String publicId,String userIds,int status){
        boolean result = false;
        if(status==UserService.STATUS_BLACK){
            List<String> openIds = dao.findUserOpenIdList(userIds);
            result = this.batchWeixinBlackUsers(publicId, 
                    openIds.toArray(new String[openIds.size()]));
        }else if(status==UserService.STATUS_NORMAL){
           String[] userIdArray = userIds.split(",");
           for(String userId:userIdArray){
               Map<String,Object> userInfo = this.getRecord("PLAT_WEIXIN_USER"
                       ,new String[]{"USER_ID"}, new Object[]{userId});
               String OPEN_ID = (String) userInfo.get("OPEN_ID");
               String PUBLIC_ID = (String) userInfo.get("PUBLIC_ID");
               this.unWeixinBlackUsers(PUBLIC_ID, new String[]{OPEN_ID});
           }
           result = true;
        }
        if(result){
            StringBuffer sql = new StringBuffer("UPDATE PLAT_WEIXIN_USER");
            sql.append(" SET STATUS=? WHERE USER_ID IN ");
            sql.append(PlatStringUtil.getSqlInCondition(userIds));
            dao.executeSql(sql.toString(), new Object[]{status});
        }
    }
    
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter){
        StringBuffer sql = new StringBuffer("SELECT T.USER_ID,T.NICK_NAME");
        sql.append(",T.CITY FROM PLAT_WEIXIN_USER T ");
        String selectedRecordIds = sqlFilter.getRequest().getParameter("selectedRecordIds");
        String iconfont = sqlFilter.getRequest().getParameter("iconfont");
        String itemconf = sqlFilter.getRequest().getParameter("itemconf");
        Map<String,String> getGridItemConf = PlatUICompUtil.getGridItemConfMap(itemconf);
        if(StringUtils.isNotEmpty(selectedRecordIds)){
            sql.append(" WHERE T.USER_ID IN ");
            sql.append(PlatStringUtil.getSqlInCondition(selectedRecordIds));
            sql.append(" ORDER BY T.CREATETIME DESC");
            List<Map<String,Object>> list = dao.findBySql(sql.toString(),null, null);
            list = PlatUICompUtil.getGridItemList("USER_ID", iconfont, getGridItemConf, list);
            return list;
        }else{
            return null;
        }
    }
    
    /**
     * 获取用户的openId列表
     * @param userIds
     * @return
     */
    public List<String> findUserOpenIdList(String userIds){
        return dao.findUserOpenIdList(userIds);
    }
  
    /**
     * 获取用户的IDS
     * @param publicId
     * @return
     */
    public List<String> findUserIds(String publicId){
        return dao.findUserIds(publicId);
    }
    
    /**
     * 根据公众ID获取openIds
     * @param publicId
     * @return
     */
    public List<String> findUserOpenIdsByPublicId(String publicId){
        return dao.findUserOpenIdsByPublicId(publicId);
    }
}
