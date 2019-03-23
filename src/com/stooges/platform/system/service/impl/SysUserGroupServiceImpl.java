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
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.dao.SysUserGroupDao;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.SysUserGroupService;

/**
 * 描述 用户组管理业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2018-05-10 22:05:56
 */
@Service("sysUserGroupService")
public class SysUserGroupServiceImpl extends BaseServiceImpl implements SysUserGroupService {

    /**
     * 所引入的dao
     */
    @Resource
    private SysUserGroupDao dao;
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
	 * 
	 */
	@Override
	public List findSelectList(String companyId) {
		StringBuffer sql = new StringBuffer("SELECT T.USERGROUP_ID AS VALUE,T.USERGROUP_NAME AS LABEL ");
        sql.append(" FROM PLAT_SYSTEM_USERGROUP T");
        sql.append(" WHERE T.USERGROUP_COMPANYID=? ");
        sql.append(" ORDER BY T.USERGROUP_CREATETIME asc ");
        return dao.findBySql(sql.toString(), new Object[]{companyId}, null);
	}

	/**
	 * 
	 */
	@Override
	public void saveUsers(String usergroup_id, String checkUserIds) {
		StringBuffer clearSql = new StringBuffer("");
		clearSql.append(" update PLAT_SYSTEM_SYSUSER set SYSUSER_GROUPID='' where SYSUSER_GROUPID=?  ");
		dao.executeSql(clearSql.toString(), new Object[]{usergroup_id});
		StringBuffer sql = new StringBuffer("");
		sql.append("update PLAT_SYSTEM_SYSUSER set SYSUSER_GROUPID=? ");
		sql.append(" WHERE SYSUSER_ID IN ").append(
				PlatStringUtil.getValueArray(checkUserIds));
		dao.executeSql(sql.toString(), new Object[]{usergroup_id});
		
	}
	
	/**
     * 分配用户接口
     * @param request
     * @return
     */
    public Map<String,Object> grantUsers(HttpServletRequest request){
        String USERGROUP_ID = request.getParameter("USERGROUP_ID");
        String checkUserIds = request.getParameter("checkUserIds");
        this.saveUsers(USERGROUP_ID, checkUserIds);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
    
    /**
     * 删除用户组信息
     * @param request
     * @return
     */
    public Map<String,Object> deleteGroups(HttpServletRequest request){
        String selectColValues = request.getParameter("selectColValues");
        this.deleteRecords("PLAT_SYSTEM_USERGROUP","USERGROUP_ID",selectColValues.split(","));
        sysLogService.saveBackLog("用户组管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+selectColValues+"]的用户组管理", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
    
    /**
     * 
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdateGroup(HttpServletRequest request){
        Map<String,Object> userGroup = PlatBeanUtil.getMapFromRequest(request);
        String USERGROUP_ID = (String) userGroup.get("USERGROUP_ID");
        if (StringUtils.isEmpty(USERGROUP_ID)) {
            userGroup.put("USERGROUP_CREATETIME", PlatDateTimeUtil.formatDate(new Date(),
                    "yyyy-MM-dd HH:mm:ss"));
        }
        userGroup = this.saveOrUpdate("PLAT_SYSTEM_USERGROUP",
                userGroup,AllConstants.IDGENERATOR_UUID,null);
        if(StringUtils.isNotEmpty(USERGROUP_ID)){
            sysLogService.saveBackLog("系统用户组管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+USERGROUP_ID+"]用户组管理", request);
        }else{
            USERGROUP_ID = (String) userGroup.get("USERGROUP_ID");
            sysLogService.saveBackLog("系统用户组管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+USERGROUP_ID+"]用户组管理", request);
        }
        userGroup.put("success", true);
        return userGroup;
    }
  
}
