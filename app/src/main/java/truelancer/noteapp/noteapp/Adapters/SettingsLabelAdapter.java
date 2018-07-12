package truelancer.noteapp.noteapp.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

/**
 * Created by sarveshpalav on 03/07/18.
 */

public class SettingsLabelAdapter extends RecyclerView.Adapter<SettingsLabelAdapter.MyViewHolder> {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    Context context;
    ArrayList<String> settingsList;
    CharSequence[] themes = {"Default", "Dark"};
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SettingsLabelAdapter(Context context, ArrayList<String> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
        pref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    @Override
    public SettingsLabelAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_label, parent, false);
        return new SettingsLabelAdapter.MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final SettingsLabelAdapter.MyViewHolder holder, final int position) {
        holder.settingsText.setText(settingsList.get(position));
        if (MyApp.nightMode) {
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.black));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (position) {
                    case 0:
                        share();
                        //Toast.makeText(context, "0", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        rate_us();
                        Toast.makeText(context, "1", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        sendFeedback();
                        Toast.makeText(context, "2", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


    }

    private void rate_us() {
        Uri uri = Uri.parse(context.getString(R.string.rate_us_link)); // missing 'http://' will cause crashed
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    private void share() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "" + context.getString(R.string.app_share_description_playstore) + " " + context.getString(R.string.app_share_link_playstore));
        sendIntent.setType("text/plain");
        context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.choose_app)));
    }

    private void sendFeedback() {

        final Dialog dialog = new Dialog(context);
        String options[] = {context.getString(R.string.admin_option_1), context.getString(R.string.admin_option_2)};
        dialog.setContentView(R.layout.dialog_admin);
        final Spinner adminSpinner = (Spinner) dialog.findViewById(R.id.admin_chooser);
        Button buttonSelect = (Button) dialog.findViewById(R.id.btnSelect);
        ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, options);
        adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        adminSpinner.setAdapter(adminSpinnerArrayAdapter);

        buttonSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adminSpinner.getSelectedItemPosition() == 0) {
                    String email[] = {context.getString(R.string.admin_email)};
                    shareToGmail(context, email, context.getString(R.string.admin_option_1));
                } else {
                    String email[] = {context.getString(R.string.admin_email)};
                    shareToGmail(context, email, context.getString(R.string.admin_option_2));
                }
                dialog.dismiss();
            }
        });

        dialog.show();


    }

    private void shareToGmail(Context context, String email[], String subject) {
        String body = null;
        try {
            body = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            body = "\n-----------------------------\nPlease don't remove this information\n Device OS: Android \n Device OS version: " +
                    Build.VERSION.RELEASE + "\n App Version: " + body + "\n Device Brand: " + Build.BRAND +
                    "\n Device Model: " + Build.MODEL + "\n Device Manufacturer: " + Build.MANUFACTURER +
                    "\n-----------------------------\n";
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        intent.putExtra(Intent.EXTRA_SUBJECT, "" + subject);
        intent.putExtra(Intent.EXTRA_TEXT, body);

        final PackageManager pm = context.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches) {
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        }
        if (best != null) {
            Log.d("wood", "best if: ");
            intent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            context.startActivity(intent);
        } else {
            Log.d("wood", "best else");

            context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
        }

        //context.startActivity(intent);
        //context.startActivity(Intent.createChooser(intent, context.getString(R.string.choose_email_client)));
    }


    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsText;
        Switch settingsSwitch;
        View divider;
        ConstraintLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            settingsText = (TextView) itemView.findViewById(R.id.settingstext);
            settingsSwitch = (Switch) itemView.findViewById(R.id.settingsswitch);
            divider = (View) itemView.findViewById(R.id.divider);
            itemLayout = (ConstraintLayout) itemView.findViewById(R.id.item_layout);


        }
    }
}
