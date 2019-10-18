/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.service.impl;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.DocUtil;
import com.stooges.core.util.MoneyUtil;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.platform.appmodel.service.FileAttachService;
import com.stooges.platform.iguarantee.dao.ApplyinfoDao;
import com.stooges.platform.iguarantee.service.ApplyinfoService;
import com.stooges.platform.system.service.DictionaryService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.JbpmTaskService;

/**
 * 描述 个人委托担保申请信息表业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-03-24 13:03:35
 */
@Service("applyinfoService")
public class ApplyinfoServiceImpl extends BaseServiceImpl implements ApplyinfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private ApplyinfoDao dao;
    @Resource
    private FileAttachService fileAttachService;
    @Resource
    private DictionaryService dictionaryService;
    @Resource
    private JbpmTaskService jbpmTaskService;
    @Override
    protected BaseDao getDao() {
        return dao;
    }
    /**
     * 判断天数进行相应的跳转
     * @param flowVars
     * @param jbpmFlowInfo
     * @return
     */
    public Set<String> decideResult(Map<String,Object> flowVars,JbpmFlowInfo jbpmFlowInfo){
        Set<String> resutNodeKey = new HashSet<String>();
        try {
			double Guarantee_Quota = Double.parseDouble(flowVars.get("GUARANTEE_QUOTA").toString());
			String Guarantee_TYPE=flowVars.get("GUARANTEE_TYPE").toString();
			if(Guarantee_Quota>20||"2".equals(Guarantee_TYPE)){
			    resutNodeKey.add("-11");//主管副总
			}else{
			    resutNodeKey.add("-14");//风险经理
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
        return resutNodeKey;
    }
	/* (non-Javadoc)
	 * @see com.stooges.platform.iguarantee.service.ApplyinfoService#genSaveBusData(java.util.Map, com.stooges.platform.workflow.model.JbpmFlowInfo)
	 */
	public void genSaveApplyInfoData(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
        //获取主表名称
        String mainTableName = jbpmFlowInfo.getJbpmMainTableName();
        Map<String,Object> mainRecord = dao.saveOrUpdate(mainTableName,postParams,AllConstants.IDGENERATOR_UUID,null);
        Map<String,Object> managemen = dao.saveOrUpdate("PLAT_IGUARANTEE_MANAGEMEN",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println(managemen);
        Map<String,Object> assets = dao.saveOrUpdate("PLAT_IGUARANTEE_ASSETS",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println(assets);
        Map<String,Object> family = dao.saveOrUpdate("PLAT_IGUARANTEE_FAMILY",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println(family);
        System.out.println("family start...");
        saveLine("PLAT_IGUARANTEE_FAMILY","IGUARANTEE_ID","FAMILY_ID",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println("family end.");
        Map<String,Object> DECLARE = dao.saveOrUpdate("PLAT_IGUARANTEE_DECLARE",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println(DECLARE);
        Map<String,Object> incomeexpendit = dao.saveOrUpdate("PLAT_IGUARANTEE_INCOMEEXPENDIT",postParams,AllConstants.IDGENERATOR_UUID,null);
        System.out.println(incomeexpendit);
        String recordId = (String) mainRecord.get(jbpmFlowInfo.getJbpmMainTablePkName());
        recordId=StringUtils.isBlank(recordId)?(String)postParams.get(jbpmFlowInfo.getJbpmMainTablePkName()):recordId;
        recordId=StringUtils.isBlank(recordId)?jbpmFlowInfo.getJbpmMainTableRecordId():recordId;
        jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
	}

    private void saveLine(String tableName,String mainid,String lineid,Map<String,Object> colValues ,int idGenerator, String seqName){
        String linepk=dao.getTableFieldValues(tableName, lineid,new String[]{mainid},new String[]{String.valueOf(colValues.get(mainid))},",");
        linepk=linepk.replaceAll(String.valueOf(colValues.get(lineid)),"");
    	Map<String,Object> family = dao.saveOrUpdate(tableName,colValues,AllConstants.IDGENERATOR_UUID,null);
        System.out.println("Line :"+family);
        Object lineNumber=colValues.get("lineNumbers");
        String lineNumbers[]=lineNumber==null?new String[0]:lineNumber.toString().split(",");
        List<String> cols=dao.findColumnName(tableName);
        for(String line:lineNumbers) {
        	HashMap<String,Object> formData=getFormData(mainid,line,colValues,cols);
        	linepk=linepk.replaceAll(String.valueOf(formData.get(lineid)),"");
        	family = dao.saveOrUpdate(tableName,formData,AllConstants.IDGENERATOR_UUID,null);
            System.out.println("Line "+line+":"+family);
        }
        dao.deleteRecords(tableName,lineid,linepk.split(","));
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
     * 通用设置表单业务数据
     * @param request
     * @param jbpmFlowInfo
     */
    public void genSetApplyInfoData(HttpServletRequest request,JbpmFlowInfo jbpmFlowInfo){
        String recordId =request.getParameter(jbpmFlowInfo.getJbpmMainTablePkName());
        if(StringUtils.isNotBlank(recordId)) {
        	jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
        }
        //获取主表ID
        String mainRecordId = jbpmFlowInfo.getJbpmMainTableRecordId();
        if(StringUtils.isNotEmpty(mainRecordId)){
            String tableName = jbpmFlowInfo.getJbpmMainTableName();
            String pkName =jbpmFlowInfo.getJbpmMainTablePkName();
            Map<String,Object> mainRecord = dao.getRecord(tableName,new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> managemen = dao.getRecord("PLAT_IGUARANTEE_MANAGEMEN",new String[]{pkName},new Object[]{mainRecordId});
//            Map<String,Object> assets = dao.getRecord("PLAT_IGUARANTEE_ASSETS",new String[]{pkName},new Object[]{mainRecordId});
            List<Map<String,Object>> familymembers = dao.getRecords("PLAT_IGUARANTEE_FAMILY",new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> declare = dao.getRecord("PLAT_IGUARANTEE_DECLARE",new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> incomeexpendit = dao.getRecord("PLAT_IGUARANTEE_INCOMEEXPENDIT",new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> liabilities = dao.getRecord("PLAT_IGUARANTEE_LIABILITIES",new String[]{pkName},new Object[]{mainRecordId});
            
            Map<String,Object> finfo = dao.getRecord("PLAT_IGUARANTEE_FINFO",new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> guarantee = dao.getRecord("PLAT_IGUARANTEE_CORPORATE_GUARANTEE",new String[]{pkName},new Object[]{mainRecordId});
            Map<String,Object> immovables = dao.getRecord("PLAT_IGUARANTEE_IMMOVABLES",new String[]{pkName},new Object[]{mainRecordId});
            
            Map<String,Object> person = dao.getRecord("PLAT_IGUARANTEE_CREDIT_PERSON",new String[]{pkName},new Object[]{mainRecordId});
            request.setAttribute(jbpmFlowInfo.getJbpmMainClassName(),mainRecord);
            request.setAttribute("managemen",managemen);
//            request.setAttribute("assets",assets);
            String s=null;
            try {
            	Collections.reverse(familymembers);
				s=Base64.getEncoder().encodeToString(JSON.toJSONString(familymembers).getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
            request.setAttribute("familymembers",s);
            request.setAttribute("incomeexpendit",incomeexpendit);
            request.setAttribute("declare",declare);
            request.setAttribute("liabilities",liabilities);
            request.setAttribute("finfo",finfo);
            request.setAttribute("guarantee",guarantee);
            request.setAttribute("immovables",immovables);
            request.setAttribute("person",person);
            
            String[] field_names= {"UPLOAD_ID_CARD","UPLOAD_ID_CARD_BACK","UPLOAD_RESIDENCE","UPLOAD_ASSET"};
            JSONObject file_attach=new JSONObject();
            for(int i=0;i<field_names.length;i++) {
            	JSONArray files=filelist("PLAT_IGUARANTEE_APPLYINFO",mainRecordId,"attach_"+field_names[i]);
            	file_attach.put(field_names[i], files);
            }
            String f=null;
            try {
				f=Base64.getEncoder().encodeToString(JSON.toJSONString(file_attach).getBytes("UTF-8"));
			} catch (Exception e) {
				e.printStackTrace();
			}
            request.setAttribute("file_attach",f);
        }
    }
    private JSONArray filelist(String busTableName,String busRecordId,String type) {
        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFileUrl");
        String path=attachFilePath.substring(0, attachFilePath.length()-1);
        List<Map<String, Object>> list=fileAttachService.findList(busTableName,busRecordId,type);
        list.forEach(e->{
        	e.put("id",e.get("FILE_ID"));
        	e.put("name",e.get("FILE_NAME"));
        	e.put("dbfilepath",e.get("FILE_PATH"));
			e.put("getfileurl",path+"/"+e.get("FILE_PATH"));
        });
        return JSONArray.parseArray(JSONArray.toJSONString(list));
    }
     public void genSetApplySimpleData(HttpServletRequest request,JbpmFlowInfo jbpmFlowInfo){
         String recordId =request.getParameter(jbpmFlowInfo.getJbpmMainTablePkName());
         if(StringUtils.isNotBlank(recordId)) {
         	jbpmFlowInfo.setJbpmMainTableRecordId(recordId);
         }
         //获取主表ID
         String mainRecordId = jbpmFlowInfo.getJbpmMainTableRecordId();
         if(StringUtils.isNotEmpty(mainRecordId)){
             String tableName = jbpmFlowInfo.getJbpmMainTableName();
             String pkName =jbpmFlowInfo.getJbpmMainTablePkName();
             Map<String,Object> mainRecord = dao.getRecord(tableName,new String[]{pkName},new Object[]{mainRecordId});
//             Map<String,Object> managemen = dao.getRecord("PLAT_IGUARANTEE_MANAGEMEN",new String[]{pkName},new Object[]{mainRecordId});
//             Map<String,Object> assets = dao.getRecord("PLAT_IGUARANTEE_ASSETS",new String[]{pkName},new Object[]{mainRecordId});
//             List<Map<String,Object>> familymembers = dao.getRecords("PLAT_IGUARANTEE_FAMILY",new String[]{pkName},new Object[]{mainRecordId});
//             Map<String,Object> declare = dao.getRecord("PLAT_IGUARANTEE_DECLARE",new String[]{pkName},new Object[]{mainRecordId});
//             Map<String,Object> incomeexpendit = dao.getRecord("PLAT_IGUARANTEE_INCOMEEXPENDIT",new String[]{pkName},new Object[]{mainRecordId});
             request.setAttribute(jbpmFlowInfo.getJbpmMainClassName(),mainRecord);
//             request.setAttribute("managemen",managemen);
//             request.setAttribute("assets",assets);
//             String s=null;
//             try {
// 				s=Base64.getEncoder().encodeToString(JSON.toJSONString(familymembers).getBytes("UTF-8"));
// 			} catch (Exception e) {
// 				e.printStackTrace();
// 			}
//             request.setAttribute("familymembers",s);
//             request.setAttribute("incomeexpendit",incomeexpendit);
//             request.setAttribute("declare",declare);
         }
     }
    public void genCreateApplyInfoFile(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
        //获取主表名称
        String mainTableName = jbpmFlowInfo.getJbpmMainTableName();
        System.out.println(mainTableName);
        String mainid=jbpmFlowInfo.getJbpmMainTableRecordId();
        String mainRecordId =mainid +"_"+jbpmFlowInfo.getJbpmOperingTaskId();
        Map<String,String> nodes=initMap(
        		"-3","B角初审",
        		"-6","业务部长",
        		"-7","风险部部长审批A",
        		"-12","风险部部长审批",
        		"-20","风险经理",
        		"-21","风险部部长",
        		"-24","财务部"
        		);
        //FILE_JSON=[{"dbfilepath":"matter/20190623/402800816b83bebe016b83c7816b001f.jpg","originalfilename":"QQ图片20180820204829.jpg"}]
        String FILE_JSON="[]";
        String type=jbpmFlowInfo.getJbpmExeId();//当前流程实例ID -> workflowid
        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
        attachFilePath=attachFilePath.substring(0, attachFilePath.length()-1);
        String templatePath=null;
        String outPath=null;
        final HashMap<String,Object> params=new HashMap<String,Object>();
        final HashMap<String,List<Map<String,Object>>> dicts=new HashMap<String,List<Map<String,Object>>>();
        dicts.put("POOR_TYPE",dictionaryService.findList("POOR_TYPE", "ASC"));
        dicts.put("CR",dictionaryService.findList("YesOrNo", "ASC"));
        dicts.put("INDIVIDUAL_POOR",dictionaryService.findList("YesOrNo", "ASC"));
        final HashSet<String> amounts=new HashSet<String>();
        amounts.add("GUARANTEE_QUOTA");
        postParams.forEach((k,v)->{
        	params.put(k.toUpperCase(),v);
        	if(dicts.containsKey(k.toUpperCase())) {
        		List<Map<String,Object>> dict=dicts.get(k.toUpperCase());
        		dict.forEach(map->{
        			if(v!=null&&v.equals(map.get("DIC_VALUE"))) {
        				params.put(k.toUpperCase()+"_DISP",map.get("DIC_NAME"));
        			}
        		});
        	}
        	if(amounts.contains(k.toUpperCase())) {
        		params.put(k.toUpperCase()+"_DISP",MoneyUtil.numberToChinese(Double.valueOf(v.toString())));
        	}
        });
        if("2".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||"5".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||
        		"2".equals(jbpmFlowInfo.getJbpmToEndStatus())||"3".equals(jbpmFlowInfo.getJbpmToEndStatus())) {//状态(0:草稿,1:正在办理 2:已办结(正常结束) 3:已办结(审核通过) 4:已办结(审核不通过) 5:已办结(强制终止)
	        if("-3".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成申请书
	        	templatePath=attachFilePath+"/tpl/gt20/委托担保申请书.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/委托担保申请书.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/委托担保申请书.docx\",\"originalfilename\":\"委托担保申请书.docx\"}]";
	        	//TODO : params.put("","");
	        	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }else if("-6".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成申报表
	        	templatePath=attachFilePath+"/tpl/gt20/申报表.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/申报表.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/申报表.docx\",\"originalfilename\":\"申报表.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
//	        }else if("-7".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成评审报告A
//	        	templatePath=attachFilePath+"/tpl/lt20/风险报告（20万以下）.docx";
//	        	outPath=attachFilePath+"/apply/"+type+"/风险报告（20万以下）.docx";
//	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/风险报告（20万以下）.docx\",\"originalfilename\":\"风险报告（20万以下）.docx\"}]";
//	
//	            DocUtil.genFile(templatePath, outPath, params);
//	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }else if("-7".equals(jbpmFlowInfo.getJbpmOperingNodeKey())||"-12".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成评审报告
	        	templatePath=attachFilePath+"/tpl/gt20/风险评审报告（20万以上）.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/风险评审报告（20万以上）.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/风险评审报告（20万以上）.docx\",\"originalfilename\":\"风险评审报告（20万以上）.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }else if("-20".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成担保合同编号,//生成合规性审查表
	        	//编号规则为当前系统日期+系统自动按顺序补全的4位数
	        	//1.查询主表上当天的合同编码的最大值,然后+1生成当前合同编号.2.同步到数据库中.
	        	String sql0="SELECT IGUARANTEE_ID, CONTRACT_ID, INDIVIDUAL_NAME, GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_MANAGER, INDIVIDUAL_MANAGER_NAME,INDIVIDUAL_BANK, INDIVIDUAL_BANK_NAME " + 
	        			"FROM PLAT_IGUARANTEE_CONTRACT WHERE IGUARANTEE_ID='"+mainid+"';";
	        	Map<String,Object> cmap0=dao.getBySql(sql0,null);
	        	Long cnumber=0l;
	        	if(cmap0==null||cmap0.isEmpty()) {
		        	String number=PlatDateTimeUtil.formatDate(new Date(),"yyyyMMdd");
		        	Map<String,Object> cmap=dao.getBySql("SELECT MAX(CONTRACT_ID) CONTRACT_ID FROM PLAT_IGUARANTEE_CONTRACT WHERE CONTRACT_ID LIKE '%"+number+"%'",null);
		        	Object o=cmap.get("CONTRACT_ID")==null?number+"0000":cmap.get("CONTRACT_ID");
		        	cnumber=Long.valueOf(o.toString())+1;
		        	String sql="SELECT IGUARANTEE_ID, INDIVIDUAL_NAME,GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_BANK,D.DIC_NAME INDIVIDUAL_BANK_NAME,T.TASK_REALHANDLERID AS INDIVIDUAL_MANAGER,TASK_REALHANDLERNAME AS INDIVIDUAL_MANAGER_NAME " + 
		        			"FROM PLAT_IGUARANTEE_APPLYINFO A LEFT JOIN JBPM6_TASK T ON A.IGUARANTEE_ID=T.TASK_MAINRECORDID LEFT JOIN PLAT_SYSTEM_DICTIONARY D ON A.INDIVIDUAL_BANK=D.DIC_VALUE " + 
		        			"WHERE T.TASK_FROMNODEKEY IS NULL AND DIC_DICTYPE_CODE='BANK' and IGUARANTEE_ID='"+mainid+"';";
		        	Map<String,Object> contractMap=dao.getBySql(sql,null);
		        	sql="INSERT INTO PLAT_IGUARANTEE_CONTRACT(IGUARANTEE_ID, CONTRACT_ID, INDIVIDUAL_NAME, GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_MANAGER,INDIVIDUAL_MANAGER_NAME, INDIVIDUAL_BANK,INDIVIDUAL_BANK_NAME,CONTRACT_END_TIME)VALUES(" + 
		        			"'"+contractMap.get("IGUARANTEE_ID")+"','"+cnumber+"','"+contractMap.get("INDIVIDUAL_NAME")+"','"+contractMap.get("GUARANTEE_QUOTA")+"','"+
		        			contractMap.get("GUARANTEE_TERM")+"','"+contractMap.get("INDIVIDUAL_MANAGER")+"','"+contractMap.get("INDIVIDUAL_MANAGER_NAME")+"','"+contractMap.get("INDIVIDUAL_BANK")+"','"
		        			+contractMap.get("INDIVIDUAL_BANK_NAME")+"',date_sub(curdate(),interval -"+contractMap.get("GUARANTEE_TERM")+" month))";
		        	dao.executeSql(sql,null);
	        	}else {
	        		String sql="UPDATE PLAT_IGUARANTEE_CONTRACT SET INDIVIDUAL_NAME='"+cmap0.get("INDIVIDUAL_NAME")+"', GUARANTEE_QUOTA='"+cmap0.get("GUARANTEE_QUOTA")
	        		+"', GUARANTEE_TERM='"+cmap0.get("GUARANTEE_TERM")+"', INDIVIDUAL_MANAGER='"+cmap0.get("INDIVIDUAL_MANAGER")+"',INDIVIDUAL_MANAGER_NAME='"+cmap0.get("INDIVIDUAL_MANAGER_NAME")
	        		+"', INDIVIDUAL_BANK='"+cmap0.get("INDIVIDUAL_BANK")+"',INDIVIDUAL_BANK_NAME='"+cmap0.get("INDIVIDUAL_BANK_NAME")+"',CONTRACT_CREATETIME=CURRENT_TIMESTAMP(),"
	        		+ "CONTRACT_END_TIME=CURRENT_TIMESTAMP() WHERE IGUARANTEE_ID='"+mainid+"'";
		        	dao.executeSql(sql,null);
	        	}
	        	
	        	params.put("C_NUMBER",cnumber);
	        	templatePath=attachFilePath+"/tpl/gt20/合规性审查表.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/合规性审查表.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/合规性审查表.docx\",\"originalfilename\":\"合规性审查表.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }else if("-24".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成放款通知书,生成审查审批表
	        	templatePath=attachFilePath+"/tpl/gt20/放款通知书.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/放款通知书.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/放款通知书.docx\",\"originalfilename\":\"放款通知书.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        	/*
	        	templatePath=attachFilePath+"/tpl/gt20/审查审批表.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/审查审批表.docx";
	//        	params.clear();
	        	//add pramas for 审查审批表
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/审查审批表.docx\",\"originalfilename\":\"审查审批表.docx\"}]";
	        	//TODO : params.put("","");
	        	 
	            DocUtil.genFile(templatePath, outPath,params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        	*/
	        }else if("-8".equals(jbpmFlowInfo.getJbpmOperingNodeKey())||"-13".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成担保意向书
	        	templatePath=attachFilePath+"/tpl/gt20/担保意向书.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/担保意向书.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/担保意向书.docx\",\"originalfilename\":\"担保意向书.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }
        }
	}
    public void genCreateApplySimpleFile(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
    	//获取主表名称
        String mainTableName = jbpmFlowInfo.getJbpmMainTableName();
        System.out.println(mainTableName);
        String mainid=jbpmFlowInfo.getJbpmMainTableRecordId();
        String mainRecordId =mainid +"_"+jbpmFlowInfo.getJbpmOperingTaskId();
        Map<String,String> nodes=initMap(
        		"-2","业务部长"
        		);
        //FILE_JSON=[{"dbfilepath":"matter/20190623/402800816b83bebe016b83c7816b001f.jpg","originalfilename":"QQ图片20180820204829.jpg"}]
        String FILE_JSON="[]";
        String type=jbpmFlowInfo.getJbpmExeId();//当前流程实例ID -> workflowid
        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
        attachFilePath=attachFilePath.substring(0, attachFilePath.length()-1);
        String templatePath=null;
        String outPath=null;
        final HashMap<String,Object> params=new HashMap<String,Object>();
        final HashMap<String,List<Map<String,Object>>> dicts=new HashMap<String,List<Map<String,Object>>>();
        dicts.put("POOR_TYPE",dictionaryService.findList("POOR_TYPE", "ASC"));
        dicts.put("CR",dictionaryService.findList("YesOrNo", "ASC"));
        dicts.put("INDIVIDUAL_POOR",dictionaryService.findList("YesOrNo", "ASC"));
        final HashSet<String> amounts=new HashSet<String>();
        amounts.add("GUARANTEE_QUOTA");
        postParams.forEach((k,v)->{
        	params.put(k.toUpperCase(),v);
        	if(dicts.containsKey(k.toUpperCase())) {
        		List<Map<String,Object>> dict=dicts.get(k.toUpperCase());
        		dict.forEach(map->{
        			if(v!=null&&v.equals(map.get("DIC_VALUE"))) {
        				params.put(k.toUpperCase()+"_DISP",map.get("DIC_NAME"));
        			}
        		});
        	}
        	if(amounts.contains(k.toUpperCase())) {
        		params.put(k.toUpperCase()+"_DISP",MoneyUtil.numberToChinese(Double.valueOf(v.toString())));
        	}
        });
    	if("2".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||"5".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||
        		"2".equals(jbpmFlowInfo.getJbpmToEndStatus())||"3".equals(jbpmFlowInfo.getJbpmToEndStatus())) {//状态(0:草稿,1:正在办理 2:已办结(正常结束) 3:已办结(审核通过) 4:已办结(审核不通过) 5:已办结(强制终止)
    		if("-2".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成担保合同编号,生成合规性审查表
	        	//编号规则为当前系统日期+系统自动按顺序补全的4位数
	        	//1.查询主表上当天的合同编码的最大值,然后+1生成当前合同编号.2.同步到数据库中.
	        	String sql0="SELECT IGUARANTEE_ID, CONTRACT_ID, INDIVIDUAL_NAME, GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_MANAGER, INDIVIDUAL_MANAGER_NAME,INDIVIDUAL_BANK, INDIVIDUAL_BANK_NAME " + 
	        			"FROM PLAT_IGUARANTEE_CONTRACT WHERE IGUARANTEE_ID='"+mainid+"';";
	        	Map<String,Object> cmap0=dao.getBySql(sql0,null);
	        	Long cnumber=0l;
	        	if(cmap0==null||cmap0.isEmpty()) {
		        	String number=PlatDateTimeUtil.formatDate(new Date(),"yyyyMMdd");
		        	Map<String,Object> cmap=dao.getBySql("SELECT MAX(CONTRACT_ID) CONTRACT_ID FROM PLAT_IGUARANTEE_CONTRACT WHERE CONTRACT_ID LIKE '%"+number+"%'",null);
		        	Object o=cmap.get("CONTRACT_ID")==null?number+"0000":cmap.get("CONTRACT_ID");
		        	cnumber=Long.valueOf(o.toString())+1;
		        	String sql="SELECT IGUARANTEE_ID, INDIVIDUAL_NAME,GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_BANK,D.DIC_NAME INDIVIDUAL_BANK_NAME,T.TASK_REALHANDLERID AS INDIVIDUAL_MANAGER,TASK_REALHANDLERNAME AS INDIVIDUAL_MANAGER_NAME " + 
		        			"FROM PLAT_IGUARANTEE_APPLYINFO A LEFT JOIN JBPM6_TASK T ON A.IGUARANTEE_ID=T.TASK_MAINRECORDID LEFT JOIN PLAT_SYSTEM_DICTIONARY D ON A.INDIVIDUAL_BANK=D.DIC_VALUE " + 
		        			"WHERE T.TASK_FROMNODEKEY IS NULL AND DIC_DICTYPE_CODE='BANK' and IGUARANTEE_ID='"+mainid+"';";
		        	Map<String,Object> contractMap=dao.getBySql(sql,null);
		        	sql="INSERT INTO PLAT_IGUARANTEE_CONTRACT(IGUARANTEE_ID, CONTRACT_ID, INDIVIDUAL_NAME, GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_MANAGER,INDIVIDUAL_MANAGER_NAME, INDIVIDUAL_BANK,INDIVIDUAL_BANK_NAME,CONTRACT_END_TIME)VALUES(" + 
		        			"'"+contractMap.get("IGUARANTEE_ID")+"','"+cnumber+"','"+contractMap.get("INDIVIDUAL_NAME")+"','"+contractMap.get("GUARANTEE_QUOTA")+"','"+
		        			contractMap.get("GUARANTEE_TERM")+"','"+contractMap.get("INDIVIDUAL_MANAGER")+"','"+contractMap.get("INDIVIDUAL_MANAGER_NAME")+"','"+contractMap.get("INDIVIDUAL_BANK")+"','"
		        			+contractMap.get("INDIVIDUAL_BANK_NAME")+"',date_sub(curdate(),interval -"+contractMap.get("GUARANTEE_TERM")+" month))";
		        	dao.executeSql(sql,null);
	        	}else {
	        		String sql="UPDATE PLAT_IGUARANTEE_CONTRACT SET INDIVIDUAL_NAME='"+cmap0.get("INDIVIDUAL_NAME")+"', GUARANTEE_QUOTA='"+cmap0.get("GUARANTEE_QUOTA")
	        		+"', GUARANTEE_TERM='"+cmap0.get("GUARANTEE_TERM")+"', INDIVIDUAL_MANAGER='"+cmap0.get("INDIVIDUAL_MANAGER")+"',INDIVIDUAL_MANAGER_NAME='"+cmap0.get("INDIVIDUAL_MANAGER_NAME")
	        		+"', INDIVIDUAL_BANK='"+cmap0.get("INDIVIDUAL_BANK")+"',INDIVIDUAL_BANK_NAME='"+cmap0.get("INDIVIDUAL_BANK_NAME")+"',CONTRACT_CREATETIME=CURRENT_TIMESTAMP(),"
	        		+ "CONTRACT_END_TIME=CURRENT_TIMESTAMP() WHERE IGUARANTEE_ID='"+mainid+"'";
		        	dao.executeSql(sql,null);
	        	}
	        	
	        	params.put("C_NUMBER",cnumber);
	        	templatePath=attachFilePath+"/tpl/gt20/合规性审查表.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/合规性审查表.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/合规性审查表.docx\",\"originalfilename\":\"合规性审查表.docx\"}]";
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }
    	}
    }
    private <K> HashMap<K,K> initMap(K...k){
    	HashMap<K,K> map=new HashMap<K,K>();
    	for(int i=0;i<k.length/2;i++) {
    		map.put(k[i*2],k[i*2+1]);
    	}
    	return map;
    }
    public List<Map<String,Object>> findApplyFiles(SqlFilter filter,Map<String,Object> map){
    	//FILE_NAME,FILE_URL,CREATE_DATE
    	String busTableName =filter.getRequest().getParameter("MainTableName");//"PLAT_IGUARANTEE_APPLYINFO";//String.valueOf(map.get("MainTableName"));jbpmExeId
        String busRecordId =filter.getRequest().getParameter("MainTableRecordId");//filter.getRequest().getParameter("EXECUTION_ID");
        String typeKey =filter.getRequest().getParameter("JbpmExeId");//jbpmFlowInfo.getJbpmExeId();//当前流程实例ID -> workflowid
        Map<String, String> taskInfo=jbpmTaskService.getRunningTaskInfo(typeKey);
        String CURRENT_NODEKEYS=taskInfo==null?"":taskInfo.get("CURRENT_NODEKEYS");
        Map<String, Object> user=PlatAppUtil.getBackPlatLoginUser();//
        boolean risk=false;
        if(user.containsKey("ROLECODESET")) {
        	Set<String> ROLECODESET=(Set<String>)user.get("ROLECODESET");
        	if(ROLECODESET!=null&&(ROLECODESET.contains("RiskMinister")||ROLECODESET.contains("RiskManager")||ROLECODESET.contains("adminrole"))) {
        		risk=true;
        	}
        }
        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFileUrl");
        String path=attachFilePath.substring(0, attachFilePath.length()-1);
        List<Map<String, Object>> list = fileAttachService.findList(busTableName, busRecordId, typeKey);
        ArrayList<Boolean> t=new ArrayList<Boolean>();t.add(risk);
        list.forEach(m->{
        	HashMap<String,Object> temp=new HashMap<String,Object>();
        	m.forEach((k,v)->{
        		if("FILE_CREATETIME".equalsIgnoreCase(k)) {
        			temp.put("CREATE_DATE",PlatDateTimeUtil.formatDate((Date)v,"yyyy-MM-dd HH:mm:ss"));
        		}else if("FILE_PATH".equalsIgnoreCase(k)){
        			if(m.get("FILE_NAME").toString().contains("放款通知书")) {
        				if(t.get(0)) {
            				temp.put("FILE_URL",path+"/"+v);
        				}
        			}else {
        				temp.put("FILE_URL",path+"/"+v);
        			}
        		}
        	});
        	m.putAll(temp);
        });
        return list;
    }
    public void confirmFee(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
    	//状态(0:草稿,1:正在办理 2:已办结(正常结束) 3:已办结(审核通过) 4:已办结(审核不通过) 5:已办结(强制终止)
    	 if("2".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||"5".equals(jbpmFlowInfo.getJbpmHandleTaskStatus())||
         		"2".equals(jbpmFlowInfo.getJbpmToEndStatus())||"3".equals(jbpmFlowInfo.getJbpmToEndStatus())) {
    		 Map<String,Object> fee = dao.saveOrUpdate("PLAT_GUARANTEE_FEE",postParams,AllConstants.IDGENERATOR_UUID,null);
    		 System.out.println(fee);
    	 }
    }
}
