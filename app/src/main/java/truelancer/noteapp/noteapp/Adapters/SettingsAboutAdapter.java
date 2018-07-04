package truelancer.noteapp.noteapp.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

public class SettingsAboutAdapter extends RecyclerView.Adapter<SettingsAboutAdapter.MyViewHolder> {

    private static final String SHARED_PREF_NAME = "hellonotepref";

    Context context;
    ArrayList<String> settingsList;
    CharSequence[] themes = {"Default", "Dark"};
    CharSequence[] bubblePositions = {"Top ", "Centre", "Bottom "};
    //CharSequence[] callRecords = {"Voice Call", "Voice Communication"};
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public SettingsAboutAdapter(Context context, ArrayList<String> settingsList) {
        this.context = context;
        this.settingsList = settingsList;
        pref = context.getSharedPreferences(context.getString(R.string.shared_pref), Context.MODE_PRIVATE);
        editor = pref.edit();
    }


    @Override
    public SettingsAboutAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_label, parent, false);
        return new SettingsAboutAdapter.MyViewHolder(view);
    }






    @Override
    public void onBindViewHolder(final SettingsAboutAdapter.MyViewHolder holder, final int position) {
        holder.settingsText.setText(settingsList.get(position));

        Log.d("manzil",settingsList.get(position));
        if(MyApp.nightMode){
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.white));
        }else {
            holder.settingsText.setTextColor(context.getResources().getColor(R.color.black));
        }








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
