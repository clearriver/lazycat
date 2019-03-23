/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.dao.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.core.util.PlatCollectionUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.system.dao.SysUserDao;

/**
 * 描述系统用户业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-10 17:03:31
 */
@Repository
public class SysUserDaoImpl extends BaseDaoImpl implements SysUserDao {

    /**
     * 根据角色ID获取用户IDS
     * @param roleId
     * @return
     */
    public List<String> findUserIds(String roleId){
        StringBuffer sql = new StringBuffer("SELECT T.SYSUSER_ID ");
        sql.append("FROM PLAT_SYSTEM_SYSUSERROLE T");
        sql.append(" WHERE T.ROLE_ID=? ORDER BY T.SYSUSER_ID ASC");
        List<String> list = this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{roleId},String.class);
        return list;
    }
    
    /**
     * 获取被授权的资源ID集合
     * @param userId
     * @return
     */
    public List<String> findGrantRightIds(String userId){
        Map<String,Object> sysUser = this.getRecord("PLAT_SYSTEM_SYSUSER",
                new String[]{"SYSUSER_ID"}, new Object[]{userId});
        StringBuffer sql = new StringBuffer("SELECT R.RE_RECORDID ");
        sql.append("FROM PLAT_SYSTEM_ROLERIGHT R WHERE R.ROLE_ID");
        sql.append(" IN (SELECT UR.ROLE_ID FROM PLAT_SYSTEM_SYSUSERROLE UR");
        sql.append(" WHERE UR.SYSUSER_ID=? ) AND R.ROLE_TABLE=? ");
        List<String> ids = new ArrayList<String>();
        List<String> roleRecordIds = this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{userId,"PLAT_SYSTEM_ROLE"},
                String.class);
        ids.addAll(roleRecordIds);
        String SYSUSER_GROUPID = (String) sysUser.get("SYSUSER_GROUPID");
        if(StringUtils.isNotEmpty(SYSUSER_GROUPID)){
            sql = new StringBuffer("SELECT R.RE_RECORDID ");
            sql.append("FROM PLAT_SYSTEM_ROLERIGHT R WHERE R.ROLE_ID=? ");
            sql.append(" AND R.ROLE_TABLE=? ");
            List<String> groupRecordIds = this.getJdbcTemplate().queryForList(sql.toString(),
                    new Object[]{SYSUSER_GROUPID,"PLAT_SYSTEM_USERGROUP"},
                    String.class);
            ids.addAll(groupRecordIds);
        }
        sql=  new StringBuffer("SELECT R.RE_RECORDID ");
        sql.append("FROM PLAT_SYSTEM_ROLERIGHT R WHERE R.ROLE_ID=? ");
        sql.append(" AND R.ROLE_TABLE=? ");
        List<String> userRecordIds = this.getJdbcTemplate().queryForList(sql.toString(),
                new Object[]{userId,"PLAT_SYSTEM_SYSUSER"},
                String.class);
        ids.addAll(userRecordIds);
        return ids;
    }
    
    /**
     * 判断是否存在用户
     * @param userId
     * @return
     */
    public boolean isExistsUser(String userId){
        StringBuffer sql = new StringBuffer("SELECT COUNT(*) FROM ");
        sql.append("PLAT_SYSTEM_SYSUSER U WHERE U.SYSUSER_ID=? ");
        int count = this.getIntBySql(sql.toString(), new Object[]{userId});
        if(count==0){
            return false;
        }else{
            return true;
        }
    }
    
    /**
     * 根据单位ID和角色ID获取用户IDS
     * @param companyId
     * @param roleIds
     * @return
     */
    public List<String> findUserIds(String companyId,String roleIds){
        StringBuffer sql = new StringBuffer("select T.SYSUSER_ID from ");
        sql.append("PLAT_SYSTEM_SYSUSER T WHERE T.SYSUSER_ID ");
        sql.append("IN (SELECT SR.SYSUSER_ID FROM PLAT_SYSTEM_SYSUSERROLE SR ");
        sql.append("WHERE SR.ROLE_ID IN ").append(PlatStringUtil.getSqlInCondition(roleIds));
        sql.append(") AND T.SYSUSER_COMPANYID=? ");
        return this.getJdbcTemplate().queryForList(sql.toString(), 
                String.class, new Object[]{companyId});
    }
    /**
     * 
     */
	@Override
	public void updateSn(String[] userIds) {
		int[] oldSns = new int[userIds.length];
        StringBuffer sql = new StringBuffer("select SYSUSER_SN FROM PLAT_SYSTEM_SYSUSER ").append(" WHERE SYSUSER_ID=? ");
        for (int i = 0; i < userIds.length; i++) {
            int dicSn = this.getIntBySql(sql.toString(),new Object[] { userIds[i] });
            oldSns[i] = dicSn;
        }
        int[] newSns = PlatCollectionUtil.sortByDesc(oldSns);
        StringBuffer updateSql = new StringBuffer("update PLAT_SYSTEM_SYSUSER ")
                .append(" SET SYSUSER_SN=? WHERE SYSUSER_ID=? ");
        for (int i = 0; i < userIds.length; i++) {
            getJdbcTemplate().update(updateSql.toString(), new Object[] { newSns[i], userIds[i] });
        }
		
	}
	
	/**
     * 根据用户组ID获取用户ID列表
     * @param USERGROUP_ID
     * @return
     */
    public List<String> findGroupUserIds(String USERGROUP_ID){
        StringBuffer sql = new StringBuffer("select T.SYSUSER_ID");
        sql.append(" FROM PLAT_SYSTEM_SYSUSER T WHERE T.SYSUSER_GROUPID=? ");
        return this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{USERGROUP_ID},
                String.class);
    }

	/**
	 * 根据部门ID获取获取ID列表
	 */
	@Override
	public List<String> findDepartUserIds(String DEPART_ID) {
		StringBuffer sql = new StringBuffer("select T.SYSUSER_ID");
        sql.append(" FROM PLAT_SYSTEM_SYSUSER T WHERE T.SYSUSER_DEPARTID=? ");
        return this.getJdbcTemplate().queryForList(sql.toString(), new Object[]{DEPART_ID},
                String.class);
	}
}
