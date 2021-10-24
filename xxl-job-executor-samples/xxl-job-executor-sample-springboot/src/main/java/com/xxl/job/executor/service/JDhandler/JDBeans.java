package com.xxl.job.executor.service.JDhandler;

import cn.hutool.core.lang.Console;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.GetMethodIns;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.core.JSONTree;
import com.xxl.job.executor.core.UserAgentUtil;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.getJinDou.BeanTaskList;
import com.xxl.job.executor.po.getJinDou.TaskInfosItem;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

@JobHandler(value = "JD_Beans")
@Component
@Slf4j
public class JDBeans extends IJobHandler {


    @Resource
    private CommonHandler commonHandler;
    GetMethodIns getIns;
    HashMap<String, String> msg = UserAgentUtil.randomUserAgentMsg();
    Env env;
    List<Env> envs;
    HashMap<String, String> headerMap = new HashMap<>();
    JDUser userInfo;
    BeanTaskList beanTaskList;

    @Override
    public ReturnT<String> execute(String param) {
        try {

            // 获取所有ck
            envs = commonHandler.getUsers();
            XxlJobLogger.log("【领京豆】任务开始执行啦φ(*￣0￣)");
            for (Env env : envs) {
                this.env = env;
                getHeader();
                userInfo = commonHandler.checkJdUserInfo(env);
                if (userInfo == null) continue;
                // 京东电器签到
//                applianceSign();


                //早起福利
                morningGetBean();
                // 签到领取京豆
                signForBean();
                // 首页进任务
                doViewAppHome();
                // 其他任务
                doOtherTask();
                // 打印用户等级
                printInfo();
                // ==========================================================摇红包==========================================================
            }
        } catch (Exception e) {
            e.printStackTrace();

            if (e instanceof InterruptedException) {
                System.err.println("===========================");
            }

        }
        return SUCCESS;
    }

    private void printInfo() {
        getBeanTaskList();
        if (beanTaskList == null) return;
        XxlJobLogger.log(" 当前等级:{} 下一级可领取:{}京豆", beanTaskList.getData().getCurLevel(), beanTaskList.getData().getNextLevelBeanNum());
    }

    private void morningGetBean() {
        String body = HttpRequest.get("https://api.m.jd.com/client.action?functionId=morningGetBean&area=22_1930_50948_52157&body=%7B%22rnVersion%22%3A%224.7%22%2C%22fp%22%3A%22-1%22%2C%22eid%22%3A%22%22%2C%22shshshfp%22%3A%22-1%22%2C%22userAgent%22%3A%22-1%22%2C%22shshshfpa%22%3A%22-1%22%2C%22referUrl%22%3A%22-1%22%2C%22jda%22%3A%22-1%22%7D&build=167724&client=apple&clientVersion=10.0.6&d_brand=apple&d_model=iPhone12%2C8&eid=eidI1aaf8122bas5nupxDQcTRriWjt7Slv2RSJ7qcn6zrB99mPt31yO9nye2dnwJ/OW%2BUUpYt6I0VSTk7xGpxEHp6sM62VYWXroGATSgQLrUZ4QHLjQw&isBackground=N&joycious=60&lang=zh_CN&networkType=wifi&networklibtype=JDNetworkBaseAF&openudid=32280b23f8a48084816d8a6c577c6573c162c174&osVersion=14.4&partner=apple&rfs=0000&scope=01&screen=750%2A1334&sign=0c19e5962cea97520c1ef9a2e67dda60&st=1625354180413&sv=112&uemps=0-0&uts=0f31TVRjBSsqndu4/jgUPz6uymy50MQJSPYvHJMKdY9TUw/AQc1o/DLA/rOTDwEjG4Ar9s7IY4H6IPf3pAz7rkIVtEeW7XkXSOXGvEtHspPvqFlAueK%2B9dfB7ZbI91M9YYXBBk66bejZnH/W/xDy/aPsq2X3k4dUMOkS4j5GHKOGQO3o2U1rhx5O70ZrLaRm7Jy/DxCjm%2BdyfXX8v8rwKw%3D%3D&uuid=hjudwgohxzVu96krv/T6Hg%3D%3D&wifiBssid=c99b216a4acd3bce759e369eaeeafd7").addHeaders(headerMap).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        Console.log(jsonObject);
        HashMap<String, Object> result = JSONTree.jsonToHashMap(jsonObject);
        if (result.get("code").equals("0")) {
            XxlJobLogger.log(" 早起福利:{}{}京豆", result.get("bizMsg"), result.get("awardResultFlag"));
        }
    }

    private void applianceSign() {
//        Host: jdjoy.jd.com
//lkt: 1633800007206
//Accept-Encoding: gzip, deflate, br
//Accept-Language: zh-CN,zh-Hans;q=0.9
//Accept:
//Origin: https://prodev.m.jd.com
//Content-Length: 0
//Connection:
//lks: 2dd9ba4ad5e8957dbde0ba5df805c553
//User-Agent: jdapp;iPhone;9.3.2;15.0;f061695d1c03fef9fdd71adf54dbf28ee19a0d61;network/wifi;supportApplePay/0;hasUPPay/0;hasOCPay/0;model/iPhone8,2;addressid/6484220720;supportBestPay/0;appBuild/167490;pushNoticeIsOpen/0;jdSupportDarkMode/0;pv/10.26;apprpd/;ref/JDWebViewAttentionController;psq/2;ads/;psn/f061695d1c03fef9fdd71adf54dbf28ee19a0d61|91;jdv/0|direct|-|none|-|1632830119936|1633350768;adk/;app_device/IOS;pap/JA2015_311210|9.3.2|IOS 15.0;Mozilla/5.0 (iPhone; CPU iPhone OS 15_0 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1
//Referer: https://prodev.m.jd.com/

        HashMap<String, String> header = new HashMap<>();
        header.put("Host", "jdjoy.jd.com");
        header.put("Accept-Encoding", "zh-CN,zh-Hans;q=0.9");
        header.put("Accept-Language", "gzip, deflate, br");
        header.put("Accept", "application/json, text/plain, */*");
        header.put("Origin", "https://prodev.m.jd.com");
        header.put("Content-Length", "0");
        header.put("Connection", "keep-alive");
        header.put("User-Agent", UserAgentUtil.randomUserAgent());
        header.put("Referer", "https://prodev.m.jd.com/");
        String lkt = String.valueOf(System.currentTimeMillis());
        String lks = SecureUtil.md5("JL1VTNRadM68cIMQ" + lkt );
        header.put("lks", lks);
        header.put("lkt", lkt);
        String body = HttpRequest.post("https://jdjoy.jd.com/api/turncard/channel/sign?turnTableId=347&fp=-1&eid=&invokeKey=JL1VTNRadM68cIMQ").addHeaders(header).execute().body();
        JSONObject jsonObject = JSONObject.parseObject(body);
        Console.log(jsonObject);
        HashMap<String, Object> result = JSONTree.jsonToHashMap(jsonObject);
        if (result.get("code").equals("0")) {
            XxlJobLogger.log(" 早起福利:{}{}京豆", result.get("bizMsg"), result.get("awardResultFlag"));
        }
    }
    private void doOtherTask() throws InterruptedException {

        getBeanTaskList();
        if (beanTaskList == null) return;
        List<TaskInfosItem> taskInfos = beanTaskList.getData().getTaskInfos();
        TaskInfosItem taskInfo;
        for (int i = 0; i < taskInfos.size(); i++) {
            taskInfo = taskInfos.get(i);
            if (taskInfo.getStatus() == 2) {
                XxlJobLogger.log("任务{}:今日已经完成了", taskInfo.getTaskName());
                continue;
            } else if (taskInfo.getStatus() != 1) {
                XxlJobLogger.log("任务{}:未能完成", taskInfo.getTaskName());
                continue;
            }
            int times = 0;
            do {
                getBeanTaskList();
                if (beanTaskList == null) return;
                List<TaskInfosItem> taskInfosNew = beanTaskList.getData().getTaskInfos();
                taskInfo = taskInfosNew.get(i);
                times++;
                if (taskInfo.getStatus() == 2) {
                    continue;
                }
                // 跳转任务  89是一个任务需要执行多次
                if (taskInfo.getTaskType() == 9 || taskInfo.getTaskType() == 8) {
                    String taskInfo_body = new JDBodyParam()
                            .keyMark("actionType").value(1)
                            .keyMark("taskToken").valueMark(taskInfo.getSubTaskVOS().get(0).getTaskToken()).buildBody();
                    String taskInfo_url = buildTaskUrl("beanDoTask", taskInfo_body);
                    JSONObject taskInfo_jsonObject = getIns.getJsonObject(taskInfo_url, headerMap);
                    Integer waitDuration = taskInfo.getWaitDuration();
                    //waitDuration 等待时间
                    if (waitDuration != null && waitDuration != 0) {
                        Integer max = waitDuration * 1000 + 1000;
                        Integer min = waitDuration * 1000 + 2000;
                        Long rndInteger = getRndInteger(min, max);
                        Thread.sleep(rndInteger);
                        XxlJobLogger.log("{}：任务领取成功", taskInfo.getTaskName());
                    } else {
                        XxlJobLogger.log("{}：任务领取失败", taskInfo.getTaskName());
                        Thread.sleep(getRndInteger(6500, 7000));
                    }
                }
                // 正常点击任务
                String taskInfo_body = new JDBodyParam()
                        .keyMark("actionType").value(0)
                        .keyMark("taskToken").valueMark(taskInfo.getSubTaskVOS().get(0).getTaskToken()).buildBody();
                String taskInfo_url = buildTaskUrl("beanDoTask", taskInfo_body);
                JSONObject taskInfo_jsonObject = getIns.getJsonObject(taskInfo_url, headerMap);
                HashMap<String, Object> taskInfo_data = JSONTree.jsonToHashMap(taskInfo_jsonObject);
                XxlJobLogger.log("{}", taskInfo_data.get("bizMsg"));
                Thread.sleep(getRndInteger(4000, 5500));
            } while (times < 4);
        }

    }

    private void doViewAppHome() throws InterruptedException {
        XxlJobLogger.log("开始做任务~~~");
        getBeanTaskList();
        if (beanTaskList == null) return;
        if (beanTaskList.getData().getViewAppHome() == null) {
            XxlJobLogger.log("未领取到首页任务");
        } else {
            //领取首页进领京豆任务
            if (!beanTaskList.getData().getViewAppHome().isDoneTask()) {
                String beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(0).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String beanHomeIconDoTask_url = buildTaskUrl("beanHomeIconDoTask", beanHomeIconDoTask_body);
                JSONObject beanHomeIconDoTask_jsonObject = getIns.getJsonObject(beanHomeIconDoTask_url, headerMap);
                if (beanHomeIconDoTask_jsonObject.getInteger("code") != 0) {
                    XxlJobLogger.log("❌{},失败，失败原因：{}", beanTaskList.getData().getViewAppHome().getMainTitle(), beanHomeIconDoTask_jsonObject.getJSONObject("data"));
                }
                XxlJobLogger.log("【{}】领取成功", beanTaskList.getData().getViewAppHome().getMainTitle());
                String do_beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(1).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String do_beanHomeIconDoTask_url = buildTaskUrl("beanHomeIconDoTask", do_beanHomeIconDoTask_body);
                Thread.sleep(getRndInteger(2500, 3500));
                JSONObject do_beanHomeIconDoTask_jsonObject = getIns.getJsonObject(do_beanHomeIconDoTask_url, headerMap);
                if (do_beanHomeIconDoTask_jsonObject.getInteger("code") == 0 && do_beanHomeIconDoTask_jsonObject.getJSONObject("data") != null) {
                    JSONObject data = do_beanHomeIconDoTask_jsonObject.getJSONObject("data");
                    XxlJobLogger.log("【{}】{}", beanTaskList.getData().getViewAppHome().getMainTitle(), data.getString("bizMsg") != null ? data.getString("bizMsg") : data.getString("remindMsg"));
                    if (data.getJSONObject("growthResult") != null && data.getJSONObject("growthResult").getJSONObject("sceneLevelConfig") != null) {
                        XxlJobLogger.log("【{}】额外获得{}京豆的🎊🎊", beanTaskList.getData().getViewAppHome().getMainTitle(), data.getJSONObject("growthResult").getJSONObject("sceneLevelConfig").getInteger("beanNum"));
                    }
                } else {
                    XxlJobLogger.log("errorMessage -> 点太快啦\n{}", do_beanHomeIconDoTask_jsonObject.getJSONObject("data"));
                }
            } else {
                XxlJobLogger.log("任务{}:已经完成了", beanTaskList.getData().getViewAppHome().getMainTitle());
            }
        }

    }

    private void getBeanTaskList() {
        String beanTaskList_body = new JDBodyParam().keyMark("viewChannel").valueMark("AppHome").buildBody();
        String beanTaskList_url = buildTaskUrl("beanTaskList", beanTaskList_body);
        JSONObject beanTaskList_jsonObject = getIns.getJsonObject(beanTaskList_url, headerMap);
        // 活动太火爆黑号
        if (beanTaskList_jsonObject.containsKey("errorMessage")) {
            XxlJobLogger.log("❌{},失败，失败原因：{}", env.getRemarks(), beanTaskList_jsonObject.getString("errorMessage"));
            return;
        }
        this.beanTaskList = beanTaskList_jsonObject.toJavaObject(BeanTaskList.class);
    }

    private void signForBean() {
        String body = "signBeanIndex&appid=ld";
        HashMap<String, String> beanMap = new HashMap<>();
        beanMap.put("cookie", env.getEnvValue());
        String signBeanIndex_url = buildSignUrl("signBeanIndex", body);
        JSONObject signBeanIndex = getIns.getJsonObject(signBeanIndex_url, beanMap);
        JSONObject data = signBeanIndex.getJSONObject("data");

        HashMap<String, Object> stringObjectHashMap = JSONTree.jsonToHashMap(data);

        if (signBeanIndex.getInteger("code") == 0 && data != null) {
            if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 1) {
                if (stringObjectHashMap.containsKey("dailyAward")) {
                    XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), stringObjectHashMap.get("beanCount"));
                }
                if (stringObjectHashMap.containsKey("continuityAward")) {
                    XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), stringObjectHashMap.get("beanCount"));
                }
            } else if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 2) {
                XxlJobLogger.log("【领京豆签到】[{}]已签过😭", env.getRemarks());
            } else if (signBeanIndex.getInteger("code") != 0) {
                XxlJobLogger.log("【领京豆签到失败】...请稍后重试", env.getRemarks());
            }
        }
    }


    public Long getRndInteger(Integer min, Integer max) {
        return (long) Math.floor(Math.random() * (min - max)) + min;
    }

    @Override
    public void init() {
        this.getIns = GetMethodIns.getGetIns();
    }

    @Override
    public void destroy() {

    }

    private String buildTaskUrl(String functionId, String beanTaskList_body) {
        return String.format("https://api.m.jd.com/client.action?functionId=%s&body=%s&appid=ld&client=apple&clientVersion=%s&networkType=wifi&osVersion=%s.%s&uuid=%s&openudid=%s"
                , functionId
                , beanTaskList_body
                , msg.get("jdVersion")
                , msg.get("osb")
                , msg.get("oss")
                , msg.get("uuid")
                , msg.get("uuid"));
    }

    private String buildSignUrl(String functionId, String buildSign_body) {
        return String.format("https://api.m.jd.com/client.action?functionId=%s&body=%s"
                , functionId
                , buildSign_body);
    }

    private void getHeader() {
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Accept", "*/*");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("Accept-Language", "zh-cn");
        headerMap.put("Cookie", env.getEnvValue());
        headerMap.put("Referer", "https://h5.m.jd.com/");
        headerMap.put("User-Agent", msg.get("ua"));
        this.headerMap = headerMap;
    }
}
