package com.microcardio.chat.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by AMOBBS on 2016/12/9.
 */
public class LruCacheUtil {
    //单例  
    private static LruCacheUtil lruCacheUtil;

    //内存缓存大小  
    private final int MEMO_CACHE_SIZE=((int)(Runtime.getRuntime().maxMemory()/1024));

    //内存缓存
    private LruCache<String,Bitmap> mMemoryCache;

    private LruCacheUtil() {
        //内存缓存  
        mMemoryCache=new LruCache<String, Bitmap>(MEMO_CACHE_SIZE / 8){
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount()/1024;
            };
        };
    }

    /**
     * 获取单例 
     * @return
     */
    public static LruCacheUtil getInstance() {
        if(lruCacheUtil==null){
            lruCacheUtil=new LruCacheUtil();
        }
        return lruCacheUtil;
    }

    /**
     * 图片加入内存缓存 
     * @param key
     * @param bitmap
     */
    public void addBitmapToMemory(String key,Bitmap bitmap){
        if(getBitmapFromMemory(key)==null){
           // System.out.println("dsadsdsadsdsadsadadsadsadsadad" + mMemoryCache);
           // System.out.println(key+"----"+bitmap);
            mMemoryCache.put(key, bitmap);
        }
    }
    /**
     * 获取内存缓存图片 
     * @param key
     * @return
     */
    public Bitmap getBitmapFromMemory(String key){
        return mMemoryCache.get(key);
    }
}
