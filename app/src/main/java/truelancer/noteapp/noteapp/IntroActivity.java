package truelancer.noteapp.noteapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import truelancer.noteapp.noteapp.Fragments.SlideFragment;
import truelancer.noteapp.noteapp.Fragments.WelcomeFragment2;

public class IntroActivity extends AppIntro2 {
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // ...
        super.onCreate(savedInstanceState);


        // askForPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_CONTACTS}, 1);


        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), 0); // 0 - for private mode
        editor = pref.edit();


        MyApp.defaultTheme = pref.getBoolean(getString(R.string.defaulttheme), true);

        if (pref.getBoolean(getString(R.string.shared_pref_first_time), true)) {
            //addSlide(SlideFragment.newInstance(R.layout.welcome_slide1));
            addSlide(SlideFragment.newInstance(R.layout.welcome_slide1));
            addSlide(new WelcomeFragment2());
            if(Build.VERSION.SDK_INT<=23) {
                addSlide(SlideFragment.newInstance(R.layout.welcome_slide3));
            }

        } else {
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
        askForPermissions(new String[]{ Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.CAPTURE_AUDIO_OUTPUT,Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE}, 3);

        setSwipeLock(false);

    }



    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if ((ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.SYSTEM_ALERT_WINDOW) != PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)&& (ContextCompat.checkSelfPermission(IntroActivity.this, Manifest.permission.PROCESS_OUTGOING_CALLS) != PackageManager.PERMISSION_GRANTED)) {

            askForPermissions(new String[]{ Manifest.permission.READ_PHONE_STATE,Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.SYSTEM_ALERT_WINDOW,Manifest.permission.CAPTURE_AUDIO_OUTPUT,Manifest.permission.RECORD_AUDIO,Manifest.permission.CALL_PHONE}, 3);

        }
        else {
            editor.putBoolean(getString(R.string.shared_pref_first_time), false);
            editor.commit();
            finish();
        }

    }

}