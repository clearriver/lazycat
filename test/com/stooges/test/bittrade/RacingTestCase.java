/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.test.bittrade;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;

import com.stooges.core.test.BaseTestCase;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatNumberUtil;
import com.stooges.platform.bittrade.service.RacingService;

/**
 * @author 胡裕
 *
 * 
 */
public class RacingTestCase extends BaseTestCase {
    /**
     * 
     */
    @Resource
    private RacingService racingService;
    
    /**
     * 
     * @param args
     */
    public static void main(String[] args){
        
    }
    
    /**
     * 
     * @param totalMoney
     * @return
     */
    public Integer[] getMoney(int totalMoney){
        Integer betMoney = PlatNumberUtil.getRoundingValue(totalMoney*0.5269);
        Integer lossMoney = totalMoney-betMoney;
        Integer[] money = new Integer[2];
        money[0] = betMoney;
        money[1] = lossMoney;
        return money;
    }
    
    /**
     * 
     */
    @Test
    public void initIndexDatas(){
        String beginDate = "2016-07-01";
        Date nowDate = PlatDateTimeUtil.formatStr(beginDate,"yyyy-MM-dd");
        int days = 360;
        for(int i=0;i<days;i++){
            Date nextDate = PlatDateTimeUtil.getNextTime(nowDate,Calendar.DATE,i);
            String value = PlatDateTimeUtil.formatDate(nextDate, "yyyy-MM-dd");
            Map<String,Object> result = racingService.getBetResult("2016-07-01", 190);
            List<Integer> moneyList = (List<Integer>) result.get("jwlist");
            for(int j=0;j<moneyList.size();j++){
                int number = j;
                Map<String,Object> index = new HashMap<String,Object>();
                index.put("RACINGINDEX_DATE", value);
                index.put("RACINGINDEX_NUM", number);
                index.put("RACINGINDEX_VALUE", moneyList.get(j));
                racingService.saveOrUpdate("PLAT_BITTRADE_RACINGINDEX",index,
                        AllConstants.IDGENERATOR_UUID,null);
            }
        }
    }
    
    /**
     * 
     */
    @Test
    public void calMoney(){
        String date = "2016-07-01";
        List<Map<String,Object>> list = racingService.findList(date);
        int totalMoney = 190;
        String bet = "大";
        for(Map<String,Object> data:list){
            int RACING_NUMONE = Integer.parseInt(data.get("RACING_NUMONE").toString());
            String openResult = null;
            if(RACING_NUMONE>5){
                openResult = "大";
            }else{
                openResult = "小";
            }
            if(bet.equals(openResult)){
                totalMoney = PlatNumberUtil.getRoundingValue(totalMoney*1.05);
            }else{
                totalMoney = PlatNumberUtil.getRoundingValue(totalMoney*0.947);
            }
            System.out.println("剩余钱:"+totalMoney);
        }
        
    }
}
