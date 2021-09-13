package com.xxl.job.executor.test;

import com.xxl.job.core.log.XxlJobLogger;
import com.xxl.job.executor.core.Body;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class XxlJobExecutorExampleBootApplicationTests {

    @Test
    public void test() {
        String s = new Body().Key("1").stringValue("").buildBody();
        System.out.println(s);

    }

}