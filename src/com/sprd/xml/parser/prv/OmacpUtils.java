
package com.sprd.xml.parser.prv;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.text.TextUtils;
import android.util.Log;
//add for bug 498188 begin
import com.sprd.omacp.R;
//add for bug 498188 end
import android.content.Intent;
import com.sprd.omacp.transaction.ConfirmOtaActivity;
import android.graphics.Color;
import android.app.PendingIntent;
import android.os.SystemProperties;
import com.sprd.omacp.transaction.OtaPreParser;
import java.util.HashMap;
import android.app.NotificationChannel;
public class OmacpUtils {

    public static final int NOTIFICATION_ID_OTA = 124;
    public static final boolean isRuning = false;
    //public static final String  OP_VODAFONE = "cmcc";
    public static final String  OP_VODAFONE = "vodafone";
    public static final String CHANNAL_NAME = "sprd Omacp";
    public static void setSubId(int subId) {
        mSubId = subId;
    }

    public static int getSubId() {
        return mSubId;
    }

    public static boolean isVodafone(Context context) {//modify for bug852734 begin
        boolean isOperatorSupport = context.getResources().getBoolean(R.bool.operator_support);
        Log.d(TAG, "isVodafone()===isOperatorSupport = ["+isOperatorSupport+"]");
        return isOperatorSupport;
    }//modify for bug852734 end

     public static void addComfrimDialogNotification(Context context ,Intent intent) {
        ConfirmOtaActivity.setIntentDate(intent);
        PowerManager pm = (PowerManager) context
                .getSystemService(Context.POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "MMS PushReceiver");
        wl.acquire(5000);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);

            Notification notification =new Notification(R.drawable.ic_launcher,
                    "OTA", System.currentTimeMillis());

            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_SHOW_LIGHTS;

            notification.defaults = Notification.DEFAULT_ALL;

            notification.ledARGB = Color.BLUE;
            notification.ledOnMS =5000;


            CharSequence contentTitle ="OTA";
            CharSequence contentText = context.getText(R.string.confirm_ota_message);
            Intent notificationIntent =new Intent(context, ConfirmOtaActivity.class);
            PendingIntent contentItent = PendingIntent.getActivity(context, NOTIFICATION_ID_OTA, notificationIntent, 0);
            //1102583
            int currentSDKVersion = android.os.Build.VERSION.SDK_INT;
            Log.d(TAG, "current sdk version is : " + currentSDKVersion);
            if (currentSDKVersion >= 26) {
                NotificationChannel simNch = notificationManager.getNotificationChannel("sprd.oma.omacp");
                if (simNch == null) {
                    Log.d(TAG, "first create channel");
                    NotificationChannel cn = new NotificationChannel("sprd.oma.omacp",CHANNAL_NAME, NotificationManager.IMPORTANCE_HIGH);
                    cn.enableLights(true);
                    cn.setLightColor(Color.BLUE);
                    notificationManager.createNotificationChannel(cn);
                }
                Notification.Builder b = Notification.Builder.recoverBuilder(context,notification);
                b.setChannelId("sprd.oma.omacp");
                b.setSmallIcon(R.drawable.ic_launcher);
                b.setContentIntent(contentItent);
                b.setContentTitle(contentTitle);
                b.setContentText(contentText);
                b.setOngoing(true);
                Log.d(TAG, "setChannelId SmallIcon for nb");
                notification = b.build();
                //Notification nN = b.build();
                //notificationManager.notify(notificationTag, type, nN);
            }else{
                notification.setLatestEventInfo(context, contentTitle, contentText, contentItent);
            }
            notificationManager.notify(NOTIFICATION_ID_OTA, notification);
        }

    public static void registerMsgNf(Context context) {
        Log.d(TAG, "register Message Notification");
        //modified for bug 498188 begin
        Notification.Builder builder = new Notification.Builder(context);
        builder.setSound(Uri.parse(NOTIFICATIONURI_STRING));
        builder.setDefaults(Notification.DEFAULT_LIGHTS);
        builder.setSmallIcon(R.drawable.ic_launcher);
        // add for bug 518831 begin
        builder.setContentTitle("OTA");
        builder.setContentText(context.getString(R.string.OTAConfig_title));
        // add for bug 518831 end
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.getNotification());
        //modified for bug 498188 end

        // wake the screen,when the message received.
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, "OTAMsgNotification");
        wl.acquire(WAKETIME);
    }
    //add for bug 530106 begin
    public static void clearMsgNf(Context context) {
        Log.d(TAG, "clear Message Notification begin");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        NotificationManager nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(NOTIFICATION_ID);
        Log.d(TAG, "clear  Message Notification end");
    }
    //add for bug 530106 end
    /**
     * Converts a byte array into a String of hexadecimal characters.
     *
     * @param bytes an array of bytes
     *
     * @return hex string representation of bytes array
     */
    //add for upgrade to Android N
    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null)
            return null;
        StringBuilder ret = new StringBuilder(2 * bytes.length);
        for (int i = 0; i < bytes.length; i++) {
            int b;
            b = 0x0f & (bytes[i] >> 4);
            ret.append("0123456789abcdef".charAt(b));
            b = 0x0f & bytes[i];
            ret.append("0123456789abcdef".charAt(b));
        }
        return ret.toString();
    }
    //add for upgrade to Android N

    public static int checkSec(Intent intent) {
        int tSec =-1;
        HashMap<String, String> contentTypeParameters = (HashMap<String, String>) intent
                .getExtra("contentTypeParameters");
        String sec = null;
        if (contentTypeParameters != null) {
            sec = contentTypeParameters.get(OtaPreParser.WELL_KNOW_PARAMETER_SEC);
        }
        if (sec != null) {
            try {
                tSec = Integer.parseInt(sec);
            } catch (NumberFormatException e) {
                tSec = OtaPreParser.SEC_TYPE_UNINITIALIZED;
            }
        } else {
            tSec = OtaPreParser.SEC_TYPE_UNINITIALIZED;
        }

            Log.d(TAG, " sec = " + sec + "\n ");
        return tSec;
    }

    private static Integer mSubId = null;
    private static final int WAKETIME = 10000;
    private static final int NOTIFICATION_ID = 110;
    private static final String TAG = "OmacpUtils";
    private static final String NOTIFICATIONURI_STRING = "content://settings/system/notification_sound";
}
