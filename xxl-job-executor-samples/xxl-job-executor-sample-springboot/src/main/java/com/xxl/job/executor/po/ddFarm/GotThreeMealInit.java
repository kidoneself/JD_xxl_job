package com.xxl.job.executor.po.ddFarm;


import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class GotThreeMealInit {

    private String threeMealAmount;
    private Integer pos;
    private Boolean hadGotShareAmount;
    private Boolean f;
    private JSONArray threeMealTimes;
    private Boolean hadGotAmount;
}
