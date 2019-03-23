/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.system.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.system.dao.GlobalConfigDao;
import com.stooges.platform.system.service.GlobalConfigService;

/**
 * 描述 全局配置业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2018-05-09 13:52:03
 */
@Service("globalConfigService")
public class GlobalConfigServiceImpl extends BaseServiceImpl implements GlobalConfigService {

    /**
     * 所引入的dao
     */
    @Resource
    private GlobalConfigDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

	/**
	 * 
	 */
	@Override
	public Map<String,Object> getFirstConfigMap() {
		Map<String,Object> firstMap =  null;
		try{
			StringBuffer sql = new StringBuffer("");
			sql.append("SELECT C.* FROM PLAT_SYSTEM_GLOBALCONFIG C ORDER BY C.CONFIG_CREATETIME ASC ");
			List<Map<String,Object>> list =  dao.findBySql(sql.toString(), null,null);
			if(list!=null&&list.size()>0){
				firstMap = list.get(0);
			}else{
				firstMap = new HashMap<String,Object>();
			}
		}catch(Exception e){
			
		}
		return firstMap;
	}
  
}
