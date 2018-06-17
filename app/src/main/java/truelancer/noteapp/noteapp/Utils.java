package truelancer.noteapp.noteapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.Fragments.BankAccountFragment;
import truelancer.noteapp.noteapp.Fragments.ContactFragment;
import truelancer.noteapp.noteapp.Fragments.EmailFragment;
import truelancer.noteapp.noteapp.Fragments.NoteFragment;
import truelancer.noteapp.noteapp.Fragments.RecordingFragment;

public class Utils {

    public static boolean isValidMobile(String phone) {
        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                //for only indian ic_phone no

                check = false;//if(ic_phone.length() != 10) {
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }


    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    public static void Visibility_no_data(int no, boolean isVisible) {
        switch (no) {
            case 1:
                if (isVisible) {
                    ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                } else {
                    ContactFragment.RContact_no_data.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (isVisible) {
                    EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                } else {
                    EmailFragment.REmail_no_data.setVisibility(View.GONE);
                }
                break;
            case 3:
                if (isVisible) {
                    BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                } else {
                    BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                }

                break;
            case 4:
                if (isVisible) {
                    NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                } else {
                    NoteFragment.RNote_no_data.setVisibility(View.GONE);
                }
                break;
            case 5:
                if (isVisible) {
                    RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                } else {
                    RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
                }
        }
    }
}
