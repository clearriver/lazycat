/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.AllConstants;
import com.stooges.core.util.PlatHttpUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.SSLClient;
import com.stooges.platform.bittrade.dao.HuoAccountDao;
import com.stooges.platform.bittrade.service.HuoAccountService;
import com.stooges.platform.bittrade.util.ApiSignature;

/**
 * 描述 火币账号信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-15 09:41:39
 */
@Service("huoAccountService")
public class HuoAccountServiceImpl extends BaseServiceImpl implements HuoAccountService {

    /**
     * 所引入的dao
     */
    @Resource
    private HuoAccountDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取API地址
     * @param accessKey
     * @param secretKey
     * @param method
     * @param uri
     * @return
     */
    public String getApiUrl(String accessKey,String secretKey,String method,
            String uri,Map<String,String> params){
        String API_URL = PlatPropUtil.getPropertyValue("conf/config.properties","huobihost");
        if(params==null){
            params = new HashMap<String,String>();
        }
        ApiSignature sign = new ApiSignature();
        sign.createSignature(accessKey,secretKey,method,
                ApiSignature.API_HOST, uri, params);
        return API_URL + uri + "?" + ApiSignature.toQueryString(params);
    }
    
    /**
     * 根据账号编码获取火币账号列表
     * @param code
     * @return
     */
    public List<Map> findAccountsByCode(String code){
        Map<String,Object> account = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{code});
        String accessKey = (String) account.get("HUOACCOUNT_ACCESSKEY");
        String secretKey = (String) account.get("HUOACCOUNT_SECRETKEY");
        String uri = "/v1/account/accounts";
        String url = this.getApiUrl(accessKey, secretKey, "GET", uri,null);
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        return list;
    }
    
    /**
     * 获取账号的ID
     * @param code
     * @return
     */
    public String getAccountId(String code){
        Map<String,Object> accountInfo = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{code});
        String HUOACCOUNT_TRADEID = (String) accountInfo.get("HUOACCOUNT_TRADEID");
        if(StringUtils.isEmpty(HUOACCOUNT_TRADEID)){
            Map account = this.findAccountsByCode(code).get(0);
            HUOACCOUNT_TRADEID = account.get("id").toString();
            accountInfo.put("HUOACCOUNT_TRADEID", HUOACCOUNT_TRADEID);
            dao.saveOrUpdate("PLAT_BITTRADE_HUOACCOUNT",accountInfo, 
                    AllConstants.IDGENERATOR_UUID,null);
        }
        return HUOACCOUNT_TRADEID;
    }
    
    /**
     * 获取当前委托列表
     * @param code
     * @return
     */
    public List<Map> findOrderList(String code,String symbol){
        Map<String,Object> account = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{code});
        String accessKey = (String) account.get("HUOACCOUNT_ACCESSKEY");
        String secretKey = (String) account.get("HUOACCOUNT_SECRETKEY");
        String uri = "/v1/order/orders";
        Map<String,String> params = new HashMap<String,String>();
        params.put("symbol", symbol);
        params.put("states", "submitted");
        String url = this.getApiUrl(accessKey, secretKey, "GET", uri,params);
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        return list;
    }
    
    /**
     * 获取当前委托列表
     * @param code
     * @param symbol
     * @param type
     * @return
     */
    public List<Map> findOrderList(String code,String symbol,String type){
        Map<String,Object> account = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{code});
        String accessKey = (String) account.get("HUOACCOUNT_ACCESSKEY");
        String secretKey = (String) account.get("HUOACCOUNT_SECRETKEY");
        String uri = "/v1/order/orders";
        Map<String,String> params = new HashMap<String,String>();
        params.put("symbol", symbol);
        params.put("states", "submitted");
        params.put("types", type);
        String url = this.getApiUrl(accessKey, secretKey, "GET", uri,params);
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendGetParams(url, httpClient,"UTF-8");
        Map<String,Object> map = JSON.parseObject(result,Map.class);
        List<Map> list = (List<Map>) map.get("data");
        return list;
    }
    
    /**
     * 撤销一个订单
     * @param userCode
     * @param orderId
     */
    public void revokeOneOrder(String userCode,String orderId){
        Map<String,Object> account = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{userCode});
        String accessKey = (String) account.get("HUOACCOUNT_ACCESSKEY");
        String secretKey = (String) account.get("HUOACCOUNT_SECRETKEY");
        String uri = "/v1/order/orders/"+orderId+"/submitcancel";
        String url = this.getApiUrl(accessKey, secretKey, "POST", uri,null);
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendPostParams(url,new HashMap<String,Object>()
                ,httpClient,"UTF-8");
        System.out.println("结果:"+result);
    }
    
    
    /**
     * 创建一个订单
     * @param accountId:账号ID
     * @param amount:购买数量
     * @param price:价格
     * @param symbol:成交对
     * @param userCode:用户编码
     */
    public void createOrder(String amount,
            String price,String symbol,String type,String userCode){
        //买卖类型 buy-limit sell-limit
        String accountId = this.getAccountId(userCode);
        Map<String,Object> account = this.getRecord("PLAT_BITTRADE_HUOACCOUNT",
                new String[]{"HUOACCOUNT_CODE"},new Object[]{userCode});
        String accessKey = (String) account.get("HUOACCOUNT_ACCESSKEY");
        String secretKey = (String) account.get("HUOACCOUNT_SECRETKEY");
        String uri = "/v1/order/orders/place";
        Map<String,String> paramsStr = new HashMap<String,String>();
        paramsStr.put("account-id",accountId);
        paramsStr.put("amount", amount);
        paramsStr.put("price", price);
        paramsStr.put("symbol", symbol);
        paramsStr.put("type", type);
        Map<String,Object> params = new HashMap<String,Object>();
        params.put("account-id", String.valueOf(accountId));
        params.put("amount", amount);
        params.put("price", price);
        params.put("symbol", symbol);
        params.put("type", type);
        String postBody = JSON.toJSONString(params);
        String url = this.getApiUrl(accessKey, secretKey, "POST", uri,paramsStr);
        SSLClient httpClient = null;
        String result = PlatHttpUtil.sendPostParams(url,postBody
                ,httpClient,"UTF-8");
        System.out.println("结果:"+result);
    }
    
    /**
     * 
     * 描述 根据查询参数JSON获取数据列表
     * @created 2016年3月27日 上午11:16:25
     * @param queryParamsJson
     * @return
     */
    public List<Map<String,Object>> findList(String queryParamsJson){
        StringBuffer sql = new StringBuffer("SELECT T.HUOACCOUNT_CODE AS VALUE,T.HUOACCOUNT_CODE AS LABEL ");
        sql.append(" FROM PLAT_BITTRADE_HUOACCOUNT T");
        return dao.findBySql(sql.toString(),null, null);
    }
    
    
  
}
