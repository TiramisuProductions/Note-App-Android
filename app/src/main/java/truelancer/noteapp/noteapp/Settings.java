package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import android.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.Adapters.SettingsLabelAdapter;
import truelancer.noteapp.noteapp.Adapters.SettingsSwitchAdapter;

public class Settings extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ConstraintLayout settingsLayout;
    private RecyclerView recylerViewSwitch;
    private SettingsSwitchAdapter settingsSwitchAdapter;
    private RecyclerView recyclerViewLabel;
    private SettingsLabelAdapter settingsLabelAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    Context context;
    private boolean themeChanged;
    ArrayList<String> settingsList = new ArrayList<>();
    ArrayList<String> lSettingsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//
       // pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        settingsListdata();
        settingsListOption();
        settingsLayout = (ConstraintLayout)findViewById(R.id.settingslayout);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager lLayoutManager = new LinearLayoutManager(this);

        recylerViewSwitch = findViewById(R.id.setting_Recycler_View);
        recyclerViewLabel = findViewById(R.id.label_Recyler_View);
        recyclerViewLabel.setLayoutManager(sLayoutManager);
        recylerViewSwitch.setLayoutManager(lLayoutManager);

        DividerItemDecoration sDividerItemDecoration = new DividerItemDecoration(recylerViewSwitch.getContext(),
                DividerItemDecoration.VERTICAL);
        recylerViewSwitch.addItemDecoration(sDividerItemDecoration);
        DividerItemDecoration lDividerItemDecoration = new DividerItemDecoration(recylerViewSwitch.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerViewLabel.addItemDecoration(lDividerItemDecoration);


        settingsSwitchAdapter = new SettingsSwitchAdapter(this,settingsList);
        settingsLabelAdapter = new SettingsLabelAdapter(this,lSettingsList);
        recylerViewSwitch.setAdapter(settingsSwitchAdapter);
        recyclerViewLabel.setAdapter(settingsLabelAdapter);
        settingsSwitchAdapter.notifyDataSetChanged();
        recylerViewSwitch.hasFixedSize();
        recyclerViewLabel.hasFixedSize();
        recylerViewSwitch.setNestedScrollingEnabled(false);
        recyclerViewLabel.setNestedScrollingEnabled(false);

    }


    private void settingsListdata() {

        settingsList.add("Keep bubble after Call End");

    }

    private void settingsListOption(){
        lSettingsList.add("Share with Friends");
        lSettingsList.add("Rate us");
        lSettingsList.add("Send feedback");
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

                        recylerViewSwitch.setBackgroundColor(getResources().getColor(R.color.dark));
                        settingsLayout.setBackgroundColor(getResources().getColor(R.color.dark));
                    } else {
                        recylerViewSwitch.setBackgroundColor(getResources().getColor(R.color.white));
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
