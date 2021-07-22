package cn.xlmdz.wisdomwaterapp.utils;


import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.kriston.ai.AudioInfoResponse;
import com.kriston.ai.Recorder;

import java.util.List;

import cn.xlmdz.wisdomwaterapp.view.AudioWaveView;

public class ShowHelper {
    public ShowHelper() {

    }
    public static void showWave(Context context, AudioWaveView viewWave, short[] shorts, int readSize) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);

        float fontScale = context.getResources().getDisplayMetrics().density;
        int offset = (int) (1 * fontScale + 0.5f);
        int maxSize = outMetrics.widthPixels / offset;
        maxSize = maxSize - 10;
        int waveSpeed = 300;

        List<Short> dataList = viewWave.getRecList();
        if (dataList != null) {
            int length = readSize / waveSpeed;
            short resultMax = 0, resultMin = 0;
            for (short i = 0, k = 0; i < length; i++, k += waveSpeed) {
                for (short j = k, max = 0, min = 1000; j < k + waveSpeed; j++) {
                    if (shorts[j] > max) {
                        max = shorts[j];
                        resultMax = max;
                    } else if (shorts[j] < min) {
                        min = shorts[j];
                        resultMin = min;
                    }
                }
                if (dataList.size() > maxSize) {
                    dataList.remove(0);
                }
                dataList.add(resultMax);
            }
        }
    }

    public static void showAudioReason(Context context, AudioInfoResponse audioinforesponse){
        if(audioinforesponse.no_available_reason == Recorder.SNR_NO_PASS){
            Toast.makeText(context, "环境噪声太大", Toast.LENGTH_LONG).show();
        }
        else if(audioinforesponse.no_available_reason == Recorder.AVERAGE_ENERGE_NO_PASS){
            Toast.makeText(context, "声音太小声", Toast.LENGTH_LONG).show();
        }
        else if(audioinforesponse.no_available_reason == Recorder.AUDIO_LENGTH_NO_PASS){
            Toast.makeText(context, "有效时长太短", Toast.LENGTH_LONG).show();
        }
    }
}