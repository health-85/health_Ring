package com.healthy.rvigor.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 自定义每隔几秒执行一次任务
 */
public class CustomerTimerRepeatExecutor {


    /**
     * 锁定对象
     */
    private final Object lockobj = new Object();

    /**
     * 任务信息
     */
    private static class TaskInfo {
        /**
         * 上次执行的时间
         */
        public long lastExecuterTimer = System.currentTimeMillis();

        /**
         * 执行的任务
         */
        public Runnable runnable = null;

        /**
         * 休眠时间
         */
        public long sleepTime = 0;

        public TaskInfo(Runnable runnable, long sleepTime) {
            this.runnable = runnable;
            this.sleepTime = sleepTime;
        }
    }

    /**
     * 可执行任务
     */
    private final List<TaskInfo> taskInfos = new ArrayList<>();


    public CustomerTimerRepeatExecutor() {
        Thread thread = new Thread(run);
        thread.start();
    }

    private Runnable run = new Runnable() {
        @Override
        public void run() {
            RunTask();
        }
    };

    /**
     * 是否处于一种休眠等待状态
     */
    private boolean isWaiting = false;

    /**
     * 是否退出
     */
    private boolean isExit = false;


    /**
     * 添加任务
     * @param runnable
     * @param sleepTime 毫秒
     * @return
     */
    public boolean AddTask(Runnable runnable, long sleepTime) {
        if (runnable == null) {
            return false;
        }
        if (sleepTime < 0) {
            return false;
        }
        synchronized (lockobj) {
            if (isExit){
                  return false;
            }
            if (!containRunnable(runnable)) {
                taskInfos.add(new TaskInfo(runnable, sleepTime));
                if (isWaiting) {
                    isWaiting = false;
                    lockobj.notifyAll();
                }
            } else {
                return false;
            }
        }
        return true;
    }


    /**
     * 移除任务
     * @param runnable
     */
    public  void  removeTask(Runnable runnable){
         if (runnable==null){
             return;
         }
          synchronized (lockobj){
              for (int i = 0; i < taskInfos.size(); i++) {
                  TaskInfo  curr=taskInfos.get(i);
                  if (curr.runnable == runnable) {
                      taskInfos.remove(curr);
                      return;
                  }
              }
          }
    }


    /**
     * 是否包含运行任务
     * @param runnable
     * @return
     */
    private boolean containRunnable(Runnable runnable) {
        for (int i = 0; i < taskInfos.size(); i++) {
            if (taskInfos.get(i).runnable == runnable) {
                return true;
            }
        }
        return false;
    }


    /**
     * 退出执行循环
     */
    public   void  Exit(){
         synchronized (lockobj){
             isExit=true;
             taskInfos.clear();
             if (isWaiting){
                   isWaiting=false;
                   lockobj.notifyAll();
             }
         }
    }




    /**
     * 执行任务
     */
    private void RunTask() {
        /**
         * 最少休眠时间
         */
        long minsleepTime = 0;

        /**
         * 需要立即执行的任务
         */
        LinkedList<TaskInfo> needExecutes = new LinkedList<>();


        long alltaskExecutorStartTime = 0;//开始执行所有任务时间

        long execTime = 0;//执行时间
        while (true) {
            synchronized (lockobj) {
                if (isExit){//如果退出  则退出循环
                      break;
                }
                if (taskInfos.size() == 0) {
                    isWaiting = true;
                    try {
                        lockobj.wait();
                    } catch (InterruptedException e) {
                        isWaiting = false;
                    }
                } else {
                    minsleepTime = caculateMinSleepTimeAndNeedExecutorTasks(needExecutes);
                }
            }
            alltaskExecutorStartTime = System.currentTimeMillis();//设置开始执行所有任务时间
            execute(needExecutes);
            if (minsleepTime > 2000) {//如果最小时间大于2000毫秒
                minsleepTime = 2000;
            }
            if (minsleepTime < 0) {
                minsleepTime = 0;
            }
            execTime = System.currentTimeMillis() - alltaskExecutorStartTime;
            if (execTime < 0) {
                execTime = 0;
            }
            if ((minsleepTime - execTime) > 0) {//如果休眠的最小时间减去执行时间大于零
                try {
                    Thread.sleep(minsleepTime - execTime);
                } catch (InterruptedException e) {

                }
            }
        }
    }


    /**
     * 执行任务
     *
     * @param needExecutes
     */
    private void execute(LinkedList<TaskInfo> needExecutes) {
        boolean isRemoved = false;//是否被移除
        while (needExecutes.size() > 0) {
            TaskInfo taskInfo = needExecutes.removeFirst();
            taskInfo.lastExecuterTimer = System.currentTimeMillis();
            if (taskInfo.runnable != null) {
                synchronized (lockobj) {
                    isRemoved = (!taskInfos.contains(taskInfo));
                }
                if (!isRemoved) {//如果没被移除则执行任务
                    taskInfo.runnable.run();
                }
            }
        }
    }


    /**
     * 计算最小休眠时间以及需要立即执行的任务
     *
     * @param needExecute
     * @return
     */
    private long caculateMinSleepTimeAndNeedExecutorTasks(LinkedList<TaskInfo> needExecute) {
        long minsleepTime = 0;
        for (int i = 0; i < taskInfos.size(); i++) {
            TaskInfo taskInfo = taskInfos.get(i);
            if (i == 0) {
                minsleepTime = taskInfo.sleepTime;
            } else {
                if (taskInfo.sleepTime < minsleepTime) {
                    minsleepTime = taskInfo.sleepTime;
                }
            }
            if ((System.currentTimeMillis()
                    - taskInfo.lastExecuterTimer) >= taskInfo.sleepTime) {//如果上次执行的时间大于休眠时间则需要立即执行
                needExecute.addLast(taskInfo);
            }
        }
        return minsleepTime;
    }


}
