/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.weixin.dao.MediaMatterDao;
import com.stooges.platform.weixin.service.MatterMsgService;
import com.stooges.platform.weixin.service.MediaMatterService;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.util.WeixinCoreUtil;

/**
 * 描述 媒体素材业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-27 16:24:07
 */
@Service("mediaMatterService")
public class MediaMatterServiceImpl extends BaseServiceImpl implements MediaMatterService {

    /**
     * 所引入的dao
     */
    @Resource
    private MediaMatterDao dao;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;
    /**
     * 
     */
    @Resource
    private PublicService publicService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 新增或者修改素材
     * @param mediaMatter
     * @param FILES_JSON
     * @param matterType
     */
    public Map<String,Object> saveOrUpdateMatter(Map<String,Object> mediaMatter,
            String FILES_JSON,String matterType){
        Map<String,Object> result = new HashMap<String,Object>();
        String MEDIAMATTER_PUBID = (String) mediaMatter.get("MEDIAMATTER_PUBID");
        String token = publicService.getTokenByPublicId(MEDIAMATTER_PUBID);
        //获取旧的ID
        String oldMatterId = (String) mediaMatter.get("MEDIAMATTER_ID");
        if(StringUtils.isNotEmpty(oldMatterId)){
            //加载旧数据
            Map<String,Object> oldMatter = this.getRecord("PLAT_WEIXIN_MEDIAMATTER",
                    new String[]{"MEDIAMATTER_ID"},new Object[]{oldMatterId});
            String MEDIAMATTER_MEDIAID = (String) oldMatter.get("MEDIAMATTER_MEDIAID");
            this.deleteMatter(token, MEDIAMATTER_MEDIAID);
        }
        List<Map> fileList = JSON.parseArray(FILES_JSON,Map.class);
        Map file = fileList.get(0);
        String dbfilepath = (String) file.get("dbfilepath");
        String attachFileUrl = PlatPropUtil.getPropertyValue("conf/config.properties"
                ,"attachFileUrl");
        String fullPath = attachFileUrl+dbfilepath;
        String mediaId = null;
        if(matterType.equals(WeixinCoreUtil.MATTER_VIDEO)){
            String title = (String) mediaMatter.get("MEDIAMATTER_NAME");
            String introduction = (String) mediaMatter.get("MEDIAMATTER_DESC");
            result =  WeixinCoreUtil.addVideoMatter(token,fullPath, title, introduction);
        }else{
            result =  WeixinCoreUtil.addMaterials(token,matterType, fullPath);
        }
        if(result.get("media_id")!=null){
            mediaId = (String) result.get("media_id");
            mediaMatter.put("MEDIAMATTER_MEDIAID",mediaId);
            String MEDIAMATTER_MEDIAURL = (String) result.get("url");
            if(matterType.equals(WeixinCoreUtil.MATTER_VIDEO)){
                Map<String,Object> mediaMap  = this.getVideoUrl(MEDIAMATTER_PUBID,mediaId);
                if(mediaMap!=null){
                    MEDIAMATTER_MEDIAURL = (String)mediaMap.get("down_url");
                }
                
            }
            mediaMatter.put("MEDIAMATTER_MEDIAURL",MEDIAMATTER_MEDIAURL);
            mediaMatter = dao.saveOrUpdate("PLAT_WEIXIN_MEDIAMATTER",
                    mediaMatter,AllConstants.IDGENERATOR_UUID,null);
            String MEDIAMATTER_ID = (String) mediaMatter.get("MEDIAMATTER_ID");
            fileAttachService.saveFileAttachs(FILES_JSON,"PLAT_WEIXIN_MEDIAMATTER",MEDIAMATTER_ID,null);
            result.put("success", true);
        }else{
            result.put("msg", result.get("errmsg"));
            result.put("success", false);
        }
        return result;
    }
   
    /**
     * 描述
     * @author 李俊
     * @created 2018年2月2日 下午4:27:06
     * @param mEDIAMATTER_PUBID
     * @param mediaId
     * @return
     */
    public Map<String, Object> getVideoUrl(String publicId,
            String mediaId) {
        String token = publicService.getTokenByPublicId(publicId);
        String url = "https://api.weixin.qq.com/cgi-bin/material/get_material?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("media_id", mediaId);
        String postBody = JSON.toJSONString(params);
        String result = PlatHttpUtil.postParams(url, postBody);
        Map data = JSON.parseObject(result,Map.class);
        return data;
    }

    /**
     * 保存其它类型素材
     * @param mediaMatter
     * @param mediaType
     * @return
     */
    public Map<String,Object> saveOtherMatter(Map<String,Object> mediaMatter,String mediaType){
        String FILES_JSON = (String) mediaMatter.get("FILES_JSON");
        if(StringUtils.isNotEmpty(FILES_JSON)){
            return this.saveOrUpdateMatter(mediaMatter, 
                    FILES_JSON,mediaType);
        }else{
            HashMap<String,Object> result = new HashMap<String,Object>();
            result.put("success", false);
            result.put("msg", "缺失素材文件!");
            return result;
        }
    }
    
    /**
     * 删除素材信息
     * @param token
     * @param mediaId
     * @return
     */
    public boolean deleteMatter(String token,String mediaId){
        String url = "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("media_id", mediaId);
        String result = PlatHttpUtil.postParams(url, JSON.toJSONString(params));
        Map obj = JSON.parseObject(result,Map.class);
        int errcode = Integer.parseInt(obj.get("errcode").toString());
        if(errcode==0){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * 删除素材信息
     * @param matterIds
     */
    public void deleteMatters(String[] matterIds){
        for(String matterId:matterIds){
            Map<String,Object> matter = this.getRecord("PLAT_WEIXIN_MEDIAMATTER",
                    new String[]{"MEDIAMATTER_ID"},new Object[]{matterId});
            String MEDIAMATTER_PUBID= (String) matter.get("MEDIAMATTER_PUBID");
            String token = publicService.getTokenByPublicId(MEDIAMATTER_PUBID);
            String mediaId = (String) matter.get("MEDIAMATTER_MEDIAID");
            boolean result = this.deleteMatter(token, mediaId);
            if(result){
                this.deleteRecords("PLAT_WEIXIN_MEDIAMATTER","MEDIAMATTER_ID",new String[]{matterId});
            }
        }
    }
    
    /**
     * 获取微信服务器的素材列表
     * @param sourceId
     * @param matterType
     * @return
     */
    public List<Map> findWeixinMatterList(String sourceId,String matterType){
        String token = publicService.getToken(sourceId);
        String url = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("type", matterType);
        params.put("offset", 0);
        params.put("count",20);
        String postBody = JSON.toJSONString(params);
        String result = PlatHttpUtil.postParams(url, postBody);
        Map data = JSON.parseObject(result,Map.class);
        List<Map> item = (List<Map>) data.get("item");
        return item;
    }
    
    /**
     * 获取可选的素材类型列表
     * @param param
     * @return
     */
    public List<Map<String,Object>> findMediaTypeList(String param){
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        Map<String,Object> m1 = new HashMap<String,Object>();
        m1.put("VALUE", WeixinCoreUtil.MATTER_IMG);
        m1.put("LABEL", "图片");
        Map<String,Object> m2 = new HashMap<String,Object>();
        m2.put("VALUE", WeixinCoreUtil.MATTER_VOICE);
        m2.put("LABEL", "声音");
        Map<String,Object> m3 = new HashMap<String,Object>();
        m3.put("VALUE", WeixinCoreUtil.MATTER_VIDEO);
        m3.put("LABEL", "视频");
        list.add(m1);
        list.add(m2);
        list.add(m3);
        return list;
    }
    
    /**
     * 获取素材列表
     * @param matterType
     * @return
     */
    public List<Map<String,Object>> findMatterList(String typeAndPublicId){
        if(StringUtils.isNotEmpty(typeAndPublicId)){
            StringBuffer sql  = new StringBuffer("SELECT T.MEDIAMATTER_MEDIAID AS VALUE,");
            sql.append("T.MEDIAMATTER_NAME AS LABEL FROM PLAT_WEIXIN_MEDIAMATTER T");
            sql.append(" WHERE T.MEDIAMATTER_TYPE=? AND T.MEDIAMATTER_PUBID=? ");
            sql.append(" ORDER BY T.MEDIAMATTER_TIME DESC");
            return dao.findBySql(sql.toString(), 
                    new Object[]{typeAndPublicId.split(",")[0],typeAndPublicId.split(",")[1]}, null);
        }else{
            return null;
        }
    }
    
    /**
     * 发送媒体素材给用户
     * @param openId
     * @param sourceId
     */
    public String sendMsgToUsers(String[] openIds,String sourceId,
            String mediaId,String mediaType){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("touser", openIds);
        map.put("msgtype",mediaType);
        if(mediaType.equals(MatterMsgService.TYPE_VOICE)){
            Map<String,Object> voice = new HashMap<String,Object>();
            voice.put("media_id",mediaId);
            map.put("voice", voice);
        }else if(mediaType.equals(MatterMsgService.TYPE_IMAGE)){
            Map<String,Object> image = new HashMap<String,Object>();
            image.put("media_id",mediaId);
            map.put("image", image);
        }
        String body = JSON.toJSONString(map);
        String access_token = publicService.getToken(sourceId);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }
    
    /**
     * 发送消息给用户
     * @param tagId
     * @param publicId
     * @param mediaId
     * @param mediaType
     * @return
     */
    public String sendMsgToUsers(String tagId,String publicId,
            String mediaId,String mediaType){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msgtype",mediaType);
        Map<String,Object> filter = new HashMap<String,Object>();
        filter.put("is_to_all", true);
        filter.put("tag_id",Integer.parseInt(tagId));
        map.put("filter", filter);
        if(mediaType.equals(MatterMsgService.TYPE_VOICE)){
            Map<String,Object> voice = new HashMap<String,Object>();
            voice.put("media_id",mediaId);
            map.put("voice", voice);
        }else if(mediaType.equals(MatterMsgService.TYPE_IMAGE)){
            Map<String,Object> image = new HashMap<String,Object>();
            image.put("media_id",mediaId);
            map.put("image", image);
        }
        String body = JSON.toJSONString(map);
        String access_token = publicService.getTokenByPublicId(publicId);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }
    
    /**
     * 发送视频消息给用户
     * @param openIds
     * @param MEDIAMATTER_ID
     * @return
     */
    public String sendVideoMsgToUsers(String[] openIds,String MEDIAMATTER_ID){
        Map<String,Object> videoMedia = this.getRecord("PLAT_WEIXIN_MEDIAMATTER",
                new String[]{"MEDIAMATTER_ID"},new Object[]{MEDIAMATTER_ID});
        String MEDIAMATTER_MEDIAID = (String) videoMedia.get("MEDIAMATTER_MEDIAID");
        String MEDIAMATTER_NAME = (String) videoMedia.get("MEDIAMATTER_NAME");
        String MEDIAMATTER_DESC = (String) videoMedia.get("MEDIAMATTER_DESC");
        String MEDIAMATTER_PUBID= (String) videoMedia.get("MEDIAMATTER_PUBID");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msgtype","mpvideo");
        map.put("touser", openIds);
        Map<String,Object> mpvideo = new HashMap<String,Object>();
        mpvideo.put("media_id",MEDIAMATTER_MEDIAID);
        mpvideo.put("title",MEDIAMATTER_NAME);
        mpvideo.put("description",MEDIAMATTER_DESC);
        map.put("mpvideo", mpvideo);
        String body = JSON.toJSONString(map);
        String access_token = publicService.getTokenByPublicId(MEDIAMATTER_PUBID);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }
    
    /**
     * 发送视频消息给用户
     * @param tagId
     * @param MEDIAMATTER_ID
     * @return
     */
    public String sendVideoMsgToUsers(String tagId,String MEDIAMATTER_ID){
        Map<String,Object> videoMedia = this.getRecord("PLAT_WEIXIN_MEDIAMATTER",
                new String[]{"MEDIAMATTER_ID"},new Object[]{MEDIAMATTER_ID});
        String MEDIAMATTER_MEDIAID = (String) videoMedia.get("MEDIAMATTER_MEDIAID");
        String MEDIAMATTER_NAME = (String) videoMedia.get("MEDIAMATTER_NAME");
        String MEDIAMATTER_DESC = (String) videoMedia.get("MEDIAMATTER_DESC");
        String MEDIAMATTER_PUBID= (String) videoMedia.get("MEDIAMATTER_PUBID");
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("msgtype","mpvideo");
        Map<String,Object> filter = new HashMap<String,Object>();
        filter.put("is_to_all", true);
        filter.put("tag_id",Integer.parseInt(tagId));
        map.put("filter", filter);
        Map<String,Object> mpvideo = new HashMap<String,Object>();
        mpvideo.put("media_id",MEDIAMATTER_MEDIAID);
        mpvideo.put("title",MEDIAMATTER_NAME);
        mpvideo.put("description",MEDIAMATTER_DESC);
        map.put("mpvideo", mpvideo);
        String body = JSON.toJSONString(map);
        String access_token = publicService.getTokenByPublicId(MEDIAMATTER_PUBID);
        String httpUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token="+access_token;
        String result = PlatHttpUtil.postParams(httpUrl, body);
        return result;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2018年2月1日 下午3:56:38
     * @param type
     * @param PublicId
     * @return
     */
    @Override
    public List<Map<String, Object>> findMatterListByType(String type,
            String publicId) {
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT T.*,F.FILE_PATH,F.FILE_TYPE FROM PLAT_WEIXIN_MEDIAMATTER T  ");
        sql.append(" LEFT JOIN PLAT_SYSTEM_FILEATTACH F ON F.FILE_BUSRECORDID=T.MEDIAMATTER_ID ");
        sql.append(" WHERE  F.FILE_BUSTABLELNAME='PLAT_WEIXIN_MEDIAMATTER' ");
        sql.append(" AND T.MEDIAMATTER_TYPE=? AND T.MEDIAMATTER_PUBID=? ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), 
                new Object[]{type,publicId}, null);
        return list;
    }
    
  
}
