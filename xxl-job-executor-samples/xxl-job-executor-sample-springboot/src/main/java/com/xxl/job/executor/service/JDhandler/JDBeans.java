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
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
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


    @Override
    public ReturnT<String> execute(String param) throws InterruptedException {
        // 获取所有ck
        envs = commonHandler.getUsers();
        XxlJobLogger.log("==========================================================");
        for (Env env : envs) {
            this.env = env;
            getHeader();
            userInfo = commonHandler.checkJdUserInfo(env);
            if (userInfo == null) return null;
            //  获取领京豆活动列表
            String beanTaskList_body = new JDBodyParam().keyMark("viewChannel").valueMark("AppHome").buildBody();
            String beanTaskList_url = buildUrl("beanTaskList", beanTaskList_body);
            JSONObject beanTaskList_jsonObject = getIns.getJsonObject(beanTaskList_url, headerMap);
            if (beanTaskList_jsonObject.containsKey("errorMessage")) {
                XxlJobLogger.log(beanTaskList_jsonObject.getString("errorMessage"));
                return null;
            }
            XxlJobLogger.log("【领京豆】任务开始执行");
            BeanTaskList beanTaskList = beanTaskList_jsonObject.toJavaObject(BeanTaskList.class);
            if (beanTaskList.getData().getViewAppHome() != null && !beanTaskList.getData().getViewAppHome().isDoneTask()) {
//                beanHomeIconDoTask {"flag":"0","viewChannel":"AppHome"}
                //领取首页进领京豆任务
                String beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(0).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String beanHomeIconDoTask_url = buildUrl("beanHomeIconDoTask", beanHomeIconDoTask_body);
                JSONObject beanHomeIconDoTask_jsonObject = getIns.getJsonObject(beanHomeIconDoTask_url, headerMap);
                if (beanHomeIconDoTask_jsonObject.getInteger("code") != 0) {
                    XxlJobLogger.log("【{}】领取失败", beanTaskList.getData().getViewAppHome().getMainTitle());
                }
                XxlJobLogger.log("【{}】领取成功", beanTaskList.getData().getViewAppHome().getMainTitle());
                String do_beanHomeIconDoTask_body = new JDBodyParam().keyMark("flag").valueMark(1).keyMark("viewChannel").valueMark("AppHome").buildBody();
                String do_beanHomeIconDoTask_url = buildUrl("beanHomeIconDoTask", do_beanHomeIconDoTask_body);
                Thread.sleep(RandomUtils.nextInt(2000, 3000));
                JSONObject do_beanHomeIconDoTask_jsonObject = getIns.getJsonObject(do_beanHomeIconDoTask_url, headerMap);
                if (do_beanHomeIconDoTask_jsonObject.getInteger("code") == 0 && do_beanHomeIconDoTask_jsonObject.getJSONObject("data") != null) {
                    JSONObject data = do_beanHomeIconDoTask_jsonObject.getJSONObject("data");
                    XxlJobLogger.log("【{}】{}", beanTaskList.getData().getViewAppHome().getMainTitle(), data.getString("bizMsg") != null ? data.getString("bizMsg") : data.getString("remindMsg"));
                    if (data.getJSONObject("growthResult") != null && data.getJSONObject("growthResult").getJSONObject("sceneLevelConfig") != null) {
                        XxlJobLogger.log("【{}】额外获得{}京豆😍", beanTaskList.getData().getViewAppHome().getMainTitle(), data.getJSONObject("growthResult").getJSONObject("sceneLevelConfig").getInteger("beanNum"));
                    }
                } else {
                    XxlJobLogger.log("errorMessage -> 点太快啦");
                }


            } else {
                XxlJobLogger.log("已经完成了");
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
