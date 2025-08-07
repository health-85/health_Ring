package com.healthy.rvigor.dao.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Message;

import com.healthy.rvigor.BuildConfig;
import com.healthy.rvigor.MyApplication;
import com.healthy.rvigor.greendao.gen.DaoMaster;

import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.LinkedList;


/**
 * 创建数据库、创建数据库表、包含增删改查的操作
 */
public class AppDaoManager {
    private static final String TAG = AppDaoManager.class.getSimpleName();
    private static final String DB_NAME = "HEALTHY_DB";
    private DaoMaster mWritableDaoMaster = null;
    private DaoMaster mReadableDaoMaster = null;
    private DaoMaster.DevOpenHelper mHelper;
    private MyApplication application = null;

    private static class DBOpenHelper extends DaoMaster.DevOpenHelper {

        public DBOpenHelper(Context context, String name) {
            super(context, name);
        }

        public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
            super(context, name, factory);
        }

        @Override
        public void onCreate(Database db) {
            DaoMaster.createAllTables(db, true);
        }
    }

    public AppDaoManager(MyApplication commonApplication) {
        setDebug();
        application = commonApplication;
        mHelper = new DaoMaster.DevOpenHelper(commonApplication, DB_NAME, null);
        mWritableDaoMaster = new DaoMaster(mHelper.getWritableDatabase());
        mReadableDaoMaster = new DaoMaster(mHelper.getReadableDatabase());
        new Thread(new Runnable() {
            @Override
            public void run() {
                DoneDBExecutorInIOThread();//执行任务
            }
        }).start();
    }


    /**
     * 线程已经开始运行
     */
    private void onThreadStartRunning() {
        //SleepDBEntityDao.dropTable(mWritableDaoMaster.getDatabase(),true);
        DaoMaster.createAllTables(mWritableDaoMaster.getDatabase(), true);
    }


    /**
     * 数据库context
     */
    public static class DBContext {
        /**
         * 应用程序对象
         */
        public MyApplication application = null;
        /**
         * 数据库操作对象 写入
         */
        public DaoMaster mWritableDaoMaster = null;
        /**
         * 数据库操作对象 读取
         */
        public DaoMaster mReadableDaoMaster = null;


        public DBContext(MyApplication application, DaoMaster mWritableDaoMaster, DaoMaster mReadableDaoMaster) {
            this.application = application;
            this.mWritableDaoMaster = mWritableDaoMaster;
            this.mReadableDaoMaster = mReadableDaoMaster;
        }
    }

    /**
     * 是否正在休眠
     */
    private boolean isWaiting = false;


    /**
     * 异步执行数据操作
     *
     * @param executor
     */
    public void ExecuteDBAsync(DBExecutor executor) {
        if (executor == null) {
            return;
        }
        synchronized (executors) {
            if (!executors.contains(executor)) {
                executors.addLast(executor);
                if (isWaiting) {
                    isWaiting = false;
                    executors.notifyAll();
                }
            }
        }
    }


    /**
     * 执行任务的队列
     */
    private final LinkedList<DBExecutor> executors = new LinkedList<>();

    /**
     * 处理任务 在IO后台线程里面执行该任务
     */
    private void DoneDBExecutorInIOThread() {
        onThreadStartRunning();
        DBExecutor dbExecutor = null;
        while (true) {
            dbExecutor = null;
            synchronized (executors) {
                if (executors.size() > 0) {
                    dbExecutor = executors.removeFirst();
                } else {
                    isWaiting = true;
                    try {
                        executors.wait();
                    } catch (InterruptedException e) {
                        isWaiting = false;
                    }
                }
            }
            if (dbExecutor != null) {
                dbExecutor.Execute(new DBContext(application, mWritableDaoMaster, mReadableDaoMaster));
            }
        }
    }


    /**
     * 主要是操作db
     */
    public static abstract class DBExecutor {
        /**
         * 结果接口
         */
        public static interface IResult {
            /**
             * 成功
             *
             * @param result
             */
            public void OnSucceed(Object result);

            /**
             * 失败
             *
             * @param ex
             */
            public void OnError(Exception ex);
        }

        /**
         * 可以执行 数据操作任务
         *
         * @param dbContext 上下文
         */
        public abstract void Execute(DBContext dbContext);


        /**
         * 发送消息到UI
         *
         * @param msg
         */
        protected void sendMessageToUIThread(MyApplication application, Message msg) {
            application.getUiHandler().PostAndWait(new MessageUIRunnable(application, this, msg));
        }

        protected void onMessageInUI(MyApplication application, Message msg) {
        }

        /**
         * 这是消息在UI中执行
         */
        private static class MessageUIRunnable implements Runnable {

            private MyApplication commonApplication = null;

            private DBExecutor dbExecutor = null;

            private Message msg = null;

            public MessageUIRunnable(MyApplication commonApplication, DBExecutor dbExecutor, Message msg) {
                this.commonApplication = commonApplication;
                this.dbExecutor = dbExecutor;
                this.msg = msg;
            }

            @Override
            public void run() {
                dbExecutor.onMessageInUI(commonApplication, msg);//消息已经发送到UI
                this.commonApplication = null;
                this.dbExecutor = null;
                this.msg = null;
            }
        }

    }


    /**
     * 打开输出日志，默认关闭
     */
    public void setDebug() {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    /**
     * 关闭所有的操作，数据库开启后，使用完毕要关闭
     */
    public void closeConnection() {
        closeHelper();
    }

    public void closeHelper() {
        if (mHelper != null) {
            mHelper.close();
            mHelper = null;
        }
    }

}
