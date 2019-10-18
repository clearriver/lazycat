/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.task.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.task.dao.TaskDao;
import com.stooges.platform.task.service.TaskService;

/**
 * 描述 申请流程任务表业务相关service实现类
 * @author HuYu
 * @version 1.0
 * @created 2019-08-04 17:14:44
 */
@Service("taskService")
public class TaskServiceImpl extends BaseServiceImpl implements TaskService {

    /**
     * 所引入的dao
     */
    @Resource
    private TaskDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
  
}
