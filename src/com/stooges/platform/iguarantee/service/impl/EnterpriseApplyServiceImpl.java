/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.iguarantee.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
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
import com.stooges.platform.iguarantee.dao.EnterpriseApplyDao;
import com.stooges.platform.iguarantee.service.EnterpriseApplyService;
import com.stooges.platform.system.service.DictionaryService;
import com.stooges.platform.workflow.model.JbpmFlowInfo;
import com.stooges.platform.workflow.service.JbpmTaskService;

/**
 * 描述 企业委托担保申请信息表业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-08-05 00:18:35
 */
@Service("enterpriseApplyService")
public class EnterpriseApplyServiceImpl extends BaseServiceImpl implements EnterpriseApplyService {
    @Resource
    private EnterpriseApplyDao dao;

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
    private <K> HashMap<K,K> initMap(K...k){
    	HashMap<K,K> map=new HashMap<K,K>();
    	for(int i=0;i<k.length/2;i++) {
    		map.put(k[i*2],k[i*2+1]);
    	}
    	return map;
    }
	@Override
	public void genSaveEnterpriseApplyData(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
        //获取主表名称
        String mainTableName = jbpmFlowInfo.getJbpmMainTableName();
        Map<String,Object> mainRecord = dao.saveOrUpdate(mainTableName,postParams,AllConstants.IDGENERATOR_UUID,null);
        String recordId = (String) mainRecord.get(jbpmFlowInfo.getJbpmMainTablePkName());
        recordId=StringUtils.isBlank(recordId)?(String)postParams.get(jbpmFlowInfo.getJbpmMainTablePkName()):recordId;
        recordId=StringUtils.isBlank(recordId)?jbpmFlowInfo.getJbpmMainTableRecordId():recordId;
        jbpmFlowInfo.setJbpmMainTableRecordId(recordId);	
	} 
	@Override
	public void genCreateEnterpriseApplyFile(Map<String, Object> postParams, JbpmFlowInfo jbpmFlowInfo) {
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
	        if("-2".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成申请书
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
	        }else if("-10".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成评审报告
	        	templatePath=attachFilePath+"/tpl/gt20/风险评审报告（20万以上）.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/风险评审报告（20万以上）.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/风险评审报告（20万以上）.docx\",\"originalfilename\":\"风险评审报告（20万以上）.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }else if("-14".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成担保合同编号,//生成合规性审查表
	        	//编号规则为当前系统日期+系统自动按顺序补全的4位数
	        	//1.查询主表上当天的合同编码的最大值,然后+1生成当前合同编号.2.同步到数据库中.
	        	String sql0="SELECT ENTERPRISE_APPLY_ID, ENTERPRISE_NAME, ENTERPRISE_CODE, ENTERPRISE_TYPE, ADDRESS, START_DATE, REGISTERED_CAPITAL, LEGAL_REPRESENTATIVE, IDENTITY_NUMBER, MARRIAGE, PHONE, BANK, BANK_ACCOUNT, HEADCOUNT, MAJOR_SHAREHOLDERS, SHARES_HELD, GUARANTEE_QUOTA, GUARANTEE_TERM, CO_BANK, GUARANTEE_USE, GUARANTEE_SOURCE, REPAYMENT_PLAN, GUARANTEE_GTYPE, GUARANTEE_CREATETIME, FROM_SIMPLE, MAIN_BUSINESS, LAST_TOTAL_ASSET, LAST_TOTAL_LIABILITY, LAST_LIABILITY_RATIO, INDUSTRY_TYPE, LAST_INCOME, RECENT_SALES_REVENUE, RECENT_PROFIT, SURVEY_SUMMARY, SURVEY_DATE, SURVEY_ADDRESS, LICENSE_COPY, ARTICLES_ASSOCIATION, FINANCIAL_STATEMENTS, FINANCIAL_STATEMENTS2, `LEGAL_ID CARD`, DIRECTORS_RESOLUTION, ENTERPRISE_SITUATION, OPERATIONAL_SITUATION, RISK_LEVEL, GUARANTEE_RATE, GUARANTEE_TYP, GUARANTEE_GUARANTEE_DATE, GUARANTEE_APPROVAL " + 
	        			"FROM PLAT_IGUARANTEE_ENTERPRISE_APPLY WHERE ENTERPRISE_APPLY_ID='"+mainid+"';";
	        	Map<String,Object> cmap0=dao.getBySql(sql0,null);
	        	Long cnumber=0l;
	        	if(cmap0==null||cmap0.isEmpty()) {
		        	String number=PlatDateTimeUtil.formatDate(new Date(),"yyyyMMdd");
		        	Map<String,Object> cmap=dao.getBySql("SELECT MAX(CONTRACT_ID) CONTRACT_ID FROM PLAT_IGUARANTEE_CONTRACT WHERE CONTRACT_ID LIKE '%"+number+"%'",null);
		        	Object o=cmap.get("CONTRACT_ID")==null?number+"0000":cmap.get("CONTRACT_ID");
		        	cnumber=Long.valueOf(o.toString())+1;
		        	String sql="SELECT ENTERPRISE_APPLY_ID, ENTERPRISE_NAME,GUARANTEE_QUOTA, GUARANTEE_TERM, CO_BANK,D.DIC_NAME CO_BANK_NAME,T.TASK_REALHANDLERID AS INDIVIDUAL_MANAGER,TASK_REALHANDLERNAME AS INDIVIDUAL_MANAGER_NAME " + 
		        			"FROM PLAT_IGUARANTEE_ENTERPRISE_APPLY A LEFT JOIN JBPM6_TASK T ON A.ENTERPRISE_APPLY_ID=T.TASK_MAINRECORDID LEFT JOIN PLAT_SYSTEM_DICTIONARY D ON A.CO_BANK=D.DIC_VALUE " + 
		        			"WHERE T.TASK_FROMNODEKEY IS NULL AND DIC_DICTYPE_CODE='BANK' and ENTERPRISE_APPLY_ID='"+mainid+"';";
		        	Map<String,Object> contractMap=dao.getBySql(sql,null);
		        	sql="INSERT INTO PLAT_IGUARANTEE_CONTRACT(IGUARANTEE_ID, CONTRACT_ID, INDIVIDUAL_NAME, GUARANTEE_QUOTA, GUARANTEE_TERM, INDIVIDUAL_MANAGER,INDIVIDUAL_MANAGER_NAME, INDIVIDUAL_BANK,INDIVIDUAL_BANK_NAME,CONTRACT_END_TIME,GUARANTEE_TYPE)VALUES(" + 
		        			"'"+contractMap.get("ENTERPRISE_APPLY_ID")+"','"+cnumber+"','"+contractMap.get("ENTERPRISE_NAME")+"','"+contractMap.get("GUARANTEE_QUOTA")+"','"+
		        			contractMap.get("GUARANTEE_TERM")+"','"+contractMap.get("INDIVIDUAL_MANAGER")+"','"+contractMap.get("INDIVIDUAL_MANAGER_NAME")+"','"+contractMap.get("CO_BANK")+"','"
		        			+contractMap.get("CO_BANK_NAME")+"',date_sub(curdate(),interval -"+contractMap.get("GUARANTEE_TERM")+" month),1)";
		        	dao.executeSql(sql,null);
	        	}else {
	        		String sql="UPDATE PLAT_IGUARANTEE_CONTRACT SET INDIVIDUAL_NAME='"+cmap0.get("ENTERPRISE_NAME")+"', GUARANTEE_QUOTA='"+cmap0.get("GUARANTEE_QUOTA")
	        		+"', GUARANTEE_TERM='"+cmap0.get("GUARANTEE_TERM")+"', INDIVIDUAL_MANAGER='"+cmap0.get("INDIVIDUAL_MANAGER")+"',INDIVIDUAL_MANAGER_NAME='"+cmap0.get("INDIVIDUAL_MANAGER_NAME")
	        		+"', INDIVIDUAL_BANK='"+cmap0.get("CO_BANK")+"',INDIVIDUAL_BANK_NAME='"+cmap0.get("CO_BANK_NAME")+"',CONTRACT_CREATETIME=CURRENT_TIMESTAMP(),"
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
	        }else if("-18".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成放款通知书,生成审查审批表
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
	        }else if("-12".equals(jbpmFlowInfo.getJbpmOperingNodeKey())||"-13".equals(jbpmFlowInfo.getJbpmOperingNodeKey())) {//生成担保意向书
	        	templatePath=attachFilePath+"/tpl/gt20/担保意向书.docx";
	        	outPath=attachFilePath+"/apply/"+type+"/担保意向书.docx";
	        	FILE_JSON="[{\"dbfilepath\":\"apply/"+type+"/担保意向书.docx\",\"originalfilename\":\"担保意向书.docx\"}]";
	        	//TODO : params.put("","");
	
	            DocUtil.genFile(templatePath, outPath, params);
	        	fileAttachService.saveFileAttachs(FILE_JSON,mainTableName,mainRecordId,type);
	        }
        }
	}
    public List<Map<String,Object>> findApplyFiles(SqlFilter filter,Map<String,Object> map){
    	//FILE_NAME,FILE_URL,CREATE_DATE
    	String busTableName =filter.getRequest().getParameter("MainTableName");//"PLAT_IGUARANTEE_APPLYINFO";//String.valueOf(map.get("MainTableName"));jbpmExeId
        String busRecordId =filter.getRequest().getParameter("MainTableRecordId");//filter.getRequest().getParameter("EXECUTION_ID");
        String typeKey =filter.getRequest().getParameter("JbpmExeId");//jbpmFlowInfo.getJbpmExeId();//当前流程实例ID -> workflowid
//        Map<String, String> taskInfo=jbpmTaskService.getRunningTaskInfo(typeKey);
//        String CURRENT_NODEKEYS=taskInfo==null?"":taskInfo.get("CURRENT_NODEKEYS");
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
