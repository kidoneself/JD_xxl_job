package com.xxl.job.executor.test;
 
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
 
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
 
/**
 * 
 * @author chenjiahui
 *
 */
public class Test {
 
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ScriptException 
	 */
	public static void main(String[] args) throws ScriptException, IOException {
		// TODO Auto-generated method stub
			ScriptEngine engine = new ScriptEngineManager().getEngineByExtension("js"); 
			engine.eval(loadAFileToStringDE2());
			Invocable inv = (Invocable) engine;      
			try {
				//invokeFunction()中的第一个参数就是被调用的脚本程序中的函数，第二个参数是传递给被调用函数的参数；  
				engine.put("name", "chenjiahui");
				String.valueOf(inv.invokeFunction("9pqV","研发部"));
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}   
 
	}
 
	private static String loadAFileToStringDE2() throws IOException {
		File f = new File("C:\\Users\\Lenovo\\Desktop\\js.js");
		InputStream is = null;
		String ret = null;
		try {
			is = new FileInputStream(f);
			long contentLength = f.length();
			byte[] ba = new byte[(int) contentLength];
			is.read(ba);
			ret = new String(ba);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					
				}
			}
		}
//		System.out.println(ret);
		return ret;
	}
}