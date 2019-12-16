package leqi.com.leqipassportsdk.comm;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import leqi.com.leqipassportsdk.AuthCode;
import leqi.com.leqipassportsdk.PlatformConfig;
import leqi.com.leqipassportsdk.mUserInfo;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;
/**

 */
public class Utils {
    public void initUserInfo() {
        mUserInfo.UserID ="";
        mUserInfo.LoginType =0;

    }
    public static Map<String, String> jsonToMap(JSONObject val) {
        HashMap map = new HashMap();

        Iterator iterator = val.keys();

        while(iterator.hasNext()) {
            String var4 = (String)iterator.next();
            map.put(var4, val.opt(var4) + "");
        }

        return map;
    }

    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static String getEncodeKey(String afstr) {
        //return PlatformConfig.appKey;
        //return AuthCode.authcodeEncode(PlatformConfig.appKey, PlatformConfig.authCode,60);

        return AuthCode.authcode(afstr, PlatformConfig.authCode, 60);
    }


    public static boolean isMobileNO(String mobiles) {
    /*
    移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
    联通：130、131、132、152、155、156、185、186
    电信：133、153、180、189、（1349卫通）
    ali：170
    总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
    */
        String telRegex = "[1][3456789]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public  static boolean isPass(String spass){


        String pwdRegex ="^[a-zA-Z0-9]{6,12}$";
        if (TextUtils.isEmpty( spass )) return false;
        else return spass.matches( pwdRegex );

    }

    public static boolean isVerify(String str){

        String pwdRegex ="^\\d{4,6}$";
        if (TextUtils.isEmpty( str )) return false;
        else return str.matches( pwdRegex );
    }

    /**
     * 验证输入的身份证号是否合法
     */
    public static boolean isLegalId(String id){
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")){
            return true;
        }else {
            return false;
        }
    }
    /**
     * 验证输入的名字是否为“中文”或者是否包含“·”
     */
    public static boolean isLegalName(String name){
        if (name.contains("·") || name.contains("•")){
            if (name.matches("^[\\u4e00-\\u9fa5]+[·•][\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }else {
            if (name.matches("^[\\u4e00-\\u9fa5]+$")){
                return true;
            }else {
                return false;
            }
        }
    }


    public static String PhPMD5(String data)
    {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        }
        catch (NoSuchAlgorithmException e) {
            return "";
        }
        catch (UnsupportedEncodingException e){
            return "";
        }
    }




    // 生成签名
    public static String generateSignature(Map<String, String> data)
    {
        Set<String> keySet = data.keySet();
        String[] keyArray = keySet.toArray(new String[keySet.size()]);
        Arrays.sort(keyArray);
        StringBuilder str = new StringBuilder();
        for (String k : keyArray) {
            if (data.get(k).trim().length() > 0)
                str.append(k).append("=").append(data.get(k).trim()).append("&");
        }
        Log.i("hkl", str.toString());
        return PhPMD5(PhPMD5(str.toString().replaceAll("\\&$", "")).substring(8, 24) + PhPMD5("mafia"));
    }
}
