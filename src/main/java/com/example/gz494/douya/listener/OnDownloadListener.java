package com.example.gz494.douya.listener;

/**
 * Created by gz494 on 2018/3/26.
 */

public interface OnDownloadListener {
    /*下载成功*/
    void onDownloadSuccess();
    /*下载进度*/
    void onDownloadingProcess(int process);
    /*下载失败*/
    void onDownloadFailed();
}
