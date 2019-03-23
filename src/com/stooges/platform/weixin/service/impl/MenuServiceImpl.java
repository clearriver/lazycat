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

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.weixin.dao.MenuDao;
import com.stooges.platform.weixin.service.MenuGroupService;
import com.stooges.platform.weixin.service.MenuService;
import com.stooges.platform.weixin.service.PublicService;
import com.stooges.platform.weixin.util.Menu;

/**
 * 描述 菜单业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-21 10:04:25
 */
@Service("menuService")
public class MenuServiceImpl extends BaseServiceImpl implements MenuService {

    /**
     * 所引入的dao
     */
    @Resource
    private MenuDao dao;
    /**
     * 
     */
    @Resource
    private PublicService publicService;
    /**
     * 
     */
    @Resource
    private MenuGroupService menuGroupService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据原始ID删除菜单
     * @param sourceId
     * @return
     */
    public String deleteMenus(String sourceId){
        String token = publicService.getToken(sourceId);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        String result = PlatHttpUtil.httpsRequest(url, "GET", null);
        return result;
    }
    
    /**
     * 根据菜单组获取数量
     * @param groupId
     * @return
     */
    public int getCountByGroupId(String groupId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_WEIXIN_MENU T WHERE T.MENUGROUP_ID=? ");
        return dao.getIntBySql(sql.toString(), new Object[]{groupId});
    }
    
    /**
     * 根据组ID获取列表
     * @param groupId
     * @return
     */
    public List<Map<String,Object>> findByGroupId(String groupId){
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append("PLAT_WEIXIN_MENU M WHERE M.MENUGROUP_ID=? ");
        sql.append(" ORDER BY M.MENU_SN ASC");
        return dao.findBySql(sql.toString(), new Object[]{groupId}, null);
    }
    
    /**
     * 更新所有菜单到微信
     * @param publicId
     */
    public String updateAllMenuToWeixin(String publicId){
        Map<String,Object> publicInfo = dao.getRecord("PLAT_WEIXIN_PUBLIC",
                new String[]{"PUBLIC_ID"},new Object[]{publicId});
        String sourceId = (String) publicInfo.get("PUBLIC_SOURCEID");
        this.deleteMenus(sourceId);
        List<Map<String,Object>> groupList = menuGroupService.findGroupList(publicId);
        Menu allMenu = new Menu();
        List<Map<String,Object>> topButtonList = new ArrayList<Map<String,Object>>();
        for(Map<String,Object> group:groupList){
            String groupName = (String) group.get("LABEL");
            String groupId = (String) group.get("VALUE");
            List<Map<String,Object>> menuList = this.findByGroupId(groupId);
            Map<String,Object> groupButton = new HashMap<String,Object>();
            groupButton.put("name", groupName);
            List<Map<String,Object>> buttonList = new ArrayList<Map<String,Object>>();
            for(Map<String,Object> menu:menuList){
                String MENU_TYPE = (String) menu.get("MENU_TYPE");
                String MENU_NAME = (String) menu.get("MENU_NAME");
                String MENU_KEY = (String) menu.get("MENU_KEY");
                Map<String,Object> btn = new HashMap<String,Object>();
                btn.put("name", MENU_NAME);
                btn.put("type", MENU_TYPE);
                btn.put("key", MENU_KEY);
                if(MENU_TYPE.equals(Menu.TYPE_VIEW)){
                    String MENU_URL = (String) menu.get("MENU_URL");
                    btn.put("url", MENU_URL);
                }else if(MENU_TYPE.equals(Menu.TYPE_MEDIA)){
                    String MENU_MEDIAID= (String) menu.get("MENU_MEDIAID");
                    btn.put("media_id", MENU_MEDIAID);
                }
                buttonList.add(btn);
            }
            if(buttonList.size()>0){
                groupButton.put("sub_button", buttonList);
                topButtonList.add(groupButton);
            }
        }
        allMenu.setButton(topButtonList);
        String postBody = JSON.toJSONString(allMenu);
        String token = publicService.getToken(sourceId);
        String url = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=ACCESS_TOKEN";
        url = url.replace("ACCESS_TOKEN", token);
        return PlatHttpUtil.postParams(url, postBody);
    }
}
