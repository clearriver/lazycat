/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.weixin.dao.UserTagDao;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.service.UserService;
import com.stooges.platform.weixin.service.UserTagService;

/**
 * 描述 用户标签业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-29 11:11:42
 */
@Service("userTagService")
public class UserTagServiceImpl extends BaseServiceImpl implements UserTagService {

    /**
     * 所引入的dao
     */
    @Resource
    private UserTagDao dao;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    /**
     * 
     */
    @Resource
    private UserService userService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取用户的数量
     * @param userTagId
     * @return
     */
    public int getUserCount(String userTagId,String publicId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM PLAT_WEIXIN_UANDTAG U ");
        sql.append("WHERE U.TAG_ID=? AND U.WEIUSER_ID IN ");
        sql.append("(SELECT T.USER_ID FROM PLAT_WEIXIN_USER T WHERE T.STATUS=?");
        sql.append(" AND T.PUBLIC_ID =? )");
        int count = dao.getIntBySql(sql.toString(), new Object[]{userTagId,1,publicId});
        return count;
    }
    
    /**
     * 获取标签列表
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findTagList(String publicId){
        List<Map<String,Object>> list = this.findSelectTagList(publicId);
        if(list==null){
            list = new ArrayList<Map<String,Object>>();
        }
        if(StringUtils.isNotEmpty(publicId)){
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("VALUE", "0");
            map.put("LABEL", "所有标签");
            list.add(0, map);
        }
        for(Map<String,Object> data:list){
            String VALUE = (String) data.get("VALUE");
            if(!VALUE.equals("0")){
                String LABEL = (String) data.get("LABEL");
                int userCount = this.getUserCount(VALUE,publicId);
                LABEL+="<font color='red'><b>("+userCount+")</b></font>";
                data.put("LABEL", LABEL);
            }
        }
        return list;
    }
    
    /**
     * 获取标签列表
     * @param queryParamJson
     * @return
     */
    public List<Map<String,Object>> findSelectTagList(String publicId){
        List<Map<String,Object>> list = null;
        if(StringUtils.isNotEmpty(publicId)){
            StringBuffer sql = new StringBuffer("select T.USERTAG_ID AS VALUE,T.USERTAG_NAME AS LABEL ");
            sql.append(",T.USERTAG_WEIID from PLAT_WEIXIN_USERTAG T ");
            sql.append(" WHERE T.USERTAG_PUBID=? ");
            sql.append("ORDER BY T.USERTAG_TIME ASC");
            list = dao.findBySql(sql.toString(),new Object[]{publicId}, null);
        }
        return list;
    }
    
    /**
     * 获取本地标签列
     * @param publicId
     * @return
     */
    public List<Map<String,Object>> findLocalTagList(String publicId){
        StringBuffer sql = new StringBuffer("select T.* ");
        sql.append(" from PLAT_WEIXIN_USERTAG T ");
        sql.append(" WHERE T.USERTAG_PUBID=? ");
        sql.append("ORDER BY T.USERTAG_TIME ASC");
        return dao.findBySql(sql.toString(),new Object[]{publicId}, null);
    }
    
    /**
     * 创建用户标签
     * @param publicId
     * @param tagName
     * @return
     */
    public String createWeixinUserTag(String publicId,String tagName){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/create?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        Map<String,String> tag = new HashMap<String,String>();
        tag.put("name", tagName);
        param.put("tag", tag);
        String result = PlatHttpUtil.postParams(url,JSON.toJSONString(param));
        Map json = JSON.parseObject(result,Map.class);
        Map resultTag = (Map) json.get("tag");
        if(resultTag!=null){
            return resultTag.get("id").toString();
        }else{
            return null;
        }
    }
    
    /**
     * 更新微信用户标签
     * @param publicId
     * @param weixinTagId
     * @param tagName
     * @return
     */
    public String updateWeixinUserTag(String publicId,int weixinTagId,String tagName){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/update?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        Map<String,Object> tag = new HashMap<String,Object>();
        tag.put("id", weixinTagId);
        tag.put("name", tagName);
        param.put("tag", tag);
        String result = PlatHttpUtil.postParams(url,JSON.toJSONString(param));
        Map json = JSON.parseObject(result,Map.class);
        String errcode = json.get("errcode").toString();
        if(errcode.equals("0")){
            return String.valueOf(weixinTagId);
        }else{
            return null;
        }
    }
    
    /**
     * 删除微信用户标签
     * @param publicId
     * @param weixinTagId
     * @return
     */
    public boolean delWeixinUserTag(String publicId,int weixinTagId){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/delete?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        Map<String,Object> tag = new HashMap<String,Object>();
        tag.put("id", weixinTagId);
        param.put("tag", tag);
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
     * 判断是否存在该标签
     * @param userTagName
     * @param pubId
     * @param userTagId
     * @return
     */
    public boolean isExists(String userTagName,String pubId,String userTagId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append(" PLAT_WEIXIN_USERTAG T WHERE T.USERTAG_NAME=? ");
        sql.append(" AND T.USERTAG_PUBID=? ");
        List params = new ArrayList();
        params.add(userTagName);
        params.add(pubId);
        if(StringUtils.isNotEmpty(userTagId)){
            sql.append(" AND T.USERTAG_ID!=? ");
            params.add(userTagId);
        }
        int count = dao.getIntBySql(sql.toString(), params.toArray());
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 新增或者保存用户标签
     * @param userTagInfo
     * @return
     */
    public Map<String,Object> saveOrUpdateTag(Map<String,Object> userTagInfo){
        String USERTAG_ID = (String) userTagInfo.get("USERTAG_ID");
        Map<String,Object> result = new HashMap<String,Object>();
        if(StringUtils.isEmpty(USERTAG_ID)){
            userTagInfo.put("USERTAG_TIME",PlatDateTimeUtil.
                    formatDate(new Date(),"yyyy-MM-dd HH:mm:ss"));
            this.saveOrUpdate("PLAT_WEIXIN_USERTAG",userTagInfo,
                    AllConstants.IDGENERATOR_UUID, null);
            result.put("success", true);
        }
        return result;
    }
    
    /**
     * 删除用户标签
     * @param userTagId
     * @return
     */
    public boolean deleteTag(String userTagId){
        Map<String,Object> oldUserTag = this.getRecord("PLAT_WEIXIN_USERTAG",
                new String[]{"USERTAG_ID"}, new Object[]{userTagId});
        String USERTAG_PUBID = (String) oldUserTag.get("USERTAG_PUBID");
        String USERTAG_WEIID = (String) oldUserTag.get("USERTAG_WEIID");
        if(StringUtils.isNotEmpty(USERTAG_WEIID)){
            boolean deleteResult = this.delWeixinUserTag(USERTAG_PUBID,Integer.parseInt(USERTAG_WEIID));
            if(deleteResult){
                this.deleteRecords("PLAT_WEIXIN_UANDTAG","TAG_ID",new String[]{userTagId});
                this.deleteRecord("PLAT_WEIXIN_USERTAG",new String[]{"USERTAG_ID"},
                        new Object[]{userTagId});
                return true;
            }else{
                return false;
            }
        }else{
            this.deleteRecords("PLAT_WEIXIN_UANDTAG","TAG_ID",new String[]{userTagId});
            this.deleteRecord("PLAT_WEIXIN_USERTAG",new String[]{"USERTAG_ID"},
                    new Object[]{userTagId});
            return true;
        }
        
    }
    
    /**
     * 为用户批量打上标签
     * @param userOpenIds
     * @param tagId
     * @return
     */
    public boolean signUsersTag(String[] userOpenIds,int tagId,String publicId){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchtagging?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("openid_list", userOpenIds);
        param.put("tagid", tagId);
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
     * 批量取消用户的标签
     * @param userOpenIds
     * @param tagId
     * @param publicId
     * @return
     */
    public boolean unsignUsersTag(String[] userOpenIds,int tagId,String publicId){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/members/batchuntagging?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("openid_list", userOpenIds);
        param.put("tagid", tagId);
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
     * 批量打上标签
     * @param USER_IDS
     * @param USER_TAGIDS
     */
    public void saveSignUsersTags(String USER_IDS,String USER_TAGIDS){
        String[] userIds = USER_IDS.split(",");
        String[] tagIds = USER_TAGIDS.split(",");
        for(String tagId:tagIds){
            for(String userId:userIds){
                Map<String,Object> uAndTag = this.getRecord("PLAT_WEIXIN_UANDTAG",
                        new String[]{"TAG_ID","WEIUSER_ID"},new Object[]{tagId,userId});
                if(uAndTag==null){
                    uAndTag = new HashMap<String,Object>();
                    uAndTag.put("TAG_ID", tagId);
                    uAndTag.put("WEIUSER_ID", userId);
                    dao.saveOrUpdate("PLAT_WEIXIN_UANDTAG",uAndTag,AllConstants.IDGENERATOR_ASSIGNED,null);
                }
            }
        }
    }
    
    /**
     * 获取公众号微信的标签列表
     * @param publicId
     * @return
     */
    public List<Map> findWeixinTagList(String publicId){
        String url = "https://api.weixin.qq.com/cgi-bin/tags/get?access_token=ACCESS_TOKEN";
        String token = publicService.getTokenByPublicId(publicId);
        url = url.replace("ACCESS_TOKEN", token);
        String result = PlatHttpUtil.httpsRequest(url, "GET", null);
        Map json = JSON.parseObject(result,Map.class);
        return (List<Map>) json.get("tags");
    }
    
    /**
     * 获取微信的标签MAP
     * @param publicId
     * @return
     */
    public Map<Integer,String> getWeixinTagMap(String publicId){
        Map<Integer,String> tagMap = new HashMap<Integer,String>();
        List<Map> list = this.findWeixinTagList(publicId);
        for(Map data:list){
            int id = Integer.parseInt(data.get("id").toString());
            String name = (String) data.get("name");
            tagMap.put(id, name);
        }
        return tagMap;
    }
    
    
    
    
    /**
     * 和微信的标签进行同步操作
     * @param publicId
     */
    public void refreshWeixinTag(String publicId){
        Map<Integer,String> weixinTag = this.getWeixinTagMap(publicId);
        //获取本地系统的标签列表
        List<Map<String,Object>> sysList = this.findLocalTagList(publicId);
        //本地标签集合
        Set<String> localTagNameSet = new HashSet<String>();
        for(Map<String,Object> sysData:sysList){
            String USERTAG_NAME = (String) sysData.get("USERTAG_NAME");
            localTagNameSet.add(USERTAG_NAME);
            String USERTAG_WEIID = (String) sysData.get("USERTAG_WEIID");
            if(StringUtils.isNotEmpty(USERTAG_WEIID)){
                int weixinId = Integer.parseInt(USERTAG_WEIID);
                String tagName = weixinTag.get(weixinTag);
                if(StringUtils.isNotEmpty(tagName)&&!USERTAG_NAME.equals(tagName)){
                    this.updateWeixinUserTag(publicId, weixinId, USERTAG_NAME);
                }
            }else{
                USERTAG_WEIID = this.createWeixinUserTag(publicId, USERTAG_NAME);
                sysData.put("USERTAG_WEIID", USERTAG_WEIID);
                dao.saveOrUpdate("PLAT_WEIXIN_USERTAG",sysData,AllConstants.IDGENERATOR_UUID,null);
            }
        }
        Iterator it = weixinTag.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer,String> entry = (Map.Entry<Integer,String>) it.next();
            int tagId = entry.getKey();
            String tagName = entry.getValue();
            if(!localTagNameSet.contains(tagName)){
                Map<String,Object> userTag = new HashMap<String,Object>();
                userTag.put("USERTAG_NAME", tagName);
                userTag.put("USERTAG_TIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
                userTag.put("USERTAG_PUBID", publicId);
                userTag.put("USERTAG_WEIID", tagId);
                dao.saveOrUpdate("PLAT_WEIXIN_USERTAG",userTag,AllConstants.IDGENERATOR_UUID,null);
            }
        }
        
    }
    
    /**
     * 获取本地存在的微信标签ID
     * @param userId
     * @return
     */
    public List<String> findLocalUserWeixinTagId(String userId){
        return dao.findLocalUserWeixinTagId(userId);
    }
    
    /**
     * 获取自动补全的数据
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findAutoTagUser(SqlFilter filter){
        String publicId = filter.getRequest().getParameter("publicId");
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        if(StringUtils.isNotEmpty(publicId)){
            StringBuffer sql = new StringBuffer("SELECT R.USERTAG_NAME AS value,R.USERTAG_NAME AS label");
            sql.append(" FROM PLAT_WEIXIN_USERTAG R WHERE R.USERTAG_PUBID=?");
            sql.append(" ORDER BY R.USERTAG_TIME DESC");
            List<Map<String,Object>> groupList = dao.findBySql(sql.toString(),new Object[]{publicId},null);
            list.addAll(groupList);
            //获取所有角色数据
            sql = new StringBuffer("SELECT R.NICK_NAME AS value,R.NICK_NAME AS label");
            sql.append(" FROM PLAT_WEIXIN_USER R WHERE R.PUBLIC_ID=? ");
            sql.append(" ORDER BY R.CREATETIME DESC ");
            List<Map<String,Object>> roleList = dao.findBySql(sql.toString(),new Object[]{publicId},null);
            list.addAll(roleList);
        }
        return list;
    }
    
    /**
     * 获取树形JSON
     * @param params
     * @return
     */
    public String getTreeJson(Map<String,Object> params){
        String publicId = (String) params.get("publicId");
        if(StringUtils.isNotEmpty(publicId)){
            String needCheckIds = (String) params.get("needCheckIds");
            Set<String> needCheckIdSet = new HashSet<String>();
            if(StringUtils.isNotEmpty(needCheckIds)){
                needCheckIdSet = new HashSet<String>(Arrays.asList(needCheckIds.split(",")));
            }
            Map<String,Object> rootNode = new HashMap<String,Object>();
            rootNode.put("id", "0");
            rootNode.put("name", "用户信息树");
            List<Map<String,Object>> groupList = this.findLocalTagList(publicId);
            for(Map<String,Object> group:groupList){
                String groupId = (String) group.get("USERTAG_ID");
                String groupName = (String) group.get("USERTAG_NAME");
                group.put("id", groupId);
                group.put("name", groupName);
                List<Map<String,Object>> dataList = userService.findUserList(publicId, groupId);
                if(dataList!=null&&dataList.size()>0){
                    for(Map<String,Object> data:dataList){
                        String dataId = (String) data.get("USER_ID");
                        if(needCheckIdSet.contains(dataId)){
                            data.put("checked", true);
                        }
                        data.put("id", data.get("USER_ID"));
                        data.put("name", data.get("NICK_NAME"));
                    }
                    group.put("children", dataList);
                }
            }
            rootNode.put("children", groupList);
            return JSON.toJSONString(rootNode);
        }else{
            return "";
        }
        
    }
  
}
