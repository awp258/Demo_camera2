package com.demo.camera2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;



/**
 * Copyright (C), 2013-2019, 深圳市浩瀚卓越科技有限公司
 * Author: Abraham.ai@hohem-tech.com
 * Date: 2019/11/1 9:45
 * Description:
 * History:
 */
public class FileOperation {
    private ThreadPoolExecutor mExecutor;

    String TAG="FileOperation";

    public FileOperation() {

        mExecutor = new ThreadPoolExecutor(1, 4, 10,
                TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(4),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public void saveImageFile(final FileOperationLister fileOperationLister , final Image image, final String filepath)
    {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                byte[] bytes = new byte[buffer.remaining()];
                buffer.get(bytes);//由缓冲区存入字节数组
                image.close();

                Log.e(TAG, "filepath : ." + filepath);

                File mFile = new File(filepath);
                try {
                    mFile.createNewFile();
                } catch (IOException e) {
                    Log.d(TAG, "saveBitmap: 保存图片是出错");
                }
                FileOutputStream output = null;
                try {
                    output = new FileOutputStream(mFile);
                    output.write(bytes);
                    fileOperationLister.success(bytes);

                } catch (IOException e) {
                    e.printStackTrace();
                    fileOperationLister.fail();
                } finally {
                    if (null != output) {
                        try {
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        });
    }


    public void saveFile( final Image image ,final String  filepath)
    {
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {

                compressToJpeg(filepath,image);
            }
        });
    }

    private static final int COLOR_FormatI420 = 1;
    private static final int COLOR_FormatNV21 = 2;

    private void compressToJpeg(String filePath, Image image) {
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(filePath);
        } catch (IOException ioe) {
            throw new RuntimeException("Unable to create output file " + filePath, ioe);
        }
        Rect rect = image.getCropRect();

        Long time1 = System.currentTimeMillis();
//        rotateYUV420Degree90(getDataFromImage(image, COLOR_FormatNV21),rect.width(),rect.height())
        //YuvImage yuvImage = new YuvImage(getDataFromImage(image, COLOR_FormatNV21), ImageFormat.NV21,rect.width() , rect.height(), null);
        YuvImage yuvImage = new YuvImage(rotateYUV420Degree90(getDataFromImage(image, COLOR_FormatNV21), rect.width(), rect.height()), ImageFormat.NV21, rect.height(), rect.width(), null);
        //rotateBitmap(yuvImage,90,rect,fileName);
        yuvImage.compressToJpeg(new Rect(0, 0, rect.height(), rect.width()), 100, outStream);
//        yuvImage.compressToJpeg(rect, 100, outStream);
        Long time2 = System.currentTimeMillis();
        Log.d(TAG, "compressToJpeg: time" + (time2 - time1));
    }

    private byte[] getDataFromImage(Image image, int colorFormat) {
        if (colorFormat != COLOR_FormatI420 && colorFormat != COLOR_FormatNV21) {
            throw new IllegalArgumentException("only support COLOR_FormatI420 " + "and COLOR_FormatNV21");
        }
        if (!isImageFormatSupported(image)) {
            throw new RuntimeException("can't convert Image to byte array, format " + image.getFormat());
        }
        Rect crop = image.getCropRect();
        int format = image.getFormat();
        int width = crop.width();
        int height = crop.height();
        Image.Plane[] planes = image.getPlanes();
        byte[] data = new byte[width * height * ImageFormat.getBitsPerPixel(format) / 8];
        byte[] rowData = new byte[planes[0].getRowStride()];
        if (true) Log.v(TAG, "get data from " + planes.length + " planes");
        int channelOffset = 0;
        int outputStride = 1;
        for (int i = 0; i < planes.length; i++) {
            switch (i) {
                case 0:
                    channelOffset = 0;
                    outputStride = 1;
                    break;
                case 1:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = width * height;
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height + 1;
                        outputStride = 2;
                    }
                    break;
                case 2:
                    if (colorFormat == COLOR_FormatI420) {
                        channelOffset = (int) (width * height * 1.25);
                        outputStride = 1;
                    } else if (colorFormat == COLOR_FormatNV21) {
                        channelOffset = width * height;
                        outputStride = 2;
                    }
                    break;
            }
            ByteBuffer buffer = planes[i].getBuffer();
            int rowStride = planes[i].getRowStride();
            int pixelStride = planes[i].getPixelStride();
            if (true) {
                Log.v(TAG, "pixelStride " + pixelStride);
                Log.v(TAG, "rowStride " + rowStride);
                Log.v(TAG, "width " + width);
                Log.v(TAG, "height " + height);
                Log.v(TAG, "buffer size " + buffer.remaining());
            }
            int shift = (i == 0) ? 0 : 1;
            int w = width >> shift;
            int h = height >> shift;
            //Log.d(TAG, "getDataFromImage: " + (rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift)));
            buffer.position(rowStride * (crop.top >> shift) + pixelStride * (crop.left >> shift));
            for (int row = 0; row < h; row++) {
                int length;
                if (pixelStride == 1 && outputStride == 1) {
                    length = w;
                    buffer.get(data, channelOffset, length);
                    channelOffset += length;
                } else {
                    length = (w - 1) * pixelStride + 1;
                    buffer.get(rowData, 0, length);
                    for (int col = 0; col < w; col++) {
                        data[channelOffset] = rowData[col * pixelStride];
                        channelOffset += outputStride;
                    }
                }
                if (row < h - 1) {
                    //Log.d(TAG, "getDataFromImage: " + buffer.position());
                    buffer.position(buffer.position() + rowStride - length);
                }
            }
            if (true) Log.v(TAG, "Finished reading data from plane " + i);
        }
        return data;
    }
    private static boolean isImageFormatSupported(Image image) {
        int format = image.getFormat();
        switch (format) {
            case ImageFormat.YUV_420_888:
            case ImageFormat.NV21:
            case ImageFormat.YV12:
                return true;
        }
        return false;
    }



    private static byte[] rotateYUV420Degree90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    public interface  FileOperationLister
    {
        public void success(Object object);
        public void fail();
    }
}
