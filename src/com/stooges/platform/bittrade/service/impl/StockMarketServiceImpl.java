/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONPath;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.platform.bittrade.dao.StockMarketDao;
import com.stooges.platform.bittrade.service.StockMarketService;
import com.stooges.platform.system.service.DictionaryService;

/**
 * 描述 股票行情信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-19 15:12:30
 */
@Service("stockMarketService")
public class StockMarketServiceImpl extends BaseServiceImpl implements StockMarketService {

    /**
     * 所引入的dao
     */
    @Resource
    private StockMarketDao dao;
    /**
     * 
     */
    @Resource
    private DictionaryService dictionaryService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取股票行情
     * @param code
     * @param date
     * @return
     */
    public Map<String,Double> getMarket(String code,String date){
        String host = dictionaryService.getDictionaryValue("NowApIKey","Site");
        String appKey = dictionaryService.getDictionaryValue("NowApIKey","AppKey");
        String sign = dictionaryService.getDictionaryValue("NowApIKey","Sign");
        StringBuffer url = new StringBuffer(host);
        url.append("?app=finance.stock_history&symbol=").append(code);
        url.append("&date=").append(date).append("&appkey=").append(appKey);
        url.append("&sign=").append(sign).append("&format=json");
        String result = PlatHttpUtil.sendPostParams(url.toString(), null);
        Map<String,Object> jsonMap = JSON.parseObject(result,Map.class);
        String success = (String) jsonMap.get("success");
        if(success.equals("1")){
            Object data = JSONPath.eval(JSON.parseObject(result)
                    ,"$.result.lists.*");
            Map<String,Object> price = JSON.parseArray(data.toString(), Map.class).get(0);
            NumberFormat nFromat = NumberFormat.getPercentInstance();
            nFromat.setMinimumFractionDigits(2);
            nFromat.setGroupingUsed(false);
            //获取开盘价
            Double STOCKMARKET_OPEN = Double.parseDouble(price.get("open_price").toString());
            Double STOCKMARKET_HIGH = Double.parseDouble(price.get("high_price").toString());
            Double STOCKMARKET_LOW = Double.parseDouble(price.get("low_price").toString());
            //收盘价
            Double STOCKMARKET_NOW = Double.parseDouble(price.get("last_price").toString());
            Double lowHigh = STOCKMARKET_HIGH-STOCKMARKET_LOW;
            Double closeopen= STOCKMARKET_NOW-STOCKMARKET_OPEN;
            //获取振幅
            String RANGEINFO_RANGE = nFromat.format(lowHigh/STOCKMARKET_OPEN);
            RANGEINFO_RANGE = RANGEINFO_RANGE.replace("%", "");
            //获取涨幅
            String RANGEINFO_UP= nFromat.format(closeopen/STOCKMARKET_OPEN);
            RANGEINFO_UP = RANGEINFO_UP.replace("%", "");
            Map<String,Double> market = new HashMap<String,Double>();
            market.put("STOCKMARKET_OPEN", STOCKMARKET_OPEN);
            market.put("STOCKMARKET_HIGH", STOCKMARKET_HIGH);
            market.put("STOCKMARKET_LOW", STOCKMARKET_LOW);
            market.put("STOCKMARKET_NOW", STOCKMARKET_NOW);
            market.put("STOCKMARKET_RISE",Double.parseDouble(RANGEINFO_UP));
            market.put("STOCKMARKET_SWIING", Double.parseDouble(RANGEINFO_RANGE));
            return market;
        }else{
            return null;
        }
    }
    
    /**
     * 保存股票行情信息
     * @param code
     * @param beginDate
     * @param endDate
     */
    public void saveMarket(String code,String beginDate,String endDate){
        
        //获取间隔天数
        Long days = PlatDateTimeUtil.getIntervalTime(beginDate, endDate, "yyyy-MM-dd", 4);
        Date nowDate = PlatDateTimeUtil.formatStr(beginDate,"yyyy-MM-dd");
        for(int i=0;i<=days;i++){
            Date date = PlatDateTimeUtil.getNextTime(nowDate,Calendar.DATE,i);
            String sourceDate = PlatDateTimeUtil.formatDate(date, "yyyy-MM-dd");
            String targetDate = PlatDateTimeUtil.formatDate(date, "yyyyMMdd");
            Map<String,Double> price = this.getMarket(code, targetDate);
            if(price!=null){
                Map<String,Object> market = this.getRecord("PLAT_BITTRADE_STOCKMARKET",
                        new String[]{"STOCKMARKET_CODE","STOCKMARKET_DATE"},new Object[]{code,sourceDate});
                if(market==null){
                    market = new HashMap<String,Object>();
                }
                market.putAll(price);
                market.put("STOCKMARKET_CODE", code);
                market.put("STOCKMARKET_DATE", sourceDate);
                dao.saveOrUpdate("PLAT_BITTRADE_STOCKMARKET",market,
                        AllConstants.IDGENERATOR_UUID, null);
            }
        }
    }
  
}
