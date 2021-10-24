package com.xxl.job.executor.service.JDhandler.JDBeanUtils;


import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.IJobHandler;
import com.xxl.job.executor.po.Env;
import com.xxl.job.executor.po.JDUser;
import com.xxl.job.executor.po.SendBeans;
import com.xxl.job.executor.service.CommonDo.CommonHandler;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class VenueSign extends IJobHandler {
    @Resource
    private CommonHandler commonHandler;
    Env env;
    List<Env> envs;
    HashMap<String, String> headerMap = new HashMap<>();
    JDUser userInfo;
    String lkt;
    String lks;
    Integer completeNumbers;
    String activityId;
    String activityCode;
    Integer rewardRecordId;
    Boolean completed;
    Boolean rewardOk;
    String inviteUserPin;
    List<SendBeans> helpUsers;
    List<Env> didHelp = new ArrayList<>(10);


    @Override
    public ReturnT<String> execute(String param) throws Exception {

//String[] turnTableId =
//        { "name": "PLUS会员定制", "id": 1265, "url": "https://prodev.m.jd.com/mall/active/3bhgbFe5HZcFCjEZf2jzp3umx4ZR/index.html" },
//        { "name": "京东商城-内衣", "id": 1071, "url": "https://prodev.m.jd.com/mall/active/4PgpL1xqPSW1sVXCJ3xopDbB1f69/index.html" },
//        { "name": "京东商城-健康", "id": 527, "url": "https://prodev.m.jd.com/mall/active/w2oeK5yLdHqHvwef7SMMy4PL8LF/index.html" },
//        { "name": "京东商城-清洁", "id": 446, "url": "https://prodev.m.jd.com/mall/active/2Tjm6ay1ZbZ3v7UbriTj6kHy9dn6/index.html" },
//        { "name": "京东商城-个护", "id": 336, "url": "https://prodev.m.jd.com/mall/active/2tZssTgnQsiUqhmg5ooLSHY9XSeN/index.html" },
//        { "name": "京东商城-童装", "id": 511, "url": "https://prodev.m.jd.com/mall/active/3Af6mZNcf5m795T8dtDVfDwWVNhJ/index.html" },
//        { "name": "京东商城-母婴", "id": 458, "url": "https://prodev.m.jd.com/mall/active/3BbAVGQPDd6vTyHYjmAutXrKAos6/index.html" },
//        { "name": "京东商城-数码", "id": 347, "url": "https://prodev.m.jd.com/mall/active/4SWjnZSCTHPYjE5T7j35rxxuMTb6/index.html" },
//        { "name": "京东超市", "id": 1204, "url": "https://pro.m.jd.com/mall/active/QPwDgLSops2bcsYqQ57hENGrjgj/index.html" },
        WebDriver webDriver = new ChromeDriver();
        webDriver.get("https://pro.m.jd.com/mall/active/QPwDgLSops2bcsYqQ57hENGrjgj/index.html");
        String windowHandle = webDriver.getWindowHandle();
        Cookie[] cookies = resolveCookies(env.getEnvValue());
        for (Cookie cookie : cookies) {
            webDriver.manage().addCookie(cookie);
        }
        webDriver.navigate().refresh();
        String pageSource = webDriver.getPageSource();
        WebElement sign_btn = webDriver.findElement(By.className("sign_btn"));

        return null;
    }
    static Cookie[] resolveCookies(String s) {
        ArrayList<Cookie> cookies = new ArrayList<Cookie>();
        while (s.length() != 0) {
            int index = s.indexOf('=');
            if (index == -1)
                break;
            String cookieName = s.substring(0, index).trim();
            int endIndex = s.indexOf(';');
            if (endIndex == -1) {
                endIndex = s.length();
                String cookieValue = s.substring(index + 1, endIndex).trim();
                cookies.add(new Cookie(cookieName, cookieValue));
                break;
            }
            String cookieValue = s.substring(index + 1, endIndex).trim();
            cookies.add(new Cookie(cookieName, cookieValue));
            s = s.substring(endIndex + 1);
        }
        return cookies.toArray(new Cookie[0]);
    }
    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }
}
