/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.SSLClient;
import com.stooges.platform.bittrade.dao.RangeInfoDao;
import com.stooges.platform.bittrade.service.RangeInfoService;

/**
 * 描述 日涨幅信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-14 16:16:54
 */
@Service("rangeInfoService")
public class RangeInfoServiceImpl extends BaseServiceImpl implements RangeInfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private RangeInfoDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取交易对列表
     * @return
     */
    public List<String> getSymbolList(){
        List<String> symbolList = new ArrayList<String>();
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"/v1/common/symbols";
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        for(Map data:list){
            String basecurrency = (String) data.get("base-currency");
            String quotecurrency = (String) data.get("quote-currency");
            if(quotecurrency.equals("btc")||quotecurrency.equals("usdt")){
                symbolList.add(basecurrency+","+quotecurrency);
            }
        }
        return symbolList;
    }
    
    /**
     * 根据filter和配置信息获取数据列表
     * @param filter
     * @param fieldInfo
     * @return
     */
    public List<Map<String,Object>> findList(SqlFilter filter,Map<String,Object> fieldInfo){
        String beginDate = filter.getRequest().getParameter("Q_T.RANGEINFO_DATE_GE");
        String endDate = filter.getRequest().getParameter("Q_T.RANGEINFO_DATE_LE");
        String symbolBegin = filter.getRequest().getParameter("Q_T.RANGEINFO_BEGIN_EQ");
        String symbolEnd = filter.getRequest().getParameter("Q_T.RANGEINFO_END_EQ");
        try{
            if(StringUtils.isNotEmpty(beginDate)&&StringUtils.isNotEmpty(endDate)&&
                    StringUtils.isNotEmpty(symbolEnd) ){
                if(StringUtils.isEmpty(symbolBegin)){
                    List<String> symbolList = this.getSymbolList();
                    for(String symbol:symbolList){
                        String sb = symbol.split(",")[0];
                        String se = symbol.split(",")[1];
                        if(symbolEnd.equals(se)){
                            this.initRangInfo(beginDate, endDate, sb, symbolEnd);
                        }
                    }
                }else{
                    this.initRangInfo(beginDate, endDate, symbolBegin, symbolEnd);
                }
                
            }
            String today = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
            if(StringUtils.isNotEmpty(beginDate)&&StringUtils.isNotEmpty(endDate)
                    &&today.equals(endDate)&&beginDate.equals(endDate)
                    &&StringUtils.isEmpty(symbolEnd)
                    ){
                List<String> symbolList = this.getSymbolList();
                for(String symbol:symbolList){
                    this.initRangInfo(today, symbol);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        List<Object> params = new ArrayList<Object>();
        StringBuffer sql = new StringBuffer("select T.* FROM PLAT_BITTRADE_RANGEINFO T");
        String exeSql = dao.getQuerySql(filter, sql.toString(), params);
        String sidx = filter.getRequest().getParameter("sidx");
        if(StringUtils.isEmpty(sidx)){
            exeSql+=" ORDER BY T.RANGEINFO_DATE DESC";
        }
        List<Map<String, Object>> list = dao.findBySql(exeSql,
                params.toArray(), filter.getPagingBean());
        return list;
    }
    
    /**
     * 根据交易对获取最新价格
     * @param symbol
     * @return
     */
    public double getNowPrice(String symbol){
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"/market/history/kline?period=1min&size=1&symbol="+symbol;
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        if(map!=null){
            List<Map> list = (List<Map>) map.get("data");
            if(list!=null&&list.size()>0){
                Map data = list.get(0);
                Double close = Double.parseDouble(data.get("close").toString());
                return close;
            }else{
                return -1.0;
            }
        }else{
            return -1.0;
        }
    }
    
    /**
     * 
     * @param date
     * @param symbol
     */
    public void initRangInfo(String date,String symbol){
        String symbolBegin = symbol.split(",")[0];
        String symbolEnd = symbol.split(",")[1];
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"/market/history/kline?period=1day&size=1&symbol="+symbolBegin+symbolEnd;
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        if(map!=null){
            List<Map> list = (List<Map>) map.get("data");
            if(list!=null&&list.size()>0){
                NumberFormat nFromat = NumberFormat.getPercentInstance();
                nFromat.setMinimumFractionDigits(2);
                nFromat.setGroupingUsed(false);
                for(int i=0;i<list.size();i++){
                    Map data = list.get(i);
                    Double low= Double.parseDouble(data.get("low").toString());
                    Double high= Double.parseDouble(data.get("high").toString());
                    Double open = Double.parseDouble(data.get("open").toString());
                    Double close = Double.parseDouble(data.get("close").toString());
                    Double lowHigh = high-low;
                    Double closeopen= close-open;
                    //获取振幅
                    String RANGEINFO_RANGE = nFromat.format(lowHigh/open);
                    RANGEINFO_RANGE = RANGEINFO_RANGE.replace("%", "");
                    //获取涨幅
                    String RANGEINFO_UP= nFromat.format(closeopen/open);
                    RANGEINFO_UP = RANGEINFO_UP.replace("%", "");
                    Map<String,Object> info = this.getRecord("PLAT_BITTRADE_RANGEINFO",
                            new String[]{"RANGEINFO_DATE","RANGEINFO_BEGIN","RANGEINFO_END"},
                            new Object[]{date,symbolBegin,symbolEnd});
                    if(info==null){
                        info = new HashMap<String,Object>();
                    }
                    info.put("RANGEINFO_DATE", date);
                    info.put("RANGEINFO_BEGIN",symbolBegin);
                    info.put("RANGEINFO_END",symbolEnd);
                    info.put("RANGEINFO_UP",RANGEINFO_UP);
                    info.put("RANGEINFO_RANGE",RANGEINFO_RANGE);
                    info.put("RANGEINFO_OPEN",open);
                    info.put("RANGEINFO_HIGH",high);
                    info.put("RANGEINFO_LOW",low);
                    info.put("RANGEINFO_CLOSE",close);
                    dao.saveOrUpdate("PLAT_BITTRADE_RANGEINFO",
                            info,AllConstants.IDGENERATOR_UUID,null);
                  
                }
            }
            
        }
    }
    
    /**
     * 初始化振幅信息数据
     * @param beginDate
     * @param endDate
     * @param symbolBegin
     * @param symbolEnd
     */
    public void initRangInfo(String beginDate,String endDate,
            String symbolBegin,String symbolEnd){
        //获取间隔天数
        Long days = PlatDateTimeUtil.getIntervalTime(beginDate, endDate, "yyyy-MM-dd", 4);
        int size = days.intValue()+1;
        String today = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String symbol = symbolBegin+symbolEnd;
        String url = huobihost+"/market/history/kline?period=1day&size="+size+"&symbol="+symbol;
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        Date nowDate = PlatDateTimeUtil.formatStr(endDate,"yyyy-MM-dd");
        NumberFormat nFromat = NumberFormat.getPercentInstance();
        nFromat.setMinimumFractionDigits(2);
        nFromat.setGroupingUsed(false);
        for(int i=0;i<list.size();i++){
            Map data = list.get(i);
            Date date = PlatDateTimeUtil.getNextTime(nowDate,Calendar.DATE,-i);
            String targetDate = PlatDateTimeUtil.formatDate(date, "yyyy-MM-dd");
            Double low= Double.parseDouble(data.get("low").toString());
            Double high= Double.parseDouble(data.get("high").toString());
            Double open = Double.parseDouble(data.get("open").toString());
            Double close = Double.parseDouble(data.get("close").toString());
            Double lowHigh = high-low;
            Double closeopen= close-open;
            //获取振幅
            String RANGEINFO_RANGE = nFromat.format(lowHigh/open);
            RANGEINFO_RANGE = RANGEINFO_RANGE.replace("%", "");
            //获取涨幅
            String RANGEINFO_UP= nFromat.format(closeopen/open);
            RANGEINFO_UP = RANGEINFO_UP.replace("%", "");
            if(!targetDate.equals(today)){
                Map<String,Object> info = this.getRecord("PLAT_BITTRADE_RANGEINFO",
                        new String[]{"RANGEINFO_DATE","RANGEINFO_BEGIN","RANGEINFO_END"},
                        new Object[]{targetDate,symbolBegin,symbolEnd});
                if(info==null){
                    info = new HashMap<String,Object>();
                    info.put("RANGEINFO_DATE", targetDate);
                    info.put("RANGEINFO_BEGIN",symbolBegin);
                    info.put("RANGEINFO_END",symbolEnd);
                    info.put("RANGEINFO_UP",RANGEINFO_UP);
                    info.put("RANGEINFO_RANGE",RANGEINFO_RANGE);
                    info.put("RANGEINFO_OPEN",open);
                    info.put("RANGEINFO_HIGH",high);
                    info.put("RANGEINFO_LOW",low);
                    info.put("RANGEINFO_CLOSE",close);
                    dao.saveOrUpdate("PLAT_BITTRADE_RANGEINFO",
                            info,AllConstants.IDGENERATOR_UUID,null);
                }
            }else{
                Map<String,Object> info = this.getRecord("PLAT_BITTRADE_RANGEINFO",
                        new String[]{"RANGEINFO_DATE","RANGEINFO_BEGIN","RANGEINFO_END"},
                        new Object[]{targetDate,symbolBegin,symbolEnd});
                if(info==null){
                    info = new HashMap<String,Object>();
                }
                info.put("RANGEINFO_DATE", targetDate);
                info.put("RANGEINFO_BEGIN",symbolBegin);
                info.put("RANGEINFO_END",symbolEnd);
                info.put("RANGEINFO_UP",RANGEINFO_UP);
                info.put("RANGEINFO_RANGE",RANGEINFO_RANGE);
                info.put("RANGEINFO_OPEN",open);
                info.put("RANGEINFO_HIGH",high);
                info.put("RANGEINFO_LOW",low);
                info.put("RANGEINFO_CLOSE",close);
                dao.saveOrUpdate("PLAT_BITTRADE_RANGEINFO",
                        info,AllConstants.IDGENERATOR_UUID,null);
            }
          
        }
    }
  
}
