/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.bittrade.dao.BitTradeDao;
import com.stooges.platform.bittrade.service.BitTradeService;

/**
 * @author 胡裕
 *
 * 
 */
@Service("bitTradeService")
public class BitTradeServiceImpl extends BaseServiceImpl implements
        BitTradeService {
    
    /**
     * 所引入的dao
     */
    @Resource
    private BitTradeDao dao;

    /* (non-Javadoc)
     * @see net.STOOGES.core.service.impl.BaseServiceImpl#getDao()
     */
    @Override
    protected BaseDao getDao() {
        return dao;
    }

}
