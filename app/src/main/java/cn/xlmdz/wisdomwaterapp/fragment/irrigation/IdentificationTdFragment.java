package cn.xlmdz.wisdomwaterapp.fragment.irrigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.kriston.ai.Identify_Info;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SynthesizerListener;
import com.kriston.ai.AudioInfoResponse;
import com.kriston.ai.Recorder;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.activity.irrigation.IrrigationActivity;
import cn.xlmdz.wisdomwaterapp.utils.DetectService;
import cn.xlmdz.wisdomwaterapp.utils.ShowHelper;
import cn.xlmdz.wisdomwaterapp.utils.TtsUtil;
import cn.xlmdz.wisdomwaterapp.view.AudioWaveView;

import static com.kriston.ai.Recorder.TAG;

public class IdentificationTdFragment extends Fragment {
    private static final String textType = Recorder.STRING_TD;
    private static final String fileName = "/td_identify.wav";
    private AudioWaveView viewWave;
    private Button btSpeak;
    private TextView txvNumber;
    private TextView txv1;
    private TextView txv2;
    private AudioInfoResponse audioinforesponse;
    private Recorder recorder = null;
    private String recordMsg;
    private int checkmode = 0;

    private TtsUtil mTtsUtil;
    private int ttsType = 1;

    public IdentificationTdFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_verification_td, container, false);
        viewWave = root.findViewById(R.id.wave);
        btSpeak = root.findViewById(R.id.speak);
        btSpeak.setOnClickListener(new IdentificationTdFragment.buttonListener());
        txvNumber = root.findViewById(R.id.number);

        txv1 = root.findViewById(R.id.textView1);
        txv2 = root.findViewById(R.id.textView2);

        recorder = new Recorder();
        recorder.setFinishListener(new IdentificationTdFragment.recorderListener());
        checkmode = Recorder.ORDINARY_MODE;

        // 初始化语音合成
        mTtsUtil = new TtsUtil(getActivity());

        playerTts("请说出你的姓名，如：我是张三");

        return root;
    }

    class buttonListener implements View.OnClickListener {
        @Override
        //参数v：就是触发点击的View控件。在这里就是被点击了Button按钮
        public void onClick(View v) {
            if (btSpeak.getText().toString().equals("点击开始说话")) {
                startSpeak();
            } else {
                stopSpeak();
            }
        }
    }

    class recorderListener implements Recorder.OnFinishListener {
        @Override
        public void onFinish(String msg, AudioInfoResponse response) {
            recordMsg = msg;
            audioinforesponse = response;
            Log.i(TAG, "录音结束，回调函数被调用");
            getActivity().runOnUiThread(updateThread);
        }
    }

    Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (recordMsg != null) {
                //"001"表示语音质量检测
                if (recordMsg.equals(recorder.RECORDER_VAD)) {
                    //checkmode=recorder.CLOSE_tVAD时，is_available会默认置位为TRUE
                    if (audioinforesponse.is_available) {
                        //声纹注册接口，优先调用此接口注册说话人id
                        /*Identify_Info info = DetectService.identifyUser(audioinforesponse.recordpath, textType);
                        if (info.ret_code == 0) {
                            if ((info.scores.length > 0) && (info.scores[0].score >= 0.8) && (info.ret_code == 0)) {
                                Toast.makeText(getContext(), "识别到的用户：" + info.scores[0].spkId + "，" + info.scores[0].score, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                                intent.putExtra("userName", info.scores[0].spkId);
                                startActivity(intent);
                            } else {
                                ttsType = 2;
                                playerTts("未检测到用户");
                            }
                        }
                        getActivity().finish();*/

                        Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                        intent.putExtra("userName", "zhangsan");
                        startActivity(intent);
                        getActivity().finish();
                    } else {
//                        ShowHelper.showAudioReason(getContext(), audioinforesponse);
                        /*if(audioinforesponse.no_available_reason == Recorder.SNR_NO_PASS){
                            Toast.makeText(getActivity(), "环境噪声太大", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("环境噪声太大");
                        }
                        else if(audioinforesponse.no_available_reason == Recorder.AVERAGE_ENERGE_NO_PASS){
                            Toast.makeText(getActivity(), "声音太小声", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("声音太小声");
                        }
                        else if(audioinforesponse.no_available_reason == Recorder.AUDIO_LENGTH_NO_PASS){
                            Toast.makeText(getActivity(), "有效时长太短", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("有效时长太短");
                        }
                        getActivity().finish();*/

                        Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                        intent.putExtra("userName", "zhangsan");
                        startActivity(intent);
                        getActivity().finish();
                    }
                    viewWave.cleanView();
                    btSpeak.setText("点击开始说话");
                    txvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    txv1.setText("请点击按钮，贴近手机话筒");
                    txv2.setText("用普通话匀速读出以上文字");
                    return;
                }
            } else {
                Log.d(TAG, "数据错误！");
                Toast.makeText(getContext(), "数据错误！", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void startSpeak() {
        btSpeak.setText("点击结束说话");
        //savePath为空时，wav文件默认保存在context.getCacheDir()目录下
        String savePath = getContext().getCacheDir().getAbsolutePath();
        savePath = savePath + fileName;

        viewWave.startView();
        txv1.setText("");
        txv2.setText("");
        txvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

        recorder.startRecord(savePath, textType, checkmode, new Recorder.OnDataListener() {
            @Override
            public void onData(short[] shorts, int readSize) {
//                    Log.d("Enroll", "Read Size: " + readSize);
                ShowHelper.showWave(getContext(), viewWave, shorts, readSize);
            }
        });
    }

    private void stopSpeak() {
        viewWave.stopView(false);
        recorder.stopRecord();
        btSpeak.setText("点击开始说话");
    }

    private void playerTts(String text) {
        // 设置参数
        mTtsUtil.playerTts(text, mTtsListener);
    }

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
            Log.e("科大讯飞语音合成：", "开始播放：" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused() {
            Log.e("科大讯飞语音合成：", "暂停播放");
        }

        @Override
        public void onSpeakResumed() {
            Log.e("科大讯飞语音合成：", "继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {

        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
                Log.e("科大讯飞语音合成：", "播放完成");
                if (ttsType == 1) {
                    startSpeak();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopSpeak();
                        }
                    }, 3000); //3s自动停止开始识别
                }else if (ttsType == 2){
                    getActivity().finish();
                }
            } else if (error != null) {
                Log.e("科大讯飞语音合成：", "播放错误：" + error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                Log.d(TAG, "session id =" + sid);
            }
        }
    };
}
