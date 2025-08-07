package com.healthy.rvigor.watch;


/**
 * app 升级单元
 */
public class AppSoftUpdateUtils {

//    private static final String TAG = "AppSoftUpdateUtils";
//
//    /**
//     *
//     */
//    private MyApplication application = null;
//
//    private AppVersion mAppVersion;
//
//    /**
//     * app更新回调
//     */
//    private IAppUpdateCallBack appUpdateCallBack = new IAppUpdateCallBack() {
//
//        @Override
//        public void onNewVersion(boolean hasNewVersion, String versionName) {
//
//        }
//
//        @Override
//        public void DownLoadComplate(String path) {
//            appDownloadState = "安装包下载完成";
//            if (isHasInstallPermissionWithO(application)) {
//                installApk(path);
//            } else {
//                apkpath = path;
//                application.showToast("app没有安装权限 请打开app安装权限");
//                startInstallPermissionSettingActivity();//打开系统安装权限设置对话框
//            }
//        }
//
//        @Override
//        public void DownLoadProgress(long contentLengh, long downloadlentgh, int percent) {
//            appDownloadState = "安装包下载进度" + percent + "%";
//            remoteViews.setProgressBar(R.id.n_progressbar, 100, percent, false);
//            notificationUtils.sendNotification(downloadnotifyInfo);
//        }
//
//        @Override
//        public void DownLoadError(Exception ex) {
//            appDownloadState = "安装包下载失败";
//        }
//
//    };
//
//
//    /**
//     * app下载状态
//     */
//    public String appDownloadState = "";
//
//    /**
//     * apk安装路径
//     */
//    private String apkpath = "";
//
//
//    private final static int REQUEST_CODE_APP_INSTALL = 2356;
//
//    private NotificationItemInfoBase downloadnotifyInfo = null;
//
//    /**
//     * 打开系统安装app权限对话框
//     */
//    private void startInstallPermissionSettingActivity() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//android 8以后
//            Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
//
//            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//            BaseActivity baseActivity = getTopBaseActivity(application);
//            if (baseActivity != null) {
//                baseActivity.startActivityForResult(intent, REQUEST_CODE_APP_INSTALL);
//            }
//        } else {//8以下什么都不做
//
//        }
//    }
//
//    /**
//     * 请求安装权限不含回调
//     */
//    public void startInstallPermissionSettingActivityNoResult() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//android 8以后
//            Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
//            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//            BaseActivity baseActivity = getTopBaseActivity(application);
//            if (baseActivity != null) {
//                baseActivity.startActivity(intent);
//            }
//        } else {//8以下什么都不做
//
//        }
//    }
//
//    /**
//     * activity回调
//     *
//     * @param activity
//     * @param requestCode
//     * @param resultCode
//     * @param data
//     */
//    public void onActivityResult(BaseActivity activity, int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == REQUEST_CODE_APP_INSTALL) {//如果是安装权限回调
//            if (isHasInstallPermissionWithO(application)) {
//                installApk(apkpath);
//            }
//        }
//    }
//
//    private NotificationUtils notificationUtils = null;
//
//    private RemoteViews remoteViews = null;
//
//    private String mDownloadUrl;
//
//    /**
//     * @param application
//     */
//    public AppSoftUpdateUtils( MyApplication application) {
//        this.application = application;
//        notificationUtils = new NotificationUtils(application);
//        downloadnotifyInfo = new NotificationItemInfoBase(123, R.drawable.icon_app,  MyApplication.getInstance().getResources().getString(R.string.download), "");
//        downloadnotifyInfo.isUserOldNotify = true;
//        remoteViews = new RemoteViews(application.getPackageName(), R.layout.notification_item_layout2);
//        remoteViews.setTextViewText(R.id.n_caption, "下载更新");
//        downloadnotifyInfo.setNotifycallback(new NotificationItemInfoBase.INotificationCallBack() {
//            @Override
//            public void onclick(NotificationItemInfoBase sender, CommonApplication comm) {
//
//            }
//
//            @Override
//            public void oncancel(NotificationItemInfoBase sender, CommonApplication comm) {
//
//            }
//
//            @Override
//            public RemoteViews getNotificationContentView() {
//                return remoteViews;
//            }
//        });
//        updateAppPermissions.clear();
//        updateAppPermissions.add(new PermissionUtils.PemissionItem(Manifest.permission.WRITE_EXTERNAL_STORAGE, "写存储器"));
//        updateAppPermissions.add(new PermissionUtils.PemissionItem(Manifest.permission.READ_EXTERNAL_STORAGE, "读存储器"));
//        this.AddUpdateCallBack(appUpdateCallBack);
//    }
//
//    /**
//     * app更新回调
//     */
//    public static interface IAppUpdateCallBack {
//        /**
//         * 是否有新版本
//         *
//         * @param hasNewVersion
//         * @param versionName
//         */
//        public void onNewVersion(boolean hasNewVersion, String versionName);
//
//        /**
//         * apk文件下载成功
//         *
//         * @param path
//         */
//        public void DownLoadComplate(String path);
//
//        /**
//         * apk下载进度
//         *
//         * @param contentLengh
//         * @param downloadlentgh
//         */
//        public void DownLoadProgress(long contentLengh, long downloadlentgh, int percent);
//
//        /**
//         * 下载出错
//         *
//         * @param ex
//         */
//        public void DownLoadError(Exception ex);
//
//    }
//
//    /**
//     * 是否拥有安装权限
//     *
//     * @param context
//     * @return
//     */
//    public boolean isHasInstallPermissionWithO(Context context) {
//        if (context == null) {
//            return false;
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            return context.getPackageManager().canRequestPackageInstalls();
//        } else {
//            return true;
//        }
//    }
//
//
//    /**
//     * 安装apk
//     *
//     * @param path
//     */
//    public void installApk(String path) {
//        File apk = new File(path);
//        if (apk.exists()) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                Uri uri = FileProvider.getUriForFile(application, application.getPackageName() + ".fileprovider", apk);
//                intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            } else {
//                intent.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
//            }
//            try {
//                application.startActivity(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            if (application.IsUIThread()) {
//                application.showToast("apk文件不存在");
//            }
//        }
//    }
//
//    /**
//     * 是否有新版本
//     */
//    private boolean hasNewVersion = false;
//
//    /**
//     * 注册回调
//     *
//     * @param callBack
//     */
//    public void AddUpdateCallBack(IAppUpdateCallBack callBack) {
//        if (callBack == null) {
//            return;
//        }
//        validateUIThread();
//        if (!appUpdateCallBacks.contains(callBack)) {
//            appUpdateCallBacks.add(callBack);
//        }
//    }
//
//    /**
//     * 移除回调
//     *
//     * @param callBack
//     */
//    public void removeCallBack(IAppUpdateCallBack callBack) {
//        validateUIThread();
//        appUpdateCallBacks.remove(callBack);
//    }
//
//
//    /**
//     * 通知新版本
//     *
//     * @param hasNewVersion
//     */
//    private void performNewVersionChange(boolean hasNewVersion, String versionName) {
//        validateUIThread();
//        for (int i = 0; i < appUpdateCallBacks.size(); i++) {
//            appUpdateCallBacks.get(i).onNewVersion(hasNewVersion, versionName);
//        }
//    }
//
//    /**
//     * 通知下载完成
//     *
//     * @param path
//     */
//    private void performDownLoadComplate(String path) {
//        validateUIThread();
//        for (int i = 0; i < appUpdateCallBacks.size(); i++) {
//            appUpdateCallBacks.get(i).DownLoadComplate(path);
//        }
//    }
//
//    /**
//     * 下载进度
//     *
//     * @param contentLengh
//     * @param downloadlentgh
//     */
//    private void performDownLoadProgress(long contentLengh, long downloadlentgh) {
//        validateUIThread();
//        int percent = (int) (((double) downloadlentgh / (double) contentLengh) * 100);
//        if (percent > 100) {
//            percent = 100;
//        }
//        for (int i = 0; i < appUpdateCallBacks.size(); i++) {
//            appUpdateCallBacks.get(i).DownLoadProgress(contentLengh, downloadlentgh, percent);
//        }
//    }
//
//    /**
//     * 下载出错
//     *
//     * @param ex
//     */
//    private void performDownLoadError(Exception ex) {
//        validateUIThread();
//        for (int i = 0; i < appUpdateCallBacks.size(); i++) {
//            appUpdateCallBacks.get(i).DownLoadError(ex);
//        }
//    }
//
//    /**
//     * 回调集合
//     */
//    private final List<IAppUpdateCallBack> appUpdateCallBacks = new ArrayList<>();
//
//    /**
//     * 是否有新版本
//     *
//     * @return
//     */
//    public boolean isHasNewVersion() {
//        return hasNewVersion;
//    }
//
//    /**
//     * 检测是否有新版本
//     *
//     * @param uuid
//     * @param pid
//     * @param showerror
//     */
//    public void checkedAppNewVersion(String uuid, String pid, boolean showerror) {
//        validateUIThread();
//        long uid = 0;
//        if (AppUserInfo.getInstance().isLogin()) {
//            uid = AppUserInfo.getInstance().userInfo.id;
//        }
//        if (!isAppUpdating()) {
//            if (NetTool.isNewNet) {
//                String token = (String) SPUtil.getData( MyApplication.getInstance(), SpConfig.TOKEN, "");
//                ApiHelper
//                        .get(NetTool.base_url + "system-service/front/app/selectData")
//                        .putHeader("Authorization", token)
//                        .setUUID(uuid)
//                        .putParam("consumerId", uid + "")
//                        .setTag("checkAppNewVersion")
//                        .setProgressUUID(pid)
//                        .requestCallBack(new checkedAppNewVersionCallBack(null, showerror))
//                        .execute( MyApplication.getInstance().getDeviceUpdateHttpComponet());
//            } else {
//                ApiHelper
//                        .get(NetTool.base_url + "front/app/selectData")
//                        .setUUID(uuid)
//                        .putParam("consumerId", uid + "")
//                        .setTag("checkAppNewVersion")
//                        .setProgressUUID(pid)
//                        .requestCallBack(new checkedAppNewVersionCallBack(null, showerror))
//                        .execute( MyApplication.getInstance().getDeviceUpdateHttpComponet());
//            }
//        } else {//留着稍后处理
//            IActivityManagerCallBase ui = application.getActivityManagerCallBaseByUUID(uuid);
//            if (ui != null) {
//                ui.CloseProgressByUUID(pid);
//            }
//        }
//    }
//
//    /**
//     * 是否App正在升级
//     *
//     * @return
//     */
//    public boolean isAppUpdating() {
//        boolean R =  MyApplication.getInstance().getDeviceUpdateHttpComponet().containsRequestByTag("checkAppNewVersion");
//        R = (R || isDownLoadApkFile());
//        return R;
//    }
//
//
//    /**
//     * App新版本检测回调
//     */
//    private static class checkedAppNewVersionCallBack extends ProjectJsonRequestCallBack {
//
//        public checkedAppNewVersionCallBack(UIDataBinderBase<JSONObject> dataBinder, boolean showErrorToast) {
//            super(dataBinder, showErrorToast);
//        }
//
//        public checkedAppNewVersionCallBack(UIDataBinderBase<JSONObject> dataBinder, String onSuccessInUIInvokeMethodName, String onErrorInUIInvokeMethodName, String onAfterInUIInvokeMethodName, boolean showErrorToast) {
//            super(dataBinder, onSuccessInUIInvokeMethodName, onErrorInUIInvokeMethodName, onAfterInUIInvokeMethodName, showErrorToast);
//        }
//
//        @Override
//        public void onSuccessInUI(CommonApplication con, HttpRequestBase req, JSONObject object) {
//            super.onSuccessInUI(con, req, object);
//            JsonResult jsonResult = new JsonResult(object);
//            if (jsonResult.Succeed()) {
//                JsonUtils data = jsonResult.getData();
//                if (data != null) {
//                    AppSoftUpdateUtils appSoftUpdateUtils =  MyApplication.getInstance().getAppSoftUpdateUtils();
//                    int version = data.getInt("version", -1);
//                    int min_version = NumberUtils.convertStringToInt(data.getString("minVersion"), -1);
//                    String content = data.getString("content");
//                    String uploadFile = data.getString("uploadFile");
//                    LogUtils.i(TAG, new Gson().toJson(data));
//                    if (version > APKVersionInfoUtils.getVersionCode(con) || APKVersionInfoUtils.getVersionCode(con) < min_version) {//有新版本
//                        appSoftUpdateUtils.newVersionCode = version;
//                        appSoftUpdateUtils.hasNewVersion = true;
//                        appSoftUpdateUtils.mDownloadUrl = uploadFile;
//                        BaseActivity baseActivity = appSoftUpdateUtils.getTopBaseActivity(con);
//                        if (baseActivity != null) {
//                            baseActivity.appupdatetisp.showUpdateDialog(TextUtils.isEmpty(content) ?  MyApplication.getInstance().getResources().getString(R.string.has_new_version_updated) : content
//                                    ,  MyApplication.getInstance().getResources().getString(R.string.deny), MyApplication.getInstance().getResources().getString(R.string.correct), new ConfirmBtnClick(baseActivity.getUUID(), uploadFile));
//                            baseActivity.appupdatetisp.setCanceledOnTouchOutside(APKVersionInfoUtils.getVersionCode(con) >= min_version);
//                        }
//                    } else {
//                        appSoftUpdateUtils.hasNewVersion = false;
//                    }
////                    Log.i("version", version + " " + jsonResult.tojson());
//                    appSoftUpdateUtils.performNewVersionChange(appSoftUpdateUtils.hasNewVersion, "");
//                } else {
//                    if (showErrorToast) {
//                        showToast(con, "无法获取版本信息");
//                    }
//                }
//            }
//        }
//
//
//        /**
//         * 对话框点击回调
//         */
//        private static class ConfirmBtnClick implements ConfirmTipsView.OnBtnClickListener {
//            private String uuid = "";
//            private String downloadUrl;
//
//            public ConfirmBtnClick(String uuid) {
//                this.uuid = uuid;
//            }
//
//            public ConfirmBtnClick(String uuid, String downloadUrl) {
//                this.uuid = uuid;
//                this.downloadUrl = downloadUrl;
//            }
//
//            private void dismiss() {
//                IActivityManagerCallBase ui =  MyApplication.getInstance().getActivityManagerCallBaseByUUID(uuid);
//                if (ui != null) {
//                    if (ui instanceof BaseActivity) {
//                        ((BaseActivity) ui).appupdatetisp.dismiss();
//                    }
//                }
//            }
//
//            @Override
//            public void onClick(boolean isConfirm) {
////                IActivityManagerCallBase ui =  MyApplication.getInstance().getActivityManagerCallBaseByUUID(uuid);
//                if (isConfirm) {//如果有更新
////                    if (ui != null && ui instanceof BaseActivity) {
//                    if (! MyApplication.getInstance().getAppSoftUpdateUtils().isHasInstallPermissionWithO( MyApplication.getInstance())) {//如果没有安装权限
//                        ConfirmTipsView installtisp = new ConfirmTipsView( MyApplication.getInstance());
//                        installtisp.showDialog( MyApplication.getInstance().getResources().getString(R.string.have_installation_permission),  MyApplication.getInstance().getResources().getString(R.string.deny)
//                                ,  MyApplication.getInstance().getResources().getString(R.string.correct), new ConfirmTipsView.OnBtnClickListener() {
//                                    @Override
//                                    public void onClick(boolean isConfirm) {
//                                        installtisp.dismiss();
//                                        if (isConfirm) {
//                                             MyApplication.getInstance().getAppSoftUpdateUtils().startInstallPermissionSettingActivityNoResult();
//                                        }
//                                    }
//                                });
//                    } else {
//                        dismiss();
//                         MyApplication.getInstance().getAppSoftUpdateUtils().newversionOnOk(null, downloadUrl); //开始更新
//                    }
////                    } else {
////                        dismiss();
////                         MyApplication.getInstance().getAppSoftUpdateUtils().newversionOnOk(downloadUrl); //开始更新
////                    }
//                } else {
//                    dismiss();
//                }
//            }
//        }
//    }
//
//    private final ArrayList<PermissionUtils.PemissionItem> updateAppPermissions = new ArrayList<>();
//
//
//    private final int Update_App_Permissions_Request_Code = 1236;
//
//    /**
//     * 新版本号
//     */
//    private int newVersionCode = 0;
//
//    /**
//     * 版本名称
//     */
//    private String versionName = "1.0.0";
//
//    /**
//     * 权限请求
//     *
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        if (requestCode == Update_App_Permissions_Request_Code) {
//            PermissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults, updateAppPermissions, updateAppPermissions_callback);
//        }
//    }
//
//    private PermissionUtils.IRequestPermissionsResultCallBack updateAppPermissions_callback = new PermissionUtils.IRequestPermissionsResultCallBack() {
//        @Override
//        public void onGrantedPermission(int requestCode, ArrayList<PermissionUtils.PemissionItem> permissions) {
//            if (PermissionUtils.isEquasAllPermission(updateAppPermissions, permissions)) {
//                startNewVersionRequest(mDownloadUrl);
//            }
//        }
//
//        @Override
//        public void onDeniedPermission(int requestCode, ArrayList<PermissionUtils.PemissionItem> permissions) {
//            if (permissions.size() > 0) {
//                application.showToast("您必须开启相关权限");
//            }
//        }
//    };
//
//    /**
//     * 开始更新
//     */
//    private void startNewVersionRequest(String url) {
//        if (hasNewVersion) {
//            LogUtils.i(" startNewVersionRequest " + url);
//            if (!hasApkFile(versionName + "")) {//是否存在新版本apk文件
//                if (NetTool.isNewNet) {
//                    DownloadApk(/*NetTool.base_url + "system-service/front/app/downloadFile"*/url, versionName + "");
//                } else {
//                    DownloadApk(NetTool.base_url + "front/app/downloadFile", versionName + "");
//                }
//            } else {
//                String path = getApkPath(versionName + "");
//                performDownLoadComplate(path);
//            }
//        }
//    }
//
//    /**
//     * 下载更新文件
//     *
//     * @param apkurl
//     * @param newversionnumber
//     */
//    private void DownloadApk(String apkurl, String newversionnumber) {
////        if (!isDownLoadApkFile()) {
//            ApiHelper.get(apkurl)
//                    .setTag("downloadAppUpdateApkFile")
//                    .requestCallBack(new ApkDownloadCallback(null, newversionnumber))
//                    .execute(application.getDeviceUpdateHttpComponet());
//            if (application.IsUIThread()) {
//                application.showToast( MyApplication.getInstance().getResources().getString(R.string.downloading_tip));
//            }
////        } else {
////            if (application.IsUIThread()) {
////                application.showToast("正在下载更新中");
////            }
////        }
//    }
//
//
//    /**
//     *
//     */
//    private static class ApkDownloadCallback extends HttpRequestCallBackBase {
//
//        private String newversionnum = "";
//
//        public ApkDownloadCallback(UIDataBinderBase dataBinder, String newversionnum) {
//            super(dataBinder);
//            this.newversionnum = newversionnum;
//        }
//
//        @Override
//        public Object convertSuccess(CommonApplication con, HttpRequestBase req, long contentlength, InputStream input) throws Exception {
//            String path = CommonFuc.GetBitmapPath(con, "update", newversionnum + ".temp");
//            FileOutputStream fileOutputStream = null;
//            long oldm = System.currentTimeMillis();
//            long length = 0;
//            con.getUIHandler().PostAndWait(new RunInUI(contentlength, length, ( MyApplication) con));
//            try {
//                File file = new File(path);
//                if (file.exists()) {
//                    file.delete();
//                }
//                file.createNewFile();
//                fileOutputStream = new FileOutputStream(file);
//                byte[] buff = new byte[1024 * 10];
//                int rd = -1;
//                while (true) {
//                    rd = input.read(buff);
//                    if (rd > 0) {
//                        length += rd;
//                        fileOutputStream.write(buff, 0, rd);
//                    }
//                    Thread.sleep(1);
//                    if (rd < 0) {
//                        break;
//                    }
//                    if ((System.currentTimeMillis() - oldm) >= 1000) {
//                        oldm = System.currentTimeMillis();
//                        con.getUIHandler().PostAndWait(new RunInUI(contentlength, length, ( MyApplication) con));
//                    }
//                }
//                fileOutputStream.close();
//                con.getUIHandler().PostAndWait(new RunInUI(contentlength, length, ( MyApplication) con));
//                if (length < contentlength) {
//                    throw new Exception("下载不完整");
//                }
//                return file.getPath();
//            } catch (Exception e) {
//                if (fileOutputStream != null) {
//                    fileOutputStream.close();
//                }
//                e.printStackTrace();
//                throw new Exception("更新下载失败");
//            }
//        }
//
//
//        /**
//         * 在ui中运行
//         */
//        private static class RunInUI implements Runnable {
//
//            /**
//             * 总长度
//             */
//            public long contentLengh = 0;
//            /**
//             * 下载的长度
//             */
//            public long downloadlentgh = 0;
//
//
//            private  MyApplication app = null;
//
//            public RunInUI(long contentLengh, long downloadlentgh,  MyApplication app) {
//                this.contentLengh = contentLengh;
//                this.downloadlentgh = downloadlentgh;
//                this.app = app;
//            }
//
//            @Override
//            public void run() {
//                if (contentLengh > 0) {
//                    app.getAppSoftUpdateUtils().performDownLoadProgress(contentLengh
//                            , downloadlentgh);
//                }
//                app = null;
//            }
//        }
//
//
//        @Override
//        public void onSuccessInUI(CommonApplication con, HttpRequestBase req, Object object) {
//            super.onSuccessInUI(con, req, object);
//            if (object != null) {
//                if (!StringUtils.StringIsEmptyOrNull(object.toString())) {
//                    try {
//                        File file = new File(object.toString());
//                        String path = CommonFuc.GetBitmapPath(con, "update", newversionnum + ".apk");
//                        File apkfile = new File(path);
//                        if (apkfile.exists()) {
//                            apkfile.delete();
//                        }
//                        file.renameTo(apkfile);
//                        if (con instanceof  MyApplication) {
//                            (( MyApplication) con).getAppSoftUpdateUtils().performDownLoadComplate(path);
//                        }
//                    } catch (Exception ex) {
//                        showToast(con, "安装失败");
//                        (( MyApplication) con).getAppSoftUpdateUtils().performDownLoadError(ex);
//                    }
//                }
//            }
//        }
//
//        @Override
//        public void onErrorInUI(CommonApplication con, HttpRequestBase req, Exception ex) {
//            super.onErrorInUI(con, req, ex);
//            con.showToast("下载更新错误：" + ex.getMessage());
//            (( MyApplication) con).getAppSoftUpdateUtils().performDownLoadError(ex);
//        }
//
//        @Override
//        public void onAfterInUI(CommonApplication con, HttpRequestBase req) {
//            super.onAfterInUI(con, req);
//            (( MyApplication) con).getAppSoftUpdateUtils().closeNotification();
//        }
//    }
//
//    /**
//     * 关闭下载通知图标
//     */
//    private void closeNotification() {
//        notificationUtils.CancelNotification(downloadnotifyInfo);
//    }
//
//
//    /**
//     * 获取包路径
//     *
//     * @param newversionnumber
//     * @return
//     */
//    public String getApkPath(String newversionnumber) {
//        return CommonFuc.GetBitmapPath(application, "update", newversionnumber + ".apk");
//    }
//
//
//    /**
//     * 是否存在已经下载的apk包
//     *
//     * @param newversionnumber
//     * @return
//     */
//    public boolean hasApkFile(String newversionnumber) {
//        try {
//            String path = CommonFuc.GetBitmapPath(application, "update", newversionnumber + ".apk");
//            File apkfile = new File(path);
//            return apkfile.exists();
//        } catch (Exception ex) {
//            return false;
//        }
//    }
//
//
//    /**
//     * 新版本更新
//     */
//    private void newversionOnOk(Activity activity, String downloadUrl) {
//        validateUIThread();
//        if (PermissionUtils.hasPemissions(application, updateAppPermissions)) {
//            startNewVersionRequest(downloadUrl);
//        } else {
//            if (activity != null) {
//                PermissionUtils.requestPermissions(activity, updateAppPermissions, Update_App_Permissions_Request_Code);
//            }
//        }
//    }
//
//
//    /**
//     * @param commonApplication
//     * @return
//     */
//    private BaseActivity getTopBaseActivity(CommonApplication commonApplication) {
//        ArrayList<IActivityManagerCallBase> uis = commonApplication.getActivityManager();
//        if (uis != null) {
//            if (uis.size() > 0) {
//                for (int i = uis.size() - 1; i >= 0; i--) {
//                    IActivityManagerCallBase managerCallBase = uis.get(i);
//                    if (managerCallBase instanceof BaseActivity) {
//                        return (BaseActivity) managerCallBase;
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 验证是否在UI线程
//     */
//    private void validateUIThread() {
////        if (!application.IsUIThread()) {
////            throw new RuntimeException("必须在UI中操作");
////        }
//    }
//
//
//    /**
//     * 是否正在下载apk文件
//     *
//     * @return
//     */
//    private boolean isDownLoadApkFile() {
//        boolean R = application.getDeviceUpdateHttpComponet().containsRequestByTag("downloadAppUpdateApkFile");
//        return R;
//    }
//
//    public void checkApp(Activity activity) {
//        if (AppUserInfo.getInstance().userInfo == null) return;
//        NetTool.getApi().selectDataV2(1)
//                .compose(RxUtil.IoToMainObserve())
//                .subscribe(new BeanObserver<AppVersion>(activity) {
//                    @Override
//                    public void onSuccess(BaseResponse<AppVersion> bean) {
//                        if (bean != null && bean.getData() != null) {
//                            AppVersion appVersion = bean.getData();
//                            checkAppVersion(activity, appVersion);
//                        } else {
//                            SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                            EventBus.getDefault().post(new UpdateEvent());
//                            ToastUtil.showToast( MyApplication.getInstance(), activity.getResources().getString(R.string.app_latest_version), Toast.LENGTH_SHORT);
//                        }
//                    }
//                });
//    }
//
//    public void checkAppUpdate(Activity activity) {
//        if (AppUserInfo.getInstance().userInfo == null) return;
//        NetTool.getApi().selectDataV2(1)
//                .compose(RxUtil.IoToMainObserve())
//                .subscribe(new BeanObserver<AppVersion>(activity) {
//                    @Override
//                    public void onSuccess(BaseResponse<AppVersion> bean) {
//                        if (bean != null && bean.getData() != null) {
//                            AppVersion appVersion = bean.getData();
//                            LogUtils.i(TAG, new Gson().toJson(appVersion));
//                            if (appVersion.getUploadType() == 1) {
//                                checkAppVersion(activity, appVersion);
//                            } else if (appVersion.getUploadType() == 2) {
//                                Calendar calendar = Calendar.getInstance();
//                                calendar.setTimeInMillis(System.currentTimeMillis());
//                                int curDay = calendar.get(Calendar.DAY_OF_YEAR);
//                                int saveDay = (int) SPUtil.getData( MyApplication.getInstance(), SpConfig.UPDATE_DAY, 0);
//                                String oldVersion = (String) SPUtil.getData( MyApplication.getInstance(), SpConfig.UPDATE_VERSION, "");
//                                if (curDay != saveDay || !TextUtils.equals(appVersion.getVersionName(), oldVersion)) {
//                                    SPUtil.saveData( MyApplication.getInstance(), SpConfig.UPDATE_DAY, calendar.get(Calendar.DAY_OF_YEAR));
//                                    SPUtil.saveData( MyApplication.getInstance(), SpConfig.UPDATE_VERSION, appVersion.getVersionName());
//                                    checkAppVersion(activity, appVersion);
//                                }
//                                if (AppUtils.compareVersion(appVersion.getVersionName(), APKVersionInfoUtils.getVersionName(activity))) {
//                                    SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, true);
//                                    SPUtil.saveData(activity, SpConfig.APP_UPDATE_MSG, appVersion.getContent());
//                                } else {
//                                    SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                                }
//                                EventBus.getDefault().post(new UpdateEvent());
//                            } else if (appVersion.getUploadType() == 3) {
//                                boolean isShow = (boolean) SPUtil.getData( MyApplication.getInstance(), SpConfig.IS_SHOW_UPDATE, false);
//                                String oldVersion = (String) SPUtil.getData( MyApplication.getInstance(), SpConfig.UPDATE_VERSION, "");
//                                if (!TextUtils.equals(appVersion.getVersionName(), oldVersion) || !isShow) {
//                                    SPUtil.saveData( MyApplication.getInstance(), SpConfig.IS_SHOW_UPDATE, true);
//                                    SPUtil.saveData( MyApplication.getInstance(), SpConfig.UPDATE_VERSION, appVersion.getVersionName());
//                                    checkAppVersion(activity, appVersion);
//                                }
//                                if (AppUtils.compareVersion(appVersion.getVersionName(), APKVersionInfoUtils.getVersionName(activity))) {
//                                    SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, true);
//                                    SPUtil.saveData(activity, SpConfig.APP_UPDATE_MSG, appVersion.getContent());
//                                } else {
//                                    SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                                }
//                                EventBus.getDefault().post(new UpdateEvent());
//                            }
//                        } else {
//                            SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                            EventBus.getDefault().post(new UpdateEvent());
////                            ToastUtil.showToast( MyApplication.getInstance(), "App已是最新版本", Toast.LENGTH_SHORT);
//                        }
//                    }
//                });
//    }
//
//    public void checkAppVersion(Activity activity, AppVersion appVersion) {
//        if (appVersion == null) {
//            ToastUtil.showToast( MyApplication.getInstance(),  MyApplication.getInstance().getResources().getString(R.string.unable_version_information), Toast.LENGTH_SHORT);
//            return;
//        }
//        AppSoftUpdateUtils appSoftUpdateUtils =  MyApplication.getInstance().getAppSoftUpdateUtils();
//        int version = TextUtils.isEmpty(appVersion.getVersion()) ? 0 : NumberUtils.stringToInt(appVersion.getVersion(), 0);
//        int min_version = NumberUtils.stringToInt(appVersion.getMinVersion(), -1);
//        String versionName = appVersion.getVersionName();
//        String content = appVersion.getContent();
//        String uploadFile = appVersion.getUploadFile();
////        LogUtils.i(new Gson().toJson(appVersion));
//        if (AppUtils.compareVersion(appVersion.getVersionName(), APKVersionInfoUtils.getVersionName(activity))/* || BuildConfig.DEBUG*/) {
//            SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, true);
//            SPUtil.saveData(activity, SpConfig.APP_UPDATE_MSG, appVersion.getContent());
//            appSoftUpdateUtils.newVersionCode = version;
//            appSoftUpdateUtils.versionName = versionName;
//            appSoftUpdateUtils.hasNewVersion = true;
//            appSoftUpdateUtils.mDownloadUrl = uploadFile;
//            DialogUtil.showUpdateDialog(activity, TextUtils.isEmpty(content) ? MyApplication.getInstance().getResources().getString(R.string.has_new_version_updated) : content, appVersion.getVersionName(), appVersion.getUploadType() != 1,
//                    new DialogUtil.OnDialogPositiveClickListener() {
//                        @Override
//                        public void onPositiveClickListener(View v, Object msg) {
//                            if (! MyApplication.getInstance().getAppSoftUpdateUtils().isHasInstallPermissionWithO( MyApplication.getInstance())) {//如果没有安装权限
//                                ConfirmTipsView installtisp = new ConfirmTipsView(activity);
//                                installtisp.showDialog( MyApplication.getInstance().getResources().getString(R.string.have_installation_permission),  MyApplication.getInstance().getResources().getString(R.string.deny)
//                                        ,  MyApplication.getInstance().getResources().getString(R.string.correct), new ConfirmTipsView.OnBtnClickListener() {
//                                            @Override
//                                            public void onClick(boolean isConfirm) {
//                                                installtisp.dismiss();
//                                                if (isConfirm) {
//                                                    Uri packageURI = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
//                                                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, packageURI);
//                                                    activity.startActivityForResult(intent, 5252);
//                                                }
//                                            }
//                                        });
//                            } else {
//                                 MyApplication.getInstance().getAppSoftUpdateUtils().newversionOnOk(activity, uploadFile); //开始更新
//                            }
//                        }
//                    }, new DialogUtil.OnDialogDismissClickListener() {
//                        @Override
//                        public void onDismissClickListener(View v, boolean isSure) {
//                            if (appVersion.getUploadType() == 1 && !isSure) {
//                                System.exit(0);
//                            }
//                        }
//                    });
//            appSoftUpdateUtils.performNewVersionChange(appSoftUpdateUtils.hasNewVersion, appVersion.getVersionName());
//        } else {
//            SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//            appSoftUpdateUtils.performNewVersionChange(false, appVersion.getVersionName());
//        }
//        EventBus.getDefault().post(new UpdateEvent());
//    }
//
//    public void checkAppUpdateEvent(Activity activity) {
//        if (activity == null) return;
//        if (AppUserInfo.getInstance().userInfo == null) return;
//        NetTool.getApi().selectDataV2(1)
//                .compose(RxUtil.IoToMainObserve())
//                .subscribe(new BeanObserver<AppVersion>(activity) {
//                    @Override
//                    public void onSuccess(BaseResponse<AppVersion> bean) {
//                        if (bean != null && bean.getData() != null) {
//                            AppVersion appVersion = bean.getData();
////                            LogUtils.i(TAG, new Gson().toJson(appVersion));
//                            if (AppUtils.compareVersion(appVersion.getVersionName(), APKVersionInfoUtils.getVersionName(activity))) {
//                                SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, true);
//                                SPUtil.saveData(activity, SpConfig.APP_UPDATE_MSG, appVersion.getContent());
//                            } else {
//                                SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                            }
//                            EventBus.getDefault().post(new UpdateEvent());
//                        } else {
//                            SPUtil.saveData(activity, SpConfig.IS_APP_UPDATE, false);
//                            EventBus.getDefault().post(new UpdateEvent());
//                        }
//                    }
//                });
//    }

}
