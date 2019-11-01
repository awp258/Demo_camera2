package com.demo.camera2.config;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.ImageReader;
import android.util.Range;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;




/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/11/1 10:54
 * Description:
 * History:
 */
public class TakePictureConfig {

    public CameraCaptureSession getmCameraCaptureSession() {
        return mCameraCaptureSession;
    }
    public void setmCameraCaptureSession(CameraCaptureSession mCameraCaptureSession) {
        this.mCameraCaptureSession = mCameraCaptureSession;
    }
    private  CameraCaptureSession   mCameraCaptureSession;

    public CaptureRequest.Builder getPreviewRequestBuilder() {
        return previewRequestBuilder;
    }

    public void setPreviewRequestBuilder(CaptureRequest.Builder previewRequestBuilder) {
        this.previewRequestBuilder = previewRequestBuilder;
    }

    /**
     * 预览的build
     */
    private CaptureRequest.Builder previewRequestBuilder;

    public CaptureRequest.Builder getTakePictureRequestBuilder() {
        return takePictureRequestBuilder;
    }
    public void setTakePictureRequestBuilder(CaptureRequest.Builder takePictureRequestBuilder) {
        this.takePictureRequestBuilder = takePictureRequestBuilder;
    }

    /**
     * 拍照的build
     */
    private CaptureRequest.Builder takePictureRequestBuilder;



    //初始化的话是实用中间值
    private float valueAF;
    private int valueAE;
    private long valueAETime;
    private int valueISO;

    private void initSeekBarValue() {
        valueAF = 5.0f;
        valueAETime = (214735991 - 13231) / 2;
        valueISO = (10000 - 100) / 2;
        valueAE = 0;
    }

    /**
     * 开始预览 摄像机属性设置
     * @param mCameraDevice
     * @param surfaceHolder
     */
    public void startCameraPreviewConfig(  CameraDevice mCameraDevice ,SurfaceHolder surfaceHolder)
    {
        final   CaptureRequest.Builder      previewRequestBuilder;
        try {
            previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);

            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
            //初始化参数
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
            previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, valueAF);
            previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, valueAETime);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueAE);
            previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, valueISO);
            //3A--->auto
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //3A
            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
            previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
            setPreviewRequestBuilder(previewRequestBuilder);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

    }
//    /**
//     * 开始预览
//     */
//    public void takepictureCameraDisplay(SurfaceHolder surfaceHolder, final CameraCaptureSession.CaptureCallback captureCallback , final CameraDevice mCameraDevice , ImageReader imageReaderJPEG, ImageReader imageReaderYUV) {
//
//            // 创建预览需要的CaptureRequest.Builder
//            final CaptureRequest.Builder previewRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
//            // 将SurfaceView的surface作为CaptureRequest.Builder的目标
//            previewRequestBuilder.addTarget(surfaceHolder.getSurface());
//            //初始化参数
//            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_OFF);
//            previewRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, valueAF);
//            previewRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, valueAETime);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, valueAE);
//            previewRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, valueISO);
//            //3A--->auto
//            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
//            //3A
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE, CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE, CameraMetadata.CONTROL_AE_MODE_ON);
//            previewRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, CameraMetadata.CONTROL_AWB_MODE_AUTO);
//            // 创建CameraCaptureSession，该对象负责管理处理预览请求和拍照请求
//            setPreviewRequestBuilder(previewRequestBuilder);
//
//    }




    /**
     * 将previewBuilder中修改的参数设置到captureBuilder中
     */
    public void previewBuilder2CaptureBuilder(CaptureRequest.Builder takePictureRequestBuilder) {
        //HDR等等
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_MODE));
        //AWB
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AWB_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_AWB_MODE));
        //AE
//        if (mPreviewBuilder.get(CaptureRequest.CONTROL_AE_MODE) == CameraMetadata.CONTROL_AE_MODE_OFF) {
        //曝光时间
        takePictureRequestBuilder.set(CaptureRequest.SENSOR_EXPOSURE_TIME, previewRequestBuilder.get(CaptureRequest.SENSOR_EXPOSURE_TIME));
//        } else if (mPreviewBuilder.get(CaptureRequest.CONTROL_AE_MODE) == CameraMetadata.CONTROL_AE_MODE_ON) {
        //曝光增益
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION, previewRequestBuilder.get(CaptureRequest.CONTROL_AE_EXPOSURE_COMPENSATION));
//        }
        //AF
//        if (mPreviewBuilder.get(CaptureRequest.CONTROL_AF_MODE) == CameraMetadata.CONTROL_AF_MODE_OFF) {
        //手动聚焦的值
        takePictureRequestBuilder.set(CaptureRequest.LENS_FOCUS_DISTANCE, previewRequestBuilder.get(CaptureRequest.LENS_FOCUS_DISTANCE));
//        }
        //effects
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_EFFECT_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_EFFECT_MODE));
        //ISO
        takePictureRequestBuilder.set(CaptureRequest.SENSOR_SENSITIVITY, previewRequestBuilder.get(CaptureRequest.SENSOR_SENSITIVITY));
        //AF REGIONS
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AF_REGIONS, previewRequestBuilder.get(CaptureRequest.CONTROL_AF_REGIONS));
//        mCaptureBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER, CameraMetadata.CONTROL_AF_TRIGGER_START);
        //AE REGIONS
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_REGIONS, previewRequestBuilder.get(CaptureRequest.CONTROL_AE_REGIONS));
//        mCaptureBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER, CameraMetadata.CONTROL_AE_PRECAPTURE_TRIGGER_START);
        //SCENSE
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_SCENE_MODE, previewRequestBuilder.get(CaptureRequest.CONTROL_SCENE_MODE));
        //zoom
        takePictureRequestBuilder.set(CaptureRequest.SCALER_CROP_REGION, previewRequestBuilder.get(CaptureRequest.SCALER_CROP_REGION));
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    ///为了使照片竖直显示
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    //闪光灯开启或关闭
    private static int LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_OFF;
    public void takePictureConfig(CameraCharacteristics characteristics,Activity context, ImageReader imageReaderJPEG, SurfaceHolder surfaceHolder , CameraDevice mCameraDevice)
    {
        try {
            takePictureRequestBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        // 将imageReader的surface作为CaptureRequest.Builder的目标
        takePictureRequestBuilder.addTarget(imageReaderJPEG.getSurface());
        takePictureRequestBuilder.addTarget(surfaceHolder.getSurface());
        //takePictureRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
        //设置连续帧
        Range<Integer> fps[] = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES);
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fps[fps.length - 1]);//设置每秒30帧
        //得到方向
        takePictureRequestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                CameraMetadata.CONTROL_AE_MODE_ON);
        takePictureRequestBuilder.set(CaptureRequest.FLASH_MODE, LIGHTOPENORCLOSE);
        int rotation = context. getWindowManager().getDefaultDisplay().getRotation();
        // 根据设备方向计算设置照片的方向
        takePictureRequestBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));
    }

}
