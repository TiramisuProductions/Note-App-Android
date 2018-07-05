package truelancer.noteapp.noteapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
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



        // fromExport=false;



    public static boolean dataAlreadyExists(String tsMilli_fromBackup, String number) {

        if (number.equals("1")) {

            List<Contact> contacts = Contact.listAll(Contact.class);
            // boolean exists = false;

            Log.d("cricket", "then: " + tsMilli_fromBackup);

            for (int i = 0; i < contacts.size(); i++) {

                String tsMilli_fromLocalDb = contacts.get(i).getTsMilli();
                Log.d("cricket2", "then: " + tsMilli_fromLocalDb);

                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    Log.d("cricket3", "then: " + true);
                    return true;
                }
            }
            //Log.d(TAG + "baaghi", "" + exists);
            return false;
        } else if (number.equals("2")) {

            List<Email> emails = Email.listAll(Email.class);
            //boolean exists = false;

            for (int i = 0; i < emails.size(); i++) {

                String tsMilli_fromLocalDb = emails.get(i).getTsMilli();

                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    return true;
                }
            }
            return false;
        } else if (number.equals("3")) {

            List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
            //boolean exists = false;

            for (int i = 0; i < bankAccounts.size(); i++) {
                String tsMilli_fromLocalDb = bankAccounts.get(i).getTsMilli();

                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    return true;
                }
            }
            return false;
        } else if (number.equals("4")) {
            List<Note> notes = Note.listAll(Note.class);
            //boolean exists = false;

            for (int i = 0; i < notes.size(); i++) {
                String tsMilli_fromLocalDb = notes.get(i).getTsMilli();
                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    return true;
                }
            }
            return false;
        }
        return false;
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
