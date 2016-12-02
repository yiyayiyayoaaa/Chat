package com.microcardio.chat.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by AMOBBS on 2016/11/24.
 */
public class FileNameUtil {

    public static String randomFileName(String type){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        int random1 = new Random().nextInt(10);
        int random2 = new Random().nextInt(10);
        int random3 = new Random().nextInt(10);
        StringBuilder stringBuilder = new StringBuilder(format.format(new Date()));
        stringBuilder.append(random1).append(random2).append(random3).append(type);
        return  stringBuilder.toString();
    }
}
