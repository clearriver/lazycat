/*
 * Copyright (c) 2005, 2018, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

/**
 * @author river  <br/>
 * <b>XWPFRun表示有相同属性的一段文本，</b> <br/>
 * <b>所以模板里变量内容需要从左到右的顺序写，${userName}，</b> <br/>
 * <b>如果先写${},再添加内容，会拆分成几部分，不能正常使用<b>
 */
public class DocUtil {
	public static void main(String[] args) {
		try {
//			testWrite();
			
			HashMap<String,Object> params=new HashMap<String,Object>(){{put("name","aa");put("sex", "男");put("age",10);put("date","2018-08-08");
			put("name2","搜索");put("name3","");put("name4","搜    索");}};
			String templatePath = "E:\\work\\担保系统\\template\\委托担保申请书.docx";
			String outFile = "E:\\work\\担保系统\\template\\委托担保申请书1.docx";
			genFile(templatePath,outFile,params);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void testWrite() throws Exception {
		List<HashMap<String,Object>> list = new ArrayList<HashMap<String,Object>>();
		list.add(new HashMap<String,Object>(){{put("name","a");put("sex", "男");put("age",10);put("date","2018-08-08");}});
		list.add(new HashMap<String,Object>(){{put("name","b");put("sex", "女");put("age",20);put("date","2017-07-07");}});
		HashMap<String,Object> params=new HashMap<String,Object>(){{put("name","aa");put("sex", "男");put("age",10);put("date","2018-08-08");
		put("name2","搜索");put("name3","");put("name4","搜    索");}};
		String templatePath = "E:\\work\\担保系统\\template\\委托担保申请书.docx";
		String outFile = "E:\\work\\担保系统\\template\\委托担保申请书1.docx";
		InputStream is = new FileInputStream(templatePath);
		XWPFDocument doc = new XWPFDocument(is);
		replaceInPara(doc, params);
		replaceInTable(doc,params);
		OutputStream os = new FileOutputStream(outFile);
        doc.write(os);
		close(os);
		close(is);
	}
	public static void genFile(String templatePath,String outPath,Map<String,Object> params){
		try {
			File dir=new File(outPath.substring(0,outPath.lastIndexOf("/")));
			if(!dir.exists()) {
				dir.mkdirs();
			}
			InputStream is = new FileInputStream(templatePath);
			XWPFDocument doc = new XWPFDocument(is);
			replaceInPara(doc, params);
			replaceInTable(doc,params);
			OutputStream os = new FileOutputStream(outPath);
			doc.write(os);
			close(os);
			close(is);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * 替换段落里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private static void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
       Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
       XWPFParagraph para;
       while (iterator.hasNext()) {
          para = iterator.next();
          replaceInPara(para, params);
       }
    }

    /**
     * 替换段落里面的变量
     * @param para 要替换的段落
     * @param params 参数
     */
    private static void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
       List<XWPFRun> runs;
       Matcher matcher;
       if (matcher(para.getParagraphText()).find()) {
          runs = para.getRuns();
          for (int i=0; i<runs.size(); i++) {
             XWPFRun run = runs.get(i);
             String runText = run.toString();
             matcher = matcher(runText);
             if (matcher.find()) {
                 while ((matcher = matcher(runText)).find()) {
                	 Object v=params.get(matcher.group(1));
                    runText = matcher.replaceFirst(String.valueOf(v==null?"":v));
                 }
                 //直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                 //所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                 para.removeRun(i);
                 para.insertNewRun(i).setText(runText);
             }
          }
       }
    }

    /**
     * 替换表格里面的变量
     * @param doc 要替换的文档
     * @param params 参数
     */
    private static void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
       Iterator<XWPFTable> iterator = doc.getTablesIterator();
       XWPFTable table;
       List<XWPFTableRow> rows;
       List<XWPFTableCell> cells;
       List<XWPFParagraph> paras;
       while (iterator.hasNext()) {
          table = iterator.next();
          rows = table.getRows();
          for (XWPFTableRow row : rows) {
             cells = row.getTableCells();
             for (XWPFTableCell cell : cells) {
                 paras = cell.getParagraphs();
                 for (XWPFParagraph para : paras) {
                    replaceInPara(para, params);
                 }
             }
          }
       }
    }

    /**
     * 正则匹配字符串
     * @param str
     * @return
     */
    private static Matcher matcher(String str) {
       Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
       Matcher matcher = pattern.matcher(str);
       return matcher;
    }
    private static void close(InputStream is) {
       if (is != null) {
          try {
             is.close();
          } catch (IOException e) {
             e.printStackTrace();
          }
       }
    }
    private static void close(OutputStream os) {
       if (os != null) {
          try {
             os.close();
          } catch (IOException e) {
             e.printStackTrace();
          }
       }
    }

}
