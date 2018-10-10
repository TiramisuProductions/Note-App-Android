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
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import truelancer.noteapp.noteapp.MyApp;



public class PhoneStateRecieverOutgoing extends BroadcastReceiver {

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

        //Toast.makeText(context, " Receiver start outgoing ", Toast.LENGTH_SHORT).show();
        app = (MyApp) context.getApplicationContext();
        this.context = context;

        final String outgoingNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (app.firstRun) {

            new LongOperation().execute(outgoingNumber,tsMilli);
            Toast.makeText(context,"Auto Call record Iniated",Toast.LENGTH_LONG).show();

            //app.bindService();

        } else {
            app.checkForDraft(outgoingNumber, getContactDisplayNameByNumber(outgoingNumber, context), false, tsMilli);
            app.popUpService.removeAllChatHeads();
            app.popUpService.addChatHead(outgoingNumber, getContactDisplayNameByNumber(outgoingNumber, context), false, tsMilli);
            if(MyApp.autoCallRecord){
                Toast.makeText(context,"Auto Call record Iniated",Toast.LENGTH_LONG).show();
            }
        }


        String msg = "Intercepted outgoing call number " + outgoingNumber + " " + getContactDisplayNameByNumber(outgoingNumber, context);

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
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

            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
}
