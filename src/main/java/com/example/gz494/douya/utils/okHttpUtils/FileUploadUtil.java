package com.example.gz494.douya.utils.okHttpUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import retrofit2.http.Url;

/**
 * Created by gz494 on 2018/9/2.
 */

public class FileUploadUtil {
    private static Object lock = new Object();
    private static volatile FileUploadUtil mFileUploadUtil = null;
    private final String BOUNDARY = "----WebKitFormBoundary4lg4euy1kMpXtPie";

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
    public boolean uploadFile(Map<String, String> params, String fileFormName, File uploadFile, String newFileName, String url){

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
            while((len = in.read(buf)) != -1){
                out.write(buf, 0, len);
            }

            //最后是结尾
            out.write(endInfo); // 对应于14
            in.close();
            out.close();

            if(conn.getResponseCode() == 200){
                System.out.println("上传成功");
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
