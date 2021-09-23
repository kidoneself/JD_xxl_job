package com.xxl.job.executor.service.JDhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.JDHttpFactory;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.service.JDhandler.JDBeanUtils.ShakeRedEnvelopes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URISyntaxException;
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
    @Resource
    private ShakeRedEnvelopes shakeRedEnvelopes;


    @Override
    public ReturnT<String> execute(String param) {
//        this.paradiseUuids = getParadiseUuids();
//        XxlJobLogger.log("【助力码】您提供了{}个", paradiseUuids.size());
        // 初始化所有用户
        List<Env> envs = getUsers();
        XxlJobLogger.log("==========================================================");
        envs.forEach(env -> {
            JDUser userInfo = checkJdUserInfo(env);
            if (userInfo == null) return;
            // ==========================================================签到领取京豆==========================================================
//            signForBean(env);
            // ==========================================================摇红包==========================================================
//            try {
//                shakeRedEnvelopes.getVVipClubLotteryTask(env.getEnvValue());
//            } catch (URISyntaxException | InterruptedException e) {
//                e.printStackTrace();
//            }
        });
        return SUCCESS;
    }

    private void signForBean(Env env) {
        try {
            String body = "signBeanIndex&appid=ld";
            HashMap<String, String> beanMap = new HashMap<>();
            beanMap.put("cookie", env.getEnvValue());
            JSONObject signBeanIndex = httpIns.buildBeanUrl("signBeanIndex", body, beanMap);
            if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 1) {
                XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), signBeanIndex.getJSONObject("data").getJSONObject("dailyAward").getJSONObject("beanAward").getString("beanCount"));
            } else if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 2) {
                XxlJobLogger.log("【领京豆签到】[{}]已签过 ⚠", env.getRemarks());
            } else if (signBeanIndex.getInteger("code") != 0) {
                XxlJobLogger.log("【领京豆签到失败】...请稍后重试", env.getRemarks());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
    private JDUser checkJdUserInfo(Env env) {
        JDUser userInfo;
        /*================= 获取用户信息 ================= */
        HashMap<String, String> loginMap = new HashMap<>();
        // 设置获取用户信息header
        loginMap.put("cookie", env.getEnvValue());
        loginMap.put("User-Agent", UserAgentUtil.randomUserAgent());
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
