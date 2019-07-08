package leqi.com.leqipassportsdk.comm;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
//import com.eyewind.framework.base.BaseApplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GoogleBillingUtil {
    private Activity activity ;
    private String[] inAppSKUS = new String[]{"10002", "fs_gw_product100499"};//内购ID
    private String[] subsSKUS = new String[]{};//订阅ID

    public static final String BILLING_TYPE_INAPP = BillingClient.SkuType.INAPP;//内购
    public static final String BILLING_TYPE_SUBS = BillingClient.SkuType.SUBS;//订阅

    private BillingClient mBillingClient;
    private OnPurchaseFinishedListener mOnPurchaseFinishedListener;
    private OnStartSetupFinishedListener mOnStartSetupFinishedListener ;
    private OnQueryFinishedListener mOnQueryFinishedListener;

    public boolean mIsServiceConnected= false;
    private boolean isAutoConsumeAsync = false;

    public GoogleBillingUtil(Activity activity,OnPurchaseFinishedListener mOnPurchaseFinishedListener,OnQueryFinishedListener mOnQueryFinishedListener) {
        this.activity = activity;
        this.mOnPurchaseFinishedListener = mOnPurchaseFinishedListener;
        this.mOnQueryFinishedListener = mOnQueryFinishedListener;
    }

    public GoogleBillingUtil()
    {

    }



    public GoogleBillingUtil build()
    {

        if(mBillingClient==null)
        {
            synchronized (this)
            {
                if(mBillingClient==null) {

                        //本来想做单实例，但新版api示例是在build之前就要传入购买回调接口，所以没办法做静态了。
                        mBillingClient = BillingClient.newBuilder(this.activity).setListener(new MyPurchasesUpdatedListener(mOnPurchaseFinishedListener)).build();



                }
            }
        }
        synchronized (this)
        {
            if(!mIsServiceConnected)
            {
                startConnection();
            }
        }
        return this;
    }

    public void startConnection()
    {
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(@BillingClient.BillingResponse int billingResponseCode) {
                if (billingResponseCode == BillingClient.BillingResponse.OK) {
                    mIsServiceConnected = true;
                    queryInventoryInApp();
                    //queryInventorySubs();
                    queryPurchasesInApp();
                    if(mOnStartSetupFinishedListener!=null)
                    {
                        mOnStartSetupFinishedListener.onSetupSuccess();
                    }
                }
                else
                {
                    mIsServiceConnected = false;
                    if(mOnStartSetupFinishedListener!=null)
                    {
                        mOnStartSetupFinishedListener.onSetupFail(billingResponseCode);
                    }
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                mIsServiceConnected = false;
                if(mOnStartSetupFinishedListener!=null)
                {
                    mOnStartSetupFinishedListener.onSetupError();
                }
            }
        });
    }

    /**
     * Google购买商品回调接口(订阅和内购都走这个接口)
     */
    private class MyPurchasesUpdatedListener implements PurchasesUpdatedListener
    {

        private OnPurchaseFinishedListener mOnPurchaseFinishedListener ;

        public MyPurchasesUpdatedListener(OnPurchaseFinishedListener onPurchaseFinishedListener) {
            mOnPurchaseFinishedListener = onPurchaseFinishedListener;
        }
        @Override
        public void onPurchasesUpdated(int responseCode, @Nullable List<Purchase> list) {
            if(mOnPurchaseFinishedListener==null)
            {
                return ;
            }
            if(responseCode== BillingClient.BillingResponse.OK&&list!=null)
            {
                if(isAutoConsumeAsync)
                {
                    //消耗商品
                    for(Purchase purchase:list)
                    {
                        if(getSkuType(purchase.getSku()).equals(BillingClient.SkuType.INAPP))
                        {
                            consumeAsync(purchase.getPurchaseToken());
                        }
                    }
                }
                mOnPurchaseFinishedListener.onPurchaseSuccess(list);
            }
            else
            {
                mOnPurchaseFinishedListener.onPurchaseFail(responseCode);
            }
        }
    }

    /**
     * 查询内购商品信息
     */
    public void queryInventoryInApp()
    {
        queryInventory(BillingClient.SkuType.INAPP);
    }

    /**
     * 查询订阅商品信息
     */
    public void queryInventorySubs()
    {
        queryInventory(BillingClient.SkuType.SUBS);
    }

    private void queryInventory(final String skuType) {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBillingClient == null)
                {
                    if(mOnQueryFinishedListener!=null)
                    {
                        mOnQueryFinishedListener.onQueryError();
                    }
                    return ;
                }
                ArrayList<String> skuList = new ArrayList<>();
                if(skuType.equals(BillingClient.SkuType.INAPP))
                {
                    for(String sku:inAppSKUS)
                    {
                        skuList.add(sku);
                    }
                }
                else if(skuType.equals(BillingClient.SkuType.SUBS))
                {
                    for(String sku:subsSKUS)
                    {
                        skuList.add(sku);
                    }
                }
                SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                params.setSkusList(skuList).setType(skuType);
                mBillingClient.querySkuDetailsAsync(params.build(),new MySkuDetailsResponseListener(mOnQueryFinishedListener));
            }
        };
        executeServiceRequest(runnable);
    }

    /**
     * Google查询商品信息回调接口
     */
    private class MySkuDetailsResponseListener implements SkuDetailsResponseListener
    {
        private OnQueryFinishedListener mOnQueryFinishedListener ;
        public MySkuDetailsResponseListener(OnQueryFinishedListener onQueryFinishedListener) {
            mOnQueryFinishedListener = onQueryFinishedListener;
        }

        @Override
        public void onSkuDetailsResponse(int responseCode , List<SkuDetails> list) {

            if(mOnQueryFinishedListener==null)
            {
                return ;
            }
            if(responseCode== BillingClient.BillingResponse.OK&&list!=null)
            {
                mOnQueryFinishedListener.onQuerySuccess(list);
            }
            else
            {
                mOnQueryFinishedListener.onQueryFail(responseCode);
            }
        }

    }

    /**
     * 发起内购
     * @param skuId
     * @return
     */
    public void purchaseInApp(String skuId)
    {
        //
        inAppSKUS =null;
        inAppSKUS = new String[]{skuId};//内购ID
        purchase(skuId,BillingClient.SkuType.INAPP);
    }

    /**
     * 发起订阅
     * @param skuId
     * @return
     */
    public void purchaseSubs(String skuId)
    {
        purchase(skuId,BillingClient.SkuType.SUBS);
    }

    private void purchase(final String skuId,final String skuType)
    {
        if(mIsServiceConnected)
        {
            if(mBillingClient==null)
            {
                if(mOnPurchaseFinishedListener!=null)
                {
                    mOnPurchaseFinishedListener.onPurchError();
                }
            }
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSku(skuId)
                    .setType(skuType)
                    .build();
            mBillingClient.launchBillingFlow(activity,flowParams);


        }
       else
        {
            if(mOnPurchaseFinishedListener!=null)
            {
                mOnPurchaseFinishedListener.onPurchError();
            }
            startConnection();
        }
    }

    /**
     * 消耗商品
     * @param purchaseToken
     */
    public void consumeAsync(String purchaseToken)
    {
        if(mBillingClient==null)
        {
            return ;
        }
        mBillingClient.consumeAsync(purchaseToken, new MyConsumeResponseListener());
    }

    /**
     * Googlg消耗商品回调
     */
    private class MyConsumeResponseListener implements ConsumeResponseListener
    {
        @Override
        public void onConsumeResponse(int responseCode, String s) {
            if (responseCode == BillingClient.BillingResponse.OK) {
                Log.i("hkl", "消耗成功="+s);
            }
        }
    }


    /**
     * 获取已经内购的商品
     * @return
     */
    public List<Purchase> queryPurchasesInApp()
    {
        return queryPurchases(BillingClient.SkuType.INAPP);
    }

    /**
     * 获取已经订阅的商品
     * @return
     */
    public List<Purchase> queryPurchasesSubs()
    {
        return queryPurchases(BillingClient.SkuType.SUBS);
    }

    private List<Purchase> queryPurchases(String skuType)
    {
        if(mBillingClient==null)
        {
            return null;
        }
        if(!mIsServiceConnected)
        {
            startConnection();
        }
        else
        {
            Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(skuType);
            if(purchasesResult!=null)
            {
                if(purchasesResult.getResponseCode()== BillingClient.BillingResponse.OK)
                {
                    List<Purchase> purchaseList =  purchasesResult.getPurchasesList();
                    if(isAutoConsumeAsync)
                    {
                        if(purchaseList!=null)
                        {
                            for(Purchase purchase:purchaseList)
                            {
                                if(skuType.equals(BillingClient.SkuType.INAPP))
                                {
                                    consumeAsync(purchase.getPurchaseToken());
                                }
                            }
                        }
                    }
                    return purchaseList;
                }
            }

        }
        return null;
    }

    /**
     * 获取有效订阅的数量
     * @return -1查询失败，0没有有效订阅，>0具有有效的订阅
     */
    public int getPurchasesSizeSubs()
    {
        List<Purchase > list = queryPurchasesSubs();
        if(list!=null)
        {
            return list.size();
        }
        return -1;
    }

    /**
     * 通过sku获取订阅商品序号
     * @param sku
     * @return
     */
    public int getSubsPositionBySku(String sku)
    {
        return getPositionBySku(sku, BillingClient.SkuType.SUBS);
    }

    /**
     * 通过sku获取内购商品序号
     * @param sku
     * @return
     */
    public int getInAppPositionBySku(String sku)
    {
        return getPositionBySku(sku, BillingClient.SkuType.INAPP);
    }

    private int getPositionBySku(String sku,String skuType)
    {

        if(skuType.equals(BillingClient.SkuType.INAPP))
        {
            int i = 0;
            for(String s:inAppSKUS)
            {
                if(s.equals(sku))
                {
                    return i;
                }
                i++;
            }
        }
        else if(skuType.equals(BillingClient.SkuType.SUBS))
        {
            int i = 0;
            for(String s:subsSKUS)
            {
                if(s.equals(sku))
                {
                    return i;
                }
                i++;
            }
        }
        return -1;
    }

    private void executeServiceRequest(final Runnable runnable)
    {
        if(mIsServiceConnected)
        {
            runnable.run();
        }
        else
        {
            startConnection();
        }
    }

    /**
     * 通过序号获取订阅sku
     * @param position
     * @return
     */
    public String getSubsSkuByPosition(int position)
    {
        if(position>=0&&position<subsSKUS.length)
        {
            return subsSKUS[position];
        }
        else {
            return null;
        }
    }

    /**
     * 通过序号获取订阅sku
     * @param position
     * @return
     */
    public String getInAppSkuByPosition(int position)
    {
        if(position>=0&&position<inAppSKUS.length)
        {
            return inAppSKUS[position];
        }
        else
        {
            return null;
        }
    }

    /**
     * 通过sku获取商品类型(订阅获取内购)
     * @param sku
     * @return inapp内购，subs订阅
     */
    private String getSkuType(String sku)
    {
        if(Arrays.asList(inAppSKUS).contains(sku))
        {
            return BillingClient.SkuType.INAPP;
        }
        else if(Arrays.asList(subsSKUS).contains(sku))
        {
            return BillingClient.SkuType.SUBS;
        }
        return null;
    }

    public GoogleBillingUtil setOnQueryFinishedListener(OnQueryFinishedListener onQueryFinishedListener) {
        mOnQueryFinishedListener = onQueryFinishedListener;
        return this;
    }

    public GoogleBillingUtil setOnPurchaseFinishedListener(OnPurchaseFinishedListener onPurchaseFinishedListener) {
        mOnPurchaseFinishedListener = onPurchaseFinishedListener;
        return this;
    }

    public OnStartSetupFinishedListener getOnStartSetupFinishedListener() {
        return mOnStartSetupFinishedListener;
    }

    public GoogleBillingUtil setOnStartSetupFinishedListener(OnStartSetupFinishedListener onStartSetupFinishedListener) {
        mOnStartSetupFinishedListener = onStartSetupFinishedListener;
        return this;
    }

    /**
     *  本工具查询回调接口
     */
    public interface OnQueryFinishedListener{
        public void onQuerySuccess(List<SkuDetails> list);
        public void onQueryFail(int responseCode);
        public void onQueryError();
    }

    /**
     * 本工具购买回调接口(内购与订阅都走这接口)
     */
    public interface OnPurchaseFinishedListener{

        public void onPurchaseSuccess(List<Purchase> list);

        public void onPurchaseFail(int responseCode);

        public void onPurchError();

    }

    /**
     * oogle服务启动接口
     */
    public interface OnStartSetupFinishedListener{
        public void onSetupSuccess();

        public void onSetupFail(int responseCode);

        public void onSetupError();
    }

    public boolean isServiceConnected() {
        return mIsServiceConnected;
    }

    public boolean isAutoConsumeAsync()
    {
        return isAutoConsumeAsync;
    }

    public void setIsAutoConsumeAsync(boolean isAutoConsumeAsync)
    {
        this.isAutoConsumeAsync= isAutoConsumeAsync;
    }

}
