package com.xxl.job.executor.test;

import com.alibaba.fastjson.JSONObject;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.xxl.job.executor.core.HeaderUtil;
import com.xxl.job.executor.core.JDBodyParam;
import com.xxl.job.executor.core.RequestConstant;
import com.xxl.job.executor.po.ShakeList;
import com.xxl.job.executor.po.TaskItemsItem;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() throws UnirestException, URISyntaxException {
//
        HashMap<String, String> headerMap = new HashMap<>();
        headerMap.put("Host", "api.m.jd.com");
        headerMap.put("Connection", "keep-alive");
        headerMap.put("Pragma", "no-cache");
        headerMap.put("Cache-Control", "no-cache");
        headerMap.put("Sec-Fetch-Site", "same-site");
        headerMap.put("Origin", "https://spa.jd.com");
        headerMap.put("Sec-Fetch-Mode", "cors");
        headerMap.put("Sec-Fetch-Dest", "empty");
        headerMap.put("Accept", "application/json");
        headerMap.put("Referer", "https://spa.jd.com/");
        headerMap.put("Accept-Encoding", "gzip, deflate, br");
        headerMap.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
        headerMap.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1");
        headerMap.put("cookie", "pt_key=AAJhRyK1ADCfaMLMkUA96laOm1_845DZqAuxdaP7mSbEeNfmuQoM2kItc-La3dm18Mb9e37nJ1w;pt_pin=wdlLxrYZBojiba;");

        //1.生成httpclient，相当于该打开一个浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            URI uri = new URI("https://api.m.jd.com/?t=1632064755196&appid=vip_h5&functionId=vvipclub_lotteryTask&body=%7B%22info%22:%22browseTask%22,%22withItem%22:true%7D");
            HttpGet request = new HttpGet(uri);
            Header[] headers = HeaderUtil.convertHeader(headerMap);
            request.setHeaders(headers);
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            String shakeListRes = EntityUtils.toString(httpEntity, "utf-8");
            JSONObject shakeListObj = JSONObject.parseObject(shakeListRes);
            ShakeList shakeList1 = JSONObject.toJavaObject(shakeListObj, ShakeList.class);
            List<TaskItemsItem> sharkLists = shakeList1.getData().get(0).getTaskItems();

            sharkLists.forEach(sharkList -> {
                if (!sharkList.isFinish()) {
                    String body = new JDBodyParam()
                            .keyMark("taskName").valueMark("browseTask")
                            .keyMark("taskItemId").value(sharkList.getId()).
                            buildBody();


                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }

    }

    @Test
    public void extracted() {
        System.out.println(generateFp());
    }

    private long generateFp() {
        while (true) {
            long numb = (long) (Math.random() * 100000000 * 1000000); // had to use this as int's are to small for a 13 digit number.
            if (String.valueOf(numb).length() == 13) {
                System.out.println(numb);
                return numb;
            }
        }
    }


    public String doGet(String url, Map<String, String> headersMap) {
        //1.生成httpclient，相当于该打开一个浏览器
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        try {
            HttpGet request = new HttpGet(url);
            Header[] headers = HeaderUtil.convertHeader(headersMap);
            request.setHeaders(headers);
            response = httpClient.execute(request);
            HttpEntity httpEntity = response.getEntity();
            return EntityUtils.toString(httpEntity, "utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //6.关闭
            HttpClientUtils.closeQuietly(response);
            HttpClientUtils.closeQuietly(httpClient);
        }
        return null;
    }

    @Test
    public void buildUrl() throws URISyntaxException {
        String body = "{\"xxxxx\"}";
        String url = new URIBuilder()
                .setScheme(RequestConstant.SCHEME)
                .setHost(RequestConstant.HOST)
                .setParameter(RequestConstant.FUNCTIONID, "functionId")
                .setParameter(RequestConstant.BODY, body)
                .setParameter(RequestConstant.APPID, "vip_h5")
                .setParameter("t", new Timestamp(System.currentTimeMillis()).toString())
                .build().toString();
        System.out.println(url);
    }


    @Test
    public void aaa() throws InterruptedException {

        long v = (long) (Math.floor(Math.random() * (4000 - 3000)) + 1000);
        System.out.println(v);

    }
}