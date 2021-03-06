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

        // ?????????????????????
        mTtsUtil = new TtsUtil(getActivity());

        playerTts("??????????????????????????????????????????");

        return root;
    }

    class buttonListener implements View.OnClickListener {
        @Override
        //??????v????????????????????????View????????????????????????????????????Button??????
        public void onClick(View v) {
            /*if (btSpeak.getText().toString().equals("??????????????????")) {
                startSpeak();
            } else {
                stopSpeak();
            }*/
        }
    }

    class recorderListener implements Recorder.OnFinishListener {
        @Override
        public void onFinish(String msg, AudioInfoResponse response) {
            recordMsg = msg;
            audioinforesponse = response;
            Log.i(TAG, "????????????????????????????????????");
            getActivity().runOnUiThread(updateThread);
        }
    }

    Runnable updateThread = new Runnable() {
        @Override
        public void run() {
            if (recordMsg != null) {
                //"001"????????????????????????
                if (recordMsg.equals(recorder.RECORDER_VAD)) {
                    //checkmode=recorder.CLOSE_tVAD??????is_available??????????????????TRUE
                    if (audioinforesponse.is_available) {
                        //?????????????????????????????????????????????????????????id
                        Identify_Info info = DetectService.identifyUser(audioinforesponse.recordpath, textType);
                        if (info.ret_code == 0) {
                            if ((info.scores.length > 0) && (info.scores[0].score >= 0.8) && (info.ret_code == 0)) {
                                Toast.makeText(getContext(), "?????????????????????" + info.scores[0].spkId + "???" + info.scores[0].score, Toast.LENGTH_LONG).show();

                                Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                                intent.putExtra("userName", info.scores[0].spkId);
                                startActivity(intent);
                            } else {
                                ttsType = 2;
                                playerTts("??????????????????");
                            }
                        }
                        getActivity().finish();

                        /*Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                        intent.putExtra("userName", "zhangsan");
                        startActivity(intent);
                        getActivity().finish();*/
                    } else {
//                        ShowHelper.showAudioReason(getContext(), audioinforesponse);
                        if(audioinforesponse.no_available_reason == Recorder.SNR_NO_PASS){
                            Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("??????????????????");
                        }
                        else if(audioinforesponse.no_available_reason == Recorder.AVERAGE_ENERGE_NO_PASS){
                            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("???????????????");
                        }
                        else if(audioinforesponse.no_available_reason == Recorder.AUDIO_LENGTH_NO_PASS){
                            Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_LONG).show();
                            mTtsUtil.playerTts("??????????????????");
                        }
                        getActivity().finish();

                        /*Intent intent = new Intent(getActivity(), IrrigationActivity.class);
                        intent.putExtra("userName", "zhangsan");
                        startActivity(intent);
                        getActivity().finish();*/
                    }
                    viewWave.cleanView();
                    btSpeak.setText("??????????????????");
                    txvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
                    txv1.setText("????????????????????????????????????");
                    txv2.setText("????????????????????????????????????");
                    return;
                }
            } else {
                Log.d(TAG, "???????????????");
                Toast.makeText(getContext(), "???????????????", Toast.LENGTH_LONG).show();
            }
        }
    };

    private void startSpeak() {
        btSpeak.setText("??????????????????");
        //savePath????????????wav?????????????????????context.getCacheDir()?????????
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
        btSpeak.setText("??????????????????");
    }

    private void playerTts(String text) {
        // ????????????
        mTtsUtil.playerTts(text, mTtsListener);
    }

    /**
     * ?????????????????????
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("????????????");
            Log.e("???????????????????????????", "???????????????" + System.currentTimeMillis());
        }

        @Override
        public void onSpeakPaused() {
            Log.e("???????????????????????????", "????????????");
        }

        @Override
        public void onSpeakResumed() {
            Log.e("???????????????????????????", "????????????");
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
                Log.e("???????????????????????????", "????????????");
                if (ttsType == 1) {
                    startSpeak();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopSpeak();
                        }
                    }, 5000); //3s????????????????????????
                }else if (ttsType == 2){
                    getActivity().finish();
                }
            } else if (error != null) {
                Log.e("???????????????????????????", "???????????????" + error.getPlainDescription(true));
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
