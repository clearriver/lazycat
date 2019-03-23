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
import com.stooges.platform.weixin.dao.TextPictureMatterDao;
import com.stooges.platform.weixin.service.TextPictureMatterService;

/**
 * 描述 图文素材业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2018-02-22 16:09:23
 */
@Service("textPictureMatterService")
public class TextPictureMatterServiceImpl extends BaseServiceImpl implements TextPictureMatterService {

    /**
     * 所引入的dao
     */
    @Resource
    private TextPictureMatterDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2018年2月22日 下午4:12:33
     * @param publicId
     * @return
     */
    @Override
    public List<Map<String, Object>> findListByPublicId(String publicId) {
        StringBuffer sql = new StringBuffer("");
        sql.append(" SELECT T.* FROM PLAT_WEIXIN_TEXTPICTUREMATTER T WHERE T.TEXTPICTURE_PUBID=? ");
        sql.append(" ORDER BY T.TEXTPICTURE_SN ASC ");
        return dao.findBySql(sql.toString(), new Object[]{publicId}, null);
    }
  
}
