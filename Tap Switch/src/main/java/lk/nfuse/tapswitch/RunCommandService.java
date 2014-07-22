package lk.nfuse.tapswitch;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.io.DataOutputStream;
import java.io.IOException;

public class RunCommandService extends Service {
    public RunCommandService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        int dt2wValue = intent.getExtras().getInt("dt2wValue");
        if (dt2wValue == 2){
            runCommand("echo 2 > /sys/android_touch/doubletap2wake");
        }
        else if (dt2wValue == 0){
            runCommand("echo 0 > /sys/android_touch/doubletap2wake");
        }

    }

    private boolean runCommand(String cmd) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("su");
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (IOException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }
}
