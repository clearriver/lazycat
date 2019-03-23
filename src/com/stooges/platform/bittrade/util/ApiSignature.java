/*
 * Copyright (c) 2005, 2014, STOOGES Technology Co.,Ltd. All rights reserved.
 * STOOGES PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.stooges.platform.bittrade.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author 胡裕
 *
 * 
 */
public class ApiSignature {
    /**
     * 
     */
    public static final String API_HOST = "api.huobi.pro";
    /**
     * 
     */
    public final Logger log = LoggerFactory.getLogger(getClass());
    
    /**
     * 
     */
    public static final DateTimeFormatter DT_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    /**
     * 
     */
    public static final ZoneId ZONE_GMT = ZoneId.of("Z");
    
    /**
     * 
     * @param params
     * @return
     */
    public static String toQueryString(Map<String, String> params) {
        return String.join(
                "&",
                params.entrySet()
                        .stream()
                        .map((entry) -> {
                            return entry.getKey() + "="
                                    + ApiSignature.urlEncode(entry.getValue());
                        }).collect(Collectors.toList()));
    }

    /**
     * 创建一个有效的签名。该方法为客户端调用，将在传入的params中添加AccessKeyId、Timestamp、SignatureVersion、
     * SignatureMethod、Signature参数。
     * 
     * @param appKey
     *            AppKeyId.
     * @param appSecretKey
     *            AppKeySecret.
     * @param method
     *            请求方法，"GET"或"POST"
     * @param host
     *            请求域名，例如"be.huobi.com"
     * @param uri
     *            请求路径，注意不含?以及后的参数，例如"/v1/api/info"
     * @param params
     *            原始请求参数，以Key-Value存储，注意Value不要编码
     */
    public void createSignature(String appKey, String appSecretKey,
            String method, String host, String uri, Map<String, String> params) {
        StringBuilder sb = new StringBuilder(1024);
        sb.append(method.toUpperCase()).append('\n') // GET
                .append(host.toLowerCase()).append('\n') // Host
                .append(uri).append('\n'); // /path
        params.remove("Signature");
        params.put("AccessKeyId", appKey);
        params.put("SignatureVersion", "2");
        params.put("SignatureMethod", "HmacSHA256");
        params.put("Timestamp", gmtNow());
        // build signature:
        SortedMap<String, String> map = new TreeMap<>(params);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append('=').append(urlEncode(value)).append('&');
        }
        // remove last '&':
        sb.deleteCharAt(sb.length() - 1);
        // sign:
        Mac hmacSha256 = null;
        try {
            hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secKey = new SecretKeySpec(
                    appSecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSha256.init(secKey);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("No such algorithm: " + e.getMessage());
        } catch (InvalidKeyException e) {
            throw new RuntimeException("Invalid key: " + e.getMessage());
        }
        String payload = sb.toString();
        byte[] hash = hmacSha256.doFinal(payload
                .getBytes(StandardCharsets.UTF_8));
        String actualSign = Base64.getEncoder().encodeToString(hash);
        params.put("Signature", actualSign);
        if (log.isDebugEnabled()) {
            log.debug("Dump parameters:");
            for (Map.Entry<String, String> entry : params.entrySet()) {
                log.debug("  key: " + entry.getKey() + ", value: "
                        + entry.getValue());
            }
        }
    }

    /**
     * 使用标准URL Encode编码。注意和JDK默认的不同，空格被编码为%20而不是+。
     * 
     * @param s
     *            String字符串
     * @return URL编码后的字符串
     */
    public static String urlEncode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UTF-8 encoding not supported!");
        }
    }

    /**
     * Return epoch seconds
     */
    long epochNow() {
        return Instant.now().getEpochSecond();
    }
    
    /**
     * 
     * @return
     */
    String gmtNow() {
        return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT)
                .format(DT_FORMAT);
    }
}
/**
 * 
 * @author 胡裕
 *
 *
 */
class JsonUtil {
    
    /**
     * 
     * @param obj
     * @return
     * @throws IOException
     */
    public static String writeValue(Object obj) throws IOException {
        return OBJECT_MAPPER.writeValueAsString(obj);
    }
    
    /**
     * 
     * @param s
     * @param ref
     * @return
     * @throws IOException
     */
    public static <T> T readValue(String s, TypeReference<T> ref)
            throws IOException {
        return OBJECT_MAPPER.readValue(s, ref);
    }
    
    /**
     * 
     */
    public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();

    /**
     * 
     * @return
     */
    static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.KEBAB_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        // disabled features:
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
