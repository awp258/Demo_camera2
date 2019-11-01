package com.demo.camera2.config;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.Toast;

import com.demo.camera2.activity.TakePictureActivity;
import com.demo.camera2.utils.FilePathUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;


/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/11/1 15:24
 * Description:
 * History:
 */
public class CameraHelper {

    private CameraManager cameraManager;
    public String getCameraID() {
        return CameraID;
    }
    public void setCameraID(String cameraID) {
        CameraID = cameraID;
    }
    public CameraCharacteristics getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(CameraCharacteristics characteristics) {
        this.characteristics = characteristics;
    }
    private CameraCharacteristics characteristics;
    CameraHelperLister mCameraHelperLister;
    /**
     * 最小的焦距值
     */
    private Float minimumLens;
    /**
     * 传感器的信息ISO
     */
    private Range<Integer> integerRange;

    private StreamConfigurationMap map;

    /**
     * 传感器的最大值
     */
    private Integer maxISO;
    /**
     * 传感器的最小值
     */
    private Integer minISO;
    /**
     * 前后摄像头设置
     */
    String CameraID= String.valueOf(CameraCharacteristics.LENS_FACING_BACK);
    @SuppressLint("MissingPermission")
    public void init(Context mContext , Handler mainHandler, CameraHelperLister cameraHelperLister)
    {
         cameraManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
         this.mCameraHelperLister=cameraHelperLister;
        try {
            //打开摄像头
            Log.d("MyCamera", "打开摄像头成功");
            cameraManager.openCamera(CameraID, stateCallback, mainHandler);
            characteristics = cameraManager.getCameraCharacteristics(CameraID);


            minimumLens = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE);
            //传感器的信息
            integerRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            Integer integer1 = characteristics.get(CameraCharacteristics.SENSOR_MAX_ANALOG_SENSITIVITY);

            Log.d(TAG, "initCamera: SENSOR_MAX_ANALOG_SENSITIVITY " + integer1);
            if (integerRange != null) {
                //获取最大最小值
                maxISO = integerRange.getUpper();
                minISO = integerRange.getLower();
                Log.d(TAG, "initCamera: minimumLens :" + minimumLens + " maxISO: " + maxISO + " minISO: " + minISO + " range: " + integerRange.toString());
            }
            Log.d(TAG, "initCamera: " + minimumLens + " ca ");
            map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            Integer integer = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);
            Toast.makeText(mContext, "" + integer.toString(), Toast.LENGTH_SHORT).show();
            Log.d(TAG, "initCamera: " + integer.toString());
            int[] outputFormats = map.getOutputFormats();
            for (int i = 0; i < outputFormats.length; i++) {
                Log.d(TAG, "outputFormats: " + outputFormats[i]);
            }
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            //设置最大的图像尺寸
            imageReaderYUV = ImageReader.newInstance(1080, 1920, ImageFormat.YUV_420_888, 35);
            imageReaderJPEG = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, 10);
            imageReaderYUV.setOnImageAvailableListener(new MyOnImageAvailableListener(), mainHandler);
            imageReaderJPEG.setOnImageAvailableListener(new MyJPEGOnImageAvailableListener(), mainHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d("MyCamera", "打开摄像头失败");
        }

    }

    public CaptureCallbackLister getCaptureCallbackLister() {
        return mCaptureCallbackLister;
    }

    public void setCaptureCallbackLister(CaptureCallbackLister captureCallbackLister) {
        this.mCaptureCallbackLister = captureCallbackLister;
    }

    CaptureCallbackLister mCaptureCallbackLister;


    public void takePicture(TakePictureConfig takePictrueConfig, Activity activity , SurfaceHolder surfaceHolder,CameraDevice mCameraDevice)
    {
        try {
            ImageReader imageReaderJPEG= getImageReaderJPEG();
            takePictrueConfig.takePictureConfig(characteristics, activity, imageReaderJPEG, surfaceHolder, mCameraDevice);
            CaptureRequest.Builder takePictureRequestBuilder=takePictrueConfig.getTakePictureRequestBuilder();

            takePictrueConfig. previewBuilder2CaptureBuilder(takePictureRequestBuilder);

            //  Log.d(TAG, "takePicture: " + rotation);
            //拍照
            CaptureRequest mCaptureRequest = takePictureRequestBuilder.build();
            takePictrueConfig.getmCameraCaptureSession().stopRepeating();
            takePictrueConfig.getmCameraCaptureSession().capture(mCaptureRequest, myCaptureCallback, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private CameraCaptureSession.CaptureCallback myCaptureCallback = new CameraCaptureSession.CaptureCallback() {

        @Override
        public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {
            super.onCaptureStarted(session, request, timestamp, frameNumber);
            //Log.d(TAG, "onCaptureStarted: ");
        }

        private void process(CaptureResult result) {
            mCaptureCallbackLister.process( result);
        }
        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            //Log.d(TAG, "onCaptureProgressed: ");
            //process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            //

            if(mCaptureCallbackLister==null){
                Log.d(TAG, "mCaptureCallbackLister is null ");
            }
            mCaptureCallbackLister.onCaptureCompleted(session,request,result  );

    //        mCameraCaptureSession = session;
            process(result);
        }

        @Override
        public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
            super.onCaptureFailed(session, request, failure);
          //  mCameraCaptureSession = session;
            //Log.d(TAG, "onCaptureFailed: ");
            mCaptureCallbackLister.onCaptureFailed(session,request ,failure );


        }

        @Override
        public void onCaptureSequenceCompleted(@NonNull CameraCaptureSession session, int sequenceId, long frameNumber) {
            super.onCaptureSequenceCompleted(session, sequenceId, frameNumber);
        }

        @Override
        public void onCaptureSequenceAborted(@NonNull CameraCaptureSession session, int sequenceId) {
            super.onCaptureSequenceAborted(session, sequenceId);
            //Log.d(TAG, "onCaptureSequenceAborted: ");
        }

        @Override
        public void onCaptureBufferLost(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull Surface target, long frameNumber) {
            super.onCaptureBufferLost(session, request, target, frameNumber);
            //Log.d(TAG, "onCaptureBufferLost: ");
        }
    };


    class MyOnImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            mCameraHelperLister.onImageAvailable("YUV", imageReader);
        }
    }

    class MyJPEGOnImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader imageReader) {
            mCameraHelperLister.onImageAvailable("JPEG", imageReader);
        }
    }

    public ImageReader getImageReaderYUV() {
        return imageReaderYUV;
    }

    public void setImageReaderYUV(ImageReader imageReaderYUV) {
        this.imageReaderYUV = imageReaderYUV;
    }

    public ImageReader getImageReaderJPEG() {
        return imageReaderJPEG;
    }

    public void setImageReaderJPEG(ImageReader imageReaderJPEG) {
        this.imageReaderJPEG = imageReaderJPEG;
    }

   private ImageReader imageReaderYUV, imageReaderJPEG;


    /**
     * 摄像头创建监听
     */
    private CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {//打开摄像头
            //Toast.makeText(CameraActivity.this, "打开摄像头成功", Toast.LENGTH_SHORT).show();
           // Log.d(TAG, "onOpened: 打开摄像头成功");
            mCameraHelperLister.onOpened(camera);
        }
        @Override
        public void onDisconnected(CameraDevice camera) {//关闭摄像头
         //   Toast.makeText(mContext, "关闭摄像头成功", Toast.LENGTH_SHORT).show();
            mCameraHelperLister.onDisconnected(camera);
        }
        @Override
        public void onError(CameraDevice camera, int error) {//发生错误
          //  Toast.makeText(mContext, "摄像头开启失败", Toast.LENGTH_SHORT).show();
            mCameraHelperLister.onError(camera,error);
        }
    };

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }
    }


    /**
     * 摄像头开始预览
     */
    public void startCameraPreview(CameraDevice mCameraDevice, final TakePictureConfig takePictureConfig, SurfaceHolder surfaceHolder)
    {
         takePictureConfig.startCameraPreviewConfig(mCameraDevice,surfaceHolder);
        try {
            mCameraDevice.createCaptureSession(Arrays.asList(surfaceHolder.getSurface(), imageReaderYUV.getSurface(), imageReaderJPEG.getSurface()), new CameraCaptureSession.StateCallback() // ③
            {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    // 当摄像头已经准备好时，开始显示预览
                    takePictureConfig.setmCameraCaptureSession(cameraCaptureSession);
                    // 显示预览
                    CaptureRequest previewRequest = takePictureConfig.getPreviewRequestBuilder().build();
                    //  mCameraCaptureSession.setRepeatingRequest(previewRequest, captureCallback, handler1);
                    try {
                        cameraCaptureSession.setRepeatingRequest(previewRequest, myCaptureCallback,null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    //Toast.makeText(CameraActivity.this, "配置失败", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onConfigureFailed: 配置失败");
                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }


    }



    /**
     * 更新预览
     */
    public void updatePreview(CaptureRequest.Builder previewRequestBuilder,CameraCaptureSession cameraCaptureSession) {
        try {
            previewRequestBuilder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
            //3A
            cameraCaptureSession.setRepeatingRequest(previewRequestBuilder.build(), myCaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("updatePreview", "ExceptionExceptionException");
        }
    }


 public   interface CameraHelperLister {
         void  onOpened(CameraDevice camera);
         void  onDisconnected(CameraDevice camera);
         void  onError(CameraDevice camera, int error);
         void onImageAvailable(String type, ImageReader imageReader);
    }

    /**
     * 拍照回调
     */
    public interface  CaptureCallbackLister
    {
         void  onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber);
         void  process(CaptureResult result);
         void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                 @NonNull CaptureRequest request,
                                 @NonNull TotalCaptureResult result);
         void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure);

    }
}
