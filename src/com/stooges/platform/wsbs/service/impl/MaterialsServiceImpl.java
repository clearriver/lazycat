/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.wsbs.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.wsbs.dao.MaterialsDao;
import com.stooges.platform.wsbs.service.MaterialsService;

/**
 * 描述 事项申报材料列表业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2017-05-18 11:02:38
 */
@Service("materialsService")
public class MaterialsServiceImpl extends BaseServiceImpl implements MaterialsService {

    /**
     * 所引入的dao
     */
    @Resource
    private MaterialsDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 获取事项需要上传材料列表
     */
    @Override
    public List<Map<String, Object>> findFilesList(Map<String,Object> map) {
        List<Map<String, Object>> list =  new ArrayList<Map<String, Object>>();
        String SERITEM_ID = (String)map.get("SERITEM_ID");
        String EXECUTION_ID = (String)map.get("EXECUTION_ID");
        if(StringUtils.isNotEmpty(SERITEM_ID)){
            StringBuffer sql =  new StringBuffer("");
            sql.append("SELECT T.SERMATER_ID,T.SERMATER_ISNEED,T.SERMATER_CODE,'2' as FILE_UPLOADTYPE,");
            sql.append("T.SERMATER_NAME,T.SERMATER_TYPE,T.SERMATER_SIZE,T.SERMATER_TYPENAME,T.SERMATER_DESC ");
            sql.append(" FROM PLAT_WSBS_SERMATER T  ");
            sql.append(" WHERE T.SERITEM_ID=? ");
            sql.append(" ORDER BY T.SERMATER_TYPESORT ASC,T.SERMATER_SN ASC  "); 
            list = dao.findBySql(sql.toString(), new Object[]{SERITEM_ID}, null);
        } 
        if(list!=null&&list.size()>0&&StringUtils.isNotEmpty(EXECUTION_ID)){
            list = setUploadFiles(list,EXECUTION_ID);
        }
        if(list!=null&&list.size()>0){
            list = this.setUploadFileMaterType(list);
        }
        return list;
    }
    /**
     * 获取上传的数据
     * @param list
     * @param eXECUTION_ID
     * @return
     */
    public List<Map<String, Object>> setUploadFiles(
            List<Map<String, Object>> list, String execution_id) {
        List<Map<String,Object>> newApplyMater = new ArrayList<Map<String,Object>>();
        for (int i = 0; i < list.size(); i++) {
            String SERMATER_CODE = (String)list.get(i).get("SERMATER_CODE");
            StringBuffer sql = new StringBuffer("");
            sql.append(" SELECT T.*, T.ROWID FROM PLAT_SYSTEM_FILEATTACH T  ");
            sql.append(" WHERE T.FILE_BUSRECORDID = ? AND T.SERMATER_CODE = ? ");
            sql.append(" ORDER BY T.FILE_CREATETIME ASC ");
            List<Map<String,Object>> uploadFiles = dao.findBySql(sql.toString(),
                    new Object[]{execution_id,SERMATER_CODE}, null);
            list.get(i).put("uploadFiles", uploadFiles);
            String FILE_UPLOADTYPE = "";
            //定义审核状态
            for(Map<String,Object> uploadFile:uploadFiles){
                //获取收取方式
                FILE_UPLOADTYPE = (String) uploadFile.get("FILE_UPLOADTYPE");
                list.get(i).put("FILE_UPLOADTYPE", FILE_UPLOADTYPE);
            }
            newApplyMater.add(list.get(i));
        }
        return newApplyMater;
    }

    /**
     * 
     * @param applyMaters
     * @return
     */
    public List<Map<String, Object>> setUploadFileMaterType(
            List<Map<String, Object>> applyMaters){
        List<Map<String,Object>> newApplyMater = new ArrayList<Map<String,Object>>();
        List<String> typeNames = new ArrayList<String>();
        if(applyMaters!=null){
            for (int i = 0; i < applyMaters.size(); i++) {
                String MATER_TYPENAME = (String)applyMaters.get(i).get("SERMATER_TYPENAME");
                if(StringUtils.isEmpty(MATER_TYPENAME)){
                    MATER_TYPENAME = "未分类";
                }
                if(!typeNames.contains(MATER_TYPENAME)){
                    typeNames.add(MATER_TYPENAME);
                }
            }
            for(int j = 0;j<typeNames.size();j++){
                int m = 0 ;
                for(int i = 0; i < applyMaters.size(); i++){
                    String typename = typeNames.get(j);
                    String MATER_TYPENAME = (String)applyMaters.get(i).get("SERMATER_TYPENAME");
                    if(StringUtils.isEmpty(MATER_TYPENAME)){
                        MATER_TYPENAME = "未分类";
                    }
                    if(MATER_TYPENAME.equals(typename)){
                        Map<String,Object> map = applyMaters.get(i);
                        if(m==0){
                            map.put("MATER_TYPENAME_TOP", typename);
                        }
                        m++;
                        map.put("MATER_INDEX", m);
                        newApplyMater.add(map);
                    }
                }
                m = 0;
            }
        }else{
            newApplyMater = applyMaters;
        }
        return newApplyMater;
    }
  
}
