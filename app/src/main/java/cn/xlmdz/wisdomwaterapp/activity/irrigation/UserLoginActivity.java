package cn.xlmdz.wisdomwaterapp.activity.irrigation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.kriston.ai.Identify_Info;
import com.kriston.ai.AudioInfoResponse;
import com.kriston.ai.Recorder;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.base.activity.BaseActivity;
import cn.xlmdz.wisdomwaterapp.fragment.system.EnrollTdFragment;
import cn.xlmdz.wisdomwaterapp.room.entity.User;
import cn.xlmdz.wisdomwaterapp.room.manager.UserManager;
import cn.xlmdz.wisdomwaterapp.utils.DetectService;
import cn.xlmdz.wisdomwaterapp.utils.ShowHelper;
import cn.xlmdz.wisdomwaterapp.view.AudioWaveView;

import static com.kriston.ai.Recorder.TAG;

public class UserLoginActivity extends BaseActivity implements View.OnClickListener {
    private Button mBtnLogin, mBtnVoiceprint;
    private EditText mEtUsername, mEtPassword;
    private AudioWaveView viewWave;

    private static final String fileName = "/td_identify.wav";
    private AudioInfoResponse audioinforesponse;
    private Recorder recorder = null;
    private String recordMsg;
    private int checkmode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);

        initView();
    }

    private void initView() {
        mBtnLogin = findViewById(R.id.btnLogin);
        mBtnVoiceprint = findViewById(R.id.btnLoginForVoiceprint);
        mEtUsername = findViewById(R.id.etUserName);
        mEtPassword = findViewById(R.id.etPassword);

        mBtnLogin.setOnClickListener(this);

        mBtnVoiceprint.setOnClickListener(this);

        recorder = new Recorder();
        recorder.setFinishListener(new recorderListener());
        checkmode = Recorder.ORDINARY_MODE;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String username = mEtUsername.getText().toString().trim();
                String password = mEtPassword.getText().toString().trim();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    Toast.makeText(UserLoginActivity.this, "请填写账号信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = UserManager.getUser(UserLoginActivity.this, username);
                if (user == null) {
                    Toast.makeText(UserLoginActivity.this, "没有该用户信息", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!user.getPassword().equals(password)) {
                    Toast.makeText(UserLoginActivity.this, "密码错误，请检查", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case R.id.btnLoginForVoiceprint:
                AlertDialog.Builder builder = new AlertDialog.Builder(UserLoginActivity.this);
                builder.setTitle("请说话");
                View view = LayoutInflater.from(UserLoginActivity.this).inflate(R.layout.dialog_voiceprint_verify, null);
                viewWave = view.findViewById(R.id.wave_dialog);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        viewWave.stopView(true);
                        recorder.stopRecord();
                    }
                });
                builder.setView(view);
                builder.setCancelable(false);
                AlertDialog dialog = builder.show();
                viewWave.startView();

                String savePath = getCacheDir().getAbsolutePath();
                savePath = savePath + fileName;
                recorder.startRecord(savePath, "td", checkmode, new Recorder.OnDataListener() {
                    @Override
                    public void onData(short[] shorts, int readSize) {
//                    Log.d("Enroll", "Read Size: " + readSize);
                        ShowHelper.showWave(UserLoginActivity.this, viewWave, shorts, readSize);
                    }
                });

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 5000); //5s后停止检测声纹
                break;
        }
    }

    class recorderListener implements Recorder.OnFinishListener {
        @Override
        public void onFinish(String msg, AudioInfoResponse response) {
            recordMsg = msg;
            audioinforesponse = response;
            Log.i(TAG, "录音结束，回调函数被调用");
            runOnUiThread(updateThread);
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
                        Identify_Info info = DetectService.identifyUser(audioinforesponse.recordpath, "td");
                        if (info.ret_code == 0) {
                            if ((info.scores.length > 0) && (info.scores[0].score >= 0.8) && (info.ret_code == 0)) {
                                String speaker = info.scores[0].spkId;
                                float score = info.scores[0].score;
                                Toast.makeText(UserLoginActivity.this, speaker + "：" + score, Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(UserLoginActivity.this, "未找到用户", Toast.LENGTH_LONG).show();
                                Log.e("声纹识别", "未找到用户");
                            }
                            return;
                        } else {
                            Toast.makeText(UserLoginActivity.this, "声纹查找失败", Toast.LENGTH_LONG).show();
                            Log.e("声纹识别", "声纹查找失败");
                        }
                    } else {
                        ShowHelper.showAudioReason(UserLoginActivity.this, audioinforesponse);
                    }
                    viewWave.cleanView();
                    return;
                }
            } else {
                Log.d(TAG, "数据错误！");
                Toast.makeText(UserLoginActivity.this, "数据错误！", Toast.LENGTH_LONG).show();
            }
        }
    };
}