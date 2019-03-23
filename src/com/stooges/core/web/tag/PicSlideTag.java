/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 * 
 */
package com.stooges.core.web.tag;

import java.io.IOException;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;

import com.stooges.core.util.PlatBeanUtil;
import com.stooges.core.util.PlatLogUtil;
import com.stooges.core.util.PlatUICompUtil;


/**
 * @author 胡裕
 * 图片滑动标签
 * 2017年7月14日
 */
public class PicSlideTag extends BaseCompTag {
    /**
     * 点击事件名称
     */
    private String onclickfn;
    /**
     * 控件高度
     */
    private String height;

    /**
     * 方法doStartTag
     * 
     * @return 返回值int
     */
    public int doStartTag() throws JspException {
        return EVAL_PAGE;
    }
    
    /**
     * 方法doEndTag
     * 
     * @return 返回值int
     */
    public int doEndTag() throws JspException {
        JspWriter out = this.pageContext.getOut();
        Map<String,Object> paramMap = PlatBeanUtil.getSonAndSuperClassField(this);
        String htmlStr = PlatUICompUtil.getPicSlideTagHtml(paramMap);
        try {
            out.print(htmlStr);
        } catch (IOException e) {
            PlatLogUtil.printStackTrace(e);
        }
        return EVAL_PAGE;
    }
    
    /**
     * @return the onclickfn
     */
    public String getOnclickfn() {
        return onclickfn;
    }

    /**
     * @param onclickfn the onclickfn to set
     */
    public void setOnclickfn(String onclickfn) {
        this.onclickfn = onclickfn;
    }
    
    /**
     * @return the height
     */
    public String getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(String height) {
        this.height = height;
    }
}
