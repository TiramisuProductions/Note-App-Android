package truelancer.noteapp.noteapp.Services;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
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
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string


        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tsMilli = "" + startDate.getTime();
        //Toast.makeText(context, "Date : "+tsMilli, Toast.LENGTH_SHORT).show();

        //Log.d("Receiver", "Start");
        //Toast.makeText(context, " Receiver start ", Toast.LENGTH_SHORT).show();
        this.context = context;
        app = (MyApp) context.getApplicationContext();
        //app.popUpService.removeAllChatHeads();
        //Log.d("yoyo", "" + app.toHideBubble);

        String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
            String ringingNumber = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            Toast.makeText(context, "Ringing State Number is -" + ringingNumber, Toast.LENGTH_SHORT).show();
            /*if (!MyApp.toSave) {

            }*/

            if (app.firstRun) {

                new LongOperation().execute(ringingNumber,tsMilli);

            } else {
                if (app != null) {
                    app.checkForDraft(ringingNumber, getContactDisplayNameByNumber(ringingNumber, context), true, tsMilli);
                    app.popUpService.removeAllChatHeads();
                    app.popUpService.addChatHead(ringingNumber, getContactDisplayNameByNumber(ringingNumber, context), true, tsMilli);
                    if(MyApp.autoCallRecord){
                        Toast.makeText(context,"Auto Call record Iniated",Toast.LENGTH_LONG).show();
                    }
                }

            }

            Toast.makeText(context, "Ringing State Name is -" + getContactDisplayNameByNumber(ringingNumber, context), Toast.LENGTH_SHORT).show();
        }

        if ((state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK))) {
            Toast.makeText(context, "Received State Hookup", Toast.LENGTH_SHORT).show();
            MyApp.off_hook = true;

            /*Toast.makeText(context, "off_hook", Toast.LENGTH_SHORT).show();
            Log.d("wood", "off_hook ");*/
        }

        if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
            Toast.makeText(context, "Idle State", Toast.LENGTH_SHORT).show();
            Log.d("wood", "off hook: " + MyApp.off_hook);

            if(!MyApp.keepBubble && app.popUpService!=null){
                app.popUpService.removeAllChatHeads();
            }

            if (MyApp.off_hook) {
                // Toast.makeText(context, "call ended", Toast.LENGTH_SHORT).show();
                //Log.d("wood", "call ended ");
                if (MyApp.recording_in_progress) {
                    app.popUpService.stopRecording();
                }
                MyApp.off_hook = false;
            }
        }
    }


    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            MyApp.firstRunRingingNumber = params[0];
            MyApp.firstRunContactName = getContactDisplayNameByNumber(params[0], context);
            MyApp.firstRunIsIncoming = false;
            MyApp.firstRuntsMilli = params[1];
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            app.bindService();

            // might want to change "executed" for the returned string passed
            // into onPostExecute() but that is upto you
        }

        @Override
        protected void onPreExecute() {}

        @Override
        protected void onProgressUpdate(Void... values) {}
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


                Log.d("wood", "name: " + name);
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
