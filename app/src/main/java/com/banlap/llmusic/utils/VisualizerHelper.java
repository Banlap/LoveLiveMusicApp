package com.banlap.llmusic.utils;

import android.media.audiofx.Visualizer;
import android.util.Log;

import java.util.Arrays;

/**
 * 捕获波形数据或音频频率数据 帮助类
 * */
public class VisualizerHelper {
    private static final String TAG = VisualizerHelper.class.getSimpleName();
    private Visualizer mVisualizer;
    private int mCount;//频谱数量
    private VisualizerCallback visualizerCallback;

    /**
     * 开始捕获波形数据或音频频率数据
     * */
    public void startVisualizer(int audioSessionId) {
        if(mVisualizer != null) {
            mVisualizer.release();
        }
        Log.i(TAG, "audioSessionId:" + audioSessionId);
        mVisualizer = new Visualizer(audioSessionId);
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        //rate：采样的频率，其范围是0~Visualizer.getMaxCaptureRate()，
        //waveform：是否获取波形信息
        //fft：是否获取快速傅里叶变换后的数据
        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] waveform, int samplingRate) {
                //Log.i(TAG, "waveform:" + Arrays.toString(waveform) +  " samplingRate: " + samplingRate);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] fft, int samplingRate) {
                //Log.i(TAG, "fft:" + Arrays.toString(fft) +  " samplingRate: " + samplingRate);
                float[] model = new float[fft.length / 2 + 1];
                model[0] = (byte) Math.abs(fft[1]);
                int j = 1;

                for (int i = 2; i < mCount *2;) {
                    model[j] = (float) Math.hypot(fft[i], fft[i + 1]);
                    i += 2;
                    j++;
                    model[j] = (float) Math.abs(fft[j]);
                }
                //model即为最终用于绘制的数据
                visualizerCallback.onDataReturn(model);
            }
        }, Visualizer.getMaxCaptureRate() , true, true);
        mVisualizer.setEnabled(true);
    }

    public void setVisualizerCallback(VisualizerCallback visualizerCallback) {
        this.visualizerCallback = visualizerCallback;
    }

    public void setVisualCount(int count) {
        mCount = count;
    }

    /** */
    public void release() {
        if (mVisualizer != null) {
            mVisualizer.setEnabled(false);
            mVisualizer.release();
        }
    }

    public interface VisualizerCallback {
        void onDataReturn(float[] parseData);
    }
}
