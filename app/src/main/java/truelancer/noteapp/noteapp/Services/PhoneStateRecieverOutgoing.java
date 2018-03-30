package truelancer.noteapp.noteapp.Services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.provider.ContactsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import truelancer.noteapp.noteapp.MyApp;

/**
 * Created by Siddhant Naique on 28-01-2018.
 */

public class PhoneStateRecieverOutgoing extends BroadcastReceiver {

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

        //Toast.makeText(context, " Receiver start outgoing ", Toast.LENGTH_SHORT).show();
        app = (MyApp) context.getApplicationContext();

        final String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if(app.firstRun){
            MyApp.firstRunRingingNumber = outgoingNumber;
            MyApp.firstRunContactName = getContactDisplayNameByNumber(outgoingNumber,context);
            MyApp.firstRunIsIncoming = false;
            MyApp.firstRuntsMilli = tsMilli;
            app.bindService();

        }
        else{
            app.checkForDraft(outgoingNumber, getContactDisplayNameByNumber(outgoingNumber, context), false, tsMilli);
            app.popUpService.removeAllChatHeads();
            app.popUpService.addChatHead(outgoingNumber, getContactDisplayNameByNumber(outgoingNumber, context), false, tsMilli);
        }






        String msg = "Intercepted outgoing call number " + outgoingNumber + " " + getContactDisplayNameByNumber(outgoingNumber, context);

        //Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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

            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
