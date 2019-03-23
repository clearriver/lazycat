/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.dao.PositionDao;
import com.stooges.platform.system.service.PositionService;
import com.stooges.platform.system.service.SysLogService;

/**
 * 描述 岗位业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-05-12 15:52:01
 */
@Service("positionService")
public class PositionServiceImpl extends BaseServiceImpl implements PositionService {

    /**
     * 所引入的dao
     */
    @Resource
    private PositionDao dao;
    /**
     * 
     */
    @Resource
    private SysLogService sysLogService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 保存用户岗位中间表
     * @param positionId
     * @param userIds
     */
    public void saveUsers(String positionId,String[] userIds){
        dao.deleteRecords("PLAT_SYSTEM_SYSUSERPOS","POSITION_ID", new String[]{positionId});
        StringBuffer sql = new StringBuffer("insert into PLAT_SYSTEM_SYSUSERPOS");
        sql.append("(SYSUSER_ID,POSITION_ID) values(?,?) ");
        for(String userId:userIds){
            dao.executeSql(sql.toString(), new Object[]{userId,positionId});
        }
    }
    
    /**
     * 获取已经选择的服务记录
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findSelected(SqlFilter filter){
        String selectedRoleIds = filter.getRequest().getParameter("selectedRecordIds");
        StringBuffer sql = new StringBuffer("select T.POSITION_ID,T.POSITION_NAME");
        sql.append(" from PLAT_SYSTEM_POSITION T ");
        if(StringUtils.isNotEmpty(selectedRoleIds)){
            sql.append(" WHERE T.POSITION_ID IN ").append(PlatStringUtil.getValueArray(selectedRoleIds));
            sql.append(" ORDER BY T.POSITION_TIME DESC ");
            return this.findBySql(sql.toString(), null, null);
        }else{
            return null;
        }
    }
    
    /**
     * 获取用户所在岗位信息
     * @param userId
     * @return
     */
    public Map<String,String> getUserPositionInfo(String userId){
        StringBuffer sql = new StringBuffer("select T.POSITION_ID,T.POSITION_NAME");
        sql.append(" from PLAT_SYSTEM_POSITION T WHERE T.POSITION_ID IN");
        sql.append(" (SELECT D.POSITION_ID FROM PLAT_SYSTEM_SYSUSERPOS D WHERE D.SYSUSER_ID=? )");
        sql.append(" ORDER BY T.POSITION_TIME DESC");
        List<Map<String,Object>> list = this.findBySql(sql.toString(), new Object[]{userId}, null);
        Map<String,String> info = new HashMap<String,String>();
        StringBuffer ids = new StringBuffer("");
        StringBuffer names = new StringBuffer("");
        for(int i=0;i<list.size();i++){
            if(i>0){
                ids.append(",");
                names.append(",");
            }
            Map<String,Object> pos = list.get(i);
            ids.append(pos.get("POSITION_ID"));
            names.append(pos.get("POSITION_NAME"));
        }
        info.put("ids", ids.toString());
        info.put("names", names.toString());
        return info;
    }
    
    /**
     * 分配用户接口
     * @param request
     * @return
     */
    public Map<String,Object> grantUsers(HttpServletRequest request){
        String POSITION_ID = request.getParameter("POSITION_ID");
        String checkUserIds = request.getParameter("checkUserIds");
        this.saveUsers(POSITION_ID, checkUserIds.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
    
    /**
     * 删除数据
     * @param request
     * @return
     */
    public Map<String,Object> deletePos(HttpServletRequest request){
        String selectColValues = request.getParameter("selectColValues");
        this.deleteRecords("PLAT_SYSTEM_POSITION","POSITION_ID",selectColValues.split(","));
        sysLogService.saveBackLog("岗位管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的岗位", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
  
    /**
     * 保存数据
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdatePos(HttpServletRequest request){
        Map<String,Object> position = PlatBeanUtil.getMapFromRequest(request);
        String POSITION_ID = (String) position.get("POSITION_ID");
        if(StringUtils.isEmpty(POSITION_ID)){
            position.put("POSITION_TIME",PlatDateTimeUtil.formatDate(new Date()
            , "yyyy-MM-dd HH:mm:ss"));
        }
        position = this.saveOrUpdate("PLAT_SYSTEM_POSITION",
                position,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(POSITION_ID)){
            sysLogService.saveBackLog("岗位管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+POSITION_ID+"]岗位", request);
        }else{
            POSITION_ID = (String) position.get("POSITION_ID");
            sysLogService.saveBackLog("岗位管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+POSITION_ID+"]岗位", request);
        }
        position.put("success", true);
        return position;
    }
}
