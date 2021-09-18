package com.xxl.job.executor.service.JDhandler;

import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.JDHttpFactory;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@JobHandler(value = "JD_Beans")
@Component
@Slf4j
public class JDBeans extends IJobHandler {
    @Resource
    private EnvMapper envMapper;
    JDHttpFactory.HttpInstance httpIns;
    List<String> paradiseUuids;
    NumberFormat fmt = NumberFormat.getPercentInstance();


    @Override
    public ReturnT<String> execute(String param) throws Exception {



        this.paradiseUuids = getParadiseUuids();
        XxlJobLogger.log("【助力码】您提供了{}个", paradiseUuids.size());
        // 初始化所有ck
        List<Env> envs = getUsers();
        XxlJobLogger.log("==========================================================");
        envs.forEach(env -> {










        });
        return SUCCESS;
    }




    private List<String> getParadiseUuids() {
        List<Env> envs = envMapper.getAllCookie("FRUITS_SHARE_CODE");
        return envs.stream().map(Env::getEnvValue).collect(Collectors.toList());
    }
    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie("JD_COOKIE");
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return envs;
    }

    // 校验用户
    private JDUser checkJdUserInfo(Env env, String cookie) {
        JDUser userInfo;
        /*================= 获取用户信息 ================= */
        HashMap<String, String> loginMap = new HashMap<>();
        // 设置获取用户信息header
        loginMap.put("cookie", cookie);
        loginMap.put("User-Agent", env.getUa());
        XxlJobLogger.log("【用户信息】{}", env.getRemarks());
        userInfo = httpIns.getUserInfo(loginMap);
        if (userInfo == null) {
            XxlJobLogger.log(env.getRemarks() + "的cookie失效，请获取最新的cookie");
            return null;
        }
        return userInfo;
    }
    @Override
    public void init() {
        this.httpIns = JDHttpFactory.getInstance();
    }

    @Override
    public void destroy() {

    }
}
