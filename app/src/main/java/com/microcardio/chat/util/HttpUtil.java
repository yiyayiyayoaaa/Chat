package com.microcardio.chat.util;

import com.microcardio.chat.activity.ChatActivity;
import com.microcardio.chat.po.Constants;
import com.microcardio.chat.po.Message;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by AMOBBS on 2016/11/23.
 */
public class HttpUtil {

    public static void upFile(File file, String newFileName, final Message message){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        })
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .readTimeout(4000,TimeUnit.MILLISECONDS)
                .writeTimeout(4000, TimeUnit.MILLISECONDS)
                .build();
        /* 上传的file */
       // File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.jpg");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , file);
        String fileName = file.getName();
        /* form的分割线 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
            /* 上传一个普通的String参数 , key 叫 "p" */
            /* 底下是上传了两个文件 */
                .addFormDataPart("file" , fileName , fileBody)
                .build();

    /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(Constants.UPLOAD_URL+"?fileName="+newFileName).post(mBody).build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                android.os.Message msg = ChatActivity.handler.obtainMessage();
                msg.obj = message;
                msg.what = 111;
                ChatActivity.handler.sendMessage(msg);
                //System.out.println("服务器响应");
            }
            public void onFailure(Call call, final IOException e) {
            }
        });
    }


    public static void upAudio(File file, String newFileName, final Message message){
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                return response;
            }
        })
                .connectTimeout(4000, TimeUnit.MILLISECONDS)
                .readTimeout(4000,TimeUnit.MILLISECONDS)
                .writeTimeout(4000, TimeUnit.MILLISECONDS)
                .build();
        /* 上传的file */
        // File file1 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/a.jpg");
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream") , file);
        String fileName = file.getName();
        /* form的分割线 */
        String boundary = "xx--------------------------------------------------------------xx";

        MultipartBody mBody = new MultipartBody.Builder(boundary).setType(MultipartBody.FORM)
            /* 上传一个普通的String参数 , key 叫 "p" */
            /* 底下是上传了两个文件 */
                .addFormDataPart("file" , fileName , fileBody)
                .build();

    /* 下边的就和post一样了 */
        Request request = new Request.Builder().url(Constants.UPLOAD_URL+"?fileName="+newFileName).post(mBody).build();
        client.newCall(request).enqueue(new Callback() {
            public void onResponse(Call call, Response response) throws IOException {
                android.os.Message msg = ChatActivity.handler.obtainMessage();
                msg.obj = message;
                msg.what = 111;
                ChatActivity.handler.sendMessage(msg);
                //System.out.println("服务器响应");
            }
            public void onFailure(Call call, final IOException e) {
            }
        });
    }
}
