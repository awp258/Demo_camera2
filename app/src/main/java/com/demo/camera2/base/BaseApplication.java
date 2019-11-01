package com.demo.camera2.base;

import android.app.Application;
import android.content.Context;

/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/10/31 16:55
 * Description:
 * History:
 */
public class BaseApplication extends Application {


    public static Context getInstance() {
        return BaseApplication.getInstance().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
