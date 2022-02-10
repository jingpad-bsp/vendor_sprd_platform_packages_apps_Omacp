package com.sprd.omacp.transaction;

import com.sprd.omacp.R;
import com.sprd.xml.parser.prv.OmacpUtils;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.content.Intent;

// spread Bug 484073  start
public class ConfirmOtaActivity extends Activity {
    private Bundle mBundle;
    private TextView mComfirm_ok , mComfirm_cancle;
    private static Intent mIntent;
    private NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(" ");
        setContentView(R.layout.confirm_ota_dialog_activity);
        mComfirm_ok = (TextView)findViewById(R.id.comfirm_ok_tv);
        mComfirm_cancle = (TextView)findViewById(R.id.comfirm_cancle_tv);
        setFinishOnTouchOutside(false);//727144
        mNotificationManager = (NotificationManager) this.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        mComfirm_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mIntent != null){
                    mIntent.setClass(ConfirmOtaActivity.this, OtaOmaService.class);
                    startService(mIntent);
                }
                mNotificationManager.cancel(OmacpUtils.NOTIFICATION_ID_OTA);
                finish();
            }
        });

        mComfirm_cancle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mNotificationManager.cancel(OmacpUtils.NOTIFICATION_ID_OTA);
                finish();
            }
        });

    /************************************************************************
    Process White Box Start
     White Box Code 2017/12/14 Add  by Yu.LI
    *************************************************************************/
        if( OtaPreParser.isWhiteBoxTest(getIntent())){
            Log.d(OtaPreParser.WB_TAG, "  Enter White Box Parse Flow ^_^");
            mIntent.setClass(ConfirmOtaActivity.this, OtaOmaService.class);
            startService(mIntent);
            mNotificationManager.cancel(OmacpUtils.NOTIFICATION_ID_OTA);
            finish();

        }

    /************************************************************************
    Process White Box End
     White Box Code 2017/12/14 Add  by Yu.LI
    *************************************************************************/
    }



    public static  void  setIntentDate(Intent intent){
          mIntent = intent;
    }

}
//spread Bug 484073  start
