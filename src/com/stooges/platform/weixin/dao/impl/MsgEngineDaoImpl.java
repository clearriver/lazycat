/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.weixin.dao.MsgEngineDao;

/**
 * 描述消息处理引擎业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-12-19 09:24:26
 */
@Repository
public class MsgEngineDaoImpl extends BaseDaoImpl implements MsgEngineDao {

    /**
     * 获取处理公众号的消息接口
     * @param publicSourceId
     * @param msgType
     * @return
     */
    public String getInvokeMsgInter(String publicSourceId,String msgType){
        StringBuffer sql = new StringBuffer("SELECT T.MSGENGINE_CODE FROM PLAT_WEIXIN_MSGENGINE T");
        sql.append(" WHERE T.PUBLIC_ID=(SELECT P.PUBLIC_ID FROM PLAT_WEIXIN_PUBLIC P");
        sql.append(" WHERE P.PUBLIC_SOURCEID=? )");
        sql.append(" AND T.MSGENGINE_STATUS=? AND T.MSGENGINE_TYPE=? ");
        sql.append(" ORDER BY T.MSGENGINE_TIME DESC");
        List<String> list = this.getJdbcTemplate().queryForList(sql.toString(), 
                new Object[]{publicSourceId,1,msgType}, String.class);
        if(list!=null&&list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }
}
