package com.example.gz494.douya.utils.okHttpUtils;

import android.util.Log;

import com.example.gz494.douya.listener.OnUploadResultListener;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import retrofit2.http.Url;


/**
 * Created by gz494 on 2018/9/2.
 */

public class FileUploadUtil {
    private static final String TAG = "FileUploadUtil";
    private static Object lock = new Object();
    private static volatile FileUploadUtil mFileUploadUtil = null;
    private final String BOUNDARY = "----WebKitFormBoundary4lg4euy1kMpXtPie";
    private final int DOWNLOAD_WITH_HTTPURLCONNECTION = 0;
    private final int DOWNLOAD_WITH_OKHTTP = 1;
    private final String END = "\r\n";
    private final String LAST = "--";

    private FileUploadUtil(){

    }

    public static FileUploadUtil getInstance(){
        if(mFileUploadUtil == null){
            synchronized (lock){
                if(mFileUploadUtil == null){
                    return new FileUploadUtil();
                }
            }
        }
        return mFileUploadUtil;
    }

    public void uploadFile(final int downloadWay, final Map<String, String> params, final String fileFormName, final File uploadFile, final String newFileName, final String url, final OnUploadResultListener uploadResultListener){
        if(!uploadFile.exists()){
            System.out.println("文件不存在！");
            return;
        }

        if(downloadWay == DOWNLOAD_WITH_HTTPURLCONNECTION){
            //HttpURLConnection, need run with thread because of network can't use in uiThread.
            Log.d(TAG, "uploadFile by HttpURLConnection");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    uploadFileWithHttpURLConnection(params, fileFormName, uploadFile, newFileName, url, uploadResultListener);
                }
            }).start();

        } else if(downloadWay == DOWNLOAD_WITH_OKHTTP){
            //Okhttp
            Log.d(TAG, "uploadFile by okHttp");
            uploadFileWithOkHttp(params, fileFormName, uploadFile, newFileName, url, uploadResultListener);
        }
    }


        /**
         *
         ------WebKitFormBoundary90QkFBcF68eTRSiS       1
         Content-Disposition: form-data; name="data"     2
                                                        3
         fafa                                           4
         ------WebKitFormBoundary90QkFBcF68eTRSiS       5
         Content-Disposition: form-data; name="username"  6
                                                        7
         fafa                                           8
         ------WebKitFormBoundary90QkFBcF68eTRSiS       9
         Content-Disposition: form-data; name="file"; filename=""       10
         Content-Type: application/octet-stream             11
                                                            12
                                                            13
         ------WebKitFormBoundary90QkFBcF68eTRSiS--          14
         * @param params 普通表单数据
         * @param fileFormName 表单文件名称
         * @param newFileName  文件名称，如果不设置，默认使用fileFormName
         * @param url  请求上传的URL地址
         * @param uploadFile 上传的文件
         */
    private void uploadFileWithHttpURLConnection(Map<String, String> params, String fileFormName, File uploadFile, String newFileName, String url, OnUploadResultListener uploadResultListener){

        try {


            StringBuffer sb = new StringBuffer();

            //普通表单数据（非文件），例如上述的data和name两项，取决于接收文件的jsp页面的表单内容，对应于1-8
            for(String key: params.keySet()){
                sb.append(LAST + BOUNDARY + END);//对应于1和5
                sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + END); //对应于2和6
                sb.append(END); //对应于3和7
                sb.append(params.get(key) + END); //对应于4和8
            }

            //文件头
            sb.append(LAST + BOUNDARY + END); //对应于9
            sb.append("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + newFileName + "\"" + END); //对应于10
            sb.append("Content-Type: image/jpeg" + END);//对应于11
            sb.append(END);//对应于12

            //接下来就是将上述内容和文件内容读写

            //上面所有的头信息
            byte[] headerInfo = sb.toString().getBytes("UTF-8");

            //尾信息，两者之间是文件byte
            byte[] endInfo = (END + LAST + BOUNDARY + LAST + END).getBytes("UTF-8");



            System.out.println(sb.toString());
            URL httpUrl = new URL(url);
            HttpURLConnection conn =(HttpURLConnection)httpUrl.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Length", String.valueOf(headerInfo.length + uploadFile.length() + endInfo.length));
            conn.setChunkedStreamingMode(0);//使用HttpURLConnection容易产生内存溢出，因为它有默认的缓存机制，在对文件操作时会将读取的数据写入到缓存区中，
            // 并不是直接写入到服务器上，只有当流被关闭时，才将数据提交到服务器上。当缓存区的数据大于虚拟机给点的内存时，就导致内存溢出
            // 设置这个选项后，直接将流提到服务器，就不会内存溢出了，还有一种方案可以选择来避免内存溢出---Socket方式
            OutputStream out = conn.getOutputStream();
            InputStream in = new FileInputStream(uploadFile);

            //首先把头信息写入
            out.write(headerInfo);

            //接着是写文件内容
            byte[] buf = new byte[1024];
            int len;
            int sum = 0;
            long totalLength = uploadFile.length();
            Log.d(TAG, "uploadFileWithHttpURLConnection: totalLength = " + totalLength);
            while((len = in.read(buf)) != -1){
                out.write(buf, 0, len);
                sum += len;
                int process = (int)(sum *1.0f/totalLength*100);
                uploadResultListener.uploadProcess(process);
            }

            //最后是结尾
            out.write(endInfo); // 对应于14
            in.close();
            out.close();

            if(conn.getResponseCode() == 200){
                System.out.println("上传成功");
                uploadResultListener.uploadSuccess();
            }
        } catch (IOException e) {
            System.out.println("上传失败");
            uploadResultListener.uploadFailed();
            e.printStackTrace();
        }
    }


    private void uploadFileWithOkHttp(Map<String, String> params, String fileFormName, final File uploadFile, String newFileName, String url, final OnUploadResultListener uploadResultListener){
        OkHttpClient mOkHttpClient = new OkHttpClient();
        RequestBody mRequestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("username", "gaozhan")
                .addFormDataPart(fileFormName, newFileName, RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile))
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(mRequestBody)
                .build();
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                uploadResultListener.uploadFailed();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(TAG, "onResponse: " + response.toString());
                if(response.code() == 200){
                    uploadResultListener.uploadSuccess();
                }
            }
        });
    }

}
