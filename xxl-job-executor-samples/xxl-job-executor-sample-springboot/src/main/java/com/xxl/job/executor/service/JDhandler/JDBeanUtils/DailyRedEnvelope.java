package com.xxl.job.executor.service.JDhandler.JDBeanUtils;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.GetMethodIns;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.mapper.EnvMapper;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.dailyRed.DailyRed;
import com.xxl.job.executor.po.dailyRed.TurntableBrowserAdsItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 天天红包 ->https://pro.m.jd.com/mall/active/3WydMN2DnYJDPm5BWJ6b4Lbggd5q/index.html
 * 入口：https://h5.m.jd.com/babelDiy/Zeus/CvMVbdFGXPiWFFPCc934RiJfMPu/index.html
 */
@JobHandler(value = "JD_DailyRedEnvelope")
@Component
@Slf4j
public class DailyRedEnvelope extends IJobHandler {
    @Resource
    private EnvMapper envMapper;
    GetMethodIns getIns;
    List<String> shareCodes;

    @Override
    public ReturnT<String> execute(String param) {

        List<Env> envs = getUsers();
        this.shareCodes = getShareCode();
        XxlJobLogger.log("【助力码】您提供了{}个", shareCodes.size());
        XxlJobLogger.log("==========================================================");
        envs.forEach(env -> {
            try {
                XxlJobLogger.log("【初始化】{}的天天抽奖您提供了{}个", env.getRemarks());
                DoDailyRedEnvelopeTask(env.getEnvValue(), env.getUa());
            } catch (InterruptedException e) {
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

    private List<Env> getUsers() {
        List<Env> envs = envMapper.getAllCookie("JD_COOKIE");
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return envs;
    }

    public void DoDailyRedEnvelopeTask(String cookie, String UA) throws InterruptedException {
        HashMap<String, String> headerMap = getTaskMap(cookie, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
        XxlJobLogger.log("======【开始执行天天红包任务】=======");
        // 初始化
        DailyRed dailyRed = getDailyRedInit(headerMap);
        if (dailyRed == null) return;
        if (!dailyRed.isTimingGotStatus()) {
            doFreeTimes(headerMap);
        } else {
            XxlJobLogger.log("【天天抽奖】不在领取时间");
        }
        XxlJobLogger.log("【天天红包】开始执行浏览任务");
        doTaskAndGetTimes(headerMap, dailyRed);
        //再次初始化
        DailyRed dailyRed2 = getDailyRedInit(headerMap);
        if (dailyRed2 == null) return;
        XxlJobLogger.log("【天天红包】开始执行助力好友");
        shareCodes.forEach(shareCode -> {
            String helpBody = new JDBodyParam()
                    .Key("imageUrl").valueMark(null)
                    .Key("nickName").valueMark(null)
                    .Key("shareCode").valueMark(shareCode + "-3")
                    .Key("babelChannel").valueMark(3)
                    .Key("version").value(4)
                    .Key("channel").value(1).buildBody();
            String initForFarmUrl = getIns.buildUrl("initForFarm", helpBody, "wh5");
            JSONObject initForFarm = getIns.getJsonObject(initForFarmUrl, headerMap);
            if (initForFarm.getInteger("code") == 0) {
                JSONObject helpResult = initForFarm.getJSONObject("helpResult");
                if (helpResult.getInteger("code") == 0) {
                    XxlJobLogger.log("【天天抽奖】助力{}剩余助力{}次", helpResult.getJSONObject("masterUserInfo").getString("nickName"), helpResult.getInteger("addLotteryTimes"));
                } else if (helpResult.getInteger("code") == 11) {
                    XxlJobLogger.log("【天天抽奖】已经助力过{}", helpResult.getJSONObject("masterUserInfo").getString("nickName"));
                } else if (helpResult.getInteger("code") == 13) {
                    XxlJobLogger.log("【天天抽奖】助力已经用完", helpResult.getJSONObject("masterUserInfo").getString("nickName"));
                }
            }
        });

        if (dailyRed2.getRemainLotteryTimes() > 0) {
            //抽奖
            XxlJobLogger.log("【天天红包】开始抽奖");
            luckDraw(headerMap, dailyRed2);
        }
    }

    private void luckDraw(HashMap<String, String> headerMap, DailyRed dailyRed2) throws InterruptedException {
        for (int i = 0; i < dailyRed2.getRemainLotteryTimes(); i++) {
            //抽奖
            String lotteryForTurntableFarmBody = new JDBodyParam()
                    .Key("type").value(1)
                    .Key("version").value(4)
                    .Key("channel").value(1).buildBody();
            String lotteryForTurntableFarm = getIns.buildUrl("lotteryForTurntableFarm", lotteryForTurntableFarmBody, "wh5");
            JSONObject jsonObject = getIns.getJsonObject(lotteryForTurntableFarm, headerMap);
            if (jsonObject.getInteger("code") == 0) {
                String type = jsonObject.getString("type");
                switch (type) {
                    case "thanks":
                        XxlJobLogger.log("【天天抽奖】获取到个屁");
                        break;
                    case "bean1":
                        XxlJobLogger.log("【天天抽奖】获取到1个京豆");
                        break;
                    case "bean2":
                        XxlJobLogger.log("【天天抽奖】获取到2个京豆");
                        break;
                    case "bean3":
                        XxlJobLogger.log("【天天抽奖】获取到3个京豆");
                        break;
                    case "hongbao1":
                        XxlJobLogger.log("【天天抽奖】获取到红包0.1元");
                        break;
                    case "water1":
                        XxlJobLogger.log("【天天抽奖】获取到水滴10g");
                        break;
                    case "bean5":
                        XxlJobLogger.log("【天天抽奖】获取到5个京豆");
                        break;
                    case "bean4":
                        XxlJobLogger.log("【天天抽奖】获取到4个京豆");
                        break;
                    case "hongbao3":
                        XxlJobLogger.log("【天天抽奖】获取到红包888元");
                        break;
                    case "hongbao2":
                        XxlJobLogger.log("【天天抽奖】获取到红包1.5元");
                        break;
                }
                System.out.println(jsonObject);
            }
            XxlJobLogger.log("【天天红包】等待两秒继续执行...");
            Thread.sleep(2000);
        }
    }

    private void doTaskAndGetTimes(HashMap<String, String> headerMap, DailyRed dailyRed) {
        List<TurntableBrowserAdsItem> turntableBrowserAds = dailyRed.getTurntableBrowserAds();
        turntableBrowserAds.forEach(turntableBrowserAdsItem -> {
            if (!turntableBrowserAdsItem.isGotStatus()) {
                //浏览任务
                //https://api.m.jd.com/client.action?functionId=browserForTurntableFarm&body={"type":1,"adId":"3001558527","version":4,"channel":1}&appid=wh5
                String browserForTurntableFarmBody = new JDBodyParam()
                        .Key("type").value(1)
                        .Key("adId").valueMark(turntableBrowserAdsItem.getAdId())
                        .Key("version").value(4)
                        .Key("channel").value(1).buildBody();
                String waterFriendGotAwardForFarm = getIns.buildUrl("browserForTurntableFarm", browserForTurntableFarmBody, "wh5");
                JSONObject jsonObject = getIns.getJsonObject(waterFriendGotAwardForFarm, headerMap);
                if (jsonObject.getInteger("code") == 0) {
                    XxlJobLogger.log("【成功浏览】{}", turntableBrowserAdsItem.getMain());
                }
                // 领取任务奖励
                String browserForTurntableFarmBody2 = new JDBodyParam()
                        .Key("type").value(2)
                        .Key("adId").valueMark(turntableBrowserAdsItem.getAdId())
                        .Key("version").value(4)
                        .Key("channel").value(1).buildBody();
                String waterFriendGotAwardForFarm2 = getIns.buildUrl("browserForTurntableFarm", browserForTurntableFarmBody2, "wh5");
                JSONObject jsonObject2 = getIns.getJsonObject(waterFriendGotAwardForFarm2, headerMap);
                if (jsonObject2.getInteger("code") == 0) {
                    XxlJobLogger.log("【成功领取】{}次机会，剩余{}次", jsonObject2.getInteger("addTimes"), jsonObject2.getInteger("totalTimes"));
                }
            }
        });
    }

    private void doFreeTimes(HashMap<String, String> headerMap) {
        XxlJobLogger.log("【天天抽奖】领取定时奖励");
        String timingAwardForTurntableFarmBody = new JDBodyParam()
                .Key("version").value(4)
                .Key("channel").value(1).buildBody();
        String timingAwardForTurntableFarm = getIns.buildUrl("timingAwardForTurntableFarm", timingAwardForTurntableFarmBody, "wh5");
        JSONObject jsonObject = getIns.getJsonObject(timingAwardForTurntableFarm, headerMap);
        if (jsonObject.getInteger("code") == 0 && jsonObject.getInteger("addTimes") == 1) {
            XxlJobLogger.log("【天天抽奖】获得{}次定时奖励，剩余{}次机会", jsonObject.getInteger("addTimes"), jsonObject.getInteger("remainLotteryTimes"));
        }
    }

    private DailyRed getDailyRedInit(HashMap<String, String> headerMap) {
        String initForTurntableFarmBody = new JDBodyParam()
                .Key("version").value(4)
                .Key("channel").value(1).buildBody();
        String initForTurntableFarmUrl = getIns.buildUrl("initForTurntableFarm", initForTurntableFarmBody, "wh5");

        JSONObject initForTurntableFarm = getIns.getJsonObject(initForTurntableFarmUrl, headerMap);
        if (initForTurntableFarm.getInteger("code") != 0) {
            XxlJobLogger.log("活动太火爆了！请稍后重试");
            return null;
        }
        DailyRed dailyRed = initForTurntableFarm.toJavaObject(DailyRed.class);
        return dailyRed;
    }

    private List<String> getShareCode() {
        List<Env> envs = envMapper.getAllCookie("FRUITS_SHARE_CODE");
        List<String> shareCodes = envs.stream().map(Env::getEnvValue).collect(Collectors.toList());
        XxlJobLogger.log("【初始化用户】共获取到{}个账号", envs.size());
        return shareCodes;
    }

    private HashMap<String, String> getTaskMap(String cookie, String UA) {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Pragma", "no-cache");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("Sec-Fetch-Site", "same-site");
        headerMap.put("Origin", "https://h5.m.jd.com/");
        headerMap.put("Sec-Fetch-Mode", "cors");
        headerMap.put("Sec-Fetch-Dest", "empty");
        headerMap.put("Accept", " */*");
        headerMap.put("Referer", "https://h5.m.jd.com/");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        headerMap.put("User-Agent", UA);
        headerMap.put("Cookie", cookie);
        return headerMap;
    }


}






















