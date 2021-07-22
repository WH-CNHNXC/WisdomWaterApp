package cn.xlmdz.wisdomwaterapp.base.application;

import android.app.Application;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.serotonin.modbus4j.ModbusConfig;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 科大讯飞声纹识别SDK初始化
        // 将“12345678”替换成您申请的APPID，申请地址：http://www.xfyun.cn
        // 请勿在“=”与appid之间添加任何空字符或者转义符
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=545724bc");
        configModbus();
    }

    /**
     * 配置Modbus,可选
     */
    private void configModbus() {
        // 启用rtu的crc校验（默认就启用）
        ModbusConfig.setEnableRtuCrc(true);
        // 打印数据log（默认全禁用）
        // System.out: MessagingControl.send: 01030000000305cb
        // System.out: MessagingConnection.read: 010306000100020000bd75
        ModbusConfig.setEnableDataLog(true, true);
    }
}
