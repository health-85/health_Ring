package com.healthy.rvigor.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.ClientException;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.ServiceException;
import com.alibaba.sdk.android.oss.callback.OSSCompletedCallback;
import com.alibaba.sdk.android.oss.callback.OSSProgressCallback;
import com.alibaba.sdk.android.oss.common.OSSLog;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.alibaba.sdk.android.oss.internal.OSSAsyncTask;
import com.alibaba.sdk.android.oss.model.DeleteObjectRequest;
import com.alibaba.sdk.android.oss.model.DeleteObjectResult;
import com.alibaba.sdk.android.oss.model.PutObjectRequest;
import com.alibaba.sdk.android.oss.model.PutObjectResult;
import com.healthbit.framework.util.FileUtil;
import com.healthbit.framework.util.ToastUtil;
import com.healthy.rvigor.BuildConfig;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.bean.Aliyun;
import com.healthy.rvigor.net.BaseResponse;
import com.healthy.rvigor.net.BeanObserver;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * @Description: Rvigor
 * @Author: wb
 * @CreateDate: 2024/5/6 22:22
 * @UpdateRemark:
 */
public class OssUtil {

    private static final String TAG = "=====OssUtil===>>>";

    //区域地址
    public static final String ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";
    //    public static final String ENDPOINT = "http://oss-cn-shenzhen.aliyuncs.com";
    //删除文件地址
    public static final String UHEALTHTIME_DELETE_PATH = "https://img.uhealthtime.com/";

    //总目录
    private static final String BUCKET_NAME = "meridian-file";

    /**
     * 上传目录
     */
    private static final String PROD_PATH = "prod/app"; //生产目录  正式服
    private static final String DEV_PATH = "dev/app";  //开发目录   本地
    private static final String TEST_PATH = "test/app"; //测试目录  测试服

    private Context mContext;
    private OSS mOssClient; //Oss客户端
    private static OssUtil instance; //OssUtil

    /**
     * 上传成功的objectKey，提交给接口
     */
    private List<String> mSuccessPath = new ArrayList<>();
    /**
     * 上传成功的本地路径
     */
    private List<String> mSuccessLocalPath = new ArrayList<>();

    private volatile int mNum;

    private OssUtil(Context context) {
        mContext = context;
    }

    public static OssUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (OssUtil.class) {
                if (instance == null) {
                    instance = new OssUtil(context);
                }
            }
        }
        return instance;
    }

    /**
     * Oss上传结果接口
     */
    public interface OSSUploadCallback {

        //list上传成功
        void onSuccess(List<String> allPath, List<String> allLocalPath);

        //上传失败
        void onFailure(ClientException clientException, ServiceException serviceException);

        //上传进度
        void onProgress(int progress);
    }

    /**
     * 上传单个文件   (图片,视频,gif,.....)
     *
     * @param path     本地地址
     * @param callback 成功回调
     */
    public void uploadFile(String uuid, String path, OSSUploadCallback callback) {
        List<String> strings = new ArrayList<>();
        strings.add(path);
        getAliyunKey(uuid, strings, callback);
    }

    /**
     * 上传多文件   (图片,视频,gif,.....)
     *
     * @param paths    本地地址
     * @param callback 成功回调
     */
    public void uploadFiles(String uuid, List<String> paths, OSSUploadCallback callback) {
        getAliyunKey(uuid, paths, callback);
    }

    /**
     * 阿里获取临时签名
     */
    private void getAliyunKey(String uuid, List<String> paths, OSSUploadCallback callback) {
        MyApplication.Companion.instance().getService().getAliyunKey()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BeanObserver<Aliyun>(){
                    @Override
                    public void onSuccess(BaseResponse<Aliyun> bean) {
                        if (bean == null || bean.getData() == null) return;
                        Aliyun.CredentialsDTO credentialsDTO = bean.getData().getCredentials();
                        mOssClient = getOSSClient(credentialsDTO.getAccessKeyId(), credentialsDTO.getAccessKeySecret(), credentialsDTO.getSecurityToken());
                        uploads(paths, callback);
                    }

                    @Override
                    protected void onFailure(Throwable e, String rawResponse) {
                        ToastUtil.showToast(MyApplication.Companion.instance().getApplicationContext(), "上传阿里失败");
                    }
                });
    }

    /**
     * @return 配置Oos
     */
    //OSS的配置,建议只实例化一次，即不要放在循环体中。
    public OSS getOSSClient(String accessKeyId, String accessKeySecret, String securityToken) {
        if (BuildConfig.DEBUG) {
            OSSLog.enableLog();
        }
        // 推荐使用
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider(accessKeyId, accessKeySecret, securityToken);
        // 配置类如果不设置，会有默认配置。
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        // oss为全局变量，endpoint是一个OSS区域地址
        return new OSSClient(mContext, ENDPOINT, credentialProvider, conf);
    }

    /**
     * 多图片上传方法
     *
     * @param paths 需上传文件的路径
     */
    private void uploads(final List<String> paths, OSSUploadCallback callback) {
        if (mOssClient == null) {
            return;
        }
        mNum = 0;
        mSuccessPath.clear();
        mSuccessLocalPath.clear();
        for (String path : paths) {
            // 构造上传请求
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, getUploadFilePath(path), path);
            // 设置文件元信息为可选操作。
//            ObjectMetadata metadata = new ObjectMetadata();
//            metadata.setContentType("application/octet-stream"); // 设置content-type。
//            metadata.setContentMD5(BinaryUtil.calculateBase64Md5(getUploadFilePath(path))); // 校验MD5。
//            //设置object的访问权限为私有
//            metadata.setHeader("x-oss-object-acl", "private");
//            //设置object的归档类型为标准存储
//            metadata.setHeader("x-oss-storage-class", "Standard");
//            //设置覆盖同名目标Object
//            metadata.setHeader("x-oss-forbid-overwrite", "true");
//            //指定Object的对象标签，可同时设置多个标签。
//            metadata.setHeader("x-oss-tagging", "a:1");
//            //指定OSS创建目标Object时使用的服务器端加密算法 。
//            metadata.setHeader("x-oss-server-side-encryption", "AES256");
//            //表示KMS托管的用户主密钥，该参数仅在x - oss - server - side - encryption为KMS时有效。
//            metadata.setHeader("x-oss-server-side-encryption-key-id", "9468da86-3509-4f8d-a61e-6eab1eac****");
//            request.setMetadata(metadata);
            // 异步上传可回调上传进度。
            request.setProgressCallback(new OSSProgressCallback<PutObjectRequest>() {
                @Override
                public void onProgress(PutObjectRequest request, long currentSize, long totalSize) {
                    LogUtils.d("PutObject" + "currentSize: " + currentSize + " totalSize: " + totalSize);
                    int progress = (int) (100 * currentSize / totalSize);
                    LogUtils.d("上传进度=" + progress);
//                    number++;
//                    //上传成功
//                    successPath.add(getAllPath(request.getObjectKey()));
//                    if (number == paths.size() && callback != null) {
//                        callback.onSuccess(successPath);
//                    }
                }
            });

            //同步上传回调
            OSSAsyncTask ossAsyncTask = mOssClient.asyncPutObject(request, new OSSCompletedCallback<PutObjectRequest, PutObjectResult>() {
                @Override
                public void onSuccess(PutObjectRequest request, PutObjectResult result) {
                    Log.d(TAG, request.getUploadFilePath() + "===上传成功== ：");
                    Log.d(TAG, " mSuccessPath " + mSuccessPath + " mSuccessLocalPath " + mSuccessLocalPath);
                    mNum++;
                    //上传成功
                    mSuccessPath.add(request.getObjectKey());
                    mSuccessLocalPath.add(request.getUploadFilePath());
                    if (mSuccessPath.size() >= paths.size() && callback != null) {
                        callback.onSuccess(mSuccessPath, mSuccessLocalPath);
                    }
                }

                @Override
                public void onFailure(PutObjectRequest request, ClientException clientException, ServiceException serviceException) {
                    //上传失败
                    if (callback != null) {
                        callback.onFailure(clientException, serviceException);
                    }

                    if (clientException != null) {
                        // 本地异常如网络异常等
                        Log.e(TAG, "UploadFailure：表示向OSS发送请求或解析来自OSS的响应时发生错误。\n" +
                                "  *例如，当网络不可用时，这个异常将被抛出");
                        Log.e(TAG, "ErrorCode ：" + clientException.getMessage());
                        Log.e(TAG, "==========================================");
                        clientException.printStackTrace();
                    }

                    if (serviceException != null) {
                        // 服务异常
                        Log.e(TAG, "===UploadFailure：表示在OSS服务端发生错误====");
                        Log.e(TAG, "ErrorCode ：" + serviceException.getErrorCode());
                        Log.e(TAG, "RequestId ：" + serviceException.getRequestId());
                        Log.e(TAG, "HostId ：" + serviceException.getHostId());
                        Log.e(TAG, "RawMessage ：" + serviceException.getRawMessage());
                        Log.e(TAG, "==========================================");
                        if (serviceException.getErrorCode().equals("InvalidAccessKeyId")) {
                            //token过期
                            Log.e(TAG, "token过期");
                            Log.e(TAG, "==========================================");
//                            getNetOssStsToken();
                        }
                    }
                }
            });
            //ossAsyncTask.cancel(); // 可以取消任务
            //ossAsyncTask.waitUntilFinished(); // 可以等待直到任务完成
        }
    }

    /**
     * 解析头像地址
     *
     * @param path
     */
    public void deleteHttpPath(String path) {
//        if (TextUtils.isEmpty(path)) return;
//        if (path.startsWith("https://") || path.startsWith("http://")) {
//            try {
//                if (path.contains("\\?Expires")) {
//                    String[] pathArray = path.split("\\?Expires");
//                    if (pathArray == null || pathArray.length == 0) return;
//                    String devPath = pathArray[0].replaceAll(UHEALTHTIME_DELETE_PATH, "");
//                    deleteFile(devPath);
////                    CommonClass.Log.LogUtils.i(" deleteHttpPath pathArray " + pathArray[0] + " " + pathArray[1]);
//                    CommonClass.Log.LogUtils.i(" deleteHttpPath devPath " + devPath);
//                } else if (path.contains(UHEALTHTIME_DELETE_PATH)) {
//                    String devPath = path.replace(UHEALTHTIME_DELETE_PATH, "");
//                    deleteFile(devPath);
//                    CommonClass.Log.LogUtils.i(" deleteHttpPath devPath " + devPath);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }

    //删除文件
    public void deleteFile(String path) {
//        CommonClass.Log.LogUtils.i(" deleteFile path " + path);
//        if (TextUtils.isEmpty(path)) return;
//        if (path.contains("5b171071b6a2e9fba71f0f30055ad01")) return;
//        if (path.contains("e0798a439dc8fff3e33323f8d48c318")) return;
//        NetTool.getApi().getAliyunKey().subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<BaseResponse<Aliyun>>("", "") {
//                    @Override
//                    public void onData(BaseResponse<Aliyun> value) {
//                        if (value == null || value.getData() == null) return;
//                        Aliyun.CredentialsDTO credentialsDTO = value.getData().getCredentials();
//                        mOssClient = getOSSClient(credentialsDTO.getAccessKeyId(), credentialsDTO.getAccessKeySecret(), credentialsDTO.getSecurityToken());
//                        deleteFile(mOssClient, path);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        super.onError(e);
//                        ToastUtil.showToast(MainApplication.getInstance().getApplicationContext(), "删除图片失败", Toast.LENGTH_SHORT);
//                    }
//                });
    }

    private void deleteFile(OSS oos, String path) {
        if (oos == null) return;
        // 创建删除请求。
        // 依次填写Bucket名称（例如examplebucket）和Object完整路径（例如exampledir/exampleobject.txt）。Object完整路径中不能包含Bucket名称。
        try {
            DeleteObjectRequest delete = new DeleteObjectRequest(BUCKET_NAME, path);
            // 异步删除。
            OSSAsyncTask deleteTask = mOssClient.asyncDeleteObject(delete, new OSSCompletedCallback<DeleteObjectRequest, DeleteObjectResult>() {
                @Override
                public void onSuccess(DeleteObjectRequest request, DeleteObjectResult result) {
                    Log.d("asyncDeleteObject", "success!");
                }

                @Override
                public void onFailure(DeleteObjectRequest request, ClientException clientExcepion, ServiceException serviceException) {
                    // 请求异常。
                    if (clientExcepion != null) {
                        // 客户端异常，例如网络异常等。
                        clientExcepion.printStackTrace();
                    }
                    if (serviceException != null) {
                        // 服务端异常。
                        Log.e("ErrorCode", serviceException.getErrorCode());
                        Log.e("RequestId", serviceException.getRequestId());
                        Log.e("HostId", serviceException.getHostId());
                        Log.e("RawMessage", serviceException.getRawMessage());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件目录名称
     * 格式: uploadFilePath/sfdsgfsdvsdfdsfs.jpg
     *
     * @param path
     * @return
     */
    private String getUploadFilePath(String path) {
        String uploadPath;
//        String currentTime = DateTimeUtils.s_long_2_str(System.currentTimeMillis(), DateTimeUtils.day_format);
        if (BuildConfig.DEBUG) {
            uploadPath = String.format(TEST_PATH + "/" + "%s_" + FileUtil.getFolderName(path), System.currentTimeMillis() + "");
        }
        if (TextUtils.equals(Context.BATTERY_SERVICE, "https://app.uhealthtime.com/")) {
            uploadPath = String.format(PROD_PATH + "/" + "%s_" + FileUtil.getFolderName(path), System.currentTimeMillis() + "");
        } else {
            uploadPath = String.format(DEV_PATH + "/" + "%s_" + FileUtil.getFolderName(path), System.currentTimeMillis() + "");
        }
//        uploadPath = PROD_PATH + "/" + FileUtil.getFileName(path);
//        CommonClass.Log.LogUtils.i(" FilePath " + path + " FileName " + FileUtil.getFileName(path) + " uploadPath " + uploadPath);
        return uploadPath;
    }

}
