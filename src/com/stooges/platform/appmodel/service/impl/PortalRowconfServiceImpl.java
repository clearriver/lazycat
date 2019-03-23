/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.dao.PortalRowconfDao;
import com.stooges.platform.appmodel.service.PortalRowconfService;

/**
 * 描述 行组件配置业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 15:47:00
 */
@Service("portalRowconfService")
public class PortalRowconfServiceImpl extends BaseServiceImpl implements PortalRowconfService {

    /**
     * 所引入的dao
     */
    @Resource
    private PortalRowconfDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 初始化行配置信息
     * @param rowId
     * @param portalRow
     */
    public void initRowConfs(String rowId,Map<String,Object> portalRow){
      //获取布局类型
        String LAYOUT_TYPE = (String) portalRow.get("ROW_LAYOUT");
        if(LAYOUT_TYPE.equals("1")){
            for(int i=1;i<=4;i++){
                Map<String,Object> conf = new HashMap<String,Object>();
                conf.put("CONF_ROWID", rowId);
                conf.put("CONF_TITLE", "标题"+i);
                conf.put("CONF_SN", i);
                conf.put("CONF_BORDERCOLOR", PortalRowconfService.BORDERCOLOR_DEFAULT);
                conf.put("CONF_COLNUM", 3);
                this.saveOrUpdate("PLAT_APPMODEL_PORTALROWCONF",conf,
                        AllConstants.IDGENERATOR_UUID,null);
            }
        }else if(LAYOUT_TYPE.equals("2")||LAYOUT_TYPE.equals("3")||LAYOUT_TYPE.equals("5")){
            for(int i=1;i<=2;i++){
                Map<String,Object> conf = new HashMap<String,Object>();
                conf.put("CONF_ROWID", rowId);
                conf.put("CONF_TITLE", "标题"+i);
                conf.put("CONF_SN", i);
                conf.put("CONF_BORDERCOLOR",  PortalRowconfService.BORDERCOLOR_DEFAULT);
                switch(i){
                    case 1:
                        if(LAYOUT_TYPE.equals("2")){
                            conf.put("CONF_COLNUM", 3);
                        }else if(LAYOUT_TYPE.equals("3")){
                            conf.put("CONF_COLNUM", 9);
                        }else if(LAYOUT_TYPE.equals("5")){
                            conf.put("CONF_COLNUM", 6);
                        }
                        break;
                    case 2:
                        if(LAYOUT_TYPE.equals("2")){
                            conf.put("CONF_COLNUM", 9);
                        }else if(LAYOUT_TYPE.equals("3")){
                            conf.put("CONF_COLNUM", 3);
                        }else if(LAYOUT_TYPE.equals("5")){
                            conf.put("CONF_COLNUM", 6);
                        }
                        break;
                    default:
                        break;
                }
                this.saveOrUpdate("PLAT_APPMODEL_PORTALROWCONF",conf,
                        AllConstants.IDGENERATOR_UUID,null);
            }
        }else if(LAYOUT_TYPE.equals("4")){
            for(int i=1;i<=3;i++){
                Map<String,Object> conf = new HashMap<String,Object>();
                conf.put("CONF_ROWID", rowId);
                conf.put("CONF_TITLE", "标题"+i);
                conf.put("CONF_SN", i);
                conf.put("CONF_BORDERCOLOR",  PortalRowconfService.BORDERCOLOR_DEFAULT);
                conf.put("CONF_COLNUM", 4);
                this.saveOrUpdate("PLAT_APPMODEL_PORTALROWCONF",conf,
                        AllConstants.IDGENERATOR_UUID,null);
            }
        }else if(LAYOUT_TYPE.equals("6")){
            Map<String,Object> conf = new HashMap<String,Object>();
            conf.put("CONF_ROWID", rowId);
            conf.put("CONF_TITLE", "标题");
            conf.put("CONF_SN", 1);
            conf.put("CONF_BORDERCOLOR",  PortalRowconfService.BORDERCOLOR_DEFAULT);
            conf.put("CONF_COLNUM", 12);
            this.saveOrUpdate("PLAT_APPMODEL_PORTALROWCONF",conf,
                    AllConstants.IDGENERATOR_UUID,null);
        }
    }
    
    /**
     * 根据行ID获取数据列表
     * @param rowId
     * @return
     */
    public List<Map<String,Object>> findByRowId(String rowId){
        StringBuffer sql = new StringBuffer("select C.*,R.RES_NAME,R.RES_MENUICON ");
        sql.append(",R.RES_CODE,R.RES_MENUURL from PLAT_APPMODEL_PORTALROWCONF C");
        sql.append(" LEFT JOIN PLAT_APPMODEL_PORTALCOMP T ON C.CONF_COMPID=T.COMP_ID");
        sql.append(" LEFT JOIN PLAT_SYSTEM_RES R ON R.RES_ID=T.COMP_MORERESID");
        sql.append(" WHERE C.CONF_ROWID=? ORDER BY C.CONF_SN ASC ");
        return dao.findBySql(sql.toString(), new Object[]{rowId}, null);
    }
    
    /**
     * 获取组件配置数量
     * @param rowId
     * @return
     */
    public int getCompConfCount(String rowId){
        return dao.getCompConfCount(rowId);
    }
    
    /**
     * 获取列数量
     * @param ROW_LAYOUT
     * @return
     */
    private int getColNum(String LAYOUT_TYPE,int confSn){
        if(LAYOUT_TYPE.equals("1")){
            return 3;
        }else if(LAYOUT_TYPE.equals("2")||LAYOUT_TYPE.equals("3")||LAYOUT_TYPE.equals("5")){
            switch(confSn){
                case 1:
                    if(LAYOUT_TYPE.equals("2")){
                        return 3;
                    }else if(LAYOUT_TYPE.equals("3")){
                        return 9;
                    }else if(LAYOUT_TYPE.equals("5")){
                        return 6;
                    }
                    break;
                case 2:
                    if(LAYOUT_TYPE.equals("2")){
                        return 9;
                    }else if(LAYOUT_TYPE.equals("3")){
                        return 3;
                    }else if(LAYOUT_TYPE.equals("5")){
                        return 6;
                    }
                    break;
                default:
                    return 3;
            }
        }else if(LAYOUT_TYPE.equals("4")){
            return 4;
        }else if(LAYOUT_TYPE.equals("6")){
            return 12;
        }
        return 3;
    }
    
    /**
     * 更新组件的排序
     * @param confId
     * @param sn
     */
    public void updateCompConfSn(String confId,int sn,String rowId){
        Map<String,Object> row = dao.getRecord("PLAT_APPMODEL_PORTALROW",new String[]{"ROW_ID"}, 
                new Object[]{rowId});
        String ROW_LAYOUT= (String) row.get("ROW_LAYOUT");
        int CONF_COLNUM = this.getColNum(ROW_LAYOUT, sn);
        StringBuffer sql = new StringBuffer("UPDATE PLAT_APPMODEL_PORTALROWCONF");
        sql.append(" SET CONF_SN=?,CONF_ROWID=?,CONF_COLNUM=? WHERE CONF_ID=? ");
        dao.executeSql(sql.toString(), new Object[]{sn,rowId,CONF_COLNUM,confId});
    }
    
    /**
     * 判断是否存在该配置
     * @param THEME_ID 主题ID
     * @param CONF_ID 现有配置ID
     * @param CONF_COMPID 组件ID
     * @return
     */
    public boolean isExists(String THEME_ID,String CONF_ID,String CONF_COMPID){
        return dao.isExists(THEME_ID, CONF_ID, CONF_COMPID);
    }
    
    /**
     * 更新组件的URL
     * @param compId
     * @param compUrl
     */
    public void updateCompUrl(String compId,String compUrl){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_APPMODEL_PORTALROWCONF");
        sql.append(" SET CONF_COMPURL=? WHERE CONF_COMPID=?");
        dao.executeSql(sql.toString(), new Object[]{compUrl,compId});
    }
    
    /**
     * 更新组件为空
     * @param compIds
     */
    public void updateCompToNull(String compIds){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_APPMODEL_PORTALROWCONF");
        sql.append(" SET CONF_COMPID=null,CONF_COMPTYPECODE=null,CONF_COMPURL=null");
        sql.append(" WHERE CONF_COMPID IN ").append(PlatStringUtil.getValueArray(compIds));
        dao.executeSql(sql.toString(),null);
    }
  
}
