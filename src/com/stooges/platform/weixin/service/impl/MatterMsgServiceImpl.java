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

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.weixin.dao.MatterMsgDao;
import com.stooges.platform.weixin.service.MatterMsgService;
import com.stooges.platform.weixin.service.MediaMatterService;
import com.stooges.platform.weixin.service.TextMatterService;
import com.stooges.platform.weixin.service.UserService;

/**
 * 描述 素材消息群发业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-05 13:48:09
 */
@Service("matterMsgService")
public class MatterMsgServiceImpl extends BaseServiceImpl implements MatterMsgService {

    /**
     * 所引入的dao
     */
    @Resource
    private MatterMsgDao dao;
    /**
     * 
     */
    @Resource
    private UserService userService;
    /**
     * 
     */
    @Resource
    private TextMatterService textMatterService;
    /**
     * 
     */
    @Resource
    private MediaMatterService mediaMatterService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据类型和公众号ID获取素材列表
     * @param typeAndPublicId
     * @return
     */
    public List<Map<String,Object>> findMatterList(String typeAndPublicId){
        if(StringUtils.isNotEmpty(typeAndPublicId)&&typeAndPublicId.length()>1){
            String type = typeAndPublicId.split(",")[0];
            String publicId = typeAndPublicId.split(",")[1];
            StringBuffer sql = null;
            List<Object> params = new ArrayList<Object>();
            if(type.equals("text")){
                sql = new StringBuffer("SELECT T.TEXTMATTER_ID AS VALUE,");
                sql.append("T.TEXTMATTER_NAME AS LABEL FROM PLAT_WEIXIN_TEXTMATTER T");
                sql.append(" WHERE T.TEXTMATTER_PUBID=? ORDER BY T.TEXTMATTER_TIME ASC");
                params.add(publicId);
            }else{
                sql = new StringBuffer("SELECT T.MEDIAMATTER_ID AS VALUE,");
                sql.append("T.MEDIAMATTER_NAME AS LABEL FROM PLAT_WEIXIN_MEDIAMATTER T");
                sql.append(" WHERE T.MEDIAMATTER_PUBID=? AND T.MEDIAMATTER_TYPE=? ");
                sql.append("ORDER BY T.MEDIAMATTER_TIME ASC");
                params.add(publicId);
                params.add(type);
            }
            return dao.findBySql(sql.toString(), params.toArray(), null);
        }else{
            return null;
        }
    }
    
    /**
     * 发送文本消息
     * @param MATTERMSG_MID
     * @param matterMsg
     * @param userIds
     * @param sourceId
     * @param MATTERMSG_RETAGIDS
     * @param MATTERMSG_PUBID
     * @return
     */
    private String sendTextMsg(String MATTERMSG_MID,Map<String,Object> matterMsg
            ,String userIds,String sourceId,String MATTERMSG_RETAGIDS,String MATTERMSG_PUBID){
        Map<String,Object> textMatter = textMatterService.getRecord("PLAT_WEIXIN_TEXTMATTER",
                new String[]{"TEXTMATTER_ID"},new Object[]{MATTERMSG_MID});
        String TEXTMATTER_CONTENT = (String) textMatter.get("TEXTMATTER_CONTENT");
        matterMsg.put("MATTERMSG_MNAME", textMatter.get("TEXTMATTER_NAME"));
        String sendResult = null;
        if(StringUtils.isNotEmpty(userIds)){
            List<String> openIds = userService.findUserOpenIdList(userIds);
            sendResult = textMatterService.sendMsgToUsers(openIds.toArray(new String[openIds.size()]), 
                    sourceId, TEXTMATTER_CONTENT);
        }else{
            String[] tagIdArray= MATTERMSG_RETAGIDS.split(",");
            for(String tagId:tagIdArray){
                Map<String,Object> tagInfo = this.getRecord("PLAT_WEIXIN_USERTAG",
                        new String[]{"USERTAG_ID"},new Object[]{tagId});
                String USERTAG_WEIID = (String) tagInfo.get("USERTAG_WEIID");
                sendResult = textMatterService.sendMsgToTagUsers(USERTAG_WEIID,MATTERMSG_PUBID,TEXTMATTER_CONTENT);
            }
        }
        return sendResult;
    }
    
    /**
     * 
     * @param MATTERMSG_MID
     * @param matterMsg
     * @param userIds
     * @param sourceId
     * @param MATTERMSG_RETAGIDS
     * @param MATTERMSG_PUBID
     * @return
     */
    private String sendMediaMsg(String MATTERMSG_MID,Map<String,Object> matterMsg
            ,String userIds,String sourceId,String MATTERMSG_RETAGIDS,
            String MATTERMSG_PUBID,String MATTERMSG_TYPE){
        Map<String,Object> mediaMatter = this.getRecord("PLAT_WEIXIN_MEDIAMATTER",
                new String[]{"MEDIAMATTER_ID"},new Object[]{MATTERMSG_MID});
        String MEDIAMATTER_MEDIAID = (String) mediaMatter.get("MEDIAMATTER_MEDIAID");
        String sendResult = null;
        if(StringUtils.isNotEmpty(userIds)){
            List<String> openIds = userService.findUserOpenIdList(userIds);
            sendResult = mediaMatterService.sendMsgToUsers(openIds.toArray(new String[openIds.size()]),
                    sourceId, MEDIAMATTER_MEDIAID, MATTERMSG_TYPE);
        }else{
            String[] tagIdArray= MATTERMSG_RETAGIDS.split(",");
            for(String tagId:tagIdArray){
                Map<String,Object> tagInfo = this.getRecord("PLAT_WEIXIN_USERTAG",
                        new String[]{"USERTAG_ID"},new Object[]{tagId});
                String USERTAG_WEIID = (String) tagInfo.get("USERTAG_WEIID");
                sendResult = mediaMatterService.sendMsgToUsers(USERTAG_WEIID, MATTERMSG_PUBID,
                        MEDIAMATTER_MEDIAID,MATTERMSG_TYPE);
            }
        }
        return sendResult;
    }
    
    /**
     * 进行消息的群发
     * @param matterMsg
     */
    public Map<String,Object> saveMatterMsg(Map<String,Object> matterMsg){
        String MATTERMSG_REIDS = (String) matterMsg.get("MATTERMSG_REIDS");
        String MATTERMSG_PUBID = (String) matterMsg.get("MATTERMSG_PUBID");
        String MATTERMSG_TYPE = (String) matterMsg.get("MATTERMSG_TYPE");
        String MATTERMSG_MID = (String) matterMsg.get("MATTERMSG_MID");
        String MATTERMSG_RETYPE= (String) matterMsg.get("MATTERMSG_RETYPE");
        String MATTERMSG_RETAGIDS= (String) matterMsg.get("MATTERMSG_RETAGIDS");
        String userIds = null;
        if(MATTERMSG_RETYPE.equals("1")){
            List<String> userIdArray = userService.findUserIds(MATTERMSG_PUBID);
            userIds = PlatStringUtil.getListStringSplit(userIdArray);
        }else if(MATTERMSG_RETYPE.equals("2")){
            userIds = MATTERMSG_REIDS;
        }
        Map<String,Object> publicInfo = this.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_ID"}, new Object[]{MATTERMSG_PUBID});
        String sendResult = null;
        String sourceId = (String) publicInfo.get("PUBLIC_SOURCEID");
        if(MATTERMSG_TYPE.equals(MatterMsgService.TYPE_TEXT)){
            sendResult =  this.sendTextMsg(MATTERMSG_MID, matterMsg, userIds, 
                    sourceId, MATTERMSG_RETAGIDS, MATTERMSG_PUBID);
        }else if(MATTERMSG_TYPE.equals(MatterMsgService.TYPE_IMAGE)
                ||MATTERMSG_TYPE.equals(MatterMsgService.TYPE_VOICE)){
            sendResult = this.sendMediaMsg(MATTERMSG_MID, matterMsg, userIds, sourceId,
                    MATTERMSG_RETAGIDS, MATTERMSG_PUBID, MATTERMSG_TYPE);
        }else if(MATTERMSG_TYPE.equals(MatterMsgService.TYPE_VIDEO)){
            if(StringUtils.isNotEmpty(userIds)){
                List<String> openIds = userService.findUserOpenIdList(userIds);
                sendResult = mediaMatterService.sendVideoMsgToUsers(openIds.toArray(new String[openIds.size()])
                        ,MATTERMSG_MID);
            }else{
                String[] tagIdArray= MATTERMSG_RETAGIDS.split(",");
                for(String tagId:tagIdArray){
                    Map<String,Object> tagInfo = this.getRecord("PLAT_WEIXIN_USERTAG",
                            new String[]{"USERTAG_ID"},new Object[]{tagId});
                    String USERTAG_WEIID = (String) tagInfo.get("USERTAG_WEIID");
                    sendResult = mediaMatterService.sendVideoMsgToUsers(USERTAG_WEIID
                            ,MATTERMSG_MID);
                }
            }
        }
        Map<String,Object> sendResultJson = JSON.parseObject(sendResult,Map.class);
        String errcode= sendResultJson.get("errcode").toString();
        String errmsg = (String) sendResultJson.get("errmsg");
        Map<String,Object> result = new HashMap<String,Object>();
        if(errcode.equals("0")){
            matterMsg.put("MATTERMSG_TIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
            dao.saveOrUpdate("PLAT_WEIXIN_MATTERMSG",matterMsg,AllConstants.IDGENERATOR_UUID,null);
            result.put("success", true);
        }else{
            result.put("success", false);
            result.put("msg", errmsg);
        }
        return result;
    }
  
}
