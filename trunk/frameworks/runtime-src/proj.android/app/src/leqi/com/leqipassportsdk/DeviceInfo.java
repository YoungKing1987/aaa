package leqi.com.leqipassportsdk;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import leqi.com.leqipassportsdk.openudid.OpenUDID_manager;


/**
 * Created by hkl on 2017/4/20.
 */

class DeviceInfo {

//---------------自定义meta-------------------

    private static String DEFAULT_CHANNEL ="1000";
    private static ActivityManager mActivityManager = null ;
    /**
     * 获取发布渠道信息
     * @param context
     * @return
     */
    public static String getChannel(Context context){
        String msg = DEFAULT_CHANNEL;
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            msg=appInfo.metaData.getInt("channel")+"";
        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            msg="0000";
        }
        return msg;
    }


//---------------唯一标识UDID-------------------

    /**
     * 设备通用统一标识符（注意是从OpenUDID_manager里取，不是直接获得）
     * @return
     */
    public static String getUDID() {
        return OpenUDID_manager.isInitialized() == false ? "REPLACE_UDID" : OpenUDID_manager.getOpenUDID();
    }


    //---------------系统固有信息-------------------

    /**
     * get current connected network type
     *
     * @return
     */
    public static String getNetType(Context context) {
        String type = null;
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = conMan.getActiveNetworkInfo();
        if (info != null) // TYPE_MOBILE
        {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_MOBILE:
                    switch (info.getSubtype()) {
                        case TelephonyManager.NETWORK_TYPE_EDGE:
                            type = "EDGE";
                            break;
                        case TelephonyManager.NETWORK_TYPE_CDMA:
                            type = "CDMA";
                            break;
                        case TelephonyManager.NETWORK_TYPE_GPRS:
                            type = "GPRS";
                            break;
                        case TelephonyManager.NETWORK_TYPE_EVDO_0:
                            type = "EVDO_0";
                            break;
                        case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                            type = "UNKOWN";
                            break;
                    }
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    type = "wifi";
                    break;
            }
        } else
            type = "outofnetwork";
        return type;
    }

    /**
     * 系统类型
     * @return
     */
    public static String getOS() {
        return "Android";
    }

    /**
     * 手机卡位置
     * @return
     */


    /**
     * 系统版本号
     * @return
     */

    public static String getOSVersion() {
        return android.os.Build.VERSION.RELEASE;
    }


    /**
     * 手机型号
     * @return
     */
    public static String getDevice() {
        return android.os.Build.MODEL;
    }

    /**
     * 分辨率
     * @param context
     * @return like “480x800”
     */
    public static String getResolution(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        Display display = wm.getDefaultDisplay();

        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.widthPixels + "x" + metrics.heightPixels;
    }

    /**
     * 获取屏幕密度分级
     * @param context
     * @return
     */
    public static String getDensity(Context context) {
        int density = context.getResources().getDisplayMetrics().densityDpi;

        switch (density) {
            case DisplayMetrics.DENSITY_LOW:
                return "LDPI";
            case DisplayMetrics.DENSITY_MEDIUM:
                return "MDPI";
            case DisplayMetrics.DENSITY_TV:
                return "TVDPI";
            case DisplayMetrics.DENSITY_HIGH:
                return "HDPI";
            case DisplayMetrics.DENSITY_XHIGH:
                return "XHDPI";
            case DisplayMetrics.DENSITY_XXHIGH:
                return "XXHDPI";
//                not support on android 4.1.2
//            case DisplayMetrics.DENSITY_XXXHIGH:
//                return "XXXHDPI";
            default:
                return "";
        }
    }


    /**
     * 运营商名
     * @param context
     * @return
     */
    public static String getCarrier(Context context) {
        try {
            TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return manager.getNetworkOperatorName();
        } catch (NullPointerException npe) {
            npe.printStackTrace();
            Log.e("Countly", "No carrier found");
        }
        return "";
    }

    /**
     * 获得本地化信息
     * @return “语言_国家”
     */
    public static String getLocale() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static String getLanguage(){
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static String getCountry(Context context){
        return getUserCountry(context);
    }

    public static String getUserCountry(Context context) {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            final String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) { // SIM country code is available
                return simCountry.toLowerCase(Locale.US);
            }
            else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) { // device is not 3G (would be unreliable)
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2) { // network country code is available
                    return networkCountry.toLowerCase(Locale.US);
                }
            }
        }
        catch (Exception e) { }
        return "";
    }


    /**
     * app 版本
     * @param context
     * @return
     */
    public static String appVersion(Context context) {
        String result = "999";


        try {
            PackageManager manager  = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            result = info.versionName +"."+String.valueOf(info.versionCode) ;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 把设备和app信息组装成json
     * @param context
     * @return
     */
    public static String getMetrics(Context context) {
        String result = "";
        JSONObject json = new JSONObject();

        try {
            json.put("_device", getDevice());
            json.put("_os", getOS());
            json.put("_os_version", getOSVersion());
            json.put("_carrier", getCarrier(context));
            json.put("_resolution", getResolution(context));
            json.put("_density", getDensity(context));
            json.put("_locale", getLocale());
            json.put("_app_version", appVersion(context));
            json.put("_channel",getChannel(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        result = json.toString();


        //Log.d("metric origin",result);

        try {
            //确认编码为utf-8字符
            result = java.net.URLEncoder.encode(result, "UTF-8");
        } catch (UnsupportedEncodingException e) {

        }

        return result;
    }

    //总内存大小
    public static String getTotalRam(Context context){//M
        String path = "/proc/meminfo";
        String firstLine = null;
        int totalRam = 0 ;
        try{
            FileReader fileReader = new FileReader(path);
            BufferedReader br = new BufferedReader(fileReader,8192);
            firstLine = br.readLine().split("\\s+")[1];
            br.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        if(firstLine != null){
            totalRam = (int)Math.ceil((new Float(Float.valueOf(firstLine) / (1024)).doubleValue()));
        }

        return totalRam + "M";//返回1M
    }

    public static boolean ExistSDCard() {
        if (android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }
    //sd卡空间
    public static String getSDFreeSize(){

        return "0";

        //return (freeBlocks * blockSize)/1024 /1024; //单位MB
    }

    public static String getelectricSize() {

        return "100%";
    }

    //获取内存

    public static String getMemoryinfo(Context context){


        mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        int pid = android.os.Process.myPid();
        android.os.Debug.MemoryInfo[] memoryInfoArray = mActivityManager.getProcessMemoryInfo(new int[] {pid});


        String sm = String.valueOf((int)memoryInfoArray[0].getTotalPrivateDirty() /1024) ;



        String availMemStr = getSystemAvaialbeMemorySize(context);

        sm = availMemStr +" " +sm;

        return sm;
    }

    //获得系统可用内存信息
    private static String getSystemAvaialbeMemorySize(Context context){
        //获得MemoryInfo对象
        MemoryInfo memoryInfo = new MemoryInfo() ;
        //获得系统可用内存，保存在MemoryInfo对象上
        mActivityManager.getMemoryInfo(memoryInfo) ;
        long memSize = memoryInfo.availMem /1024 /1024;
        long memtotal =memoryInfo.totalMem /1024 /1024;

        //字符类型转换
        String availMemStr = formateFileSize(context, memSize);
        availMemStr = availMemStr + "/"+formateFileSize(context, memtotal);

        availMemStr = String.valueOf(memSize) + "/" ;
        availMemStr += String.valueOf(memtotal) +"M";

        return availMemStr ;
    }

    //调用系统函数，字符串转换 long -String KB/MB
    private static String formateFileSize(Context context,long size){
        return Formatter.formatFileSize(context, size);
    }
}
