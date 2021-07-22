package cn.xlmdz.wisdomwaterapp.activity.irrigation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.licheedev.modbus4android.ModbusCallback;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;
import com.serotonin.modbus4j.msg.WriteRegistersResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import cn.xlmdz.wisdomwaterapp.R;
import cn.xlmdz.wisdomwaterapp.base.activity.BaseActivity;
import cn.xlmdz.wisdomwaterapp.listener.MyTtsListener;
import cn.xlmdz.wisdomwaterapp.modbus.ModbusManager;
import cn.xlmdz.wisdomwaterapp.room.entity.User;
import cn.xlmdz.wisdomwaterapp.room.manager.UserManager;
import cn.xlmdz.wisdomwaterapp.utils.HexUtil;
import cn.xlmdz.wisdomwaterapp.utils.IEEE754;
import cn.xlmdz.wisdomwaterapp.utils.ModbusUtil;
import cn.xlmdz.wisdomwaterapp.utils.TtsUtil;
import cn.xlmdz.wisdomwaterapp.utils.WakeUtil;

public class IrrigationActivity extends BaseActivity {
    private Button mBtnStop;
    private ImageView mIvIcon;
    private TextView mTvName, mTvTime, mTvUsageTime;
    private LinearLayout mLlStopButton, mLlStopText, mLlLoginTime, mLlUsageTime;
    private EditText mEtStartElec, mEtStartWater, mEtStopElec, mEtStopWater, mEtUsageElec, mEtUsageWater;

    private long mStartTime, mStopTime;
    private SimpleDateFormat smp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private double startElec, startWater, stopElec, stopWater, usageElec, usageWater;
    private int hour, minute, second;

    private User mUser;
    private TtsUtil mTtsUtil;
    private ModbusUtil mModbusUtil = new ModbusUtil();

    private DecimalFormat mDf = new DecimalFormat("#.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irrigation);

        Intent intent = getIntent();
        String userName = intent.getStringExtra("userName");
        if (!TextUtils.isEmpty(userName)) {
            mUser = UserManager.getUser(this, userName);
        }

        mTtsUtil = new TtsUtil(this);

        initView();
        mModbusUtil.openDevice();
        playerTts();
    }

    private void initView() {
        mBtnStop = findViewById(R.id.btnStop);
        mIvIcon = findViewById(R.id.ivWaterPump);
        mTvName = findViewById(R.id.name);
        mTvTime = findViewById(R.id.tvLoginTime);
        mTvUsageTime = findViewById(R.id.tvUsageTime);
        mLlStopButton = findViewById(R.id.llStopButton);
        mLlStopText = findViewById(R.id.llStopText);
        mLlLoginTime = findViewById(R.id.llLoginTime);
        mLlUsageTime = findViewById(R.id.llUsageTime);
        mEtStartElec = findViewById(R.id.etStartElec);
        mEtStartWater = findViewById(R.id.etStartWater);
        mEtStopElec = findViewById(R.id.etStopElec);
        mEtStopWater = findViewById(R.id.etStopWater);
        mEtUsageElec = findViewById(R.id.etUsageElec);
        mEtUsageWater = findViewById(R.id.etUsageWater);

        mTvName.setText(mUser.getName());

        mBtnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    // 开始作业
    private void startIrrigation() {
        mStartTime = System.currentTimeMillis();
        mTvTime.setText(smp.format(mStartTime));

        getElecMeterData(1);
        getWaterMeterData(1);
        relayControl(true);
    }

    // 结束作业
    private void stopIrrigation() {
        mStopTime = System.currentTimeMillis();

        mLlStopButton.setVisibility(View.GONE);
        mLlStopText.setVisibility(View.VISIBLE);
        mLlLoginTime.setVisibility(View.GONE);
        mLlUsageTime.setVisibility(View.VISIBLE);

        long difference = mStopTime - mStartTime;
        hour = (int) (difference / 1000 / 3600);
        minute = (int) (difference / 1000 % 3600) / 60;
        second = (int) (difference / 1000) % 3600 % 60;
        mTvUsageTime.setText(hour + "小时" + minute + "分" + second + "秒");

        mIvIcon.setImageResource(R.drawable.water_pump2);
    }

    @Override
    protected void onStop() {
        super.onStop();
        WakeUtil.stopWakeuper();
    }

    private void playerTts() {
        if (mUser != null) {
            String text = mUser.getName() + "你好，欢迎使用智慧水务。开始作业";
            mTtsUtil.playerTts(text, new MyTtsListener() {

                @Override
                public void onCompleted(SpeechError speechError) {
                    if (speechError == null) {
                        Log.e("科大讯飞语音合成：", "播放完成");
                    } else if (speechError != null) {
                        Log.e("科大讯飞语音合成：", "播放错误：" + speechError.getPlainDescription(true));
                    }

                    startIrrigation();
                    WakeUtil.startWakeuper(mWakeuperListener);
                }
            });
        }
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
                Toast.makeText(IrrigationActivity.this, "检测到唤醒：" + id, Toast.LENGTH_SHORT).show();
                if (id == 1) {
                    // TODO 结束作业
                    stopIrrigation();
                    relayControl(false);
                    getElecMeterData(2);
                    getWaterMeterData(2);
                }
            } catch (JSONException e) {
                Toast.makeText(IrrigationActivity.this, "唤醒词解析出错", Toast.LENGTH_SHORT).show();
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

    private void playStopTts() {
        StringBuffer sb = new StringBuffer();
        sb.append("感谢使用智慧水务，本次作业");
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分钟");
        }
        sb.append(second + "秒");
        sb.append("，用电" + usageElec + "度，用水" + usageWater + "吨，十五秒后退出");
        mTtsUtil.playerTts(sb.toString(), new MyTtsListener() {

            @Override
            public void onCompleted(SpeechError speechError) {
                if (speechError == null) {
                    Log.e("科大讯飞语音合成：", "播放完成，15S后退出");

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 15000); //15s自动返回首页
                } else if (speechError != null) {
                    Log.e("科大讯飞语音合成：", "播放错误：" + speechError.getPlainDescription(true));
                }
            }
        });
    }

    private void getWaterMeterData(int type) {
        int mCableSalveId = 0x01; //设备地址
        int mCableOffset = 0x08; //寄存器地址
        int mCableAmount = 0x04; //寄存器数量

        ModbusManager.get().readHoldingRegisters(mCableSalveId, mCableOffset, mCableAmount,
                new ModbusCallback<ReadHoldingRegistersResponse>() {
                    @Override
                    public void onSuccess(ReadHoldingRegistersResponse readHoldingRegistersResponse) {
                        byte[] data = readHoldingRegistersResponse.getData();

                        byte[] height = new byte[]{data[2], data[3]};
                        byte[] low = new byte[]{data[0], data[1]};
                        byte[] decimalBytes = new byte[]{data[6], data[7], data[4], data[5]};
                        int heightInt = HexUtil.ByteArray2Int(height, false) << 16;
                        int lowInt = HexUtil.ByteArray2Int(low, false);
                        double decimal = IEEE754.hex2FloatIeee(decimalBytes);
                        double value = Double.parseDouble(mDf.format(((double) (heightInt + lowInt) + decimal) / 10.0));
                        Log.e("modbus", "水表流量：" + value);
                        if (type == 1) {
                            startWater = value;
                            mEtStartWater.setText("" + startWater);
                        } else if (type == 2) {
                            stopWater = value;
                            mEtStopWater.setText("" + stopWater);
                            usageWater = stopWater - startWater;
                            mEtUsageWater.setText("" + usageWater);
                        }
                    }

                    @Override
                    public void onFailure(Throwable tr) {
                        Log.d("Modbus：", "Modbus读取失败：" + tr + "\n");
                    }

                    @Override
                    public void onFinally() {
                        if (type == 2) {
                            playStopTts();
                        }
                    }
                });
    }

    //0210002A0001020007
    //0210002A0001020000
    private void relayControl(boolean isOpen) {
        int salveId = 0x02; //设备地址
        int start = 0x002A; //寄存器开始地址
        int value = 0x0007; //0x0007拉闸 0x0000合闸

        if (!isOpen) {
            value = 0x0000;
        }

        ModbusManager.get().writeRegistersButOne(salveId, start, value, new ModbusCallback<WriteRegistersResponse>() {
            @Override
            public void onSuccess(WriteRegistersResponse writeRegistersResponse) {

            }

            @Override
            public void onFailure(Throwable tr) {

            }

            @Override
            public void onFinally() {

            }
        });
    }

    private void getElecMeterData(int type) {
        int mCableSalveId = 0x02; //设备地址
        int mCableOffset = 0x101E; //寄存器地址
        int mCableAmount = 0x02; //寄存器数量

        ModbusManager.get().readHoldingRegisters(mCableSalveId, mCableOffset, mCableAmount,
                new ModbusCallback<ReadHoldingRegistersResponse>() {
                    @Override
                    public void onSuccess(ReadHoldingRegistersResponse readHoldingRegistersResponse) {
                        byte[] data = readHoldingRegistersResponse.getData();

                        double elec = Double.parseDouble(mDf.format(IEEE754.hex2FloatIeee(data)));

                        if (type == 1) {
                            startElec = elec;
                            mEtStartElec.setText("" + startElec);
                        } else {
                            stopElec = elec;
                            mEtStopElec.setText("" + stopElec);
                            usageElec = stopElec - startElec;
                            mEtUsageWater.setText("" + usageElec);
                        }

                        Log.e("modbus", "电表读数：" + elec);
                    }

                    @Override
                    public void onFailure(Throwable tr) {
                        Log.d("Modbus：", "Modbus读取失败：" + tr + "\n");
                    }

                    @Override
                    public void onFinally() {

                    }
                });
    }

    @Override
    public void onBackPressed() {

    }
}