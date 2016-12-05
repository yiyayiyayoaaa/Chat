package com.microcardio.chat.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Display;

/**
 * Created by AMOBBS on 2016/11/25.
 */
public class BitMapUtil {
    public  static Bitmap narrowImage(String path, Activity activity){

        BitmapFactory.Options opts = new BitmapFactory.Options();
        //只请求图片宽高，不解析图片像素
        opts.inJustDecodeBounds = true;
        //返回null，获取图片宽高，保存在opts对象中
        BitmapFactory.decodeFile(path, opts);
        //获取图片宽高
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        System.out.println("---path:"+path + "---height" + imageHeight + "---width" + imageWidth);
        //获取屏幕宽高
        Display dp = activity.getWindowManager().getDefaultDisplay();
        int screenWidth = dp.getWidth();
        int screenHeight = dp.getHeight();

        //计算缩放比例
        int scale = 1;
        int scaleWidth = imageWidth / (screenWidth/3);
        int scaleHeight = imageHeight / (screenHeight/3);

        //判断取哪个比例
        if(scaleWidth >= scaleHeight && scaleWidth > 1){
            scale = scaleWidth;
        }
        else if(scaleWidth < scaleHeight && scaleHeight > 1){
            scale = scaleHeight;
        }

        //设置缩小比例
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        //获取缩小后的图片的像素信息
        return BitmapFactory.decodeFile(path, opts);

    }

    public  static Bitmap viewImage(String path, Activity activity){
        int actionbarHeight = 0;
        if(activity.getActionBar() != null){
            actionbarHeight = activity.getActionBar().getHeight();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        //只请求图片宽高，不解析图片像素
        opts.inJustDecodeBounds = true;
        //返回null，获取图片宽高，保存在opts对象中
        BitmapFactory.decodeFile(path, opts);
        //获取图片宽高
        int imageWidth = opts.outWidth;
        int imageHeight = opts.outHeight;
        System.out.println("---path:"+path + "---height" + imageHeight + "---width" + imageWidth);
        //获取屏幕宽高
        Display dp = activity.getWindowManager().getDefaultDisplay();
        int screenWidth = dp.getWidth();
        int screenHeight = dp.getHeight();

        //计算缩放比例
        int scale = 1;
        int scaleWidth = imageWidth /(screenWidth);
        int scaleHeight = imageHeight / (screenHeight-actionbarHeight);
        System.out.println(screenHeight);
        //判断取哪个比例
        if(scaleWidth >= scaleHeight && scaleWidth > 1){
            scale = scaleWidth;
        }
        else if(scaleWidth < scaleHeight && scaleHeight > 1){
            scale = scaleHeight;
        }
        //设置缩小比例
        System.out.println("scaleHeight"+scaleHeight);
        System.out.println("scaleWidth"+scaleWidth);
        System.out.println(scale);
        opts.inSampleSize = scale;
        opts.inJustDecodeBounds = false;
        //获取缩小后的图片的像素信息
        return BitmapFactory.decodeFile(path, opts);

    }
}
