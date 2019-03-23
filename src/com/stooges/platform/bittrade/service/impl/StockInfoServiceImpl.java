/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.bittrade.dao.StockInfoDao;
import com.stooges.platform.bittrade.service.StockInfoService;

/**
 * 描述 股票涨跌幅业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2018-06-02 10:44:07
 */
@Service("stockInfoService")
public class StockInfoServiceImpl extends BaseServiceImpl implements StockInfoService {

    /**
     * 所引入的dao
     */
    @Resource
    private StockInfoDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}
