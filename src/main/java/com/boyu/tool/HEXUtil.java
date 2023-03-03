package com.boyu.tool;

import java.math.BigDecimal;

public class HEXUtil {
    /**
     * 返回十六进制字符串的十进制整数表示 */
    public static int hex2Dec(String hexString) {
        return Integer.parseInt(hexString,16);
    }
    /**
     * 十进制数转十六进制字符串
     */
    public static String dec2Hex(int decimal){
        return String.format("%08x", decimal);
    }
    /**
     * 16进制表示的字符串转换为字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }
    /**
     * 字节组转16进制字符串
     * @param bytes
     * @return
     */
    public static String bytesToHexString(byte[] bytes,boolean uppersign) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        if(uppersign){
            return sb.toString().toUpperCase();
        }else{
            return sb.toString();
        }
    }

    /**
     * 解析观测时间
     * @param bytes
     * @return
     */
    public static String bytesToDatetime(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        sb.append("20");
        sb.append(Integer.toHexString(0xFF & bytes[0]));
        sb.append("-");
        sb.append(Integer.toHexString(0xFF & bytes[1]));
        sb.append("-");
        sb.append(Integer.toHexString(0xFF & bytes[2]));
        sb.append(" ");
        sb.append(Integer.toHexString(0xFF & bytes[3]));
        sb.append(":");
        sb.append(Integer.toHexString(0xFF & bytes[4]));
        sb.append(":00");
        return sb.toString();
    }

    /**
     * 解析12个5分钟降雨量
     * @param bytes
     * @return
     */
    public static BigDecimal[] bytesToFiverain(byte[] bytes){
        int sign=0;
        BigDecimal[] frianArray=new BigDecimal[12];
        for(int i=0;i<bytes.length;i++){
            String frainstr=Integer.toHexString(0xFF & bytes[i]);
            if(frainstr.toUpperCase().equals("FF")){
                frianArray[i]=new BigDecimal(-1);
                sign++;
            }else{
                frianArray[i]=new BigDecimal(Integer.parseInt(frainstr)/10);
            }
        }
        if(sign==12){
            return new BigDecimal[1];
        }else {
            return frianArray;
        }
    }

    /**
     * 解析12个5分钟水位
     * @param bytes
     * @return
     */
    public static BigDecimal[] bytesToFiverrsvr(byte[] bytes){
        int sign=0;
        BigDecimal[] frsvrArray=new BigDecimal[12];
        for(int i=0;i<bytes.length;i+=2){
            int j;
            if(i==0){
                j=0;
            }else{
                j=i-1;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(Integer.toHexString(0xFF & bytes[i]));
            sb.append(Integer.toHexString(0xFF & bytes[i+1]));
            String frsvr=sb.toString();
            if(frsvr.toUpperCase().equals("FFFF")){
                frsvrArray[j]=new BigDecimal(-1);
                sign++;
            }else{
                frsvrArray[j]=new BigDecimal(Integer.parseInt(frsvr)/100);
            }
        }
        if(sign==12){
            return new BigDecimal[1];
        }else {
            return frsvrArray;
        }
    }

    /**
     * 获取大坝安全测点编号
     * @param bytes
     * @return
     */
    public static String[] bytesToSpprCd(byte[] bytes){
        int sign=0;
        String[] mpcdArray=new String[bytes.length/4];
        for (int i=0;i<bytes.length;i+=4){
            byte[] mpcdbyte=new byte[4];
            System.arraycopy(bytes, i, mpcdbyte, 0, 4);
            String mpcd=bytesToHexString(mpcdbyte,false);
            if(mpcd.equals("00000000")){
                sign++;
            }
            int j=i==0?0:i-4;
            mpcdArray[j]=mpcd;
        }
        if(sign==bytes.length/4){
            return new String[1];
        }else{
            return mpcdArray;
        }
    }

    /**
     * 获取大坝安全测点值：渗压水位，渗流，高程
     * @param bytes
     * @return
     */
    public static BigDecimal[] bytesToSafeval(byte[] bytes){
        BigDecimal[] valArray=new BigDecimal[bytes.length/4];
        for(int i=0;i<bytes.length;i+=4){
            byte[] valbyte=new byte[4];
            System.arraycopy(bytes, i, valbyte, 0, 4);
            String valstr=bytesToHexString(valbyte,false);
            BigDecimal val=new BigDecimal(Double.parseDouble(valstr)/1000);
            int j=i==0?0:i-4;
            valArray[j]=val;
        }
        return valArray;
    }

    /**
     * 获取大坝安全测点值：X向位移，Y向位移，垂直位移
     * @param bytes
     * @return
     */
    public static BigDecimal[] bytesToHRval(byte[] bytes){
        BigDecimal[] valarray=new BigDecimal[bytes.length/5];
        for(int i=0;i<bytes.length;i+=5){
            byte[] valbyte=new byte[4];
            System.arraycopy(bytes,i+1,valbyte,0,4);
            String valstr=bytesToHexString(valbyte,false);
            String sign=Integer.toHexString(0xFF & bytes[i]).toUpperCase();
            BigDecimal val;
            if(sign.equals("FF")){
                val=new BigDecimal((Double.parseDouble(valstr)/100)*-1);
            }else{
                val=new BigDecimal(Double.parseDouble(valstr)/100);
            }
            int j=i==0?0:i-5;
            valarray[j]=val;
        }
        return valarray;
    }

    /**
     * 解析经度和纬度
     * @param bytes
     * @param count
     * @return
     */
    public static BigDecimal[] bytesToEslg(byte[] bytes,int count){
        BigDecimal[] valarray=new BigDecimal[bytes.length/count];
        for(int i=0;i<bytes.length;i+=count){
            byte[] valbyte=new byte[count];
            System.arraycopy(bytes,i,valbyte,0,count);
            String valstr=bytesToHexString(valbyte,false);
            BigDecimal val=new BigDecimal(Double.parseDouble(valstr)/1000000);
            int j=i==0?0:count;
            valarray[j]=val;
        }
        return valarray;
    }
}
