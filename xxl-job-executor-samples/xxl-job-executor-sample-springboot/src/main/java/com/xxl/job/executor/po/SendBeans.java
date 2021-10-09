package com.xxl.job.executor.po;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SendBeans {

    String username;
    Integer rewardRecordId;
    Boolean completed;
    Boolean rewardOk;

}
