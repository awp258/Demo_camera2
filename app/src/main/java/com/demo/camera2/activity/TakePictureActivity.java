package com.demo.camera2.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.demo.camera2.FocusFrameView;
import com.demo.camera2.R;
import com.demo.camera2.base.BaseActivity;
import com.demo.camera2.config.CameraHelper;
import com.demo.camera2.config.TakePictureConfig;
import com.demo.camera2.utils.FileOperation;
import com.demo.camera2.utils.FilePathUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/10/31 16:17
 * Description:
 * History:
 */
public class TakePictureActivity extends BaseActivity {
    @BindView(R.id.surfaceView)
    SurfaceView surfaceView;
    @BindView(R.id.btn_single_take)
    Button btnSingleTake;
    @BindView(R.id.btn_more_take)
    Button btnMoreTake;
    @BindView(R.id.iv_Look)
    ImageView ivLook;

    Handler mBackgroundHandler;

    @BindView(R.id.btn_camera_state)
    Button btnCameraState;
    /**
     * 对焦框
     */
    private FocusFrameView focusFrameView;

    CameraHelper cameraHelper=new CameraHelper();
    /**
     * 初始化相机
     */
    @SuppressLint("MissingPermission")
    private void initCamera() {
        HandlerThread handlerThread = new HandlerThread("Camera2");
        handlerThread.start();
        handler1 = new Handler(handlerThread.getLooper());
        HandlerThread handlerThread1 = new HandlerThread("Captures");
        handlerThread1.start();
        handler2 = new Handler(handlerThread1.getLooper());
        mainHandler = new Handler(getMainLooper());
        HandlerThread mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
        //获取摄像头管理
        //获取最小的焦距值
        cameraHelper.init(mContext, mainHandler, new CameraHelper.CameraHelperLister() {
            @Override
            public void onOpened(CameraDevice camera) {
                //Toast.makeText(CameraActivity.this, "打开摄像头成功", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "onOpened: 打开摄像头成功");
                //当打开摄像头成功后开始预览
                mCameraDevice = camera;
                cameraHelper.startCameraPreview(mCameraDevice, takePictrueConfig, surfaceHolder);
                mCameraCaptureSession=takePictrueConfig.getmCameraCaptureSession();
            }
            @Override
            public void onDisconnected(CameraDevice camera) {
                Toast.makeText(mContext, "关闭摄像头成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(CameraDevice camera, int error) {
                Toast.makeText(mContext, "摄像头开启失败", Toast.LENGTH_SHORT).show();
            }
            /**
             * 图片数据准备好的监听，获取图片
             */
            @Override
            public void onImageAvailable(String type, ImageReader imageReader) {
                if(type.equals("JPEG"))
                {
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    filepath = FilePathUtils.getInstance(true).getDefaultImageFilePath() + "/" + timeStamp + "_" + ".jpg";
                    // new Handler().post(new JPEGImageSaver());
                    fileOperation.saveImageFile(new FileOperation.FileOperationLister() {
                        @Override
                        public void success(Object object) {
                            //显示图片
                            byte[] bytes = (byte[]) object;
                            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ivLook.setImageBitmap(bitmap);
                                }
                            });
                        }
                        @Override
                        public void fail() {
                            Toast.makeText(mContext, "图片保存失败", Toast.LENGTH_SHORT).show();

                        }
                    },imageReader.acquireNextImage(), filepath);
                }
                else{
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
                    String filepath = FilePathUtils.getInstance(true).getDefaultImageFilePath() + "/" + timeStamp + "_" + ".jpg";
                    Log.d(TAG, "onImageAvailable: saveimage in " + filepath);
                    //保存文件
                    fileOperation.saveFile(imageReader.acquireNextImage(), filepath);
                }
            }
        });
        characteristics=cameraHelper.getCharacteristics();
    }

    FileOperation fileOperation=new FileOperation();
    TakePictureConfig takePictrueConfig=new TakePictureConfig();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_take_picture);
        ButterKnife.bind(this);
        surfaceHolder = surfaceView.getHolder();
        focusFrameView = new FocusFrameView(mContext, 540, 960, 200, 200, Color.BLUE);
//在一个activity上面添加额外的content
        addContentView(focusFrameView, new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT));
        focusFrameView.setVisibility(View.GONE);
        surfaceHolder = surfaceView.getHolder();
        //保持常亮
        surfaceView.setKeepScreenOn(true);
        //surfaceview的回调
        surfaceHolder.addCallback(new SurfaceHolderCallback());
        initLister();
    }

    public void initLister()
    {
        cameraHelper.setCaptureCallbackLister(new CameraHelper.CaptureCallbackLister() {
            @Override
            public void onCaptureStarted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, long timestamp, long frameNumber) {

            }

            @Override
            public void process(CaptureResult result) {
                switch (mState) {
                    case STATE_PREVIEW: {
                        // We have nothing to do when the camera preview is working normally.
                        break;
                    }
                    case STATE_CAPTURE:
                        CaptureRequest.Builder takePictureRequestBuilder=takePictrueConfig.getTakePictureRequestBuilder();
                        try {
                            mCameraCaptureSession.capture(takePictureRequestBuilder.build(), null, handler2);
                        } catch (CameraAccessException e) {
                            e.printStackTrace();
                        }
                        break;
                    case STATE_REPRE:
                        cameraHelper.updatePreview(takePictrueConfig.getPreviewRequestBuilder(),takePictrueConfig.getmCameraCaptureSession() );
                        mState = STATE_PREVIEW;
                        break;
                }

            }

            @Override
            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                mCameraCaptureSession = session;
                process(result);
            }

            @Override
            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                mCameraCaptureSession = session;
            }
        });
    }
    /**
     * SurfaceHolder的回调
     * 当surfaceview准备好的时候初始化相机
     */
    class SurfaceHolderCallback implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "surfaceCreated: ");
            //初始化相机
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
        }
    }



    boolean isFront=true;
    @OnClick({R.id.btn_single_take, R.id.btn_more_take, R.id.iv_Look,R.id.btn_camera_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_single_take:
                takePicture();
                break;
            case R.id.btn_more_take:


                break;
            case R.id.iv_Look:
                Intent intent = new Intent(TakePictureActivity.this, LookImageActivity.class);
                intent.putExtra("imagePath", filepath);
                startActivity(intent);
                break;
            case R.id.btn_camera_state:
                if(isFront)
                {
               String CameraID= String.valueOf(CameraCharacteristics.LENS_FACING_FRONT);
                 cameraHelper.setCameraID(CameraID);
                }
                else {
                    String    CameraID= String.valueOf(CameraCharacteristics.LENS_FACING_BACK);
                    cameraHelper.setCameraID(CameraID);
                }
                initCamera();
                isFront=!isFront;
                break;

        }
    }
    Handler mainHandler;
    SurfaceHolder surfaceHolder;
    private CameraDevice mCameraDevice;

    private CameraCaptureSession mCameraCaptureSession;
    private Handler handler1, handler2;

    /**
     * 拍照
     */

    /**
     * camera数据属性
     */
    private CameraCharacteristics characteristics;
    private StreamConfigurationMap streamConfigurationMap;
    //闪光灯开启或关闭
    private static int LIGHTOPENORCLOSE = CaptureRequest.FLASH_MODE_OFF;
    private static final int STATE_PREVIEW = 0;
    private static final int STATE_CAPTURE = 1;
    private static final int STATE_REPRE = 2;
    private int mState = STATE_PREVIEW;

    private void takePicture() {
        cameraHelper.takePicture( takePictrueConfig, TakePictureActivity.this, surfaceHolder, mCameraDevice);
         mState = STATE_REPRE;

    }
    String filepath = "";

}
