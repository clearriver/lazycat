/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import freemarker.cache.TemplateLoader;

/**
 * 描述
 * @author 胡裕
 * @version 1.0
 * @created 2014年9月16日 上午10:13:10
 */
public class StringTemplateLoader implements TemplateLoader {
    /**
     * 缺省KEY
     */
    private static final String DEFAULT_TEMPLATE_KEY = "_default_template_key";
    /**
     * 缺省模版
     */
    private Map templates = new HashMap();
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:13:42
     * @param defaultTemplate
     */
    public StringTemplateLoader(String defaultTemplate) {
        if (defaultTemplate != null && !defaultTemplate.equals("")) {
            templates.put(DEFAULT_TEMPLATE_KEY, defaultTemplate);
        }
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:13:47
     * @param name
     * @param template
     */
    public void addTemplate(String name, String template) {
        if (name == null || template == null || name.equals("")
                || template.equals("")) {
            return;
        }
        if (!templates.containsKey(name)) {
            templates.put(name, template);
        }
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:13:51
     * @param templateSource
     * @throws IOException
     * @see freemarker.cache.TemplateLoader#closeTemplateSource(java.lang.Object)
     */
    public void closeTemplateSource(Object templateSource) throws IOException {

    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:13:54
     * @param name
     * @return
     * @throws IOException
     * @see freemarker.cache.TemplateLoader#findTemplateSource(java.lang.String)
     */
    public Object findTemplateSource(String name) throws IOException {
        if (name == null || name.equals("")) {
            name = DEFAULT_TEMPLATE_KEY;
        }
        return templates.get(name);
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:13:57
     * @param templateSource
     * @return
     * @see freemarker.cache.TemplateLoader#getLastModified(java.lang.Object)
     */
    public long getLastModified(Object templateSource) {
        return 0;
    }
    /**
     * 
     * 描述
     * @author 胡裕
     * @created 2014年9月16日 上午10:14:00
     * @param templateSource
     * @param encoding
     * @return
     * @throws IOException
     * @see freemarker.cache.TemplateLoader#getReader(java.lang.Object, java.lang.String)
     */
    public Reader getReader(Object templateSource, String encoding) throws IOException {
        return new StringReader((String) templateSource);
    }

}
