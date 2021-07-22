package cn.xlmdz.wisdomwaterapp.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.util.ResourceUtil;

import cn.xlmdz.wisdomwaterapp.R;

public class WakeUtil {
    private static Context mContext;
    // 语音唤醒对象
    private static VoiceWakeuper mIvw;
    // 唤醒结果内容
    private static String resultString;

    public static VoiceWakeuper initIntance(Context context, InitListener initListener) {
        if (mIvw == null) {
            mIvw = VoiceWakeuper.createWakeuper(context, initListener);
        }
        mContext = context;
        return mIvw;
    }

    public static void startWakeuper(WakeuperListener wakeuperListener) {
        //非空判断，防止因空指针使程序崩溃
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            resultString = "";

            // 清空参数
            mIvw.setParameter(SpeechConstant.PARAMS, null);
            // 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
            mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:" + 1450);
            // 设置唤醒模式
            mIvw.setParameter(SpeechConstant.IVW_SST, "wakeup");
            // 设置持续进行唤醒
            mIvw.setParameter(SpeechConstant.KEEP_ALIVE, "1");
            // 设置闭环优化网络模式
            mIvw.setParameter(SpeechConstant.IVW_NET_MODE, "0");
            // 设置唤醒资源路径
            mIvw.setParameter(SpeechConstant.IVW_RES_PATH, getResource());
            // 设置唤醒录音保存路径，保存最近一分钟的音频
            mIvw.setParameter(SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath() + "/msc/ivw.wav");
            mIvw.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
            // 如有需要，设置 NOTIFY_RECORD_DATA 以实时通过 onEvent 返回录音音频流字节
            //mIvw.setParameter( SpeechConstant.NOTIFY_RECORD_DATA, "1" );
            // 启动唤醒
            /*	mIvw.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");*/

            mIvw.startListening(wakeuperListener);
				/*File file = new File(Environment.getExternalStorageDirectory().getPath() + "/msc/ivw1.wav");
				byte[] byetsFromFile = getByetsFromFile(file);
				mIvw.writeAudio(byetsFromFile,0,byetsFromFile.length);*/
            //	mIvw.stopListening();
        } else {
            Toast.makeText(mContext, "唤醒未初始化", Toast.LENGTH_SHORT).show();
        }
    }

    private static String getResource() {
        final String resPath = ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "ivw/545724bc.jet");
        return resPath;
    }

    public static void stopWakeuper(){
        mIvw.stopListening();
    }
}
