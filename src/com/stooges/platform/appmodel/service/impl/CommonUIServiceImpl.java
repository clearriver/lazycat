/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatDbUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.dao.CommonUIDao;
import com.stooges.platform.appmodel.service.CommonUIService;
import com.stooges.platform.appmodel.service.DbManagerService;
import com.stooges.platform.appmodel.service.DesignService;
import com.stooges.platform.appmodel.service.FieldConfigService;
import com.stooges.platform.appmodel.service.FormcontrolService;
import com.stooges.platform.appmodel.service.GenCmpTplService;
import com.stooges.platform.appmodel.service.UicompService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年3月12日 下午2:09:14
 */
@Service("commonUIService")
public class CommonUIServiceImpl extends BaseServiceImpl implements
        CommonUIService {
    /**
     * 所引入的dao
     */
    @Resource
    private CommonUIDao dao;
    /**
     * 
     */
    @Resource
    private FieldConfigService fieldConfigService;
    /**
     * 
     */
    @Resource
    private UicompService uicompService;
    /**
     * 
     */
    @Resource
    private DbManagerService dbManagerService;
    /**
     * 
     */
    @Resource
    private GenCmpTplService genCmpTplService;
    /**
     * 
     */
    @Resource
    private FormcontrolService formcontrolService;
    /**
     * 
     */
    @Resource
    private DesignService designService;
    
    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取配置的布局
     * @param sqlFilter
     * @return
     */
    public List<Map> findDirectLayouts(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String layoutJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"LAYOUT_JSON");
        if(StringUtils.isNotEmpty(layoutJson)){
            return JSON.parseArray(layoutJson, Map.class);
        }else{
            return null;
        }
    }
    
    /**
     * 获取配置的列信息
     * @param sqlFilter
     * @return
     */
    public List<Map> findDatatableColumns(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String columnJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"COLUMN_JSON");
        if(StringUtils.isNotEmpty(columnJson)){
            List<Map> list = JSON.parseArray(columnJson, Map.class);
            for(Map map:list){
                String FORMAT_FN = (String) map.get("FORMAT_FN");
                if(StringUtils.isNotEmpty(FORMAT_FN)){
                    map.put("FORMAT_FN", StringEscapeUtils.escapeHtml3(FORMAT_FN));
                }
            }
            return list;
        }else{
            return null;
        }
    }
    
    /**
     * 获取配置的事件信息
     * @param sqlFilter
     * @return
     */
    public List<Map> findListGroupEvents(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String eventJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"EVENT_JSON");
        if(StringUtils.isNotEmpty(eventJson)){
            List<Map> list = JSON.parseArray(eventJson, Map.class);
            for(Map map:list){
                String CLICK_FN = (String) map.get("CLICK_FN");
                if(StringUtils.isNotEmpty(CLICK_FN)){
                    map.put("CLICK_FN", StringEscapeUtils.escapeHtml3(CLICK_FN));
                }
            }
            return list;
        }else{
            List<Map> list = new ArrayList<Map>();
            Map<String,Object> groupclick = new HashMap<String,Object>();
            groupclick.put("CLICK_FNTYPE","1");
            groupclick.put("CLICK_FNTYPENAME","列表点击事件");
            groupclick.put("CLICK_FNNAME", "operlistclick");
            Map<String,Object> groupclick2 = new HashMap<String,Object>();
            groupclick2.put("CLICK_FNTYPE","2");
            groupclick2.put("CLICK_FNTYPENAME","新增点击事件");
            groupclick2.put("CLICK_FNNAME", "operladdclick");
            Map<String,Object> groupclick3 = new HashMap<String,Object>();
            groupclick3.put("CLICK_FNTYPE","3");
            groupclick3.put("CLICK_FNTYPENAME","删除点击事件");
            groupclick3.put("CLICK_FNNAME", "operldelclick");
            Map<String,Object> groupclick4 = new HashMap<String,Object>();
            groupclick4.put("CLICK_FNTYPE","4");
            groupclick4.put("CLICK_FNTYPENAME","编辑点击事件");
            groupclick4.put("CLICK_FNNAME", "operleditclick");
            list.add(groupclick);
            list.add(groupclick2);
            list.add(groupclick3);
            list.add(groupclick4);
            return list;
        }
    }
    
    /**
     * 获取配置的排序列信息
     * @param sqlFilter
     * @return
     */
    public List<Map> findDatatableOrders(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String orderJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"ORDERFIELD_JSON");
        if(StringUtils.isNotEmpty(orderJson)){
            return JSON.parseArray(orderJson, Map.class);
        }else{
            return null;
        }
    }
    
    /**
     * 保存 jq树形表格基本配置信息
     * @param sqlFilter
     * @param fieldInfo
     */
    public void saveJqTreeTableBaseConfig(SqlFilter sqlFilter,Map<String,Object> fieldInfo){
      //获取是否需要初始化
        String IS_INITCOL = (String) fieldInfo.get("IS_INITCOL");
        String FORMCONTROL_ID = (String) fieldInfo.get("FORMCONTROL_ID");
        if(IS_INITCOL.equals("1")){
            String TREE_TABLENAME = (String) fieldInfo.get("TREE_TABLENAME");
            String ID_ANDNAME = (String) fieldInfo.get("ID_ANDNAME");
            String TARGET_COLS = (String) fieldInfo.get("TARGET_COLS");
            String[] idAndName = ID_ANDNAME.split(",");
            //定义ID列的名称
            String idColumnName = idAndName[0];
            String idColumnComment = this.getColComment(TREE_TABLENAME, idColumnName);
            Map<String,Object> idColumnMap  = new HashMap<String,Object>();
            idColumnMap.put("FIELD_NAME", idColumnName);
            idColumnMap.put("FIELD_COMMENT", idColumnComment);
            //定义NAME列的名称
            String nameColumnName = idAndName[1];
            String nameColumnComment = this.getColComment(TREE_TABLENAME, nameColumnName);
            Map<String,Object> nameColumnMap  = new HashMap<String,Object>();
            nameColumnMap.put("FIELD_NAME", nameColumnName);
            nameColumnMap.put("FIELD_COMMENT", nameColumnComment);
            List<Map<String,Object>> columnList = new ArrayList<Map<String,Object>>();
            columnList.add(idColumnMap);
            columnList.add(nameColumnMap);
            for(String targetCol:TARGET_COLS.split(",")){
                String targetColComment = this.getColComment(TREE_TABLENAME, targetCol);
                Map<String,Object> targetColumn  = new HashMap<String,Object>();
                targetColumn.put("FIELD_NAME", targetCol);
                targetColumn.put("FIELD_COMMENT", targetColComment);
                columnList.add(targetColumn);
            }
            saveTableColumnList(FORMCONTROL_ID, columnList);
        }
    }
    
    /**
     * 保存数据表格基本配置信息
     * @param sqlFilter
     * @param fieldInfo
     */
    public void saveDataTableBaseConfig(SqlFilter sqlFilter,Map<String,Object> fieldInfo){
        //获取是否需要初始化
        String IS_INITCOL = (String) fieldInfo.get("IS_INITCOL");
        String FORMCONTROL_ID = (String) fieldInfo.get("FORMCONTROL_ID");
        if(IS_INITCOL.equals("1")){
            String DATA_TYPE = (String) fieldInfo.get("DATA_TYPE");
            List<Map<String,Object>> columnList = null;
            if(DATA_TYPE.equals("1")){
                String SQL_CONTENT = (String) fieldInfo.get("SQL_CONTENT");
                SQL_CONTENT = SQL_CONTENT.toUpperCase();
                SQL_CONTENT = PlatDbUtil.transSqlContent(SQL_CONTENT, null, null);
                //获取后台登录用户
                Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
                Map<String,Object> root = new HashMap<String,Object>();
                root.put(AllConstants.BACKPLAT_USERSESSIONKEY, backLoginUser);
                SQL_CONTENT = PlatStringUtil.getFreeMarkResult(SQL_CONTENT, root);
                columnList = dbManagerService.findFieldColumnsBySql(SQL_CONTENT);
            }else if(DATA_TYPE.equals("2")){
                columnList = dbManagerService.findFieldColumnsByInterface(fieldInfo, sqlFilter);
            }
            saveTableColumnList(FORMCONTROL_ID, columnList);
        }
    }

    /**
     * 保存表格列配置信息
     * @param FORMCONTROL_ID
     * @param columnList
     */
    private void saveTableColumnList(String FORMCONTROL_ID,
            List<Map<String, Object>> columnList) {
        if(columnList!=null&&columnList.size()>0){
            for(int i = 0;i<columnList.size();i++){
                Map<String,Object> colInfo = columnList.get(i);
                colInfo.put("FIELD_WIDTH","100");
                colInfo.put("FIELD_ISHIDE","-1");
                colInfo.put("FIELD_SORT","-1");
            }
            Map<String,Object> oldFieldConfig = fieldConfigService.getRecord("PLAT_APPMODEL_FIELDCONFIG",
                    new String[]{"FIELDCONFIG_FORMCONTROLID","FIELDCONFIG_FIELDNAME"},
                    new Object[]{FORMCONTROL_ID,"COLUMN_JSON"});
            if(oldFieldConfig!=null){
                String OLD_COLUMN_JSON = (String) oldFieldConfig.get("FIELDCONFIG_FIELDVALUE");
                List<Map> oldColumnList = JSON.parseArray(OLD_COLUMN_JSON, Map.class);
                for(Map<String,Object> map:columnList){
                    String FIELD_NAME = (String) map.get("FIELD_NAME");
                    for(Map<String,Object> oldMap:oldColumnList){
                        if(oldMap.get("FIELD_NAME").toString().equals(FIELD_NAME)){
                            map.putAll(oldMap);
                        }
                    }
                }
            }
            String COLUMN_JSON = JSON.toJSONString(columnList);
            if(oldFieldConfig==null){
                oldFieldConfig = new HashMap<String,Object>();
                oldFieldConfig.put("FIELDCONFIG_FORMCONTROLID",FORMCONTROL_ID);
                oldFieldConfig.put("FIELDCONFIG_CONFTYPE","attach");
            }
            oldFieldConfig.put("FIELDCONFIG_FIELDNAME","COLUMN_JSON");
            oldFieldConfig.put("FIELDCONFIG_FIELDVALUE",COLUMN_JSON);
            this.fieldConfigService.saveOrUpdate("PLAT_APPMODEL_FIELDCONFIG",oldFieldConfig,
                    AllConstants.IDGENERATOR_UUID,null);
        }
    }
    
    /**
     * 根据表单控件ID和通用TPLID获取按钮的模版代码
     * @param FORMCONTROL_ID
     * @param GENCMPTPL_ID
     * @return
     */
    public String getJqgridBtnTplCode(String FORMCONTROL_ID,String GENCMPTPL_ID){
        Map<String,Object> genCmpTpl = genCmpTplService.getRecord("PLAT_APPMODEL_GENCMPTPL"
                ,new String[]{"GENCMPTPL_ID"},new Object[]{GENCMPTPL_ID});
        String GENCMPTPL_CODE = (String) genCmpTpl.get("GENCMPTPL_CODE");
        Map<String,Object> formControl = formcontrolService.getRecord("PLAT_APPMODEL_FORMCONTROL",
                new String[]{"FORMCONTROL_ID"},new Object[]{FORMCONTROL_ID});
        Map<String,Object> root = new HashMap<String,Object>();
        if(formControl!=null){
            String designId = (String) formControl.get("FORMCONTROL_DESIGN_ID");
            List<Map> assoicalTableList = designService.findAssoicalTables(designId);
            String jqtableId = fieldConfigService.getFieldValue(FORMCONTROL_ID, "CONTROL_ID");
            root.put("jqtableId", jqtableId);
            if(assoicalTableList!=null&&assoicalTableList.size()>0){
                Map<String,Object> assoicalTable = designService.findAssoicalTables(designId).get(0);
                root.putAll(assoicalTable);
                String TABLE_NAME = (String) assoicalTable.get("TABLE_NAME");
                String TABLE_PKNAME = this.findPrimaryKeyNames(TABLE_NAME).get(0);
                root.put("TABLE_PKNAME", TABLE_PKNAME);
            }
        }
        String tplCode = PlatStringUtil.getFreeMarkResult(GENCMPTPL_CODE, root);
        return tplCode;
    }
    
    /**
     * 获取表格的数据列表
     * @param filter
     * @param IS_PAGE
     * @param formControlId
     * @return
     */
    public List<Map<String,Object>> findTableDatas(SqlFilter filter,String IS_PAGE,String formControlId){
        //获取默认排序值
        String ORDERFIELD_JSON = fieldConfigService.getFieldValue(formControlId, "ORDERFIELD_JSON");
        if(StringUtils.isNotEmpty(ORDERFIELD_JSON)){
            List<Map> orderFields = JSON.parseArray(ORDERFIELD_JSON,Map.class);
            //先获取当前的查询参数
            Map<String, String> orderParams = filter.getOrderParams();
            for(Map<String,Object> map:orderFields){
                String ORDER_FIELD = (String) map.get("ORDER_FIELD");
                String ORDER_TYPE = (String) map.get("ORDER_TYPE");
                if(orderParams.get("O_"+ORDER_FIELD)==null){
                    filter.addFilter("O_"+ORDER_FIELD,ORDER_TYPE,SqlFilter.FILTER_TYPE_ORDER);
                }
            }
        }
        Map<String,Object> queryParams = PlatBeanUtil.getMapFromRequest(filter.getRequest());
        List params = new ArrayList();
        boolean isUserSqlFilter = true;
        String sqlContent = fieldConfigService.getFieldValue(formControlId, "SQL_CONTENT");
        if(sqlContent.contains("#{")){
            isUserSqlFilter = false;
        }
        sqlContent = PlatDbUtil.transSqlContent(sqlContent,queryParams,params);
        //获取后台登录用户
        Map<String,Object> backLoginUser = PlatAppUtil.getBackPlatLoginUser();
        Map<String,Object> root = new HashMap<String,Object>();
        root.put(AllConstants.BACKPLAT_USERSESSIONKEY, backLoginUser);
        sqlContent = PlatStringUtil.getFreeMarkResult(sqlContent, root);
        String exeSql = null;
        if(isUserSqlFilter){
            StringBuffer sql = new StringBuffer(sqlContent);
            exeSql = dao.getQuerySql(filter, sql.toString(), params);
        }else{
            exeSql = sqlContent;
        }
        if(IS_PAGE.equals("-1")){
            return dao.findBySql(exeSql,
                    params.toArray(), null);
        }else{
            return dao.findBySql(exeSql,
                    params.toArray(), filter.getPagingBean());
        }
    }
    
    /**
     * 通用的获取树形下拉框数据源列表
     * @param paramsConfig 参数配置
     * 参数例子:TABLE_NAME指的是表名,TREE_IDANDNAMECOL指的是作为下拉树形的ID和NAME的，
     * TREE_QUERYFIELDS指的是额外需要查询的列表,FILTERS指的是作为过滤的查询条件
     * [TABLE_NAME:PLAT_APPMODEL_MODULE],[TREE_IDANDNAMECOL:MODULE_ID,MODULE_NAME],
     * [TREE_QUERYFIELDS:MODULE_CODE,MODULE_PARENTID],[FILTERS:MODULE_NAME_LIKE|测试,MODULE_TREESN_EQ|3]
     * @return
     */
    public List<Map<String,Object>> findGenTreeSelectorDatas(String paramsConfig){
        Map<String,String> treeConfig = new HashMap<String,String>();
        Pattern p = Pattern.compile("\\[(.*?)]");
        Matcher m = p.matcher(paramsConfig);
        while (m.find()) {
            String config =m.group(1);
            String paramName = config.split(":")[0];
            String paramValue = config.split(":")[1];
            treeConfig.put(paramName, paramValue);
        } 
        StringBuffer sql = new StringBuffer("SELECT ");
        String TABLE_NAME = treeConfig.get("TABLE_NAME");
        String SHOW_ROOT = treeConfig.get("SHOW_ROOT");
        if(StringUtils.isEmpty(SHOW_ROOT)){
            SHOW_ROOT = "true";
        }
        String entityName = TABLE_NAME.substring(TABLE_NAME.lastIndexOf("_")+1, TABLE_NAME.length());
        String TREE_SN_NAME = entityName+"_TREESN";
        String PARENTID_NAME = entityName+"_PARENTID";
        String TREELEVE_NAME = entityName+"_LEVEL";
        String TREE_IDANDNAMECOL = treeConfig.get("TREE_IDANDNAMECOL");
        sql.append(TREE_IDANDNAMECOL);
        String TREE_QUERYFIELDS = treeConfig.get("TREE_QUERYFIELDS");
        if(StringUtils.isNotEmpty(TREE_QUERYFIELDS)){
            sql.append(",").append(TREE_QUERYFIELDS);
            if(!TREE_QUERYFIELDS.contains(TREELEVE_NAME)){
                sql.append(",").append(TREELEVE_NAME);
            }
        }
        sql.append(" FROM ").append(TABLE_NAME);
        sql.append(" WHERE ");
        StringBuffer preSql = new StringBuffer(sql.toString());
        String FILTERS = treeConfig.get("FILTERS");
        String[] filterArray = FILTERS.split(",");
        List<Object> params = new ArrayList<Object>();
        //定义加载根ID
        String loadRootId = "";
        for(int i=0;i<filterArray.length;i++){
            String fieldName = filterArray[i].split("[|]")[0];
            String fieldValue = filterArray[i].split("[|]")[1];
            if(i>0){
                sql.append(" AND ");
            }
            // 获取到字段的名称
            String columnName = fieldName.substring(0,fieldName.lastIndexOf("_"));
            // 获取到字段的查询条件
            String condition = fieldName.substring(fieldName.lastIndexOf("_") + 1,
                    fieldName.length());
            if(columnName.equals(PARENTID_NAME)){
                loadRootId = fieldValue;
            }
            constructSqlAndQueryParams(sql, params, fieldValue, columnName,
                    condition);
        }
        sql.append(" ORDER BY ").append(TREE_SN_NAME).append(" ASC ");
        List<Map<String,Object>> treeList = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> topList = dao.findBySql(sql.toString(), params.toArray(), null);
        for(Map<String,Object> top:topList){
            top.put("VALUE", top.get(TREE_IDANDNAMECOL.split(",")[0]));
            top.put("LABEL", top.get(TREE_IDANDNAMECOL.split(",")[1]));
            top.put("TREE_LEVEL",Integer.parseInt(top.get(TREELEVE_NAME).toString())); 
            top.put("level", Integer.parseInt(top.get(TREELEVE_NAME).toString()));
            top.put("parent",top.get(PARENTID_NAME));
            top.put("expanded",false);
            String parentId = (String) top.get(TREE_IDANDNAMECOL.split(",")[0]);
            treeList.add(top);
            this.addTreeSelectChildren(treeList, parentId, preSql, filterArray, 
                    PARENTID_NAME,TREE_SN_NAME,TREELEVE_NAME,TREE_IDANDNAMECOL,top);
        }
        if(!loadRootId.equals("0")&&SHOW_ROOT.equals("true")){
            String pkName = dao.findPrimaryKeyNames(TABLE_NAME).get(0);
            Map<String,Object> rootNode = dao.getRecord(TABLE_NAME,
                    new String[]{pkName},new Object[]{loadRootId});
            List<Map<String,Object>> rootList = new ArrayList<Map<String,Object>>();
            rootNode.put("VALUE", rootNode.get(TREE_IDANDNAMECOL.split(",")[0]));
            rootNode.put("LABEL", rootNode.get(TREE_IDANDNAMECOL.split(",")[1]));
            rootNode.put("TREE_LEVEL",Integer.parseInt(rootNode.get(TREELEVE_NAME).toString())); 
            rootNode.put("level", Integer.parseInt(rootNode.get(TREELEVE_NAME).toString()));
            rootNode.put("parent",rootNode.get(PARENTID_NAME));
            rootNode.put("expanded",true);
            rootList.add(rootNode);
            rootList.addAll(treeList);
            treeList = PlatUICompUtil.getTreeSelectDatas(rootList);
        }else{
            treeList = PlatUICompUtil.getTreeSelectDatas(treeList);
        }
        return treeList;
    }
    
    /**
     * 
     * @param treeList
     * @param parentId
     * @param preSql
     * @param filterArray
     * @param parentIdName
     * @param treeSnName
     * @param TREELEVE_NAME
     * @param TREE_IDANDNAMECOL
     */
    private void addTreeSelectChildren(List<Map<String,Object>> treeList,String parentId,
            StringBuffer preSql,String[] filterArray,String parentIdName,
            String treeSnName,String TREELEVE_NAME,String TREE_IDANDNAMECOL,Map<String,Object> parentInfo){
        StringBuffer sql = new StringBuffer(preSql);
        sql.append(parentIdName).append("=?");
        List<Object> params = new ArrayList<Object>();
        params.add(parentId);
        List<String> filterParamArray = new ArrayList<String>();
        for(int i=0;i<filterArray.length;i++){
            String fieldName = filterArray[i].split("[|]")[0];
            if(!fieldName.contains("PARENTID")){
                filterParamArray.add(filterArray[i]);
            }
        }
        if(filterParamArray.size()>0){
            sql.append(" AND ");
        }
        for(int i=0;i<filterParamArray.size();i++){
            String fieldName = filterParamArray.get(i).split("[|]")[0];
            String fieldValue = filterParamArray.get(i).split("[|]")[1];
            if(i>0){
                sql.append(" AND ");
            }
            // 获取到字段的名称
            String columnName = fieldName.substring(0,fieldName.lastIndexOf("_"));
            // 获取到字段的查询条件
            String condition = fieldName.substring(fieldName.lastIndexOf("_") + 1,
                    fieldName.length());
            constructSqlAndQueryParams(sql, params, fieldValue, columnName,
                    condition);
        }
        sql.append(" ORDER BY ").append(treeSnName).append(" ASC ");
        List<Map<String,Object>> children = dao.findBySql(sql.toString(), params.toArray(), null);
        if(children!=null&&children.size()>0){
            parentInfo.put("isLeaf", false);
        }else{
            parentInfo.put("isLeaf", true);
        }
        for(Map<String,Object> child:children){
            child.put("VALUE", child.get(TREE_IDANDNAMECOL.split(",")[0]));
            child.put("LABEL", child.get(TREE_IDANDNAMECOL.split(",")[1]));
            child.put("TREE_LEVEL", Integer.parseInt(child.get(TREELEVE_NAME).toString())); 
            child.put("level", Integer.parseInt(child.get(TREELEVE_NAME).toString()));
            child.put("parent",child.get(parentIdName));
            child.put("expanded",false);
            String childId = (String) child.get(TREE_IDANDNAMECOL.split(",")[0]);
            treeList.add(child);
            this.addTreeSelectChildren(treeList, childId, preSql, filterArray,
                    parentIdName,treeSnName,TREELEVE_NAME,TREE_IDANDNAMECOL,child);
            
        }
    }

    /**
     * @param sql
     * @param params
     * @param fieldValue
     * @param columnName
     * @param condition
     */
    private void constructSqlAndQueryParams(StringBuffer sql,
            List<Object> params, String fieldValue, String columnName,
            String condition) {
        if(condition.equals("EQ")){
            condition = "=";
        }else if(condition.equals("NEQ")){
            condition = "!=";
        }else if(condition.equals("LT")){
            condition = "<";
        }else if(condition.equals("GT")){
            condition = ">";
        }else if(condition.equals("LE")){
            condition = "<=";
        }else if(condition.equals("GE")){
            condition = ">=";
        }
        sql.append(columnName);
        if (condition.equals("LIKE")) {
            sql.append(" LIKE ? ESCAPE '\\' ");
            fieldValue = fieldValue.replace("_", "\\_");
            params.add("%"+fieldValue+"%");
        } else if (condition.equals("LLIKE")) {
            sql.append(" LIKE ? ESCAPE '\\' ");
            fieldValue = fieldValue.replace("_", "\\_");
            params.add("%"+fieldValue);
        } else if (condition.equals("RLIKE")) {
            sql.append(" LIKE ? ESCAPE '\\' ");
            fieldValue = fieldValue.replace("_", "\\_");
            params.add(fieldValue+"%");
        } else if(condition.equals("IN")){
            sql.append(" in (");
            String[] newValues = fieldValue.split(",");
            for(String newValue:newValues){
                sql.append("?,");
                params.add(newValue);
            }
            sql.deleteCharAt(sql.length()-1);
            sql.append(") ");
        }else if(condition.equals("NULL")){
            sql.append(" is null ");
        }else if(condition.equals("NOTNULL")){
            sql.append(" is not null ");
        }else if(condition.equals("APPEND")){
             sql.append(" ").append(fieldValue).append(" ");
        } else {
            sql.append(" ").append(condition).append("?").append(" ");
            params.add(fieldValue);
        }
    }
    
    /**
     * 根据sqlFilter获取页签配置信息列表
     * @param sqlFilter
     * @return
     */
    public List<Map> findTabConfigList(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String tabConfigJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"tabConfigJson");
        if(StringUtils.isNotEmpty(tabConfigJson)){
            List<Map> tabConfigList = JSON.parseArray(tabConfigJson, Map.class);
            for(Map tabconfig:tabConfigList){
                String SUBTAB_CLICKCONTENT = (String) tabconfig.get("SUBTAB_CLICKCONTENT");
                tabconfig.put("SUBTAB_CLICKCONTENT", StringEscapeUtils.escapeHtml3(SUBTAB_CLICKCONTENT));
            }
            return tabConfigList;
        }else{
            return null;
        }
    }
    
    /**
     * 根据sqlFilter获取步骤配置列表
     * @param sqlFilter
     * @return
     */
    public List<Map> findWizardConfigList(SqlFilter sqlFilter){
        String FORMCONTROL_ID = sqlFilter.getRequest().getParameter("FORMCONTROL_ID");
        String wizardstepJson = fieldConfigService.getFieldValue(FORMCONTROL_ID,"wizardstepJson");
        if(StringUtils.isNotEmpty(wizardstepJson)){
            List<Map> wizardConfigList = JSON.parseArray(wizardstepJson, Map.class);
            for(Map config:wizardConfigList){
                String STEPNEXT_FNCONTENT = (String) config.get("STEPNEXT_FNCONTENT");
                config.put("STEPNEXT_FNCONTENT", StringEscapeUtils.escapeHtml3(STEPNEXT_FNCONTENT));
            }
            return wizardConfigList;
        }else{
            return null;
        }
    }
    

}
