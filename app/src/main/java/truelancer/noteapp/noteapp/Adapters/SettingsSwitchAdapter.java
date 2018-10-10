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
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

public class SettingsSwitchAdapter extends RecyclerView.Adapter<SettingsSwitchAdapter.MyViewHolder> {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    Context context;
    ArrayList<String> settingsList;
    CharSequence[] themes = {"Default", "Dark"};
    CharSequence[] bubblePositions = {"Top ", "Centre", "Bottom "};
    //CharSequence[] callRecords = {"Voice Call", "Voice Communication"};
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SettingsSwitchAdapter(Context context, ArrayList<String> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
        pref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    @Override
    public SettingsSwitchAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_switch, parent, false);
        return new SettingsSwitchAdapter.MyViewHolder(view);
    }






    @Override
    public void onBindViewHolder(final SettingsSwitchAdapter.MyViewHolder holder, final int position) {
        holder.settingsText.setText(settingsList.get(position));

        if(MyApp.nightMode){
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.white));
            holder.settingsSwitch.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.black));
            holder.settingsSwitch.setTextColor(context.getResources().getColor(R.color.black));
        }




        switch (position){
            case 0:
                if(MyApp.nightMode){
                    holder.settingsSwitch.setText("On");
                    holder.settingsSwitch.setChecked(true);

                    editor.putBoolean(context.getString(R.string.key_night_mode),true);
                    editor.commit();
                }else{
                    holder.settingsSwitch.setText("Off");
                    holder.settingsSwitch.setChecked(false);

                    editor.putBoolean(context.getString(R.string.key_night_mode),false);
                    editor.commit();
                }



                holder.settingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            holder.settingsSwitch.setText("On");
                            editor.putBoolean(context.getString(R.string.key_night_mode),true);
                            editor.commit();
                            MyApp.nightMode = true;
                            EventBus.getDefault().post(new EventB("changeUIMode"));
                            notifyDataSetChanged();
                        }else{
                            holder.settingsSwitch.setText("Off");
                            editor.putBoolean(context.getString(R.string.key_night_mode),false);
                            editor.commit();
                            MyApp.nightMode = false;
                            EventBus.getDefault().post(new EventB("changeUIMode"));
                            notifyDataSetChanged();

                        }
                    }
                });
                break;

            case 1:

                if(pref.getBoolean(context.getString(R.string.key_keep_bubble),true)){
                    holder.settingsSwitch.setText("On");
                    holder.settingsSwitch.setChecked(true);

                }else{
                    holder.settingsSwitch.setText("Off");
                    holder.settingsSwitch.setChecked(false);
                }

                holder.settingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            holder.settingsSwitch.setText("On");
                            MyApp.keepBubble = true;
                            editor.putBoolean(context.getString(R.string.key_keep_bubble),true);
                            editor.commit();
                        }else{
                            holder.settingsSwitch.setText("Off");
                            MyApp.keepBubble = false;
                            editor.putBoolean(context.getString(R.string.key_keep_bubble),false);
                            editor.commit();

                        }
                    }
                });

                break;


            case 2:

                if(pref.getBoolean(context.getString(R.string.key_auto_call_record),false)){
                    holder.settingsSwitch.setText("On");
                    holder.settingsSwitch.setChecked(true);

                }else{
                    holder.settingsSwitch.setText("Off");
                    holder.settingsSwitch.setChecked(false);
                }

                holder.settingsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if(b){
                            holder.settingsSwitch.setText("On");
                            MyApp.autoCallRecord = true;
                            editor.putBoolean(context.getString(R.string.key_auto_call_record),true);
                            editor.commit();
                        }else{
                            holder.settingsSwitch.setText("Off");
                            MyApp.autoCallRecord = false;
                            editor.putBoolean(context.getString(R.string.key_auto_call_record),false);
                            editor.commit();

                        }
                    }
                });






        }












    }

    @Override
    public int getItemCount() {
        return settingsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView settingsText;
        Switch   settingsSwitch;
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
