package com.microcardio.chat.util;

import com.microcardio.chat.po.Constants;

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

    public static boolean isImage(String content){
        //String regex = "(http://"+ Constants.SERVER_ADDRESS + ":8080/upload/)(.)+(\\.amr)(.)+";
        if(content.startsWith(Constants.FILE_PATH) && !content.contains(".amr")){
            return true;
        }else {
            return false;
        }
    }


    public static boolean isAudio(String content){
        String regex = "(http://"+ Constants.SERVER_ADDRESS + ":8080/upload/)(.)+(\\.amr)(.)+";
//        System.out.println("regex"+regex);
//        System.out.println("regex"+content.matches(regex));
        if(content.matches(regex)){
            return true;
        }else {
            return false;
        }
    }
}
