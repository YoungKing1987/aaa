package leqi.com.leqipassportsdk.comm;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import leqi.com.leqipassportsdk.mUserInfo;

public class FaceBookLogin {

    private Activity activity ;
    private CallbackManager callbackManager ;
    private FacebookListener facebookListener ;
    private List<String> permissions = Collections.<String>emptyList();
    private LoginManager loginManager;


    public int requestCode =3001;



    public FaceBookLogin( Activity activity ){
        this.activity = activity ;



        //初始化facebook登录服务
        callbackManager = CallbackManager.Factory.create() ;


        getLoginManager().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // login success
                AccessToken accessToken = loginResult.getAccessToken();
                getLoginInfo(accessToken);

                LoginManager.getInstance().logOut();
            }

            @Override
            public void onCancel() {
                //取消登录
                Log.i("hkl", "取消FB登录");
                facebookListener.facebookLoginFail();
            }

            @Override
            public void onError(FacebookException error) {
                //登录出错
                Log.i("hkl", "Facebook login Error==="+error.toString());
                //if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
               // }
            }

        });

        permissions = Arrays.asList("public_profile");
                //.asList("email", "user_likes", "user_status", "user_photos", "user_birthday", "public_profile", "user_friends") ;
    }

    /**
     * 登录
     */
    public void login(int itype){
        requestCode = itype;

        if (itype==3001){
            //绑定
            getLoginManager().setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
            //getLoginManager().setLoginBehavior(loginManager.getLoginBehavior());
        }
        else
        {
            //logout();
            if(AccessToken.getCurrentAccessToken()!=null) {
                LoginManager.getInstance().logOut();
                Log.i("hkl", "Facebook，登出");
            }
            //getLoginManager().setLoginBehavior(loginManager.getLoginBehavior());
            getLoginManager().setLoginBehavior(LoginBehavior.WEB_VIEW_ONLY);
        }
       // getLoginManager().setLoginBehavior(LoginBehavior.WEB_ONLY);
        getLoginManager().setDefaultAudience(getLoginManager().getDefaultAudience());
        //getLoginManager().setLoginBehavior(getLoginManager().getLoginBehavior());
        getLoginManager().logInWithReadPermissions( activity, permissions);

    }

    public void login_change(){

        getLoginManager().logOut();
        login(3001);
    }

    /**
     * 退出
     */
    public void logout(){
        String logout = activity.getResources().getString(
                leqi.com.leqipassportsdk.R.string.com_facebook_loginview_log_out_action);
        String cancel = activity.getResources().getString(
                leqi.com.leqipassportsdk.R.string.com_facebook_loginview_cancel_action);
        String message;
        Profile profile = Profile.getCurrentProfile();
        if (profile != null && profile.getName() != null) {
            message = String.format(
                    activity.getResources().getString(
                            leqi.com.leqipassportsdk.R.string.com_facebook_loginview_logged_in_as),
                    profile.getName());
        } else {
            message = activity.getResources().getString(
                    leqi.com.leqipassportsdk.R.string.com_facebook_loginview_logged_in_using_facebook);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getLoginManager().logOut();
                        //LoginManager.getInstance().logOut();


                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    /**
     * 获取登录信息
     * @param accessToken
     */
    public void getLoginInfo( AccessToken accessToken ){
        GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (object != null) {
                    String id = object.optString("id");   //比如:1565455221565
                    String name = object.optString("name");  //比如：Zhang San
                    String gender = object.optString("gender");  //性别：比如 male （男）  female （女）
                    String email = object.optString("email");  //邮箱：比如：56236545@qq.com

                    mUserInfo.facebook_id =id;
                    mUserInfo.facebook_user_name =name;
                    mUserInfo.facebook_user_email =email;


                    //获取用户头像
                    JSONObject object_pic = object.optJSONObject("picture");
                    JSONObject object_data = object_pic.optJSONObject("data");
                    String photo = object_data.optString("url");

                    //获取地域信息
                    String locale = object.optString("locale");   //zh_CN 代表中文简体


                    facebookListener.facebookLoginSuccess(requestCode);
                    Log.i("hkl", "fblogin:getinfo======"+object.toString());

                }
                else{
                    mUserInfo.facebook_id ="";
                    mUserInfo.facebook_user_name ="";
                    mUserInfo.facebook_user_email ="";
                    facebookListener.facebookLoginFail();
                }
            }
        }) ;

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,picture,locale,updated_time,timezone,age_range,first_name,last_name");
        //parameters.putString("fields", "public_profile");
        request.setParameters(parameters);
        request.executeAsync() ;
    }

    /**
     * 获取loginMananger
     * @return
     */
    private LoginManager getLoginManager() {
        if (loginManager == null) {
            loginManager = LoginManager.getInstance();

        }
        return loginManager;
    }

    public CallbackManager getCallbackManager(){
        return callbackManager ;
    }

    /**
     * 设置登录简体器
     * @param facebookListener
     */
    public void setFacebookListener( FacebookListener facebookListener ){
        this.facebookListener =facebookListener ;
    }

    public interface FacebookListener {
        void facebookLoginSuccess(int  requestCode);
        void facebookLoginFail() ;
    }


}