package leqi.com.leqipassportsdk;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

//import com.google.android.gms.ads.identifier.AdvertisingIdClient;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executors;

import leqi.com.leqipassportsdk.comm.NetUtils;
import leqi.com.leqipassportsdk.comm.Utils;
import leqi.com.leqipassportsdk.openudid.OpenUDID_manager;
import leqi.com.leqipassportsdk.comm.AdvertisingIdClient;
/**
 * Created by hkl on 2018/3/7.
 */

public class leqipass {
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

    public void init(Context context, String serverURL, String appKey,String appVer) {
        PlatformConfig.appKey = appKey;
        PlatformConfig.postURL = serverURL;
        PlatformConfig.appVer =appVer;

        context_ =context;
       // mUserInfo.UserInfo_Init();//初始化用户数据

        game_init();

        //获取AdId
        Executors.newSingleThreadExecutor().execute(new Runnable(){
            @Override
            public void run(){
                String adid=null;
                try{
                    adid = AdvertisingIdClient.getGoogleAdId(context_);

                    //MyFirebaseInstanceIDService myFirebaseInstanceIDService = new MyFirebaseInstanceIDService();
                    //myFirebaseInstanceIDService.onTokenRefresh();

                    FirebaseInstanceId.getInstance().getInstanceId()
                            .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                @Override
                                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                    if (!task.isSuccessful()){
                                        Log.i("hkl", "getInstanceId failed");
                                        return;
                                    }

                                    mUserInfo.fcm_token =task.getResult().getToken();

                                    Log.i("hkl", "fcm_token="+mUserInfo.fcm_token);
                                }
                            });

                    Log.i("hkl", "AdID="+adid);

                }catch (Exception e){

                    Log.i("hkl", "广告ID无法获取");
                    e.printStackTrace();
                }
                finally {
                    if ((adid==null) ||(adid.substring(0,5).equals("00000"))){
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
        //end of AdId

    }

    public int errcodeConvert(String errcode){
        if (errcode.equals("10001")) {
           return 10001;
        }
        else if (errcode.equals("10002")) {
            return 10002;
        }
        else if (errcode.equals("10005")) {
            return 10005;
        }
        else if (errcode.equals("20001")) {
            return 20001;
        }
        else if(errcode.equals("20002")){
            return 20002;
        }
        else if (errcode.equals("20003")) {
            return 20003;
        }
        else if (errcode.equals("20004")) {
            return 20004;
        }
        else if (errcode.equals("20005")) {
            return 20005;
        }
        else if (errcode.equals("30001")) {
            return 30001;
        }
        else if (errcode.equals("30002")) {
            return 30002;
        }
        else
            return -1;
    }
//硬件码登录
    public void loginByDeviceID(){
        //Toast.makeText(context_, "auth_code=" + Utils.getEncodeKey("login")
        //        + "&gg_client=" + (mUserInfo.google_id) +"&device_id="+mUserInfo.DeviceiD +"&openid="+mUserInfo.DeviceiD ,Toast.LENGTH_LONG ).show();
        if(mUserInfo.DeviceiD==null){
            Log.i("hkl", "硬件码无法获取");
            leqiLoginBack.back_loginfail("login",1001);
            return;
        }



        Log.i("hkl","登录：平台请求======auth_code=" + Utils.getEncodeKey("login") +"&device_id="+mUserInfo.DeviceiD );
        NetUtils.doPost( PlatformConfig.postURL + "login.html", "auth_code=" + Utils.getEncodeKey("login") +"&googlefcm="+mUserInfo.fcm_token
                 +"&device_id="+mUserInfo.DeviceiD +"&user_idfa="+mUserInfo.user_idfa +"&country="+DeviceInfo.getUserCountry(context_), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="login";

                if (response == null || response.length() == 0) {
                    Log.i( "hkl", "login.htm:null respone" );
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                Log.i("hkl","登录：平台返回======"+response.toString());

                int errcode = response.optInt( "errcode" );



                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");
                        mUserInfo.gg_client =ret.optString("gg_client");
                        mUserInfo.fb_client =ret.optString("fb_client");
                        //mUserInfo.fcm_token =ret.optString("googlefcm");g
                        mUserInfo.google_user_name =ret.optString("gg_nickname");
                        mUserInfo.facebook_user_name =ret.optString("fb_nickname");
                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();

                        if (ret.optString("is_gm").equals("1")){
                            if (!Utils.md5(mUserInfo.UserID +";" +ret.optString("timestamp")+";battleship").equals(mUserInfo.loginkey)) {
                                Log.i("hkl", "login数据非法！");
                                leqiLoginBack.back_loginfail(saction,-1001);
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
                                "progress", ret.optString("progress"),
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "serverip", ret.optString("serverip"),
                                "is_gm", ret.optString("is_gm"),
                                "server_port", ret.optString("server_port"),
                                "server_status",ret.optString("server_status"),
                                "notice", ret.optString("notice"),
                                "action", "login",
                                "errcode", "0"
                                );

                        //leqiLoginBack.back_login_by_device_success(json);
                        leqiLoginBack.back_from_pass(json);

                    } catch (Exception e) {
                        Log.i("hkl", e.toString());
                        leqiLoginBack.back_loginfail(saction,errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i("hkl","login.html：errcode error!=="+ response.toString());

                    leqiLoginBack.back_loginfail(saction,errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("login",-1001);
                Log.i( "hkl", "login.html:onFailure" );
            }
        } );
    }

    //切换账号
    public void exChange(int exTYpe, String is_create){
        String sauthcode ="";
        String openid   ="";

        if (exTYpe ==1003){
            sauthcode ="gg_client";
            openid =mUserInfo.google_id;
        }
        else if (exTYpe==3003){
            sauthcode ="fb_client";
            openid =mUserInfo.facebook_id;
        }

//注意 现在只支持fb切换，不支持

        Log.i("hkl","切换账号：平台请求======auth_code=" + Utils.getEncodeKey(sauthcode) +"&account_id="+mUserInfo.UserID +"&openid_str="+openid + "&device_id=" + mUserInfo.DeviceiD +"&is_create="+is_create);
        NetUtils.doPost(PlatformConfig.postURL + "switch.html", "auth_code=" + Utils.getEncodeKey(sauthcode)
                +"&account_id="+mUserInfo.UserID +"&openid_str="+openid +"&is_create="+is_create
                +"&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email
                + "&device_id=" + mUserInfo.DeviceiD, new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="exchange";
                if (response ==null || response.length()==0){
                    Log.i("hkl", "switch.html response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }
                Log.i("hkl","切换账号：平台返回======"+response.toString());
                int errcode = response.optInt( "errcode" );

                if (errcode ==0){
                    try{
                        String UserID_ori =mUserInfo.UserID;
                        JSONObject ret = (JSONObject)response.opt( "data" );

                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");


                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "account_id_ori", UserID_ori,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "progress", ret.optString("progress"),
                                "gg_client", "0",
                                "fb_client", "0",
                                "action","exchange",
                                "errcode", "0"
                        );

                       // leqiLoginBack.back_exchange_success(json);
                        leqiLoginBack.back_from_pass(json);

                    } catch (Exception e) {
                        leqiLoginBack.back_loginfail(saction,errcode);
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i("hkl","switch.html：errcode error!=="+ response.toString());

                    leqiLoginBack.back_loginfail(saction, errcode);
                }


            }

            @Override
            public void onFailure() {
                Log.i( "hkl", "switch.html:onFailure" );
            }
        });
    }
//    //选择账号
//    public void account_select(int exTYpe){
//        String sauthcode ="";
//        if (exTYpe ==1){
//            sauthcode ="select";
//        }
//        else if (exTYpe==2){
//            sauthcode ="select";
//        }
//        Log.i("hkl","选择账号：平台请求======auth_code=" + Utils.getEncodeKey(sauthcode) +"&account_id="+mUserInfo.UserID + "&device_id=" + mUserInfo.DeviceiD);
//        NetUtils.doPost(PlatformConfig.postURL + "select.html", "auth_code=" + Utils.getEncodeKey(sauthcode)
//                +"&account_id="+mUserInfo.UserID + "&device_id=" + mUserInfo.DeviceiD, new NetUtils.HttpResponseCallBack() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                if (response ==null || response.length()==0){
//                    Log.i("hkl", "select.html response is null");
//                    return;
//                }
//                Log.i("hkl","选择账号：平台返回======"+response.toString());
//                String strerrcode = response.optString( "errcode" );
//                String strcode = response.optString( "data" );
//
//
//                if (strerrcode == null || strerrcode.length() == 0) {
//                    Log.i( "hkl", "select.html:errcode error!" );
//                    return;
//                }
//                Log.i("hkl","select.html：response=="+ response.toString());
//                if (strerrcode.equals("0")){
//                    try{
//                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        mUserInfo.UserID = ret.optString( "account_id" ) ;
//                        mUserInfo.loginkey =ret.optString( "login_key" );
//                        mUserInfo.server_id =ret.optString("serverid");
//
//
//                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
//                        final JSONObject json = new JSONObject();
//                        fillJSONIfValuesNotEmpty(json,
//                                "account_id", mUserInfo.UserID,
//                                "login_key", mUserInfo.loginkey,
//                                "server_id", mUserInfo.server_id,
//                                "progress", ret.optString("progress"),
//                                //"gg_client", "0",
//                                //"fb_client", "0",
//                                "gg_client",ret.optString("gg_client"),
//                                "fb_client", ret.optString("fb_client"),
//                                "action","select",
//                                "errcode", "0"
//                        );
//
//                        //leqiLoginBack.back_account_select_success(json);
//                        leqiLoginBack.back_from_pass(json);
//
//                    } catch (Exception e) {
//                        leqiLoginBack.back_loginfail(-1);
//                        e.printStackTrace();
//                    }
//                }
//                else{
//                    Log.i("hkl","select.html：errcode error!=="+ response.toString());
//
//                    leqiLoginBack.back_loginfail(errcodeConvert(strerrcode));
//                }
//
//
//            }
//
//            @Override
//            public void onFailure() {
//                Log.i( "hkl", "select.html:onFailure" );
//            }
//        });
//    }
    //账号绑定
    public void Binding(int BindType){
        String sauthcode ="";
        String openid_str ="";
        String saction ="bind";
        String str ="";
        if (BindType ==1001 || BindType==1002){  //1自动绑定    2手动绑定
            if (mUserInfo.gg_client.equals("1")){
                //进度已绑定
                Log.i("hkl","此进度已绑定GG");
                if (BindType==1002) { //自动绑定不回调
                    leqiLoginBack.back_loginfail(saction, 20003);
                }
                return;
            }
            sauthcode ="gg_client";
            mUserInfo.action ="binding_gg";
            openid_str =mUserInfo.google_id;
            str ="&gg_nickname="+mUserInfo.google_user_name +"&gg_email="+mUserInfo.google_user_email;
        }
        else if (BindType==3001){  //fb手动绑定
            if (mUserInfo.fb_client.equals("1")){
                Log.i("hkl","此进度已绑定FB");
                leqiLoginBack.back_loginfail(saction,20002);
                return;
            }
            sauthcode ="fb_client";
            mUserInfo.action ="binding_fb";
            openid_str =mUserInfo.facebook_id;
            str ="&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email;
        }
        else{
            Log.i("hkl","请检查绑定类型！");
            return;
        }
//
//        if (mUserInfo.gg_client.equals("1") && BindType ==20){
//            Log.i("hkl", "GG已绑定，结束");
//            leqiLoginBack.back_loginfail(saction,20006);
//            return;
//        }
Log.i("hkl", "绑定：=====device_id=" + mUserInfo.DeviceiD +"&auto_bind="+ BindType +str
        + "&account_id=" + mUserInfo.UserID + "&auth_code=" + Utils.getEncodeKey(sauthcode) + "&openid_str=" + openid_str);
        NetUtils.doPost(PlatformConfig.postURL + "bind.html", "device_id=" + mUserInfo.DeviceiD +"&auto_bind="+ BindType +str
                + "&account_id=" + mUserInfo.UserID + "&auth_code=" + Utils.getEncodeKey(sauthcode) + "&openid_str=" + openid_str, new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="bind";
                if (response== null || response.length()==0){
                    Log.i("hkl", "binding.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }
                Log.i("hkl","绑定平台返回======"+response.toString());
               int errcode = response.optInt( "errcode" );

                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");
                        mUserInfo.gg_client =ret.optString("gg_client");
                        mUserInfo.fb_client =ret.optString("fb_client");

                        if (response.optString("auto_bind").equals("1")){
                            Log.i("hkl", "自动绑定GG成功，不回调");
                            return;
                        }

                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "account_id", mUserInfo.UserID,
                                "login_key", mUserInfo.loginkey,
                                "server_id", mUserInfo.server_id,
                                "progress", ret.optString("progress"),
                                "gg_client", ret.optString("gg_client"),
                                "fb_client", ret.optString("fb_client"),
                                "action",mUserInfo.action,
                                "errcode", "0"
                        );

                        //leqiLoginBack.back_binding_success(json);
                        leqiLoginBack.back_from_pass(json);

                       // getUserInfo(mUserInfo.game_userid);

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
                                "action",mUserInfo.action,
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

                Log.i("hkl", "binding.html onFailure");
            }

        });
    }

    //账户解绑
//    public void unBinding(int unBindType){
//        String sauthcode ="";
//        if (unBindType ==1){
//            sauthcode ="gg_client";
//        }
//        else{
//            sauthcode ="fb_client";
//        }
//        Log.i("hkl",PlatformConfig.postURL + "unbind.html?"+"account_id=" + mUserInfo.UserID
//                +"&device_id="+mUserInfo.DeviceiD+ "&auth_code=" + Utils.getEncodeKey(sauthcode));
//        NetUtils.doPost(PlatformConfig.postURL + "unbind.html", "account_id=" + mUserInfo.UserID
//                +"&device_id="+mUserInfo.DeviceiD+ "&auth_code=" + Utils.getEncodeKey(sauthcode), new NetUtils.HttpResponseCallBack() {
//            @Override
//            public void onSuccess(JSONObject response) {
//                if (response== null || response.length()==0){
//                    Log.i("hkl", "unbind.html  response is null");
//                    return;
//                }
//                Log.i("hkl","平台返回======"+response.toString());
//                String strerrcode = response.optString( "errcode" );
//                String strcode = response.optString( "data" );
//
//                if (strerrcode==null || strerrcode.length()==0){
//                    Log.i("hkl", "unbind.html strerrcdoe is null");
//                    return;
//                }
//                if (strerrcode.equals("0")){
//
//                    try{
//                        JSONObject ret = (JSONObject)response.opt( "data" );
//                        mUserInfo.UserID = ret.optString( "account_id" ) ;
//                        mUserInfo.loginkey =ret.optString( "login_key" );
//                        mUserInfo.server_id =ret.optString("serverid");
//
//
//                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
//                        final JSONObject json = new JSONObject();
//                        fillJSONIfValuesNotEmpty(json,
//                                "account_id", mUserInfo.UserID,
//                                "login_key", mUserInfo.loginkey,
//                                "server_id", mUserInfo.server_id,
//                                "progress", ret.optString("progress"),
//                                "gg_client", ret.optString("gg_client"),
//                                "fb_client", ret.optString("fb_client"),
//                                "action","unbind",
//                                "errcode", "0"
//                        );
//                        //leqiLoginBack.back_unbinding_gg(json);
//                        leqiLoginBack.back_from_pass(json);
//
//                    } catch (Exception e) {
//                        leqiLoginBack.back_loginfail(-1);
//                        e.printStackTrace();
//                    }
//                }
//                else {
//                    Log.i("hkl","unbind.html：errcode=="+ response.toString());
//                    leqiLoginBack.back_loginfail(errcodeConvert(strerrcode));
//                }
//
//            }
//
//            @Override
//            public void onFailure() {
//                Log.i("hkl", "unbind.html onFailure");
//            }
//        });
//    }

    public void game_init(){
        Log.i("hkl", "请求初始化");
        NetUtils.doPost(PlatformConfig.postURL + "version.html", "auth_code=" + Utils.getEncodeKey("version"), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("hkl", "初始化返回=" +response.toString());
                String saction ="version";
                if (response== null || response.length()==0){
                    Log.i("hkl", "version.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i("hkl","游戏初始化：平台返回======"+response.toString());


                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );

                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
                        final JSONObject json = new JSONObject();
                        fillJSONIfValuesNotEmpty(json,
                                "version", ret.optString("version"),
                                "action","version",
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
                leqiLoginBack.back_loginfail("version", -1001);
                Log.i( "hkl", "version.html:onFailure" );
            }
        });
    }
    //游戏服务器列表
    public void gameserver_list(){
        //
        Log.i("hkl", "获取服务器列表:account_id="+mUserInfo.UserID );
        NetUtils.doPost(PlatformConfig.postURL + "getserver.html", "account_id=" + mUserInfo.UserID +
                "&auth_code="+ Utils.getEncodeKey("server"), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("hkl", "获取服务器列表：平台返回"+response.toString());

                String saction ="getserver";
                if (response== null || response.length()==0){
                    Log.i("hkl", "getserver.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i("hkl","获取服务器列表：平台返回======"+response.toString());

                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );


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
                leqiLoginBack.back_loginfail("server", -1001);

                Log.i( "hkl", "server_select.html:onFailure" );

            }
        });
    }
    //游戏内换服
    public void gameserver_select(String serverid){
        //
        Log.i("hkl", "account_id="+mUserInfo.UserID +"&server_id="+serverid);
        NetUtils.doPost(PlatformConfig.postURL + "server.html", "account_id=" + mUserInfo.UserID +
                "&server_id=" + serverid+"&device_id="+mUserInfo.DeviceiD+"&auth_code="+ Utils.getEncodeKey("server"), new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                Log.i("hkl", "换服：平台返回"+response.toString());

                String saction ="server";
                if (response== null || response.length()==0){
                    Log.i("hkl", "server.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i("hkl","换服：平台返回======"+response.toString());

                if (errcode ==0){
                    try{
                        JSONObject ret = (JSONObject)response.opt( "data" );
                        mUserInfo.UserID = ret.optString( "account_id" ) ;
                        mUserInfo.loginkey =ret.optString( "login_key" );
                        mUserInfo.server_id =ret.optString("serverid");


                        //Toast.makeText(context_, "登录成功" ,Toast.LENGTH_LONG ).show();
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

                Log.i( "hkl", "server_select.html:onFailure" );

            }
        });
    }

    //充值
    public void purchase(String billing, String sign){

        Log.i("hkl","充值：平台请求======"+"account_id=" + mUserInfo.UserID
                        +"&signeddata="+billing+"&signature="+sign + "&auth_code=" + Utils.getEncodeKey("googlepay")
                        + "&platform=" + "android" +"&device_id="+mUserInfo.DeviceiD);
       // sign =sign.replace("+", "|");
       // sign =sign.replace("/", "_");
        NetUtils.doPost( PlatformConfig.postURL + "paycheck.html", "account_id=" + mUserInfo.UserID
                +"&signeddata="+billing+"&signature="+sign+ "&auth_code=" + Utils.getEncodeKey("googlepay")
                + "&platform=" + "android" +"&device_id="+mUserInfo.DeviceiD , new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {
                String saction ="paycheck";
                if (response== null || response.length()==0){
                    Log.i("hkl", "googlepay.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i("hkl","订单确认：平台返回======"+response.toString());

                if (errcode==0 ||errcode==40007){
                    try{

                        JSONObject ret = (JSONObject)response.opt( "data" );

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
                        Log.i("hkl", e.toString());
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i("hkl","googlepay.html：errcode error!=="+ response.toString());

                    leqiLoginBack.back_loginfail(saction, errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("paycheck", -1001);
                Log.i( "hkl", "googlepay.html:onFailure" );
            }
        } );
    }

    //发起内购，第一步提交平台
    public void purchase_order(String productid,  int pay_money, String package_name, String game_user_id){

        Log.i("hkl","充值新订单：平台请求======"+"account_id=" + mUserInfo.UserID
                + "&auth_code=" + Utils.getEncodeKey("paycheck")+"&product_id="+productid +"&package_name="+package_name
                + "&platform=" + "android" +"&device_id="+mUserInfo.DeviceiD +"&pay_money="+pay_money);
        NetUtils.doPost( PlatformConfig.postURL + "payorder.html", "account_id=" + mUserInfo.UserID
                + "&auth_code=" + Utils.getEncodeKey("payform")+"&product_id="+productid +"&package_name="+package_name
                + "&platform=" + "android" +"&device_id="+mUserInfo.DeviceiD +"&pay_money="+pay_money +"&game_user_id="+game_user_id , new NetUtils.HttpResponseCallBack() {
            @Override
            public void onSuccess(JSONObject response) {


                String saction ="payorder";
                if (response== null || response.length()==0){
                    Log.i("hkl", "payorder.html  response is null");
                    leqiLoginBack.back_loginfail(saction,-1001);
                    return;
                }

                int errcode = response.optInt( "errcode" );
                Log.i("hkl","订单：平台返回======"+response.toString());

                if (errcode ==0){
                    try{

                        JSONObject ret = (JSONObject)response.opt( "data" );

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
                        Log.i("hkl", e.toString());
                        leqiLoginBack.back_loginfail(saction, errcode);
                        e.printStackTrace();
                    }
                }
                else {
                    Log.i("hkl","googlepay.html：errcode error!=="+ response.toString());

                    leqiLoginBack.back_loginfail(saction, errcode);
                }

            }

            @Override
            public void onFailure() {
                leqiLoginBack.back_loginfail("payorder", -1001);
                Log.i( "hkl", "googlepay.html:onFailure" );
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
            // shouldn't ever happen when putting String objects into a JSONObject,
            // it can only happen when putting NaN or INFINITE doubles or floats into it
        }
    }
    public void setLeqiLoginBack(LeqiLoginBack leqiLoginBack){
        this.leqiLoginBack =leqiLoginBack;
    }
    public interface LeqiLoginBack{
        /*
        void back_login_by_device_success(JSONObject json);
        void back_login_by_gg_success(JSONObject json);
        void back_login_by_fb_success(JSONObject json);
        void back_binding_success(JSONObject json);
        void back_unbinding_gg(JSONObject json);
        void back_exchange_success(JSONObject json);
        void back_account_select_success(JSONObject json);
        */
        void back_pay_by_gg_success(JSONObject json);
        void back_pay_by_order_success(JSONObject json);
        void back_loginfail(String saction, int retcode);
        void back_from_pass(JSONObject json);
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
            _country =DeviceInfo.getCountry();
            mUserInfo.game_userid=_game_userid=sgame_userid;



            NetUtils.doPost(PlatformConfig.postURL + "submit.html", "account_id=" + mUserInfo.UserID +
                            "&device_id=" + mUserInfo.DeviceiD + "&auth_code=" + Utils.getEncodeKey("submit") +
                            "&os_version=" + _os_version + "&phone_model=" + _devicetype + "&resolving=" + _resolution +
                            "&operator=" + "" + "&language=" + _language + "&app_version=" + _appversion + "&googlefcm=" + mUserInfo.fcm_token+
                            "&gg_nickname="+mUserInfo.google_user_name+"&gg_email=" +mUserInfo.google_user_email +
                            "&fb_nickname="+mUserInfo.facebook_user_name +"&fb_email="+mUserInfo.facebook_user_email+
                            "&game_userid="+_game_userid,
                    new NetUtils.HttpResponseCallBack() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            String saction ="submit";
                            if (response == null || response.length() == 0) {
                                Log.i( "hkl", "googlepay.htm:null respone" );

                                return;
                            }

                            Log.i("hkl","提交用户信息：平台返回======"+response.toString());


                        }

                        @Override
                        public void onFailure() {
                            Log.i("hkl","提交用户信息：失败");
                        }
                    });


            final JSONObject json = new JSONObject();
            fillJSONIfValuesNotEmpty(json,
                    "account_id", mUserInfo.UserID,
                    "login_key", mUserInfo.loginkey,
                    "server_id", mUserInfo.server_id,
                    "gg_alias", mUserInfo.google_user_name,
                    "fb_alias", mUserInfo.facebook_user_name,
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

            Log.i("hkl", "提交用户信息======="+json.toString());
            return json;

        } catch (Exception e) {
            Log.i("hkl", "获取玩家信息失败=");
            e.printStackTrace();
            return null;
        }
    }
}
