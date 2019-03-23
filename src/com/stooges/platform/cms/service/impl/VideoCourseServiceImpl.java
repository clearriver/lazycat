/*
 * Copyright (c) 2017, 2020, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.cms.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.model.SqlFilter;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.platform.cms.dao.VideoCourseDao;
import com.stooges.platform.cms.service.VideoCourseService;

/**
 * 描述 视频教程业务相关service实现类
 * @author 胡裕
 * @version 1.0
 * @created 2017-08-07 16:27:05
 */
@Service("videoCourseService")
public class VideoCourseServiceImpl extends BaseServiceImpl implements VideoCourseService {

    /**
     * 所引入的dao
     */
    @Resource
    private VideoCourseDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }
    
    /**
     * 获取下一个排序值
     * @return
     */
    public int getNextSn(){
        return dao.getMaxSn()+1;
    }

    /**
     * 描述
     * @created 2017年8月11日 下午9:06:45
     * @param filter
     * @return
     */
    @Override
    public List<Map<String, Object>> getVideoCourse(SqlFilter filter) {
        List params = new ArrayList();
        StringBuffer sql = new StringBuffer("");
        sql.append("select t.* from PLAT_CMS_VIDEOCOURSE t ");
        String levelValue = filter.getRequest().getParameter("levelValue");
        if(StringUtils.isEmpty(levelValue)){
            levelValue ="0";
        }
        if(!levelValue.equals("0")){
            sql.append(" WHERE  T.VIDEOCOURSE_LEVEL=? ");
            params.add(levelValue);
        }
        String exeSql = dao.getQuerySql(filter, sql.toString(), params);
        List<Map<String,Object>> list =  dao.findBySql(exeSql,
                params.toArray(), filter.getPagingBean());
        if(list!=null&&list.size()>0){
            for (int i = 0; i < list.size(); i++) {
                String videocourse_id = (String)list.get(i).get("VIDEOCOURSE_ID");
                StringBuffer fileSql = new StringBuffer("");
                fileSql.append(" SELECT F.FILE_PATH FROM PLAT_SYSTEM_FILEATTACH F ");
                fileSql.append("WHERE F.FILE_BUSRECORDID=? AND F.FILE_BUSTABLELNAME='PLAT_CMS_VIDEOCOURSE' ");
                Map<String,Object> fileMap = dao.getBySql(fileSql.toString(), new Object[]{videocourse_id});
                if(fileMap!=null){
                    list.get(i).put("FILE_PATH", fileMap.get("FILE_PATH"));
                }
            }
        }
        return list;
    }

    /**
     * 描述
     * @created 2017年8月17日 下午5:06:19
     * @return
     */
    @Override
    public int getVideoClickNum() {
        StringBuffer sql = new StringBuffer("");
        sql.append("select sum(t.videocourse_clickcount) as allnum from PLAT_CMS_VIDEOCOURSE t " );
        int num = 0;
        Map<String,Object> map = dao.getBySql(sql.toString(), 
                null);
        if(map!=null){
            num = Integer.parseInt(map.get("ALLNUM").toString());
        }
        return num;
    }
  
}
