package truelancer.noteapp.noteapp.Services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import truelancer.noteapp.noteapp.MyApp;


/**
 * Created by Sarvesh Palav on 12/6/2017.
 */


public class PhoneStateReceiver extends BroadcastReceiver {

    public static MyApp app;

    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string


        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tsMilli = "" + startDate.getTime();
        //Toast.makeText(context, "Date : "+tsMilli, Toast.LENGTH_SHORT).show();

        Log.d("Receiver", "Start");
        //Toast.makeText(context, " Receiver start ", Toast.LENGTH_SHORT).show();
        app = (MyApp) context.getApplicationContext();
        //app.popUpService.removeAllChatHeads();
        Log.d("yoyo", "" + app.toHideBubble);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String ringingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Ringing State Number is -" + ringingNumber, Toast.LENGTH_SHORT).show();
            if (!MyApp.toSave) {

            }

            if(app.firstRun){
                MyApp.firstRunRingingNumber = ringingNumber;
                MyApp.firstRunContactName = getContactDisplayNameByNumber(ringingNumber,context);
                MyApp.firstRunIsIncoming = false;
                MyApp.firstRuntsMilli = tsMilli;
                app.bindService();

            }
            else{
                app.checkForDraft(ringingNumber, getContactDisplayNameByNumber(ringingNumber, context), true, tsMilli);
                app.popUpService.removeAllChatHeads();
                app.popUpService.addChatHead(ringingNumber, getContactDisplayNameByNumber(ringingNumber, context), true, tsMilli);
            }


            //Toast.makeText(context, "Ringing State Name is -" + getContactDisplayNameByNumber(ringingNumber, context), Toast.LENGTH_SHORT).show();

        }

        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
            //Toast.makeText(context, "Received State Hookup", Toast.LENGTH_SHORT).show();
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            //Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
        }

    }


    public String getContactDisplayNameByNumber(String number, Context context) {
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String name = "?";

        ContentResolver contentResolver = context.getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[]{BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
