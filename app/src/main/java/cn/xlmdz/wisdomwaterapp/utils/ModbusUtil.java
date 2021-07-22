package cn.xlmdz.wisdomwaterapp.utils;

import android.util.Log;

import com.licheedev.modbus4android.ModbusCallback;
import com.serotonin.modbus4j.ModbusMaster;
import com.serotonin.modbus4j.msg.ReadHoldingRegistersResponse;

import cn.xlmdz.wisdomwaterapp.modbus.ModbusManager;

public class ModbusUtil {

    public void openDevice() {
        if (ModbusManager.get().isModbusOpened()) {
            return;
        }

        ModbusManager.get().initModbus(new ModbusCallback<ModbusMaster>() {
            @Override
            public void onSuccess(ModbusMaster modbusMaster) {
                Log.d("Modbus：","Modbus串口打开成功");
            }

            @Override
            public void onFailure(Throwable tr) {
                Log.d("Modbus：","Modbus串口打开失败");
            }

            @Override
            public void onFinally() {

            }
        });
    }

    public void closeDevice() {
        if (ModbusManager.get().isModbusOpened()) {
            // 关闭设备
            ModbusManager.get().closeModbusMaster();
        }
    }

    public boolean isModbusOpened() {
        return ModbusManager.get().isModbusOpened();
    }

    /**
     * 获取传感器的状态
     */
    public void getSensorData() {
        int mCableSalveId = 0x01; //设备地址
        int mCableOffset = 0x08; //寄存器地址
        int mCableAmount = 0x02; //寄存器数量

        ModbusManager.get().readHoldingRegisters(mCableSalveId, mCableOffset, mCableAmount,
                new ModbusCallback<ReadHoldingRegistersResponse>() {
                    @Override
                    public void onSuccess(ReadHoldingRegistersResponse readHoldingRegistersResponse) {
                        byte[] data = readHoldingRegistersResponse.getData();

                        byte[] height = new byte[]{data[2],data[3]};
                        byte[] low = new byte[]{data[0],data[1]};
                        int heightInt = HexUtil.ByteArray2Int(height,false) << 16;
                        int lowInt = HexUtil.ByteArray2Int(low,false);
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
}
