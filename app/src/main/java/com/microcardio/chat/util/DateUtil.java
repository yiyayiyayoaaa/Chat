package com.microcardio.chat.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	//将时间转成功字符串
	public static String parseStr(Date date){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.format(date);
	}
	//将字符串转成时间
	public static Date parseDate(String string){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			return dateFormat.parse(string);
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
