/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.cms.dao.DonationDao;
import com.stooges.platform.cms.service.DonationService;

/**
 * 描述 捐赠信息业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2018-01-30 11:03:13
 */
@Service("donationService")
public class DonationServiceImpl extends BaseServiceImpl implements DonationService {

    /**
     * 所引入的dao
     */
    @Resource
    private DonationDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 描述
     * @author 李俊
     * @created 2018年2月22日 上午11:56:49
     * @return
     * @see com.stooges.platform.cms.service.DonationService#findAllList()
     */
    @Override
    public List<Map<String, Object>> findAllList() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT T.* FROM PLAT_CMS_DONATION T ");
        sql.append(" order by t.DONATION_DATE desc ");
        return dao.findBySql(sql.toString(), null, null);
    }
  
}
