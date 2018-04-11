package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import java.util.ArrayList;

public class Settings extends AppCompatActivity {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private RecyclerView recyclerViewSettings;
    private SettingsAdapter settingsAdapter;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getApplicationContext().getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        if(pref.getBoolean("darkTheme",false)){
            setTheme(R.style.MyMaterialThemeDark);
        }

        setContentView(R.layout.activity_settings);
        context = getApplicationContext();

        recyclerViewSettings = (RecyclerView)findViewById(R.id.setting_Recycler_View);
        settingsAdapter = new SettingsAdapter(context);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerViewSettings.setLayoutManager(layoutManager);
        recyclerViewSettings.setAdapter(settingsAdapter);
        settingsAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "in resume settings", Toast.LENGTH_SHORT).show();
    }
}
