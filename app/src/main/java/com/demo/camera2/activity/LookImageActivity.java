package com.demo.camera2.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.demo.camera2.R;
import com.demo.camera2.base.BaseActivity;

import com.demo.camera2.view.DoubleScaleImageView;
import com.demo.camera2.view.ZoomImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/10/31 16:24
 * Description: 图片查看
 * History:
 */
public class LookImageActivity extends BaseActivity {


    String path = "";
    @BindView(R.id.image)
    ZoomImageView image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_look_image);

        ButterKnife.bind(this);

        path=getIntent().getStringExtra("imagePath");

        Glide.with(mContext)
                .load(path)
                //     .diskCacheStrategy(file.isFile() ? DiskCacheStrategy.NONE : DiskCacheStrategy.ALL)
                .crossFade()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher)
                .into(image);

       // GlideUtils.getInstance().displayRoundedHeaderView(image, path);
        Log.i("textLog" , "path : "+path );


    }

}
