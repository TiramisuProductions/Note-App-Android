package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences.Editor editor;
        String SHARED_PREF_NAME = "hellonotepref";
        SharedPreferences pref;
        pref = MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.shared_pref), Context.MODE_PRIVATE);


        if (pref.getBoolean(getString(R.string.defaulttheme),false)) {
            setTheme(R.style.MyMaterialTheme);
        } else {
            setTheme(R.style.MyMaterialThemeDark);

        }
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {


        public Context context = null;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.settings_main);
            context = getActivity();

            // gallery EditText change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_gallery_name)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_dark_theme)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_bubble_persists)));
            bindPreferenceSummaryToValue(findPreference(getString(R.string.key_app_service_in_background_notification)));

            // notification preference change listener
            //bindPreferenceSummaryToValue(findPreference(getString(R.string.key_notifications_new_message_ringtone)));

            // feedback preference click listener
            /*Preference myPref = findPreference(getString(R.string.key_send_feedback));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference preference) {
                    sendFeedback(getActivity());
                    return true;
                }
            });*/
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getBoolean(preference.getKey(), false));
    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();
            Log.d("wood", "qwerty");
            SharedPreferences.Editor editor;
            String SHARED_PREF_NAME = "hellonotepref";
            SharedPreferences pref;
            pref = MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
            editor = pref.edit();

            if (preference instanceof SwitchPreference) {
                //Log.d("wood", "switch is onjj: " + preference.getKey());

                if (preference.getKey().equals("key_dark_theme")) {
                    if (((SwitchPreference) preference).isChecked()) {
                        editor.putBoolean(MyApp.context.getString(R.string.defaulttheme), true);
                        pref = MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.apply();
                        MyApp.defaultTheme = true;

                    } else {
                        editor.putBoolean(MyApp.context.getString(R.string.defaulttheme), false);
                        pref = MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
                        editor = pref.edit();
                        editor.apply();
                        MyApp.defaultTheme = false;
                    }

                    Log.d("wood", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());
                } else if (preference.getKey().equals("key_bubble_persists")) {

                    Log.d("wood", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());
                } else if (preference.getKey().equals("key_app_service_in_background_notification")) {
                    Log.d("wood", "switch is on? enable: " + preference.isEnabled());
                    Log.d("wood", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());
                }
            }
            return true;
        }
    };


}