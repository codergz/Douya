package com.example.gz494.douya.activity;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.gz494.douya.R;
import com.example.gz494.douya.config.ServerUrlConfig;
import com.example.gz494.douya.listener.OnUploadResultListener;
import com.example.gz494.douya.utils.okHttpUtils.FileUploadUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gz494 on 2018/9/3.
 */

public class UploadActivity extends Activity {
    private static final String TAG = "UploadActivity";
    Button button;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = findViewById(R.id.btn_start_download);
        final Map<String, String> mFormMap = new HashMap<>();
        mFormMap.put("username", "gaozhan");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/data/data/com.example.gz494.douya/cache/baifukaoya.jpg");
                FileUploadUtil.getInstance().uploadFile(1, mFormMap, "file1", file, "baibaibai.jpg", ServerUrlConfig.SERVER_ADDRESS + ServerUrlConfig.UPLOAD_FILE,
                        new OnUploadResultListener() {
                            @Override
                            public void uploadSuccess() {
                                Log.d(TAG, "uploadSuccess: ");
                            }

                            @Override
                            public void uploadProcess(int process) {

                                Log.d(TAG, "uploadProcess: " + process);
                            }

                            @Override
                            public void uploadFailed() {
                                Log.d(TAG, "uploadFailed" );
                            }
                        });
            }
        });
    }
}
