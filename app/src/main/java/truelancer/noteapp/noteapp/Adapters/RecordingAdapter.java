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
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;

/**
 * Created by Siddhant Naique on 10-04-2018.
 */


public class RecordingAdapter extends RecyclerView.Adapter<RecordingAdapter.MyView> {
    List<CallRecording> callRecordings;
    Context activity, itemContext;
    String timeStampString;
    String inout = "";
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    boolean isPlaying = false;
    MediaPlayer mp;
    boolean wasPaused = false;

    MediaPlayer mediaPlayer;
    final Handler handler = new Handler();
    Runnable run;
    long finalTime;
    double startTime;
    int pausedLocation;
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
            playStopbtn = (ImageView) itemView.findViewById(R.id.play_puase_btn);
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

                final Dialog dialog = new Dialog(itemContext);
                dialog.setContentView(R.layout.dialog_media_player);
                dialog.setTitle("Title...");
                dialog.setCanceledOnTouchOutside(true);

                final ImageView play, stop;
                final TextView recordName, startTimeTXT, finalTimeTXT;
                final SeekBar seekBar;

                play = (ImageView)dialog.findViewById(R.id.play);
                stop = (ImageView) dialog.findViewById(R.id.stop);
                seekBar = (SeekBar) dialog.findViewById(R.id.seekBar);
                recordName = (TextView) dialog.findViewById(R.id.record_name);
                startTimeTXT = (TextView) dialog.findViewById(R.id.start_time);
                finalTimeTXT = (TextView) dialog.findViewById(R.id.final_time);

                startTimeTXT.setText("0:00");
                recordName.setText(callRecordings.get(position).getRecordName());


                Uri uri = Uri.parse(callRecordings.get(position).getRecordPath());
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(activity, uri);
                String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                finalTime = Long.parseLong(durationStr);
                finalTimeTXT.setText(milliSecondsToTimer(finalTime));


                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(callRecordings.get(position).getRecordPath());
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    finalTimeTXT.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                    seekBar.setMax(mediaPlayer.getDuration());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                run = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            startTimeTXT.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                        }

                        handler.postDelayed(this, 500);
                    }
                };
                handler.postDelayed(run, 500);
                play.setImageResource(R.drawable.ic_pause);
                isPlaying = !isPlaying;

                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isPlaying) {//not playing
                            play.setImageResource(R.drawable.ic_play);
                            if (mediaPlayer != null) {
                                wasPaused = true;
                                Log.d("pause", "onPause: " + mediaPlayer.getCurrentPosition());
                                pausedLocation = mediaPlayer.getCurrentPosition();
                                mediaPlayer.pause();
                            }

                        } else {//playing
                            play.setImageResource(R.drawable.ic_pause);
                            if (wasPaused) {
                                Toast.makeText(activity, "was Paused", Toast.LENGTH_SHORT).show();
                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(callRecordings.get(position).getRecordPath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mediaPlayer.prepare();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                mediaPlayer.seekTo(pausedLocation);
                                mediaPlayer.start();

                                seekBar.setMax(mediaPlayer.getDuration());

                                run = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mediaPlayer != null) {

                                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                        } else {

                                        }
                                        handler.postDelayed(this, 500);
                                    }
                                };
                                handler.postDelayed(run, 500);

                                wasPaused = false;
                            } else {

                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource(callRecordings.get(position).getRecordPath());
                                    mediaPlayer.prepare();
                                    mediaPlayer.start();
                                    finalTimeTXT.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                                    seekBar.setMax(mediaPlayer.getDuration());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                run = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mediaPlayer != null) {
                                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                                            startTimeTXT.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                                        }

                                        handler.postDelayed(this, 500);
                                    }
                                };
                                handler.postDelayed(run, 500);
                            }
                        }

                        isPlaying = !isPlaying;

                    }
                });

                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mediaPlayer != null) {
                            startTimeTXT.setText("0:00");
                            seekBar.setProgress(0);
                            mediaPlayer.stop();
                            //pausedLocation = 0000000000000;
                            play.setImageResource(R.drawable.ic_play);
                            isPlaying=false;
                            wasPaused = false;
                            handler.removeCallbacks(run);
                            handler.removeCallbacksAndMessages(null);

                        }
                    }
                });

                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (mediaPlayer != null && fromUser) {
                            mediaPlayer.seekTo(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        if (mediaPlayer != null) {
                            mediaPlayer.stop();
                            startTimeTXT.setText("0:00");
                            mediaPlayer = null;
                            seekBar.setProgress(0);
                            handler.removeCallbacks(run);
                            handler.removeCallbacksAndMessages(null);
                            if (run != null) {
                                run = null;
                            }
                        }
                    }
                });

                dialog.show();
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.floatingActionMenu.close(true);
                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());

                popup_overflow.getMenu().findItem(R.id.save).setVisible(false);

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

                        } else {//Edit

                            final Dialog dialog = new Dialog(itemContext);
                            dialog.setContentView(R.layout.edit_dialog_record);
                            dialog.setCancelable(false);

                            final TextInputLayout recordNameTextInputLayout = (TextInputLayout) dialog.findViewById(R.id.recordtextInputLayout);
                            final TextInputLayout recordCalledNameInputLayout = (TextInputLayout) dialog.findViewById(R.id.recordCalledNameInputLayout);
                            final TextInputLayout recordCalledNumberTextInputLayout = (TextInputLayout) dialog.findViewById(R.id.recordCalledNumberInputLayout);
                            final EditText recordName = (EditText) dialog.findViewById(R.id.editRecordName);
                            final EditText recordCalledName = (EditText) dialog.findViewById(R.id.editCalledNameRec);
                            final EditText recordCalledNumber = (EditText) dialog.findViewById(R.id.editCalledNumberRec);
                            final ImageView tick1 = (ImageView) dialog.findViewById(R.id.tick1);
                            final ImageView tick2= (ImageView) dialog.findViewById(R.id.tick2);
                            final ImageView tick3 = (ImageView) dialog.findViewById(R.id.tick3);
                            Button btnDone = (Button) dialog.findViewById(R.id.button_done);
                            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                            TextView title=(TextView)dialog.findViewById(R.id.edit_dialog_text);
                            title.setText(R.string.edit_dialog_record);
                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            String options[] = {"Incoming","Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext,   android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            calledState.setAdapter(adminSpinnerArrayAdapter);



                            recordName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    if (!TextUtils.isEmpty(recordName.getText().toString())) {
                                        tick1.setVisibility(View.VISIBLE);
                                        recordNameTextInputLayout.setError(null);
                                    } else {
                                        tick1.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {
                                }
                            });

                            recordCalledName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (!TextUtils.isEmpty(recordCalledName.getText().toString())) {
                                        tick2.setVisibility(View.VISIBLE);
                                        recordCalledNameInputLayout.setError(null);
                                    } else {
                                        tick2.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            recordCalledNumber.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (String.valueOf(s).length()>0) {
                                        tick3.setVisibility(View.VISIBLE);
                                        recordCalledNumberTextInputLayout.setError(null);
                                    } else {
                                        tick3.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            btnDone.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Log.d("bluebottle1", "onClick: ");

                                    if (TextUtils.isEmpty(recordName.getText().toString())) {
                                        recordNameTextInputLayout.setError("Enter Record Name");
                                        return;
                                    }else {

                                        CallRecording callRecording = CallRecording.findById(CallRecording.class, callRecordings.get(position).getId());
                                        callRecording.setRecordName(recordName.getText().toString());
                                        callRecording.setCalledName(recordCalledName.getText().toString());
                                        callRecording.setCalledNumber(recordCalledNumber.getText().toString());
                                        if (calledState.getSelectedItemPosition() == 0) {
                                            callRecording.setIncoming(true);
                                        } else {
                                            callRecording.setIncoming(false);
                                        }
                                        callRecording.save();

                                        dialog.dismiss();
                                    }
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                            recordName.setText(callRecordings.get(position).getRecordName());
                            recordCalledName.setText(callRecordings.get(position).getCalledName());
                            recordCalledNumber.setText(callRecordings.get(position).getCalledNumber());
                            dialog.show();
                        }


                        return true;
                    }
                });
                popup_overflow.show();
            }
        });

    }

    public void stop(int position, MyView holder) {
        holder.playStopbtn.setImageResource(R.drawable.ic_play);
        Log.d("buds", "Inside stop funtion");
        mp.stop();
        mp.release();
        mp = null;
    }

    public void play(final int position, final MyView holder) {
        holder.playStopbtn.setImageResource(R.drawable.ic_stop);
        Log.d("buds", "Inside play function " + callRecordings.get(position).getRecordPath());
        mp = new MediaPlayer();

        try {
            mp.setDataSource(callRecordings.get(position).getRecordPath());
            mp.prepare();
            mp.start();
            Log.d("jeans", ""+callRecordings.get(position).getRecordPath());


        } catch (Exception e) {

            e.printStackTrace();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                holder.playStopbtn.setImageResource(R.drawable.ic_play);
                mp.release();

            }
        });
    }

    /**
     * Function to convert milliseconds time to Timer Format
     * Hours:Minutes:Seconds
     */
    public String milliSecondsToTimer(long milliseconds) {
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int) (milliseconds / (1000 * 60 * 60));
        int minutes = (int) (milliseconds % (1000 * 60 * 60)) / (1000 * 60);
        int seconds = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        // Add hours if there
        if (hours > 0) {
            finalTimerString = hours + ":";
        }

        // Prepending 0 to seconds if it is one digit
        if (seconds < 10) {
            secondsString = "0" + seconds;
        } else {
            secondsString = "" + seconds;
        }

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

}
