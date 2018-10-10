package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.Adapters.SettingsAboutAdapter;
import truelancer.noteapp.noteapp.Adapters.SettingsLabelAdapter;
import truelancer.noteapp.noteapp.Adapters.SettingsSwitchAdapter;

public class Settings extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private ConstraintLayout settingsLayout;
    private RecyclerView recyclerViewSwitch;
    private RecyclerView recyclerViewAbout;
    private SettingsSwitchAdapter settingsSwitchAdapter;
    private SettingsAboutAdapter settingsAboutAdapter;
    private RecyclerView recyclerViewLabel;
    private SettingsLabelAdapter settingsLabelAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    Context context;
    private boolean themeChanged;
    ArrayList<String> settingsList = new ArrayList<>();
    ArrayList<String> lSettingsList = new ArrayList<>();
    ArrayList<String> aSettingsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }


        setContentView(R.layout.activity_settings);
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Settings");
        settingsListData();
        settingsListOption();

        settingsLayout = (ConstraintLayout) findViewById(R.id.settingslayout);
        RecyclerView.LayoutManager sLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager lLayoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager aLayoutManager = new LinearLayoutManager(this);
        recyclerViewSwitch = findViewById(R.id.setting_Recycler_View);
        recyclerViewAbout = findViewById(R.id.about_recyler_view);
        recyclerViewLabel = findViewById(R.id.label_Recyler_View);
        recyclerViewLabel.setLayoutManager(sLayoutManager);
        recyclerViewSwitch.setLayoutManager(lLayoutManager);
        recyclerViewAbout.setLayoutManager(aLayoutManager);
        DividerItemDecoration sDividerItemDecoration = new DividerItemDecoration(recyclerViewSwitch.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerViewSwitch.addItemDecoration(sDividerItemDecoration);
        DividerItemDecoration lDividerItemDecoration = new DividerItemDecoration(recyclerViewSwitch.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerViewLabel.addItemDecoration(lDividerItemDecoration);
        DividerItemDecoration aDividerItemDecoration = new DividerItemDecoration(recyclerViewSwitch.getContext(),
                DividerItemDecoration.VERTICAL);
        recyclerViewAbout.addItemDecoration(aDividerItemDecoration);


        settingsSwitchAdapter = new SettingsSwitchAdapter(this, settingsList);
        settingsLabelAdapter = new SettingsLabelAdapter(this, lSettingsList);
        settingsAboutAdapter = new SettingsAboutAdapter(this, aSettingsList);
        recyclerViewSwitch.setAdapter(settingsSwitchAdapter);
        recyclerViewLabel.setAdapter(settingsLabelAdapter);
        recyclerViewAbout.setAdapter(settingsAboutAdapter);
        settingsSwitchAdapter.notifyDataSetChanged();
        recyclerViewSwitch.hasFixedSize();
        recyclerViewLabel.hasFixedSize();
        recyclerViewSwitch.setNestedScrollingEnabled(false);
        recyclerViewLabel.setNestedScrollingEnabled(false);


        if (MyApp.nightMode) {
            recyclerViewSwitch.setBackgroundColor(getResources().getColor(R.color.dark));
            settingsLayout.setBackgroundColor(getResources().getColor(R.color.dark));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void settingsListData() {

        settingsList.add("Night Mode");
        settingsList.add("Keep bubble after Call");
        settingsList.add("Auto Record Calls");

    }

    private void settingsListOption() {
        lSettingsList.add("Share with Friends");
        lSettingsList.add("Rate us");
        lSettingsList.add("Send feedback");
        aSettingsList.add("About");
        aSettingsList.add("Privacy Policy");
    }


    @Subscribe
    public void onEvent(EventB event) {
        // your implementation
        if (event.getMessage().equals("changeUIMode")) {

            Log.d("gotit", "gotit");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // themeChanged = true;
                    if (MyApp.nightMode) {

                        recyclerViewSwitch.setBackgroundColor(getResources().getColor(R.color.dark));
                        settingsLayout.setBackgroundColor(getResources().getColor(R.color.dark));

                    } else {
                        recyclerViewSwitch.setBackgroundColor(getResources().getColor(R.color.white));
                        settingsLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    }
                    settingsLabelAdapter.notifyDataSetChanged();
                    settingsAboutAdapter.notifyDataSetChanged();


                }

            });
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "in resume settings", Toast.LENGTH_SHORT).show();
    }
}
