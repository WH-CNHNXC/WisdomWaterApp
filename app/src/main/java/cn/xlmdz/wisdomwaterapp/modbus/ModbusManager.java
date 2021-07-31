package cn.xlmdz.wisdomwaterapp.modbus;

import com.licheedev.modbus4android.ModbusCallback;
import com.licheedev.modbus4android.ModbusParam;
import com.licheedev.modbus4android.ModbusWorker;
import com.licheedev.modbus4android.param.SerialParam;
import com.serotonin.modbus4j.ModbusMaster;

public class ModbusManager extends ModbusWorker {

    private static volatile ModbusManager sInstance;

    private int REQUEST_TIMEOUT = 1000;
    private String mDevicePaths = "/dev/ttyS2";
    private int mBaudrates = 9600;
    private int mDataBits = 8;
    private int mStopBits = 1;

    public static ModbusManager get() {
        ModbusManager manager = sInstance;
        if (manager == null) {
            synchronized (ModbusManager.class) {
                manager = sInstance;
                if (manager == null) {
                    manager = new ModbusManager();
                    sInstance = manager;
                }
            }
        }
        return manager;
    }

    private ModbusManager() {
    }

    /**
     * 释放整个ModbusManager，单例会被置null
     */
    public synchronized void release() {
        super.release();
        sInstance = null;
    }

    /**
     * 初始化modbus
     *
     * @param callback
     */
    public synchronized void initModbus(ModbusCallback<ModbusMaster> callback) {

        if (isModbusOpened()) {
            // 先把原来的关掉
            closeModbusMaster();
            return;
        }

        // 串口
        ModbusParam serialParam = SerialParam.create(mDevicePaths, mBaudrates)
                .setTimeout(REQUEST_TIMEOUT) //超时时间
                .setDataBits(mDataBits)
                .setStopBits(mStopBits)
                .setRetries(0); // 不重试

        init(serialParam, callback);
    }
}
