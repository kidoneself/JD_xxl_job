package com.xxl.job.executor.core;


import java.sql.Timestamp;

public class Body {

    private final StringBuilder body = new StringBuilder();

    public Body Key(String key) {
        body.append("\"").append(key).append("\"");
        return this;
    }

    public Body stringValue(String value) {
        if (value == null) {
            body.append(":").append(",");
        }
        body.append(":").append("\"").append(value).append("\"").append(",");

        return this;
    }

    public Body integerValue(Integer value) {
        if (value == null) {
            body.append(":").append("\"").append("\"").append(",");
        }
        body.append(":").append(value).append(",");
        return this;
    }

    public Body integerValue(Timestamp value) {
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