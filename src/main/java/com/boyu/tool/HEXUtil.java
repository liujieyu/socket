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
     * 16进制转换成为string类型字符串
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }
    /**
     * 解析观测时间(或者发报时间)
     * @param bytes
     * @return
     */
    public static String bytesToDatetime(byte[] bytes){
        StringBuilder sb = new StringBuilder();
        sb.append("20");
        sb.append(Integer.toHexString(0xFF & bytes[0]));
        sb.append("-");
        String mon=Integer.toHexString(0xFF & bytes[1]);
        if(mon.length()==1){
            sb.append("0");
        }
        sb.append(mon);
        sb.append("-");
        String day=Integer.toHexString(0xFF & bytes[2]);
        if(day.length()==1){
            sb.append("0");
        }
        sb.append(day);
        sb.append(" ");
        String hour=Integer.toHexString(0xFF & bytes[3]);
        if(hour.length()==1){
            sb.append("0");
        }
        sb.append(hour);
        sb.append(":");
        String min=Integer.toHexString(0xFF & bytes[4]);
        if(min.length()==1){
            sb.append("0");
        }
        sb.append(min);
        if(bytes.length==5){
            sb.append(":00");
        }else{
            sb.append(":");
            String second=Integer.toHexString(0xFF & bytes[5]);
            if(second.length()==1){
                sb.append("0");
            }
            sb.append(second);
        }

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
                frianArray[i]=new BigDecimal(((double)Integer.parseInt(frainstr,16))/10).setScale(1,BigDecimal.ROUND_HALF_UP);
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
            int j=i/2;
            StringBuilder sb = new StringBuilder();
            String num1=Integer.toHexString(0xFF & bytes[i]);
            if(num1.length()==1){
                sb.append("0");
            }
            sb.append(num1);
            String num2=Integer.toHexString(0xFF & bytes[i+1]);
            if(num2.length()==1){
                sb.append("0");
            }
            sb.append(num2);
            String frsvr=sb.toString();
            if(frsvr.toUpperCase().equals("FFFF")){
                frsvrArray[j]=new BigDecimal(-1);
                sign++;
            }else{
                frsvrArray[j]=new BigDecimal(((double)(Integer.parseInt(frsvr,16)))/100).setScale(2,BigDecimal.ROUND_HALF_UP);
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
            int j=i/4;
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
            BigDecimal val=new BigDecimal(Double.parseDouble(valstr)/1000).setScale(3,BigDecimal.ROUND_HALF_UP);
            int j=i/4;
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
                val=new BigDecimal((Double.parseDouble(valstr)/100)*-1).setScale(2,BigDecimal.ROUND_HALF_UP);
            }else{
                val=new BigDecimal(Double.parseDouble(valstr)/100).setScale(2,BigDecimal.ROUND_HALF_UP);
            }
            int j=i/5;
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
            BigDecimal val=new BigDecimal(Double.parseDouble(valstr)/1000000).setScale(6,BigDecimal.ROUND_HALF_UP);
            int j=i/count;
            valarray[j]=val;
        }
        return valarray;
    }
}
