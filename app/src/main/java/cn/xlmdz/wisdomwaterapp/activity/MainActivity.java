package cn.xlmdz.wisdomwaterapp.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.kriston.ai.Recorder;

import org.json.JSONException;
import org.json.JSONObject;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.activity.irrigation.UserLoginActivity;
import cn.xlmdz.wisdomwaterapp.activity.irrigation.UserVoiceLoginActivity;
import cn.xlmdz.wisdomwaterapp.activity.system.AdminLoginActivity;
import cn.xlmdz.wisdomwaterapp.base.activity.BaseActivity;
import cn.xlmdz.wisdomwaterapp.room.entity.User;
import cn.xlmdz.wisdomwaterapp.room.manager.UserManager;
import cn.xlmdz.wisdomwaterapp.utils.DetectService;
import cn.xlmdz.wisdomwaterapp.utils.WakeUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTvIrr, mTvSystem;

    private static final int mRequestCode = 13;
    //声明一个数组permissions，将所有需要申请的权限都放在里面
    String[] permissions = new String[]{android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initPermission();
        int ret = DetectService.initKvp(this);
        if (ret != 0) {
            new AlertDialog.Builder(this)
                    .setTitle("错误")
                    .setMessage("初始化声纹引擎失败[" + String.valueOf(ret) + "]")
                    .create()
                    .show();
        }
        Recorder.init(this);

        // 初始化唤醒对象
        WakeUtil.initIntance(this, null);

        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();

        WakeUtil.startWakeuper(mWakeuperListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        WakeUtil.stopWakeuper();
    }

    private void initView() {
        mTvIrr = findViewById(R.id.tvIrrigation);
        mTvSystem = findViewById(R.id.tvSystem);
        mTvIrr.setOnClickListener(this);
        mTvSystem.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvIrrigation:
                startActivity(UserVoiceLoginActivity.class);
                break;
            case R.id.tvSystem:
                startActivity(AdminLoginActivity.class);
                break;
        }
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, mRequestCode);
        }
    }

    private void startActivity(Class<?> clazz) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onResult(WakeuperResult wakeuperResult) {
            try {
                String text = wakeuperResult.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                int id = object.optInt("id");
                Toast.makeText(MainActivity.this, "检测到唤醒：" + id, Toast.LENGTH_SHORT).show();
                if (id == 0) {
                    startActivity(UserVoiceLoginActivity.class);
                }
            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "唤醒词解析出错", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }

        @Override
        public void onVolumeChanged(int i) {

        }
    };
}