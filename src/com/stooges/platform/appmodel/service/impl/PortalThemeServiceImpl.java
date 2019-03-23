/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.dao.PortalThemeDao;
import com.stooges.platform.appmodel.service.PortalRowService;
import com.stooges.platform.appmodel.service.PortalRowconfService;
import com.stooges.platform.appmodel.service.PortalThemeService;

/**
 * 描述 门户主题业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 10:20:49
 */
@Service("portalThemeService")
public class PortalThemeServiceImpl extends BaseServiceImpl implements PortalThemeService {

    /**
     * 所引入的dao
     */
    @Resource
    private PortalThemeDao dao;
    /**
     * 
     */
    @Resource
    private PortalRowService portalRowService;
    /**
     * 
     */
    @Resource
    private PortalRowconfService portalRowconfService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 克隆主题的数据
     * @param sourceThemeId
     * @return
     */
    public Map<String,Object> cloneTheme(String sourceThemeId,String newThemeName){
        Map<String,Object> sourceTheme = dao.getRecord("PLAT_APPMODEL_PORTALTHEME",
                new String[]{"THEME_ID"},new Object[]{sourceThemeId});
        String IS_DEFAULT = (String) sourceTheme.get("IS_DEFAULT");
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        String SYSUSER_ID = (String) backLoginUser.get("SYSUSER_ID");
        String SYSUSER_NAME = (String) backLoginUser.get("SYSUSER_NAME");
        Map<String,Object> newThemeInfo = sourceTheme;
        newThemeInfo.remove("THEME_ID");
        newThemeInfo.remove("THEME_CREATETIME");
        newThemeInfo.remove("IS_DEFAULT");
        newThemeInfo.put("THEME_NAME", newThemeName);
        newThemeInfo.put("CREATOR_ID", SYSUSER_ID);
        newThemeInfo.put("CREATOR_NAME", SYSUSER_NAME);
        if(IS_DEFAULT.equals("1")){
            newThemeInfo.put("IS_COPYDEFAULT","1");
        }
        newThemeInfo.put("CREATOR_NAME", SYSUSER_NAME);
        newThemeInfo.put("THEME_CREATETIME",PlatDateTimeUtil.
                formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        newThemeInfo.put("IS_DEFAULT", "-1");
        newThemeInfo =  dao.saveOrUpdate("PLAT_APPMODEL_PORTALTHEME", 
                newThemeInfo,AllConstants.IDGENERATOR_UUID,null);
        String newThemeId = (String) newThemeInfo.get("THEME_ID");
        this.portalRowService.copyRows(sourceThemeId, newThemeId);
        return newThemeInfo;
    }
    
    /**
     * 更新主题的名称和行JSON
     * @param themeId
     * @param themeName
     * @param rowJson
     */
    public void updateThemeNameAndRowOrder(String themeId,String themeName,
            String rowJson,String isAdminDesign){
        Map<String,Object> themeInfo = dao.getRecord("PLAT_APPMODEL_PORTALTHEME",
                new String[]{"THEME_ID"},new Object[]{themeId});
        themeInfo.put("THEME_NAME", themeName);
        dao.saveOrUpdate("PLAT_APPMODEL_PORTALTHEME",themeInfo,
                AllConstants.IDGENERATOR_UUID,null);
        List<Map> rowList = JSON.parseArray(rowJson, Map.class);
        for(int i=0;i<rowList.size();i++){
            int sn = i+1;
            Map<String,Object> row = rowList.get(i);
            String ROW_ID = (String) row.get("ROW_ID");
            String CONF_IDS = (String) row.get("CONF_IDS");
            String[] confIdArray = CONF_IDS.split(",");
            for(int j=0;j<confIdArray.length;j++){
                int confSn = j+1;
                portalRowconfService.updateCompConfSn(confIdArray[j],confSn,ROW_ID);
            }
            portalRowService.updateSn(ROW_ID, sn);
        }
    }
    
    /**
     * 根据创建人获取主题列表数据
     * @param creatorId
     * @return
     */
    public List<Map<String,Object>> createAndfindByCreatorId(String creatorId){
        StringBuffer sql = new StringBuffer("SELECT * FROM PLAT_APPMODEL_PORTALTHEME");
        sql.append(" T WHERE T.CREATOR_ID=? AND T.IS_DEFAULT=?  ");
        sql.append(" ORDER BY T.THEME_CREATETIME DESC ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), 
                new Object[]{creatorId,"-1"}, null);
        if(list==null||list.size()==0){
            list = new ArrayList<Map<String,Object>>();
            //获取缺省主题
            Map<String,Object> defaultTheme = dao.getRecord("PLAT_APPMODEL_PORTALTHEME",
                    new String[]{"IS_DEFAULT"},new Object[]{"1"});
            String sourceThemeId= (String) defaultTheme.get("THEME_ID");
            String newThemeName = (String) defaultTheme.get("THEME_NAME");
            //克隆主题
            Map<String,Object> copyTheme = this.cloneTheme(sourceThemeId, newThemeName);
            list.add(copyTheme);
        }
        return list;
    }
    
    /**
     * 删除主题数据
     * @param themeIds
     */
    public void deleteCascade(String themeIds){
        StringBuffer sql = new StringBuffer("DELETE FROM PLAT_APPMODEL_PORTALROWCONF");
        sql.append("  WHERE CONF_ROWID IN (SELECT R.ROW_ID ");
        sql.append("FROM PLAT_APPMODEL_PORTALROW R WHERE R.ROW_THEMEID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(themeIds));
        sql.append(" ) ");
        dao.executeSql(sql.toString(),null);
        sql = new StringBuffer("DELETE FROM PLAT_APPMODEL_PORTALROW ");
        sql.append(" WHERE ROW_THEMEID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(themeIds));
        dao.executeSql(sql.toString(),null);
        sql = new StringBuffer("DELETE FROM PLAT_APPMODEL_PORTALTHEME ");
        sql.append(" WHERE THEME_ID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(themeIds));
        dao.executeSql(sql.toString(),null);
    }
    
    /**
     * 
     * @param themeId
     * @param themeName
     * @param rowJson
     */
    public void cloneForNewTheme(String themeId,String themeName,String rowJson){
        
    }
  
}
