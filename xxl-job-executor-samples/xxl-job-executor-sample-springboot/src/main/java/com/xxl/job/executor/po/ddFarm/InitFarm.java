
package com.xxl.job.executor.po.ddFarm;

import lombok.Data;

import java.util.Date;
import java.util.List;


@Data
public class InitFarm {

    private String code;
    private boolean showExchangeGuidance;
    private boolean clockInGotWater;
    private boolean isOpenOldRemind;
    private GuidPopupTask guidPopupTask;
    private int toFruitEnergy;
    private long sysTime;
    private boolean canHongbaoContineUse;
    private int shareChannelType;
    private int toFlowTimes;
    private IosConfigResouces iosConfigResouces;
    private TodayGotWaterGoalTask todayGotWaterGoalTask;
    private int lowFreqStatus;
    private boolean funCollectionHasLimit;
    private int treeState;
    private boolean iconFirstPurchaseInit;
    private int toFlowEnergy;
    private FarmUserPro farmUserPro;
    private int retainPopupLimit;
    private int toBeginEnergy;
    private CollectPopWindow collectPopWindow;
    private boolean enableSign;
    private LoadFriend loadFriend;
    private boolean hadCompleteXgTask;
    private List<Integer> oldUserIntervalTimes;
    private int toFruitTimes;
    private List<String> oldUserSendWater;
}