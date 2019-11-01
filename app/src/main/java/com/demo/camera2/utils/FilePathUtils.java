package com.demo.camera2.utils;

import android.os.Environment;


import com.demo.camera2.base.BaseApplication;

import java.io.File;

/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/9/17 13:39
 * Description: 文件路径操作
 * History:
 */
public class FilePathUtils {

    private String SDCARD_PATH;
    //发布模式路径
//    private String SAVE_PATH_NAME = "."+ BaseApplication.getInstance().getPackageName();
    //开发模式路径
    private String SAVE_PATH_NAME = "a_camera2_test";

    private volatile static FilePathUtils mPathUtils;

    /**
     * 得到缓存路径 1，如果有sd卡 就是sd空间 2，没有sd卡 就是手机系统分配空间
     *
     */
    private FilePathUtils(boolean isSaveSDCard) {
        // SDCARD 目录
//        isSaveSDCard=false;
        if (isSaveSDCard) {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                SDCARD_PATH = Environment.getExternalStorageDirectory()
                        .getAbsolutePath();
            } else {
                SDCARD_PATH = BaseApplication.getInstance().getCacheDir().getPath();
            }
        } else {
            // 内存目录
            SDCARD_PATH = BaseApplication.getInstance().getCacheDir().getPath();
        }

        File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static FilePathUtils getInstance(boolean isSaveSDCard) {
        if(mPathUtils == null) {
            synchronized (FilePathUtils.class) {
                if(mPathUtils == null) {
                    mPathUtils = new FilePathUtils(isSaveSDCard);
                }
            }
        }
        return mPathUtils;
    }

    /**
     * 获得当前应用默认的解压路径
     *
     * @return
     */
    public File getDefaultUnzipFile() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/unZip");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
        return null;
    }

    /**
     * 获取SD卡目录下相对应包名程序下的文件保存的图片的路径
     *
     * @return
     */
    public String getDefaultFilePath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/file");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡目录下相对应包名程序下的拍照保存的图片的路径
     *
     * @return
     */
    public String getDefaultImageFilePath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/image");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     *  保存文件多张图片选择后，保存视频文件缓存文件夹
     *
     * @return
     */
    public String getDefaultImageMergeFileCachePath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/ImageMergeCache");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡目录下相对应包名程序下的录音保存的图片的路径
     *
     * @return
     */
    public String getDefaultRecordPath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/record");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡目录下相对应包名程序下的视频保存的图片的路径
     *
     * @return
     */
    public String getDefaultVideoPath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/video");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }


    /**
     * 获取SD卡目录下相对应包名程序下的视频保存的图片的路径
     * @return   图片压缩成视频文件目录
     */
    public String getDefaultMerageVideoPath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/video/merge");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 获取SD卡目录下相对应包名程序下的视频保存的图片的路径
     * @return   视频转换成图片路径  放在缓存目录下，
     */
    public String getDefaultVideoToImagePath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/video/image2Video");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }
    /**
     * 获取SD卡目录提供的背景MP3文件
     * @return   视频转换成图片路径  放在缓存目录下，
     */
    public String getDefaultVideoBGMp3Path() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/music/BGMp3");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    /**
     * 资源文件下存放路径
     * @return
     */
    public String getDefaultAssetsPath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/assets");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }


    /**
     * 获取SD卡目录下相对应包名程序下的安装包路径
     *
     * @return
     */
    public String getDefaultApkPath() {
        if (SDCARD_PATH != null) {
            File file = new File(SDCARD_PATH + "/" + SAVE_PATH_NAME + "/apk");
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        }
        return null;
    }

    public String getSDCardPath() {
        return SDCARD_PATH + "/" + SAVE_PATH_NAME;
    }
}
