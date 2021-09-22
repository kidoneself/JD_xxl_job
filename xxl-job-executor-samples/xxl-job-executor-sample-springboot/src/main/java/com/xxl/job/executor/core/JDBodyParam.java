package com.xxl.job.executor.core;


import java.sql.Timestamp;

public class JDBodyParam {

    private final StringBuilder body = new StringBuilder();

    public JDBodyParam Key(String key) {
        body.append("\"").append(key).append("\"");
        return this;
    }

    public JDBodyParam stringValue(String value) {
        if (value == null)
            body.append(":").append(",");
        else
            body.append(":").append("\"").append(value).append("\"").append(",");
        return this;
    }

    public JDBodyParam stringValueNo(String value) {
        if (value == null)
            body.append(":").append(",");
        else
            body.append(":").append(value).append(",");
        return this;
    }

    public JDBodyParam integerValue(Integer value) {
        if (value == null)
            body.append(":").append("\"").append("\"").append(",");
        else
            body.append(":").append(value).append(",");
        return this;
    }

    public JDBodyParam integerValue(Timestamp value) {
        if (value == null)
            body.append(":").append("\"").append("\"").append(",");
        else
            body.append(":").append(value).append(",");
        return this;
    }

    public JDBodyParam boolValue(Boolean value) {
        if (value == null)
            body.append(":").append("\"").append("\"").append(",");
        else
            body.append(":").append(value).append(",");
        return this;
    }

    public String buildBody() {
        body.insert(0, "{").deleteCharAt(body.length() - 1).append("}");
        return body.toString();
    }

    // 不带双引号
    public JDBodyParam value(Object value) {
        if (value == null)
            body.append(":").append(",");
        else
            body.append(":").append(value).append(",");
        return this;
    }

    // 带双引号
    public JDBodyParam valueMark(Object value) {
        if (value == null)
            body.append(":").append("\"").append("\"").append(",");
        else
            body.append(":").append("\"").append(value).append("\"").append(",");

        return this;
    }
}