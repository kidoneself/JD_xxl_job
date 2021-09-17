package com.xxl.job.executor.core;


import java.sql.Timestamp;

public class JDBodyParam {

    private final StringBuilder body = new StringBuilder();

    public JDBodyParam Key(String key) {
        body.append("\"").append(key).append("\"");
        return this;
    }

    public JDBodyParam stringValue(String value) {
        if (value == null) {
            body.append(":").append(",");
        }
        body.append(":").append("\"").append(value).append("\"").append(",");

        return this;
    }

    public JDBodyParam integerValue(Integer value) {
        if (value == null) {
            body.append(":").append("\"").append("\"").append(",");
        }
        body.append(":").append(value).append(",");
        return this;
    }

    public JDBodyParam integerValue(Timestamp value) {
        if (value == null) {
            body.append(":").append("\"").append("\"").append(",");
        }
        body.append(":").append(value).append(",");
        return this;
    }
    public String buildBody() {
        body.insert(0, "{").deleteCharAt(body.length() - 1).append("}");
        return body.toString();
    }

}