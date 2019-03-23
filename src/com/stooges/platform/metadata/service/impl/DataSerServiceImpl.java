/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.metadata.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.BrowserUtils;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.appmodel.service.RedisService;
import com.stooges.platform.metadata.dao.DataSerDao;
import com.stooges.platform.metadata.service.DataResService;
import com.stooges.platform.metadata.service.DataSerService;
import com.stooges.platform.metadata.service.DatalogService;

/**
 * 描述 数据服务信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-05-08 16:49:06
 */
@Service("dataSerService")
public class DataSerServiceImpl extends BaseServiceImpl implements DataSerService {

    /**
     * 所引入的dao
     */
    @Resource
    private DataSerDao dao;
    /**
     * 
     */
    @Resource
    private DataResService dataResService;
    /**
     * 
     */
    @Resource
    private DatalogService datalogService;
    /**
     * 
     */
    @Resource
    private RedisService redisService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo){
        String CATALOG_ID= filter.getRequest().getParameter("CATALOG_ID");
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("select T.DATASER_ID,R.DATARES_NAME,T.DATASER_CODE");
        sql.append(",T.DATASER_NAME,T.DATASER_STATUS,T.DATASER_CACHEABLE,T.DATASER_RELOG");
        sql.append(" from PLAT_METADATA_DATASER T LEFT JOIN PLAT_METADATA_DATARES");
        sql.append(" R ON T.DATARES_ID=R.DATARES_ID ");
        if(StringUtils.isNotEmpty(CATALOG_ID)){
            sql.append("WHERE T.DATASER_ID IN (SELECT S.DATASER_ID FROM PLAT_METADATA_CATASER ");
            sql.append("  S WHERE S.CATALOG_ID=?)");
            params.add(CATALOG_ID);
        }
        String exeSql = dao.getQuerySql(filter, sql.toString(), params);
        exeSql+=" ORDER BY T.DATASER_TIME DESC";
        List<Map<String, Object>> list = dao.findBySql(exeSql,
                params.toArray(), filter.getPagingBean());
        return list;
    }
    
    /**
     * 保存目录服务中间表
     * @param resId
     * @param catalogIds
     */
    public void saveOrUpdateSerCatalog(String serId,String catalogIds){
        this.deleteRecords("PLAT_METADATA_CATASER","DATASER_ID",
                new String[]{serId});
        String[] catalogIdArray = catalogIds.split(",");
        String sql = "insert into PLAT_METADATA_CATASER(CATALOG_ID,DATASER_ID)";
        sql+=" VALUES(?,?) ";
        for(String catalogId:catalogIdArray){
            dao.executeSql(sql, new Object[]{catalogId,serId});
        }
    }
    
    /**
     * 获取已经选择的服务记录
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findSelected(SqlFilter filter){
        String selectedRoleIds = filter.getRequest().getParameter("selectedRecordIds");
        StringBuffer sql = new StringBuffer("select T.DATASER_ID,T.DATASER_CODE,T.DATASER_NAME");
        sql.append(" from PLAT_METADATA_DATASER T ");
        if(StringUtils.isNotEmpty(selectedRoleIds)){
            sql.append(" WHERE T.DATASER_ID IN ").append(PlatStringUtil.getValueArray(selectedRoleIds));
            sql.append(" ORDER BY T.DATASER_TIME DESC ");
            return this.findBySql(sql.toString(), null, null);
        }else{
            return null;
        }
    }
    
    /**
     * 根据服务IDS获取服务信息
     * @param serIds
     * @return
     */
    public Map<String,String> getDataSerInfo(String serIds){
        Map<String,String> serInfo =new HashMap<String,String>();
        StringBuffer sql = new StringBuffer("SELECT T.DATASER_ID,");
        sql.append("T.DATASER_NAME FROM PLAT_METADATA_DATASER T");
        sql.append(" WHERE T.DATASER_ID IN ").append(PlatStringUtil.getSqlInCondition(serIds));
        StringBuffer recordIds = new StringBuffer();
        StringBuffer recordNames = new StringBuffer();
        List<Map<String,Object>> list = this.findBySql(sql.toString(), null, null);
        for(int i=0;i<list.size();i++){
            Map<String,Object> map = list.get(i);
            String DATASER_ID= (String) map.get("DATASER_ID");
            String DATASER_NAME = (String) map.get("DATASER_NAME");
            if(i>0){
                recordIds.append(",");
                recordNames.append(",");
            }
            recordIds.append(DATASER_ID);
            recordNames.append(DATASER_NAME);
        }
        serInfo.put("recordIds", recordIds.toString());
        serInfo.put("recordNames", recordNames.toString());
        return serInfo;
    }
    
    /**
     * 获取查询参数列表
     * @param sqlFilter
     * @return
     */
    public List<Map> findQueryParams(SqlFilter sqlFilter){
        return null;
    }
    
    /**
     * 调用请求服务
     * @param request
     * @return
     * @throws Exception 
     */
    public Map<String,Object> invokeService(HttpServletRequest request,boolean isTest) throws Exception{
        Map<String,Object> result = new HashMap<String,Object>();
        //获取请求过来的IP地址
        String requestIp = BrowserUtils.getIpAddr(request);
        //获取请求的服务编码
        String servicecode = request.getParameter("servicecode");
        //获取请求方的授权码
        String grantcode = request.getParameter("grantcode");
        if(isTest){
            grantcode = "BFbhXSKZXvjii1+bMaL/Wg==";
        }
        Map<String,Object> dockInfo = null;
        if(StringUtils.isNotEmpty(grantcode)){
            //获取请求方信息
            dockInfo = this.getRecord("PLAT_METADATA_DOCK", 
                    new String[]{"DOCK_GRCODE"}, new Object[]{grantcode});
        }else{
            result.put("success", false);
            result.put("invokeResultCode", DataSerService.CODE_INVALID);
            return result;
        }
        if(dockInfo!=null){
            //获取请求是否被授权通过
            String DOCK_RESULT = dockInfo.get("DOCK_RESULT").toString();
            if(!DOCK_RESULT.equals("1")){
                result.put("success", false);
                result.put("invokeResultCode", DataSerService.CODE_INVALID);
                return result;
            }
            //获取请求方申请的IP
            String DOCK_GRIPS = (String) dockInfo.get("DOCK_GRIPS");
            if(StringUtils.isNotEmpty(DOCK_GRIPS)){
                Set<String> callerIpSet = new HashSet<String>(Arrays.
                        asList(DOCK_GRIPS.split(",")));
                if(!callerIpSet.contains(requestIp)){
                    result.put("success", false);
                    result.put("invokeResultCode", DataSerService.CODE_IPNOAUTH);
                    return result;
                }
            }
            Map<String,Object> serviceInfo = this.getRecord("PLAT_METADATA_DATASER",
                    new String[]{"DATASER_CODE"},new Object[]{servicecode});
            if(serviceInfo!=null){
                //获取服务ID
                String serviceId = (String) serviceInfo.get("DATASER_ID");
                //获取被授权的服务IDS
                String DOCK_SERIDS = (String) dockInfo.get("DOCK_SERIDS");
                Set<String> grantSerIdSet = new HashSet<String>(Arrays.
                        asList(DOCK_SERIDS.split(",")));
                if(!grantSerIdSet.contains(serviceId)&&isTest==false){
                    result.put("success", false);
                    result.put("invokeResultCode", DataSerService.CODE_NOAUTHSERVICE);
                    return result;
                }
                int DATASER_DAYCOUNT = serviceInfo.get("DATASER_DAYCOUNT")!=null?Integer.parseInt(
                        serviceInfo.get("DATASER_DAYCOUNT").toString()):0;
                if(DATASER_DAYCOUNT!=0){
                    int invokeCount = datalogService.getCount(servicecode,grantcode);
                    if(invokeCount>=DATASER_DAYCOUNT){
                        result.put("success", false);
                        result.put("invokeResultCode", DataSerService.CODE_OVERCOUNT);
                        return result;
                    }
                }
                String DATASER_STATUS = serviceInfo.get("DATASER_STATUS").toString();
                if(DATASER_STATUS.equals("1")){
                    String DATASER_CACHEABLE = serviceInfo.get("DATASER_CACHEABLE").toString();
                    if(DATASER_CACHEABLE.equals("1")){
                        String resultJson = redisService.get(DataSerService.CACHEPRE_CODE+servicecode);
                        if(StringUtils.isNotEmpty(resultJson)){
                            result = JSON.parseObject(resultJson,Map.class);
                            return result;
                        }
                    }
                    result = dataResService.invokeRes(request, serviceInfo, dockInfo);
                    if(DATASER_CACHEABLE.equals("1")&&(Boolean)result.get("success")==true){
                        String resultJson = JSON.toJSONString(result);
                        redisService.set(DataSerService.CACHEPRE_CODE+servicecode,resultJson);
                    }
                    return result;
                }else{
                    result.put("success", false);
                    result.put("invokeResultCode", DataSerService.CODE_STOPSERVICE);
                    return result;
                }
            }else{
                result.put("success", false);
                result.put("invokeResultCode", DataSerService.CODE_NOSERVICE);
                return result;
            }
        }else{
            result.put("success", false);
            result.put("invokeResultCode", DataSerService.CODE_INVALID);
            return result;
        }
    }
    
    /**
     * 获取自动清除缓存的服务
     * @return
     */
    public List<Map<String,Object>> findAutoClearList(){
        StringBuffer sql = new StringBuffer("SELECT T.DATASER_CODE FROM PLAT_METADATA_DATASER");
        sql.append(" T WHERE T.AUTO_CLEARCACHE=? ");
        return this.findBySql(sql.toString(), new Object[]{1}, null);
    }
    
    /**
     * 调用请求服务
     * @param request
     * @return
     */
    public Map<String,Object> invokeService(HttpServletRequest request){
        //获取请求过来的IP地址
        String requestIp = BrowserUtils.getIpAddr(request);
        //获取请求的服务编码
        String servicecode = request.getParameter("servicecode");
        //获取请求方的授权码
        String grantcode = request.getParameter("grantcode");
        Map<String,Object> result = new HashMap<String,Object>();
        //开始记录请求日志
        Map<String,Object> datalog = new HashMap<String,Object>();
        String DATALOG_ERLOG = null;
        try {
            result = this.invokeService(request, false);
        } catch (Exception e) {
            DATALOG_ERLOG = ExceptionUtils.getStackTrace(e);
            result.put("success", false);
            result.put("invokeResultCode", DataSerService.CODE_OTHER);
            e.printStackTrace();
        }
        datalogService.saveDataLog(request, requestIp, servicecode, grantcode, result,
                datalog, DATALOG_ERLOG);
        return result;
    }
  
}
