/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatAppUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatOfficeUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.core.util.PlatStringUtil;
import com.stooges.core.util.UUIDGenerator;
import com.stooges.platform.appmodel.dao.WordTplDao;
import com.stooges.platform.appmodel.service.WordTplService;

/**
 * 描述 WORD模版业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-05-31 10:59:58
 */
@Service("wordTplService")
public class WordTplServiceImpl extends BaseServiceImpl implements WordTplService {

    /**
     * 所引入的dao
     */
    @Resource
    private WordTplDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 根据模版编码和参数生成模版
     * @param tplCode
     * @param params
     * @return
     */
    public String genWordByTplCodeAndParams(String tplCode,Map<String,Object> params){
        Map<String,Object> wordTpl = dao.getRecord("PLAT_APPMODEL_WORDTPL",
                new String[]{"TPL_CODE"},new Object[]{tplCode});
        String dyna_interface = (String) wordTpl.get("TPL_INTER");
        String TPL_CONTENT = (String) wordTpl.get("TPL_CONTENT");
        String beanId = dyna_interface.split("[.]")[0];
        String method = dyna_interface.split("[.]")[1];
        Object serviceBean = PlatAppUtil.getBean(beanId);
        Map<String,Object> rootData = new HashMap<String,Object>();
        if (serviceBean != null) {
            Method invokeMethod;
            try {
                invokeMethod = serviceBean.getClass().getDeclaredMethod(method,
                        new Class[] { Map.class });
                rootData = (Map<String,Object>) invokeMethod.invoke(serviceBean,
                        new Object[] { params });
            } catch (Exception e) {
                PlatLogUtil.printStackTrace(e);
            }
        }
        String uuid = UUIDGenerator.getUUID();
        String attachFilePath = PlatPropUtil.getPropertyValue("conf/config.properties", "attachFilePath");
        StringBuffer wordTplpath = new StringBuffer("genword/").append(uuid).append(".doc");
        String htmlCode = PlatStringUtil.getFreeMarkResult(TPL_CONTENT, rootData);
        PlatOfficeUtil.htmlCodeToWordByJacob(htmlCode,attachFilePath+wordTplpath);
        return wordTplpath.toString();
    }
  
}
