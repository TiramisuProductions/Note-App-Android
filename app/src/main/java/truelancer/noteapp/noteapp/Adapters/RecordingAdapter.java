package truelancer.noteapp.noteapp.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.CallRecording;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.PlayRecording;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;


public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.MyView> {
    List<CallRecording> callRecordings;
    Context activity, itemContext;
    String timeStampString;
    String inout = "";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    String directoryNameForStoringRecordedCalls = "Hello-Note";

    public RecordingAdapter(Activity activity1, List<CallRecording> callRecordings1) {
        this.activity = activity1;
        this.callRecordings = callRecordings1;
    }

    public class MyView extends RecyclerView.ViewHolder {
        public TextView recordName, calledName, calledNumber, call_txt, date_time,savedDetails,recordId;
        public CardView recordCardView;
        public ImageView overflow, state_of_call, playStopbtn;


        public MyView(View itemView) {
            super(itemView);

            recordName = (TextView) itemView.findViewById(R.id.record_name);
            playStopbtn = (ImageView) itemView.findViewById(R.id.play_pause_btn);
            call_txt = (TextView) itemView.findViewById(R.id.callText);
            recordCardView = (CardView) itemView.findViewById(R.id.record_cardView);
            calledName = (TextView) itemView.findViewById(R.id.called_contact);
            calledNumber = (TextView) itemView.findViewById(R.id.called_number);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateOfCall);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            savedDetails = (TextView)itemView.findViewById(R.id.saved_details);
            recordId = (TextView)itemView.findViewById(R.id.record_id);

            itemContext = itemView.getContext();


        }
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_recording, parent, false);
        return new RecordingAdapter.MyView(itemView);
    }

    @Override
    public void onBindViewHolder(MyView holder, int position) {
        executeit(holder, position);
    }


    @Override
    public int getItemCount() {
        return callRecordings.size();
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    private void executeit(final MyView holder, final int position) {


        if(MyApp.nightMode){
            holder.recordCardView.setCardBackgroundColor(itemContext.getResources().getColor(R.color.darker_card));
            holder.call_txt.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledNumber.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.recordName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.date_time.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.savedDetails.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.recordId.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.overflow.setColorFilter(itemContext.getResources().getColor(R.color.white));

        }



        if (!callRecordings.get(position).isIncoming()) {

            holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
            inout = "Call To";

        } else {
            holder.state_of_call.setImageResource(R.drawable.ic_incoming);
            inout = "Call By";
        }

        String tsMilli = callRecordings.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        holder.call_txt.setText(inout);
        timeStampString = getDate(tsLong);
        holder.date_time.setText(timeStampString);
        holder.calledName.setText(callRecordings.get(position).getCalledName());
        holder.calledNumber.setText(callRecordings.get(position).getCalledNumber());
        holder.recordName.setText(callRecordings.get(position).getRecordName());

        holder.playStopbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (isPlaying) {
                    Log.d("buds", "Inside isplaying if");
                    stop(position, holder);

                } else {
                    Log.d("buds", "Inside else");
                    play(position, holder);
                }
                isPlaying = !isPlaying;*/
               Intent intent = new Intent(activity, PlayRecording.class);
               intent.putExtra("recName",callRecordings.get(position).getRecordName());
               intent.putExtra("recId",""+callRecordings.get(position).getId());
               intent.putExtra("rec_path",callRecordings.get(position).getRecordPath()); 
               activity.startActivity(intent);

            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.floatingActionMenu.close(true);
                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());

                popup_overflow.getMenu().findItem(R.id.save).setVisible(false);
                popup_overflow.getMenu().findItem(R.id.edit_menu).setVisible(false);

                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share

                            String shareText = "DateTime: [" + timeStampString + "]\n"
                                    + inout + " " + callRecordings.get(position).getCalledName() + "\n"
                                    + callRecordings.get(position).getCalledNumber() + "\n"
                                    + "Saved Details\n\n"
                                    + "Record Name : " + callRecordings.get(position).getRecordName();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("audio/mp3");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(callRecordings.get(position).getRecordPath()));
                            //shareIntent.putExtra(Intent.EXTRA_TEXT,""+shareText);

                            try {
                                activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.send_to)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (temp.equals("Delete")) {//Delete

                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setTitle("You want to delete");
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    File file = new File(callRecordings.get(position).getRecordPath()).getAbsoluteFile();
                                    boolean deleted = file.delete();
                                    Log.d("shower1", "" + callRecordings.get(position).getRecordPath() + " deleted: " + deleted);

                                    callRecordings.get(position).getId();
                                    CallRecording callRecording = CallRecording.findById(CallRecording.class, callRecordings.get(position).getId());
                                    callRecording.delete();
                                    callRecordings.remove(position);
                                    notifyDataSetChanged();
                                    if(callRecordings.size()==0){
                                        Utils.Visibility_no_data(5,true);
                                    }else {
                                        Utils.Visibility_no_data(5,false);
                                    }
                                    dialog.dismiss();
                                }
                            });


                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();

                        }


                        return true;
                    }
                });
                popup_overflow.show();
            }
        });

    }

}
