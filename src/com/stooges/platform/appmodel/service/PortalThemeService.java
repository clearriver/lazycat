/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service;

import java.util.List;
import java.util.Map;

import com.stooges.core.service.BaseService;

/**
 * 描述 门户主题业务相关service
 * @author 胡裕
 * @version 1.0
 * @created 2017-06-08 10:20:49
 */
public interface PortalThemeService extends BaseService {
    /**
     * 更新主题的名称和行JSON
     * @param themeId
     * @param themeName
     * @param rowJson
     */
    public void updateThemeNameAndRowOrder(String themeId,String themeName,String rowJson,String isAdminDesign);
    /**
     * 根据创建人获取主题列表数据
     * @param creatorId
     * @return
     */
    public List<Map<String,Object>> createAndfindByCreatorId(String creatorId);
    /**
     * 删除主题数据
     * @param themeIds
     */
    public void deleteCascade(String themeIds);
    /**
     * 
     * @param themeId
     * @param themeName
     * @param rowJson
     */
    public void cloneForNewTheme(String themeId,String themeName,String rowJson);
}
