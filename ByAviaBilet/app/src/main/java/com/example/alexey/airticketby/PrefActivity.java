package com.example.alexey.airticketby;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by Alexey on 17.09.2016.
 */
public class PrefActivity extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref);
    }
}
