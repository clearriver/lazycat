/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.appmodel.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.springframework.stereotype.Service;

import com.stooges.core.dao.BaseDao;
import com.stooges.core.service.impl.BaseServiceImpl;
import com.stooges.core.util.PlatFileUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatOfficeUtil;
import com.stooges.core.util.PlatPropUtil;
import com.stooges.platform.appmodel.dao.ImportWordDao;
import com.stooges.platform.appmodel.service.ImportWordService;

/**
 * 描述 word导入业务相关service实现类
 * @author 李俊
 * @version 1.0
 * @created 2018-04-10 17:26:07
 */
@Service("importWordService")
public class ImportWordServiceImpl extends BaseServiceImpl implements ImportWordService {

    /**
     * 所引入的dao
     */
    @Resource
    private ImportWordDao dao;

    @Override
    protected BaseDao getDao() {
        return dao;
    }

    /**
     * 
     */
    @Override
    public String getWordHtmlContent(String dbfilepath) {
        String fileExt = PlatFileUtil.getFileExt(dbfilepath);
        String wordHtmlContent = "";
        if(fileExt.equals("doc")){
            try {
               wordHtmlContent =  PlatOfficeUtil.docToHtmlByPoi(dbfilepath);
            } catch (TransformerException e) {
                PlatLogUtil.printStackTrace(e);
            } catch (IOException e) {
                PlatLogUtil.printStackTrace(e);
            } catch (ParserConfigurationException e) {
                PlatLogUtil.printStackTrace(e);
            }
        }else if(fileExt.equals("docx")){
            try {
                wordHtmlContent =   PlatOfficeUtil.docxToHtmlByPoi(dbfilepath);
            } catch (Exception e) {
                PlatLogUtil.printStackTrace(e);
            }
        }
        return wordHtmlContent;
    }
  
}
