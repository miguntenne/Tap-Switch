package lk.nfuse.tapswitch;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Chomal on 7/14/2014.
 */
public class ChargerConnected extends BroadcastReceiver {



    @Override
    public void onReceive(Context context, Intent intent) {

        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        //Boolean chargePref = sharedPreferences.getBoolean("pref_charge",false);

        Intent intent1 = new Intent(context,RunCommandService.class);



        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, intentFilter);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        //int usbStatus = intent.getIntExtra("status", BatteryManager.BATTERY_HEALTH_UNKNOWN);
        int acCharge = 0/*, usbCharge = 0*/;
        acCharge = batteryStatus.getIntExtra("plugged",BatteryManager.BATTERY_PLUGGED_AC);
        //usbCharge = batteryStatus.getIntExtra("plugged",BatteryManager.BATTERY_PLUGGED_USB);



        if (/*(status == 2 || status == 5) && */acCharge == 1){
            Toast.makeText(context,"connected",Toast.LENGTH_SHORT).show();
            intent1.putExtra("dt2wValue",2);
            context.startService(intent1);

            //Main.dt2w.setChecked(true);
        }
        else if (/*(status == 4 || status == 3) &&*/ acCharge !=1  /*usbStatus == BatteryManager.BATTERY_PLUGGED_USB*/){
            Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show();
            intent1.putExtra("dt2wValue",0);
            context.startService(intent1);
            //Main.dt2w.setChecked(false);
        }

       // if (chargePref){
            /*int pluggedAC = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,-1);
            boolean acCharge = pluggedAC == BatteryManager.BATTERY_PLUGGED_AC;

            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging && acCharge){
                //Main.dt2w.setChecked(true);
                //Main.runCommand("echo 2 > /sys/android_touch/doubletap2wake");
                Toast.makeText(context,"connected",Toast.LENGTH_SHORT).show();
            }
            else if (!isCharging){
                //Main.dt2w.setChecked(false);
               //Main.runCommand("echo 0 > /sys/android_touch/doubletap2wake");
                Toast.makeText(context,"disconnected",Toast.LENGTH_SHORT).show();
            }*/
        //}



    }


}
