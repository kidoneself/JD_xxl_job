package com.xxl.job.executor.po.ddFarm;

import lombok.Data;
import java.util.List;


@Data
public class InitFromFriends {

    private String shareCodeInviteNotFarmAddOn;
    private String code;
    private String shareCodeCallAddOn;
    private String inviteFriendCountToPropsSendCard;
    private int inviteFriendCountToProps;
    private int inviteFriendGotAwardCount;
    private int inviteFriendCount;
    private int inviteFirstFriendAwardWater;
    private int awardCallUserWater;
    private String shareCodeInviteAddOn;
    private boolean hadGotInviteFriendCountToProps;
    private String statisticsTimes;
    private long sysTime;
    private boolean fullFriend;
    private int inviteFriendAwardEach;
    private int countOfFriend;
    private String message;
    private List<Friends> friends;
    private boolean newUserAward;
    private int deleteFriendCount;
    private int awardInviteNewUserSendWater;
    private int inviteFriendMax;
    private boolean newFriendMsg;
    private boolean loadFriend;


}