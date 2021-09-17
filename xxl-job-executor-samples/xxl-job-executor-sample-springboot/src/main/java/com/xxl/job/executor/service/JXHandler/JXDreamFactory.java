package com.xxl.job.executor.service.JXHandler;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.JXHttpFactory;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

@JobHandler(value = "JX_Fruits")
@Component
@Slf4j
public class JXDreamFactory extends IJobHandler {

    @Resource
    private EnvMapper envMapper;
    JXHttpFactory.HttpInstance httpIns;
    List<String> shareCodes;
    NumberFormat fmt = NumberFormat.getPercentInstance();


    @Override
    public ReturnT<String> execute(String param) throws Exception {
        // 初始化所有ck
        List<Env> envs = getUsers();

        XxlJobLogger.log("==========================================================");
        // 2.开始执行任务
        envs.forEach(env -> {
            String cookie = env.getEnvValue();
            JDUser userInfo = checkJdUserInfo(env, cookie);
            if (userInfo == null) return;
            XxlJobLogger.log("\uD83E\uDD1C【{}】开始执行京喜工厂任务\uD83E\uDD1B", env.getRemarks());
/*================================================================================================================================================*/
            XxlJobLogger.log("【初始化】{}的工厂", env.getRemarks());
//            initFarm = initForFarm(env, cookie);

            XxlJobLogger.log("=====================================================");
        });

        return SUCCESS;


    }



    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie();
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
        this.httpIns = JXHttpFactory.getInstance();
    }

    @Override
    public void destroy() {

    }


}
