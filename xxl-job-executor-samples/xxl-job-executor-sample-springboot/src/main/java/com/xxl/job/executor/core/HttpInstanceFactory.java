package com.xxl.job.executor.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.executor.po.JDUser;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


public class HttpInstanceFactory {
    private static HttpInstance httpInstance;

    @Data
    @Slf4j
    public static class HttpInstance {
        // 请求url
        private String url;
        private StringBuffer Log = new StringBuffer();
        // 请求头
        Header[] fixedHeaders = {
                new BasicHeader("accept", "*/*"),
                new BasicHeader("accept-encoding", "gzip, deflate, br"),
                new BasicHeader("accept-language", "zh-CN,zh;q=0.9"),
                new BasicHeader("cache-control", "no-cache"),
                new BasicHeader("origin", "no-cache"),
                new BasicHeader("cache-control", "https://home.m.jd.com"),
                new BasicHeader("referer", "https://home.m.jd.com/myJd/newhome.action?sceneval=2&ufc=&"),
                new BasicHeader("sec-fetch-dest", "empty"),
                new BasicHeader("sec-fetch-mode", "cors"),
                new BasicHeader("sec-fetch-site", "same-site"),
                new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
        };
        Header[] selfHeaders;

        /**
         * 获取用户信息
         *
         * @return
         */
        public JDUser getUserInfo() throws UnsupportedEncodingException {
            String userInfoUrl = "https://wq.jd.com/user_new/info/GetJDUserInfoUnion?sceneval=2";
            String response = doGet(userInfoUrl);
            if (!response.contains("retcode")) {
                Log.append("用户Cookie填写错误，请填写正确的Cookie\n");
                return null;
            }

            JSONObject result = JSONObject.parseObject(response);
            JSONObject o = result.getJSONObject("data").getJSONObject("userInfo").getJSONObject("baseInfo");
            return JSON.toJavaObject(o, JDUser.class);

        }

        public String doGet(String url) {
            //1.生成httpclient，相当于该打开一个浏览器
            CloseableHttpClient httpClient = HttpClients.createDefault();
            CloseableHttpResponse response = null;
            try {
                //2.创建get请求，相当于在浏览器地址栏输入 网址
                HttpGet request = new HttpGet(url);
                Header[] headers = new Header[fixedHeaders.length + selfHeaders.length];
                System.arraycopy(fixedHeaders, 0, headers, 0, fixedHeaders.length);
                System.arraycopy(selfHeaders, 0, headers, fixedHeaders.length, selfHeaders.length);
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
