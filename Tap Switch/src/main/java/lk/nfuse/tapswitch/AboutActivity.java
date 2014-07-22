package lk.nfuse.tapswitch;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;


/**
 * Created by Chomal on 7/19/2014.
 */
public class AboutActivity extends Activity{

    TextView ver, title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_fragment);

        View frameLayout = findViewById(R.id.aboutLayout);
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ver = (TextView)findViewById(R.id.versionView);
        title = (TextView)findViewById(R.id.nfuseView);

        try {
            ver.setText("ver. "+getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //title.setText("nFuse"+Html.fromHtml("&trade"));
        title.setText("nFuse");
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/nFuseLK")));
            }
        });

    }

}
