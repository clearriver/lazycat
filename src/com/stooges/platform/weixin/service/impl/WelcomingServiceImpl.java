/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.weixin.dao.WelcomingDao;
import com.stooges.platform.weixin.service.WelcomingService;
import com.stooges.platform.weixin.util.MessageUtil;

/**
 * 描述 关注欢迎语业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 15:30:22
 */
@Service("welcomingService")
public class WelcomingServiceImpl extends BaseServiceImpl implements WelcomingService {

    /**
     * 所引入的dao
     */
    @Resource
    private WelcomingDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取欢迎语内容
     * @param pubSourceId
     * @return
     */
    public String getWelcomingContent(String pubSourceId){
        StringBuffer sql = new StringBuffer("SELECT * FROM PLAT_WEIXIN_WELCOMING W");
        sql.append(" WHERE W.WELCOMING_PUBID=(SELECT P.PUBLIC_ID FROM PLAT_WEIXIN_PUBLIC P");
        sql.append(" WHERE P.PUBLIC_SOURCEID=?)");
        Map<String,Object> wel = dao.getBySql(sql.toString(), new Object[]{pubSourceId});
        String WELCOMING_MTYPE = (String) wel.get("WELCOMING_MTYPE");
        String WELCOMING_MTID = (String) wel.get("WELCOMING_MTID");
        if(WELCOMING_MTYPE.equals(MessageUtil.REQ_MESSAGE_TYPE_TEXT)){
            Map<String,Object> text = dao.getRecord("PLAT_WEIXIN_TEXTMATTER",
                    new String[]{"TEXTMATTER_ID"},new Object[]{WELCOMING_MTID});
            String TEXTMATTER_CONTENT = (String) text.get("TEXTMATTER_CONTENT");
            return TEXTMATTER_CONTENT;
        }
        return null;
    }
  
}
