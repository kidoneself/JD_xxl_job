package com.xxl.job.executor.service.JDhandler;

import com.alibaba.fastjson.JSONObject;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.core.handler.annotation.JobHandler;
import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.GetMethodIns;
import com.xxl.job.executor.core.JDBodyParam;
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
    List<String> paradiseUuids;
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


                //早起福利

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
                System.out.println("++++++++++++++++++++");
                getBeanTaskList();
                if (beanTaskList == null) return;
                List<TaskInfosItem> taskInfosNew = beanTaskList.getData().getTaskInfos();
                taskInfo = taskInfosNew.get(i);
                times++;
                if (taskInfo.getStatus() == 2) {
                    continue;
                }
                // 跳转任务
                if (taskInfo.getTaskType() == 9 || taskInfo.getTaskType() == 8) {
                    String taskInfo_body = new JDBodyParam()
                            .keyMark("actionType").value(1)
                            .keyMark("taskToken").valueMark(taskInfo.getSubTaskVOS().get(0).getTaskToken()).buildBody();
                    String taskInfo_url = buildTaskUrl("beanDoTask", taskInfo_body);
                    JSONObject taskInfo_jsonObject = getIns.getJsonObject(taskInfo_url, headerMap);
                    Integer waitDuration = taskInfo.getWaitDuration();
                    if (waitDuration != null && waitDuration != 0) {
                        Integer max = waitDuration * 1000 + 1000;
                        Integer min = waitDuration * 1000 + 2000;
                        Long rndInteger = getRndInteger(min, max);
                        System.out.println(rndInteger);
                        Thread.sleep(rndInteger);
                    } else {
                        System.out.println(getRndInteger(6500, 7000));
                        Thread.sleep(getRndInteger(6500, 7000));
                    }
                    System.out.println("跳转任务" + "==" + taskInfo_jsonObject);
                }
                // 正常点击任务
                String taskInfo_body = new JDBodyParam()
                        .keyMark("actionType").value(0)
                        .keyMark("taskToken").valueMark(taskInfo.getSubTaskVOS().get(0).getTaskToken()).buildBody();
                String taskInfo_url = buildTaskUrl("beanDoTask", taskInfo_body);
                JSONObject taskInfo_jsonObject = getIns.getJsonObject(taskInfo_url, headerMap);
                System.out.println("正常点击任务" + "==" + taskInfo_jsonObject);
                System.out.println(getRndInteger(4000, 5500));
                Thread.sleep(getRndInteger(4000, 5500));
                System.out.println(times);
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
                System.out.println(getRndInteger(2500, 3500));
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
        if (signBeanIndex.getInteger("code") == 0 && data != null) {
            if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 1) {
                if (data.containsKey("dailyAward")) {
                    XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), signBeanIndex.getJSONObject("data").getJSONObject("dailyAward").getJSONObject("beanAward").getString("beanCount"));
                }
                if (data.containsKey("continuityAward")) {
                    XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), signBeanIndex.getJSONObject("data").getJSONObject("continuityAward").getJSONObject("dailyAward").getJSONObject("beanAward").getString("beanCount"));
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
