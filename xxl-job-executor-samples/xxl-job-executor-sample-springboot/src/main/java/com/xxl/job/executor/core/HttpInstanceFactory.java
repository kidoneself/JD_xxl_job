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
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.PrivateKey;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class HttpInstanceFactory {
    private static HttpInstance httpInstance;

    @Data
    public static class HttpInstance {


        // build api
        public JSONObject buildUrl(String functionId, String body, Map<String, String> headersMap) throws URISyntaxException {
            String url = new URIBuilder()
                    .setScheme(RequestConstant.SCHEME)
                    .setHost(RequestConstant.HOST)
                    .setParameter(RequestConstant.FUNCTIONID, functionId)
                    .setParameter(RequestConstant.BODY, body)
                    .setParameter(RequestConstant.APPID, RequestConstant.WH5)
                    .build().toString();
            String res = this.doGet(url, headersMap);
            return JSONObject.parseObject(res);
        }

        /**
         * 获取用户信息
         *
         * @return
         */
        public JDUser getUserInfo(HashMap<String, String> map) {
            String userInfoUrl = "https://wq.jd.com/user_new/info/GetJDUserInfoUnion?sceneval=2";
            HashMap<String, String> loginMap = new HashMap<>(map);
            loginMap.put("accept", "*/*");
            loginMap.put("accept-encoding", "gzip, deflate, br");
            loginMap.put("accept-language", "zh-CN,zh;q=0.9");
            loginMap.put("origin", "no-cache");
            loginMap.put("cache-control", "https://home.m.jd.com");
            loginMap.put("referer", "https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&");
            loginMap.put("sec-fetch-dest", "empty");
            loginMap.put("sec-fetch-mode", "cors");
            loginMap.put("sec-fetch-site", "same-site");
            loginMap.put("Content-Type", "application/x-www-form-urlencoded");
            String response = doGet(userInfoUrl, loginMap);
            if (!response.contains("retcode")) {
                XxlJobLogger.log("用户Cookie填写错误，请填写正确的Cookie");
            }
            JSONObject result = JSONObject.parseObject(response);
            JSONObject data = result.getJSONObject("data");
            if (data.size() == 0) {
                XxlJobLogger.log("cookie失效，请获取最新的cookie");
                return null;
            }
            JSONObject o = result.getJSONObject("data").getJSONObject("userInfo").getJSONObject("baseInfo");
            return JSON.toJavaObject(o, JDUser.class);
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
