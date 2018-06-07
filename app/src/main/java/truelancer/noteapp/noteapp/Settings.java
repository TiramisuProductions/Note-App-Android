package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.Adapters.SettingsAdapter;

public class Settings extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ConstraintLayout settingsLayout;
    private RecyclerView recyclerViewSettings;
    private SettingsAdapter settingsAdapter;
    Context context;
    private boolean themeChanged;
    ArrayList<String> settingsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        settingsListdata();
        settingsLayout = (ConstraintLayout)findViewById(R.id.settingslayout);
        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }
        context = getApplicationContext();
        recyclerViewSettings = (RecyclerView)findViewById(R.id.setting_Recycler_View);
        settingsAdapter = new SettingsAdapter(context,settingsList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        if(!MyApp.defaultTheme){
            recyclerViewSettings.setBackgroundColor(getResources().getColor(R.color.dark));
            settingsLayout.setBackgroundColor(getResources().getColor(R.color.dark));

        }

        recyclerViewSettings.setLayoutManager(layoutManager);
        recyclerViewSettings.setAdapter(settingsAdapter);
        settingsAdapter.notifyDataSetChanged();

    }


    private void settingsListdata() {
        settingsList.add("Theme");
        settingsList.add("About");
        settingsList.add("Open Source licenses");
    }

    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals(getString(R.string.themechanged))) {

            Log.d("gotit", "gotit");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                   // themeChanged = true;
                    if (!MyApp.defaultTheme) {


                        recyclerViewSettings.setBackgroundColor(getResources().getColor(R.color.dark));
                        settingsLayout.setBackgroundColor(getResources().getColor(R.color.dark));
                    } else {
                        recyclerViewSettings.setBackgroundColor(getResources().getColor(R.color.white));
                        settingsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }


                }

            });
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final Intent i = context.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "in resume settings", Toast.LENGTH_SHORT).show();
    }
}
