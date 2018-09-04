package com.example.gz494.douya.listener;

/**
 * Created by gz494 on 2018/9/3.
 */

public interface OnUploadResultListener {
    void uploadSuccess();
    void uploadProcess(int process);
    void uploadFailed();
}
