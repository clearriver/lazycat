/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.chatonline.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 用户分组业务相关service
 * @author 李俊
 * @version 1.0
 * @created 2017-07-13 10:59:23
 */
public interface UserGroupService extends BaseService {

    /**
     * 描述 保存群
     * @author 李俊
     * @created 2017年7月16日 下午2:26:03
     * @return
     */
    public Map<String, Object> saveOrUpdateGroup(Map<String,Object> userGroup);

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月16日 下午3:41:03
     * @param id
     * @return
     */
    public Map<String, Object> getCreateMap(String id);

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月16日 下午3:50:02
     * @param id
     * @return
     */
    public List<Map<String, Object>> findGroupMemberListMap(String id);

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月28日 上午11:41:40
     * @param groupId
     * @return
     */
    public String getTreeJsonByGroupId(String groupId);

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月28日 下午3:03:27
     * @param sqlFilter
     * @return
     */
    public List<Map<String, Object>> findAutoComplete(SqlFilter sqlFilter);

    /**
     * 描述
     * @author 李俊
     * @created 2017年7月28日 下午3:41:31
     * @param userGroup
     * @return
     */
    public Map<String, Object> assignmentGroup(Map<String, Object> userGroup);
    
}
