//package com.xxl.job.executor.service.JDhandler.JDBeanUtils;
//
//import com.alibaba.fastjson.JSONObject;
//import com.xxl.job.core.handler.annotation.JobHandler;
//import com.xxl.job.core.log.XxlJobLogger;
//import com.xxl.job.executor.core.GetMethodIns;
//import com.xxl.job.executor.core.JDBodyParam;
//import com.xxl.job.executor.core.RequestConstant;
//import com.xxl.job.executor.po.ShakeList;
//import com.xxl.job.executor.po.TaskItemsItem;
//import com.xxl.job.executor.po.shake.FloorInfoListItem;
//import com.xxl.job.executor.po.shake.ShakingBoxInfo;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.http.client.utils.URIBuilder;
//import org.springframework.stereotype.Component;
//
//import java.io.UnsupportedEncodingException;
//import java.sql.Timestamp;
//import java.util.HashMap;
//import java.util.List;
//
///**
// * 摇京豆 https://spa.jd.com/home?source=WJ
// */
//@JobHandler(value = "JD_ShakeRedEnvelopes")
//@Component
//@Slf4j
//public class ShakeRedEnvelopes {
//    //    Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1
//    //pt_key=AAJhRyK1ADCfaMLMkUA96laOm1_845DZqAuxdaP7mSbEeNfmuQoM2kItc-La3dm18Mb9e37nJ1w;pt_pin=wdlLxrYZBojiba;
//
//    @SneakyThrows
//    public void getVVipClubLotteryTask(String cookie, String UA) {
//        GetMethodIns getIns = GetMethodIns.getGetIns();
//        HashMap<String, String> headerMap = getTaskMap(cookie, "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
//        // 初始化摇盒子
//        XxlJobLogger.log("=====初始化摇一摇=====");
//        JSONObject data = getShakeRed(getIns, headerMap);
//        if (data != null) {
//            // 获取任务列表 并且浏览任务
//            getAndDoTask(getIns, headerMap);
//            // 再次获取摇一摇信息
//            JSONObject dataNew = getShakeRed(getIns, headerMap);
//            if (dataNew != null) {
//                List<FloorInfoListItem> floorInfoList = dataNew.getJSONArray("floorInfoList").toJavaList(FloorInfoListItem.class);
//                floorInfoList.forEach(floorInfo -> {
//                    if ("SHAKING_BOX_INFO".equals(floorInfo.getCode())) {
//                        ShakingBoxInfo shakingBoxInfo = floorInfo.getFloorData().getShakingBoxInfo();
//                        int remainLotteryTimes = shakingBoxInfo.getRemainLotteryTimes();
//                        for (int i = 0; i < remainLotteryTimes; i++) {
//                            // 开始摇一摇
//                            try {
//                                shark(getIns, headerMap, i);
//                            } catch (UnsupportedEncodingException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                });
//            }
//        } else {
//            XxlJobLogger.log("活动太火爆！");
//        }
//        // 剩余摇一摇次数
//
//
//    }
//
//    @SneakyThrows
//    private void shark(GetMethodIns getIns, HashMap<String, String> headerMap, int i) throws UnsupportedEncodingException {
//        //摇一摇
//        String url = "https://api.m.jd.com/?appid=sharkBean&functionId=vvipclub_shaking_lottery&body=%7B%7D";
////        String decode = URLEncoder.encode(url, "UTF-8");
//        JSONObject jsonObject = getIns.getJsonObject(url, headerMap);
//        if (jsonObject.getJSONObject("data").containsKey("rewardBeanAmount")) {
//            XxlJobLogger.log("【第{}摇一摇】获得{}个京豆", i, jsonObject.getInteger("rewardBeanAmount"));
//        } else {
//            System.out.println(jsonObject);
//        }
//    }
//
//    private void getAndDoTask(GetMethodIns getIns, HashMap<String, String> headerMap) {
//        String vvipclub_lotteryTask_body = new JDBodyParam()
//                .Key("info").stringValue("browseTask")
//                .Key("withItem").boolValue(true).buildBody();
//        String vvipclub_lotteryTask = buildUrl("vvipclub_lotteryTask", vvipclub_lotteryTask_body);
//        JSONObject vvipclub_lotteryTask_jsonObject = getIns.getJsonObject(vvipclub_lotteryTask, headerMap);
//        ShakeList shakeList = vvipclub_lotteryTask_jsonObject.toJavaObject(ShakeList.class);
//        List<TaskItemsItem> sharkLists = shakeList.getData().get(0).getTaskItems();
//        // 执行任务
//        sharkLists.forEach(sharkList -> {
//            if (!sharkList.isFinish()) {
//                String body = new JDBodyParam()
//                        .Key("taskName").stringValue("browseTask")
//                        .Key("taskItemId").integerValue(sharkList.getId()).buildBody();
//                // 浏览所有任务
//                String vvipclub_doTask = buildUrl("vvipclub_doTask", body);
//                getIns.getJsonObject(vvipclub_doTask, headerMap);
//                XxlJobLogger.log("【已浏览任务】{}", sharkList.getTitle());
//            }
//        });
//    }
//
//    private JSONObject getShakeRed(GetMethodIns getIns, HashMap<String, String> headerMap) {
////        https://api.m.jd.com/?t=1632141148390&appid=sharkBean&functionId=pg_channel_page_data&body={"paramData":{"token":"dd2fb032-9fa3-493b-8cd0-0d57cd51812d"}}
//        String token = new JDBodyParam()
//                .Key("token").stringValue("dd2fb032-9fa3-493b-8cd0-0d57cd51812d").buildBody();
//        String paramData_body = new JDBodyParam()
//                .Key("paramData").stringValueNo(token).buildBody();
//        String pg_channel_page_data = buildUrlSharkBean("pg_channel_page_data", paramData_body);
//        JSONObject jsonObject = getIns.getJsonObject(pg_channel_page_data, headerMap);
//        return jsonObject.getJSONObject("data");
//    }
//
//    private HashMap<String, String> getTaskMap(String cookie, String UA) {
//        HashMap<String, String> headerMap = new HashMap<>();
//        headerMap.put("Host", "api.m.jd.com");
//        headerMap.put("Connection", "keep-alive");
//        headerMap.put("Pragma", "no-cache");
//        headerMap.put("Cache-Control", "no-cache");
//        headerMap.put("Sec-Fetch-Site", "same-site");
//        headerMap.put("Origin", "https://spa.jd.com");
//        headerMap.put("Sec-Fetch-Mode", "cors");
//        headerMap.put("Sec-Fetch-Dest", "empty");
//        headerMap.put("Accept", "application/json");
//        headerMap.put("Referer", "https://spa.jd.com/");
//        headerMap.put("Accept-Encoding", "gzip, deflate, br");
//        headerMap.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
//        headerMap.put("User-Agent", UA);
//        headerMap.put("Cookie", cookie);
//        return headerMap;
//    }
//
//    @SneakyThrows
//    public String buildUrl(String functionId, String body) {
//        return new URIBuilder()
//                .setScheme(RequestConstant.SCHEME)
//                .setHost(RequestConstant.BASEHOST)
//                .setParameter(RequestConstant.FUNCTIONID, functionId)
//                .setParameter(RequestConstant.BODY, body)
//                .setParameter(RequestConstant.APPID, "vip_h5")
//                .setParameter("t", "1632109144140")
//                .build().toString();
//    }
//
//    @SneakyThrows
//    public String buildUrlSharkBean(String functionId, String body) {
//        System.out.println(String.valueOf(new Timestamp(System.currentTimeMillis())));
//        return new URIBuilder()
//                .setScheme(RequestConstant.SCHEME)
//                .setHost(RequestConstant.BASEHOST)
//                .setParameter("t", String.valueOf(System.currentTimeMillis()))
//                .setParameter(RequestConstant.APPID, "sharkBean")
//                .setParameter(RequestConstant.FUNCTIONID, functionId)
//                .setParameter(RequestConstant.BODY, body)
//                .build().toString();
//    }
//}
