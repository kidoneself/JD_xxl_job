package com.xxl.job.executor.po.ddFarm;


import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class WaterRainInit {

    private Integer lastTime;
    private Boolean f;
    private Integer winTimes;
    private JSONObject config;
}
