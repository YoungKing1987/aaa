/****************************************************************************
Copyright (c) 2008-2010 Ricardo Quesada
Copyright (c) 2010-2016 cocos2d-x.org
Copyright (c) 2013-2016 Chukong Technologies Inc.
Copyright (c) 2017-2018 Xiamen Yaji Software Co., Ltd.
 
http://www.cocos2d-x.org

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
****************************************************************************/
package org.cocos2dx.lua;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import org.cocos2dx.lib.Cocos2dxActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import leqi.com.leqipassportsdk.comm.FaceBookLogin;
import leqi.com.leqipassportsdk.comm.GoogleBillingUtil;
import leqi.com.leqipassportsdk.comm.GoogleLogin;
import leqi.com.leqipassportsdk.leqipass;
import leqi.com.leqipassportsdk.mUserInfo;

import org.cocos2dx.lib.Cocos2dxLuaJavaBridge;
import org.json.JSONObject;
import com.dipan.beenpc.sdk.Countly;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import android.os.Build;
import android.view.WindowManager;
import admin.*;
import android.view.WindowManager.LayoutParams;

import org.cocos2dx.lua.LuaPlatform;

public class AppActivity extends Cocos2dxActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleLogin.GoogleSignListener,leqipass.LeqiLoginBack,FaceBookLogin.FacebookListener
{
    public leqipass tmp;
    public int mLuaCallBack = 0;
    private LuaPlatform platform = null;
    public GoogleLogin googleLogin;
    public FaceBookLogin faceBookLogin;
    public static int useSDK = -1;
    public Thread thread_ =null;
    private GoogleBillingUtil googleBillingUtil =null;
    public Countly countly;
    String uid = "";
    private MyOnPurchaseFinishedListener mOnPurchaseFinishedListener = new MyOnPurchaseFinishedListener();//购买回调接口
    private MyOnQueryFinishedListener mOnQueryFinishedListener = new MyOnQueryFinishedListener();//查询回调接口
    private MyOnStartSetupFinishedListener mOnStartSetupFinishedListener = new MyOnStartSetupFinishedListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
        super.setKeepScreenOn(true);
        // Workaround in https://stackoverflow.com/questions/16283079/re-launch-of-activity-on-home-button-but-only-the-first-time/16447508
        if (!isTaskRoot()) {
            // Android launched another instance of the root activity into an existing task
            //  so just quietly finish and go away, dropping the user back into the activity
            //  at the top of the stack (ie: the last state of this task)
            // Don't need to finish it again since it's finished in super.onCreate .
            return;
        }
        // Make sure we're running on Pie or higher to change cutout mode
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // Enable rendering into the cutout area
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            getWindow().setAttributes(lp);
        }

        platform =LuaPlatform.shareInstance();
        platform.init(this);
        // DO OTHER INITIALIZATION BELOW
        countly = Countly.sharedInstance();
        countly.init(this,"https://countly-mafia.5stargame.com/api.html","31651830a58984cccf9e5daa72e9e9cc");
        tmp =leqipass.shareInstance();
        tmp.init(this);
        tmp.setLeqiLoginBack(this);
        //https://account-mafia.5stargame.com/api/
        //
        initGoogleLogin();
        initFaceBookLogin();
        //tmp.game_init("https://account-mafia.5stargame.com/api/","","");
    }

    public void initSdk(final String url ,final String var){
        //initGoogleLogin();
        //initFaceBookLogin();
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmp.game_init(url,"",var);
            }
        });

    }

    public void setLuaCallBack(final int lua){
        mLuaCallBack = lua;
    }

    void initFaceBookLogin(){
        if (faceBookLogin == null) {
            faceBookLogin = new FaceBookLogin(this);
            faceBookLogin.setFacebookListener(this);
        }
    }

    void initGoogleLogin()
    {
        if (googleLogin == null) {
            googleLogin = new GoogleLogin(this, this, 1002);
            googleLogin.setGoogleSignListener(this);
            googleLogin.requestCode =1002;
        }
    }

    public void CheckPayOrder(){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initGoogleBillingUtil();
            }
        });

    }

    public void lianxi(final String uid){
        beenpcSdkBuilder sdkBuilder = new beenpcSdkImplBuilder();
        beenpcSdkDirector sdkDirector = beenpcSdkDirector.getInstance(sdkBuilder);
        sdkDirector.setUrl("https://feedback-mafia.5stargame.com/home/index/index.html");
        sdkDirector.setGameKey("JmxAYQTUvlzIJhfGwzacmwpltQFMVSFsShVcALcleiZgtBUuXplidIqPPSNRTrNx");
        sdkDirector.setUserID(uid);
        sdkDirector.setModID(mUserInfo.fcm_token);
        sdkDirector.setSvrID(mUserInfo.server_id);
        sdkDirector.startActivity(this);
    }

    public void gameserver_list(final  String areaid){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmp.gameserver_list(areaid);
            }
        });

    }

    void initGoogleBillingUtil(){
        if (googleBillingUtil==null){
            googleBillingUtil = new GoogleBillingUtil(this,mOnPurchaseFinishedListener,mOnQueryFinishedListener)
                    .setOnPurchaseFinishedListener(mOnPurchaseFinishedListener)
                    .setOnQueryFinishedListener(mOnQueryFinishedListener)
                    .setOnStartSetupFinishedListener(mOnStartSetupFinishedListener)
                    .build();
        }
    }

    public void pay(final String json){

        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject josnObj = new JSONObject(json);
                    String productName =  josnObj.optString("productName");
                    String productDesc =  josnObj.optString("productDesc");
                    int price =  josnObj.optInt("price");
                    String uid =  josnObj.optString("uid");
                    List<Purchase> list = googleBillingUtil.queryPurchasesInApp();
                    if (list!=null && list.size()>0) {
                        tmp.purchase(list.get(0).getOriginalJson(), list.get(0).getSignature());
                    }
                    else{
                        tmp.purchase_order(productName, price, productDesc,uid);//pay_money 金额单位  分
                    }
                } catch (Exception e) {
                    messageShow("pay"+e.toString());
                }
            }
        });
    }

    public void gameserver_select(final  String sid){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tmp.gameserver_select(sid);
            }
        });

    }

    public void bindPlatform(String platform){
        if (platform.equals("FB")){
            faceBookLogin.login(3001);//
        }else if(platform.equals("CFB")){
            faceBookLogin.login(3003);
        }else if(platform.equals("GG")){
            googleLogin.signIn();
        }else if(platform.equals("CFB1")) {
            tmp.exChange(3003, "1");
        }else if(platform.equals("CFB2")) {
            tmp.exChange(1003, "11");
        }else if(platform.equals("CGG")) {
            if (googleLogin == null) {
                googleLogin = new GoogleLogin(this, this, 1003);
                googleLogin.setGoogleSignListener(this);
            }
            else{googleLogin.requestCode =1003;}
            googleLogin.signOut();
        }else if(platform.equals("CGG1")){
            tmp.exChange(1003, "1");
        }
    }

    private class MyOnStartSetupFinishedListener implements GoogleBillingUtil.OnStartSetupFinishedListener
    {
        @Override
        public void onSetupSuccess() {
            //必须先消耗掉同条目的商品才能内购下一个
            try {
                List<Purchase> list = googleBillingUtil.queryPurchasesInApp();
                if (list!=null && list.size() > 0) {
                    messageShow("未消耗=" + list.get(0).getOriginalJson());
                    //重新提交平台，返回后消耗---- 平台须查询重复订单
                    tmp.purchase(list.get(0).getOriginalJson(), list.get(0).getSignature());
                }
            }catch (Exception e){
            }


        }
        @Override
        public void onSetupFail(int responseCode) {
            messageShow("onSetupFail="+responseCode );
        }
        @Override
        public void onSetupError() {
            messageShow("onSetupError=" );
        }
//...;
    }

    public void  OnPayCallBack(boolean Success, String paymoney , String platform) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action","paycheck");
            jsonObj.put("paymoney",paymoney);
            jsonObj.put("platform",platform);
            if(Success) {
                jsonObj.put("code", 0);
            }
            else {
                jsonObj.put("code", -1);
            }
        }catch (Exception e){

        }
        messageShow(jsonObj.toString());
        dealMessage(jsonObj.toString());
//        if(mLuaCallBack > 0 ){
//            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,jsonObj.toString());
//        }

        //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", jsonObj.toString());
    }

    //查询商品信息回调接口
    private class MyOnQueryFinishedListener implements GoogleBillingUtil.OnQueryFinishedListener
    {
        @Override
        public void onQuerySuccess(List<SkuDetails> list) {
            messageShow("onQuerySuccess"+list.toString() );
            //pay("a");
        }

        @Override
        public void onQueryFail(int responseCode) {
            OnPayCallBack(false,"queryFail","google");
        }

        @Override
        public void onQueryError() {
            OnPayCallBack(false,"query","google");
        }

    }

    //购买商品回调接口
    private class MyOnPurchaseFinishedListener implements GoogleBillingUtil.OnPurchaseFinishedListener
    {
        @Override
        public void onPurchaseSuccess(List<Purchase> list) {
            // Log.i("hkl", "onPurchaseSuccess="+list.toString());
            //[Purchase. Json: {"orderId":"GPA.3354-2454-9671-28068","packageName":"com.leqi.passport","productId":"10001","purchaseTime":1524721463378,"purchaseState":0,"purchaseToken":"aphophpafbgjjnnljhpjiplg.AO-J1OzvRg5qsmhtW1d8bDuhKT9Lfx5_rHZ1VUCT44Ld8aMDMLy7d9k4tgGBkSDSgq-7jHmcGh858Z9o-Vbga_auPpZb7bnJ63gM5NP5tRI5zoZf4CyhSkg"}]

            Log.i("hkl","getOriginalJson======="+list.get(0).getOriginalJson());
            Log.i("hkl", "getSignature======="+list.get(0).getSignature());

            tmp.purchase(list.get(0).getOriginalJson(), list.get(0).getSignature());
        }

        @Override
        public void onPurchaseFail(int responseCode) {
            OnPayCallBack(false,"purchase","google");
        }

        @Override
        public void onPurchError() {
            OnPayCallBack(false,"purcherror","google");
        }
//...;
    }

    //平台返回
    @Override
    public  void back_from_pass(JSONObject json) {
        String action = json.optString("action");
        if (action.equals("version")) {
            dealMessage(json.toString());
//            if(mLuaCallBack > 0 ){
//                Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,json.toString());
//            }
            ///UnityPlayer.UnitySendMessage("PlatformManager", "OnInitSuc", json.optString("audit_version"));
        }
        else {
            messageShow(json.toString());
            dealMessage(json.toString());
//            if(mLuaCallBack > 0 ){
//                Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,json.toString());
//            }
            //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", json.toString());
            if (action.equals("login")){
                //CheckPayOrder();
                String gg_client =json.optString("gg_client");  //1代表已绑定GG
                if (gg_client.equals("0")) {
                    googleLogin.requestCode =1001;
                    googleLogin.signIn();
                }

            }
        }
    }

    public  void  getUserInfo(final String sgame_userid){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                JSONObject json = tmp.getUserInfo(sgame_userid);
                if (json!=null) {
                    dealMessage(json.toString());
//                    if(mLuaCallBack > 0 ){
//                        Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,json.toString());
//                    }
                    //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", json.toString());
                }
            }
        });

    }

    //--------------------------------------count----------------------------------------------
    public void CountlyInfo(final String userid,final  String serverid,final  String sources,final  String appver,final  String userLevel){
        uid = userid;
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countly.setinfo(userid,serverid,sources,appver,userLevel);
                }
            });
    }

    public  void recordEvent(final String key){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countly.recordEvent(key);
            }
        });

    }
    public  void recordNewTaskEvent(final String key){
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                countly.recordNewTaskEvent("newtask",key);
            }
        });

    }



    //平台错误信息返回
    @Override
    public void back_loginfail(String saction, int retcode){
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action",saction);
            jsonObj.put("code", retcode);
        }catch (Exception e){

        }
        messageShow(jsonObj.toString());
        dealMessage(jsonObj.toString());
//        if(mLuaCallBack > 0 ){
//            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,jsonObj.toString());
//        }
        //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", jsonObj.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            messageShow("requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
            if(googleLogin!=null && requestCode==googleLogin.requestCode){
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                googleLogin.handleSignInResult( result );
            }
            else if(requestCode ==64206)
            {
                faceBookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);
            }
        }catch (Exception e){}
    }

    @Override
    public void googleLoginSuccess(int requestCode){

        if(requestCode ==1001){  //绑定GG
            tmp.Binding(1001);
        }       else if(requestCode==1002) {//手动绑定
            tmp.Binding(1002);
        }
        else if(requestCode==1003){ //切换账号
            tmp.exChange(1003,"0");
        }
    }
    @Override
    public void googleLoginFail(){
        //未登录状态会调用
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action","bind");
            jsonObj.put("code", 4);
        }catch (Exception e){

        }
        dealMessage(jsonObj.toString());
//        if(mLuaCallBack > 0 ){
//            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,jsonObj.toString());
//        }
        //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", jsonObj.toString());
        //未登录状态会调用
        messageShow(jsonObj.toString());
    }
    @Override
    public void googleLogoutSuccess(){
        Log.i("hkl","googleLogoutSuccess");
        //切换账号时，先退出GG账号，返回googleLogoutSuccess后再走绑定流程

        googleLogin.signIn();

    }
    @Override
    public void googleLogoutFail(){
        Log.i("hkl","googleLogoutFail");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("hkl","google登录-->onConnectionFailed,connectionResult=="+connectionResult);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Log.i("hkl", "onStart..........");

        //mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //if (mGoogleApiClient.isConnected()) {
        //    mGoogleApiClient.disconnect();
        //}
    }

    @Override
    public void facebookLoginSuccess(int requestCode) {
        messageShow("facebookLoginSuccess" );
        if (requestCode ==3001){
            tmp.Binding(3001);
        }
        else if(requestCode==3003){
            tmp.exChange(3003,"0");
        }
    }

    //平台充值接口返回
    @Override
    public void back_pay_by_gg_success(JSONObject json){
        //消耗内购商品
        messageShow("back_pay_by_gg_success=====" +json.toString()+"\n"+"开始提交GG消耗商品1");

        OnPayCallBack(true,json.optString("paymoney"),"google");
        if (countly!=null)
           countly.recordPayEvents(uid,json.optString("paymoney"),"google");
        dealMessage(json.toString());
//        if(mLuaCallBack > 0 ){
//            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,json.toString());
//        }
        //UnityPlayer.UnitySendMessage("PlatformManager", "recordPayEvents", json.optString("paymoney"));
        googleBillingUtil.consumeAsync(json.optString("purchasetoken"));

    }

    //平台充值第一步订单接口返回
    @Override
    public void back_pay_by_order_success(JSONObject json){

        messageShow("back_pay_by_order_success=====" +json.toString()+"\n"+"开始GG内购商品");
        String spro_id = json.optString("product_id");
        googleBillingUtil.purchaseInApp(spro_id );

    }

    public void messageShow(String str){
        Log.i("sdk", str);
    }

    public void dealMessage(final  String str){
        this.runOnGLThread(new Runnable() {
            @Override
            public void run() {
                if(mLuaCallBack > 0 ){
                    Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,str);
                }
            }
        });

    }

    @Override
    public void facebookLoginFail(int  requestCode) {
        JSONObject jsonObj = new JSONObject();
        try {
            jsonObj.put("action","bind");
            jsonObj.put("code", requestCode);
        }catch (Exception e){
        }
        messageShow(jsonObj.toString());
        dealMessage(jsonObj.toString());
//        if(mLuaCallBack > 0 ){
//            Cocos2dxLuaJavaBridge.callLuaFunctionWithString(mLuaCallBack,jsonObj.toString());
//        }
        //UnityPlayer.UnitySendMessage("PlatformManager", "OnGetUserInfoSuc", jsonObj.toString());
    }
}
