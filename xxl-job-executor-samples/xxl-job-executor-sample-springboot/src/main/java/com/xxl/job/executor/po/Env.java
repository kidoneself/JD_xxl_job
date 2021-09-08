package com.xxl.job.executor.po;

import lombok.Data;

import java.sql.Date;

@Data
public class Env {
    private Integer id;
    private String evnName;
    private String remarks;
    private String evnValue;
    private Date createTime;
    private Integer sort;
    private Integer status;

}
