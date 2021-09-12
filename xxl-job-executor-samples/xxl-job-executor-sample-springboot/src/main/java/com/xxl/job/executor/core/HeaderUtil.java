package com.xxl.job.executor.core;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.ArrayList;
import java.util.Map;

public class HeaderUtil {

    public static Header[] convertHeader(Map<String, String> headerMap) {
        ArrayList<BasicHeader> basicHeaders = new ArrayList<>();
        headerMap.forEach((k, v) -> basicHeaders.add(new BasicHeader(k, v)));
        return basicHeaders.toArray(new Header[basicHeaders.size()]);
    }
}
