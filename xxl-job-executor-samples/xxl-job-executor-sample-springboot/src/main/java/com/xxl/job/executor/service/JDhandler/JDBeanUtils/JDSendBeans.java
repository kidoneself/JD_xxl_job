package com.xxl.job.executor.service.JDhandler.JDBeanUtils;


import cn.hutool.core.lang.Console;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.JSONTree;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.SendBeans;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@JobHandler(value = "JD_SendBeans")
@Component
@Slf4j
public class JDSendBeans extends IJobHandler {

    @Resource
    private CommonHandler commonHandler;
    Env env;
    List<Env> envs;
    HashMap<String, String> headerMap = new HashMap<>();
    JDUser userInfo;
    String lkt;
    String lks;
    Integer completeNumbers;
    String activityId;
    String activityCode;
    Integer rewardRecordId;
    Boolean completed;
    Boolean rewardOk;
    String inviteUserPin;
    List<SendBeans> helpUsers;

    @Override
    public ReturnT<String> execute(String param) throws Exception {
        proxy();
        envs = commonHandler.getUsers();
        XxlJobLogger.log("【送豆得豆】任务开始执行啦φ(*￣0￣)");
        env = envs.get(0);
        getActivityInfo();
        getActivityDetail();
        XxlJobLogger.log("获取到的活动ID：{}，需要邀请{}人瓜分", activityId, completeNumbers);
        int openCount = (int) Math.floor((envs.size() - 1) / completeNumbers);
        XxlJobLogger.log("共有{}个账号，前{}个账号可以开团", envs.size(), openCount);
        helpUsers = new ArrayList<>();
        for (int i = 0; i < openCount; i++) {
            this.env = envs.get(i);
            userInfo = commonHandler.checkJdUserInfo(env);
            if (userInfo == null) continue;
            Boolean bool = invite();
            helpUsers = new ArrayList<>();
            Thread.sleep(1000);
            getActivityDetail();
            SendBeans sendBeanUser = SendBeans.builder().username(inviteUserPin).rewardRecordId(rewardRecordId).completed(completed).rewardOk(rewardOk).build();
            helpUsers.add(sendBeanUser);
            if (!bool && rewardRecordId != null) {
                XxlJobLogger.log("{}已经开过团", env.getRemarks());
                continue;
            }
            XxlJobLogger.log("{}开团成功，瓜分id：{}", env.getRemarks(), rewardRecordId);
        }
        Thread.sleep(3000);
        for (SendBeans sendBeans : helpUsers) {
            if (sendBeans.getCompleted()) continue;
            for (Env env : envs) {
                if (sendBeans.getCompleted()) continue;
                this.env = env;
                userInfo = commonHandler.checkJdUserInfo(env);
                if (userInfo == null) continue;
                if (userInfo.getCurPin().equals(sendBeans.getUsername())) continue;
                XxlJobLogger.log("{}去助力{}", env.getRemarks(), sendBeans.getUsername());
                String url = String.format("https://draw.jdfcloud.com/common/api/bean/activity/participate?activityCode=%s&activityId=%s&inviteUserPin=%s&invokeKey=JL1VTNRadM68cIMQ&timestap=%s",
                        activityCode, activityId, URLEncoder.encode(sendBeans.getUsername()), System.currentTimeMillis());
                lkt = String.valueOf(System.currentTimeMillis());
                lks = SecureUtil.md5("JL1VTNRadM68cIMQ" + lkt + activityCode);
                getActivityInfo();
                getActivityDetail();
                Thread.sleep(3000);
                JSONObject post = Post(url);
                Thread.sleep(3000);
                HashMap<String, Object> result = JSONTree.jsonToHashMap(post);
                Console.log(post);
                XxlJobLogger.log("参团结果：{}", result.get("desc"));
                if (result.get("errorMessage") != null) {
                    XxlJobLogger.log((String) result.get("errorMessage"));
                    continue;
                }
                if (post.getInteger("result") != null && post.getInteger("result") == 5) {
                    sendBeans.setCompleted(true);
                } else if (post.getInteger("result") != null && post.getInteger("result") == 1 || post.getInteger("result") != null && post.getInteger("result") == 0) {
                    XxlJobLogger.log((String) result.get("desc"));
                }
            }
        }
        return ReturnT.SUCCESS;

    }


    private Boolean invite() {
        lkt = String.valueOf(System.currentTimeMillis());
        lks = SecureUtil.md5("JL1VTNRadM68cIMQ" + lkt + activityCode);
        getDetailHeader();
        String url = String.format("https://draw.jdfcloud.com/common/api/bean/activity/invite?activityCode=%s&openId=&activityId=%s&userSource=mp&formId=123&jdChannelId=&fp=&appId=wxccb5c536b0ecd1bf&invokeKey=JL1VTNRadM68cIMQ",
                activityCode, activityId);
        JSONObject post = Post(url);
        return post.getBoolean("data");
    }

    private void getActivityInfo() {
        // 获取活动列表
        lkt = String.valueOf(System.currentTimeMillis());
        lks = SecureUtil.md5("JL1VTNRadM68cIMQ" + lkt);
        getTuanHeader();
        String url = "https://sendbeans.jd.com/common/api/bean/activity/get/entry/list/by/channel?channelId=14&channelType=H5&sendType=0&singleActivity=false&invokeKey=JL1VTNRadM68cIMQ";
        JSONObject openTuan = Get(url);
        HashMap<String, Object> toHashMap = JSONTree.jsonToHashMap(openTuan);
        if (!toHashMap.containsKey("status") || "NOT_BEGIN".equals(toHashMap.get("status"))) {
            XxlJobLogger.log("获取活动详情失败");
            return;
        }
        activityId = toHashMap.get("activeId").toString();
        activityCode = toHashMap.get("activityCode").toString();
    }

    private void getActivityDetail() {
        lkt = String.valueOf(System.currentTimeMillis());
        lks = SecureUtil.md5("JL1VTNRadM68cIMQ" + lkt + activityCode);
        getDetailHeader();
        String activityDetailUrl = String.format("https://draw.jdfcloud.com/common/api/bean/activity/detail?activityCode=%s&activityId=%s&userOpenId=&timestap=%s&userSource=mp&jdChannelId=&appId=wxccb5c536b0ecd1bf&invokeKey=JL1VTNRadM68cIMQ",
                activityCode, activityId, System.currentTimeMillis());
        HashMap<String, Object> activityDetail = JSONTree.jsonToHashMap(Get(activityDetailUrl));
        if (!activityDetail.containsKey("completeNumbers")) return;
        rewardRecordId = (Integer) activityDetail.get("rewardRecordId");
        completeNumbers = (Integer) activityDetail.get("completeNumbers");
        completed = (Boolean) activityDetail.get("completed");
        rewardOk = (Boolean) activityDetail.get("rewardOk");
        inviteUserPin = activityDetail.get("inviteUserPin").toString();
    }


    private void getTuanHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Host", "sendbeans.jd.com");
        header.put("Origin", "https://sendbeans.jd.com");
        header.put("Cookie", env.getEnvValue());
        header.put("Connection", "keep-alive");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("User-Agent", UserAgentUtil.randomUserAgent());
        header.put("Accept-Language", "zh-cn");
        header.put("Referer", "https://sendbeans.jd.com/dist/index.html");
        header.put("Accept-Encoding", "gzip, deflate, br");
        header.put("openId", "");
        header.put("lkt", lkt);
        header.put("lks", lks);
        this.headerMap = header;
    }

    private void getTotalBeanHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Host", "wq.jd.com");
        header.put("Accept", "*/*");
        header.put("Cookie", env.getEnvValue());
        header.put("Connection", "keep-alive");
        header.put("User-Agent", UserAgentUtil.randomUserAgent());
        header.put("Accept-Language", "zh-cn");
        header.put("Referer", "https://sendbeans.jd.com/dist/index.html");
        header.put("Accept-Encoding", "gzip, deflate, br");
        this.headerMap = header;
    }


    private void getDetailHeader() {
        HashMap<String, String> header = new HashMap<>();
        header.put("Cookie", env.getEnvValue());
        header.put("openId", "");
        header.put("Connection", "keep-alive");
        header.put("App-Id", "wxccb5c536b0ecd1bf");
        header.put("content-type", "application/json");
        header.put("Host", "draw.jdfcloud.com");
        header.put("Accept-Encoding", "gzip,compress,br,deflate");
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 14_7_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 MicroMessenger/8.0.13(0x18000d2a) NetType/WIFI Language/zh_CN");
        header.put("lkt", lkt);
        header.put("lks", lks);
        header.put("Referer", "https://servicewechat.com/wxccb5c536b0ecd1bf/755/page-frame.html");
        header.put("Lottery-Access-Signature", "wxccb5c536b0ecd1bf1537237540544h79HlfU");
        this.headerMap = header;
    }

    public JSONObject Get(String url) {
        String body = HttpRequest.get(url).addHeaders(headerMap).execute().body();
        return JSONObject.parseObject(body);
    }

    public JSONObject Post(String url) {
        String body = HttpRequest.post(url).addHeaders(headerMap).execute().body();
        return JSONObject.parseObject(body);
    }


    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
