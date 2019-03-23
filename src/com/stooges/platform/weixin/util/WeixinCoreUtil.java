/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.weixin.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

/**
 * @author 胡裕
 * 核心工具类
 * 
 */
public class WeixinCoreUtil {
    /**
     * 图片素材
     */
    public static final String MATTER_IMG = "image";
    /**
     * 声音素材
     */
    public static final String MATTER_VOICE = "voice";
    /**
     * 视频素材
     */
    public static final String MATTER_VIDEO = "video";
    /**
     * 缩略图
     */
    public static final String MATTER_THUMB = "thumb";
    /**
     * 图文
     */
    public static final String MATTER_NEWS = "news";
    /**
     * 定义TOKEN失效集合
     */
    public static Map<String,PublicToken> tokenTimeOut = new HashMap<String,PublicToken>();
    
    /**
     * 根据内容类型判断文件扩展名
     * 
     * @param contentType 内容类型
     * @return
     */
    public static String getFileExt(String contentType) {
        String fileExt = "";
        if ("image/jpeg".equals(contentType))
            fileExt = ".jpg";
        else if ("audio/mpeg".equals(contentType))
            fileExt = ".mp3";
        else if ("audio/amr".equals(contentType))
            fileExt = ".amr";
        else if ("video/mp4".equals(contentType))
            fileExt = ".mp4";
        else if ("video/mpeg4".equals(contentType))
            fileExt = ".mp4";
        return fileExt;
    }
    /**
     * 添加视频素材
     */
    public static Map<String,Object> addVideoMatter(String accessToken,String fileUrl,
            String title, String introduction){
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin"
                + "/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";
        uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE",MATTER_VIDEO);
        HttpURLConnection downloadCon = null;  
        InputStream inputStream = null;  
        try {  
            URL urlFile = new URL(fileUrl);  
            downloadCon = (HttpURLConnection) urlFile.openConnection();  
            inputStream = downloadCon.getInputStream();  
                  
            URL urlObj = new URL(uploadMediaUrl);   
            HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();  
            conn.setConnectTimeout(5000);  
            conn.setReadTimeout(30000);    
            conn.setDoOutput(true);    
            conn.setDoInput(true);    
            conn.setUseCaches(false);    
            conn.setRequestMethod("POST");   
            conn.setRequestProperty("Connection", "Keep-Alive");  
            conn.setRequestProperty("Cache-Control", "no-cache");  
            String boundary = "-----------------------------"+System.currentTimeMillis();  
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);  
                          
            OutputStream output = conn.getOutputStream();  
            output.write(("--" + boundary + "\r\n").getBytes());  
            String regex = ".*/([^\\.]+)";  
            output.write(String.format("Content-Disposition: form-data; name=\"media\"; "
                    + "filename=\"%s\"\r\n", fileUrl.replaceAll(regex, "$1")).getBytes());    
            output.write("Content-Type: video/mp4 \r\n\r\n".getBytes());  
            int bytes = 0;  
            byte[] bufferOut = new byte[1024];  
            while ((bytes = inputStream.read(bufferOut)) != -1) {  
                output.write(bufferOut, 0, bytes);  
            }  
            inputStream.close();  
              
            output.write(("--" + boundary + "\r\n").getBytes());  
            output.write("Content-Disposition: form-data; name=\"description\";\r\n\r\n".getBytes());  
            output.write(String.format("{\"title\":\"%s\", \"introduction\":\"%s\"}",title,introduction).getBytes());  
            output.write(("\r\n--" + boundary + "--\r\n\r\n").getBytes());  
            output.flush();  
            output.close();  
            inputStream.close();  
            InputStream resp = conn.getInputStream();  
            StringBuffer sb = new StringBuffer();  
            while((bytes= resp.read(bufferOut))>-1)  
            sb.append(new String(bufferOut,0,bytes,"utf-8"));  
            resp.close();  
            return JSON.parseObject(sb.toString(),Map.class);
        } catch (IOException e) {  
            e.printStackTrace();    
            return null;    
        }  
    }
    
    /**
     * 上传图片
     * @param accessToken
     * @param mediaFileUrl
     * @return
     */
    public static String uploadImg(String accessToken,String mediaFileUrl){
     // 拼装请求地址
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin"
                + "/media/uploadimg?access_token=ACCESS_TOKEN";
        uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken);
        // 定义数据分隔符
        String boundary = "------------7da2e536604c8";
        try {
            URL uploadUrl = new URL(uploadMediaUrl);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            // 设置请求头Content-Type
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 获取媒体文件上传的输出流（往微信服务器写数据）
            OutputStream outputStream = uploadConn.getOutputStream();
            URL mediaUrl = new URL(mediaFileUrl);
            HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
            meidaConn.setDoOutput(true);
            meidaConn.setRequestMethod("GET");
            // 从请求头中获取内容类型
            String contentType = meidaConn.getHeaderField("Content-Type");
            // 根据内容类型判断文件扩展名
            String fileExt = getFileExt(contentType);
            // 请求体开始
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(String.format("Content-Disposition: form-data; "
                    + "name=\"media\"; filename=\"file1%s\"\r\n", fileExt).getBytes());
            outputStream.write(String.format("Content-Type: "
                    + "%s\r\n\r\n", contentType).getBytes());

            // 获取媒体文件的输入流（读取文件）
            BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1) {
                // 将媒体文件写到输出流（往微信服务器写数据）
                outputStream.write(buf, 0, size);
            }
            // 请求体结束
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
            outputStream.close();
            bis.close();
            meidaConn.disconnect();

            // 获取媒体文件上传的输入流（从微信服务器读数据）
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            uploadConn.disconnect();
            Map<String,Object> resultMap = JSON.parseObject(buffer.toString(),Map.class);
            return resultMap.get("url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * 创建素材
     * @param accessToken
     * @param type
     * @param mediaFileUrl
     */
    public static Map<String,Object> addMaterials(String accessToken, String type, String mediaFileUrl){
        // 拼装请求地址
        String uploadMediaUrl = "https://api.weixin.qq.com/cgi-bin"
                + "/material/add_material?access_token=ACCESS_TOKEN&type=TYPE";
        uploadMediaUrl = uploadMediaUrl.replace("ACCESS_TOKEN", accessToken).replace("TYPE", type);
        // 定义数据分隔符
        String boundary = "------------7da2e536604c8";
        try {
            URL uploadUrl = new URL(uploadMediaUrl);
            HttpURLConnection uploadConn = (HttpURLConnection) uploadUrl.openConnection();
            uploadConn.setDoOutput(true);
            uploadConn.setDoInput(true);
            uploadConn.setRequestMethod("POST");
            // 设置请求头Content-Type
            uploadConn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            // 获取媒体文件上传的输出流（往微信服务器写数据）
            OutputStream outputStream = uploadConn.getOutputStream();
            URL mediaUrl = new URL(mediaFileUrl);
            HttpURLConnection meidaConn = (HttpURLConnection) mediaUrl.openConnection();
            meidaConn.setDoOutput(true);
            meidaConn.setRequestMethod("GET");
            // 从请求头中获取内容类型
            String contentType = meidaConn.getHeaderField("Content-Type");
            // 根据内容类型判断文件扩展名
            String fileExt = getFileExt(contentType);
            // 请求体开始
            outputStream.write(("--" + boundary + "\r\n").getBytes());
            outputStream.write(String.format("Content-Disposition: form-data; "
                    + "name=\"media\"; filename=\"file1%s\"\r\n", fileExt).getBytes());
            outputStream.write(String.format("Content-Type: "
                    + "%s\r\n\r\n", contentType).getBytes());

            // 获取媒体文件的输入流（读取文件）
            BufferedInputStream bis = new BufferedInputStream(meidaConn.getInputStream());
            byte[] buf = new byte[8096];
            int size = 0;
            while ((size = bis.read(buf)) != -1) {
                // 将媒体文件写到输出流（往微信服务器写数据）
                outputStream.write(buf, 0, size);
            }
            // 请求体结束
            outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
            outputStream.close();
            bis.close();
            meidaConn.disconnect();

            // 获取媒体文件上传的输入流（从微信服务器读数据）
            InputStream inputStream = uploadConn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer buffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            bufferedReader.close();
            inputStreamReader.close();
            // 释放资源
            inputStream.close();
            inputStream = null;
            uploadConn.disconnect();
            return JSON.parseObject(buffer.toString(),Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
