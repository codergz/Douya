package com.example.gz494.douya.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.gz494.douya.R;
import com.example.gz494.douya.config.ServerUrlConfig;
import com.example.gz494.douya.listener.OnDownloadListener;
import com.example.gz494.douya.utils.okHttpUtils.DownloadUtil;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ImageView imageView;
    DownloadUtil downloadUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.image_view);
        //Glide.with(this).load("http://192.168.232.2:8080/bargains/images/baifukaoya.jpg").into(imageView);
        downloadUtil = DownloadUtil.getDownloadUtil();

        downloadUtil.download(ServerUrlConfig.PICTURE_SERVER_URL + "baifukaoya.jpg", "/data/data/com.example.gz494.douya/cache/", new OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.d(TAG, " 下载成功");
            }

            @Override
            public void onDownloadingProcess(int process) {
                Log.d(TAG, "Process : " + process);
            }

            @Override
            public void onDownloadFailed() {
                Log.d(TAG, "下载失败");
            }
        });
    }
}
