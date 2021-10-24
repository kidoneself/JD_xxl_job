package com.xxl.job.executor.po.joy;


import lombok.Data;

import java.util.List;

@Data
public class CrazyJoyUserInfo {

    private Integer totalCoinAmount;
    private Integer offlineCoinAmount;
    private List<Integer> joyIds;
    private Integer currentSceneId;
    private String noticeFlag;
    private Integer userTopLevelJoyId;
    private String nickName;
    private Boolean newUserFlag;
    private Integer hourCoinCountDown;
    private Boolean growthAwardFlag;
    private String userInviteCode;
    private String userId;

}
