package com.xxl.job.executor.po.getJinDou;

import lombok.Data;

import java.util.List;

public @Data class JDInfo {
    private int curLevel;
    private int nextLevelBeanNum;
    private String taskTopIcon;
    private ViewAppHome viewAppHome;
    private List<TaskInfosItem> taskInfos;
}