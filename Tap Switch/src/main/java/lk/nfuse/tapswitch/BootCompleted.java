package lk.nfuse.tapswitch;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;


public class BootCompleted extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            //System.out.println("booted");
            //context.startService(new Intent(context,SUOnBootService.class));
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("su");

            }catch (IOException e){
                e.printStackTrace();
            }


        }
    }
}
