package leqi.com.leqipassportsdk;

import android.content.Context;
import android.app.Activity;
//import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.concurrent.Executors;

import androidx.annotation.NonNull;
import leqi.com.leqipassportsdk.comm.NetUtils;
import leqi.com.leqipassportsdk.comm.Utils;
import leqi.com.leqipassportsdk.openudid.OpenUDID_manager;
import leqi.com.leqipassportsdk.comm.AdvertisingIdClient;
//import com.app5stargames.battleship.Restart;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class leqipass {
    public  static  String Tag = "SDK";
    private static leqipass sharedInstance_;
    private Context context_;
    private LeqiLoginBack leqiLoginBack;

    static public leqipass shareInstance(){
        if (sharedInstance_ == null)
            sharedInstance_ = new leqipass();
        return sharedInstance_;
    }

    private  leqipass(){

    }


    public void init(Context context) {

        context_ = context;
        Executors.newSingleThreadExecutor().execute(new Runnable(){
            @Override
            public void run(){
                String adid=null;
                try{
                    adid = AdvertisingIdClient.getGoogleAdId(context_);
                    Log.i(leqipass.Tag, "AdID="+adid);
                }catch (Exception e){
                    Log.i(leqipass.Tag, "广告ID无法获取");
                    e.printStackTrace();
                }
                finally {
                    if ( adid == null || adid == ""  || (adid.substring(0,5).equals("00000"))){
                        OpenUDID_manager.sync(context_);
                        mUserInfo.DeviceiD = Utils.md5(DeviceInfo.getUDID());
                    }
                    else
                    {
                        mUserInfo.DeviceiD =adid;
                        mUserInfo.user_idfa =adid;
                    }
                }
            }
        });
    }


    public void game_init(String serverURL, String appKey , String appVer){
        PlatformConfig.appKey = appKey;
        PlatformConfig.postURL = serverURL;
        PlatformConfig.appVer = appVer;
        loginByDeviceID();


        try {
            GetFcmToken();
        }
        catch (Exception e){
            Log.i("hkl","获取fcmtoken失败="+e.toString());
        }


//        String str_sign_time = ""+System.currentTimeMillis();
//        String str_auth_code =Utils.getEncodeKey("version");
//        String auth_code ="auth_code=" + str_auth_code
//                +"&sign_time=" +str_sign_time +"&security=1";
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("auth_code", str_auth_code);
//        map.put("sign_time", str_sign_time);
//        map.put("security", "1");
//        NetUtils.doPost(PlatformConfig.postURL + "version.html", auth_code +"&sign=" +Utils.generateSignature(map)
//                , new NetUtils.HttpResponseCallBack() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                Log.i(leqipass.Tag, "初始化返回=" +response.toString());
//                String saction ="version";
//                if (response== null || response.length()==0){
//                    Log.i(leqipass.Tag, "version.html  response is null");
//                    leqiLoginBack.back_loginfail(saction,-1001);
//                    return;
//                }
//                int errcode = response.optInt( "errcode" );
//                if (errcode ==0){
//                    try{
//                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }
//
//                        final JSONObject json = new JSONObject();
//                        fillJSONIfValuesNotEmpty(json,
//                                "version", ret.optString("version"),
//                                "action",saction,
//                                "errcode", "0"
//                        );
//                        leqiLoginBack.back_from_pass(json);
//                    } catch (Exception e) {
//                        leqiLoginBack.back_loginfail(saction, errcode);
//                        e.printStackTrace();
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure() {
//                leqiLoginBack.back_loginfail("version", -1001);
//                Log.i( leqipass.Tag, "version.html:onFailure" );
//            }
//        });
    }



    public void gameserver_list(String areaid){
        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey("server");
        String auth_code ="auth_code=" + str_auth_code +"&account_id=" +mUserInfo.UserID
                +"&sign_time=" +str_sign_time +"&security=1";

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("account_id", mUserInfo.UserID);
        map.put("security", "1");

        NetUtils.doPost(PlatformConfig.postURL + "getserver.html", auth_code +"&sign="
                +Utils.generateSignature(map), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {

                String saction ="getserver";
                if (response== null || response.length()==0){
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );

                if (errcode ==0){
                    try{

                        //JSONObject ret = (JSONObject)response.opt( "server" );
                        //leqiLoginBack.back_unbinding_gg(json);
                        leqiLoginBack.back_from_pass(response);
                    } catch (Exception e) {
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("getserver", -1001);
            }
        });
    }





//硬件码登录
    public void loginByDeviceID(){
        if(mUserInfo.DeviceiD==null){
            Log.i(leqipass.Tag, "硬件码无法获取");
            leqiLoginBack.back_loginfail("login",1001);
            return;
        }

//        +"&googlefcm="+mUserInfo.fcm_token
        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey("login");
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"
                +"&device_id="+mUserInfo.DeviceiD +"&user_idfa="+mUserInfo.user_idfa +"&country="+DeviceInfo.getUserCountry(context_);

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("device_id",mUserInfo.DeviceiD);
//        map.put("googlefcm",mUserInfo.fcm_token );
        map.put("user_idfa", mUserInfo.user_idfa );
        map.put("country",DeviceInfo.getUserCountry(context_));

        Log.i( leqipass.Tag, auth_code);


        NetUtils.doPost( PlatformConfig.postURL + "login.html", auth_code +"&sign=" +Utils.generateSignature(map), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="login";

                if (response == null || response.length() == 0) {
                    Log.i( leqipass.Tag, "login.htm:null respone" );
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                Log.i(leqipass.Tag,"登录：平台返回======"+response.toString());
                int errcode = response.optInt( "errcode" );

                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                           // leqiLoginBack.back_loginfail(saction, 99999);
//                            //return;
//                        }

                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");
                        mUserInfo.gg_client =ret.optString("gg_client");
                        mUserInfo.fb_client =ret.optString("fb_client");
                        mUserInfo.google_user_name =ret.optString("gg_nickname").replace("&", "###@##");
                        mUserInfo.facebook_user_name =ret.optString("fb_nickname").replace("&", "###@##");

                        if (ret.optString("is_gm").equals("1")){
                            if (!Utils.md5(mUserInfo.UserID +";" +ret.optString("timestamp")+";mafia").equals(mUserInfo.loginkey)) {
                                Log.i("hkl", "login数据非法！");
                                leqiLoginBack.back_loginfail("login",-1001);
                                return;
                            }
                        }


                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "gg_alias", ret.optString("gg_nickname"),
                                "fb_alias", ret.optString("fb_nickname"),
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "account_id", ret.optString("account_id"),
                                "server_port", ret.optString("server_port"),
                                "serverip", ret.optString("serverip"),
                                "is_gm", ret.optString("is_gm"),
                                "notice", ret.optString("notice"),
                                "googlefcm",ret.optString("googlefcm"),
                                "country",DeviceInfo.getUserCountry(context_),
                                "server_status", ret.optString("server_status"),
                                "carrier",DeviceInfo.getCarrier(context_),
                                "device_id",mUserInfo.DeviceiD,
                                "deviceType",DeviceInfo.getDevice(),
                                "resolution",DeviceInfo.getResolution(context_),
                                "os_version", DeviceInfo.getOSVersion(),
                                "language",DeviceInfo.getLanguage(),
                                "platform", "android",
                                "app_version",PlatformConfig.appVer,
                                "action", saction,
                                "errcode", "0"
                                );

                        //leqiLoginBack.back_login_by_device_success(json);
                        leqiLoginBack.back_from_pass(json);

                    } catch (Exception e) {
                        Log.i(leqipass.Tag, e.toString());
                        leqiLoginBack.back_loginfail(saction,errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i(leqipass.Tag,"login.html：errcode error!=="+ response.toString());

                    leqiLoginBack.back_loginfail(saction,errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("login",-1001);
                Log.i( leqipass.Tag, "login.html:onFailure" );
            }
        } );
    }



    //切换账号
    public void exChange(int exTYpe,String is_create){
        String sauthcode ="";
        String openid   ="";

        if (exTYpe == 1003){
            sauthcode = "gg_client";
            openid = mUserInfo.google_id;
        }
        else if (exTYpe == 3003){
            sauthcode = "fb_client";
            openid = mUserInfo.facebook_id;
        }

        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey(sauthcode);
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"+"&account_id="+mUserInfo.UserID
                +"&openid_str="+openid +"&is_create="+is_create
                +"&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email
                + "&device_id=" + mUserInfo.DeviceiD;

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("device_id",mUserInfo.DeviceiD);
        map.put("openid_str",openid);
        map.put("is_create", is_create );
        map.put("fb_nickname",mUserInfo.facebook_user_name);
        map.put("fb_email",mUserInfo.facebook_user_email);
        map.put("account_id", mUserInfo.UserID);

        Log.i( leqipass.Tag, auth_code );


        NetUtils.doPost(PlatformConfig.postURL + "switch.html", auth_code +"&sign=" +Utils.generateSignature(map), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="switch";

                if (response ==null || response.length()==0){
                    Log.i(leqipass.Tag, "switch.html response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                Log.i(leqipass.Tag,"切换账号：平台返回======"+response.toString());
                int errcode = response.optInt( "errcode" );
                if (errcode ==0){
                    try{
                        String UserID_ori = mUserInfo.UserID;
                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }

                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");

                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "account_id_ori", UserID_ori,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "action",saction,
                                "errcode", "0"
                        );

                        //if (json.optString("account_id").equals(json.optString("account_id_ori"))) {
                            leqiLoginBack.back_from_pass(json);
                      //  }else {
                            //重启游戏
                            //Restart.getInstance((Activity) context_).restart();
                      //  }
//                         leqiLoginBack.back_from_pass(json);
                    } catch (Exception e) {
                        leqiLoginBack.back_loginfail(saction,errcode);
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i(leqipass.Tag,"switch.html：errcode error!=="+ response.toString());
                    leqiLoginBack.back_loginfail(saction, errcode);
                }
            }
            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("switch", -101);
                Log.i( leqipass.Tag, "switch.html:onFailure" );
            }
        });
    }


    //账号绑定
    public void Binding(int BindType){
        String sauthcode ="";
        String openid_str ="";
        String saction ="bind";
        String str ="";

        if (BindType ==1001 || BindType==1002){  //1自动绑定    2手动绑定
            if (mUserInfo.gg_client.equals("1")){
                //进度已绑定
                if (BindType==1002) { //自动绑定不回调
                    leqiLoginBack.back_loginfail(saction, 20003);
                }
                return;
            }
            sauthcode ="gg_client";
            openid_str = mUserInfo.google_id;
            str ="&gg_nickname="+mUserInfo.google_user_name +"&gg_email="+mUserInfo.google_user_email;
        }
        else if (BindType==3001){  //fb手动绑定
            if (mUserInfo.fb_client.equals("1")){
                leqiLoginBack.back_loginfail(saction,20002);
                return;
            }
            sauthcode ="fb_client";
            openid_str =mUserInfo.facebook_id;
            str ="&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email;
        }
        else{
            Log.i(leqipass.Tag,"请检查绑定类型！");
            return;
        }


        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey(sauthcode);
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"+"&account_id="+mUserInfo.UserID
                +"&openid_str="+openid_str +"&auto_bind="+ BindType +str
                + "&device_id=" + mUserInfo.DeviceiD;

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("device_id",mUserInfo.DeviceiD);
        map.put("openid_str",openid_str);
        map.put("auto_bind", ""+ BindType  );
        if (BindType==3001){
            map.put("fb_nickname", mUserInfo.facebook_user_name);
            map.put("fb_email", mUserInfo.facebook_user_email);
        }
        else
        {
            map.put("gg_nickname", mUserInfo.google_user_name);
            map.put("gg_email", mUserInfo.google_user_email);
        }
        map.put("account_id", mUserInfo.UserID);

        Log.i(leqipass.Tag,auth_code);

        NetUtils.doPost(PlatformConfig.postURL + "bind.html", auth_code +"&sign="+Utils.generateSignature(map), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="bind";
                if (response== null || response.length()==0){
                    Log.i(leqipass.Tag, "binding.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                Log.i(leqipass.Tag,"绑定平台返回======"+response.toString());
                int errcode = response.optInt( "errcode" );
                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );

//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }


                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");
                        mUserInfo.gg_client =ret.optString("gg_client");
                        mUserInfo.fb_client =ret.optString("fb_client");
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "progress", ret.optString("progress"),
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "action",saction,
                                "errcode", "0"
                        );
                        leqiLoginBack.back_from_pass(json);
                    } catch (Exception e) {
                        leqiLoginBack.back_loginfail(saction,errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    if (errcode==20006){
                        //自动绑定20006错误处理，换手机或者广告ID重置情况 11代表允许忽略判断直接绑定
                        //exChange(1003,"11");
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "progress", "1",
                                "gg_client", "0",
                                "fb_client", "0",
                                "action",saction,
                                "account", mUserInfo.google_user_email,
                                "errcode", Integer.toString(errcode)
                        );
                        Log.i("hkl", json.toString());
                        leqiLoginBack.back_from_pass(json);
                    }
                    else {
                        leqiLoginBack.back_loginfail(saction, errcode);
                    }
                }
            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("bind", -101);
                Log.i(leqipass.Tag, "binding.html onFailure");
            }

        });
    }




    //游戏内换服
    public void gameserver_select(String serverid){
        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey("server");
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"
                +"&account_id=" + mUserInfo.UserID +
                "&server_id=" + serverid+"&device_id="+mUserInfo.DeviceiD;

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("account_id",mUserInfo.UserID  );
        map.put("server_id", serverid);
        map.put("device_id", mUserInfo.DeviceiD);

        Log.e("Unity", auth_code);


        NetUtils.doPost(PlatformConfig.postURL + "server.html", auth_code +"&sign=" +Utils.generateSignature(map), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i(leqipass.Tag, "换服：平台返回"+response.toString());

                String saction ="server";
                if (response== null || response.length()==0){
                    Log.i(leqipass.Tag, "server.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i(leqipass.Tag,"换服：平台返回======"+response.toString());

                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );

//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }

                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "progress", ret.optString("progress"),
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "action","server_select",
                                "errcode", "0"
                        );
                        //leqiLoginBack.back_unbinding_gg(json);
                        leqiLoginBack.back_from_pass(json);

                    } catch (Exception e) {
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("server", -1001);
                Log.i( leqipass.Tag, "server_select.html:onFailure" );
            }
        });
    }

    //充值
    public void purchase(String billing, String sign){
        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey("googlepay");
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"
                +"&signeddata=" +billing +"&signature=" +sign
                +"&platform=android" +"&device_id=" +mUserInfo.DeviceiD +"&account_id="+mUserInfo.UserID;

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("signeddata",billing);
        map.put("signature",sign );
        map.put("platform", "android");
        map.put("device_id", mUserInfo.DeviceiD);
        map.put("account_id", mUserInfo.UserID);


        Log.e("Unity", auth_code);



        NetUtils.doPost( PlatformConfig.postURL + "paycheck.html", auth_code +"&sign=" +Utils.generateSignature(map) , new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="paycheck";
                if (response== null || response.length()==0){
                    Log.i(leqipass.Tag, "googlepay.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }
                int errcode = response.optInt( "errcode" );
                Log.i(leqipass.Tag,"订单确认：平台返回======"+response.toString());
                if (errcode==0 ||errcode==40007){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }


                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "action", "googlepay",
                                "purchasetoken",ret.optString("token"),
                                "paymoney", ret.optString("paymoney"),
                                "errcode", "0"
                        );
                        leqiLoginBack.back_pay_by_gg_success(json);
                    } catch (Exception e) {
                        Log.i(leqipass.Tag, e.toString());
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    leqiLoginBack.back_loginfail(saction, errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("paycheck", -1001);
                Log.i( leqipass.Tag, "googlepay.html:onFailure" );
            }
        } );
    }






    //发起内购，第一步提交平台
    public void purchase_order(String productid,  int pay_money, String package_name, String game_user_id){
        String str_sign_time = ""+System.currentTimeMillis();
        String str_auth_code =Utils.getEncodeKey("payform");
        String auth_code ="auth_code=" + str_auth_code
                +"&sign_time=" +str_sign_time +"&security=1"
                +"&account_id=" + mUserInfo.UserID
                +"&product_id="+productid +"&package_name="+package_name
                + "&platform=" + "android" +"&device_id="+mUserInfo.DeviceiD +"&pay_money="+pay_money +"&game_user_id="+game_user_id;

        Map<String, String> map = new HashMap<String, String>();
        map.put("auth_code", str_auth_code);
        map.put("sign_time", str_sign_time);
        map.put("security", "1");
        map.put("product_id",productid );
        map.put("package_name", package_name);
        map.put("platform", "android");
        map.put("device_id", mUserInfo.DeviceiD);
        map.put("pay_money", ""+pay_money);
        map.put("game_user_id", game_user_id);
        map.put("account_id", mUserInfo.UserID);

        Log.e("Unity", auth_code);


        NetUtils.doPost( PlatformConfig.postURL + "payorder.html", auth_code +"&sign="+Utils.generateSignature(map) , new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="payorder";
                if (response== null || response.length()==0){
                    Log.i(leqipass.Tag, "payorder.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                Log.i(leqipass.Tag,"订单：平台返回======"+response.toString());
                int errcode = response.optInt( "errcode" );
                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        if (!md5Check_json(ret, response.optString("sign")))
//                        {
//                            leqiLoginBack.back_loginfail(saction, 99999);
//                            return;
//                        }


                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "product_id", ret.optString("product_id"),
                                "action", "payorder",
                                "errcode", "0"
                        );
                        leqiLoginBack.back_pay_by_order_success(json);
                    } catch (Exception e) {
                        Log.i(leqipass.Tag, e.toString());
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    leqiLoginBack.back_loginfail(saction, errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("payorder", -1001);
                Log.i( leqipass.Tag, "googlepay.html:onFailure" );
            }
        } );
    }




    static void fillJSONIfValuesNotEmpty(final JSONObject json, final String ... objects) {
        try {
            if (objects.length > 0 && objects.length % 2 == 0) {
                for (int i = 0; i < objects.length; i += 2) {
                    final String key = objects[i];
                    final String value = objects[i + 1];
                    if (value != null && value.length() > 0) {
                        json.put(key, value);
                    }
                }
            }
        } catch (JSONException ignored) {
        }
    }


    public void setLeqiLoginBack(LeqiLoginBack leqiLoginBack){
        this.leqiLoginBack =leqiLoginBack;
    }


    public interface LeqiLoginBack{
        void back_pay_by_gg_success(JSONObject json);
        void back_pay_by_order_success(JSONObject json);
        void back_loginfail(String saction, int retcode);
        void back_from_pass(JSONObject json);
    }


    public void GetFcmToken(){
        Thread thread_ = new Thread() {
            @Override
            public void run() {
                __GetFcmToken();
            }
        };
        thread_.start();
    }


    public void __GetFcmToken(){
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()){
                                Log.i(leqipass.Tag, "getInstanceId failed");
                                mUserInfo.fcm_token = "";
                                return;
                            }
                            mUserInfo.fcm_token =task.getResult().getToken();
                            Log.i(leqipass.Tag, "fcm_token="+mUserInfo.fcm_token);
                        }
                    });
        }catch (Exception e){}

    }




    public JSONObject getUserInfo(String sgame_userid){
        try{

            String _os_version, _devicetype, _resolution, _carrier, _appversion, _language, _country, _game_userid;
            _os_version =DeviceInfo.getOSVersion();
            _devicetype =DeviceInfo.getDevice();
            _resolution =DeviceInfo.getResolution(context_);
            _carrier = DeviceInfo.getCarrier(context_);
            _appversion =PlatformConfig.appVer;
            _language =DeviceInfo.getLanguage();
            _country =DeviceInfo.getUserCountry(context_);
            mUserInfo.game_userid=_game_userid=sgame_userid;
            ///加密
            String str_sign_time = ""+System.currentTimeMillis();
            String str_auth_code =Utils.getEncodeKey("submit");
            String auth_code ="auth_code=" + str_auth_code
                    +"&sign_time=" +str_sign_time +"&security=1&platform=android"
                    +"&account_id=" + mUserInfo.UserID +
                    "&device_id=" + mUserInfo.DeviceiD  +
                    "&os_version=" + _os_version + "&phone_model=" + _devicetype + "&resolving=" + _resolution +
                    "&operator=" + "" + "&language=" + _language + "&app_version=" + _appversion + "&googlefcm=" + mUserInfo.fcm_token+
                    "&gg_nickname="+mUserInfo.google_user_name+"&gg_email=" +mUserInfo.google_user_email +
                    "&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email+
                    "&game_userid="+_game_userid;

            Map<String, String> map = new HashMap<String, String>();
            map.put("auth_code", str_auth_code);
            map.put("sign_time", str_sign_time);
            map.put("security", "1");
            map.put("platform", "android");
            map.put("device_id", mUserInfo.DeviceiD);
            map.put("account_id", mUserInfo.UserID);
            map.put("os_version",_os_version );
            map.put("game_userid", _game_userid);
            map.put("phone_model", _devicetype);
            map.put("resolving",_resolution );
            map.put("operator", "");
            map.put("language",_language );
            map.put("app_version",_appversion );
            map.put("googlefcm", mUserInfo.fcm_token);
            map.put("gg_nickname",mUserInfo.google_user_name );
            map.put("gg_email",mUserInfo.google_user_email );
            map.put("fb_nickname", mUserInfo.facebook_user_name);
            map.put("fb_email", mUserInfo.facebook_user_email);

            Log.e("Unity", auth_code);


            NetUtils.doPost(PlatformConfig.postURL + "submit.html", auth_code +"&sign="+Utils.generateSignature(map),
                    new NetUtils.HttpResponseCallBack() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            String saction ="submit";
                            if (response == null || response.length() == 0) {
                                Log.i( leqipass.Tag, "googlepay.htm:null respone" );
                                return;
                            }
                            Log.i(leqipass.Tag,"提交用户信息：平台返回======"+response.toString());
                        }
                        @Override
                        public void onFailure() {
                            Log.i(leqipass.Tag,"提交用户信息：失败");
                        }
                    });


            final JSONObject json = new JSONObject();
            fillJSONIfValuesNotEmpty(json,
                    "account_id", mUserInfo.UserID,
                    "login_key", mUserInfo.loginkey,
                    "server_id", mUserInfo.server_id,
                    "gg_alias", mUserInfo.google_user_name.replace("###@##", "&"),
                    "fb_alias", mUserInfo.facebook_user_name.replace("###@##", "&"),
                    "gg_client",mUserInfo.gg_client,
                    "fb_client",mUserInfo.fb_client,
                    "os_version", _os_version,
                    "deviceType",_devicetype,
                    "resolution",_resolution,
                    "carrier",_carrier,
                    "appversion", _appversion,
                    "language",_language,
                    "country", _country,
                    "action","getuserinfo"
            );
            Log.i(leqipass.Tag, "提交用户信息======="+json.toString());
            return json;

        } catch (Exception e) {
            Log.i(leqipass.Tag, "获取玩家信息失败=");
            e.printStackTrace();
            return null;
        }

    }


    public static boolean md5Check_json(JSONObject js, String str_dst){
        Iterator it =js.keys();
        String skey;
        String svalue;
        String smd5="";
        Map<String, String> map= new HashMap<String, String>();
        while(it.hasNext()){

            skey =((String)it.next());
            if (!skey.equals("notice")){
                svalue =js.optString(skey);
                map.put(skey, svalue);
            }
        }
        return (Utils.generateSignature(map).equals(str_dst));

    }





}
