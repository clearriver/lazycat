/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatNumberUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.platform.bittrade.dao.RacingDao;
import com.stooges.platform.bittrade.service.RacingService;

/**
 * 描述 赛车数据业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-31 10:43:06
 */
@Service("racingService")
public class RacingServiceImpl extends BaseServiceImpl implements RacingService {

    /**
     * 所引入的dao
     */
    @Resource
    private RacingDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据日期获取列表数据
     * @param date
     * @return
     */
    public List<Map<String,Object>> findList(String date){
        String sql ="SELECT * FROM PLAT_APPMODEL_RACING T WHERE T.RACING_DATE=? ";
        sql+=" ORDER BY T.RACING_NUM ASC";
        return dao.findBySql(sql,new Object[]{date},null);
    }
    
     
     /**
      * 获取金额
      * @param date
      * @param totalMoney
      * @return
      */
     public Map<String,Object> getBetResult(String date,Integer totalMoney){
         List<Map<String,Object>> list = this.findList(date);
         List<Integer> moneyList = new ArrayList<Integer>();
         List<Integer> qihaoList = new ArrayList<Integer>();
         String bet = "大";
         Map<String,Object> result = new HashMap<String,Object>();
         for(Map<String,Object> data:list){
             int randomNumber = PlatStringUtil.getRandomIntNumber(1, 100);
             if(randomNumber>50){
                 bet = "大";
             }else{
                 bet = "小";
             }
             int RACING_NUMONE = Integer.parseInt(data.get("RACING_NUMONE").toString());
             qihaoList.add(Integer.parseInt(data.get("RACING_NUM").toString()));
             String openResult = null;
             if(RACING_NUMONE>5){
                 openResult = "大";
             }else{
                 openResult = "小";
             }
             if(bet.equals(openResult)){
                 totalMoney = PlatNumberUtil.getRoundingValue(totalMoney*1.013);
             }else{
                 totalMoney = PlatNumberUtil.getRoundingValue(totalMoney*0.982);
             }
             moneyList.add(totalMoney);
         }
         result.put("qslist", qihaoList);
         result.put("jwlist", moneyList);
         return result;
     }
  
}
