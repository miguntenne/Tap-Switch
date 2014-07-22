package lk.nfuse.tapswitch;




import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;



public class SettingsActivity extends PreferenceActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        getActionBar().setTitle("  " + getTitle());
    }


}
