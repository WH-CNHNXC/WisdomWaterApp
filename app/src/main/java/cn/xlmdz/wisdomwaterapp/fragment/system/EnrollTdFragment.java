package cn.xlmdz.wisdomwaterapp.fragment.system;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.kriston.ai.AudioInfoResponse;
import com.kriston.ai.Recorder;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.room.entity.User;
import cn.xlmdz.wisdomwaterapp.room.manager.UserManager;
import cn.xlmdz.wisdomwaterapp.utils.DetectService;
import cn.xlmdz.wisdomwaterapp.utils.ShowHelper;
import cn.xlmdz.wisdomwaterapp.view.AudioWaveView;

import static com.kriston.ai.Recorder.TAG;

public class EnrollTdFragment extends Fragment {
    private static final String textType = Recorder.STRING_TD;
    private static final String fileName = "/td_register_";

    private AudioWaveView viewWave;
    private Button btSpeak;

    private TextView regPage1;
    private TextView regPage2;
    private TextView regPage3;

    private TextView txvNumber;
    private TextView txv1;
    private TextView txv2;

    private AudioInfoResponse audioinforesponse;
    private Recorder recorder = null;

    private String[] regPath = new String[5];
    private String recordMsg;
    public String speaker;
    public String name;
    public String password;

    private int register_cnt = 0;
    private int register_num_set = 3; //注册语音条数
    private int checkmode = 0;

    public EnrollTdFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_enroll_td, container, false);
        viewWave = root.findViewById(R.id.wave);
        btSpeak = root.findViewById(R.id.speak);
        btSpeak.setOnClickListener(new buttonListener());
        txvNumber = root.findViewById(R.id.number);
        txv1 = root.findViewById(R.id.textView1);
        txv2 = root.findViewById(R.id.textView2);

        regPage1 = root.findViewById(R.id.regPage1);
        regPage2 = root.findViewById(R.id.regPage2);
        regPage3 = root.findViewById(R.id.regPage3);

        recorder = new Recorder();
        recorder.setFinishListener(new recorderListener());
        checkmode = Recorder.ORDINARY_MODE;

        return root;
    }

    class buttonListener implements View.OnClickListener {
        @Override
        //参数v：就是触发点击的View控件。在这里就是被点击了Button按钮
        public void onClick(View v) {

            if (btSpeak.getText().toString().equals("点击开始说话")) {
                btSpeak.setText("点击结束说话");
                //savePath为空时，wav文件默认保存在context.getCacheDir()目录下
                String savePath = getActivity().getCacheDir().getAbsolutePath();
                savePath = savePath + fileName + register_cnt + ".wav";
                regPath[register_cnt] = savePath;

                txv1.setText("");
                txv2.setText("");
                txvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
                viewWave.startView();

                recorder.startRecord(savePath, textType, checkmode, new Recorder.OnDataListener() {
                    @Override
                    public void onData(short[] shorts, int readSize) {
//                    Log.d("EnrollRdFragment", "Read Size: " + readSize);
                        ShowHelper.showWave(getContext(), viewWave, shorts, readSize);
                    }
                });
            } else {
                viewWave.stopView(false);
                recorder.stopRecord();
            }
        }
    }

    public class recorderListener implements Recorder.OnFinishListener {
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
                        if (register_cnt == (register_num_set - 1)) {
                            DetectService.deleteUser(speaker, textType);
                            String savePath = getActivity().getCacheDir().getAbsolutePath();
                            savePath = savePath + "/" + textType + "_register_merge" + ".wav";
                            recorder.wavMerge(regPath, savePath);
                            //声纹注册接口，优先调用此接口注册说话人id
                            int ret = DetectService.registUser(speaker, savePath, textType);
                            if (ret == 0) {
                                // 用户信息插入数据库
                                User user = new User(speaker, name, password);
                                user.setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                                UserManager.addUser(getActivity(), user);

                                FinishEnrollFragment fragment = new FinishEnrollFragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction()
                                        .replace(R.id.content, fragment)
                                        .commit();
                                return;
                            }
                            register_cnt = 0;
                        }
                        register_cnt++;
                        setPageNum(register_cnt);
                    } else {
                        ShowHelper.showAudioReason(getContext(), audioinforesponse);
                        viewWave.cleanView();
                    }
                    txvNumber.setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);
                    btSpeak.setText("点击开始说话");
                    return;
                }

                //"002"表示环境音检测
//                if(recordMsg.equals(recorder.RECORDER_ENV)){
//                    txv.setText("");
//                    if(audioinforesponse.is_available){
//                        txv.append("result:" + "pass" + "\n");
//                    }
//                    else{
//                        txv.append("result:" + "no pass" + "\n");
//                    }
//                }
            } else {
                Log.d(TAG, "数据错误！");
                Toast.makeText(getContext(), "数据错误！", Toast.LENGTH_LONG).show();
            }
        }
    };

    public void setPageNum(int index) {
        switch (index) {
            case 0:
                regPage1.setBackgroundResource(R.drawable.ic_circle_on);
                regPage2.setBackgroundResource(R.drawable.ic_circle_off);
                regPage3.setBackgroundResource(R.drawable.ic_circle_off);
                break;
            case 1:
                regPage1.setBackgroundResource(R.drawable.ic_circle_off);
                regPage2.setBackgroundResource(R.drawable.ic_circle_on);
                regPage3.setBackgroundResource(R.drawable.ic_circle_off);
                txv1.setText("已成功录制第一段语音");
                txv2.setText("请继续上传语音");
                break;
            case 2:
                regPage1.setBackgroundResource(R.drawable.ic_circle_off);
                regPage2.setBackgroundResource(R.drawable.ic_circle_off);
                regPage3.setBackgroundResource(R.drawable.ic_circle_on);
                txv1.setText("已成功录制第二段语音");
                txv2.setText("请继续上传语音");
                break;
        }
    }
}
