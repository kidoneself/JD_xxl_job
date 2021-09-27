package com.xxl.job.executor.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.po.JDUser;
import lombok.Data;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;


public class JDHttpFactory {
    private static HttpInstance httpInstance;

    @Data
    public static class HttpInstance {
        final String API = "https://api.m.jd.com/client.action";

        // build api
        public JSONObject buildUrl(String functionId, String body, Map<String, String> headersMap) throws URISyntaxException {
            String url = String.format("%s?functionId=%s&appid=%s&body=%s",
                    API, functionId, RequestConstant.WH5, body);
            String res = this.doGet(url, headersMap);
            return JSONObject.parseObject(res);
        }

        /**
         * get请求
         */
        public String doGet(String url, Map<String, String> headersMap) {
            //1.生成httpclient，相当于该打开一个浏览器
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                HttpGet request = new HttpGet(url);
                Header[] headers = HeaderUtil.convertHeader(headersMap);
                request.setHeaders(headers);
                response = httpClient.execute(request);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity, "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //6.关闭
                HttpClientUtils.closeQuietly(response);
                HttpClientUtils.closeQuietly(httpClient);
            }
            return null;
        }
    }


    public static HttpInstance getInstance() {
        if (httpInstance == null) {
            synchronized (HttpInstance.class) {
                if (httpInstance == null) {
                    httpInstance = new HttpInstance();
                }
            }
        }
        return httpInstance;
    }
}
