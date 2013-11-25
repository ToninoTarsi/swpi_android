package com.swpi.sintwindpi;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
 
public class UserSettingActivity extends PreferenceActivity {
 
	
	private SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
		settings  = getSharedPreferences("preferences", 0);

    	
        super.onCreate(savedInstanceState);
 
        addPreferencesFromResource(R.xml.preferences);
        
        
        EditTextPreference Audiorepetitiontime = (EditTextPreference) findPreference("Audio_repetition_time");
        Audiorepetitiontime.setSummary(Audiorepetitiontime.getText());

        Audiorepetitiontime.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object o) {
                    preference.setSummary(o.toString());
                    //GetApplicationSettings();
                    return true;
                
            };
        });
 
    }
}