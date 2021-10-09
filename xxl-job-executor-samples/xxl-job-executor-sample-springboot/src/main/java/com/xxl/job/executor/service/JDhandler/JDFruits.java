package com.xxl.job.executor.service.JDhandler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.DataUtils;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.core.JDHttpFactory;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.ddFarm.*;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.annotation.Resource;
import java.net.URISyntaxException;
import java.text.NumberFormat;
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
@ControllerAdvice
public class JDFruits extends IJobHandler {

    @Resource
    private CommonHandler commonHandler;

    JDHttpFactory.HttpInstance httpIns;
    List<String> shareCodes;
    NumberFormat fmt = NumberFormat.getPercentInstance();
    List<Env> envs;
    Task task;
    InitFarm initFarm;
    JDUser userInfo;
    String ua = UserAgentUtil.randomUserAgent();
    Env env;
    HashMap<String, String> fruitMap = new HashMap<>();

    @Override
    public ReturnT<String> execute(String param) {

        //初始化所有农场shareCode
        shareCodes = commonHandler.getShareCode("FRUITS_SHARE_CODE");
        // 初始化所有ck
        envs = commonHandler.getUsers();
        XxlJobLogger.log("***********【初始化完成开始执行农场任务】***********");

        // 2.开始执行任务
        envs.forEach(env -> {
            this.env = env;
            XxlJobLogger.log("\uD83E\uDD1C【{}】东东农场任务开始执行\uD83E\uDD1B", env.getRemarks());
            try {
                userInfo = commonHandler.checkJdUserInfo(env);
                if (userInfo == null) return;
                // 4.生成所需header
                fruitMap = getPublicHeader();
                XxlJobLogger.log("【初始化】{}的农场", env.getRemarks());
                initFarm = initForFarm();
                FarmUserPro farmUserPro = initFarm.getFarmUserPro();
                XxlJobLogger.log("【水果名称】{}", farmUserPro.getName());
                XxlJobLogger.log("【好友互助码】:{}", farmUserPro.getShareCode());
                XxlJobLogger.log("【已成功兑换水果】{}次", farmUserPro.getWinTimes());
                // 初始化农场任务->获取所有农场任务列表
                getTask();
                // 1-签到任务
                flow();
                if (!task.getSignInit().getTodaySigned()) {
                    signTask(env, fruitMap);
                } else {
                    XxlJobLogger.log("【签到任务】已完成");
                }
                // 2-广告任务
                browseAdTaskForFarm();

                // 3-三餐任务
                gotThreeMealForFarm();

                // 6-红包雨任务
                waterRainForFarm();
                // 小鸭子
                getFullCollectionReward();
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
                dohelpFriendWater();
                getTwoHelp();
                // 开始执行浇水任务
                doWater();

                // 领取10次浇水奖励
                firstWaterTaskForFarm();
                // 助力好友
                help();
            } catch (Exception e) {
                e.printStackTrace();
                XxlJobLogger.log("账号似乎存在问题，新号可能导致脚本执行不稳定！！京东服务器返回空数据");
            }
            XxlJobLogger.log("***********【农场任务执行完毕】***********");
        });

        // 农场助力奖励

        XxlJobLogger.log("-----------------------------------------------------");
        XxlJobLogger.log("|*******************开始领取助力奖励*******************|");
        XxlJobLogger.log("-----------------------------------------------------");
        envs.forEach(env -> {
            this.env = env;
            fruitMap = getPublicHeader();
            JDUser userInfo = commonHandler.checkJdUserInfo(env);
            if (userInfo == null) return;
            // 4.生成所需header
            Map<String, String> fruitMap = getPublicHeader();
            try {
                String body = new JDBodyParam()
                        .keyMark("babelChannel").valueMark("121")
                        .keyMark("channel").value(1)
                        .keyMark("version").value(14).buildBody();
                JSONObject masterHelpTaskInitForFarm = httpIns.buildUrl("masterHelpTaskInitForFarm", body, fruitMap);
                JSONArray masterHelpPeoples = masterHelpTaskInitForFarm.getJSONArray("masterHelpPeoples");
                if (!masterHelpTaskInitForFarm.getBoolean("f") && masterHelpPeoples != null && masterHelpPeoples.size() == 5) {
                    String getBody = new JDBodyParam()
                            .keyMark("babelChannel").valueMark("121")
                            .keyMark("channel").value(1)
                            .keyMark("version").value(14).buildBody();
                    JSONObject masterGotFinishedTaskForFarm = httpIns.buildUrl("masterGotFinishedTaskForFarm", getBody, fruitMap);
                    XxlJobLogger.log("【好友助力奖励】获得{}g💧", masterGotFinishedTaskForFarm.get("amount"));
                } else if (!masterHelpTaskInitForFarm.getBoolean("f") && masterHelpPeoples != null) {
                    XxlJobLogger.log("【好友助力】{}人,未达到5人", masterHelpPeoples.size());
                } else if (masterHelpTaskInitForFarm.getBoolean("f")) {
                    XxlJobLogger.log("【好友助力】已经领取");
                }
                doWaterAgain();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        });

        XxlJobLogger.log("-----------------------------------------------------");
        XxlJobLogger.log("|**********************开始预测**********************|");
        XxlJobLogger.log("-----------------------------------------------------");
        for (Env env : envs) {
            this.env = env;
            JDUser userInfo = commonHandler.checkJdUserInfo(env);
            if (userInfo == null) continue;
            fruitMap = getPublicHeader();
            try {
                forecast();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return SUCCESS;
    }

    private void doWaterAgain() throws URISyntaxException {
        InitFarm initFarm;
        initFarm = initForFarm();
        Integer totalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
        if (totalEnergy >= 110) {
            int n = (totalEnergy - 100) / 10;
            XxlJobLogger.log("【剩余水滴】{}g\uD83D\uDCA7继续浇水{}次", totalEnergy, n);
            waterGoodForFarm(n);
        }
    }

    private void dohelpFriendWater() throws URISyntaxException {
        InitFromFriends initFromFriendsAgain = initFromFriends();
        List<Friends> friendsAgain = initFromFriendsAgain.getFriends();
        // 7-给好友浇水
        if (!task.getWaterFriendTaskInit().getWaterFriendGotAward()) {
            waterFriendForFarm(fruitMap, friendsAgain);
            // 交完水领取两次浇水任务
        }
    }

    private void doWater() throws URISyntaxException {
        getTask();
        Integer waterDay = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
        if (waterDay < 10) {
            waterGoodForFarm(10 - waterDay);
        }
    }

    private void getFullCollectionReward() throws URISyntaxException {
        for (int i = 0; i < 6; i++) {
            String body = new JDBodyParam()
                    .keyMark("type").value(2)
                    .keyMark("babelChannel").valueMark("121")
                    .keyMark("channel").value(1)
                    .keyMark("version").value(14).buildBody();
            JSONObject getFullCollectionReward = httpIns.buildUrl("getFullCollectionReward", body, fruitMap);
            if (getFullCollectionReward.containsKey("addWater") && getFullCollectionReward.getInteger("code") == 0) {
                XxlJobLogger.log("【{}】获得{}g💧", getFullCollectionReward.get("title"), getFullCollectionReward.get("addWater"));
            }
        }
    }

    private void waterRainForFarm() throws URISyntaxException {
        if (!task.getWaterRainInit().getF()) {
            Integer winTimes = task.getWaterRainInit().getWinTimes();
            for (int i = 0; i < 2 - winTimes; i++) {
                String body = new JDBodyParam()
                        .keyMark("type").value(1)
                        .keyMark("hongBaoTimes").value(100)
                        .keyMark("version").value(3).buildBody();
                JSONObject totalWaterTaskForFarm = httpIns.buildUrl("waterRainForFarm", body, fruitMap);
                if (totalWaterTaskForFarm.getInteger("code") == 0) {
                    XxlJobLogger.log("【红包雨】获得{}g💧", totalWaterTaskForFarm.get("addEnergy"));
                }
            }
        } else {
            XxlJobLogger.log("【红包雨获得】已完成");
        }


    }

    private void forecast() throws URISyntaxException {
        InitFarm initFarm;
        initFarm = initForFarm();
        Integer totalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
        getTask();
        initFarm = initForFarm();
        Integer waterEveryDayT = task.getTotalWaterTaskInit().getTotalWaterTaskTimes();
        Integer newTotalEnergy = initFarm.getFarmUserPro().getTotalEnergy();
        FarmUserPro farmUserPro = initFarm.getFarmUserPro();
        XxlJobLogger.log("***********【{}】***********", env.getRemarks());
        XxlJobLogger.log("【当前种植】{}", farmUserPro.getName());
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
            XxlJobLogger.log("【预测】{}天之后{} 可兑换水果\uD83C\uDF49", waterD + 1, DataUtils.forecastDay((int) (waterD + 1)));
        }
    }

    private void getTwoHelp() throws URISyntaxException {
        // 领取两次浇水任务
        String twoBody = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("waterFriendGotAwardForFarm", twoBody, fruitMap);
        if (friendsObj.getInteger("code") == 0) {
            XxlJobLogger.log("【好友浇水任务】获得{}g💧", friendsObj.get("addWater"));
        }
    }

    private void addFriends(Env env) {
        shareCodes.forEach(shareCode -> {
            try {
                String addBody = new JDBodyParam()
                        .keyMark("imageUrl").valueMark("")
                        .keyMark("nickName").valueMark("")
                        .keyMark("shareCode").valueMark(shareCode + "-inviteFriend")
                        .keyMark("version").value(4)
                        .keyMark("channel").value(2).buildBody();
                HashMap<String, String> addFriendHeader = new HashMap<>();
                addFriendHeader.put("cookie", env.getEnvValue());
                addFriendHeader.put("User-Agent", UserAgentUtil.randomUserAgent());
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
                .keyMark("shareCode").valueMark(shareCode)
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121")
                .buildBody();
        return httpIns.buildUrl("friendInitForFarm", initFriendBody, fruitMap);
    }

    private void deleteFriends(Map<String, String> fruitMap) throws URISyntaxException {
        InitFromFriends initFromFriends = initFromFriends();
        List<Friends> oldFriends = initFromFriends.getFriends();

        // TODO 获取好友列表
        // 删除所有好友
        if (oldFriends != null && oldFriends.size() > 0) {
            XxlJobLogger.log("【好友数量】共获取到{}个好友", oldFriends.size());
            oldFriends.forEach(friend -> {
                try {
                    String delBody = new JDBodyParam()
                            .keyMark("shareCode").valueMark(friend.getShareCode())
                            .keyMark("version").value(14)
                            .keyMark("channel").value(1)
                            .keyMark("babelChannel").valueMark("121").buildBody();
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
                            .keyMark("shareCode").valueMark(shareCode)
                            .keyMark("version").value(14)
                            .keyMark("channel").value(1)
                            .keyMark("babelChannel").valueMark("121").buildBody();
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

    private InitFromFriends initFromFriends() throws URISyntaxException {
        // 获取好友
        String initBody = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject friendsObj = httpIns.buildUrl("friendListInitForFarm", initBody, fruitMap);
        // 获取好友列表
        return friendsObj.toJavaObject(InitFromFriends.class);
    }

    private JSONObject getTenTask() throws URISyntaxException {
        String initBody = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        return httpIns.buildUrl("totalWaterTaskForFarm", initBody, fruitMap);
    }

    private void signTask(Env env, Map<String, String> fruitMap) throws URISyntaxException {
        // 签到任务
        XxlJobLogger.log("开始初始化【" + env.getRemarks() + "】的签到任务");
        String initBody = new JDBodyParam()
                .keyMark("timestamp").value(System.currentTimeMillis())
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject clockInInitForFarm = httpIns.buildUrl("clockInInitForFarm", initBody, fruitMap);

        // 开始签到
        if (!clockInInitForFarm.getBoolean("todaySigned")) {
            String signBody = new JDBodyParam()
                    .keyMark("type").value(1)
                    .keyMark("version").value(14)
                    .keyMark("channel").value(1)
                    .keyMark("babelChannel").valueMark("121").buildBody();
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

    private void flow() throws URISyntaxException {
        String initBody = new JDBodyParam()
                .keyMark("timestamp").value(System.currentTimeMillis())
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject clockInInitForFarm = httpIns.buildUrl("clockInInitForFarm", initBody, fruitMap);
        List<Theme> themes = clockInInitForFarm.getJSONArray("themes").toJavaList(Theme.class);
        for (Theme theme : themes) {
            if (!theme.getHadGot()) {
                String flowBody = new JDBodyParam()
                        .keyMark("id").valueMark(theme.getId().toString())
                        .keyMark("type").valueMark("theme")
                        .keyMark("step").value(1).buildBody();
                JSONObject flowObj = httpIns.buildUrl("clockInFollowForFarm", flowBody, fruitMap);
                if (flowObj.getInteger("code") == 0) {
                    String getBody = new JDBodyParam()
                            .keyMark("id").valueMark(theme.getId().toString())
                            .keyMark("type").valueMark("theme")
                            .keyMark("step").value(2).buildBody();
                    JSONObject getObj = httpIns.buildUrl("clockInFollowForFarm", getBody, fruitMap);
                    XxlJobLogger.log("【关注领水】[{}]获得{}g💧", theme.getAdDesc(), getObj.get("amount"));
                }
            } else {
                XxlJobLogger.log("【关注领水】[{}]已完成", theme.getAdDesc());
            }
        }
    }

    private void totalWaterTaskForFarm() throws URISyntaxException {
        // 领取十次浇水任务奖励
        if (!task.getTotalWaterTaskInit().getTotalWaterTaskFinished()) {
            String body = new JDBodyParam()
                    .keyMark("version").value(14)
                    .keyMark("channel").value(1)
                    .keyMark("babelChannel").valueMark("121").buildBody();
            JSONObject totalWaterTaskForFarm = httpIns.buildUrl("totalWaterTaskForFarm", body, fruitMap);
            if (totalWaterTaskForFarm.getInteger("code") == 0) {
                XxlJobLogger.log("【十次浇水】获得{}g💧", totalWaterTaskForFarm.get("totalWaterTaskEnergy"));
            }
        } else {
            XxlJobLogger.log("【十次浇水任务】已完成");
        }

    }

    private void additionalAfterWater(Map<String, String> fruitMap) throws URISyntaxException {
        // 领取十次浇水后跳转小程序奖励
        String body = new JDBodyParam()
                .keyMark("type").value(3)
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject gotWaterGoalTaskForFarm = httpIns.buildUrl("gotWaterGoalTaskForFarm", body, fruitMap);
        System.out.println("gotWaterGoalTaskForFarm==" + gotWaterGoalTaskForFarm);
        if (gotWaterGoalTaskForFarm.getInteger("code") == 0) {
            XxlJobLogger.log("【小程序签到】获得{}g💧", gotWaterGoalTaskForFarm.get("addEnergy"));
        }
    }

    private void getTask() throws URISyntaxException {
        String body = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject taskInitForFarm = httpIns.buildUrl("taskInitForFarm", body, fruitMap);
        this.task = taskInitForFarm.toJavaObject(Task.class);
    }

    // 生成农场header
    public HashMap<String, String> getPublicHeader() {
        HashMap<String, String> fruitMap = new HashMap<>();
        fruitMap.put("Host", "api.m.jd.com");
        fruitMap.put("sec-fetch-mode", "cors");
        fruitMap.put("origin", "https://carry.m.jd.com");
        fruitMap.put("accept", "*/*");
        fruitMap.put("sec-fetch-site", "same-site");
        fruitMap.put("x-request-with", "com.jingdong.app.mall");
        fruitMap.put("referer", "https://carry.m.jd.com/babelDiy/Zeus/3KSjXqQabiTuD1cJ28QskrpWoBKT/index.html?babelChannel=121&lng=121.463611&lat=31.021696&sid=5ff1f498bb1025bac5c96263ecafc15w&un_area=2_2813_61130_0");
        fruitMap.put("accept-encoding", "gzip, deflate, br");
        fruitMap.put("accept-language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        fruitMap.put("cookie", env.getEnvValue());
        fruitMap.put("user-agent", ua);
        return fruitMap;
    }

    private void help() {
        XxlJobLogger.log("【开始助力】....");
        for (String shareCode : shareCodes) {
            try {
                HashMap<String, String> helpMap = new HashMap<>();
                helpMap.put("cookie", env.getEnvValue());
                helpMap.put("user-agent", UserAgentUtil.randomUserAgent());
                String body = new JDBodyParam()
                        .keyMark("imageUrl").valueMark("")
                        .keyMark("nickName").valueMark("")
                        .keyMark("shareCode").valueMark(shareCode)
                        .keyMark("babelChannel").value(2)
                        .keyMark("channel").value(1)
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
    private InitFarm initForFarm() throws URISyntaxException {
        Map<String, String> publicHeader = getPublicHeader();
        String body = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject initForFarm = httpIns.buildUrl("initForFarm", body, publicHeader);
        return JSONObject.parseObject(String.valueOf(initForFarm), InitFarm.class);
    }

    // 广告任务领取
    private void browseAdTaskForFarm() {
        // 获取所有广告任务
        if (!task.getGotBrowseTaskAdInit().getF()) {
            GotBrowseTaskAdInit gotBrowseTaskAdInit = task.getGotBrowseTaskAdInit();
            List<AdTask> adTasks = gotBrowseTaskAdInit.getUserBrowseTaskAds();
            // 开始执行浏览广告任务并且领取奖励
            adTasks.forEach(adTask -> {
                try {
                    int type = 0;
                    JSONObject doTaskObj = doTask(fruitMap, adTask, type);
                    if (doTaskObj.getInteger("code") == 0) {
                        type = 1;
                        JSONObject getTaskRewardResObj = doTask(fruitMap, adTask, type);
                        XxlJobLogger.log("【{}】获取到：{}g💧", adTask.getMainTitle(), getTaskRewardResObj.get("amount"));
                    } else {
                        XxlJobLogger.log("【{}】任务已经已经完成", adTask.getMainTitle());
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            });
        } else {
            XxlJobLogger.log("【广告任务】已完成");
        }

    }

    // 5个浏览任务api
    private JSONObject doTask(Map<String, String> taskMap, AdTask adTask, Integer type) throws URISyntaxException {
        String body = new JDBodyParam()
                .keyMark("advertId").valueMark(adTask.getAdvertId())
                .keyMark("type").value(type)
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        return httpIns.buildUrl("browseAdTaskForFarm", body, taskMap);
    }

    // 首次浇水任务
    private void firstWaterTaskForFarm() throws URISyntaxException {
        String body = new JDBodyParam()
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        JSONObject firstWaterTaskForFarm = httpIns.buildUrl("firstWaterTaskForFarm", body, fruitMap);
        if (firstWaterTaskForFarm.getInteger("code") != 0) {
            XxlJobLogger.log("【首次浇水】任务已完成");
        } else {
            XxlJobLogger.log("【首次浇水】获取到：{}g💧", firstWaterTaskForFarm.get("amount"));
        }
        JSONObject tenTask = getTenTask();
        JSONObject todayGotWaterGoalTask = tenTask.getJSONObject("todayGotWaterGoalTask");
        Boolean canPop = todayGotWaterGoalTask.getBoolean("canPop");
        if (canPop) {
            // 领取十次浇水后跳转小程序奖励
            additionalAfterWater(fruitMap);
        }
    }

    // 浇水10次api
    private void waterGoodForFarm(int n) throws URISyntaxException {
        String body = new JDBodyParam()
                .keyMark("type").valueMark("")
                .keyMark("version").value(14)
                .keyMark("channel").value(1)
                .keyMark("babelChannel").valueMark("121").buildBody();
        for (int i = 0; i < n; i++) {
            JSONObject tenWaterObj = httpIns.buildUrl("waterGoodForFarm", body, fruitMap);
            if (!(tenWaterObj.getInteger("code") == 0)) {
                return;
            } else {
                XxlJobLogger.log("【浇水结果】执行第{}浇水成功", i + 1);
            }
        }
    }

    private void gotThreeMealForFarm() throws URISyntaxException {
        XxlJobLogger.log("开始领取定时水滴");
        if (!task.getGotThreeMealInit().getF()) {
            String body = new JDBodyParam()
                    .keyMark("version").value(14)
                    .keyMark("channel").value(1)
                    .keyMark("babelChannel").valueMark("121").buildBody();
            JSONObject threeObj = httpIns.buildUrl("gotThreeMealForFarm", body, fruitMap);
            if (!threeObj.get("code").equals("0")) {
                XxlJobLogger.log("【定时领水】时间未到或者已领取");
            } else {
                XxlJobLogger.log("【定时领水】 获取到：{}g💧", threeObj.get("amount"));
            }
        } else {
            XxlJobLogger.log("【三餐任务】已完成");
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
