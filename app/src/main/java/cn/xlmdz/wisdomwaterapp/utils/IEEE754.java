package cn.xlmdz.wisdomwaterapp.utils;

import android.annotation.SuppressLint;

public class IEEE754 {

    /**
     * @Desc: IEEE754标准（四字节转浮点数）,公式转换
     * @Author: Aries.hu
     * @Date: 2021/2/3 10:14
     */
    public static float hex2FloatIeee(byte[] hex){
        String hexStr = DataTransform.bytesToHex(hex);
        StringBuilder binaryStr = new StringBuilder();
        for(int i=0;i< hexStr.length();i+=2){
            String a = hexStr.substring(i,i+2);
            int c = Integer.parseInt(a,16);
            @SuppressLint("DefaultLocale") String item = String.format("%08d",Integer.parseInt(Integer.toBinaryString(c)));
            binaryStr.append(item);
        }
        int n =  Integer.parseInt(binaryStr.substring(1,9),2);
        String mStr = binaryStr.substring(9,binaryStr.length()-1);
        double sum = 0;
        for(int i =1;i<=mStr.length();i++){
            if(mStr.charAt(i-1)=='1'){
                sum = sum+Math.pow(0.5,i);
            }
        }
        float v = (float) ((Math.pow(2, n - 127)) * (1 + sum));
        return v;
    }

    /**
     * @Desc: IEEE754标准（四字节转浮点数）调用JDK接口转换
     * @Author: Aries.hu
     * @Date: 2021/2/5 10:23
     */
    public static float hex2floatIeeeApi(byte[] bytes, boolean reverse){
        if(reverse){
            bytes = DataTransform.bytesArrayReverse(bytes);
        }
        String hex= DataTransform.encodeHexStr(bytes, true);
        return Float.intBitsToFloat(Integer.valueOf(hex, 16));
    }

    /**
     * @Desc: IEEE754标准（浮点数转四字节）调用JDK接口转换
     * @Author: Aries.hu
     * @Date: 2021/2/5 11:57
     */
    public static byte[] float2hexIeeeApi(float f){
        int intBits = Float.floatToRawIntBits(f);
        return DataTransform.intToByteArray(intBits);
    }

}
