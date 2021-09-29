package com.xxl.job.executor.service.CommonDo;


import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.HeaderUtil;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class CommonHandler {
    @Resource
    private EnvMapper envMapper;


    public List<String> getShareCode(String type) {
        List<Env> envs = envMapper.getAllCookie(type);
        List<String> shareCodes = envs.stream().map(Env::getEnvValue).collect(Collectors.toList());
        XxlJobLogger.log("【初始化用户】共获取到{}个助力码", envs.size());
        return shareCodes;
    }

    public List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie("JD_COOKIE");
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return envs;
    }

    public JDUser checkJdUserInfo(Env env) {
        String userInfoUrl = "https://wq.jd.com/user_new/info/GetJDUserInfoUnion?sceneval=2";
        HashMap<String, String> loginMap = new HashMap<>();
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
        loginMap.put("cookie", env.getEnvValue());
        loginMap.put("User-Agent", UserAgentUtil.randomUserAgent());
        String response = doGet(userInfoUrl, loginMap);
        if (!response.contains("retcode")) {
            XxlJobLogger.log("用户Cookie填写错误，请填写正确的Cookie");
        }
        JSONObject result = JSONObject.parseObject(response);
        JSONObject data = result.getJSONObject("data");
        if (data.size() == 0) {
            XxlJobLogger.log(env.getRemarks() +"cookie失效，请获取最新的cookie❌❌❌");
            return null;
        }
        JSONObject o = result.getJSONObject("data").getJSONObject("userInfo").getJSONObject("baseInfo");
        JDUser userInfo = o.toJavaObject(JDUser.class);
        if (userInfo == null) {
            XxlJobLogger.log(env.getRemarks() + "的cookie失效，请获取最新的cookie");
            return null;
        }
        XxlJobLogger.log("**********开始用户【{}】**********", env.getRemarks());
        return userInfo;
    }

    public String doGet(String url, Map<String, String> headersMap) {
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
