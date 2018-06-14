package truelancer.noteapp.noteapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;

public class IntroActivity extends AppIntro {
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), 0); // 0 - for private mode
        editor = pref.edit();

        MyApp.defaultTheme = pref.getBoolean(getString(R.string.defaulttheme), true);

        if (pref.getBoolean(getString(R.string.shared_pref_first_time), true)) {
            addSlide(AppIntroFragment.newInstance("Thanks For Downloading Hello Note", "", (R.drawable.ic_logo), getResources().getColor(R.color.colorPrimary)));

        } else {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        editor.putBoolean(getString(R.string.shared_pref_first_time), false);
        editor.commit();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }

}
