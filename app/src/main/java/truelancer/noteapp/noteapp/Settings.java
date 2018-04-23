package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

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
    ArrayList<String> settingsList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref),MODE_PRIVATE);
        setContentView(R.layout.activity_settings);
        settingsListdata();
        settingsLayout = (ConstraintLayout)findViewById(R.id.settingslayout);
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
        settingsList.add("Bubble Location");
        settingsList.add("Call Record");
        settingsList.add("About");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "in resume settings", Toast.LENGTH_SHORT).show();
    }
}
