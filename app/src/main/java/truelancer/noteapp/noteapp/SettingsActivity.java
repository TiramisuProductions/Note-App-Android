package truelancer.noteapp.noteapp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
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
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();
    public static Context context;
    public static SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), 0);

        context = this;

        SharedPreferences.Editor editor;
        String SHARED_PREF_NAME = "hellonotepref";
        SharedPreferences pref;
        pref = MyApp.context.getSharedPreferences(MyApp.context.getString(R.string.shared_pref), Context.MODE_PRIVATE);

        Log.d("themeSelected", String.valueOf(pref.getBoolean("defaulttheme", false)));

        if (MyApp.defaultTheme) {
            setTheme(R.style.MyMaterialTheme);
        } else {
            setTheme(R.style.MyMaterialThemeDark);

        }
        // load settings fragment
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MainPreferenceFragment()).commit();
    }


    public static class MainPreferenceFragment extends PreferenceFragment {


        public Context context = null;
        SharedPreferences.Editor editor;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);


            addPreferencesFromResource(R.xml.settings_main);
            context = getActivity();


            // gallery EditText change listener

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


                if (preference.getKey().equals("key_bubble_persists")) {

                    Log.d("wood 1", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());
                    if (((SwitchPreference) preference).isChecked()) {
                        editor.putBoolean("key_bubble_persists", false);
                        editor.commit();

                    } else {
                        editor.putBoolean("key_bubble_persists", true);
                        editor.commit();

                    }


                    Toast.makeText(MyApp.context, "Changes will reflect after new calls are placed", Toast.LENGTH_LONG).show();


                } else if (preference.getKey().equals("key_app_service_in_background_notification")) {
                    Log.d("wood", "switch is on? enable: " + preference.isEnabled());
                    Log.d("wood", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());



                        Log.d("wood 1", "switch is on? checked: " + ((SwitchPreference) preference).isChecked());
                        if (((SwitchPreference) preference).isChecked()) {
                            editor.putBoolean("key_app_service_in_background_notification", false);
                            editor.commit();

                        } else {
                            editor.putBoolean("key_app_service_in_background_notification", true);
                            editor.commit();
                        }

                        Log.d("glass",""+pref.getBoolean("key_app_service_in_background_notification",true));




                        Toast.makeText(MyApp.context, "Changes will reflect after new calls are placed", Toast.LENGTH_LONG).show();

                }
            }

            return true;
        }
    };
}





