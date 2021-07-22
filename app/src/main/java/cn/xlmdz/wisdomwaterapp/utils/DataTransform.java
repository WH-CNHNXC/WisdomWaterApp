package cn.xlmdz.wisdomwaterapp.utils;

public class DataTransform {
    //https://www.iteye.com/blog/aub-1129228
    //https://github.com/FirebirdSQL/decimal-java

    /**
     * 用于建立十六进制字符的输出的小写字符数组
     */
    private static final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 用于建立十六进制字符的输出的大写字符数组
     */
    private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    /**
     * @Desc: 字节转十六进制字符串
     * @Author: Aries.hu
     * @Date: 2021/2/3 11:19
     */
    public static String byteToHex(byte b) {
        String hex = Integer.toHexString(b & 0xFF);
        if (hex.length() < 2) {
            hex = "0" + hex;
        }
        return hex;
    }

    /**
     * @Desc: 字节数组转字符串
     * @Author: Aries.hu
     * @Date: 2021/2/3 11:19
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuffer = new StringBuilder();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if (hex.length() < 2) {
                sbuffer.append(0);
            }
            sbuffer.append(hex);
        }
        return sbuffer.toString();
    }

    /**
     * @Desc: 数组倒序
     * @Author: Aries.hu
     * @Date: 2021/2/3 15:15
     */
    public static byte[] bytesArrayReverse(byte[] src){
        byte temp = 0;
        int len = src.length;
        for (int i = 0; i < len / 2; i++) {
            temp = src[i];
            src[i] = src[len - i - 1];
            src[len - i - 1] = temp;
        }

        return src;
    }

    /**
     * @Desc: byte数组转换为二进制字符串,每个字节以","隔开
     * @Author: Aries.hu
     * @Date: 2021/2/3 15:47
     */
    public static String byteArrToBinStr(byte[] b) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            result.append(Long.toString(b[i] & 0xff, 2) + ",");
        }
        return result.toString().substring(0, result.length() - 1);
    }

    /**
     * @Desc: 二进制字符串转换为byte数组,8位一个字节
     * @Author: Aries.hu
     * @Date: 2021/2/3 15:48
     */
    public static byte[] binStrToByteArr(String binStr) {

        if("".equals(binStr)){
            return new byte[0];
        }

        final int bits = 8;
        int str_len = 0;
        int byte_len = 0;

        StringBuilder binStrBuilder = new StringBuilder(binStr);
        while (binStrBuilder.length() < 32){
            binStrBuilder.append("0");
        }
        binStr = binStrBuilder.toString();
        str_len = binStr.length();

        System.out.println(binStr);

        if(str_len % bits == 0){
            byte_len = str_len / bits;
        }

        byte[] b = new byte[byte_len];
        int index = 0;

        for(int i=0;i<str_len;i+=bits){
            String s = binStr.substring(i, i+bits);
            System.out.println(s);
            b[index] = bit2byte(s);
            index++;
        }

        return b;
    }

    protected static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    /**
     * @Desc: 字节数组转十六进制字符串
     * @Author: Aries.hu
     * @Date: 2021/2/5 11:48
     */
    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    /**
     * @Desc: 将字节数组转换为十六进制字符数组
     * @Author: Aries.hu
     * @Date: 2021/2/5 11:47
     */
    protected static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    public static byte bit2byte(String bString){
        byte result=0;
        for(int i=bString.length()-1,j=0;i>=0;i--,j++){
            result+=(Byte.parseByte(bString.charAt(i)+"")*Math.pow(2, j));
        }
        return result;
    }

    /**
     * @Desc: 字节数组转二进制字符串
     * @Author: Aries.hu
     * @Date: 2021/2/3 10:24
     */
    public static String bytes2BinaryStr(byte[] bytes){
        StringBuilder binaryStr = new StringBuilder();
        for (byte aByte : bytes) {
            String str = Integer.toBinaryString((aByte & 0xFF) + 0x100).substring(1);
            binaryStr.append(str);
        }
        return binaryStr.toString();
    }

    /**
     * @Desc: int转byte数组
     * @Author: Aries.hu
     * @Date: 2021/2/5 21:21
     */
    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }
}
