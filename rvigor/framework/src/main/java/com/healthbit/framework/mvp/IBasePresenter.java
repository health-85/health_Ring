package com.healthbit.framework.mvp;

import android.content.Intent;
import android.os.Bundle;

/**
* @Description:
* @Author:         zxy(1051244836@qq.com)
* @CreateDate:     2019/4/23
* @UpdateRemark:   无
* @Version:        1.0
*/
public interface IBasePresenter<V extends IBaseView> {
    void onViewAttached(V view);

    void onViewDetached();

    V getView();

    void loadData(Intent intent);

    void loadData(Bundle bundle);

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
