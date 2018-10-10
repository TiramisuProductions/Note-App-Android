package truelancer.noteapp.noteapp;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import truelancer.noteapp.noteapp.Database.CallRecording;

public class PlayRecording extends AppCompatActivity {
    List<CallRecording> callRecordings;
    TextView startTime,endTime,recordName;
    ImageView audioImg,playBtn,stopBtn;
    SeekBar playBar;
    ConstraintLayout constraintLayout;
    CardView cardViewRec;
    View divider1;
    String recId,recName,recPath;
    long finalTime;
    int pausedLocation;
    MediaPlayer mediaPlayer,mp;
    Runnable runnable;
    boolean isPlaying=false,wasPaused=false;
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_recording);

        startTime=(TextView)findViewById(R.id.start_time);
        endTime=(TextView)findViewById(R.id.final_time);
        recordName=(TextView)findViewById(R.id.rec_name);
        audioImg= (ImageView) findViewById(R.id.audio_image);
        playBtn=(ImageView)findViewById(R.id.play);
        stopBtn=(ImageView)findViewById(R.id.stop);
        playBar= (SeekBar) findViewById(R.id.seekBar);
        constraintLayout=(ConstraintLayout)findViewById(R.id.record_layout);
        cardViewRec=(CardView)findViewById(R.id.cardView_rec);
        divider1=(View)findViewById(R.id.divider2);
        startTime.setText("00:00");
        recName=getIntent().getStringExtra("recName");
        recId=getIntent().getStringExtra("recId");
        recPath=getIntent().getStringExtra("rec_path");

        recordName.setText(recName);

        if (MyApp.nightMode){
            startTime.setTextColor(getResources().getColor(R.color.white));
            endTime.setTextColor(getResources().getColor(R.color.white));
            recordName.setTextColor(getResources().getColor(R.color.white));
            audioImg.setBackgroundColor(getResources().getColor(R.color.dark));
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            //cardViewRec.setBackgroundColor(getResources().getColor(R.color.darker_card));
            cardViewRec.setCardBackgroundColor(getResources().getColor(R.color.darker_card));
            divider1.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        Uri uri=Uri.parse(recPath);
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(this, uri);
        String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        finalTime = Long.parseLong(durationStr);
        endTime.setText(milliSecondsToTimer(finalTime));

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(recPath);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
            endTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
            playBar.setMax(mediaPlayer.getDuration());
        } catch (Exception e) {
            e.printStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    playBar.setProgress(mediaPlayer.getCurrentPosition());
                    startTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                }

                handler.postDelayed(this, 500);
            }
        };
        handler.postDelayed(runnable, 500);
        playBtn.setImageResource(R.drawable.ic_pause_circle_outline_blue_48dp);
        isPlaying = !isPlaying;
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isPlaying) {//not playing
                    playBtn.setImageResource(R.drawable.ic_play_circle_outline_blue_48dp);
                    if (mediaPlayer != null) {
                        wasPaused = true;
                        Log.d("pause", "onPause: " + mediaPlayer.getCurrentPosition());
                        pausedLocation = mediaPlayer.getCurrentPosition();
                        mediaPlayer.pause();
                    }

                } else {//playing
                    playBtn.setImageResource(R.drawable.ic_pause_circle_outline_blue_48dp);
                    if (wasPaused) {
                        Toast.makeText(PlayRecording.this, "was Paused", Toast.LENGTH_SHORT).show();
                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(recPath);
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
                        mediaPlayer.setLooping(true);
                        playBar.setMax(mediaPlayer.getDuration());

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null) {

                                    playBar.setProgress(mediaPlayer.getCurrentPosition());
                                } else {

                                }
                                handler.postDelayed(this, 500);
                            }
                        };
                        handler.postDelayed(runnable, 500);

                        wasPaused = false;
                    } else {

                        mediaPlayer = new MediaPlayer();
                        try {
                            mediaPlayer.setDataSource(recPath);
                            mediaPlayer.prepare();
                            mediaPlayer.start();
                            mediaPlayer.setLooping(true);
                            endTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                            playBar.setMax(mediaPlayer.getDuration());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        runnable = new Runnable() {
                            @Override
                            public void run() {
                                if (mediaPlayer != null) {
                                    playBar.setProgress(mediaPlayer.getCurrentPosition());
                                    startTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                                }

                                handler.postDelayed(this, 500);
                            }
                        };
                        handler.postDelayed(runnable, 500);
                    }
                }

                isPlaying = !isPlaying;


            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    startTime.setText("00:00");
                    playBar.setProgress(0);
                    mediaPlayer.stop();
                    //pausedLocation = 0000000000000;
                    playBtn.setImageResource(R.drawable.ic_play_circle_outline_blue_48dp);
                    isPlaying=false;
                    wasPaused = false;
                    handler.removeCallbacks(runnable);
                    handler.removeCallbacksAndMessages(null);

                }
            }
        });




        playBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {





            }
        });

    }

    @Override
    protected void onStop() {

        if (isPlaying) {//not playing
            playBtn.setImageResource(R.drawable.ic_play_circle_outline_blue_48dp);

            if (mediaPlayer != null) {
                wasPaused = true;
                Log.d("pause", "onPause: " + mediaPlayer.getCurrentPosition());
                pausedLocation = mediaPlayer.getCurrentPosition();
                mediaPlayer.pause();

            }

        }
        isPlaying=!isPlaying;
        super.onStop();
    }

    @Override
    public void onBackPressed() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            startTime.setText("00:00");
            mediaPlayer = null;
            playBar.setProgress(0);
            handler.removeCallbacks(runnable);
            handler.removeCallbacksAndMessages(null);
            if (runnable != null) {
                runnable = null;
            }
        }
        super.onBackPressed();
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
