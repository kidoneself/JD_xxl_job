package com.xxl.job.executor.po.ddFarm;


import lombok.Data;

@Data
public class TotalWaterTaskInit {

    private Integer totalWaterTaskLimit;
    private Integer totalWaterTaskEnergy;
    private Boolean f;
    private Boolean totalWaterTaskFinished;
    private Integer totalWaterTaskTimes;
}
