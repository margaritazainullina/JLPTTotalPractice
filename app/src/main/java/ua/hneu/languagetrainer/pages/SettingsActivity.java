package ua.hneu.languagetrainer.pages;

import ua.hneu.edu.languagetrainer.R;
import ua.hneu.languagetrainer.App;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import java.util.Locale;

public class SettingsActivity extends PreferenceActivity implements
        OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        SharedPreferences settings = getSharedPreferences("appSettings",
                MODE_PRIVATE);
        Editor editor = settings.edit();
        if (App.userInfo.getLevel() == 4 || App.userInfo.getLevel() == 5)
            App.isShowRomaji = true;
        else
            App.isShowRomaji = false;

        App.isColored = App.settings.getBoolean("color", true);
        App.isAutoplayed = App.settings.getBoolean("autoplay", true);

        if(key.equals("language")){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.after_reload),
                    Toast.LENGTH_LONG).show();
            Locale locale = new Locale(key);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        if(key.equals("level")){
            App.goToLevel(Integer.parseInt(sharedPreferences.getString(key, "5")));
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.after_reload),
                    Toast.LENGTH_LONG).show();
    }
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
}