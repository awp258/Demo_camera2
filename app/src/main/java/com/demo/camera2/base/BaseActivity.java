package com.demo.camera2.base;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.demo.camera2.permission.PermissionsManager;
import com.demo.camera2.permission.PermissionsResultAction;

/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/10/31 16:26
 * Description:
 * History:
 */
public class BaseActivity  extends Activity{
   public String TAG=this.getClass().getName();
   public Context mContext;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        mContext=this.getApplicationContext();
    }
    private String[] permissions = {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    private void requestPermission() {
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                Log.i(TAG,"onGranted >>>>> success");
            }
            @Override
            public void onDenied(String permission) {
                Log.i(TAG,"permission >>>>>" + permission );

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }
}
