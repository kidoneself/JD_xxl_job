package com.xxl.job.executor.po.ddFarm;


import com.alibaba.fastjson.JSONArray;
import lombok.Data;

@Data
public class SignInit {
    private String signEnergyEachAmount;
    private Boolean f;
    private Integer totalSigned;
    private JSONArray signEnergyAmounts;
    private Boolean todaySigned;
}
