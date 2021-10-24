package com.xxl.job.executor.core;

import javax.script.*;
import java.util.HashMap;
import java.util.Map;

public class ScriptCompile {
    CompiledScript script;
    ScriptEngine engine;

    ScriptCompile(String scriptText) {
        this.script = initScript(scriptText, null);
    }

    /**
     * @param scriptText js脚本内容
     * @param initParams 在编译时, 初始化时传入脚本的参数
     */
    ScriptCompile(String scriptText, Map<String, Object> initParams) {


        //构造函数, 先编译
        this.script = initScript(scriptText, initParams);
    }

    public CompiledScript initScript(String scriptText, Map<String, Object> initParams) {
        CompiledScript script = null;
        ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js");
        if (initParams != null && !initParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : initParams.entrySet()) {
                engine.put(entry.getKey(), entry.getValue());
            }
        }
        this.engine = engine;
        if (engine instanceof Compilable) {
            try {
                script = ((Compilable) engine).compile(scriptText);
            } catch (ScriptException e) {
                e.printStackTrace();
            }
        }
        return script;
    }

    /**
     * 执行脚本
     *
     * @param bindingsMap 本次执行时传入的参数
     * @return
     * @throws ScriptException
     */
    public Object execute(Map<String, Object> bindingsMap) throws ScriptException {
        if (bindingsMap != null && !bindingsMap.isEmpty()) {
            Bindings bindings = new SimpleBindings();
            for (Map.Entry<String, Object> entry : bindingsMap.entrySet()) {
                bindings.put(entry.getKey(), entry.getValue());
            }
            return script.eval(bindings);
        } else {
            return script.eval();
        }
    }

    public Object execute() throws ScriptException {
        return execute(null);
    }

    /**
     * 调用脚本中的某个函数
     *
     * @param bindingsMap 本次执行传入的参数
     * @param fucName     函数名
     * @param args        函数参数,可以有多个参数
     * @return
     * @throws Exception
     */
    public Object executeFuc(Map<String, Object> bindingsMap, String fucName, Object... args) throws Exception {
        execute(bindingsMap);
        Invocable inv2 = (Invocable) engine;
        return inv2.invokeFunction(fucName, args);
    }

    public Object executeFuc(String fucName, Object... args) throws Exception {
        return executeFuc(null, fucName, args);
    }

//    public static void main(String[] args) {
//        String pin = "怪盗基德8768611";
//
//        int i = 0x0;
//        int x = 0x0;
//        int y = 0x0;
//        for (; y < pin.length(); y++) {
////            (0x7fffffff < (i = 31 * i + (int) pin.charAt(x++)) || i < 0x80000000) && (i &= 0xffffffff);
//        }
////        return Math.abs(i);
//
//
//        int sum = 0;
//        char[] chars = pin.toCharArray();
//        for (char aChar : chars) {
//            sum = sum * 31 + (int) aChar;
//            sum &= 0x7fffffff;
//        }
//        System.out.println(Math.abs(sum));
//        String pin2 = "7!^h&bj#cf$fe&%$8aj6";
//        int sum2 = 0;
//        char[] chars2 = pin2.toCharArray();
//        for (char aChar : chars2) {
//            sum2 = sum2 * 31 + (int) aChar;
//            sum2 &= 0x7fffffff;
//        }
//        System.out.println(Math.abs(sum2));
//        System.out.println(sum + sum2);
//
//
//        Map<String, Object> map = new HashMap<>();
//        ScriptCompile sc = new ScriptCompile(
//                "function hashCode(pin) {" +
//                        "    var i = 0x0;" +
//                        "    var k = (pin += '').length;" +
//                        "    var x = 0x0;" +
//                        "    var y = 0x0;" +
//                        "    for (; y < k; y++)" +
//                        "        (0x7fffffff < (i = 0x1f * i + pin.charCodeAt(x++)) || i < 0x80000000) && (i &= 0xffffffff);" +
//                        "    return Math.abs(i);" +
//                        "}", map);
//
//        for (int  = 0; i < 5; i++) {
//            try {
//                Object o = sc.executeFuc("hashCode", "xly8380");
//                System.out.println(o);
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//    }
}
