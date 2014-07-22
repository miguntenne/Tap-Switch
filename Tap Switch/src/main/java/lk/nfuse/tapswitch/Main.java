package lk.nfuse.tapswitch;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Build;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Main extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static Switch dt2w,s2s;
    TextView detView;

    private int supportCount = 0;
    public static int dt2wValue = 2;
    public static int s2sValue = 2;

    public final static String dt2wpath = "/sys/android_touch/doubletap2wake";
    public final static String s2spath = "/sys/android_touch/sweep2sleep";

    SharedPreferences settings,sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);

        sharedPreferences = getSharedPreferences("sPreferenceFile",0);
        SharedPreferences.Editor prefEdit = sharedPreferences.edit();


        System.out.println(sharedPreferences.getBoolean("isFirstRun",true));

        if (sharedPreferences.getBoolean("isFirstRun",true)){
            try {
                Runtime runtime = Runtime.getRuntime();
                Process process = runtime.exec("su");
            } catch (IOException e) {
                e.printStackTrace();
            }
            prefEdit.putBoolean("isFirstRun",false);
            prefEdit.commit();
        }

        System.out.println(sharedPreferences.getBoolean("isFirstRun",true));


        dt2w = (Switch)findViewById(R.id.tapswitch);
        s2s = (Switch)findViewById(R.id.sweepswitch);
        detView = (TextView)findViewById(R.id.textView);

        detView.setText("A nifty app to toggle Double-Tap-to-Wake and Sweep-to-Sleep features " +
                "in supported kernels. Your " + Build.MODEL + " should be rooted and flashed with a DT2W/S2S enabled custom kernel" +
                " such as ElementalX, Bricked etc. for this to work. Once enabled, double tap on screen when it is off to " +
                "wake the device and sweep across navigation bar to left to put the screen to sleep.");

        getActionBar().setTitle("  " + getTitle());



        checkFile(dt2wpath);
        checkFile(s2spath);

        if (supportCount == 2)
            detView.setText("Sorry, your current kernel " + System.getProperty("os.version") + " does not support this app.");

        dt2w.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String enabledt2w = "echo "+dt2wValue+" > /sys/android_touch/doubletap2wake";
                String disabledt2w = "echo 0 > /sys/android_touch/doubletap2wake";

                if(dt2w.isChecked())
                    runCommand(enabledt2w);
                else
                    runCommand(disabledt2w);
            }
        });

        s2s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                String enables2s = "echo "+s2sValue+" > /sys/android_touch/sweep2sleep";
                String disables2s = "echo 0 > /sys/android_touch/sweep2sleep";

                if(s2s.isChecked())
                    runCommand(enables2s);
                else
                    runCommand(disables2s);

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        FileObserver fileObserver = new FileObserver(dt2wpath) {
            @Override
            public void onEvent(int event, String path) {
                if ((FileObserver.MODIFY & event)!=0){
                    if (readString(dt2wpath).equals("0")){
                        dt2w.setChecked(false);
                    }
                    else if (readString(dt2wpath).equals("2")){
                        dt2w.setChecked(true);
                    }
                }
            }
        };
        fileObserver.startWatching();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(Main.this,SettingsActivity.class));
        }
        else if (id == R.id.about_settings){
            startActivity(new Intent(Main.this,AboutActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }



    public static boolean runCommand(String cmd) {
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

    public void checkFile(String file){

        File myFile = new File(file);
        SharedPreferences.Editor editor = settings.edit();

        if (myFile.exists() && file.equals(dt2wpath)) {
            if (readString(file).equals("2") || readString(file).equals("1")) {
                if (readString(file).equals("1")) {
                    editor.putBoolean("KEY_DT2W_BOTTOM", true);
                    editor.commit();
                }
                dt2w.setChecked(true);

            }
            else
                dt2w.setChecked(false);
        }
        else if (myFile.exists() && file.equals(s2spath)){
            if (readString(file).equals("2") || readString(file).equals("1")) {
                if (readString(file).equals("1")) {
                    editor.putBoolean("KEY_S2S_RIGHT", true);
                    editor.commit();
                }
                s2s.setChecked(true);
            }
            else
                s2s.setChecked(false);
        }
        else if (!myFile.exists() && file.equals(dt2wpath)){
            Toast.makeText(getApplicationContext(), "Your kernel does not support DoubleTap2Wake", Toast.LENGTH_LONG).show();
            dt2w.setEnabled(false);
            supportCount++;

        }
        else if (!myFile.exists() && file.equals(s2spath)) {
            Toast.makeText(getApplicationContext(), "Your kernel does not support SweepToSleep", Toast.LENGTH_LONG).show();
            s2s.setEnabled(false);
            supportCount++;
        }
    }

    private static String readString(String filename) {
        try {
            File f = new File(filename);
            if (f.exists()) {
                InputStream is = null;
                if (f.canRead()) {
                    is = new FileInputStream(f);
                } else {
                    Log.w(SyncStateContract.Constants._COUNT, "read-only file, trying w/ root: " + filename);
					String[] commands = {
                            "cat " + filename + "\n", "exit\n"
                    };
                    Process p = Runtime.getRuntime().exec(getSUbinaryPath());
                    DataOutputStream dos = new DataOutputStream(p.getOutputStream());
                    for (String command : commands) {
                        dos.writeBytes(command);
                        dos.flush();
                    }
                    if (p.waitFor() == 0) {
                        is = p.getInputStream();
                    } else {
                        return null;
                    }
                }
                BufferedReader br = new BufferedReader(new InputStreamReader(is), 2048);
                String line = br.readLine();
                br.close();
                return line;
            } else {
					Log.e(SyncStateContract.Constants._COUNT, "file does not exist: " + filename);
                return null;
            }
        } catch (InterruptedException iex) {
            Log.e(SyncStateContract.Constants._COUNT, iex.getMessage(), iex);
            return null;
        } catch (IOException ioex) {
            Log.e(SyncStateContract.Constants._COUNT, ioex.getMessage(), ioex);
            return null;
        }
    }

    public static String getSUbinaryPath() {
        String s = "/system/bin/su";
        File f = new File(s);
        if (f.exists()) {
            return s;
        }
        s = "/system/xbin/su";
        f = new File(s);
        if (f.exists()) {
            return s;
        }
        return null;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("KEY_DT2W_BOTTOM")){
            if (sharedPreferences.getBoolean(key, false)) {
                dt2wValue = 1;
                if (dt2w.isChecked()) {
                    runCommand("echo 1 > /sys/android_touch/doubletap2wake");
                }
            }
            else {
                dt2wValue = 2;
                if (dt2w.isChecked()) {
                    runCommand("echo 2 > /sys/android_touch/doubletap2wake");
                }
            }

        }
        else if (key.equals("KEY_S2S_RIGHT")){
            if (sharedPreferences.getBoolean(key,false)) {
                s2sValue = 1;
                if (s2s.isChecked()){
                    runCommand("echo 1 > /sys/android_touch/sweep2sleep");
                }
            }
            else {
                s2sValue = 2;
                if (s2s.isChecked()){
                    runCommand("echo 2 > /sys/android_touch/sweep2sleep");
                }
            }
        }


    }

}
