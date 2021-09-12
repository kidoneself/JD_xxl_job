package com.xxl.job.executor.po.ddFarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class Task {

    private Boolean allTaskFinished;
    private String code;
    private SignInit signInit;
    private GotBrowseTaskAdInit gotBrowseTaskAdInit;
    private Object message;
    private JSONArray taskOrder;
    private GotThreeMealInit gotThreeMealInit;
    private FirstWaterInit firstWaterInit;
    private TotalWaterTaskInit totalWaterTaskInit;
    private Object statisticsTimes;
    private Long sysTime;
    private JSONObject orderInit;
    private WaterRainInit waterRainInit;
    private WaterFriendTaskInit waterFriendTaskInit;

}
