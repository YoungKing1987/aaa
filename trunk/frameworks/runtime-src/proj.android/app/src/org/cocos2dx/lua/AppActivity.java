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
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import android.os.Build;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import org.cocos2dx.lib.Cocos2dxLuaJavaBridge;

public class AppActivity extends Cocos2dxActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleLogin.GoogleSignListener,leqipass.LeqiLoginBack,FaceBookLogin.FacebookListener
{
    public static leqipass tmp;
    public GoogleLogin googleLogin;
    public FaceBookLogin faceBookLogin;
    public static int useSDK = -1;
    public Thread thread_ =null;
    private GoogleBillingUtil googleBillingUtil =null;
    private MyOnPurchaseFinishedListener mOnPurchaseFinishedListener = new MyOnPurchaseFinishedListener();//购买回调接口
    private MyOnQueryFinishedListener mOnQueryFinishedListener = new MyOnQueryFinishedListener();//查询回调接口
    private MyOnStartSetupFinishedListener mOnStartSetupFinishedListener = new MyOnStartSetupFinishedListener();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.setEnableVirtualButton(false);
        super.onCreate(savedInstanceState);
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
        // DO OTHER INITIALIZATION BELOW
        tmp =leqipass.shareInstance();
        tmp.init(this, "https://account-mafia.5stargame.com/api/","","");
        tmp.setLeqiLoginBack(this);

        //
    }

    public static  void callJavaMethod(final int isUseSDK){
//        runOnUIThread(new Runnable() {
//            @Override
//            public void run() {
//                Cocos2dxLuaJavaBridge.callLuaFunctionWithString(luaFuncCallback, "success");
//                Cocos2dxLuaJavaBridge.releaseLuaFunction(luaFuncCallback);
//            }
//        });
        useSDK = isUseSDK;
//        AppActivity app = (AppActivity)getContext();
//        app.runOnUiThread(new Runnable()
//        {
//            @Override
//            public void run()
//            {
//                tmp.loginByDeviceID();
//            }
//        });

    }

    private class MyOnStartSetupFinishedListener implements GoogleBillingUtil.OnStartSetupFinishedListener
    {
        @Override
        public void onSetupSuccess() {

            //messageShow("未消耗=" + googleBillingUtil.queryPurchasesInApp().toString());
            //必须先消耗掉同条目的商品才能内购下一个
            List<Purchase> list = googleBillingUtil.queryPurchasesInApp();

            if (list.size() > 0) {
                //重新提交平台，返回后消耗---- 平台须查询重复订单
                tmp.purchase(list.get(0).getOriginalJson(), list.get(0).getSignature());

                //googleBillingUtil.consumeAsync(list.get(0).getPurchaseToken());//消耗商品
            }
        }

        @Override
        public void onSetupFail(int responseCode) {
            //messageShow("onSetupFail="+responseCode );
        }

        @Override
        public void onSetupError() {
           // messageShow("onSetupError=" );
        }
//...;
    }
    //查询商品信息回调接口
    private class MyOnQueryFinishedListener implements GoogleBillingUtil.OnQueryFinishedListener
    {
        @Override
        public void onQuerySuccess(List<SkuDetails> list) {
            //messageShow("onQuerySuccess"+list.toString() );
        }

        @Override
        public void onQueryFail(int responseCode) {
            Log.i("hkl", "onQueryFail");
        }

        @Override
        public void onQueryError() {
            Log.i("hkl", "onQueryError");
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
            Log.i("hkl", "onPurchaseFail" +responseCode);
        }

        @Override
        public void onPurchError() {
            Log.i("hkl", "onPurchError");
        }
//...;
    }

    //平台返回
    @Override
    public  void back_from_pass(JSONObject json) {
        String spro_id = json.toString();
        Log.i("hkl", "qwe收到错误="+json.toString());
        Log.i("hkl", "qwe收到错误11="+json.optString("data").toString());
//        this.runOnGLThread(new Runnable()
////        {
////            @Override
////            public void run()
////            {
////                Cocos2dxLuaJavaBridge.callLuaFunctionWithString(luaFuncCallback,spro_id);
////                //Cocos2dxLuaJavaBridge.releaseLuaFunction(funC);
////            }
////        });
        String action =json.optString("action");
        if (action.equals("login")){
            //硬件码登录

            if (json.optString("server_status").equals("99"))
            {
                //服务器维护状态  json.optString("notice") 是维护公告
                Log.i("hkl",json.optString("notice"));
                return;
            }

            if (googleLogin == null) {
                googleLogin = new GoogleLogin(this, this, 1001);
            }
            googleLogin.setGoogleSignListener(this);

            if (thread_ != null && thread_.isAlive()) {
                return;
            }

            //需要登录GG账号
            if (googleLogin.googlestatus <=0){
                return;
            }
            thread_ = new Thread() {
                @Override
                public void run() {
                    googleLogin.signIn();
                }
            };
            thread_.start();
        }
        else if(action.equals("select")){
            //进度不一致，选择新进度并提交平台返回
            //在此需要重新加载游戏（账号已 更换）
        }
        else if(action.equals("exchange")){
            //切换账号
            //在此需要重新加载游戏（账号已 更换）
            if (json.optString("account_id").equals(json.optString("account_id_ori"))){
                //如果切换的账号和当前账号相同，则提示用户，不用重新加载游戏
                Log.i("hkl","要切换的账号和当前账号相同，结束");
            }
            else
            {
                Log.i("hkl","此处重新加载游戏进度");
            }

        }
        else if(action.equals("binding_gg")){
            //绑定GG
            String strcode =json.optString("errcode");
            if (strcode.equals("0")) { //绑定成功
                Log.i("hkl", "GG绑定成功");
            }
            else if(strcode.equals("20003")){
                Log.i("hkl", "当前账号已绑定过GG");
            }
            else if(strcode.equals("20006")){

            }
        }
        else if(action.equals("binding_fb")){
            //绑定FB
            String strcode =json.optString("errcode");
            if (strcode.equals("0")) { //绑定成功
                Log.i("hkl", "FB绑定成功");
            }
            else if(strcode.equals("20002")){
                Log.i("hkl", "当前账号已绑定过FB");
            }
            else if(strcode.equals("20005")){
                Log.i("hkl", "此FB账号已绑定过其他设备");
            }

        }
        else if(action.equals("unbind")){
            //解除绑定成功
            //更新绑定设置显示，暂不做其他处理
        }
        else if(action.equals("server")){
            if (json.optString("errcode").equals("0")){
                //切换服务器成功,程序需要重新登录
            }
        }
        else if(action.equals("version")){
            Log.i("hkl", json.optString("version"));
            tmp.loginByDeviceID();
        }
        else if(action.equals("getserver")){
            Log.i("hkl", json.toString());
        }

        toLuaFunC(useSDK,spro_id);
      // Cocos2dxLuaJavaBridge.callLuaFunctionWithString(1,"fslkjfalkdfjsldkj");
    }

    public void toLuaFunC(final int func, final String msg)
    {
        if (null != this)
        {
            this.runOnGLThread(new Runnable()
            {
                @Override
                public void run()
                {
                    Log.i("hkl", "1111111111111111111111111111111");
                    //int i = Cocos2dxLuaJavaBridge.callLuaGlobalFunctionWithString("hqxpcall_error",msg);
                    //Cocos2dxLuaJavaBridge.releaseLuaFunction(funC);
                   // Log.i("hkl", "444444111111111111+"+i);
                }
            });
        }
    }



    //平台错误信息返回
    @Override
    public void back_loginfail(String saction, int retcode){
        Log.i("hkl", "回调收到错误="+saction+","+retcode);
        if (saction.equals("login")){//硬件码登录
            //retcode:
            //1001 硬件码获取失败
            //<0网络错误
            //平台返回错误
        }
        /*
//saction
    login =登录
    bind =绑定
    payorder =新订单
    paycheck =订单确认
    server=换服务器
    submit=提交用户信息
    version=初始化
    exchange=切换账号


-1001 网络错误
20002	当前账号已经绑定过 fb
20003	当前账号已经绑定过 gg
20004	当前账号已经绑定过 gc
20005	此FB已经绑定其它设备，不能再进行绑定
20006	此GG已经绑定其它设备，不能再进行绑定
20007	此GC已经绑定其它设备，不能再进行绑定

40001	当前服务器不存在
40002	支付条目ID或条目序号不存在
40003	订单信息出错，未提交
40004	订单信息出错，无法识别
40005	订单信息出错，应用包名出错
40006	订单信息出错，验证失败


         */
        Log.i("hkl","retcode="+retcode);
        //Toast.makeText(MainActivity.this, "登录平台失败",Toast.LENGTH_LONG ).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("hkl", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);


        if(requestCode==googleLogin.requestCode){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googleLogin.handleSignInResult( result );
        }
        else if(requestCode ==64206)

        {faceBookLogin.getCallbackManager().onActivityResult(requestCode, resultCode, data);}
    }

    @Override
    public void googleLoginSuccess(int requestCode){

        if(requestCode ==1001){  //绑定GG
            tmp.Binding(1001);
        }
        else if(requestCode==1002) {//手动绑定
            tmp.Binding(1002);
        }
        else if(requestCode==1003){ //切换账号
           // tmp.exChange(1003);
        }
    }
    @Override
    public void googleLoginFail(){
        //未登录状态会调用
       // messageShow("googleLoginFail");
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
        //messageShow("facebookLoginSuccess" );
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
        //messageShow("back_pay_by_gg_success=====" +json.toString()+"\n"+"开始提交GG消耗商品1");

        json.optString("paymoney");///此为充值成功金额
        googleBillingUtil.consumeAsync(json.optString("purchasetoken"));

    }

    //平台充值第一步订单接口返回
    @Override
    public void back_pay_by_order_success(JSONObject json){

        //messageShow("back_pay_by_order_success=====" +json.toString()+"\n"+"开始GG内购商品");
        String spro_id = json.optString("product_id");
        googleBillingUtil.purchaseInApp(spro_id );

    }

    @Override
    public void facebookLoginFail() {
        Log.i("hkl", "FB登录失败或取消登录");
    }
}
