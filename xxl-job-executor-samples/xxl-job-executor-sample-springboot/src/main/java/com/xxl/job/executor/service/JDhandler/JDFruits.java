package com.xxl.job.executor.service.JDhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.core.DataUtils;
import com.xxl.job.executor.core.JDHttpFactory;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.ddFarm.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 东东农场
 */
@JobHandler(value = "JD_Fruits")
@Component
@Slf4j
public class JDFruits extends IJobHandler {

    @Resource
    private EnvMapper envMapper;
    JDHttpFactory.HttpInstance httpIns;
    List<String> shareCodes;
    NumberFormat fmt = NumberFormat.getPercentInstance();


    @Override
    public ReturnT<String> execute(String param) {

        //初始化所有农场shareCode
        List<String> shareCodes = new ArrayList<>();
        shareCodes.add("81f8c0f0ea554b2385d4f866d4b2203f");
        shareCodes.add("a2ff105430d2467cb08ba487af4758c1");
        shareCodes.add("4d0a825a47234a8ea1f0073f42b5fb56");
        shareCodes.add("29e99e9f60e4400daa2aa465ce82d8b7");
        shareCodes.add("83e540d0b47445baa362ce87c9cc26c0");
        shareCodes.add("7ee0b96117b845a292994ded6826bf9d");
        shareCodes.add("185ac99da28949f6a91499b59786bbc0");
        shareCodes.add("454dd649c9214b3db94d7d5ff1f61fa2");
        shareCodes.add("b87f644a61cb4ed69b90b8a9701263c7");
        XxlJobLogger.log("【助力码】您提供了{}个", shareCodes.size());
        // todo 助力码不够->补充。
        this.shareCodes = shareCodes;
        // 初始化所有ck
        List<Env> envs = getUsers();
        XxlJobLogger.log("==========================================================");
        // 2.开始执行任务
        envs.forEach(env -> {
            Task task;
            InitFarm initFarm;
            XxlJobLogger.log("\uD83E\uDD1C【{}】开始执行东东农场任务\uD83E\uDD1B", env.getRemarks());
            try {
                // 3.获取当前cookie
                String cookie = env.getEnvValue();
                // 5. 校验当前cookie
                JDUser userInfo = checkJdUserInfo(env, cookie);
                if (userInfo == null) return;
                // 4.生成所需header
                Map<String, String> fruitMap = getPublicHeader(env, cookie);
                XxlJobLogger.log("【初始化】{}的农场", env.getRemarks());
                initFarm = initForFarm(env, cookie);
                FarmUserPro farmUserPro = initFarm.getFarmUserPro();
                XxlJobLogger.log("【水果名称】{}", farmUserPro.getName());
                XxlJobLogger.log("【{}】好友互助码:{}", farmUserPro.getNickName(), farmUserPro.getShareCode());
                XxlJobLogger.log("【已成功兑换水果】{}次", farmUserPro.getWinTimes());
                // 初始化农场任务->获取所有农场任务列表
                task = getTask(fruitMap);
                // 1-签到任务
                flow(fruitMap);
                if (!task.getSignInit().getTodaySigned()) {
                    signTask(env, fruitMap);
                } else {
                    XxlJobLogger.log("【签到任务】已完成");
                }
                // 2-广告任务
                if (!task.getGotBrowseTaskAdInit().getF()) {
                    browseAdTaskForFarm(fruitMap, task.getGotBrowseTaskAdInit());
                } else {
                    XxlJobLogger.log("【广告任务】已完成");
                }
                // 3-三餐任务
                if (!task.getGotThreeMealInit().getF()) {
                    gotThreeMealForFarm(userInfo, fruitMap);
                } else {
                    XxlJobLogger.log("【三餐任务】已完成");
                }

                // 5-十次浇水任务
                if (!task.getTotalWaterTaskInit().getTotalWaterTaskFinished()) {
                    totalWaterTaskForFarm(fruitMap);
                } else {
                    XxlJobLogger.log("【十次浇水任务】已完成");
                }
                // 4-首次浇水任务
                if (!task.getFirstWaterInit().getFirstWaterFinished()) {
                    firstWaterTaskForFarm(userInfo, fruitMap);
                } else {
                    XxlJobLogger.log("【首次浇水任务】已完成");
                }
                // 6-红包雨任务
                if (!task.getWaterRainInit().getF()) {
                    waterRainForFarm(task, fruitMap);
                } else {
                    XxlJobLogger.log("【红包雨获得】已完成");
                }

                // 获取号好友列表
//                InitFromFriends initFromFriends = initFromFriends(fruitMap);
//                List<Friends> friends = initFromFriends.getFriends();
//                if (friends.size() > 2) {
//                    //先删除好友
//                    deleteFriends(fruitMap);
//                }
                // 添加好友
                addFriends(env);
                // 再次获取好友列表
                InitFromFriends initFromFriendsAgain = initFromFriends(fruitMap);
                List<Friends> friendsAgain = initFromFriendsAgain.getFriends();
                // 7-给好友浇水
                if (!task.getWaterFriendTaskInit().getWaterFriendGotAward()) {
                    waterFriendForFarm(fruitMap, friendsAgain);
                    // 交完水领取两次浇水任务
                }
                getTwoHelp(fruitMap);
                // 开始执行浇水任务
                task = getTask(fruitMap);
                Integer waterDay = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
                if (waterDay < 10) {
                    waterGoodForFarm(fruitMap, 10 - waterDay);
                }
                // 领取10次浇水奖励
                JSONObject tenTask = getTenTask(fruitMap);
                JSONObject todayGotWaterGoalTask = tenTask.getJSONObject("todayGotWaterGoalTask");
                Boolean canPop = todayGotWaterGoalTask.getBoolean("canPop");
                if (canPop) {
                    // 领取十次浇水后跳转小程序奖励
                    additionalAfterWater(fruitMap);
                }

                // 助力好友
                help(env, cookie, userInfo);
                // 预测结果
                forecast(env, cookie, userInfo, fruitMap);
            } catch (Exception e) {
                e.printStackTrace();
                XxlJobLogger.log("账号似乎存在问题，新号可能导致脚本执行不稳定！！京东服务器返回空数据");
            }
            XxlJobLogger.log("=====================================================");
        });

        return SUCCESS;
    }

    private void waterRainForFarm(Task task, Map<String, String> fruitMap) throws URISyntaxException {
        Integer winTimes = task.getWaterRainInit().getWinTimes();
        for (int i = 0; i < 2 - winTimes; i++) {
            String body = new JDBodyParam()
                    .Key("type").integerValue(1)
                    .Key("hongBaoTimes").integerValue(100)
                    .Key("version").integerValue(3).buildBody();
            JSONObject totalWaterTaskForFarm = httpIns.buildUrl("waterRainForFarm", body, fruitMap);
            if (totalWaterTaskForFarm.getInteger("code") == 0) {
                XxlJobLogger.log("【红包雨】获得{}g💧", totalWaterTaskForFarm.get("addEnergy"));
            }
        }
    }

    private void forecast(Env env, String cookie, JDUser userInfo, Map<String, String> fruitMap) throws URISyntaxException {
        Task task;
        InitFarm initFarm;
        initFarm = initForFarm(env, cookie);
        Integer totalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
        if (totalEnergy > 110) {
            int n = (totalEnergy - 100) / 10;
            XxlJobLogger.log("【剩余水滴】{}g\uD83D\uDCA7继续浇水{}次", totalEnergy, n);
            waterGoodForFarm(fruitMap, n);
        }
        task = getTask(fruitMap);
        initFarm = initForFarm(env, cookie);
        Integer waterEveryDayT = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
        Integer newTotalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
        XxlJobLogger.log("【今日浇水】{}次", waterEveryDayT);
        XxlJobLogger.log("【剩余水滴】{}g\uD83D\uDCA7", newTotalEnergy);
        Integer treeEnergy = initFarm.getFarmUserPro().getTreeEnergy();
        Integer treeTotalEnergy = initFarm.getFarmUserPro().getTreeTotalEnergy();
        //保留两位小数
        fmt.setMaximumFractionDigits(2);
        //三目运算符避免除0异常
        treeTotalEnergy = treeTotalEnergy == 0 ? 1 : treeTotalEnergy;
        String speed = fmt.format((float) treeEnergy / treeTotalEnergy);
        XxlJobLogger.log("【水果进度】{}，已浇水{}次，还需{}次", speed, treeEnergy / 10, (treeTotalEnergy - treeEnergy) / 10);
        // 预测n天后水果课可兑换功能
        //一共还需浇多少次水
        int waterTotalT = (treeTotalEnergy - treeEnergy - totalEnergy) / 10;
        if (treeEnergy.equals(treeTotalEnergy)) {
            XxlJobLogger.log("【兑换水果】已经可以啦！！\uD83C\uDF8A\uD83C\uDF8A");
        } else {
            double waterD = Math.ceil(waterTotalT / waterEveryDayT);
            XxlJobLogger.log("【预测】{}天之后{} 可兑换水果\uD83C\uDF49", waterD, DataUtils.forecastDay((int) waterD));
        }
    }

    private void getTwoHelp(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取两次浇水任务
        String twoBody = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("waterFriendGotAwardForFarm", twoBody, fruitMap);
        if (friendsObj.getInteger("code") == 0) {
            XxlJobLogger.log("【好友浇水任务】获得{}g💧", friendsObj.get("addWater"));
        }
    }

    private void addFriends(Env env) {
        shareCodes.forEach(shareCode -> {
            try {
                String addBody = new JDBodyParam()
                        .Key("imageUrl").stringValue("")
                        .Key("nickName").stringValue("")
                        .Key("shareCode").stringValue(shareCode + "-inviteFriend")
                        .Key("version").integerValue(4)
                        .Key("channel").integerValue(2).buildBody();
                HashMap<String, String> addFriendHeader = new HashMap<>();
                addFriendHeader.put("cookie", env.getEnvValue());
                addFriendHeader.put("User-Agent", env.getUa());
                JSONObject addFriendObj = httpIns.buildUrl("initForFarm", addBody, addFriendHeader);
                JSONObject addResult = addFriendObj.getJSONObject("helpResult");
                if (addResult.getInteger("code") == 0) {
                    JSONObject masterUserInfoJson = addResult.getJSONObject("masterUserInfo");
                    MasterUserInfo masterUserInfo = masterUserInfoJson.toJavaObject(MasterUserInfo.class);
                    XxlJobLogger.log("【成功添加】{}好友", masterUserInfo.getNickName());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    private JSONObject initFriendFrom(Map<String, String> fruitMap, String shareCode) throws URISyntaxException {
        //初始话好友农场
        String initFriendBody = new JDBodyParam()
                .Key("shareCode").stringValue(shareCode)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121")
                .buildBody();
        return httpIns.buildUrl("friendInitForFarm", initFriendBody, fruitMap);
    }

    private void deleteFriends(Map<String, String> fruitMap) throws URISyntaxException {
        InitFromFriends initFromFriends = initFromFriends(fruitMap);
        List<Friends> oldFriends = initFromFriends.getFriends();

        // TODO 获取好友列表
        // 删除所有好友
        if (oldFriends != null && oldFriends.size() > 0) {
            XxlJobLogger.log("【好友数量】共获取到{}个好友", oldFriends.size());
            oldFriends.forEach(friend -> {
                try {
                    String delBody = new JDBodyParam()
                            .Key("shareCode").stringValue(friend.getShareCode())
                            .Key("version").integerValue(14)
                            .Key("channel").integerValue(1)
                            .Key("babelChannel").stringValue("121").buildBody();
                    JSONObject delFriendObj = httpIns.buildUrl("deleteFriendForFarm", delBody, fruitMap);
                    if (delFriendObj.getString("code").equals("0")) {
                        XxlJobLogger.log("【删除好友】成功删除好友：【" + friend.getNickName() + "】");
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void waterFriendForFarm(Map<String, String> fruitMap, List<Friends> friends) throws URISyntaxException {
        List<Friends> canWaterFriendList = friends.stream().filter(friend -> friend.getFriendState() == 1).collect(Collectors.toList());
        for (int i = 0; i < (Math.min(canWaterFriendList.size(), 2)); i++) {
            String shareCode = canWaterFriendList.get(i).getShareCode();
            JSONObject initFriendFrom = initFriendFrom(fruitMap, shareCode);
            Boolean canWaterFriend = initFriendFrom.getBoolean("canWaterFriend");
            if (canWaterFriend) {
                try {
                    String initBody = new JDBodyParam()
                            .Key("shareCode").stringValue(shareCode)
                            .Key("version").integerValue(14)
                            .Key("channel").integerValue(1)
                            .Key("babelChannel").stringValue("121").buildBody();
                    JSONObject friendsObj = httpIns.buildUrl("waterFriendForFarm", initBody, fruitMap);
                    if (friendsObj.getInteger("code") == 0 && friendsObj.getString("sendCard") != null) {
                        XxlJobLogger.log("【好友浇水任务】获得{}卡", friendsObj.getString("sendCard"));
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private InitFromFriends initFromFriends(Map<String, String> fruitMap) throws URISyntaxException {
        // 获取好友
        String initBody = new JDBodyParam()
                .Key("lastId").stringValue(null)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("friendListInitForFarm", initBody, fruitMap);
        // 获取好友列表
        return friendsObj.toJavaObject(InitFromFriends.class);
    }

    private JSONObject getTenTask(Map<String, String> fruitMap) throws URISyntaxException {
        String initBody = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        return httpIns.buildUrl("totalWaterTaskForFarm", initBody, fruitMap);
    }

    private void signTask(Env env, Map<String, String> fruitMap) throws URISyntaxException {
        // 签到任务
        XxlJobLogger.log("开始初始化【" + env.getRemarks() + "】的签到任务");
        String initBody = new JDBodyParam()
                .Key("timestamp").integerValue(new Timestamp(System.currentTimeMillis()))
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject clockInInitForFarm = httpIns.buildUrl("clockInInitForFarm", initBody, fruitMap);

        // 开始签到
        if (!clockInInitForFarm.getBoolean("todaySigned")) {
            String signBody = new JDBodyParam()
                    .Key("type").integerValue(1)
                    .Key("version").integerValue(14)
                    .Key("channel").integerValue(1)
                    .Key("babelChannel").stringValue("121").buildBody();
            JSONObject clockInForFarm = httpIns.buildUrl("clockInForFarm", signBody, fruitMap);
            XxlJobLogger.log("【签到任务】获取到：{}g💧", clockInForFarm.get("amount"));
            Integer signDay = clockInForFarm.getInteger("signDay");
            XxlJobLogger.log("【签到任务】已经签到：{}天，再连续签到{}天可以获取惊喜礼包！", signDay, 7 - signDay);
            if (signDay == 7) {
                //TODO 领取惊喜礼包
                XxlJobLogger.log("【可以领取惊喜礼包");
            }
        } else {
            XxlJobLogger.log("【签到任务】已完成");
        }
        // 关注得水滴

    }

    private void flow(Map<String, String> fruitMap) throws URISyntaxException {
        String initBody = new JDBodyParam()
                .Key("timestamp").integerValue(new Timestamp(System.currentTimeMillis()))
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject clockInInitForFarm = httpIns.buildUrl("clockInInitForFarm", initBody, fruitMap);

        List<Theme> themes = clockInInitForFarm.getJSONArray("themes").toJavaList(Theme.class);
        for (Theme theme : themes) {
            if (!theme.getHadGot()) {
                String flowBody = new JDBodyParam()
                        .Key("id").stringValue(theme.getId().toString())
                        .Key("type").stringValue("theme")
                        .Key("step").integerValue(2)
                        .Key("version").integerValue(14)
                        .Key("channel").integerValue(1)
                        .Key("babelChannel").stringValue("121").buildBody();
                JSONObject flowObj = httpIns.buildUrl("clockInFollowForFarm", flowBody, fruitMap);
                XxlJobLogger.log("【关注领水】获得{}g💧", flowObj.get("amount"));
            }
        }
    }

    private void totalWaterTaskForFarm(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水任务奖励
        String body = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", body, fruitMap);
        if (totalWaterTaskForFarm.getInteger("code") == 0) {
            XxlJobLogger.log("【十次浇水】获得{}g💧", totalWaterTaskForFarm.get("totalWaterTaskEnergy"));
        }
    }

    private void additionalAfterWater(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水后跳转小程序奖励
        String body = new JDBodyParam()
                .Key("type").integerValue(3)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject gotWaterGoalTaskForFarm = httpIns.buildUrl("gotWaterGoalTaskForFarm", body, fruitMap);
        if (gotWaterGoalTaskForFarm.getInteger("code") == 0) {
            XxlJobLogger.log("【小程序签到】获得{}g💧", gotWaterGoalTaskForFarm.get("amount"));
        }
    }

    private Task getTask(Map<String, String> fruitMap) throws URISyntaxException {
        String body = new JDBodyParam()
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
        XxlJobLogger.log("【用户信息】{}", env.getRemarks());
        userInfo = httpIns.getUserInfo(loginMap);
        if (userInfo == null) {
            XxlJobLogger.log(env.getRemarks() + "的cookie失效，请获取最新的cookie");
            return null;
        }
        return userInfo;
    }

    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie();
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return envs;
    }

    private void help(Env env, String cookie, JDUser userInfo) {
        XxlJobLogger.log("【开始助力】....");
        for (String shareCode : shareCodes) {
            try {
                HashMap<String, String> helpMap = new HashMap<>();
                helpMap.put("cookie", cookie);
                helpMap.put("user-agent", env.getUa());
                String body = new JDBodyParam()
                        .Key("imageUrl").stringValue("")
                        .Key("nickName").stringValue("")
                        .Key("shareCode").stringValue(shareCode)
                        .Key("babelChannel").integerValue(2)
                        .Key("channel").integerValue(1)
                        .buildBody();
                JSONObject gotWaterGoalTaskForFarm = httpIns.buildUrl("initForFarm", body, helpMap);
                HelpWater helpWater = JSONObject.parseObject(gotWaterGoalTaskForFarm.toString(), HelpWater.class);
                HelpResult helpResult = helpWater.getHelpResult();
                if (helpResult.getRemainTimes() == 0) {
                    XxlJobLogger.log("【剩余助力】{}次", helpResult.getRemainTimes());
                    return;
                }
                int code = Integer.parseInt(helpResult.getCode());
                MasterUserInfo masterUserInfo = helpResult.getMasterUserInfo();
                if (masterUserInfo == null) {
                    XxlJobLogger.log("【助力结果】 shareCode已经过期或者设置错误请重新设置");
                    return;
                }
                Object helpUser = masterUserInfo.getNickName();
                switch (code) {
                    case 0:
                        XxlJobLogger.log("【助力结果】: 已成功给【" + helpUser + "】助力");
                        break;
                    case 7:
                        XxlJobLogger.log("【助力结果】不能为自己助力哦，跳过自己的shareCode");
                        break;
                    case 8:
                        XxlJobLogger.log("【助力结果】: 助力[{}]失败，您今天助力次数已耗尽,跳出助力", helpUser);
                    case 9:
                        XxlJobLogger.log("【助力结果】: 今天已经给[{}]助力过了", helpUser);
                        break;
                    case 10:
                        XxlJobLogger.log("【助力结果】: 好友[{}]已满五人助力", helpUser);
                        break;
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    // 初始化农场
    private InitFarm initForFarm(Env env, String cookie) throws URISyntaxException {
        Map<String, String> publicHeader = getPublicHeader(env, cookie);
        String body = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject initForFarm = httpIns.buildUrl("initForFarm", body, publicHeader);
        return JSONObject.parseObject(String.valueOf(initForFarm), InitFarm.class);
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
                    XxlJobLogger.log("【{}】获取到：{}g💧", adTask.getMainTitle(), getTaskRewardResObj.get("amount"));
                } else {
                    XxlJobLogger.log("【{}】任务已经已经完成", adTask.getMainTitle());
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
    }

    // 5个浏览任务api
    private JSONObject doTask(Map<String, String> taskMap, AdTask adTask, Integer type) throws URISyntaxException {
        String body = new JDBodyParam()
                .Key("advertId").stringValue(adTask.getAdvertId())
                .Key("type").integerValue(type)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        return httpIns.buildUrl("browseAdTaskForFarm", body, taskMap);
    }

    // 首次浇水任务
    private void firstWaterTaskForFarm(JDUser userInfo, Map<String, String> taskMap) throws URISyntaxException {
        String body = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject firstWaterTaskForFarm = httpIns.buildUrl("firstWaterTaskForFarm", body, taskMap);
        if (firstWaterTaskForFarm.getInteger("code") != 0) {
            XxlJobLogger.log(userInfo.getNickname() + "【首次浇水】任务已完成");
        } else {
            XxlJobLogger.log(userInfo.getNickname() + "【首次浇水】获取到：{}g💧", firstWaterTaskForFarm.get("amount"));
        }
    }

    // 浇水10次api
    private void waterGoodForFarm(Map<String, String> taskMap, int n) throws URISyntaxException {
        String body = new JDBodyParam()
                .Key("type").stringValue("")
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        for (int i = 0; i < n; i++) {
            JSONObject tenWaterObj = httpIns.buildUrl("waterGoodForFarm", body, taskMap);
            if (!(tenWaterObj.getInteger("code") == 0)) {
                return;
            } else {
                XxlJobLogger.log("【浇水结果】执行第{}浇水成功", i + 1);
            }
        }
    }

    private void gotThreeMealForFarm(JDUser userInfo, Map<String, String> taskMap) throws URISyntaxException {
        XxlJobLogger.log("开始领取定时水滴");
        String body = new JDBodyParam()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject threeObj = httpIns.buildUrl("gotThreeMealForFarm", body, taskMap);
        if (!threeObj.get("code").equals("0")) {
            XxlJobLogger.log("【定时领水】时间未到或者已领取");
        } else {
            XxlJobLogger.log("【定时领水】 获取到：{}g💧", threeObj.get("amount"));
        }
    }

    @Override
    public void init() {
        this.httpIns = JDHttpFactory.getInstance();
    }

    @Override
    public void destroy() {
    }
}
