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
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.platform.bittrade.dao.OrderDao;
import com.stooges.platform.bittrade.service.HuoAccountService;
import com.stooges.platform.bittrade.service.OrderService;
import com.stooges.platform.bittrade.service.PriceRemindService;
import com.stooges.platform.bittrade.service.RangeInfoService;
import com.stooges.platform.bittrade.util.Order;

/**
 * 描述 委托单业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-16 18:12:13
 */
@Service("orderService")
public class OrderServiceImpl extends BaseServiceImpl implements OrderService {

    /**
     * 所引入的dao
     */
    @Resource
    private OrderDao dao;
    /**
     * 
     */
    @Resource
    private HuoAccountService huoAccountService;
    /**
     * 
     */
    @Resource
    private RangeInfoService rangeInfoService;
    /**
     * 
     */
    @Resource
    private PriceRemindService priceRemindService;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 执行自动交易
     */
    public void exeTrade(){
        List<Map<String,Object>> userSymbolList = this.findCurrentUserAndSymbol();
        for(Map<String,Object> userSymbol:userSymbolList){
            String ORDER_USERCODE = (String) userSymbol.get("ORDER_USERCODE");
            String ORDER_SYMBOL = (String) userSymbol.get("ORDER_SYMBOL");
            List<Map> orderList = huoAccountService.findOrderList(ORDER_USERCODE,ORDER_SYMBOL);
            if(orderList!=null&&orderList.size()>0){
                //如果没有卖出委托单,那么需要结束所有的挂单
                boolean noSellOrder = true;
                double sellPrice = -1.0;
                String sellOrderId = null;
                List<String> orderIds = new ArrayList<String>();
                for(Map order:orderList){
                    String type = order.get("type").toString();
                    String id = order.get("id").toString();
                    if(type.contains("sell")){
                        noSellOrder = false;
                        sellOrderId = id;
                        sellPrice = Double.parseDouble(order.get("price").toString());
                    }
                    orderIds.add(id);
                }
                if(noSellOrder){
                    for(String orderId:orderIds){
                        huoAccountService.revokeOneOrder(ORDER_USERCODE, orderId);
                    }
                }else{
                    /*int currentOrderSize = orderList.size();
                    List<Double> sellList = this.findUserOrderSell(ORDER_SYMBOL, ORDER_USERCODE);
                    List<Map<String,Object>> sysOrderList = this.findSysList(ORDER_USERCODE, ORDER_SYMBOL);
                    if(currentOrderSize==4){
                        String systemMoney = String.valueOf(sellList.get(3));
                        String huobiMoney = String.valueOf(sellPrice);
                        if(!systemMoney.contains(huobiMoney)){
                            System.out.println("止盈价不一致,进行撤销操作...");
                            huoAccountService.revokeOneOrder(ORDER_USERCODE, sellOrderId);
                            //进行新卖出挂单操作
                            huoAccountService.createOrder(amount, price, symbol, type, userCode);
                        }
                        System.out.println("火币中的止盈价:"+sellPrice+",系统中的止盈价:"+sellList.get(3));
                    }*/
                }
            }
        }
        
    }
    
    /**
     * 获取系统中的委托单列表
     * @param userCode
     * @param symbol
     * @return
     */
    public List<Map<String,Object>> findSysList(String userCode,String symbol){
        StringBuffer sql = new StringBuffer("SELECT T.*");
        sql.append(" FROM PLAT_BITTRADE_ORDER T WHERE T.ORDER_USERCODE=?");
        sql.append(" AND T.ORDER_SYMBOL=? ORDER BY T.ORDER_SELL ASC");
        return dao.findBySql(sql.toString(), new Object[]{userCode,symbol}, null);
    }
    
    /**
     * 
     * @param everyMoney
     * @param firstPrice
     * @return
     */
    public List<Order> getOrderList(double everyMoney,double firstPrice){
        List<Order> list = new ArrayList<Order>();
        Order o1 = new Order();
        o1.setMoney(everyMoney);
        o1.setPrice(firstPrice);
        o1.setNum(everyMoney/firstPrice);
        o1.setCost(firstPrice);
        o1.setSell(firstPrice*1.1);
        Order o2 = new Order();
        o2.setMoney(everyMoney);
        o2.setPrice(o1.getPrice()*0.9);
        o2.setNum(everyMoney/o2.getPrice());
        double o2Cost = (o1.getPrice()*o1.getNum()+o2.getPrice()*o2.getNum())/(o1.getNum()+o2.getNum());
        o2.setCost(o2Cost);
        o2.setSell(o2Cost*1.1);
        Order o3 = new Order();
        o3.setMoney(everyMoney);
        o3.setPrice(o2.getPrice()*0.85);
        o3.setNum(everyMoney/o3.getPrice());
        double o3Cost = (o1.getPrice()*o1.getNum()+o2.getPrice()*o2.getNum()+
                o3.getPrice()*o3.getNum())/(o1.getNum()+o2.getNum()+o3.getNum());
        o3.setCost(o3Cost);
        o3.setSell(o3Cost*1.1);
        Order o4 = new Order();
        o4.setMoney(everyMoney);
        o4.setPrice(o3.getPrice()*0.8);
        o4.setNum(everyMoney/o4.getPrice());
        double o4Cost = (o1.getPrice()*o1.getNum()+o2.getPrice()*o2.getNum()+
                o3.getPrice()*o3.getNum()+o4.getPrice()*o4.getNum())/
                (o1.getNum()+o2.getNum()+o3.getNum()+o4.getNum());
        o4.setCost(o4Cost);
        o4.setSell(o4Cost*1.1);
        list.add(o1);
        list.add(o2);
        list.add(o3);
        list.add(o4);
        return list;
    }
    
    /**
     * 生成委托单
     * @param orderInfo
     */
    public void genOrders(HttpServletRequest request){
        String ORDER_USERCODE = request.getParameter("ORDER_USERCODE");
        String ORDER_SYMBOL = request.getParameter("ORDER_SYMBOL");
        String FIRST_PRICE = request.getParameter("FIRST_PRICE");
        String TOTAL_MONEY = request.getParameter("TOTAL_MONEY");
        double totalMoney = Double.parseDouble(TOTAL_MONEY);
        double firstPrice = Double.parseDouble(FIRST_PRICE);
        double everyMoney = totalMoney/4;
        List<Order> orderList = this.getOrderList(everyMoney, firstPrice);
        double buyPrice = -1.0;
        double sellPrice = -1.0;
        for(int i=0;i<orderList.size();i++){
            Order order = orderList.get(i);
            if(i==1){
                buyPrice = order.getPrice();
            }else if(i==0){
                sellPrice = order.getSell();
            }
            Map<String,Object> map = new HashMap<String,Object>();
            map.put("ORDER_USERCODE", ORDER_USERCODE);
            map.put("ORDER_PRICE",order.getPrice());
            map.put("ORDER_NUM", order.getNum());
            map.put("ORDER_COST",order.getCost());
            map.put("ORDER_SELL",order.getSell());
            map.put("ORDER_MONEY",order.getMoney());
            map.put("ORDER_SYMBOL", ORDER_SYMBOL);
            dao.saveOrUpdate("PLAT_BITTRADE_ORDER",map,
                    AllConstants.IDGENERATOR_UUID,null);
        }
        priceRemindService.saveRemind(ORDER_USERCODE, 
                ORDER_SYMBOL, buyPrice,sellPrice);
    }
    
    /**
     * 获取当前系统中的交易用户账号和交易对
     * @return
     */
    public List<Map<String,Object>> findCurrentUserAndSymbol(){
        StringBuffer sql = new StringBuffer("SELECT T.ORDER_USERCODE,T.ORDER_SYMBOL");
        sql.append(" FROM PLAT_BITTRADE_ORDER T GROUP BY T.ORDER_USERCODE,T.ORDER_SYMBOL");
        return dao.findBySql(sql.toString(), null, null);
    }
    
    /**
     * 获取止盈价列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Double> findUserOrderSell(String symbol,String userCode){
        return dao.findUserOrderSell(symbol, userCode);
    }
    
    
    /**
     * 获取订单列表
     * @param symbol
     * @param userCode
     * @return
     */
    public List<Map<String,Object>> findOrders(String symbol,String userCode){
        StringBuffer sql = new StringBuffer("SELECT T.ORDER_PRICE,");
        sql.append("T.ORDER_SELL FROM PLAT_BITTRADE_ORDER T WHERE ");
        sql.append("T.ORDER_USERCODE=? AND T.ORDER_SYMBOL=? ");
        sql.append("ORDER BY T.ORDER_PRICE DESC");
        return dao.findBySql(sql.toString(), new Object[]{userCode,symbol},null);
    }
  
}
