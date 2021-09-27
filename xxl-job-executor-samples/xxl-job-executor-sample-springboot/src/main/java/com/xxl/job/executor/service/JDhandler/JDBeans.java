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
import com.xxl.job.executor.po.getJinDou.SubTaskVOSItem;
import com.xxl.job.executor.po.getJinDou.TaskInfosItem;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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


    @Override
    public ReturnT<String> execute(String param) throws InterruptedException {
        // 获取所有ck
        envs = commonHandler.getUsers();
        XxlJobLogger.log("==========================================================");
        for (Env env : envs) {
            this.env = env;
            getHeader();
            userInfo = commonHandler.checkJdUserInfo(env);
            if (userInfo == null) {
                break;
            }
            //  获取领京豆活动列表
            String beanTaskList_body = new JDBodyParam().keyMark("viewChannel").valueMark("AppHome").buildBody();
            String beanTaskList_url = buildUrl("beanTaskList", beanTaskList_body);
            JSONObject beanTaskList_jsonObject = getIns.getJsonObject(beanTaskList_url, headerMap);
            // 活动太火爆黑号
            if (beanTaskList_jsonObject.containsKey("errorMessage")) {
                XxlJobLogger.log("❌{},失败，失败原因：{}", env.getRemarks(), beanTaskList_jsonObject.getString("errorMessage"));
                break;
            }
            XxlJobLogger.log("【领京豆】任务开始执行啦φ(*￣0￣)");
            BeanTaskList beanTaskList = beanTaskList_jsonObject.toJavaObject(BeanTaskList.class);
            if (beanTaskList.getData().getViewAppHome() != null && !beanTaskList.getData().getViewAppHome().isDoneTask()) {
                //领取首页进领京豆任务
                String beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(0).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String beanHomeIconDoTask_url = buildUrl("beanHomeIconDoTask", beanHomeIconDoTask_body);
                JSONObject beanHomeIconDoTask_jsonObject = getIns.getJsonObject(beanHomeIconDoTask_url, headerMap);
                if (beanHomeIconDoTask_jsonObject.getInteger("code") != 0) {
                    XxlJobLogger.log("❌{},失败，失败原因：{}", beanTaskList.getData().getViewAppHome().getMainTitle(), beanHomeIconDoTask_jsonObject.getJSONObject("data"));
                }
                XxlJobLogger.log("【{}】领取成功", beanTaskList.getData().getViewAppHome().getMainTitle());
                String do_beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(1).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String do_beanHomeIconDoTask_url = buildUrl("beanHomeIconDoTask", do_beanHomeIconDoTask_body);
                Thread.sleep(RandomUtils.nextInt(3000, 5000));
                XxlJobLogger.log("随机间隔3~5秒防止点击太快");
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
                XxlJobLogger.log("【{}】已经完成了", beanTaskList.getData().getViewAppHome().getMainTitle());
            }
            // 完成其他任务
            List<TaskInfosItem> taskInfos = beanTaskList.getData().getTaskInfos();
            for (TaskInfosItem taskInfo : taskInfos) {
                if (taskInfo.getStatus() == 2) {
                    XxlJobLogger.log("任务:{}已完成", taskInfo.getTaskName());
                    continue;
                } else if (taskInfo.getStatus() != 1) {
                    XxlJobLogger.log("任务:{}未能完成", taskInfo.getTaskName());
                    continue;
                }

                if (taskInfo.getTaskType() == 9 || taskInfo.getTaskType() == 8) {
                    String taskInfo_body = new JDBodyParam()
                            .keyMark("actionType").value(0)
                            .keyMark("taskToken").valueMark(taskInfo.getSubTaskVOS().get(0).getTaskToken()).buildBody();
                    String taskInfo_url = buildUrl("beanDoTask", taskInfo_body);
                    JSONObject taskInfo_jsonObject = getIns.getJsonObject(taskInfo_url, headerMap);

                    getRndInteger
                    (long) Math.floor(Math.random() * (x - y)) + 1000)
                    Thread.sleep((long) (1);
                    XxlJobLogger.log("随机间隔3~5秒防止点击太快");
                    XxlJobLogger.log(taskInfo_jsonObject.toString());
                    System.out.println(taskInfo_jsonObject);
                }

            }


            // ==========================================================签到领取京豆==========================================================
            signForBean(env);
            // ==========================================================摇红包==========================================================
        }

        return SUCCESS;
    }

    private String buildUrl(String functionId, String beanTaskList_body) {
        return String.format("https://api.m.jd.com/client.action?functionId=%s&body=%s&appid=ld&client=apple&clientVersion=%s&networkType=wifi&osVersion=%s.%s&uuid=%s&openudid=%s"
                , functionId
                , beanTaskList_body
                , msg.get("jdVersion")
                , msg.get("osb")
                , msg.get("oss")
                , msg.get("uuid")
                , msg.get("uuid"));
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


    private void signForBean(Env env) {
//        try {
//            String body = "signBeanIndex&appid=ld";
//            HashMap<String, String> beanMap = new HashMap<>();
//            beanMap.put("cookie", env.getEnvValue());
//            JSONObject signBeanIndex = httpIns.buildBeanUrl("signBeanIndex", body, beanMap);
//            JSONObject data = signBeanIndex.getJSONObject("data");
//            if (signBeanIndex.getInteger("code") == 0 && data != null) {
//                if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 1) {
//                    if (data.containsKey("dailyAward")) {
//                        XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), signBeanIndex.getJSONObject("data").getJSONObject("dailyAward").getJSONObject("beanAward").getString("beanCount"));
//                    }
//                    if (data.containsKey("continuityAward")) {
//                        XxlJobLogger.log("【领京豆签到】[{}]获得{}个京豆", env.getRemarks(), signBeanIndex.getJSONObject("data").getJSONObject("continuityAward").getJSONObject("dailyAward").getJSONObject("beanAward").getString("beanCount"));
//                    }
//                } else if (signBeanIndex.getInteger("code") == 0 && signBeanIndex.getJSONObject("data").getInteger("status") == 2) {
//                    XxlJobLogger.log("【领京豆签到】[{}]已签过 ⚠", env.getRemarks());
//                } else if (signBeanIndex.getInteger("code") != 0) {
//                    XxlJobLogger.log("【领京豆签到失败】...请稍后重试", env.getRemarks());
//                }
//            }
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void init() {
        this.getIns = GetMethodIns.getGetIns();
    }

    @Override
    public void destroy() {

    }
}
