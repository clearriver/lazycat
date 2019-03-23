/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.platform.bittrade.dao.PriceRemindDao;
import com.stooges.platform.bittrade.service.PriceRemindService;
import com.stooges.platform.bittrade.service.RangeInfoService;
import com.stooges.platform.weixin.service.TextMatterService;
import com.stooges.platform.weixin.service.UserService;

/**
 * 描述 价格提醒业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-18 11:20:54
 */
@Service("priceRemindService")
public class PriceRemindServiceImpl extends BaseServiceImpl implements PriceRemindService {

    /**
     * 所引入的dao
     */
    @Resource
    private PriceRemindDao dao;
    /**
     * 
     */
    @Resource
    private RangeInfoService rangeInfoService;
    /**
     * 
     */
    @Resource
    private TextMatterService textMatterService;
    /**
     * 
     */
    @Resource
    private UserService userService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 保存提醒信息数据
     * @param userCode
     * @param symbol
     * @param fallPrice
     * @param risePrice
     */
    public void saveRemind(String userCode,String symbol,
            double fallPrice,double risePrice){
        Map<String,Object> remind = new HashMap<String,Object>();
        remind.put("PRICEREMIND_USERCODE", userCode);
        remind.put("PRICEREMIND_SYMBOL", symbol);
        remind.put("PRICEREMIND_BUYPRICE", fallPrice);
        remind.put("PRICEREMIND_SELLPRICE", risePrice);
        remind.put("PRICEREMIND_ENABLE",1);
        remind.put("PRICEREMIND_TIME",PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss"));
        dao.saveOrUpdate("PLAT_BITTRADE_PRICEREMIND",
                remind,AllConstants.IDGENERATOR_UUID,null);
    }
    
    /**
     * 获取下一个提醒的委托单
     * @return
     */
    public Map<String,Object> getNextRemindOrder(double lastFallPrice,double lastRisePrice){
        Map<String,Object> nextOrder = new HashMap<String,Object>();
        StringBuffer sql = new StringBuffer("SELECT T.* FROM ");
        sql.append(" PLAT_BITTRADE_ORDER T WHERE T.ORDER_PRICE<? ");
        sql.append(" ORDER BY T.ORDER_PRICE DESC ");
        List<Map<String,Object>> list = dao.findBySql(sql.toString(),
                new Object[]{lastFallPrice},null);
        if(list!=null&&list.size()>0){
            Map<String,Object> data = list.get(0);
            nextOrder.put("ORDER_PRICE", data.get("ORDER_PRICE"));
        }else{
            nextOrder.put("ORDER_PRICE", "0.0");
        }
        sql = new StringBuffer("SELECT T.* FROM ");
        sql.append(" PLAT_BITTRADE_ORDER T WHERE T.ORDER_SELL<? ");
        sql.append(" ORDER BY T.ORDER_SELL DESC ");
        list = dao.findBySql(sql.toString(),
                new Object[]{lastRisePrice},null);
        if(list!=null&&list.size()>0){
            Map<String,Object> data = list.get(0);
            nextOrder.put("ORDER_SELL", data.get("ORDER_SELL"));
        }
        return nextOrder;
    }
    
    /**
     * 更新状态
     * @param userCode
     * @param smlbol
     * @param type
     */
    public void updateDisable(String userCode,String smlbol,int type){
        StringBuffer sql = new StringBuffer("UPDATE PLAT_BITTRADE_PRICEREMIND");
        sql.append(" SET PRICEREMIND_ENABLE=? WHERE PRICEREMIND_USERCODE=?");
        sql.append(" AND PRICEREMIND_SYMBOL=? AND PRICEREMIND_TYPE=?");
        dao.executeSql(sql.toString(),new Object[]{-1,userCode,smlbol,type});
    }
    
    /**
     * 获取当前设置的有效价格提醒列表
     * @return
     */
    public List<Map<String,Object>> findList(){
        StringBuffer sql = new StringBuffer("SELECT * FROM ");
        sql.append("PLAT_BITTRADE_PRICEREMIND T WHERE T.PRICEREMIND_ENABLE=?");
        sql.append(" ORDER BY T.PRICEREMIND_TIME DESC");
        return dao.findBySql(sql.toString(), new Object[]{1}, null);
    }
    
    /**
     * 提醒价格接口
     */
    public void remindPrice(){
        List<Map<String,Object>> priceList = this.findList();
        for(Map<String,Object> price:priceList){
            String ORDER_SYMBOL = (String) price.get("PRICEREMIND_SYMBOL");
            //获取当前的价格
            double currentPrice = this.rangeInfoService.getNowPrice(ORDER_SYMBOL);
            if(currentPrice>0){
                String remindMsg = null;
                double fallPrice = Double.parseDouble(price.
                        get("PRICEREMIND_BUYPRICE").toString());
                double risePrice = Double.parseDouble(price.
                        get("PRICEREMIND_SELLPRICE").toString());
                if(fallPrice>=currentPrice){
                    remindMsg = ORDER_SYMBOL+"交易对价格已经跌破:"+fallPrice;
                }else if(currentPrice>=risePrice){
                    remindMsg = ORDER_SYMBOL+"交易对价格已经超过:"+risePrice;
                }
                if(StringUtils.isNotEmpty(remindMsg)){
                    //推送微信消息提醒
                    //textMatterService.sendMsgToAllUser(remindMsg, "gh_e898cf9f6221");
                    //推送邮件提醒
                    userService.sendEmailToUsers(remindMsg,remindMsg, "gh_e898cf9f6221");
                    price.put("PRICEREMIND_ENABLE", "-1");
                    dao.saveOrUpdate("PLAT_BITTRADE_PRICEREMIND",
                            price,AllConstants.IDGENERATOR_UUID,null);
                }
            }
        }
    }
  
}
