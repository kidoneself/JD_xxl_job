package com.xxl.job.executor.po;

import lombok.Data;

import java.sql.Date;

@Data
public class Env {
    private Integer id;
    private String envName;
    private String remarks;
    private String envValue;
    private Date createTime;
    private Integer sort;
    private Integer status;
    private String ua;
    private String shareCode;

}
