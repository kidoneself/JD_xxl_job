package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.Body;
import com.xxl.job.executor.core.HttpInstanceFactory;
import com.xxl.job.executor.core.RequestConstant;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.ddFarm.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 东东农场
 */
@JobHandler(value = "JD_Fruits")
@Component
@Slf4j
public class JDFruits extends IJobHandler {

    @Resource
    private EnvMapper envMapper;
    HttpInstanceFactory.HttpInstance httpIns;

    @Override
    public ReturnT<String> execute(String param) {
        // 1. 获取所有需要执行任务的用户
        List<Env> envs = getUsers();
        // 2.开始执行任务
        envs.forEach(env -> {
            XxlJobLogger.log("==========================================================");
            try {
                // 3.获取当cookie
                String cookie = env.getEnvValue();
                
                // 5. 校验当前cookie
                JDUser userInfo = checkJdUserInfo(env, cookie);
                if (userInfo == null) return;

                // 4.生成所需header
                Map<String, String> fruitMap = getPublicHeader(env, cookie);

                // 6.初始化农场
                FarmUserPro farmUserPro = initForFarm(env, cookie);
                Task task = getTask(fruitMap);

                if (!task.getAllTaskFinished()) {
                    // 所有任务是否完成
                    if (!task.getSignInit().getTodaySigned()) {
                        signTask(env, fruitMap);
                    } else {
                        XxlJobLogger.log("已经完成签到任务");
                    }
                    if (!task.getFirstWaterInit().getFirstWaterFinished()) {
                        // 首次浇水任务
                        firstWaterTaskForFarm(userInfo, fruitMap);
                    } else {
                        XxlJobLogger.log("已经完成首次浇水任务");
                    }
                    if (!task.getGotThreeMealInit().getF()) {
                        // 一天三次定时任务获取
                        gotThreeMealForFarm(userInfo, fruitMap);
                    } else {
                        XxlJobLogger.log("当前不在定时领水时间");
                    }
                    if (!task.getTotalWaterTaskInit().getTotalWaterTaskFinished()) {
                        // 领取浇水十次奖励
                        totalWaterTaskForFarm(fruitMap);
                    } else {
                        XxlJobLogger.log("已经完成今日浇水十次任务");
                    }
                    if (!task.getGotBrowseTaskAdInit().getF()) {
                        // 获取广告任务
                        browseAdTaskForFarm(fruitMap, task.getGotBrowseTaskAdInit());
                    } else {
                        XxlJobLogger.log("已经完成今日全部广告任务");
                    }
                }
                if (help(env, cookie, userInfo)) return;
                doFriendsTask(fruitMap);
                // 添加好友
                // TODO 添加判断条件留下100滴水
//                waterGoodForFarm(userInfo, fruitMap);
                // 领取10次浇水奖励
//                getTenTask(fruitMap);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return SUCCESS;
    }

    private void doFriendsTask(Map<String, String> fruitMap) throws URISyntaxException {
        // 获取好友
        String initBody = new Body()
                .Key("lastId").stringValue("null")
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("friendListInitForFarm", initBody, fruitMap);
        InitFromFriends initFromFriends = friendsObj.toJavaObject(InitFromFriends.class);
        // 获取好友列表
        List<Friends> friends = initFromFriends.getFriends();
        // TODO 获取好友列表
        List<String> shareCodes = new ArrayList<>();
        shareCodes.add("b87f644a61cb4ed69b90b8a9701263c7");
        shareCodes.add("81f8c0f0ea554b2385d4f866d4b2203f");
        shareCodes.add("29e99e9f60e4400daa2aa465ce82d8b7");
        shareCodes.add("83e540d0b47445baa362ce87c9cc26c0");
        shareCodes.add("4d0a825a47234a8ea1f0073f42b5fb56");
        shareCodes.add("7ee0b96117b845a292994ded6826bf9d");
        // 删除所有好友
        friends.forEach(friend -> {
            try {
                String delBody = new Body()
                        .Key("shareCode").stringValue(friend.getShareCode())
                        .Key("version").integerValue(14)
                        .Key("channel").integerValue(1)
                        .Key("babelChannel").stringValue("121").buildBody();
                JSONObject delFriendObj = httpIns.buildUrl("deleteFriendForFarm", delBody, fruitMap);
                if (delFriendObj.getString("code").equals("0")) {
                    XxlJobLogger.log("成功删除好友：【" + friend.getNickName() + "】");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        // TODO 添加好友 暂时还有问题
        shareCodes.forEach(shareCode -> {
            try {
                String addBody = new Body()
                        .Key("imageUrl").stringValue("")
                        .Key("nickName").stringValue("")
                        .Key("shareCode").stringValue(shareCode + "-inviteFriend")
                        .Key("version").integerValue(14)
                        .Key("channel").integerValue(2).buildBody();
                JSONObject addFriendObj = httpIns.buildUrl("initForFarm", addBody, fruitMap);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private void getTenTask(Map<String, String> fruitMap) throws URISyntaxException {
        String initBody = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", initBody, fruitMap);
    }

    private void signTask(Env env, Map<String, String> fruitMap) throws URISyntaxException {
        // 签到任务
        XxlJobLogger.log("开始初始化【" + env.getRemarks() + "】的签到任务");
        String initBody = new Body()
                .Key("timestamp").integerValue(new Timestamp(System.currentTimeMillis()))
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject clockInInitForFarm = httpIns.buildUrl("clockInInitForFarm", initBody, fruitMap);

        if (!clockInInitForFarm.getBoolean("todaySigned")) {
            // 开始签到
            String signBody = new Body()
                    .Key("type").integerValue(1)
                    .Key("version").integerValue(14)
                    .Key("channel").integerValue(1)
                    .Key("babelChannel").stringValue("121").buildBody();
            JSONObject clockInForFarm = httpIns.buildUrl("clockInForFarm", signBody, fruitMap);
            XxlJobLogger.log("签到成功获取到：" + clockInForFarm.get("amount") + "g💧");
            // TODO 关注得水滴
        }
    }

    private void totalWaterTaskForFarm(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水任务奖励
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", body, fruitMap);

    }

    private void additionalAfterWater(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水后跳转小程序奖励
        String body = new Body()
                .Key("type").integerValue(3)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject gotWaterGoalTaskForFarm = httpIns.buildUrl("gotWaterGoalTaskForFarm", body, fruitMap);
    }

    private Task getTask(Map<String, String> fruitMap) throws URISyntaxException {
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject taskInitForFarm = httpIns.buildUrl("taskInitForFarm", body, fruitMap);
        return taskInitForFarm.toJavaObject(Task.class);
    }

    // 生成农场header
    private Map<String, String> getPublicHeader(Env env, String cookie) {
        Map<String, String> fruitMap = new HashMap<>();
        fruitMap.put("Host", "api.m.jd.com");
        fruitMap.put("sec-fetch-mode", "cors");
        fruitMap.put("origin", "https://carry.m.jd.com");
        fruitMap.put("accept", "*/*");
        fruitMap.put("sec-fetch-site", "same-site");
        fruitMap.put("x-request-with", "com.jingdong.app.mall");
        fruitMap.put("referer", "https://carry.m.jd.com/babelDiy/Zeus/3KSjXqQabiTuD1cJ28QskrpWoBKT/index.html?babelChannel=121&lng=121.463611&lat=31.021696&sid=5ff1f498bb1025bac5c96263ecafc15w&un_area=2_2813_61130_0");
        fruitMap.put("accept-encoding", "gzip, deflate, br");
        fruitMap.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        fruitMap.put("cookie", cookie);
        fruitMap.put("user-agent", env.getUa());
        return fruitMap;
    }

    // 校验用户
    private JDUser checkJdUserInfo(Env env, String cookie) {
        JDUser userInfo;
        /*================= 获取用户信息 ================= */
        HashMap<String, String> loginMap = new HashMap<>();
        // 设置获取用户信息header
        loginMap.put("cookie", cookie);
        loginMap.put("User-Agent", env.getUa());
        XxlJobLogger.log("开始获取【" + env.getRemarks() + "】的用户信息");
        userInfo = httpIns.getUserInfo(loginMap);
        if (userInfo == null) {
            XxlJobLogger.log(env.getRemarks() + "的cookie失效，请获取最新的cookie");
            return null;
        }
        return userInfo;
    }

    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie();
        XxlJobLogger.log("东东农场开始执行");
        XxlJobLogger.log("共获取到" + envs.size() + "个账号");
        return envs;
    }

    private boolean help(Env env, String cookie, JDUser userInfo) throws URISyntaxException {
        String shareCode = env.getShareCode();
        URI helpUri = new URIBuilder()
                .setScheme(RequestConstant.SCHEME)
                .setHost(RequestConstant.HOST)
                .setParameter(RequestConstant.FUNCTIONID, "initForFarm")
                .setParameter(RequestConstant.BODY, "{\"imageUrl\":\"\",\"nickName\":\"\",\"shareCode\":\"" + "81f8c0f0ea554b2385d4f866d4b2203f" + "\",\"babelChannel\":\"3\",\"version\":2,\"channel\":1}")
                .setParameter(RequestConstant.APPID, "wh5")
                .build();
        HashMap<String, String> helpMap = new HashMap<>();
        helpMap.put("cookie", cookie);
        helpMap.put("user-agent", env.getUa());
        XxlJobLogger.log("开始助力");
        String helpRes = httpIns.doGet(helpUri.toString(), helpMap);
        HelpWater helpWater = JSONObject.parseObject(helpRes, HelpWater.class);
        HelpResult helpResult = helpWater.getHelpResult();
        int code = Integer.parseInt(helpResult.getCode());
        MasterUserInfo masterUserInfo = helpResult.getMasterUserInfo();
        if (masterUserInfo == null) {
            XxlJobLogger.log("【助力好友结果】 shareCode已经过期或者设置错误请重新设置");
            return true;
        }
        Object helpUser = masterUserInfo.getNickName();
        switch (code) {
            case 0:
                XxlJobLogger.log("【助力好友结果】: 已成功给【" + helpUser + "】助力");
                break;
            case 7:
                XxlJobLogger.log("【不能为自己助力哦，跳过自己的shareCode】");
                break;
            case 8:
                XxlJobLogger.log("【助力好友结果】: 助力【" + helpUser + "】失败，您今天助力次数已耗尽");
                break;
            case 9:
                XxlJobLogger.log("【助力好友结果】: 今天已经给【" + helpUser + "】助力过了");
                break;
            case 10:
                XxlJobLogger.log("【助力好友结果】: 好友【" + helpUser + "】已满五人助力");
                break;
        }
        XxlJobLogger.log(userInfo.getNickname() + "剩余助力【" + helpResult.getRemainTimes() + "】次");
        return false;
    }

    // 初始化农场
    private FarmUserPro initForFarm(Env env, String cookie) throws URISyntaxException {
        XxlJobLogger.log("开始初始化【" + env.getRemarks() + "】的农场");
        Map<String, String> publicHeader = getPublicHeader(env, cookie);
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject initForFarm = httpIns.buildUrl("initForFarm", body, publicHeader);
        FarmUserPro farmUserPro = JSONObject.parseObject(initForFarm.getString("farmUserPro"), FarmUserPro.class);
        XxlJobLogger.log("【水果名称】" + farmUserPro.getName());
        XxlJobLogger.log("京东账号【" + farmUserPro.getNickName() + "】好友互助码:" + farmUserPro.getShareCode());
        XxlJobLogger.log("【已成功兑换水果】" + farmUserPro.getWinTimes());
        return farmUserPro;
    }

    // 广告任务领取
    private void browseAdTaskForFarm(Map<String, String> taskMap, GotBrowseTaskAdInit gotBrowseTaskAdInit) {
        // 获取所有广告任务
        List<AdTask> adTasks = gotBrowseTaskAdInit.getUserBrowseTaskAds();
        // 开始执行浏览广告任务并且领取奖励
        adTasks.forEach(adTask -> {
            try {
                int type = 0;
                JSONObject doTaskObj = doTask(taskMap, adTask, type);
                if (doTaskObj.get("code") != "0") {
                    type = 1;
                    JSONObject getTaskRewardResObj = doTask(taskMap, adTask, type);
                    XxlJobLogger.log(adTask.getMainTitle() + "获取到：" + getTaskRewardResObj.get("amount") + "g💧");
                } else {
                    XxlJobLogger.log("今天已经做过" + adTask.getMainTitle() + "任务");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    // 5个浏览任务api
    private JSONObject doTask(Map<String, String> taskMap, AdTask adTask, Integer type) throws URISyntaxException {
        String body = new Body()
                .Key("advertId").stringValue(adTask.getAdvertId())
                .Key("type").integerValue(type)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        return httpIns.buildUrl("browseAdTaskForFarm", body, taskMap);
    }

    // 首次浇水任务
    private void firstWaterTaskForFarm(JDUser userInfo, Map<String, String> taskMap) throws URISyntaxException {
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject firstWaterTaskForFarm = httpIns.buildUrl("firstWaterTaskForFarm", body, taskMap);
        if (firstWaterTaskForFarm.get("code") != "0") {
            XxlJobLogger.log(userInfo.getNickname() + "首次浇水任务已完成");
        } else {
            XxlJobLogger.log(userInfo.getNickname() + "首次浇水 获取到：" + firstWaterTaskForFarm.get("amount") + "g💧");
        }
    }

    // 浇水10次api
    private void waterGoodForFarm(JDUser userInfo, Map<String, String> taskMap) throws URISyntaxException {
        String body = new Body()
                .Key("type").stringValue("")
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        for (int i = 0; i < 10; i++) {
            JSONObject tenWaterObj = httpIns.buildUrl("waterGoodForFarm", body, taskMap);
            if (!tenWaterObj.get("code").equals("0")) {
                XxlJobLogger.log(userInfo.getNickname() + "执行浇水失败");
            } else {
                XxlJobLogger.log(userInfo.getNickname() + "第" + i + "浇水成功");
            }
        }
    }

    private void gotThreeMealForFarm(JDUser userInfo, Map<String, String> taskMap) throws URISyntaxException {
        XxlJobLogger.log("开始领取定时水滴");
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject threeObj = httpIns.buildUrl("waterGoodForFarm", body, taskMap);
        if (!threeObj.get("code").equals("0")) {
            XxlJobLogger.log(userInfo.getNickname() + "定时领水时间未到或者已领取");
        } else {
            XxlJobLogger.log(userInfo.getNickname() + "定时领水 获取到：" + threeObj.get("amount") + "g💧");
        }
    }

    @Override
    public void init() {
        this.httpIns = HttpInstanceFactory.getInstance();
    }

    @Override
    public void destroy() {
    }
}
