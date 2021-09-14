package com.xxl.job.executor.service.jobhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.Body;
import com.xxl.job.executor.core.DataUtils;
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
import java.text.NumberFormat;
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
    List<String> shareCodes;
    NumberFormat fmt = NumberFormat.getPercentInstance();


    @Override
    public ReturnT<String> execute(String param) {

        //初始化所有农场shareCode
        List<String> shareCodes = new ArrayList<>();
        shareCodes.add("b87f644a61cb4ed69b90b8a9701263c7");
        shareCodes.add("81f8c0f0ea554b2385d4f866d4b2203f");
        shareCodes.add("29e99e9f60e4400daa2aa465ce82d8b7");
        shareCodes.add("83e540d0b47445baa362ce87c9cc26c0");
        shareCodes.add("4d0a825a47234a8ea1f0073f42b5fb56");
        shareCodes.add("7ee0b96117b845a292994ded6826bf9d");
        XxlJobLogger.log("您提供了{}个账号的农场助力码", shareCodes.size());
        // todo 助力码不够->补充。
        this.shareCodes = shareCodes;
        // 初始化所有ck
        List<Env> envs = getUsers();
        XxlJobLogger.log("==========================================================");
        // 2.开始执行任务
        envs.forEach(env -> {
            Task task;
            InitFarm initFarm;
            XxlJobLogger.log("\uD83E\uDD1C开始执行【{}】的东东农场任务\uD83E\uDD1B", env.getRemarks());
            try {
                // 3.获取当前cookie
                String cookie = env.getEnvValue();
                // 5. 校验当前cookie
                JDUser userInfo = checkJdUserInfo(env, cookie);
                if (userInfo == null) return;
                // 4.生成所需header
                Map<String, String> fruitMap = getPublicHeader(env, cookie);
                initFarm = initForFarm(env, cookie);
                FarmUserPro farmUserPro = initFarm.getFarmUserPro();
                XxlJobLogger.log("【水果名称】{}", farmUserPro.getName());
                XxlJobLogger.log("京东账号【{}】好友互助码:{}", farmUserPro.getNickName(), farmUserPro.getShareCode());
                XxlJobLogger.log("【已成功兑换水果】{}次", farmUserPro.getWinTimes());
                // 初始化农场任务->获取所有农场任务列表
                task = getTask(fruitMap);
                XxlJobLogger.log("开始完成农场任务");
                // 签到任务
                if (!task.getSignInit().getTodaySigned()) {
                    signTask(env, fruitMap);
                } else {
                    XxlJobLogger.log("已经完成签到任务");
                }
                // 一天三次定时任务获取
                if (!task.getGotThreeMealInit().getF()) {
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
                help(env, cookie, userInfo);
                doFriendsTask(fruitMap, env);
                // 添加好友
                // 6.获取用户农场所有信息
                initFarm = initForFarm(env, cookie);
                task = getTask(fruitMap);
                Integer waterDay = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
                if (initFarm.getFarmUserPro().getTotalEnergy() > 100) {
                    waterGoodForFarm(userInfo, fruitMap, 10 - waterDay);
                }
                // 领取10次浇水奖励
                getTenTask(fruitMap);
                // 领取十次浇水后跳转小程序奖励
                additionalAfterWater(fruitMap);
                // 首次浇水任务
                if (!task.getFirstWaterInit().getFirstWaterFinished()) {
                    firstWaterTaskForFarm(userInfo, fruitMap);
                } else {
                    XxlJobLogger.log("已经完成首次浇水任务");
                }
                // 预测结果
                //今天到到目前为止，浇了多少次水
                task = getTask(fruitMap);
                initFarm = initForFarm(env, cookie);
                Integer totalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
                if (totalEnergy > 110) {
                    int n = (totalEnergy - 100) / 10;
                    XxlJobLogger.log("【剩余 水滴】{}g\uD83D\uDCA7继续浇水{}次", totalEnergy, n);
                    waterGoodForFarm(userInfo, fruitMap, n);
                }
                initFarm = initForFarm(env, cookie);
                Integer waterEveryDayT = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
                Integer newTotalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
                XxlJobLogger.log("【今日共浇水】{}次", waterEveryDayT);
                XxlJobLogger.log("【剩余 水滴】{}g\uD83D\uDCA7", newTotalEnergy);
                Integer treeEnergy = initFarm.getFarmUserPro().getTreeEnergy();
                Integer treeTotalEnergy = initFarm.getFarmUserPro().getTreeTotalEnergy();
                //保留两位小数
                fmt.setMaximumFractionDigits(2);
                //三目运算符避免除0异常
                treeTotalEnergy = treeTotalEnergy == 0 ? 1 : treeTotalEnergy;
                String speed = fmt.format((float) treeEnergy / treeTotalEnergy);
                XxlJobLogger.log("【水果\uD83C\uDF49进度】{}", speed);
                XxlJobLogger.log("已浇水{}次，还需{}次", treeEnergy / 10, (treeTotalEnergy - treeEnergy) / 10);
                // 预测n天后水果课可兑换功能
                //一共还需浇多少次水
                int waterTotalT = (treeTotalEnergy - treeEnergy - totalEnergy) / 10;
                if (treeEnergy.equals(treeTotalEnergy)) {
                    XxlJobLogger.log("\uD83C\uDF8A\uD83C\uDF8A已经可以兑换水果啦！！\uD83C\uDF8A\uD83C\uDF8A");
                } else {
                    double waterD = Math.ceil(waterTotalT / waterEveryDayT);
                    XxlJobLogger.log("【预测】{}天之后{} 可兑换水果\uD83C\uDF49", waterD, DataUtils.forecastDay((int) waterD));
                }
            } catch (Exception e) {
                e.printStackTrace();
                XxlJobLogger.log("账号似乎存在问题，新号可能导致脚本执行不稳定！！京东服务器返回空数据");
            }
            XxlJobLogger.log("=====================================================");
        });

        return SUCCESS;
    }

    private void doFriendsTask(Map<String, String> fruitMap, Env env) throws URISyntaxException {
        List<Friends> oldFriends = getFriendsList(fruitMap);
        // TODO 获取好友列表
        // 删除所有好友
        if (oldFriends != null && oldFriends.size() > 0) {
            oldFriends.forEach(friend -> {
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
        }
        // TODO 添加好友 暂时还有问题
        shareCodes.forEach(shareCode -> {
            try {
                String addBody = new Body()
                        .Key("imageUrl").stringValue("")
                        .Key("nickName").stringValue("")
                        .Key("shareCode").stringValue(shareCode + "-inviteFriend")
                        .Key("version").integerValue(4)
                        .Key("channel").integerValue(2).buildBody();
                HashMap<String, String> addFriendHeader = new HashMap<>();
                addFriendHeader.put("cookie", env.getEnvValue());
                addFriendHeader.put("User-Agent", env.getUa());
                JSONObject addFriendObj = httpIns.buildUrl("initForFarm", addBody, addFriendHeader);
                System.out.println("addFriendObj---" + addFriendObj);
                //初始话好友农场
                String initFriendBody = new Body()
                        .Key("shareCode").stringValue("")
                        .Key("version").integerValue(14)
                        .Key("channel").integerValue(1)
                        .Key("babelChannel").stringValue("121")
                        .buildBody();
                JSONObject initFriendObj = httpIns.buildUrl("friendInitForFarm", initFriendBody, fruitMap);
                System.out.println("friendInitForFarm---" + initFriendObj);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        List<Friends> newFriends = getFriendsList(fruitMap);
        // 给好友浇水Task task;
        Boolean waterFriendGotAward = getTask(fruitMap).getWaterFriendTaskInit().getWaterFriendGotAward();
        if (!waterFriendGotAward) {
            for (int i = 0; i < (Math.min(newFriends.size(), 2)); i++) {
                try {
                    String initBody = new Body()
                            .Key("shareCode").stringValue(newFriends.get(i).getShareCode())
                            .Key("version").integerValue(14)
                            .Key("channel").integerValue(1)
                            .Key("babelChannel").stringValue("121").buildBody();
                    JSONObject friendsObj = httpIns.buildUrl("waterFriendForFarm", initBody, fruitMap);
                    if (friendsObj.getInteger("code") == 0) {
                        XxlJobLogger.log("【给好友浇水】获得{}卡", friendsObj.getString("sendCard"));
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }
        // 领取两次浇水任务
        String twoBody = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("waterFriendGotAwardForFarm", twoBody, fruitMap);
        if (friendsObj.getInteger("code") == 0) {
            XxlJobLogger.log("【两次好友浇水任务】获得{}g💧", friendsObj.get("addWater"));
        }
    }

    private List<Friends> getFriendsList(Map<String, String> fruitMap) throws URISyntaxException {
        // 获取好友
        String initBody = new Body()
                .Key("lastId").stringValue(null)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("friendListInitForFarm", initBody, fruitMap);
        InitFromFriends initFromFriends = friendsObj.toJavaObject(InitFromFriends.class);
        // 获取好友列表
        return initFromFriends.getFriends();
    }

    private void getTenTask(Map<String, String> fruitMap) throws URISyntaxException {
        String initBody = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", initBody, fruitMap);
        System.out.println(totalWaterTaskForFarm.toString());
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
        System.out.println("clockInInitForFarm=====" + clockInInitForFarm);
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
        }else {
//            XxlJobLogger.log("签到成功获取到：" + clockInForFarm.get("amount") + "g💧");
        }
    }

    private void totalWaterTaskForFarm(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水任务奖励
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", body, fruitMap);
        if (totalWaterTaskForFarm.getInteger("code") == 0) {
            XxlJobLogger.log("【十次浇水奖励】获得{}g💧", totalWaterTaskForFarm.get("totalWaterTaskEnergy"));
        }
    }

    private void additionalAfterWater(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水后跳转小程序奖励
        String body = new Body()
                .Key("type").integerValue(3)
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject gotWaterGoalTaskForFarm = httpIns.buildUrl("gotWaterGoalTaskForFarm", body, fruitMap);
        if (gotWaterGoalTaskForFarm.getInteger("code") == 0) {
            XxlJobLogger.log("【十次浇水奖励】获得{}g💧", gotWaterGoalTaskForFarm.get("amount"));
        }
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

    private boolean help(Env env, String cookie, JDUser userInfo) {
        XxlJobLogger.log("开始助力");
        shareCodes.forEach(shareCode -> {
            try {
                URI helpUri = new URIBuilder()
                        .setScheme(RequestConstant.SCHEME)
                        .setHost(RequestConstant.HOST)
                        .setParameter(RequestConstant.FUNCTIONID, "initForFarm")
                        .setParameter(RequestConstant.BODY, "{\"imageUrl\":\"\",\"nickName\":\"\",\"shareCode\":\"" + shareCode + "\",\"babelChannel\":\"3\",\"version\":2,\"channel\":1}")
                        .setParameter(RequestConstant.APPID, "wh5")
                        .build();
                HashMap<String, String> helpMap = new HashMap<>();
                helpMap.put("cookie", cookie);
                helpMap.put("user-agent", env.getUa());
                String helpRes = httpIns.doGet(helpUri.toString(), helpMap);
                HelpWater helpWater = JSONObject.parseObject(helpRes, HelpWater.class);
                HelpResult helpResult = helpWater.getHelpResult();
                if (helpResult.getRemainTimes() > 0) {
                    int code = Integer.parseInt(helpResult.getCode());
                    MasterUserInfo masterUserInfo = helpResult.getMasterUserInfo();
                    if (masterUserInfo == null) {
                        XxlJobLogger.log("【助力好友结果】 shareCode已经过期或者设置错误请重新设置");
                        return;
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
                            XxlJobLogger.log("【助力好友结果】: 助力【" + helpUser + "】失败，您今天助力次数已耗尽,跳出助力");
                        case 9:
                            XxlJobLogger.log("【助力好友结果】: 今天已经给【" + helpUser + "】助力过了");
                            break;
                        case 10:
                            XxlJobLogger.log("【助力好友结果】: 好友【" + helpUser + "】已满五人助力");
                            break;
                    }
                    XxlJobLogger.log(userInfo.getNickname() + "剩余助力【" + helpResult.getRemainTimes() + "】次");
                } else {
                    XxlJobLogger.log("【助力好友结果】: 助力失败，您今天助力次数已耗尽,跳出助力");
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        });
        return false;

    }

    // 初始化农场
    private InitFarm initForFarm(Env env, String cookie) throws URISyntaxException {
        XxlJobLogger.log("开始初始化【" + env.getRemarks() + "】的农场");
        Map<String, String> publicHeader = getPublicHeader(env, cookie);
        String body = new Body()
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        JSONObject initForFarm = httpIns.buildUrl("initForFarm", body, publicHeader);
        InitFarm initFarm = JSONObject.parseObject(String.valueOf(initForFarm), InitFarm.class);

        return initFarm;
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
    private void waterGoodForFarm(JDUser userInfo, Map<String, String> taskMap, int n) throws URISyntaxException {
        String body = new Body()
                .Key("type").stringValue("")
                .Key("version").integerValue(14)
                .Key("channel").integerValue(1)
                .Key("babelChannel").stringValue("121").buildBody();
        for (int i = 0; i < n; i++) {
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
        JSONObject threeObj = httpIns.buildUrl("gotThreeMealForFarm", body, taskMap);
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
