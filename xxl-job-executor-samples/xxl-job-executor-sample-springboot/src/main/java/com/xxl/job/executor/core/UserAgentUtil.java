package com.xxl.job.executor.core;

import java.util.HashMap;

public class UserAgentUtil {


    public static String randomUserAgent() {
        String jdVersion = "10.1.2";
        String osb = String.format("%.0f", Math.random() * (2) + 12);
        String oss = String.format("%.0f", Math.ceil(Math.random() * 4));
        String uuid = getRandomString();
        return String.format("jdapp;iPhone;%s;%s.%s;%s;network/wifi;model/iPhone12,1;addressid/0;appBuild/167802;jdSupportDarkMode/0;Mozilla/5.0 (iPhone; CPU iPhone OS %s_%s like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1"
                , jdVersion
                , osb
                , oss
                , uuid
                , osb
                , oss);

    }

    public static HashMap<String, String> randomUserAgentMsg() {
        String jdVersion = "10.1.2";
        String osb = String.format("%.0f", Math.random() * (2) + 12);
        String oss = String.format("%.0f", Math.ceil(Math.random() * 4));
        String uuid = getRandomString();
        String ua = String.format("jdapp;iPhone;%s;%s.%s;%s;network/wifi;model/iPhone12,1;addressid/0;appBuild/167802;jdSupportDarkMode/0;Mozilla/5.0 (iPhone; CPU iPhone OS %s_%s like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148;supportJDSHWK/1"
                , jdVersion
                , osb
                , oss
                , uuid
                , osb
                , oss);
        HashMap<String, String> meg = new HashMap<>();
        meg.put("jdVersion", jdVersion);
        meg.put("osb", osb);
        meg.put("oss", oss);
        meg.put("uuid", uuid);
        meg.put("ua", ua);
        return meg;
    }

    private static String getRandomString() {
        int a = 40 | 32;
        String list = "abcdef0123456789";
        char[] chars = list.toCharArray();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < a; i++) {
            randomString.append(chars[(int) Math.floor(Math.random() * chars.length)]);
        }
        return randomString.toString();
    }
}

