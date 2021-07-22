package cn.xlmdz.wisdomwaterapp.utils;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

public class HexUtil {

    /**
     * 16进制数字字符集
     */
    private static String hexString = "0123456789ABCDEF";

    /**
     * 转化字符串为十六进制编码
     */
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 转化十六进制编码为字符串
     */
    public static String toStringHex(String hexStr) {
        String str = "";
        byte[] baKeyword = new byte[hexStr.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(hexStr.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            str = new String(baKeyword, "utf-8");//UTF-16le:Not
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return str;
    }

    /**
     * 将字符串编码成16进制数字,适用于所有字符（包括中文）
     */
    public static String encode(String str) {
        //根据默认编码获取字节数组
        byte[] bytes = str.getBytes();
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        //将字节数组中每个字节拆解成2位16进制整数
        for (int i = 0; i < bytes.length; i++) {
            sb.append(hexString.charAt((bytes[i] & 0xf0) >> 4));
            sb.append(hexString.charAt((bytes[i] & 0x0f) >> 0));
        }
        return sb.toString();
    }

    /**
     * 将16进制数字解码成字符串,适用于所有字符（包括中文）
     */
    public static String decode(String bytes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length() / 2);
        //将每2位16进制整数组装成一个字节
        for (int i = 0; i < bytes.length(); i += 2)
            baos.write((hexString.indexOf(bytes.charAt(i)) << 4 | hexString.indexOf(bytes.charAt(i + 1))));
        return new String(baos.toByteArray());
    }

    /**
     * 将字符串转换成16进制编码字符串
     * <p>Title: ToHexString</p>
     * <p>Description: </p>
     *
     * @param str
     * @return
     */
    public static String ToHexString(String str) {
        String str1 = "";
        try {
            byte[] b = str.getBytes("gbk");
            int i = 0;
            int max = b.length;
            for (; i < max; i++) {
                str1 += String.format("%02x",b[i] & 0xFF);
            }
        } catch (UnsupportedEncodingException e) {
            System.out.println("异常信息ToHexString" + e.getMessage());
        }
        return str1;
    }

    /**
     * 16进制编码字符串转字节数组
     * <p>Title: toByteArray</p>
     * <p>Description: </p>
     *
     * @param hexString
     * @return
     */
    public static byte[] toByteArray(String hexString) {
        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {// 因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    /**
     * 字节数组转16进制编码字符串
     * <p>Title: toHexString</p>
     * <p>Description: </p>
     *
     * @param byteArray
     * @return
     */
    public static String toHexString(byte[] byteArray) {
        String str = null;
        if (byteArray != null && byteArray.length > 0) {
            StringBuffer stringBuffer = new StringBuffer(byteArray.length);
            for (byte byteChar : byteArray) {
                stringBuffer.append(String.format("%02X", byteChar));
            }
            str = stringBuffer.toString();
        }
        return str;
    }

    /**
     * 字节数组转字符串
     * <p>Title: ByteArrayDecode</p>
     * <p>Description: </p>
     *
     * @param bytes
     * @return
     */
    public static String ByteArray2String(byte[] bytes) {
        String hexStr = toHexString(bytes);
        return decode(hexStr);
    }

    /**
     * 字节数组转int
     * @param buf
     * @param asc 是否高位在后
     * @return
     */
    public final static int ByteArray2Int(byte[] buf, boolean asc) {
        int len = buf.length;
        if (buf == null) {
            throw new IllegalArgumentException("byte array is null!");
        }
        if (len > 4) {
            throw new IllegalArgumentException("byte array size > 4 !");
        }
        int r = 0;
        if (asc)
            for (int i = len - 1; i >= 0; i--) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        else
            for (int i = 0; i < len; i++) {
                r <<= 8;
                r |= (buf[i] & 0x000000ff);
            }
        return r;
    }

    /**
     * 计算CRC16校验码
     *
     * @param data 需要校验的字符串
     * @return 校验码
     */
    public static String getCRC(String data) {
        data = data.replace(" ", "");
        int len = data.length();
        if (!(len % 2 == 0)) {
            return "0000";
        }
        int num = len / 2;
        byte[] para = new byte[num];
        for (int i = 0; i < num; i++) {
            int value = Integer.valueOf(data.substring(i * 2, 2 * (i + 1)), 16);
            para[i] = (byte) value;
        }
        return getCRC(para);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return {@link String} 校验码
     * @since 1.0
     */
    public static String getCRC(byte[] bytes) {
        //CRC寄存器全为1
        int CRC = 0x0000ffff;
        //多项式校验值
        int POLYNOMIAL = 0x0000a001;
        int i, j;
        for (i = 0; i < bytes.length; i++) {
            CRC ^= ((int) bytes[i] & 0x000000ff);
            for (j = 0; j < 8; j++) {
                if ((CRC & 0x00000001) != 0) {
                    CRC >>= 1;
                    CRC ^= POLYNOMIAL;
                } else {
                    CRC >>= 1;
                }
            }
        }
        //结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        //交换高低位
        return result.substring(2, 4) + result.substring(0, 2);
    }
}
