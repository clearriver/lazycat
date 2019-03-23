/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.text.NumberFormat;
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
import com.stooges.platform.bittrade.dao.CoinCostDao;
import com.stooges.platform.bittrade.service.CoinCostService;

/**
 * 描述 币交易成本业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-04 17:01:22
 */
@Service("coinCostService")
public class CoinCostServiceImpl extends BaseServiceImpl implements CoinCostService {

    /**
     * 所引入的dao
     */
    @Resource
    private CoinCostDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取某个交易对的最新价格
     * @param symbol
     * @return
     */
    public Map<String,Double> getCoinNewestPrice(String symbol){
        String huobihost = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        String url = huobihost+"/market/history/kline?period=1day&size=1&symbol="+symbol;
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        NumberFormat nFromat = NumberFormat.getPercentInstance();
        nFromat.setMinimumFractionDigits(2);
        nFromat.setGroupingUsed(false);
        Map<String,Double> priceMap = new HashMap<String,Double>();
        if(list!=null&&list.size()>0){
            Map data = list.get(0);
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
            priceMap.put("COINCOST_NEWPRICE", close);
            priceMap.put("COINCOST_RANGE",Double.parseDouble(RANGEINFO_RANGE));
            priceMap.put("COINCOST_UP",Double.parseDouble(RANGEINFO_UP));
            priceMap.put("COINCOST_OPEN", open);
            priceMap.put("COINCOST_HIGH", high);
            priceMap.put("COINCOST_LOW", low);
        }
        return priceMap;
        
    }
    
    /**
     * 根据filter获取配置的交易记录
     * @param filter
     * @return
     */
    public List<Map> findTradeByFilter(SqlFilter filter){
        String COINCOST_ID = filter.getRequest().getParameter("COINCOST_ID");
        if(StringUtils.isNotEmpty(COINCOST_ID)){
            Map<String,Object> record = this.getRecord("PLAT_BITTRADE_COINCOST",
                    new String[]{"COINCOST_ID"},new Object[]{COINCOST_ID});
            String COINCOST_TRADEJSON = (String) record.get("COINCOST_TRADEJSON");
            if(StringUtils.isNotEmpty(COINCOST_TRADEJSON)){
                List<Map> list = JSON.parseArray(COINCOST_TRADEJSON,Map.class);
                return list;
            }else{
                return null;
            }
        }else{
            return null;
        }
    }
    
    /**
     * 更新所有价格
     */
    public void updateCostInfo(){
        StringBuffer sql = new StringBuffer("SELECT * FROM PLAT_BITTRADE_COINCOST");
        sql.append(" T ORDER BY T.COINCOST_TIME ASC ");
        List<Map<String,Object>> list = this.findBySql(sql.toString(), null, null);
        for(Map<String,Object> coinCost:list){
            String symbol = (String) coinCost.get("COINCOST_SYMBOL");
            Map<String,Double> newPrice = this.getCoinNewestPrice(symbol);
            if(newPrice.size()>1){
                coinCost.putAll(newPrice);
                String COINCOST_TRADEJSON = (String) coinCost.get("COINCOST_TRADEJSON");
                if(StringUtils.isNotEmpty(COINCOST_TRADEJSON)){
                    List<Map> tradeList = JSON.parseArray(COINCOST_TRADEJSON, Map.class);
                    Double totalCost = 0.0;
                    Double totalNum = 0.0;
                    for(Map trade:tradeList){
                        Double COIN_NUM = Double.parseDouble(trade.get("COIN_NUM").toString());
                        Double COIN_PRICE = Double.parseDouble(trade.get("COIN_PRICE").toString());
                        totalCost+=(COIN_NUM*COIN_PRICE);
                        totalNum+=COIN_NUM;
                    }
                    //获取目前成本价格
                    Double COINCOST_PRICE = totalCost/totalNum;
                    //获取目前最新价格
                    Double COINCOST_NEWPRICE = (Double) coinCost.get("COINCOST_NEWPRICE");
                    Double minus = COINCOST_NEWPRICE-COINCOST_PRICE;
                    NumberFormat nFromat = NumberFormat.getPercentInstance();
                    nFromat.setMinimumFractionDigits(2);
                    nFromat.setGroupingUsed(false);
                    //获取
                    String COINCOST_RATE = nFromat.format(minus/COINCOST_PRICE);
                    COINCOST_RATE = COINCOST_RATE.replace("%", "");
                    coinCost.put("COINCOST_RATE", COINCOST_RATE);
                    coinCost.put("COINCOST_PRICE", COINCOST_PRICE);
                }
                dao.saveOrUpdate("PLAT_BITTRADE_COINCOST",coinCost,
                        AllConstants.IDGENERATOR_UUID, null);
            }
        }
    }
  
}
