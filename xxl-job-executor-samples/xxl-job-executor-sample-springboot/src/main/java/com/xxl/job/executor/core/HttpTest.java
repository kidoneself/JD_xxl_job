package com.xxl.job.executor.core;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;

import java.net.URI;

public class HttpTest {

    //调用
    public static void main(String arg[]) throws Exception {


        URI uri = new URIBuilder()
                .setScheme("https")
                .setHost("api.m.jd.com/client.action")
                .setParameter("functionId", "taskInitForFarm")
                .setParameter("body", "{\"version\":14,\"channel\":1,\"babelChannel\":\"121\"}")
                .setParameter("appid", "wh5")
                .build();
        System.out.println(uri.toString());
    }
}