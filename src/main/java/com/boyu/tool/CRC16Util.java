package com.boyu.tool;

public class CRC16Util {

    //一个字节包含位的数量 8
    private static final int BITS_OF_BYTE = 8;

    //多项式
    private static final int POLYNOMIAL = 0xA001;

    //初始值
    private static final int INITIAL_VALUE = 0xFFFF;

    public static String getCRC16(String data) {
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
        return getCRC16(para);
    }

    /**
     * 计算CRC16校验码
     *
     * @param bytes 字节数组
     * @return 校验码
     */
    public static String getCRC16(byte[] bytes) {
        // CRC寄存器全为1
        int CRC = 0x0000ffff;
        // 多项式校验值
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
        // 结果转换为16进制
        String result = Integer.toHexString(CRC).toUpperCase();
        if (result.length() != 4) {
            StringBuffer sb = new StringBuffer("0000");
            result = sb.replace(4 - result.length(), 4, result).toString();
        }
        //String str = result.substring(0, 2)+result.substring(2, 4);
        return result;
    }

    public static int getCRC16Int(byte[] bytes){
        // CRC寄存器全为1
        int CRC = 0x0000ffff;
        // 多项式校验值
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
        int lowByte = (CRC & 0x0000ff00) >> 8;
        int highByte = (CRC & 0x000000ff) << 8;
        return lowByte | highByte;
    }

    public static String formatWithMakingUp(String src) {
        String formatstr="0000";
        int delta = formatstr.length() - src.length();
        if (delta <= 0) {
            return src;
        }
        return formatstr.substring(0, delta) + src;
    }
}
