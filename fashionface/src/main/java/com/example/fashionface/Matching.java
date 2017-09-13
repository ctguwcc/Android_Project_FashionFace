package com.example.fashionface;

/***
 *  这个部分主要是为了计算路径
 */

public class Matching {
	public static String sethuman(String msg,int i) {
		String pathString="";
		if(Integer.valueOf(msg)<=20){
			pathString="/20/pic"+i;
		}
		else if(Integer.valueOf(msg)<=25){
			pathString="/25/pic"+i;
		}
		else if(Integer.valueOf(msg)<=35){
			pathString="/30/pic"+i;
		}
		else if(Integer.valueOf(msg)>35){
			pathString="/35/pic"+i;
		}
		return pathString;
	}
	public static String setOtOMessage(String msg) {
		String messageString="";
		if(Integer.valueOf(msg)<=15){
			messageString="青春无敌呢，千万不要早恋哦~";
		}
		else if(Integer.valueOf(msg)<=25){
			messageString="嗯。到谈恋爱的年纪了呢。";
		}
		else if(Integer.valueOf(msg)<=35){
			messageString="这把年纪不要熬夜了哦，会长痘…";
		}
		else if(Integer.valueOf(msg)>35){
			messageString="叔叔阿姨，宝宝不约！";
		}
		return messageString;
	}
}
