/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.webstatistics.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.alibaba.fastjson.JSON;
import com.stooges.core.util.PlatDateTimeUtil;
import com.stooges.core.util.PlatHttpUtil;

/**
 * 描述
 * @author 李俊
 * @created 2017年7月26日 下午9:24:03
 */
public class TrafficStatisticsUrl {
    /**
     * 
     */
    public static DefaultHttpClient httpclient = new DefaultHttpClient(
            new ThreadSafeClientConnManager());
    /**
     * 
     */
    public static String domainname = "https://web.umeng.com/";
    /**
     * 登录url
     */
    public static String logincheckurl = "login_check.php";
    /**
     * 获取趋势URL
     * &siteid=1263027173&st=2017-07-27&et=2017-07-27
     */
    public static String  trendurl="main.php?c=flow&a=trend&ajax=module%3Dsummary%7Cmodule%"
            + "3DfluxList_currentPage%3D1_pageType%3D30";
    /**
     * 获取网站来源
     * &siteid=1263027173&st=2017-07-23&et=2017-07-23
     */
    public static String domainurl = "main.php?c=traf&a=domain&ajax=modul"
            + "e=summary%7Cmodule=inputList%7Cmodule=innerList%7Cmodule=refererDomainList_orderBy="
            + "pv_orderType=-1_currentPage=1_pageType=10000&domainCondType=&itemName"
            + "=&itemNameType=&itemVal=&siteType=";
    /**
     * 获取入口URL
     */
    public static String  inviewurl="main.php?c=cont&a=page&ajax=module="
            + "summaryinput%7Cmodule=statistics_dataType="
            + "input_currentPage=1_orderBy=pv_orderType=-1_pageType=10000&sourcetype="
            + "&condtype=&condname=&condvalue=";
    /**
     * 新旧用户数据
     */
    public static String  newoldurl="main.php?c=visitor&a=type&ajax=module%3DnewOldList%7Cmodule%3DrefererList";
    /**
     * &Quota=pv&st=2017-07-27&et=2017-07-27&siteid=1263027173
     */
    public static String  mapdataurl="main.php?c=visitor&a=districtnet&ajax=module=flash&moduleType=location&type=Map";
    /**
     * 
     */
    public static DefaultHttpClient getHttpclient(String siteid){
        String url = "";
        String date = PlatDateTimeUtil.formatDate(new Date(), "yyyy-MM-dd");
        /*String url = TrafficStatisticsUrl.DOMAIN_NAME+TrafficStatisticsUrl.LOGIN_CHECK_URL
                +"?siteid="+TrafficStatisticsUrl.SITEID;*/
        Map<String,Object> params = new HashMap<String,Object>();
        /*PlatHttpUtil.sendPostParams(url, params, httpclient);*/
        url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.trendurl+"&siteid="
                +siteid+"&st="+date+"&et="+date;
        String result = PlatHttpUtil.sendPostParams(url, params, httpclient);
        if(result!=null){
            Map<String,Object> resultMap = (Map)JSON.parseObject(result, Map.class).get("data");
            Object status = resultMap.get("status");
            if(status!=null){
                url = TrafficStatisticsUrl.domainname+TrafficStatisticsUrl.logincheckurl
                        +"?siteid="+siteid;
                PlatHttpUtil.sendPostParams(url, params, httpclient);
               /* System.out.println("===============1====================");*/
            }
        }
        return httpclient;
    }
}
