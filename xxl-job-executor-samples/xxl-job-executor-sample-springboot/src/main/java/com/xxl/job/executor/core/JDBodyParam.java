package com.xxl.job.executor.core;


public class JDBodyParam {

    private final StringBuilder body = new StringBuilder();

    // 不带双引号
    public JDBodyParam key(String key) {
        body.append(key);
        return this;
    }

    public JDBodyParam value(Object value) {
        if (value == null)
            body.append("%3A").append("%2C");
        else
            body.append("%3A").append(value).append("%2C");
        return this;
    }

    // 带双引号

    /**
     * 带双引号
     * @param key
     * @return
     */
    public JDBodyParam keyMark(String key) {
        body.append("%22").append(key).append("%22");
        return this;
    }

    public JDBodyParam valueMark(Object value) {
        if (value == null)
            body.append("%3A").append("%22").append("%22").append("%2C");
        else
            body.append("%3A").append("%22").append(value).append("%22").append("%2C");
        return this;
    }


    public String buildBody() {
        body.insert(0, "%7B").deleteCharAt(body.length() - 1).deleteCharAt(body.length() - 1).deleteCharAt(body.length() - 1).append("%7D");
        return body.toString();
    }


}