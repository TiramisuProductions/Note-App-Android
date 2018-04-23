package truelancer.noteapp.noteapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.MyViewHolder> {

    private static final String SHARED_PREF_NAME = "hellonotepref";
    Context context;
    ArrayList<String> settingsList = new ArrayList<>();
    CharSequence[] themes = {"Default", "Dark"};
    CharSequence[] bubblePositions = {"Top ","Centre",  "Bottom "};
    CharSequence[] callRecords = {"Voice Call", "Voice Communication"};
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SettingsAdapter(Context context, ArrayList<String> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
        pref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    @Override
    public SettingsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.settings_row, parent, false);
        return new SettingsAdapter.MyViewHolder(view);
    }


    private void themeDialogBox(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("Select Theme");

        final Intent i = context.getPackageManager()
                .getLaunchIntentForPackage( context.getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        builder.setSingleChoiceItems(themes, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://default
                        //Toast.makeText(context, "" +which, Toast.LENGTH_SHORT).show();
                        editor.putBoolean(context.getString(R.string.defaulttheme), true);
                        editor.apply();
                        context.startActivity(i);
                        break;
                    case 1://dark
                        editor.putBoolean(context.getString(R.string.defaulttheme), false);
                        editor.apply();
                        context.startActivity(i);
                        break;
                }
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
                    case 0://Top
                    editor.putString(context.getString(R.string.bubblelocation),"top");
                    editor.commit();
                    dialog.dismiss();
                        break;
                    case 1://Centre
                        editor.putString(context.getString(R.string.bubblelocation),"centre");
                        editor.commit();
                        dialog.dismiss();
                        break;
                    case 2://Bottom
                        editor.putString(context.getString(R.string.bubblelocation),"bottom");
                        editor.commit();
                        dialog.dismiss();
                        break;

                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void callRecordDialog (View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        builder.setTitle("Select configuration");
        builder.setSingleChoiceItems(callRecords, -1, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0://Voice call
                        editor.putBoolean(context.getString(R.string.voicecall),true);
                        editor.commit();
                        dialog.dismiss();
                        notifyDataSetChanged();
                        break;
                    case 1://Voice Communication
                        editor.putBoolean(context.getString(R.string.voicecall),false);
                        editor.commit();
                        dialog.dismiss();
                        notifyDataSetChanged();
                        break;
                }
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

    @Override
    public void onBindViewHolder(final SettingsAdapter.MyViewHolder holder, final int position) {
        holder.settingsText.setText(settingsList.get(position));
        if(!MyApp.defaultTheme){
            Log.d("hello","icecream");
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.white));
            holder.settingstate.setTextColor(context.getResources().getColor(R.color.white));


        }


        switch (position){
            case 0:
                if(!MyApp.defaultTheme){
                    holder.settingstate.setText("Dark Theme");
                }else {
                    holder.settingstate.setText("Default Theme");
                }
                break;

            case 2:
                if(pref.getBoolean(context.getString(R.string.voicecall),true)){
                    holder.settingstate.setText("Voice Call");
                }else {
                    holder.settingstate.setText("Voice Communication");
                }
                break;

            case 3:
                holder.settingstate.setText("");
                break;
        }



           holder.itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (position) {
                        case 0://Theme
                            themeDialogBox(view);
                            break;
                        case 1://Bubble Location
                            bubbleDialogBox(view);
                            break;
                        case 2://Call Record
                            callRecordDialog(view);

                            break;
                        case 3://About
                            about(view);
                            break;
                    }
                }
            });


    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsText, settingstate;
        View divider;
        ConstraintLayout itemLayout;

        public MyViewHolder(View itemView) {
            super(itemView);

            settingsText = (TextView) itemView.findViewById(R.id.setting_item);
            settingstate = (TextView) itemView.findViewById(R.id.settingstate);
            divider = (View) itemView.findViewById(R.id.divider);
            itemLayout = (ConstraintLayout)itemView.findViewById(R.id.item_layout);




        }



    }
}
