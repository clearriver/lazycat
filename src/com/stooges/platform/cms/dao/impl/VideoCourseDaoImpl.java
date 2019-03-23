/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.dao.impl;

import org.springframework.stereotype.Repository;

import com.stooges.core.dao.impl.BaseDaoImpl;
import com.stooges.platform.cms.dao.VideoCourseDao;

/**
 * 描述视频教程业务相关dao实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-07 16:27:05
 */
@Repository
public class VideoCourseDaoImpl extends BaseDaoImpl implements VideoCourseDao {

    /**
     * 获取最大排序值
     * @return
     */
    public int getMaxSn(){
        StringBuffer sql = new StringBuffer("SELECT MAX(T.VIDEOCOURSE_SN)");
        sql.append(" FROM PLAT_CMS_VIDEOCOURSE T ");
        int maxSn = this.getIntBySql(sql.toString(),null);
        return maxSn;
    }
}
