/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.chatonline.service.ChatOnlineService;
import com.stooges.platform.system.dao.SysUserDao;
import com.stooges.platform.system.service.PositionService;
import com.stooges.platform.system.service.ResService;
import com.stooges.platform.system.service.RoleGroupService;
import com.stooges.platform.system.service.RoleRightService;
import com.stooges.platform.system.service.RoleService;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.system.service.SysUserService;

/**
 * 描述 系统用户业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-04-10 17:03:31
 */
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl implements SysUserService {

    /**
     * 所引入的dao
     */
    @Resource
    private SysUserDao dao;
    /**
     * 
     */
    @Resource
    private FileAttachService fileAttachService;
    /**
     * 
     */
    @Resource
    private ResService resService;
    /**
     * 
     */
    @Resource
    private RoleService roleService;
    /**
     * 
     */
    @Resource
    private RoleGroupService roleGroupService;
    /**
     * 
     */
    @Resource
    private RoleRightService roleRightService;
    /**
     * 
     */
    @Resource
    private ChatOnlineService chatOnlineService;
    /**
     * 
     */
    @Resource
    private PositionService positionService;
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
     * 根据用户账号和token获取用户信息
     * @param userAccount
     * @param token
     * @return
     */
    public Map<String,Object> getByAccountAndToken(String userAccount,String token){
        //根据账号获取用户
        Map<String,Object> sysUser = dao.getRecord("PLAT_SYSTEM_SYSUSER",
                new String[]{"SYSUSER_ACCOUNT"},new Object[]{userAccount});
        if(sysUser!=null){
            String SYSUSER_PASSWORD = (String) sysUser.get("SYSUSER_PASSWORD");
            return this.getEncodeAccountAndPass(userAccount, SYSUSER_PASSWORD);
        }else{
            return null;
        }
    }
    
    /**
     * 
     * @param sysUser
     * @return
     */
    private Map<String,Object> setUserAuthRights(Map<String,Object> sysUser){
        int sysUserStatus = Integer.parseInt(sysUser.get("SYSUSER_STATUS").toString());
        if(sysUserStatus==SysUserService.SYSUSER_STATUS_NORMAL){
            String userId = (String) sysUser.get("SYSUSER_ID");
            Set<String> resCodeSet = resService.getUserGrantResCodeSet(userId);
            if(resCodeSet!=null&&resCodeSet.size()>0){
                String rescodes = PlatStringUtil.getSetStringSplit(resCodeSet);
                sysUser.put(SysUserService.RESCODESET_KEY, resCodeSet);
                sysUser.put(SysUserService.RESCODES_KEY,rescodes);
                //获取被授权的URL
                Set<String> grantUrlSet = resService.getUserGrantResUrlSet(userId,rescodes);
                sysUser.put(SysUserService.GRANTURLS_KEY, grantUrlSet);
            }
            //获取被授权的角色编码集合
            Set<String> grantRoleCodeSet = roleService.getGrantRoleCodeSet(userId);
            if(grantRoleCodeSet!=null&grantRoleCodeSet.size()>0){
                sysUser.put(SysUserService.ROLECODESET_KEY, grantRoleCodeSet);
                sysUser.put(SysUserService.ROLECODES_KEY,PlatStringUtil.getSetStringSplit(grantRoleCodeSet));
            }
            //获取被授权的角色组IDS集合
            Set<String> groupIdSet = roleGroupService.getUserGrantGroupIds(userId);
            if(groupIdSet!=null&&groupIdSet.size()>0){
                sysUser.put(SysUserService.GROUPIDSET_KEY, groupIdSet);
                sysUser.put(SysUserService.GROUPIDS_KEY,PlatStringUtil.getSetStringSplit(groupIdSet));
            }
            //获取被授权的流程定义和流程类别集合
            Set<String> flowDefAndTypeSet = roleRightService.getUserGrantRightIds(userId, "JBPM6_FLOWDEF");
            if(flowDefAndTypeSet!=null&&flowDefAndTypeSet.size()>0){
                sysUser.put(SysUserService.FLOWDEFTYPEIDS_KEY,PlatStringUtil.getSetStringSplit(flowDefAndTypeSet));
            }
            if(grantRoleCodeSet.contains("adminrole")){
                sysUser.put(SysUserService.ISADMIN_KEY, true);
            }else{
                sysUser.put(SysUserService.ISADMIN_KEY, false);
            }
            List<Map<String,Object>> resList = resService.findGrantResList(userId,null);
            sysUser.put(SysUserService.GRANTRESLIST_KEY, resList);
        }
        return sysUser;
    }
    
    /**
     * 更新用户拥有的权限JSON字段
     * @param userId
     */
    public void updateUserRightJson(String userId){
        Map<String,Object> rightJsonMap = new HashMap<String,Object>();
        Set<String> resCodeSet = resService.getUserGrantResCodeSet(userId);
        if(resCodeSet!=null&&resCodeSet.size()>0){
            String rescodes = PlatStringUtil.getSetStringSplit(resCodeSet);
            rightJsonMap.put(SysUserService.RESCODES_KEY, rescodes);
            Set<String> grantUrlSet = resService.getUserGrantResUrlSet(userId,rescodes);
            rightJsonMap.put(SysUserService.GRANTURLSTR_KEY,PlatStringUtil.getSetStringSplit(grantUrlSet));
        }
         //获取被授权的角色编码集合
        Set<String> grantRoleCodeSet = roleService.getGrantRoleCodeSet(userId);
        rightJsonMap.put(SysUserService.ROLECODES_KEY,PlatStringUtil.getSetStringSplit(grantRoleCodeSet));
        Set<String> groupIdSet = roleGroupService.getUserGrantGroupIds(userId);
        if(groupIdSet!=null&&groupIdSet.size()>0){
            rightJsonMap.put(SysUserService.GROUPIDS_KEY,PlatStringUtil.getSetStringSplit(groupIdSet));
        }
         //获取被授权的流程定义和流程类别集合
        Set<String> flowDefAndTypeSet = roleRightService.getUserGrantRightIds(userId, "JBPM6_FLOWDEF");
        if(flowDefAndTypeSet!=null&&flowDefAndTypeSet.size()>0){
            rightJsonMap.put(SysUserService.FLOWDEFTYPEIDS_KEY,PlatStringUtil.getSetStringSplit(flowDefAndTypeSet));
        }
        List<Map<String,Object>> resList = resService.findGrantResList(userId,null);
        rightJsonMap.put("GRANTRESJSON",JSON.toJSONString(resList));
        String rightJson = JSON.toJSONString(rightJsonMap);
        StringBuffer sql = new StringBuffer("UPDATE PLAT_SYSTEM_SYSUSER ");
        sql.append(" SET SYSUSER_RIGHTJSON=? WHERE SYSUSER_ID=?");
        dao.executeSql(sql.toString(), new Object[]{rightJson,userId});
    }
    
    /**
     * 
     * @param userAccount
     * @param password
     * @return
     */
    private Map<String,Object> getEncodeAccountAndPass(String userAccount,String password){
        StringBuffer sql = new StringBuffer("SELECT T.*,C.COMPANY_NAME,C.COMPANY_PATH");
        sql.append(",D.DEPART_ID,D.DEPART_NAME FROM PLAT_SYSTEM_SYSUSER");
        sql.append(" T LEFT JOIN PLAT_SYSTEM_COMPANY C ON T.SYSUSER_COMPANYID=C.COMPANY_ID");
        sql.append(" LEFT JOIN PLAT_SYSTEM_DEPART D ON D.DEPART_ID=T.SYSUSER_DEPARTID ");
        sql.append(" WHERE T.SYSUSER_ACCOUNT=? AND T.SYSUSER_PASSWORD=? AND T.SYSUSER_STATUS!=?");
        Map<String,Object> sysUser = dao.getBySql(sql.toString(), 
                new Object[]{userAccount,password,SysUserService.SYSUSER_STATUS_DEL});
        if(sysUser!=null){
            String SYSUSER_RIGHTJSON = (String) sysUser.get("SYSUSER_RIGHTJSON");
            String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
            Map<String,String> posInfo = this.positionService.getUserPositionInfo(SYSUSER_ID);
            sysUser.put("SYSUSER_POSIDS", posInfo.get("ids"));
            sysUser.put("SYSUSER_POSNAMES", posInfo.get("names"));
            if(StringUtils.isNotEmpty(SYSUSER_RIGHTJSON)){
                Map rightJsonMap = JSON.parseObject(SYSUSER_RIGHTJSON,Map.class);
                sysUser.putAll(rightJsonMap);
                String rescodes = (String) sysUser.get(SysUserService.RESCODES_KEY);
                sysUser.put(SysUserService.RESCODESET_KEY,
                        new HashSet<String>(Arrays.asList(rescodes.split(","))));
                String granturls = (String) sysUser.get(SysUserService.GRANTURLSTR_KEY);
                sysUser.put(SysUserService.GRANTURLS_KEY,
                        new HashSet<String>(Arrays.asList(granturls.split(","))));
                String grantrolecodes = (String) sysUser.get(SysUserService.ROLECODES_KEY);
                Set<String> grantRoleCodeSet= new HashSet<String>(Arrays.asList(grantrolecodes.split(",")));
                sysUser.put(SysUserService.ROLECODESET_KEY,grantRoleCodeSet);
                String grantgroupids = (String) sysUser.get(SysUserService.GROUPIDS_KEY);
                if(StringUtils.isNotEmpty(grantgroupids)){
                    sysUser.put(SysUserService.GROUPIDSET_KEY,
                            new HashSet<String>(Arrays.asList(grantgroupids.split(","))));
                }
                if(grantRoleCodeSet.contains("adminrole")){
                    sysUser.put(SysUserService.ISADMIN_KEY, true);
                }else{
                    sysUser.put(SysUserService.ISADMIN_KEY, false);
                }
                String GRANTRESJSON = (String) sysUser.get("GRANTRESJSON");
                sysUser.put(SysUserService.GRANTRESLIST_KEY,JSON.parseArray(GRANTRESJSON, Map.class));
                sysUser.remove("SYSUSER_RIGHTJSON");
            }else{
                sysUser = this.setUserAuthRights(sysUser);
                Map<String,Object> rightJsonMap = new HashMap<String,Object>();
                rightJsonMap.put(SysUserService.RESCODES_KEY,sysUser.get(SysUserService.RESCODES_KEY));
                rightJsonMap.put(SysUserService.ROLECODES_KEY,sysUser.get(SysUserService.ROLECODES_KEY));
                Set<String> granturlSet = (Set<java.lang.String>) sysUser.get(SysUserService.GRANTURLS_KEY);
                if(granturlSet!=null&&granturlSet.size()>0){
                    rightJsonMap.put(SysUserService.GRANTURLSTR_KEY,PlatStringUtil.getSetStringSplit(granturlSet));
                }
                if(sysUser.get(SysUserService.GROUPIDS_KEY)!=null){
                    rightJsonMap.put(SysUserService.GROUPIDS_KEY,sysUser.get(SysUserService.GROUPIDS_KEY));
                }
                if(sysUser.get(SysUserService.FLOWDEFTYPEIDS_KEY)!=null){
                    rightJsonMap.put(SysUserService.FLOWDEFTYPEIDS_KEY,sysUser.get(SysUserService.FLOWDEFTYPEIDS_KEY));
                }
                if(sysUser.get(SysUserService.GRANTRESLIST_KEY)!=null){
                    rightJsonMap.put("GRANTRESJSON",
                            JSON.toJSONString(sysUser.get(SysUserService.GRANTRESLIST_KEY)));
                }
                String json = JSON.toJSONString(rightJsonMap);
                String updateSql = "UPDATE PLAT_SYSTEM_SYSUSER SET SYSUSER_RIGHTJSON=?";
                updateSql+=" WHERE SYSUSER_ID=?";
                dao.executeSql(updateSql, new Object[]{json,SYSUSER_ID});
            }
            return sysUser;
        }else{
            return null;
        }
    }
    
    /**
     * 根据用户账号和密码获取用户信息数据
     * @param userAccount
     * @param pass
     * @return
     */
    public Map<String,Object> checkAccountAndPass(String userAccount,String pass){
        //获取加密后的密码
        /*String password = PlatStringUtil.getSHA256Encode(pass, 
                null, 1);*/
        if(StringUtils.isNotEmpty(userAccount)&&StringUtils.isNotEmpty(pass)){
            return this.getEncodeAccountAndPass(userAccount, pass);
        }else{
            return null;
        }
        
    }
    
    /**
     * 新增或者修改系统用户信息
     * @param sysUser
     * @return
     */
    public Map<String,Object> saveOrUpdateUser(Map<String,Object> sysUser,int idGenerator){
        String oldUserId = (String) sysUser.get("SYSUSER_ID");
        if(StringUtils.isEmpty(oldUserId)){
            String password = PlatStringUtil.getSHA256Encode(SysUserService.DEFAULT_PASSWORD
                    , null, 1);
            sysUser.put("SYSUSER_PASSWORD", password);
            sysUser.put("SYSUSER_STATUS",SysUserService.SYSUSER_STATUS_NORMAL);
            int nextUserSn = this.getNextUserSn((String)sysUser.get("SYSUSER_COMPANYID"));
            sysUser.put("SYSUSER_SN", nextUserSn);
        }
        sysUser = dao.saveOrUpdate("PLAT_SYSTEM_SYSUSER",
                sysUser,idGenerator,null);
        if(StringUtils.isEmpty(oldUserId)){
           //增加即时通讯用户
            this.chatOnlineService.addChatUser(sysUser);
        }
        String sysUserId = (String) sysUser.get("SYSUSER_ID");
        String SYSUSER_ROLEIDS = (String) sysUser.get("SYSUSER_ROLEIDS");
        if(StringUtils.isNotEmpty(SYSUSER_ROLEIDS)){
            this.saveRoles(sysUserId, Arrays.asList(SYSUSER_ROLEIDS.split(",")));
        }
        String SYSUSER_POSIDS = (String) sysUser.get("SYSUSER_POSIDS");
        if(StringUtils.isNotEmpty(SYSUSER_POSIDS)){
            this.savePositions(sysUserId, SYSUSER_POSIDS.split(","));
        }
        String USER_PHOTO_JSON = (String) sysUser.get("USER_PHOTO_JSON");
        fileAttachService.saveFileAttachs(USER_PHOTO_JSON,"PLAT_SYSTEM_SYSUSER",sysUserId,"photo");
        //更新用户的菜单权限
        this.updateUserRightJson(sysUserId);
        return sysUser;
    }
    /**
     *  获取下一个用户的序号
     * @return
     */
    @Override
    public int getNextUserSn(String companyId) {
    	int nextUserSn = 1;
		StringBuffer sql = new StringBuffer("");
		sql.append("SELECT MAX(U.SYSUSER_SN) SYSUSER_SN FROM PLAT_SYSTEM_SYSUSER U ");
		sql.append("WHERE U.SYSUSER_COMPANYID= ? ");
		Map<String,Object> map = dao.getBySql(sql.toString(), new Object[]{companyId});
		if(map!=null){
			if(map.get("SYSUSER_SN")!=null){
				int SYSUSER_SN = Integer.parseInt(map.get("SYSUSER_SN").toString());
				nextUserSn  = SYSUSER_SN+1;
			}
		}
		return nextUserSn;
	}

	/**
     * 第三方系统新增或者修改用户
     * @param sysUser
     * @return
     */
    public Map<String,Object> saveOrUpdateByThird(Map<String,Object> sysUser){
        String oldUserId = (String) sysUser.get("SYSUSER_ID");
        Map<String,Object> oldUser = dao.getRecord("PLAT_SYSTEM_SYSUSER",
                new String[]{"SYSUSER_ID"},new Object[]{oldUserId});
        if(oldUser==null){
            String password = PlatStringUtil.getSHA256Encode(SysUserService.DEFAULT_PASSWORD
                    , null, 1);
            sysUser.put("SYSUSER_PASSWORD", password);
        }
        sysUser = dao.saveOrUpdate("PLAT_SYSTEM_SYSUSER",
                sysUser,AllConstants.IDGENERATOR_ASSIGNED,null);
        String sysUserId = (String) sysUser.get("SYSUSER_ID");
        String SYSUSER_ROLEIDS = (String) sysUser.get("SYSUSER_ROLEIDS");
        if(StringUtils.isNotEmpty(SYSUSER_ROLEIDS)){
            this.saveRoles(sysUserId, Arrays.asList(SYSUSER_ROLEIDS.split(",")));
        }
        return sysUser;
    }
    
    /**
     * 获取获得用户信息的前缀SQL
     * @return
     */
    public String getUserInfoPreSql(){
        StringBuffer sql = new StringBuffer("SELECT T.SYSUSER_ID,T.SYSUSER_ACCOUNT,T.SYSUSER_NAME,");
        sql.append("T.SYSUSER_MOBILE,D.DEPART_NAME,C.COMPANY_NAME,C.COMPANY_ID ");
        sql.append("FROM PLAT_SYSTEM_SYSUSER T LEFT JOIN PLAT_SYSTEM_COMPANY C ");
        sql.append("ON T.SYSUSER_COMPANYID=C.COMPANY_ID LEFT JOIN PLAT_SYSTEM_DEPART D ");
        sql.append(" ON D.DEPART_ID=T.SYSUSER_DEPARTID ");
        sql.append(" WHERE T.SYSUSER_STATUS!=? ");
        return sql.toString();
    }
    
    /**
     * 根据部门ID获取用户列表数据
     * @param departId
     * @return
     */
    public List<Map<String,Object>> findByDepartId(String departId){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        sql.append(" AND T.SYSUSER_DEPARTID=? ");
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{
            SysUserService.SYSUSER_STATUS_DEL,departId}, null);
        return list;
    }
    
    /**
     * 根据部门ID和角色IDS获取用户列表数据
     * @param departId
     * @param roleIds
     * @return
     */
    public List<Map<String,Object>> findByDepartIdAndRoleIds(String departId,String roleIds){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        sql.append(" AND T.SYSUSER_DEPARTID=? ");
        sql.append(" AND T.SYSUSER_ID IN (");
        sql.append("SELECT UR.SYSUSER_ID FROM  PLAT_SYSTEM_SYSUSERROLE UR ");
        sql.append("WHERE UR.ROLE_ID IN ").append(PlatStringUtil.getValueArray(roleIds));
        sql.append(" )");
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{
            SysUserService.SYSUSER_STATUS_DEL,departId}, null);
        return list;
    }
    
    /**
     * 根据角色ID获取用户列表
     * @param roleId
     * @return
     */
    public List<Map<String,Object>> findByRoleId(String roleId){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        sql.append(" AND T.SYSUSER_ID IN (");
        sql.append("SELECT UR.SYSUSER_ID FROM  PLAT_SYSTEM_SYSUSERROLE UR ");
        sql.append("WHERE UR.ROLE_ID=? )");
        //获取当前登录用户
        Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
        sql.append(" AND C.COMPANY_PATH LIKE ? ");
        String SYSUSER_COMPANYID = (String) sysUser.get("SYSUSER_COMPANYID");
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{
            SysUserService.SYSUSER_STATUS_DEL,roleId,"%"+SYSUSER_COMPANYID+"%"}, null);
        return list;
        
    }
    
    /**
     * 根据角色组ID获取用户列表,如果没有传入,则获取所有
     * @param groupId
     * @return
     */
    public List<Map<String,Object>> findByRoleGroupId(String groupId){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        List params = new ArrayList();
        params.add(SysUserService.SYSUSER_STATUS_DEL);
        if(StringUtils.isNotEmpty(groupId)){
            sql.append(" AND T.SYSUSER_ID IN (");
            sql.append("SELECT UR.SYSUSER_ID FROM  PLAT_SYSTEM_SYSUSERROLE UR ");
            sql.append("WHERE UR.ROLE_ID IN(");
            sql.append("SELECT R.ROLE_ID FROM PLAT_SYSTEM_ROLE R ");
            sql.append("WHERE R.ROLE_GROUPID=? )");
            sql.append(")");
            params.add(groupId);
        }
        //获取当前登录用户
        Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
        //获取所在单位的ID
        String SYSUSER_COMPANYID = (String) sysUser.get("SYSUSER_COMPANYID");
        sql.append(" AND C.COMPANY_PATH LIKE ? ");
        params.add("%"+SYSUSER_COMPANYID+"%");
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),params.toArray(), null);
        return list;
    }
    
    /**
     * 根据filter获取网格项目
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findGridItemList(SqlFilter sqlFilter){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        List<Object> params = new ArrayList<Object>();
        params.add(SysUserService.SYSUSER_STATUS_DEL);
        String selectedUserIds = sqlFilter.getRequest().getParameter("selectedRecordIds");
        String iconfont = sqlFilter.getRequest().getParameter("iconfont");
        String itemconf = sqlFilter.getRequest().getParameter("itemconf");
        Map<String,String> getGridItemConf = PlatUICompUtil.getGridItemConfMap(itemconf);
        if(StringUtils.isNotEmpty(selectedUserIds)){
            sql.append(" AND T.SYSUSER_ID IN ");
            sql.append(PlatStringUtil.getSqlInCondition(selectedUserIds));
            //获取当前登录用户
            Map<String,Object> sysUser = PlatAppUtil.getBackPlatLoginUser();
            //获取所在单位的ID
            String SYSUSER_COMPANYID = (String) sysUser.get("SYSUSER_COMPANYID");
            sql.append(" AND C.COMPANY_PATH LIKE ? ");
            params.add("%"+SYSUSER_COMPANYID+"%");
            sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
            List<Map<String,Object>> list = dao.findBySql(sql.toString(),params.toArray(), null);
            list = PlatUICompUtil.getGridItemList("SYSUSER_ID", iconfont, getGridItemConf, list);
            return list;
        }else{
            return null;
        }
    }
    
    /**
     * 根据角色ID获取用户IDS
     * @param roleId
     * @return
     */
    public List<String> findUserIds(String roleId){
        return dao.findUserIds(roleId);
    }
    
    /**
     * 保存角色用户中间表
     * @param userId
     * @param roleIds
     */
    public void saveRoles(String userId,List<String> roleIds){
        //先删除用户角色中间表的数据
        StringBuffer delSql = new StringBuffer("DELETE FROM PLAT_SYSTEM_SYSUSERROLE ");
        delSql.append("WHERE SYSUSER_ID=? ");
        dao.executeSql(delSql.toString(), new Object[]{userId});
        StringBuffer sql = new StringBuffer("INSERT INTO PLAT_SYSTEM_SYSUSERROLE");
        sql.append("(SYSUSER_ID,ROLE_ID) VALUES(?,?) ");
        for(String roleId:roleIds){
            dao.executeSql(sql.toString(),new Object[]{userId,roleId});
        }
    }
    /**
     * 保存用户岗位中级表
     * @param userId
     * @param positionIds
     */
    public void savePositions(String userId,String[] positionIds){
      //先删除用户角色中间表的数据
        StringBuffer delSql = new StringBuffer("DELETE FROM PLAT_SYSTEM_SYSUSERPOS ");
        delSql.append("WHERE SYSUSER_ID=? ");
        dao.executeSql(delSql.toString(), new Object[]{userId});
        StringBuffer sql = new StringBuffer("INSERT INTO PLAT_SYSTEM_SYSUSERPOS");
        sql.append("(SYSUSER_ID,POSITION_ID) VALUES(?,?) ");
        for(String posId:positionIds){
            dao.executeSql(sql.toString(),new Object[]{userId,posId});
        }
    }
    
    /**
     * 根据用户ID数组删除用户数据,伪删除
     * @param userIds
     */
    public void deleteUserCascadeRole(String[] userIds){
        for(String userId:userIds){
            Map<String,Object> sysUser = dao.getRecord("PLAT_SYSTEM_SYSUSER",
                    new String[]{"SYSUSER_ID"},new Object[]{userId});
            String SYSUSER_ACCOUNT = (String) sysUser.get("SYSUSER_ACCOUNT");
            sysUser.put("SYSUSER_ACCOUNT", SYSUSER_ACCOUNT+userId);
            sysUser.put("SYSUSER_STATUS", SysUserService.SYSUSER_STATUS_DEL);
            dao.saveOrUpdate("PLAT_SYSTEM_SYSUSER", sysUser, AllConstants.IDGENERATOR_UUID, null);
            //删除即时通讯的用户
            chatOnlineService.deleteChatUser(sysUser);
        }
        //删除角色用户中间表数据
        StringBuffer sql = new StringBuffer("DELETE FROM PLAT_SYSTEM_SYSUSERROLE");
        sql.append(" WHERE SYSUSER_ID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(userIds));
        dao.executeSql(sql.toString(),null);
    }
    
    /**
     * 获取多个用户的姓名单位相关信息
     * @param userIds
     * @return
     */
    public Map<String,String> getUserNamesCompanyInfo(String userIds){
        StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        List<Object> params = new ArrayList<Object>();
        params.add(SysUserService.SYSUSER_STATUS_DEL);
        sql.append(" AND T.SYSUSER_ID IN ").append(PlatStringUtil.getSqlInCondition(userIds));
        List<Map<String,Object>> userList = dao.findBySql(sql.toString(), params.toArray(), null);
        StringBuffer userNames = new StringBuffer("");
        List<String> companyIds = new ArrayList<String>();
        List<String> companyNames = new ArrayList<String>();
        for(int i=0;i<userList.size();i++){
            if(i>0){
                userNames.append(",");
            }
            userNames.append(userList.get(i).get("SYSUSER_NAME"));
            String companyId = (String) userList.get(i).get("COMPANY_ID");
            String companyName = (String) userList.get(i).get("COMPANY_NAME");
            if(!companyIds.contains(companyId)){
                companyIds.add(companyId);
                companyNames.add(companyName);
            }
        }
        StringBuffer COMPANY_IDS = new StringBuffer();
        StringBuffer COMPANY_NAMES = new StringBuffer();
        for(int i=0;i<companyIds.size();i++){
            if(i>0){
                COMPANY_IDS.append(",");
                COMPANY_NAMES.append(",");
            }
            COMPANY_IDS.append(companyIds.get(i));
            COMPANY_NAMES.append(companyNames.get(i));
        }
        Map<String,String> userInfo = new HashMap<String,String>();
        userInfo.put("SYSUSER_NAMES", userNames.toString());
        userInfo.put("COMPANY_IDS", COMPANY_IDS.toString());
        userInfo.put("COMPANY_NAMES", COMPANY_NAMES.toString());
        return userInfo;
    }
    
    /**
     * 获取被授权的资源ID集合
     * @param userId
     * @return
     */
    public List<String> findGrantRightIds(String userId){
        return dao.findGrantRightIds(userId);
    }
    
    /**
     * 获取在线用户列表
     * @param sqlFilter
     * @param fieldConfig
     * @return
     */
    public List<Map<String,Object>> findOnlineUsers(SqlFilter sqlFilter,Map<String,Object> fieldConfig){
        List<Map<String,Object>> onlineList = new ArrayList<Map<String,Object>>();
        String USER_ACCOUNT = sqlFilter.getRequest().getParameter("SYSUSER_ACCOUNT");
        String USER_NAME = sqlFilter.getRequest().getParameter("SYSUSER_NAME");
        Map<String,Map<String,Object>> onlineUsers = PlatAppUtil.getOnlineUser();
        if(onlineUsers!=null&&onlineUsers.size()>0){
            Iterator it = onlineUsers.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String,Map<String,Object>> entry = (Map.Entry<String,Map<String,Object>>) it.next();
                String key = entry.getKey();
                Map<String,Object> loginUser = entry.getValue();
                if(loginUser!=null){
                    String SYSUSER_ACCOUNT = (String) loginUser.get("SYSUSER_ACCOUNT");
                    String SYSUSER_NAME = (String) loginUser.get("SYSUSER_NAME");
                    Map<String,Object> sysUser = new HashMap<String,Object>();
                    sysUser.put("SESSION_ID", loginUser.get("SESSION_ID"));
                    sysUser.put("SYSUSER_ACCOUNT", loginUser.get("SYSUSER_ACCOUNT"));
                    sysUser.put("SYSUSER_NAME", loginUser.get("SYSUSER_NAME"));
                    sysUser.put("HOST", loginUser.get("HOST"));
                    sysUser.put("LASTACCESSTIME", loginUser.get("LASTACCESSTIME"));
                    if(StringUtils.isNotEmpty(USER_ACCOUNT)){
                        if(SYSUSER_ACCOUNT.contains(USER_ACCOUNT)){
                            onlineList.add(sysUser);
                        }
                    }
                    if(StringUtils.isNotEmpty(USER_NAME)){
                        if(SYSUSER_NAME.contains(USER_NAME)){
                            onlineList.add(sysUser);
                        }
                    }
                    if(StringUtils.isEmpty(USER_ACCOUNT)&&StringUtils.isEmpty(USER_NAME)){
                        onlineList.add(sysUser);
                    }
                }
                
            }
        }
        return onlineList;
    }
    
    /**
     * 根据会话获取用户信息
     * @param session
     * @return
     */
    public Map<String,Object> principal(HttpSession  session) {
        return null;
    }
    
    /**
     * 判断是否存在用户
     * @param userId
     * @return
     */
    public boolean isExistsUser(String userId){
        return dao.isExistsUser(userId);
    }
    
    /**
     * 获取测试的资源用户列表
     * @param sqlFilter
     * @return
     */
    public List<Map<String,Object>> findTestResUsers(SqlFilter sqlFilter){
        String SYSUSER_ACCOUNT = sqlFilter.getRequest().getParameter("SYSUSER_ACCOUNT");
        StringBuffer sql =new StringBuffer("SELECT ");
        sql.append(" T.SYSUSER_ACCOUNT,T.SYSUSER_NAME,T.SYSUSER_MOBILE FROM ");
        sql.append("PLAT_SYSTEM_SYSUSER T WHERE T.SYSUSER_ACCOUNT=?");
        return dao.findBySql(sql.toString(), new Object[]{SYSUSER_ACCOUNT}, null);
    }
    
    /**
     * 获取系统用户详细信息
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object> getSysUserInfo(HttpServletRequest request,
            Map<String,Object> postParams){
        String SSO_USERNAME = (String) postParams.get("SSO_USERNAME");
        Map<String,Object> sysUser = dao.getRecord("PLAT_SYSTEM_SYSUSER"
                ,new String[]{"SYSUSER_ACCOUNT"},new Object[]{SSO_USERNAME});
        Map<String,Object> result = new HashMap<String,Object>();
        if(sysUser!=null){
            sysUser = this.setUserAuthRights(sysUser);
            result.put("success", true);
            result.putAll(sysUser);
        }else{
            result.put("success", false);
        }
        return result;
    }
    
    /**
     * 新增或者修改用户信息
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object> saveOrUpdateUser(HttpServletRequest request,
            Map<String,Object> postParams){
        String SYSUSER_COMPANYID = (String) postParams.get("SYSUSER_COMPANYID");
        if(StringUtils.isEmpty(SYSUSER_COMPANYID)){
            SYSUSER_COMPANYID = "402848a55b556eb9015b55968e8e002c";
            postParams.put("SYSUSER_COMPANYID", SYSUSER_COMPANYID);
        }
        this.saveOrUpdateUser(postParams,AllConstants.IDGENERATOR_ASSIGNED);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
    
    /**
     * 删除用户信息
     * @param request
     * @param postParams
     * @return
     */
    public Map<String,Object> deleteUser(HttpServletRequest request,
            Map<String,Object> postParams){
        String USER_IDS = (String) postParams.get("USER_IDS");
        this.deleteUserCascadeRole(USER_IDS.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }
    
    /**
     * 重置用户的密码
     * @param userIds
     */
    public void resetPassword(String userIds){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_SYSTEM_SYSUSER ");
        sql.append(" SET SYSUSER_PASSWORD=? WHERE SYSUSER_ID IN ");
        sql.append(PlatStringUtil.getSqlInCondition(userIds));
        String password = PlatStringUtil.getSHA256Encode(SysUserService.DEFAULT_PASSWORD
                , null, 1);
        dao.executeSql(sql.toString(), new Object[]{password});
    }
    
    /**
     * 更新权限JSON字段为空
     */
    public void updateRightJsonToNull(){
        String sql = "update PLAT_SYSTEM_SYSUSER SET SYSUSER_RIGHTJSON=null";
        dao.executeSql(sql, null);
    }
    
    /**
     * 更新权限JSON字段为空
     * @param roleId
     */
    public void updateRightJsonToNull(String roleId,String tableName){
        StringBuffer sql = null;
        if(tableName.equals("PLAT_SYSTEM_ROLE")){
            sql = new StringBuffer("update PLAT_SYSTEM_SYSUSER ");
            sql.append("SET SYSUSER_RIGHTJSON=null WHERE SYSUSER_ID IN");
            sql.append("(SELECT U.SYSUSER_ID FROM PLAT_SYSTEM_SYSUSERROLE U ");
            sql.append(" WHERE U.ROLE_ID=? )");
        }else if(tableName.equals("PLAT_SYSTEM_SYSUSER")){
            sql = new StringBuffer("update PLAT_SYSTEM_SYSUSER ");
            sql.append("SET SYSUSER_RIGHTJSON=null WHERE SYSUSER_ID =? ");
        }else if(tableName.equals("PLAT_SYSTEM_USERGROUP")){
            sql = new StringBuffer("update PLAT_SYSTEM_SYSUSER ");
            sql.append("SET SYSUSER_RIGHTJSON=null WHERE SYSUSER_GROUPID =? ");
        }
        dao.executeSql(sql.toString(), new Object[]{roleId});
    }
    
    /**
     * 根据单位ID和角色ID获取用户IDS
     * @param companyId
     * @param roleIds
     * @return
     */
    public List<String> findUserIds(String companyId,String roleIds){
        return dao.findUserIds(companyId, roleIds);
    }
    /**
     * 
     */
	@Override
	public void updateSn(String[] userIds) {
		dao.updateSn(userIds);
	}
	
	/**
     * 根据用户组ID获取用户ID列表
     * @param USERGROUP_ID
     * @return
     */
    public List<String> findGroupUserIds(String USERGROUP_ID){
        return dao.findGroupUserIds(USERGROUP_ID);
    }
    
    /**
     * 新增或者修改用户信息
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdateUser(HttpServletRequest request){
        Map<String,Object> sysUser = PlatBeanUtil.getMapFromRequest(request);
        //获取前端传递过来的字段变更JSON
        String formfieldModifyArrayJson = request.getParameter("formfieldModifyArrayJson");
        //解析成修改字段数组
        List<Map> formfieldModifyArray = JSON.parseArray(formfieldModifyArrayJson, Map.class);
        String SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
        sysUser = this.saveOrUpdateUser(sysUser,AllConstants.IDGENERATOR_UUID);
        if(StringUtils.isNotEmpty(SYSUSER_ID)){
            sysLogService.saveBackLog("系统用户管理",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+SYSUSER_ID+"]用户信息", request,formfieldModifyArray,null,null);
        }else{
            sysUser.put("msg", "新增用户成功,默认登录密码是"+SysUserService.DEFAULT_PASSWORD);
            SYSUSER_ID = (String) sysUser.get("SYSUSER_ID");
            sysLogService.saveBackLog("系统用户管理",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+SYSUSER_ID+"]用户信息", request,formfieldModifyArray,null,null);
        }
        
        sysUser.put("success", true);
        return sysUser;
    }
    
    /**
     * 删除用户信息
     * @param request
     * @return
     */
    public Map<String,Object> deleteUsers(HttpServletRequest request){
        String deleteUserIds = request.getParameter("selectColValues");
        String sql = PlatDbUtil.getDiskSqlContent("system/sysuser/001",null);
        List<List<String>> userList = dao.findListBySql(sql+PlatStringUtil.getSqlInCondition(deleteUserIds),null,null);
        sysLogService.saveBackLog("系统用户管理",SysLogService.OPER_TYPE_DEL,
                "删除了ID为["+deleteUserIds+"]的用户信息", request,null,
                "用户表主键值,用户账号,用户姓名,手机号,所在单位,所在部门",userList);
        this.deleteUserCascadeRole(deleteUserIds.split(","));
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        return result;
    }

	/**
	 * 根据部门ID获取获取ID列表
	 */
	@Override
	public List<String> findDepartUserIds(String DEPART_ID) {
		return dao.findDepartUserIds(DEPART_ID);
	}

	/**
	 * 获取无部门的用户列表
	 */
	@Override
	public List<Map<String, Object>> findNoDepartIdByCompanyIdAndRoleIds(
			String DEPART_COMPANYID, String ROLE_IDS) {
		StringBuffer sql = new StringBuffer(this.getUserInfoPreSql());
        sql.append(" AND T.SYSUSER_COMPANYID=? ");
        sql.append(" AND T.SYSUSER_DEPARTID  IS NULL ");
        if(StringUtils.isNotEmpty(ROLE_IDS)){
        	sql.append(" AND T.SYSUSER_ID IN (");
            sql.append("SELECT UR.SYSUSER_ID FROM  PLAT_SYSTEM_SYSUSERROLE UR ");
            sql.append("WHERE UR.ROLE_ID IN ").append(PlatStringUtil.getValueArray(ROLE_IDS));
            sql.append(" )");
        }
        
        sql.append(" ORDER BY T.SYSUSER_CREATETIME DESC");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(), new Object[]{
            SysUserService.SYSUSER_STATUS_DEL,DEPART_COMPANYID}, null);
        return list;
	}
  
}
