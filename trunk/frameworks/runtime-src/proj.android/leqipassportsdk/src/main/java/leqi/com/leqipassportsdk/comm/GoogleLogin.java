package leqi.com.leqipassportsdk.comm;

/**
 * Created by hkl on 2018/3/16.
 */

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import leqi.com.leqipassportsdk.AuthCode;
import leqi.com.leqipassportsdk.mUserInfo;

public class GoogleLogin {

    public int requestCode = 1001 ;
    private FragmentActivity activity ;

    public GoogleSignInOptions gso ;
    public GoogleApiClient mGoogleApiClient ;
    public GoogleApiClient.OnConnectionFailedListener listener ;
    private GoogleSignListener googleSignListener ;
    public int googlestatus =0;

    public  GoogleLogin(FragmentActivity activity ,  GoogleApiClient.OnConnectionFailedListener listener , int loginType){
        this.activity = activity ;
        this.listener = listener ;
        this.requestCode =loginType;

        //初始化谷歌登录服务

        GoogleApiAvailability googleApiAvailability =GoogleApiAvailability.getInstance();
        int resultCode =googleApiAvailability.isGooglePlayServicesAvailable(activity);

        if(resultCode != ConnectionResult.SUCCESS){
            //Toast.makeText( activity, "无GooglePlay服务", Toast.LENGTH_SHORT).show();
            //googleSignListener.googleLoginFail(-1);
            googlestatus =-1;

            ////

            ////

        }
        else
        {
            //Toast.makeText( activity, "支持GooglePlay服务", Toast.LENGTH_SHORT).show();
            googlestatus =1;
        }


        gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                //.requestIdToken( "1000758686281-v86k4h86fpavkeak9b2f0jdkkei82qlk.apps.googleusercontent.com")
                .requestProfile()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder( activity )
                .enableAutoManage( activity , listener )
                //.addConnectionCallbacks(activity.)
                .addApi( Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    /**
     * 登录
     */
    public void signIn() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        activity.startActivityForResult(signInIntent, requestCode);


    }

    /**
     * 退出登录
     */
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if ( status.isSuccess() ){
                            if ( googleSignListener != null ){
                                googleSignListener.googleLogoutSuccess();
                            }
                        }else {
                            if ( googleSignListener!= null ){
                                googleSignListener.googleLogoutFail();
                            }
                        }
                    }
                });
    }

    public String handleSignInResult(GoogleSignInResult result) {
        Log.i("hkl","Google 登录回调"+result.toString());
        String res = "" ;
        if (result.isSuccess()) {
            //登录成功
            GoogleSignInAccount acct = result.getSignInAccount();
            res = "登录成功"
                    + "用户名为：" + acct.getDisplayName()
                    + "  邮箱为：" + acct.getEmail()
                    + " token为：" + acct.getIdToken()
                    + " 头像地址为：" + acct.getPhotoUrl()
                    + " Id为：" + acct.getId()
                    + " GrantedScopes为：" + acct.getGrantedScopes() ;
            Log.i("hkl", "GG信息获取成功=====:"+res);

            mUserInfo.google_id = AuthCode.MD5(acct.getId());
            mUserInfo.google_user_email =acct.getEmail();
            mUserInfo.google_user_name =acct.getDisplayName();

            //Toast.makeText( activity, res, Toast.LENGTH_SHORT).show();
            if ( googleSignListener != null ){
                googleSignListener.googleLoginSuccess(requestCode);
            }
        } else {
            // Signed out, show unauthenticated UI.
            mUserInfo.google_id = "";
            mUserInfo.google_user_email ="";
            mUserInfo.google_user_name ="";
            res = "-1" ;  //-1代表用户退出登录了 ， 可以自定义
            //Toast.makeText( activity , "退出登录", Toast.LENGTH_SHORT).show();
            if ( googleSignListener != null ){
                googleSignListener.googleLoginFail();

            }
        }
        return res ;
    }


    public void setGoogleSignListener( GoogleSignListener googleSignListener ){
        this.googleSignListener = googleSignListener ;
    }

    public interface GoogleSignListener {
        void googleLoginSuccess(int requestCode);
        void googleLoginFail() ;
        void googleLogoutSuccess();
        void googleLogoutFail() ;
    }

}