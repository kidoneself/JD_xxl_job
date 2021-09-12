package com.xxl.job.executor.po.ddFarm;

import lombok.Data;

@Data
public class HelpResult {
    private Integer remainTimes;
    private String code;
    private MasterUserInfo masterUserInfo;
}
