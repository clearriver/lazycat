/*
 * Copyright (c) 2005, 2017, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.model.TableColumn;
import com.stooges.core.model.TableInfo;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatUICompUtil;
import com.stooges.platform.appmodel.service.DbManagerService;
import com.stooges.platform.common.controller.BaseController;
import com.stooges.platform.common.service.CommonService;

/**
 * 描述
 * @author 胡裕
 * @created 2017年1月25日 上午9:32:52
 */
@Controller
@RequestMapping("/appmodel/DbManagerController")
public class DbManagerController extends BaseController {
    /**
     * 
     */
    @Resource
    private DbManagerService dbManagerService;
    /**
     * 
     */
    @Resource
    private CommonService commonService;
    
    /**
     * 克隆数据库表
     * @param request
     * @param response
     */
    @RequestMapping(params = "cloneTable")
    public void cloneTable(HttpServletRequest request,
            HttpServletResponse response) {
        SqlFilter sqlFilter = new SqlFilter(request);
        //新的表注释
        String NEW_TABLECOMMENTS = request.getParameter("NEW_TABLECOMMENTS");
        //新的表名称
        String NEW_TABLENAME = request.getParameter("NEW_TABLENAME");
        NEW_TABLENAME = NEW_TABLENAME.toUpperCase();
        //获取新表的后缀
        String newSuffix = NEW_TABLENAME.substring(NEW_TABLENAME.lastIndexOf("_")+1, NEW_TABLENAME.length());
        List<Map> oldTableColumns = dbManagerService.findTableColumnByFilter(sqlFilter);
        for(Map oldColumn:oldTableColumns){
            String columnName = (String) oldColumn.get("columnName");
            String oldSuffix = null;
            if(columnName.contains("_")){
                oldSuffix = columnName.substring(columnName.lastIndexOf("_"), 
                        columnName.length());
            }else{
                oldSuffix = "_"+columnName;
            }
            String newTableColumName = newSuffix+oldSuffix;
            oldColumn.put("columnName", newTableColumName);
        }
        String newColumnJson = JSON.toJSONString(oldTableColumns);
        List<TableColumn> tableColumns= JSON.parseArray(newColumnJson,TableColumn.class);
        dbManagerService.saveOrUpdateTable("false", NEW_TABLENAME,
                NEW_TABLECOMMENTS, tableColumns,null);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        result.put("msg", "操作成功!");
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 保存或者更新表格
     * @param request
     * @param response
     */
    @RequestMapping(params = "saveOrUpdateTable")
    public void saveOrUpdateTable(HttpServletRequest request,
            HttpServletResponse response) {
        String isEdit = request.getParameter("isEdit");
        String tableComments = request.getParameter("tableComments");
        String tableName = request.getParameter("tableName");
        String tableColumnJson = request.getParameter("tableColumnJson");
        String oldTableName = request.getParameter("oldTableName");
        List<TableColumn> tableColumns= JSON.parseArray(tableColumnJson,TableColumn.class);
        dbManagerService.saveOrUpdateTable(isEdit, tableName, tableComments, tableColumns,oldTableName);
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("success", true);
        result.put("msg", "操作成功!");
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 核对表格列是否存在
     * @param request
     * @param response
     */
    @RequestMapping(params = "checkColumnName")
    public void checkColumnName(HttpServletRequest request,
            HttpServletResponse response) {
        String isEdit = request.getParameter("isEdit");
        String columnName = request.getParameter("columnName");
        Map<String,String> result = new HashMap<String,String>();
        boolean isExists = true;
        if(isEdit.equals("true")){
            isExists = dbManagerService.isExistsColumn(columnName, false);
        }else{
            isExists = dbManagerService.isExistsColumn(columnName, true);
        }
        if(isExists){
            result.put("error", "该列名已经存在，请重新输入!");  
        }
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 获取表格列的数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "checkTableName")
    public void checkTableName(HttpServletRequest request,
            HttpServletResponse response) {
        String isEdit = request.getParameter("isEdit");
        String tableName = request.getParameter("tableName");
        String NEW_TABLENAME = request.getParameter("NEW_TABLENAME");
        Map<String,String> result = new HashMap<String,String>();
        boolean isExists = true;
        if(isEdit.equals("true")){
            isExists = dbManagerService.isExistsTable(tableName, false);
        }else{
            if(StringUtils.isEmpty(tableName)){
                tableName = NEW_TABLENAME;
            }
            isExists = dbManagerService.isExistsTable(tableName, true);
        }
        if(isExists){
            result.put("error", "该表记录信息已经存在，请重新输入!");  
        }else{
            result.put("ok", "");
        } 
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 获取表格列的数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "tablecolumns")
    public void tablecolumns(HttpServletRequest request,
            HttpServletResponse response) {
        List<TableColumn> tableColumns = (List<TableColumn>) 
                PlatAppUtil.getSessionCache("tableColumns");
        if(tableColumns!=null&&tableColumns.size()>0){
            this.printListJsonString(null, tableColumns, response);
        }else{
            tableColumns = new ArrayList<TableColumn>();
            this.printListJsonString(null, tableColumns, response);
        }
    }
    
    /**
     * 获取列表的JSON数据
     * @param request
     * @param response
     */
    @RequestMapping(params = "datagrid")
    public void datagrid(HttpServletRequest request,
            HttpServletResponse response) {
        SqlFilter filter = new SqlFilter(request);
        String columnOrderName = request.getParameter("sidx");
        if(StringUtils.isNotEmpty(columnOrderName)){
            String sord = request.getParameter("sord");
            filter.addFilter("O_U.TABLE_NAME",sord.toUpperCase(),SqlFilter.FILTER_TYPE_ORDER);
        }else{
            filter.addFilter("O_U.TABLE_NAME","ASC",SqlFilter.FILTER_TYPE_ORDER);
        }
        List<TableInfo> list = dbManagerService.findBySqlFilter(filter);
        this.printListJsonString(filter.getPagingBean(), list, response);
    }
    
    /**
     * 跳转到表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goform")
    public ModelAndView goform(HttpServletRequest request) {
        String tableName = request.getParameter("tableName");
        String UI_DESIGNCODE = request.getParameter("UI_DESIGNCODE");
        if(StringUtils.isNotEmpty(tableName)){
            TableInfo tableInfo = dbManagerService.getTableInfo(tableName);
            request.setAttribute("tableInfo", tableInfo);
            request.setAttribute("isEdit", "true");
        }else{
            TableInfo tableInfo = new TableInfo();
            tableInfo.setTableName("PLAT_");
            request.setAttribute("tableInfo", tableInfo);
            request.setAttribute("isEdit", "false");
        }
        return PlatUICompUtil.goDesignUI(UI_DESIGNCODE, request);
    }
    
    /**
     * 跳转到表单界面
     * @param request
     * @return
     */
    @RequestMapping(params = "goCloneView")
    public ModelAndView goCloneView(HttpServletRequest request) {
        String tableName = request.getParameter("TABLE_NAME");
        request.setAttribute("TABLE_NAME",tableName);
        return PlatUICompUtil.goDesignUI("sourcedatacloneform", request);
    }
    
    
    /**
     * 删除表格列数据
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(params = "delColumn")
    public void delColumn(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        List<TableColumn> tableColumns = (List<TableColumn>) 
                PlatAppUtil.getSessionCache("tableColumns");
        String selectColValues = request.getParameter("selectColValues");
        Set<String> colValueSet = new HashSet<String>(Arrays.asList(selectColValues.split(",")));
        Iterator<TableColumn> iter = tableColumns.iterator();
        while(iter.hasNext()){
            TableColumn tableColumn = iter.next();
            if(colValueSet.contains(tableColumn.getColumnName())){
                iter.remove();
            }
        }
        PlatAppUtil.setSessionCache("tableColumns", tableColumns);
        result.put("success", true);
        result.put("msg", "删除成功!");
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 删除表
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(params = "delTables")
    public void delTables(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        String selectColValues = request.getParameter("selectColValues");
        dbManagerService.deleteTable(selectColValues);
        result.put("success", true);
        result.put("msg", "删除成功!");
        this.printObjectJsonString(result, response);
    }
    
    /**
     * 刷新表字段缓存
     * @param request
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(params = "refreshColumns")
    public void refreshColumns(HttpServletRequest request,
            HttpServletResponse response) {
        Map<String,Object> result = new HashMap<String,Object>();
        Map<String,List<String>> pkNameList = commonService.getAllPkNames();
        PlatAppUtil.setPrimaryKeyMap(pkNameList);
        //获取表字段列表
        PlatAppUtil.setTableColumnMap(commonService.getAllTableColumnNames());
        result.put("success", true);
        result.put("msg", "刷新成功!");
        this.printObjectJsonString(result, response);
    }
}
