package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.HttpInstanceFactory;
import com.xxl.job.executor.core.config.XxlJobConfig;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 跨平台Http任务
 *
 * @author xuxueli 2018-09-16 03:48:34
 */
@JobHandler(value = "httpJobHandler")
@Component
@Slf4j
public class HttpJobHandler extends IJobHandler {

    @Resource
    private EnvMapper envMapper;
    private List<Env> envs;


    @Override
    public ReturnT<String> execute(String param) {
        XxlJobLogger.log("\n\nXXXX开始");
        XxlJobLogger.log("共获取到" + envs.size() + "个账号");
        //获取Http实例
        HttpInstanceFactory.HttpInstance instance = HttpInstanceFactory.getInstance();
        // 开始执行所有ck
        envs.forEach(env -> {
            // 获取当前账号的ck
            String cookie = env.getEnvValue();
            // 设置当前账号的header
            Header[] selfHeaders = {
                    new BasicHeader("cookie", cookie),
                    new BasicHeader("User-Agent",
                            "jdapp;android;10.0.2;10;network/wifi;Mozilla/5.0 (Linux; Android 10; GM1910 Build/QKQ1.190716.003; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/77.0.3865.120 MQQBrowser/6.2 TBS/045230 Mobile Safari/537.36")
            };
            instance.setSelfHeaders(selfHeaders);
            JDUser userInfo = null;
            try {
                // 获取用户信息
                userInfo = instance.getUserInfo();
                XxlJobLogger.log("开始助力");
                String s = instance.doGet("https://api.m.jd.com/client.action?functionId=initForFarm&appid=wh5&body=%7B%22imageUrl%22%3A%22%22%2C%22nickName%22%3A%22%22%2C%22shareCode%22%3A%2281f8c0f0ea554b2385d4f866d4b2203f%22%2C%22babelChannel%22%3A%223%22%2C%22version%22%3A2%2C%22channel%22%3A1%7D");
                JSONObject result = JSONObject.parseObject(s);
                JSONObject o = result.getJSONObject("data").getJSONObject("userInfo").getJSONObject("baseInfo");
//                instance.doGet()
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            XxlJobLogger.log(userInfo.toString());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return SUCCESS;
    }

    @Override
    public void init() {
        // 获取所有账号
        List<Env> envs = envMapper.getAllCookie();
        this.envs = envs;
    }

    @Override
    public void destroy() {

    }

}
