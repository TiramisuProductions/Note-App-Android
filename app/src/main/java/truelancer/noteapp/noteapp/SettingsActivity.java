package truelancer.noteapp.noteapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getLayoutInflater().inflate(R.layout.toolbar, (ViewGroup) findViewById(android.R.id.content));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

        private static final String SHARED_PREF_NAME = "hellonotepref";
        SharedPreferences sharedPref;
        SharedPreferences.Editor editor;

        TwoStatePreference pref = null;
        Preference pref2 = null;
        SwitchPreference p = null;
        TwoStatePreference e = null;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            sharedPref = getActivity().getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);

            pref = (TwoStatePreference) findPreference(getString(R.string.key_dark_theme));
            pref.setChecked(true);
            pref2 = findPreference(getString(R.string.key_app_service_in_background_notification));
            p = (SwitchPreference) findPreference(getString(R.string.key_app_service_in_background_notification));
            e = (TwoStatePreference) findPreference(getString(R.string.key_app_service_in_background_notification));
            e.setChecked(true);
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            switch (key) {
                case "key_dark_theme":
                    break;
                case "key_bubble_persists":
                    break;
                case "key_app_service_in_background_notification":
                    break;
            }

        }


        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            String key = preference.getKey();
            switch (key) {
                case "key_share_with_friends":
                    Log.d("wood", "share2");

                    break;
                case "key_rate_us":
                    Log.d("wood", "rate2");
                    break;
                case "key_feedback":
                    sendFeedback(getActivity());
                    Log.d("wood", "feedback2");
                    break;
                case "key_privacy_policy":
                    Log.d("wood", "privacy2");
                    break;
                case "key_about_us":
                    Log.d("wood", "about2");
                    break;
                default:
            }
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        private void sendFeedback(final Context context) {

            final Dialog dialog = new Dialog(context);
            String options[] = {getString(R.string.admin_option_1), getString(R.string.admin_option_2)};
            dialog.setContentView(R.layout.dialog_admin);
            final Spinner adminSpinner = (Spinner) dialog.findViewById(R.id.admin_chooser);
            Button buttonSelect = (Button) dialog.findViewById(R.id.btnSelect);
            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options);
            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
            adminSpinner.setAdapter(adminSpinnerArrayAdapter);

            buttonSelect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (adminSpinner.getSelectedItemPosition() == 0) {
                        String email[] = {getString(R.string.admin_email)};
                        shareToGmail(context, email, getString(R.string.admin_option_1), getString(R.string.admin_subject_1));
                    } else {
                        String email[] = {getString(R.string.admin_email)};
                        shareToGmail(context, email, getString(R.string.admin_option_2), getString(R.string.admin_subject_2));
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();


        }

        private void shareToGmail(Context context, String email[], String subject, String temp) {
            String body = null;
            try {
                body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
                body = "\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                        Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                        "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER +
                        "\n-----------------------------\n";
            } catch (Exception e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "" + subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);

            final PackageManager pm = context.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
            ResolveInfo best = null;
            for (final ResolveInfo info : matches) {
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            }
            if (best != null) {
                Log.d("wood", "best if: ");
                intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
                startActivity(intent);
            } else {
                Log.d("wood", "best else" );

                context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
            }

            //context.startActivity(intent);
            //context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
        finish();
    }
}