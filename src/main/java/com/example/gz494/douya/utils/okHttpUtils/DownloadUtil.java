package com.example.gz494.douya.utils.okHttpUtils;

import android.support.annotation.NonNull;

import com.example.gz494.douya.listener.OnDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by gz494 on 2018/3/25.
 */

public class DownloadUtil {
    private static DownloadUtil mDownloadUtil;
    private OkHttpClient mOkHttpClient;

    public static DownloadUtil getDownloadUtil(){
        if(mDownloadUtil == null){
            mDownloadUtil = new DownloadUtil();
        }
        return mDownloadUtil;
    }
    private DownloadUtil() {
        mOkHttpClient = new OkHttpClient();
    }

    /**
     *
     * @param url
     * @param fileSaveDir
     * @param onDownloadListener
     */
    public void download(final String url, final String fileSaveDir, final OnDownloadListener onDownloadListener) {
        final Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                onDownloadListener.onDownloadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buffer = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                String savePath = isExistDir(fileSaveDir);
                try{
                    is = response.body().byteStream();
                    long total = response.body().contentLength();
                    File file = new File(savePath, getNameFromUrl(url));
                    fos = new FileOutputStream(file);
                    long sum = 0;
                    while((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        sum += len;
                        int process = (int) (sum * 1.0f / total * 100);
                        onDownloadListener.onDownloadingProcess(process);
                    }
                    fos.flush();
                    onDownloadListener.onDownloadSuccess();
                } catch (Exception e){
                    e.printStackTrace();
                    onDownloadListener.onDownloadFailed();
                }

            }
        });
    }

    public String isExistDir(String fileSaveDir) throws IOException {
        String savePath = null;
        File downloadFile = new File(fileSaveDir);
        if(downloadFile.exists()){
            return downloadFile.getAbsolutePath();
        }

        if(!downloadFile.mkdirs()){
            downloadFile.createNewFile();
        }
        savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    @NonNull
    private String getNameFromUrl(String url){
        return url.substring(url.lastIndexOf("/") + 1);
    }



}
