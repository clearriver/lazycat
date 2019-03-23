/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import com.stooges.core.service.BaseService;

/**
 * 描述 word导入业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2018-04-10 17:26:07
 */
public interface ImportWordService extends BaseService {

    /**
     * @param dbfilepath
     * @return
     */
    public String getWordHtmlContent(String dbfilepath);
    
}
