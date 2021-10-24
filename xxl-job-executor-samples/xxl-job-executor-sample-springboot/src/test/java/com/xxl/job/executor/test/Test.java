package com.xxl.job.executor.test; /**
 * Created by codeya on 2018/3/2.
 */

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.StringReader;

public class Test {
    public static void main(String args[]) {

        //js function：getRouteInfo，入参为province
        String routeScript = "function hashCode(pin) {" +
                "    for (var _0x3da2a7 = 0x0, _0x2c251a = 0x0, _0x556286 = (pin += '').length, _0x55aac4 = 0x0; _0x55aac4 < _0x556286; _0x55aac4++)\n" +
                "        (0x7fffffff < (_0x3da2a7 = 0x1f * _0x3da2a7 + pin.charCodeAt(_0x2c251a++)) || _0x3da2a7 < 0x80000000) && (_0x3da2a7 &= 0xffffffff);\n" +
                "    return Math.abs(_0x3da2a7);" +
                "}";

        Integer scriptResult = null;//脚本的执行结果
        Integer scriptResult2= null;//脚本的执行结果

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");//1.得到脚本引擎
        try {
            //2.引擎读取 脚本字符串
            engine.eval(new StringReader(routeScript));
            //如果js存在文件里，举例
            // Resource aesJs = new ClassPathResource("js/aes.js");
            // this.engine.eval(new FileReader(aesJs.getFile()));

            //3.将引擎转换为Invocable，这样才可以掉用js的方法
            Invocable invocable = (Invocable) engine;

            //4.使用 invocable.invokeFunction掉用js脚本里的方法，第一個参数为方法名，后面的参数为被调用的js方法的入参
            scriptResult = (Integer) invocable.invokeFunction("hashCode", "jd_PUDaNSWCqtZp");
            scriptResult2 = (Integer) invocable.invokeFunction("hashCode", "7!^h&bj#cf$fe&%$8aj6");

        } catch (ScriptException e) {
            e.printStackTrace();
            System.out.println("Error executing script: " + e.getMessage() + " script:[" + routeScript + "]");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.out.println("Error executing script,为找到需要的方法: " + e.getMessage() + " script:[" + routeScript + "]");
        }
        System.out.println(scriptResult + scriptResult2);
    }
}