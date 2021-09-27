package com.xxl.job.executor.service.JDhandler.JDBeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.GetMethodIns;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.core.RequestConstant;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.ShakeList;
import com.xxl.job.executor.po.TaskItemsItem;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * 摇京豆 https://spa.jd.com/home?source=WJ
 */
@JobHandler(value = "JD_ShakeRedEnvelopes")
@Component
@Slf4j
public class ShakeRedEnvelopes extends IJobHandler {
    @Resource
    private EnvMapper envMapper;
    GetMethodIns getIns;
    final String API = "https://api.m.jd.com/client.action";
    Integer times;
    HashMap<String, String> headerMap = new HashMap<>();

    public void getVVipClubLotteryTask(String cookie) throws URISyntaxException, InterruptedException, UnsupportedEncodingException {
        XxlJobLogger.log("执行时间：{}", new Date());
        XxlJobLogger.log("==========初始化摇一摇==========");
        getTaskMap(cookie);
        // 初始化摇盒子
//        JSONObject data = getShakeRed();
//        if (data != null) {
            getAndDoTask();
            getTimes();
            shark();
//        }

    }

    private void getTimes()   {
        String timesBody = new JDBodyParam().keyMark("info").valueMark("freeTimes").buildBody();
        String timeUrl = buildUrl("vvipclub_luckyBox", timesBody, "vip_h5");
        JSONObject jsonObject = getIns.getJsonObject(timeUrl, headerMap);
        this.times = jsonObject.getJSONObject("data").getInteger("freeTimes");
    }

    private void shark() throws InterruptedException {
        //摇一摇
        for (int i = 0; i < times; i++) {
            Thread.sleep(2000);
            XxlJobLogger.log("等待两秒继续执行");
            String body = new JDBodyParam().keyMark("type").valueMark(0).buildBody();
            String url = buildUrl("vvipclub_shaking_lottery", body, "vip_h5");
//            String url = "https://api.m.jd.com/client.action?functionId=vvipclub_shaking_lottery&appid=vip_h5&body=%7Btype%3A%220%22%7D&_=1632409857039";
            JSONObject jsonObject = getIns.getJsonObject(url, headerMap);
            if (jsonObject.getBoolean("success")) {
                if (jsonObject.getJSONObject("data") != null && jsonObject.getJSONObject("data").containsKey("prizeBean")) {
                    System.out.println("京豆：" + jsonObject);
                    XxlJobLogger.log("【第{}摇一摇】获得{}个京豆", i, jsonObject.getJSONObject("data").getInteger("prizeBean"));
                } else if (jsonObject.getJSONObject("data") != null && jsonObject.getJSONObject("data").containsKey("prizeCoupon")) {
                    System.out.println("优惠券：" + jsonObject);
                    XxlJobLogger.log("【第{}摇一摇】获得优惠券", i, jsonObject.getJSONObject("data").getJSONObject("prizeCoupon").getString("limitStr"));
                }
            }


        }
    }

    private void getAndDoTask() {
        String vvipclub_lotteryTask_body = new JDBodyParam()
                .keyMark("info").valueMark("browseTask")
                .keyMark("withItem").value(true).buildBody();
        String vvipclub_lotteryTask = buildUrl("vvipclub_lotteryTask", vvipclub_lotteryTask_body, "vip_h5");
        JSONObject vvipclub_lotteryTask_jsonObject = getIns.getJsonObject(vvipclub_lotteryTask, headerMap);
        ShakeList shakeList = vvipclub_lotteryTask_jsonObject.toJavaObject(ShakeList.class);
        List<TaskItemsItem> sharkLists = shakeList.getData().get(0).getTaskItems();
        // 执行任务
        sharkLists.forEach(sharkList -> {
            if (!sharkList.isFinish()) {
                String body = new JDBodyParam()
                        .keyMark("taskName").valueMark("browseTask")
                        .keyMark("taskItemId").value(sharkList.getId()).buildBody();
                // 浏览所有任务
                String vvipclub_doTask = null;
                try {
                    vvipclub_doTask = buildUrl("vvipclub_doTask", body, "vip_h5");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getIns.getJsonObject(vvipclub_doTask, headerMap);
                XxlJobLogger.log("【已浏览任务】{}", sharkList.getTitle());
            }
        });
    }

    private JSONObject getShakeRed() throws
            URISyntaxException {
        String token = new JDBodyParam()
                .keyMark("token").valueMark("dd2fb032-9fa3-493b-8cd0-0d57cd51812d").buildBody();
        String paramData_body = new JDBodyParam()
                .keyMark("paramData").value(token).buildBody();
        String pg_channel_page_data = buildUrlSharkBean("pg_channel_page_data", paramData_body);
        JSONObject jsonObject = getIns.getJsonObject(pg_channel_page_data, headerMap);
        return jsonObject.getJSONObject("data");
    }

    private void getTaskMap(String cookie) {
        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Referer", "https://vip.m.jd.com/newPage/reward/123dd/slideContent?page=focus");
        headerMap.put("User-Agent", UserAgentUtil.randomUserAgent());
        headerMap.put("Cookie", cookie);
    }

    public String buildUrl(String functionId, String body, String appId) {
        return String.format("%s?functionId=%s&appid=%s&body=%s&_=%s",
                API, functionId, appId, body, System.currentTimeMillis());
    }

    public String buildUrlSharkBean(String functionId, String body) throws URISyntaxException {
        return new URIBuilder()
                .setScheme(RequestConstant.SCHEME)
                .setHost(RequestConstant.BASEHOST)
                .setParameter("t", String.valueOf(System.currentTimeMillis()))
                .setParameter(RequestConstant.APPID, "sharkBean")
                .setParameter(RequestConstant.FUNCTIONID, functionId)
                .setParameter(RequestConstant.BODY, body)
                .build().toString();
    }

    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie("JD_COOKIE");
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return envs;
    }

    //    // 校验用户
//    private JDUser checkJdUserInfo(Env env) {
//        JDUser userInfo;
//        /*================= 获取用户信息 ================= */
//        HashMap<String, String> loginMap = new HashMap<>();
//        // 设置获取用户信息header
//        loginMap.put("cookie", env.getEnvValue());
//        loginMap.put("User-Agent", UserAgentUtil.randomUserAgent());
//        XxlJobLogger.log("【用户信息】{}", env.getRemarks());
//        userInfo = httpIns.getUserInfo(loginMap);
//        if (userInfo == null) {
//            XxlJobLogger.log(env.getRemarks() + "的cookie失效，请获取最新的cookie");
//            return null;
//        }
//        return userInfo;
//    }
    @Override
    public ReturnT<String> execute(String param) throws Exception {
        List<Env> envs = getUsers();
        XxlJobLogger.log("==========================================================");
        envs.forEach(env -> {
//            JDUser userInfo = checkJdUserInfo(env);
//            if (userInfo == null) return;
            // ==========================================================签到领取京豆==========================================================
//            signForBean(env);
            // ==========================================================摇红包==========================================================
            try {
                getVVipClubLotteryTask(env.getEnvValue());
            } catch (URISyntaxException | InterruptedException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        return SUCCESS;
    }

    @Override
    public void init() {
        this.getIns = GetMethodIns.getGetIns();
    }

    @Override
    public void destroy() {

    }
}
