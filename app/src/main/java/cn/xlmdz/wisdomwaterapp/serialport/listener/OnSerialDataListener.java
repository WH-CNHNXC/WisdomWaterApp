package cn.xlmdz.wisdomwaterapp.serialport.listener;

public interface OnSerialDataListener {
    /**
     * Data sent by serial port
     */
    void onSend(String hexData);

    /**
     * Data received by serial port
     */
    void onReceive(String hexData);

    /**
     * Data received by serial port (return complete hex string)
     */
    void onReceiveFullData(String hexData);
}
