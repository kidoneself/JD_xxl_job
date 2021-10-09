package com.xxl.job.core.handler;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;

import java.util.HashMap;

/**
 * job handler
 *
 * @author xuxueli 2015-12-19 19:06:38
 */
public abstract class IJobHandler {


    /**
     * success
     */
    public static final ReturnT<String> SUCCESS = new ReturnT<String>(200, null);
    /**
     * fail
     */
    public static final ReturnT<String> FAIL = new ReturnT<String>(500, null);
    /**
     * fail timeout
     */
    public static final ReturnT<String> FAIL_TIMEOUT = new ReturnT<String>(502, null);


    /**
     * execute handler, invoked when executor receives a scheduling request
     *
     * @param param
     * @return
     * @throws Exception
     */
    public abstract ReturnT<String> execute(String param) throws Exception;

    ReturnT<String> execute2(String param) throws Exception {


        return null;
    }

    /**
     * init handler, invoked when JobThread init
     */
    public abstract void init();


    /**
     * destroy handler, invoked when JobThread destroy
     */
    public abstract void destroy();

    public String httpPost(String url, HashMap<String, String> headers) {
        return HttpRequest.get(url).addHeaders(headers).execute().body();
    }

    public JSONObject httpPostJson(String url, HashMap<String, String> headers) {
        String body = HttpRequest.get(url).addHeaders(headers).execute().body();
        return JSONObject.parseObject(body);
    }

    public void proxy() {
        System.setProperty("http.proxyHost", "192.168.31.115");
        System.setProperty("https.proxyHost", "192.168.31.115");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("https.proxyPort", "8080");
    }
}
