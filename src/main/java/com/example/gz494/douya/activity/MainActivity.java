package com.example.gz494.douya.activity;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gz494.douya.R;
import com.example.gz494.douya.config.ServerUrlConfig;
import com.example.gz494.douya.listener.OnDownloadListener;
import com.example.gz494.douya.utils.okHttpUtils.DownloadUtilWithOkHttp;

import java.io.File;
import java.lang.ref.WeakReference;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    static ImageView imageView;
    @SuppressLint("StaticFieldLeak")
    static TextView tv_downloadProcess;
    Button btn_start;
    @SuppressLint("StaticFieldLeak")
    static ProgressBar progressBar;
    DownloadUtilWithOkHttp downloadUtilWithOkHttp;
    String cachePath;
    private static final int UPDATA_DOWNLOAD_PROCESS = 0;
    private static final int UPDATA_DOWNLOAD_PICTURE = 1;
    UiHandler mUiHandler = new UiHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image_view_main);
        tv_downloadProcess = findViewById(R.id.tv_download_process);
        progressBar = findViewById(R.id.progress_bar);
        btn_start = findViewById(R.id.btn_start_download);
        cachePath = this.getCacheDir().getPath();
        //Glide.with(this).load(cachePath + File.separator + "baifukaoya.jpg").into(imageView);

        downloadUtilWithOkHttp = DownloadUtilWithOkHttp.getDownloadUtil();
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadUtilWithOkHttp.download(ServerUrlConfig.PICTURE_SERVER_URL + "baifukaoya.jpg", "/data/data/com.example.gz494.douya/cache/", new OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        Log.d(TAG, " 下载成功");
                        Message message = new Message();
                        message.what = UPDATA_DOWNLOAD_PICTURE;
                        Bundle bundle = new Bundle();
                        bundle.putString("address", "/data/data/com.example.gz494.douya/cache/baifukaoya.jpg");
                        message.setData(bundle);
                        mUiHandler.sendMessage(message);
                    }

                    @Override
                    public void onDownloadingProcess(int process) {
                        Log.d(TAG, "Process : " + process);
                        Message message = new Message();
                        message.what = UPDATA_DOWNLOAD_PROCESS;
                        message.arg1 = process;
                        mUiHandler.sendMessage(message);
                    }

                    @Override
                    public void onDownloadFailed() {
                        Log.d(TAG, "下载失败");
                    }
                });
            }
        });

    }
    static class UiHandler extends android.os.Handler{
        WeakReference<MainActivity> mainActivityWeakReference;
        private UiHandler(MainActivity activity){
            mainActivityWeakReference = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATA_DOWNLOAD_PROCESS:
                    progressBar.setProgress(msg.arg1);
                    tv_downloadProcess.setText("下载进度：" + msg.arg1);
                    break;
                case UPDATA_DOWNLOAD_PICTURE:
                    imageView.setImageURI(Uri.fromFile(new File(msg.getData().getString("address"))));
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_app, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_1:
                Toast.makeText(this, "点击了菜单1", Toast.LENGTH_LONG).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}


