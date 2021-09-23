package com.xxl.job.executor.core;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.log.XxlJobLogger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

public class GetMethodIns {
    private static GetMethodIns getIns;

    //1.生成httpclient，相当于该打开一个浏览器
    public JSONObject getJsonObject(String url, HashMap<String, String> headersMap) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(url);
            Header[] headers = HeaderUtil.convertHeader(headersMap);
            request.setHeaders(headers);
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String res = EntityUtils.toString(httpEntity, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(res);
            return jsonObject;
        } catch (
                IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

    public String buildUrl(String functionId, String body, String appid) {
        String url = null;
        try {
            url = new URIBuilder()
                    .setScheme(RequestConstant.SCHEME)
                    .setHost(RequestConstant.HOST)
                    .setParameter(RequestConstant.FUNCTIONID, functionId)
                    .setParameter(RequestConstant.BODY, body)
                    .setParameter(RequestConstant.APPID, appid)
                    .build().toString();
        } catch (URISyntaxException e) {
            XxlJobLogger.log("系统错误，稍后重试~~~~");
        }
        return url;
    }




    public static GetMethodIns getGetIns() {
        if (getIns == null) {
            synchronized (GetMethodIns.class) {
                if (getIns == null) {
                    getIns = new GetMethodIns();
                }
            }
        }
        return getIns;
    }


}
