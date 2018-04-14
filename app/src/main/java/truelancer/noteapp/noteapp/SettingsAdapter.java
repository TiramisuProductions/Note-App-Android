package truelancer.noteapp.noteapp;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Siddhant Naique on 24-03-2018.
 */

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private static final String SHARED_PREF_NAME = "hellonotepref";
    Context context;
    ArrayList<String> settingsList = new ArrayList<>();
    CharSequence[] themes = {"Default", "Dark"};
    CharSequence[] bubblePositions = {"Top Left", "Top Right", "Bottom Left", "Bottom right"};
    CharSequence[] callRecords = {"Voice Call", "Voice Communication"};


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsText, settingstate;
        View divider;

        public MyViewHolder(View itemView) {
            super(itemView);

            settingsText = (TextView) itemView.findViewById(R.id.setting_item);
            settingstate = (TextView) itemView.findViewById(R.id.settingstate);
            divider = (View) itemView.findViewById(R.id.divider);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toast.makeText(context, "" + getLayoutPosition(), Toast.LENGTH_SHORT).show();

                    switch (getLayoutPosition()) {
                        case 0://Theme
                            themeDialogBox(v);
                            break;
                        case 1://Bubble Location
                            bubbleDialogBox(v);
                            break;
                        case 2://Call Record
                            callRecordDialog(v);
                            break;
                        case 3://About
                            about(v);
                            break;
                    }
                }
            });
        }
    }

    public SettingsAdapter(Context context1) {
        this.context = context1;
        settingsListdata();
        pref = context.getSharedPreferences(context1.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        editor = pref.edit();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.settings_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.settingsText.setText(settingsList.get(position));

        switch (position) {
            case 0://Theme sub
                // holder.settingstate.setText(pref.getString("key_name1", "NULL1"));
                break;
            case 1://Bubble Location sub
               // holder.settingstate.setText(pref.getString("key_name2", "NULL2"));
                break;

            case 2://Call Record sub

                break;

            case 3://About sub

                break;

            default:
                //Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                break;

        }
        if (position == getItemCount() - 1) {
            holder.divider.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }


    private void themeDialogBox(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("Select Theme");
        builder.setSingleChoiceItems(themes, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://default
                        //Toast.makeText(context, "" +which, Toast.LENGTH_SHORT).show();
                        editor.putBoolean(context.getString(R.string.defaulttheme), true);
                        editor.apply();


                        break;
                    case 1://dark
                        editor.putBoolean(context.getString(R.string.defaulttheme), false);
                        editor.apply();

                        break;
                }
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                context.setTheme(R.style.MyMaterialThemeDark);

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void bubbleDialogBox(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("Select Theme");
        builder.setSingleChoiceItems(bubblePositions, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://Top left

                        break;
                    case 1://Top Right

                        break;
                    case 2://Bottom Left

                        break;
                    case 3://Bootom Right

                        break;
                }
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callRecordDialog (View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("Select Theme");
        builder.setSingleChoiceItems(callRecords, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://Voice call

                        break;
                    case 1://Voice Communication

                        break;
                }
            }
        });
        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();


            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void about(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("About the App");
        builder.setMessage("HelloNote is the name of the app");

        builder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void settingsListdata() {
        settingsList.add("Theme");
        settingsList.add("Bubble Location");
        settingsList.add("Call Record");
        settingsList.add("About");
    }

}
