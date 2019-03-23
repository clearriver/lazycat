/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.BaseService;

/**
 * 描述 岗位业务相关service
 * @author HuYu
 * @version 1.0
 * @created 2018-05-12 15:52:01
 */
public interface PositionService extends BaseService {
    /**
     * 保存用户岗位中间表
     * @param positionId
     * @param userIds
     */
    public void saveUsers(String positionId,String[] userIds);
    /**
     * 获取已经选择的服务记录
     * @param filter
     * @return
     */
    public List<Map<String,Object>> findSelected(SqlFilter filter);
    /**
     * 获取用户所在岗位信息
     * @param userId
     * @return
     */
    public Map<String,String> getUserPositionInfo(String userId);
    
    /**
     * 分配用户接口
     * @param request
     * @return
     */
    public Map<String,Object> grantUsers(HttpServletRequest request);
    
    /**
     * 删除数据
     * @param request
     * @return
     */
    public Map<String,Object> deletePos(HttpServletRequest request);
    
    /**
     * 保存数据
     * @param request
     * @return
     */
    public Map<String,Object> saveOrUpdatePos(HttpServletRequest request);
}
