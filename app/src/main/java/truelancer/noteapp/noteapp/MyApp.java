package truelancer.noteapp.noteapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.SugarApp;

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.Services.PopUpService;


public class MyApp extends SugarApp {
    String TAG = "red";
    public PopUpService popUpService;
    private boolean bound;
    public boolean toHideBubble;
    static public boolean toSave,off_hook=false,recording_in_progress = false;
    String defaultValue = "default";
    public boolean firstRun = true;
    public static boolean defaultTheme = true;
    public static Context context;
    public static  Boolean themeFirstRun =false;
    public static EditText editContactNameToSave, editContactNumberToSave,editEmailContactNameToSave,editEmailAdressToSave,editBankContactNameToSave,editBankAccountNoToSave,editBankOthersNoToSave,editNoteToSave;




    public static String firstRunRingingNumber,firstRunContactName,firstRuntsMilli;
    public static Boolean firstRunIsIncoming;

    static public String contactName0 = "", contactNumber0 = "",
            contactName1 = "", emailId1 = "",
            contactName2 = "", accountNumber2 = "", ifsc2 = "",
            contactName3 = "", note3 = "";

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PopUpService.LocalBinder binder = (PopUpService.LocalBinder) service;
            popUpService = binder.getService();
            bound = true;
            popUpService.minimize();
            firstRun = false;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    public void checkForDraft(String calledNumber, String calledName, boolean incomingCall, String tsMilli) {
        //////////////////////// CONTACT CHECK/////////////////////////////////
        if (contactName0.equals("") && contactNumber0.equals("")) {
            Log.d(TAG, "checkForDraft: if 0:" + contactName0 + " " + contactNumber0);

        } else {
            if (contactName0.equals("")) {
                contactName0 = defaultValue;
            }
            if (contactNumber0.equals("")) {
                contactNumber0 = defaultValue;
            }
            Contact contact = new Contact(contactName0, contactNumber0, calledNumber, calledName, incomingCall, tsMilli);
            contact.save();
            Log.d(TAG, "checkForDraft: else 0:" + contactName0 + " " + contactNumber0);
            Toast.makeText(getApplicationContext(), "" + contactName0 + " " + contactNumber0, Toast.LENGTH_SHORT).show();
            contactName0 = "";
            contactNumber0 = "";
        }
        ////////////////////////// EMAIL CHECK///////////////////////////////////

        if (contactName1.equals("") && emailId1.equals("")) {
            Log.d(TAG, "checkForDraft: if 1:" + contactName1 + " " + emailId1);
        } else {
            if (contactName1.equals("")) {
                contactName1 = defaultValue;
            }
            if (emailId1.equals("")) {
                emailId1 = defaultValue;
            }
            Email email = new Email(contactName1, emailId1, calledNumber, calledName, incomingCall, tsMilli);
            email.save();
            Log.d(TAG, "checkForDraft: else 1:" + contactName1 + " " + emailId1);
            contactName1 = "";
            emailId1 = "";
        }
        ////////////////////////// BANK ACCOUNT CHECK///////////////////////////////////
        if (contactName2.equals("") && accountNumber2.equals("") && ifsc2.equals("")) {
            Log.d(TAG, "checkForDraft: if 2:" + contactName2 + " " + accountNumber2 + " "+ ifsc2);
        } else {
            if (contactName2.equals("")) {
                contactName2 = defaultValue;
            }
            if (accountNumber2.equals("")) {
                accountNumber2 = defaultValue;
            }
            if (ifsc2.equals("")) {
                ifsc2 = defaultValue;
            }

            BankAccount bankAccount = new BankAccount(contactName2, accountNumber2, ifsc2, calledNumber, calledName, incomingCall, tsMilli);
            bankAccount.save();

            Log.d(TAG, "checkForDraft: else 2:" + contactName2 + " " + accountNumber2 + "" + ifsc2);
            contactName2 ="";
            accountNumber2 = "";
            ifsc2 ="";
        }
        //////////////////////////Note CHECK///////////////////////////////////

        if(contactName3.equals("") && note3.equals("")){
            Log.d(TAG, "checkForDraft: if 3:" + contactName3 + " " + note3);
        }
        else {
            if(contactName3.equals("")){contactName3 = defaultValue;}
            if(note3.equals("")){note3 = defaultValue;}

            Note noteN = new Note( note3, calledNumber, calledName,  tsMilli,incomingCall);
            noteN.save();
            Log.d(TAG, "checkForDraft: if 3:" + contactName3 + " " + note3);
            contactName3 = "";
            note3 = "";
        }
    }


    public void bindService(){
        Intent intent = new Intent(getApplicationContext(), PopUpService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        // font from assets: "assets/fonts/Roboto-Regular.ttf
        /*FontsOverride.setDefaultFont(this, "DEFAULT", "font.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "font.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "font.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "font.ttf");*/
    }
}
