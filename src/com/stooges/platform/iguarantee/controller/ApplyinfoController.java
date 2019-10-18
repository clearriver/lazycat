/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.core.model.PagingBean;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.system.service.SysLogService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.ExecutionService;
import com.stooges.platform.workflow.service.JbpmService;
import com.stooges.platform.iguarantee.service.ApplyinfoService;

/**
 * 
 * 描述 个人委托担保申请信息表业务相关Controller
 * @author HuYu
 * @version 1.0
 * @created 2019-03-24 13:03:35
 */
@Controller  
@RequestMapping("/iguarantee/ApplyinfoController")  
public class ApplyinfoController extends BaseController {
    @Resource
    private ApplyinfoService applyinfoService;
    @Resource
    private ExecutionService executionService;
    @Resource
    private SysLogService sysLogService;
    @Resource
    private JbpmService jbpmService;
    @Resource
    private FileAttachService fileAttachService;
    /**
     * 删除个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "multiDel")
    public void multiDel(HttpServletRequest request,
            HttpServletResponse response) {
        String selectColValues = request.getParameter("selectColValues");
        //删除草稿
        String s1="'"+(selectColValues.contains(",")?selectColValues.replaceAll(",","','"):selectColValues)+"'";
        String sql="SELECT EXECUTION_ID, FLOWDEF_ID, EXECUTION_SUBJECT,EXECUTION_VERSION, TMPSAVE_MAINTABLENAME " + 
        		"FROM JBPM6_EXECUTION WHERE STATUS='0' AND TMPSAVE_RECORDID in ("+s1+")";
        List<Map<String,Object>> list=applyinfoService.findBySql(sql,new Object[] {},null);
        HashSet<String> exectionids=new HashSet<String>();
        list.forEach(new Consumer<Map<String,Object>>() {
			@Override
			public void accept(Map<String, Object> t) {
				exectionids.add((String)t.get("EXECUTION_ID"));
			}
        });
        executionService.delCascadeSubTables(String.join(",", exectionids).split(","));
        
        //删除不含流程的记录
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_APPLYINFO","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请信息表", request);
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_MANAGEMEN","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请--个人经营情况表", request);
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_ASSETS","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请信息表--个人家庭资产信息表", request);
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_FAMILY","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请信息表--个人家庭成员信息表", request);
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_DECLARE","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请信息表--申报基本信息表", request);
        applyinfoService.deleteRecords("PLAT_IGUARANTEE_INCOMEEXPENDIT","IGUARANTEE_ID",selectColValues.split(","));
        sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_DEL,"删除了ID为["+selectColValues+"]的个人委托担保申请信息表--个人收支信息表", request);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 新增或者修改个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdate")
    public void saveOrUpdate(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String INDIVIDUAL_CID = (String) applyinfo.get("INDIVIDUAL_CID");
        if(StringUtils.isNotBlank(INDIVIDUAL_CID)){
            Map<String,Object> checkMap=checkApplyinfo(INDIVIDUAL_CID);
            if(checkMap!=null) {
            	Boolean success=(Boolean)checkMap.get("success");
            	if(!success) {
            		this.printObjectJsonString(checkMap, response);
            		return ;
            	}
            }
        }
        
        String IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_APPLYINFO",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        
        Map<String, Object> person=applyinfoService.getRecord("PLAT_IGUARANTEE_PERSON", new String[]{"INDIVIDUAL_CID"},new Object[] {applyinfo.get("INDIVIDUAL_CID")});
        if(person==null||person.size()==0) {
        	applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_PERSON",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
//        Map<String,Object> managemen = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_MANAGEMEN",applyinfo,AllConstants.IDGENERATOR_UUID,null);
//        System.out.println(managemen);
//        Map<String,Object> assets = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_ASSETS",applyinfo,AllConstants.IDGENERATOR_UUID,null);
//        System.out.println(assets);
//        Map<String,Object> family = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_FAMILY",applyinfo,AllConstants.IDGENERATOR_UUID,null);
//        System.out.println(family);
        System.out.println("family start...");
        saveLine("PLAT_IGUARANTEE_FAMILY","IGUARANTEE_ID","FAMILY_ID",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        System.out.println("family end.");
//        Map<String, Object> d=applyinfoService.getRecord("PLAT_IGUARANTEE_DECLARE", new String[]{"IGUARANTEE_ID"},new Object[] {applyinfo.get("IGUARANTEE_ID")});
//        if(d!=null&&d.size()>0) {
//        	applyinfo.put("Managemen_ID",d.get("Managemen_ID"));
//        }
//        Map<String,Object> declare = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_DECLARE",applyinfo,AllConstants.IDGENERATOR_UUID,null);
//        System.out.println(declare);
//        Map<String,Object> incomeexpendit = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_INCOMEEXPENDIT",applyinfo,AllConstants.IDGENERATOR_UUID,null);
//        System.out.println(incomeexpendit);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //applyinfo = applyinfoService.saveOrUpdateTreeData("PLAT_IGUARANTEE_APPLYINFO",
        //        applyinfo,AllConstants.IDGENERATOR_UUID,null);
        String FILE_JSON = (String) applyinfo.get("FILE_JSON");
        JSONArray jsonArray = JSON.parseArray(FILE_JSON);
        String[] field_names= {"UPLOAD_ID_CARD","UPLOAD_ID_CARD_BACK","UPLOAD_RESIDENCE","UPLOAD_ASSET"};
        for(int i=0;i<field_names.length;i++) {
        	fileAttachService.deleteFiles("PLAT_IGUARANTEE_APPLYINFO", IGUARANTEE_ID, "attach_"+field_names[i]);
        }
        for(int i=0;i<jsonArray.size();i++) {
        	JSONObject o=jsonArray.getJSONObject(i);
        	JSONArray ilist=new JSONArray();ilist.add(o);
            fileAttachService.saveFileAttachs(ilist.toJSONString(),"PLAT_IGUARANTEE_APPLYINFO",IGUARANTEE_ID,"attach_"+o.getString("field_name"));
        }
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+IGUARANTEE_ID+"]个人委托担保申请信息表", request);
        }else{
            IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+IGUARANTEE_ID+"]个人委托担保申请信息表", request);
        }
        applyinfo.put("success", true);
        this.printObjectJsonString(applyinfo, response);
    }
    private void saveLine(String tableName,String mainid,String lineid,Map<String,Object> colValues ,int idGenerator, String seqName){
        String linepk=applyinfoService.getTableFieldValues(tableName, lineid,new String[]{mainid},new String[]{String.valueOf(colValues.get(mainid))},",");
        linepk=linepk.replaceAll(String.valueOf(colValues.get(lineid)),"");
    	Map<String,Object> family = applyinfoService.saveOrUpdate(tableName,colValues,AllConstants.IDGENERATOR_UUID,null);
        System.out.println("Line :"+family);
        Object lineNumber=colValues.get("lineNumbers");
        String lineNumbers[]=StringUtils.isBlank(lineNumber.toString())?new String[0]:lineNumber.toString().split(",");
        List<String> cols=applyinfoService.findColumnName(tableName);
        for(String line:lineNumbers) {
        	HashMap<String,Object> formData=getFormData(mainid,line,colValues,cols);
        	linepk=linepk.replaceAll(String.valueOf(formData.get(lineid)),"");
        	family = applyinfoService.saveOrUpdate(tableName,formData,AllConstants.IDGENERATOR_UUID,null);
            System.out.println("Line "+line+":"+family);
        }
        linepk=linepk.replaceAll(" *, *, *",",").trim();
        if(linepk.length()>2) {
        	applyinfoService.deleteRecords(tableName,lineid,linepk.split(","));
        }
    }
    private HashMap<String,Object> getFormData(String mainid,String lineNumber,Map<String,Object> colValues,List<String> cols){
    	HashMap<String,Object> r=new HashMap<String,Object>();
    	r.put(mainid, colValues.get(mainid));
        for(Map.Entry<String,Object> e:colValues.entrySet()) {
        	if(e.getKey().endsWith("_"+lineNumber)){
        		String key=e.getKey().substring(0, e.getKey().length()-("_"+lineNumber).length());
        		if(cols.contains(key.toUpperCase())) {
        			r.put(key, e.getValue());
        		}
        	}
        }
    	return r;
    }
    /**
     * 新增或者修改个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdateFINFO")
    public void saveOrUpdateFINFO(HttpServletRequest request,
            HttpServletResponse response) {
    	//0.其他,1.信用,3.抵押质押,4.法人保证,5.自然人联保,6.自然人保证
    	//GUARANTEE_GTYPE=6,5,4,3,1
    	//１.自然人联保、自然人保证对应自然人反担保，２.法人保证对应法人反担保，３.抵押质押对应抵押质押，４.其他和信用没要填的内容
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String GUARANTEE_GTYPE=String.valueOf(applyinfo.get("GUARANTEE_GTYPE"));
        if(GUARANTEE_GTYPE.contains("5")||GUARANTEE_GTYPE.contains("6")) {
        	applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_FINFO",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
        if(GUARANTEE_GTYPE.contains("4")) {
        	applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_CORPORATE_GUARANTEE",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
        if(GUARANTEE_GTYPE.contains("3")) {
        	applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_IMMOVABLES",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_APPLYINFO",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo.put("success", true);
        this.printObjectJsonString(applyinfo, response);
    }
    /**
     * 新增或者修改个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdateINV")
    public void saveOrUpdateINV(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_LIABILITIES",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_DECLARE",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_INCOMEEXPENDIT",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_MANAGEMEN",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_APPLYINFO",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        applyinfo.put("success", true);
        this.printObjectJsonString(applyinfo, response);
    }
    /**
     * 新增或者修改个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdateCredit")
    public void saveOrUpdateCredit(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        Map<String,String> fieldMapping=new HashMap<String,String>(){{
        	put("AGE_SCORE","C_AGE");
        	put("SEX_SCORE","C_SEX");
        	put("RESIDENCE_SCORE","C_RESIDENCE");
        	put("EDUCATION_SCORE","C_EDUCATION");
        	put("PROFESSION_SCORE","C_PROFESSION");
        	put("TITLE_SCORE","C_TITLE");
        	put("WORKING_LIFE_SCORE","C_WORKING_LIFE");
        	put("MARITAL_STATUS_SCORE","C_MARITAL_STATUS");
        	put("SPOUSE_OCCUPATION_SCORE","C_SPOUSE_OCCUPATION");
        	put("SPOUSE_TITLE_SCORE","C_SPOUSE_TITLE");
        	put("HOUSE_PROPERTY_SCORE","C_HOUSE_PROPERTY");
        	put("FIXED_ASSET_SCORE","C_FIXED_ASSET");
        	put("LIQUIDITY_SCORE","C_LIQUIDITY");
        	put("FAMILY_INCOME_SCORE","C_FAMILY_INCOME");
        	put("FAMILY_YEAR_INCOME_SCORE","C_FAMILY_YEAR_INCOME");
        	put("MEMBER_INCOME_SCORE","C_MEMBER_INCOME");
        	put("FAMILY_DEBT_SCORE","C_FAMILY_DEBT");
        	put("FAMILY_Y_DEBT_SCORE","C_FAMILY_Y_DEBT");
        	put("HEALTHY_SCORE","C_HEALTHY");
        	put("INSURANCE_SCORE","C_INSURANCE");
        	put("GENERAL_SUMMARY_SCORE","C_GENERAL_SUMMARY");
        	put("PERSONAL_LOAD_YEAR_SCORE","C_PERSONAL_LOAD_YEAR");
        	put("LOAD_LIMIT_SCORE","C_LOAD_LIMIT");
        	put("LOAD_N_TIMES_SCORE","C_LOAD_N_TIMES");
        	put("COOPERATION_YEAR_SCORE","C_COOPERATION_YEAR");
        }};
        Map<String,Object> fieldScores=new HashMap<String,Object>();
        String sql="SELECT DD.DIC_ID,DD.DIC_NAME,DD.DIC_VALUE,DD.DIC_DICTYPE_CODE,DA.DICATTACH_VALUE " + 
        		"FROM PLAT_SYSTEM_DICTIONARY DD,PLAT_SYSTEM_DICATTACH DA " + 
        		"WHERE DD.DIC_ID=DA.DICATTACH_DICID AND DD.DIC_DICTYPE_CODE=? AND DA.DICATTACH_KEY='V' AND DD.DIC_VALUE=?";
        double TOTAL_SCORE=0;
        for(Map.Entry<String,String> e:fieldMapping.entrySet()) {
        	Map<String, Object> dd=applyinfoService.getBySql(sql,new Object[] {e.getValue(),applyinfo.get(e.getKey())});
        	if(dd!=null&&dd.containsKey("DICATTACH_VALUE")) {
        		Object v=dd.getOrDefault("DICATTACH_VALUE",0);
        		TOTAL_SCORE+=Double.valueOf(String.valueOf(v).trim());
        	}
        }
        applyinfo.put("TOTAL_SCORE",TOTAL_SCORE);
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_CREDIT_PERSON",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        sql="SELECT DD.DIC_ID,DD.DIC_NAME,DD.DIC_VALUE,DD.DIC_DICTYPE_CODE FROM PLAT_SYSTEM_DICTIONARY DD WHERE DD.DIC_DICTYPE_CODE=?";
        List<Map<String,Object>> CREDIT_RISK_LEVEL=applyinfoService.findBySql(sql,new Object[] {"CREDIT_RISK_LEVEL"},null);
        for(Map<String,Object> map:CREDIT_RISK_LEVEL) {
        	String[] fieldName=String.valueOf(map.get("DIC_NAME")).split("-");
        	if(Math.floor(TOTAL_SCORE)>=Double.valueOf(fieldName[0])&&Math.floor(TOTAL_SCORE)<=Double.valueOf(fieldName[1])){
                applyinfo.put("CREDIT_LEVEL",String.valueOf(map.get("DIC_VALUE")));
                applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_APPLYINFO",applyinfo,AllConstants.IDGENERATOR_UUID,null);
                break;
        	}
        }
        applyinfo.put("msg","信用评分完成!");
        applyinfo.put("success", true);
        this.printObjectJsonString(applyinfo, response);
    }
    /**
     * 新增或者修改个人委托担保申请信息表数据并启动流程
     * @author wuqh5
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveAndStartWf")
    public ModelAndView saveAndStartWf(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
        applyinfo = applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_APPLYINFO",
                applyinfo,AllConstants.IDGENERATOR_UUID,null);
        //如果是保存树形结构表数据,请调用下面的接口,而注释掉上面的接口代码
        //applyinfo = applyinfoService.saveOrUpdateTreeData("PLAT_IGUARANTEE_APPLYINFO",
        //        applyinfo,AllConstants.IDGENERATOR_UUID,null);

        
        Map<String, Object> person=applyinfoService.getRecord("PLAT_IGUARANTEE_PERSON", new String[]{"INDIVIDUAL_CID"},new Object[] {applyinfo.get("INDIVIDUAL_CID")});
        if(person==null||person.size()==0) {
        	applyinfoService.saveOrUpdate("PLAT_IGUARANTEE_PERSON",applyinfo,AllConstants.IDGENERATOR_UUID,null);
        }
        
        String FILE_JSON = (String) applyinfo.get("FILE_JSON");
        JSONArray jsonArray = JSON.parseArray(FILE_JSON);
        String[] field_names= {"UPLOAD_ID_CARD","UPLOAD_ID_CARD_BACK","UPLOAD_RESIDENCE","UPLOAD_ASSET"};
        for(int i=0;i<field_names.length;i++) {
        	fileAttachService.deleteFiles("PLAT_IGUARANTEE_APPLYINFO", IGUARANTEE_ID, "attach_"+field_names[i]);
        }
        for(int i=0;i<jsonArray.size();i++) {
        	JSONObject o=jsonArray.getJSONObject(i);
        	JSONArray ilist=new JSONArray();ilist.add(o);
            fileAttachService.saveFileAttachs(ilist.toJSONString(),"PLAT_IGUARANTEE_APPLYINFO",IGUARANTEE_ID,"attach_"+o.getString("field_name"));
        }        
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_EDIT,
                    "修改了ID为["+IGUARANTEE_ID+"]个人委托担保申请信息表", request);
        }else{
            IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
            sysLogService.saveBackLog("个人申请",SysLogService.OPER_TYPE_ADD,
                    "新增了ID为["+IGUARANTEE_ID+"]个人委托担保申请信息表", request);
        }
       
        applyinfo.put("success", true);
//        this.printObjectJsonString(applyinfo, response);
        
        String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        JbpmFlowInfo jbpmFlowInfo = jbpmService.getJbpmFlowInfo(null, FLOWDEF_ID, "false",null);
        jbpmFlowInfo.setJbpmExeId("-1");
        jbpmFlowInfo.setJbpmSerItemId(SERITEM_ID);
        return jbpmService.getFlowDesignUI(request, jbpmFlowInfo);
    }
    @RequestMapping(params = "goFormWf")
    public ModelAndView goFormWf(HttpServletRequest request) {
//        String IGUARANTEE_ID = request.getParameter("IGUARANTEE_ID");
        //获取设计的界面编码
//        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
//        Map<String,Object> applyinfo = null;
//        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
//            applyinfo = this.applyinfoService.getRecord("PLAT_IGUARANTEE_APPLYINFO"
//                    ,new String[]{"IGUARANTEE_ID"},new Object[]{IGUARANTEE_ID});
//        }else{
//            applyinfo = new HashMap<String,Object>();
//        }
//        request.setAttribute("applyinfo", applyinfo);


        String EXECUTION_ID = request.getParameter("EXECUTION_ID");
        String FLOWDEF_ID = request.getParameter("FLOWDEF_ID");
        String SERITEM_ID = request.getParameter("SERITEM_ID");
        String jbpmIsQuery = request.getParameter("jbpmIsQuery");
        JbpmFlowInfo jbpmFlowInfo =null;
        if(StringUtils.isBlank(EXECUTION_ID)) {
        	jbpmFlowInfo = jbpmService.getJbpmFlowInfo(null, FLOWDEF_ID, "false",null);
            jbpmFlowInfo.setJbpmExeId("-1");
        }else {
        	jbpmFlowInfo = jbpmService.getJbpmFlowInfo(EXECUTION_ID,null,jbpmIsQuery,null);
        }
        jbpmFlowInfo.setJbpmSerItemId(SERITEM_ID);

        String recordId =request.getParameter(jbpmFlowInfo.getJbpmMainTablePkName());
        jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
        
        return jbpmService.getFlowDesignUI(request, jbpmFlowInfo);
    }
    /**
     * 跳转到个人委托担保申请信息表表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goForm")
    public ModelAndView goForm(HttpServletRequest request) {
        String IGUARANTEE_ID = request.getParameter("IGUARANTEE_ID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> applyinfo = null;
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
            applyinfo = this.applyinfoService.getRecord("PLAT_IGUARANTEE_APPLYINFO"
                    ,new String[]{"IGUARANTEE_ID"},new Object[]{IGUARANTEE_ID});
        }else{
            applyinfo = new HashMap<String,Object>();
        }
        request.setAttribute("applyinfo", applyinfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
        //如果是跳转到树形表单录入界面,请开放以下代码,,而注释掉上面的代码
        /*String IGUARANTEE_ID = request.getParameter("IGUARANTEE_ID");
        String APPLYINFO_PARENTID = request.getParameter("APPLYINFO_PARENTID");
        //获取设计的界面编码
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        Map<String,Object> applyinfo = null;
        if(StringUtils.isNotEmpty(IGUARANTEE_ID)){
            applyinfo = this.applyinfoService.getRecord("PLAT_IGUARANTEE_APPLYINFO"
                    ,new String[]{"IGUARANTEE_ID"},new Object[]{IGUARANTEE_ID});
            APPLYINFO_PARENTID = (String) applyinfo.get("Applyinfo_PARENTID");
        }
        Map<String,Object> parentApplyinfo = null;
        if(APPLYINFO_PARENTID.equals("0")){
            parentApplyinfo = new HashMap<String,Object>();
            parentApplyinfo.put("IGUARANTEE_ID",APPLYINFO_PARENTID);
            parentApplyinfo.put("APPLYINFO_NAME","个人委托担保申请信息表树");
        }else{
            parentApplyinfo = this.applyinfoService.getRecord("PLAT_IGUARANTEE_APPLYINFO",
                    new String[]{"IGUARANTEE_ID"}, new Object[]{APPLYINFO_PARENTID});
        }
        if(applyinfo==null){
            applyinfo = new HashMap<String,Object>();
        }
        applyinfo.put("APPLYINFO_PARENTID",parentApplyinfo.get("IGUARANTEE_ID"));
        applyinfo.put("APPLYINFO_PARENTNAME",parentApplyinfo.get("APPLYINFO_NAME"));
        request.setAttribute("applyinfo", applyinfo);
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);*/
    }
    
    /**
     * 新增或者修改个人委托担保申请信息表数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "upload")
    public void upload(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String IGUARANTEE_ID = (String) applyinfo.get("IGUARANTEE_ID");
        String FILE_JSON = (String) applyinfo.get("FILE_JSON");
        fileAttachService.saveFileAttachs(FILE_JSON,"PLAT_IGUARANTEE_APPLYINFO",IGUARANTEE_ID,"attach_");
        applyinfo.put("success", true);
        this.printObjectJsonString(applyinfo, response);
    }
    @RequestMapping(params = "filelist")
    public void filelist(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> applyinfo = PlatBeanUtil.getMapFromRequest(request);
        String busTableName = (String) applyinfo.get("busTableName");
        String busRecordId = (String) applyinfo.get("busRecordId");
        String type = (String) applyinfo.get("type");

        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFileUrl");
        String path=attachFilePath.substring(0, attachFilePath.length()-1);
        List<Map<String, Object>> list=fileAttachService.findList(busTableName,busRecordId,type);
        list.forEach(e->{
        	e.put("id",e.get("FILE_ID"));
        	e.put("name",e.get("FILE_NAME"));
        	e.put("dbfilepath",e.get("FILE_PATH"));
			e.put("getfileurl",path+"/"+e.get("FILE_PATH"));
        });
        applyinfo.put("success", true);
        applyinfo.put("filelist", list);
        this.printObjectJsonString(applyinfo, response);
    }
    private Map<String,Object> checkApplyinfo(String INDIVIDUAL_CID) {
    	Map<String,Object> r=new HashMap<String,Object>();
    	//展期  1102  到期终结  1103 逾期 1104 代偿与追偿 1105 担保中 1106
    	String sql="SELECT IGUARANTEE_ID FROM ZMRY.PLAT_IGUARANTEE_APPLYINFO WHERE INDIVIDUAL_CID='"+INDIVIDUAL_CID+"' AND BUSINESS_STATUS IN('1102','1104','1105','1106')";
    	List<Map<String, Object>> result=executionService.findBySql(sql,null,null);
    	if(result!=null&&result.size()>0) {
    		Map<String,String> statusMap=new HashMap<String,String>(){{put("1102","展期");put("1104","逾期");put("1105","代偿与追偿");put("1106","担保中");}};
    		result.forEach(map->{
    			String status=String.valueOf(map.get("BUSINESS_STATUS"));
    			r.put("msg","您有\""+statusMap.get(status)+"\"的申请单，不能再申请!");
    		});
        	r.put("success",false);
    	}
    	r.put("success", true);
    	return r;
    }
    /**
     * 验证字段的唯一性
     * @param request
     * @param response
     */
//    @RequestMapping(params = "checkUnique")
//    public void checkUnique(HttpServletRequest request,
//            HttpServletResponse response) {
//        String VALID_TABLENAME= request.getParameter("VALID_TABLENAME");
//        String VALID_FIELDLABLE = request.getParameter("VALID_FIELDLABLE");
//        String VALID_FIELDNAME = request.getParameter("VALID_FIELDNAME");
//        String VALID_FIELDVALUE = request.getParameter(VALID_FIELDNAME);
//        String RECORD_ID = request.getParameter("RECORD_ID");
//        Map<String,String> result = new HashMap<String,String>();
//        boolean isExists = commonService.isExistsRecord(VALID_TABLENAME, 
//                VALID_FIELDNAME, VALID_FIELDVALUE,RECORD_ID);
//        if(isExists){
//            result.put("error", "该"+VALID_FIELDLABLE+"已经存在,请重新输入!");  
//        }
//        this.printObjectJsonString(result, response);
//    }
}
