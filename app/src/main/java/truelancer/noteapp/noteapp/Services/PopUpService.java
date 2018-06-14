package truelancer.noteapp.noteapp.Services;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.constraint.ConstraintLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flipkart.chatheads.ui.ChatHead;
import com.flipkart.chatheads.ui.ChatHeadArrangement;
import com.flipkart.chatheads.ui.ChatHeadListener;
import com.flipkart.chatheads.ui.ChatHeadManager;
import com.flipkart.chatheads.ui.ChatHeadViewAdapter;
import com.flipkart.chatheads.ui.MaximizedArrangement;
import com.flipkart.chatheads.ui.MinimizedArrangement;
import com.flipkart.chatheads.ui.container.DefaultChatHeadManager;
import com.flipkart.chatheads.ui.container.WindowManagerContainer;
import com.flipkart.circularImageView.CircularDrawable;
import com.flipkart.circularImageView.TextDrawer;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.CustomChatHeadConfig;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.CallRecording;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

public class PopUpService extends Service {

    private static final String TAG = "PopUpService";
    private final IBinder mBinder = new LocalBinder();
    int count = 0;
    MediaRecorder mRecorder;
    boolean isStartRecording = false;
    String pathToSaveRecordedCalls = null;
    String directoryNameForStoringRecordedCalls = "Hello-Note";
    String fileNameOfCallRecording = "Record_HN" + System.currentTimeMillis();// just the initial name
    String suggestedRecordName;
    String file_path;
    SharedPreferences pref;
    private DefaultChatHeadManager<String> chatHeadManager;
    private int chatHeadIdentifier = 0;
    private WindowManagerContainer windowManagerContainer;
    private Map<String, View> viewCache = new HashMap<>();
    private CustomChatHeadConfig customChatHeadConfig;
    private String calledNumber = "";
    private String calledName = "";
    private boolean incomingCall, isDone;
    private String timeStampMilli = "";

    //Email validation
    public final static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        pref = getBaseContext().getSharedPreferences(getBaseContext().getString(R.string.shared_pref), Context.MODE_PRIVATE);
        windowManagerContainer = new WindowManagerContainer(this);
        chatHeadManager = new DefaultChatHeadManager<String>(this, windowManagerContainer);

        String bubbleLocation = pref.getString(getBaseContext().getString(R.string.bubblelocation), "top");

        switch (bubbleLocation) {
            case "top":
                customChatHeadConfig = new CustomChatHeadConfig(getBaseContext(), 100, 200);
                break;
            case "centre":
                Log.d("cemtre", "centre");
                customChatHeadConfig = new CustomChatHeadConfig(getBaseContext(), 800, 800);
                break;
            case "bottom":
                customChatHeadConfig = new CustomChatHeadConfig(getBaseContext(), 100, 1500);
                break;
        }


        chatHeadManager.setConfig(customChatHeadConfig);

        // The view adapter is invoked when someone clicks a chat head.
        chatHeadManager.setViewAdapter(new ChatHeadViewAdapter<String>() {

            @Override
            public View attachView(final String key, ChatHead chatHead, ViewGroup parent) {
                // You can return the view which is shown when the arrangement changes to maximized.
                // The passed "key" param is the same key which was used when adding the chat head.


                View cachedView = viewCache.get(key);
                if (cachedView == null) {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.fragment_hoverphone, parent, false);

                    final ViewPager pager = (ViewPager) view.findViewById(R.id.viewPager);
                    final ConstraintLayout layout = (ConstraintLayout) view.findViewById(R.id.layout);
                    pager.setAdapter(new CustomPagerAdapter(getApplicationContext()));
                    final SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewPagerTab);

                    if (!MyApp.defaultTheme) {
                        viewPagerTab.setDefaultTabTextColor(getResources().getColor(R.color.white));
                        layout.setBackgroundColor(getResources().getColor(R.color.dark));
                    }


                    viewPagerTab.setViewPager(pager);

                    /*Make a directory with the varable name directoryNameForStoringRecordedCalls
                     * to store the recorded calls */
                    makeDirectory();


                    Button b1 = (Button) view.findViewById(R.id.goToAppButton);
                    final Button recordCall = (Button) view.findViewById(R.id.callRecordButton);
                    Button save = (Button) view.findViewById(R.id.save);


                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("booh", "" + pager.getCurrentItem());
                            EventBus.getDefault().post(new EventB("" + pager.getCurrentItem()));

                        }
                    });

                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent launchApp = getPackageManager().getLaunchIntentForPackage("truelancer.noteapp.noteapp");
                            startActivity(launchApp);
                            minimize();
                        }
                    });

                    recordCall.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            //   Toast.makeText(PopUpService.this, "record call : " + recordCall.getText().toString(), Toast.LENGTH_SHORT).show();

                            if (!isCallActive(getApplicationContext())) {
                                Animation shake = AnimationUtils.loadAnimation(PopUpService.this, R.anim.shake);
                                recordCall.startAnimation(shake);
                                Toast.makeText(getApplicationContext(), "Call is not active", Toast.LENGTH_LONG).show();
                            } else {

                                if (isStartRecording) {
                                    recordCall.setText(getString(R.string.call_record_start));
                                    stopRecording();
                                } else {
                                    recordCall.setText(getString(R.string.call_record_stop));
                                    startRecording();
                                }
                                isStartRecording = !isStartRecording;
                            }
                        }
                    });


                    cachedView = view;
                    viewCache.put(key, view);
                }
                parent.addView(cachedView);
                return cachedView;
            }

            @Override
            public void detachView(String key, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(key);
                if (cachedView != null) {
                    parent.removeView(cachedView);
                }
            }


            @Override
            public void removeView(String key, ChatHead<? extends Serializable> chatHead, ViewGroup parent) {
                View cachedView = viewCache.get(key);
                if (cachedView != null) {
                    viewCache.remove(key);
                    parent.removeView(cachedView);
                }
            }

            @Override
            public Drawable getChatHeadDrawable(String key) {
                // this is where you return a drawable for the chat head itself based on the key. Typically you return a circular shape
                // you may want to checkout circular image library https://github.com/flipkart-incubator/circular-image
                return PopUpService.this.getChatHeadDrawable(key);
            }
        });


        chatHeadManager.setListener(new ChatHeadListener() {
            @Override
            public void onChatHeadAdded(Object key) {
                //called whenever a new chat head with the specified 'key' has been added
                Log.d(TAG, "onChatHeadAdded() called with: key = [" + key + "]");
            }

            @Override
            public void onChatHeadRemoved(Object key, boolean userTriggered) {
                //called whenever a new chat head with the specified 'key' has been removed.
                // userTriggered: 'true' says whether the user removed the chat head, 'false' says that the code triggered it
                Log.d(TAG, "onChatHeadRemoved() called with: key = [" + key + "], userTriggered = [" + userTriggered + "]");
            }

            @Override
            public void onChatHeadArrangementChanged(ChatHeadArrangement oldArrangement, ChatHeadArrangement newArrangement) {
                //called whenever the chat head arrangement changed. For e.g minimized to maximized or vice versa.
                Log.d(TAG, "onChatHeadArrangementChanged() called with: oldArrangement = [" + oldArrangement + "], newArrangement = [" + newArrangement + "]");
            }

            @Override
            public void onChatHeadAnimateStart(ChatHead chatHead) {
                //called when the chat head has started moving (
                Log.d(TAG, "onChatHeadAnimateStart() called with: chatHead = [" + chatHead + "]");
            }

            @Override
            public void onChatHeadAnimateEnd(ChatHead chatHead) {
                //called when the chat head has settled after moving
                Log.d(TAG, "onChatHeadAnimateEnd() called with: chatHead = [" + chatHead + "]");
            }
        });

        chatHeadManager.setOnItemSelectedListener(new ChatHeadManager.OnItemSelectedListener<String>() {
            @Override
            public boolean onChatHeadSelected(String key, ChatHead chatHead) {
                if (chatHeadManager.getArrangementType() == MaximizedArrangement.class) {
                    Log.d(TAG, "chat head got selected in maximized arrangement");
                }
                return false; //returning true will mean that you have handled the behaviour and the default behaviour will be skipped
            }

            @Override
            public void onChatHeadRollOver(String key, ChatHead chatHead) {
                Log.d(TAG, "onChatHeadRollOver() called with: key = [" + key + "], chatHead = [" + chatHead + "]");
            }

            @Override
            public void onChatHeadRollOut(String key, ChatHead chatHead) {
                Log.d(TAG, "onChatHeadRollOut() called with: key = [" + key + "], chatHead = [" + chatHead + "]");
            }
        });
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string


        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tsMilli = "" + startDate.getTime();
        addChatHead(MyApp.firstRunRingingNumber, MyApp.firstRunContactName, MyApp.firstRunIsIncoming, MyApp.firstRuntsMilli);
        chatHeadManager.setArrangement(MinimizedArrangement.class, null);
        moveToForeground();

    }

    private Drawable getChatHeadDrawable(String key) {
        Random rnd = new Random();
        // int randomColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        int color = Color.argb(255, 33, 150, 243);
        CircularDrawable circularDrawable = new CircularDrawable();
        circularDrawable.setBitmapOrTextOrIcon(new TextDrawer().setText("HN").setBackgroundColor(color));
        int badgeCount = (int) (Math.random() * 10f);
        //circularDrawable.setNotificationDrawer(new CircularNotificationDrawer().setNotificationText(String.valueOf(badgeCount)).setNotificationAngle(135).setNotificationColor(Color.WHITE, Color.RED));
        circularDrawable.setBorder(Color.WHITE, 3);
        return circularDrawable;

    }

    private void moveToForeground() {
      /*  Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Hello Note is Active")
                .setContentText("Click to Open App.")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build();

        startForeground(1, notification);*/
    }

    public void addChatHead(String calledNumber, String calledName, boolean incomingCall, String tsMilli) {
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incomingCall = incomingCall;
        this.timeStampMilli = tsMilli;
        chatHeadIdentifier++;

        chatHeadManager.addChatHead(String.valueOf(chatHeadIdentifier), false, true);
        chatHeadManager.bringToFront(chatHeadManager.findChatHeadByKey(String.valueOf(chatHeadIdentifier)));
    }

    public void removeChatHead() {
        chatHeadManager.removeChatHead(String.valueOf(chatHeadIdentifier), true);
        chatHeadIdentifier--;
    }

    public void removeAllChatHeads() {
        if (chatHeadIdentifier > 0) {
            chatHeadIdentifier = 0;
            chatHeadManager.removeAllChatHeads(true);
        }

    }

    public void toggleArrangement() {
        if (chatHeadManager.getActiveArrangement() instanceof MinimizedArrangement) {
            chatHeadManager.setArrangement(MaximizedArrangement.class, null);
        } else {
            chatHeadManager.setArrangement(MinimizedArrangement.class, null);
        }
    }

    public void updateBadgeCount() {
        chatHeadManager.reloadDrawable(String.valueOf(chatHeadIdentifier));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        windowManagerContainer.destroy();
    }

    public void minimize() {
        chatHeadManager.setArrangement(MinimizedArrangement.class, null);
    }

    //Phone no validation
    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                //for only indian ic_phone no
                //if(ic_phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    public boolean isCallActive(Context context) {
        AudioManager manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (manager.getMode() == AudioManager.MODE_IN_CALL) {
            return true;
        } else {
            return false;
        }
    }

    public void startRecording() {
        MyApp.recording_in_progress = true;
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

        //voice call to be used
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(pathToSaveRecordedCalls + File.separator + fileNameOfCallRecording + ".amr");

        Log.e(TAG, "path : " + pathToSaveRecordedCalls + fileNameOfCallRecording);

        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();

            mRecorder = null;
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            //voice communication to be used
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile(pathToSaveRecordedCalls + File.separator + fileNameOfCallRecording + ".amr");

            Log.e(TAG, "path : " + pathToSaveRecordedCalls + fileNameOfCallRecording);

            try {
                mRecorder.prepare();
                mRecorder.start();
            } catch (Exception er) {
                er.printStackTrace();

            }
        }


    }

    public void stopRecording() {
        MyApp.recording_in_progress = false;
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;

        file_path = Environment.getExternalStorageDirectory().getPath()
                + File.separator + directoryNameForStoringRecordedCalls
                + File.separator + fileNameOfCallRecording + ".amr";

        final EditText usersRecordName = new EditText(this);
        final TextView text = new TextView(this);
        suggestedRecordName = "Record_" + calledName;
        usersRecordName.setText(suggestedRecordName);
        usersRecordName.setTextColor(getResources().getColor(R.color.black));
        final AlertDialog alertDialog = new AlertDialog.Builder(this, R.style.CustomDialogTheme)
                .setTitle("Recording Name")
                .setCancelable(false)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
               /* if (usersRecordName.getText().toString().length() <= 0) {
                    usersRecordName.setError("Enter recording name");
                } else {
                   *//* CallRecording callRecording = new CallRecording(suggestedRecordName, file_path, calledNumber, calledName, incomingCall, timeStampMilli);
                    callRecording.save();*//*
                }*/
                    }
                })
                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(file_path).getAbsoluteFile();
                        boolean deleted = file.delete();
                        Log.d("shower", "" + file_path + " deleted: " + deleted);
                        dialog.dismiss();
                    }
                })
                .create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alertDialog.setView(usersRecordName, 30, 30, 30, 30);
        usersRecordName.setPadding(10, 0, 0, 0);
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.cancel_color_dark));

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameByUser = usersRecordName.getText().toString().trim();
                if (nameByUser.length() > 0) {
                    CallRecording callRecording = new CallRecording(nameByUser, file_path, calledNumber, calledName, incomingCall, timeStampMilli);
                    callRecording.save();
                    EventBus.getDefault().post(new EventB("5"));
                    alertDialog.dismiss();
                } else {
                    Toast.makeText(PopUpService.this, "Enter Recording Name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        List<CallRecording> callRecordings = CallRecording.listAll(CallRecording.class);
        Collections.reverse(callRecordings);

        for (int i = 0; i < callRecordings.size(); i++) {
            CallRecording callRecording1 = callRecordings.get(i);
            Log.d("logitech1", "" + callRecording1.getRecordName());
            Log.d("logitech2", "" + callRecording1.getRecordPath());
        }
    }

    private void makeDirectory() {
        pathToSaveRecordedCalls = Environment.getExternalStorageDirectory() + File.separator + directoryNameForStoringRecordedCalls;
        File dir = new File(pathToSaveRecordedCalls);

        if (dir.exists() && dir.isDirectory()) {
            //exists
        } else {
            //Not exists
            dir.mkdir();
        }
    }

    public enum CustomPagerEnum {

        CONTACT(R.string.contact, R.layout.contact_add),
        EMAIL(R.string.email, R.layout.email_add),
        ACCOUNT(R.string.account, R.layout.account_add),
        NOTE(R.string.notes, R.layout.notes_add);

        private int mTitleResId;
        private int mLayoutResId;

        CustomPagerEnum(int titleResId, int layoutResId) {
            mTitleResId = titleResId;
            mLayoutResId = layoutResId;
        }

        public int getTitleResId() {
            return mTitleResId;
        }

        public int getLayoutResId() {
            return mLayoutResId;
        }
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private EditText abc;

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }


        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);
            count++;

            if (count == 1) {
                EventBus.getDefault().register(this);
            }
            switch (position) {

                case 0:
                    final EditText EditContactName = (EditText) layout.findViewById(R.id.contact_name_et);
                    final EditText EditContactNo = (EditText) layout.findViewById(R.id.contact_no_et);
                    final TextView contactLabel1 = (TextView) layout.findViewById(R.id.contactLabel1);
                    final TextView contactLabel2 = (TextView) layout.findViewById(R.id.contactLabel2);
                    final RelativeLayout relativeLayout = (RelativeLayout) layout.findViewById(R.id.layout);
                    final ScrollView scrollView = (ScrollView) layout.findViewById(R.id.scroll_layout);
                    if (!MyApp.defaultTheme) {
                        relativeLayout.setBackgroundColor(getResources().getColor(R.color.dark));
                        scrollView.setBackgroundColor(getResources().getColor(R.color.dark));
                        EditContactName.setTextColor(getResources().getColor(R.color.white));
                        EditContactNo.setTextColor(getResources().getColor(R.color.white));
                        contactLabel1.setTextColor(getResources().getColor(R.color.white));
                        contactLabel2.setTextColor(getResources().getColor(R.color.white));
                    }

                    MyApp.editContactNameToSave = EditContactName;
                    MyApp.editContactNumberToSave = EditContactNo;
                    final ImageView contactTick1 = (ImageView) layout.findViewById(R.id.tick1);
                    final ImageView contactTick2 = (ImageView) layout.findViewById(R.id.tick2);


                    TextWatcher textWatcher01 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                            //if (s.toString() != "") {MyApp.toSave = true;}
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {

                            MyApp.contactNumber0 = String.valueOf(s);
                            if (isValidMobile(String.valueOf(s))) {
                                contactTick2.setVisibility(View.VISIBLE);
                            } else {
                                contactTick2.setVisibility(View.INVISIBLE);
                            }


                        }
                    };
                    EditContactNo.addTextChangedListener(textWatcher01);

                    TextWatcher textWatcher02 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName0 = String.valueOf(s);

                            if (String.valueOf(s).length() > 0) {
                                contactTick1.setVisibility(View.VISIBLE);
                            } else {
                                contactTick1.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    EditContactName.addTextChangedListener(textWatcher02);


                    break;

                case 1:
                    final EditText ContactName2 = (EditText) layout.findViewById(R.id.contact_name2_et);
                    final EditText EmailID = (EditText) layout.findViewById(R.id.emailId_et);
                    final ImageView emailTick1 = (ImageView) layout.findViewById(R.id.tick1);
                    final ImageView emailTick2 = (ImageView) layout.findViewById(R.id.tick2);
                    final TextView emailLabel1 = (TextView) layout.findViewById(R.id.contactLabel1);
                    final TextView emailLabel2 = (TextView) layout.findViewById(R.id.contactLabel2);

                    MyApp.editEmailContactNameToSave = ContactName2;
                    MyApp.editEmailAdressToSave = EmailID;


                    if (!MyApp.defaultTheme) {
                        ContactName2.setTextColor(getResources().getColor(R.color.white));
                        EmailID.setTextColor(getResources().getColor(R.color.white));
                        emailLabel1.setTextColor(getResources().getColor(R.color.white));
                        emailLabel2.setTextColor(getResources().getColor(R.color.white));
                    }


                    TextWatcher textWatcher11 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName1 = String.valueOf(s);
                            if (String.valueOf(s).length() > 0) {
                                emailTick1.setVisibility(View.VISIBLE);
                            } else {
                                emailTick1.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    ContactName2.addTextChangedListener(textWatcher11);

                    TextWatcher textWatcher12 = new TextWatcher() {
                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.emailId1 = String.valueOf(s);

                            if (isValidEmail(String.valueOf(s))) {
                                emailTick2.setVisibility(View.VISIBLE);
                            } else {
                                emailTick2.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    EmailID.addTextChangedListener(textWatcher12);


                    break;

                case 2:

                    final TextView accountLabel1 = (TextView) layout.findViewById(R.id.contactLabel1);
                    final TextView accountLabel2 = (TextView) layout.findViewById(R.id.contactLabel2);
                    final TextView accountLabel3 = (TextView) layout.findViewById(R.id.contactLabel3);
                    final EditText ContactName3 = (EditText) layout.findViewById(R.id.contact_name3_et);
                    final EditText AccountNo = (EditText) layout.findViewById(R.id.account_no_et);
                    final EditText Others = (EditText) layout.findViewById(R.id.others_et);
                    final ImageView bankAccountTick1 = (ImageView) layout.findViewById(R.id.tick1);
                    final ImageView bankAccountTick2 = (ImageView) layout.findViewById(R.id.tick2);
                    final ImageView bankAccountTick3 = (ImageView) layout.findViewById(R.id.tick3);

                    MyApp.editBankContactNameToSave = ContactName3;
                    MyApp.editBankAccountNoToSave = AccountNo;
                    MyApp.editBankOthersNoToSave = Others;


                    if (!MyApp.defaultTheme) {
                        accountLabel1.setTextColor(getResources().getColor(R.color.white));
                        accountLabel2.setTextColor(getResources().getColor(R.color.white));
                        accountLabel3.setTextColor(getResources().getColor(R.color.white));
                        ContactName3.setTextColor(getResources().getColor(R.color.white));
                        AccountNo.setTextColor(getResources().getColor(R.color.white));
                        Others.setTextColor(getResources().getColor(R.color.white));
                    }


                    TextWatcher textWatcher21 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName2 = String.valueOf(s);
                            if (String.valueOf(s).length() > 0) {
                                bankAccountTick1.setVisibility(View.VISIBLE);
                            } else {
                                bankAccountTick1.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    ContactName3.addTextChangedListener(textWatcher21);

                    TextWatcher textWatcher22 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.accountNumber2 = String.valueOf(s);
                            if (String.valueOf(s).length() > 0) {
                                bankAccountTick2.setVisibility(View.VISIBLE);
                            } else {
                                bankAccountTick2.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    AccountNo.addTextChangedListener(textWatcher22);

                    TextWatcher textWatcher23 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.ifsc2 = String.valueOf(s);
                            if (String.valueOf(s).length() > 0) {
                                bankAccountTick3.setVisibility(View.VISIBLE);
                            } else {
                                bankAccountTick3.setVisibility(View.INVISIBLE);
                            }
                        }
                    };
                    Others.addTextChangedListener(textWatcher23);
                    break;

                case 3:


                    final EditText Note1 = (EditText) layout.findViewById(R.id.note_et);
                    final TextView noteLabel1 = (TextView) layout.findViewById(R.id.contactLabel2);
                    final ImageView tick = (ImageView) layout.findViewById(R.id.tick2);

                    MyApp.editNoteToSave = Note1;


                    if (!MyApp.defaultTheme) {
                        Note1.setTextColor(getResources().getColor(R.color.white));
                        noteLabel1.setTextColor(getResources().getColor(R.color.white));
                    }


                    TextWatcher textWatcher31 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName3 = String.valueOf(s);
                        }
                    };

                    TextWatcher textWatcher32 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.note3 = String.valueOf(s);

                            if (TextUtils.isEmpty(String.valueOf(s))) {
                                tick.setVisibility(View.INVISIBLE);
                            } else {
                                tick.setVisibility(View.VISIBLE);
                            }
                        }
                    };
                    Note1.addTextChangedListener(textWatcher32);


                    break;
            }

            return layout;
        }


        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View) view);
        }

        @Override
        public int getCount() {
            return CustomPagerEnum.values().length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            return mContext.getString(customPagerEnum.getTitleResId());
        }

        @Subscribe
        public void onEvent(EventB event) {
            // your implementation
            if (event.getMessage().equals("0")) {
                if (TextUtils.isEmpty(MyApp.editContactNameToSave.getText().toString())) {
                    MyApp.editContactNameToSave.setError(mContext.getString(R.string.hint_contact_name));
                    MyApp.editEmailAdressToSave.setError(null);

                } else if (!isValidMobile(MyApp.editContactNumberToSave.getText().toString())) {
                    MyApp.editContactNumberToSave.setError(mContext.getString(R.string.hint_contact_number));
                } else {
                    Contact contact = new Contact(MyApp.editContactNameToSave.getText().toString(), MyApp.editContactNumberToSave.getText().toString(), calledNumber, calledName, incomingCall, timeStampMilli);
                    contact.save();
                    Toast.makeText(mContext, "Successfully Saved", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(new EventB("1"));
                    MyApp.editContactNameToSave.setText(null);
                    MyApp.editContactNumberToSave.setText(null);
                }
            } else if (event.getMessage().equals("1")) {
                if (TextUtils.isEmpty(MyApp.editEmailContactNameToSave.getText().toString())) {
                    MyApp.editEmailContactNameToSave.setError(mContext.getString(R.string.hint_contact_name));
                } else if (!isValidEmail(MyApp.editEmailAdressToSave.getText().toString())) {
                    MyApp.editEmailAdressToSave.setError(mContext.getString(R.string.hint_email));
                } else {
                    Email email = new Email(MyApp.editEmailContactNameToSave.getText().toString(), MyApp.editEmailAdressToSave.getText().toString(), calledNumber, calledName, incomingCall, timeStampMilli);
                    email.save();
                    Toast.makeText(mContext, "Successfully Saved", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(new EventB("2"));
                    MyApp.editEmailContactNameToSave.setText(null);
                    MyApp.editEmailAdressToSave.setText(null);
                }

            } else if (event.getMessage().equals("2")) {
                if (TextUtils.isEmpty(MyApp.editBankContactNameToSave.getText().toString())) {
                    MyApp.editBankContactNameToSave.setError(mContext.getString(R.string.hint_contact_name));
                } else if (TextUtils.isEmpty(MyApp.editBankAccountNoToSave.getText().toString())) {
                    MyApp.editBankAccountNoToSave.setError(mContext.getString(R.string.hint_ac_no));
                } else {
                    BankAccount bankAccount = new BankAccount(MyApp.editBankContactNameToSave.getText().toString(), MyApp.editBankAccountNoToSave.getText().toString(), MyApp.editBankOthersNoToSave.getText().toString(), calledNumber, calledName, incomingCall, timeStampMilli);
                    bankAccount.save();
                    Toast.makeText(mContext, "Successfully Saved", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(new EventB("3"));
                    MyApp.editBankContactNameToSave.setText(null);
                    MyApp.editBankAccountNoToSave.setText(null);
                    MyApp.editBankOthersNoToSave.setText(null);
                }
            } else if (event.getMessage().equals("3")) {
                if (TextUtils.isEmpty(MyApp.editNoteToSave.getText().toString())) {
                    MyApp.editNoteToSave.setError(mContext.getString(R.string.hint_note));
                } else {
                    Note noteN = new Note(MyApp.editNoteToSave.getText().toString(), calledName, calledNumber, timeStampMilli, incomingCall);
                    noteN.save();
                    Toast.makeText(mContext, "Successfully Saved", Toast.LENGTH_LONG).show();
                    EventBus.getDefault().post(new EventB("4"));
                    MyApp.editNoteToSave.setText(null);
                }
            }


        }

    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public PopUpService getService() {
            // Return this instance of LocalService so clients can call public methods
            return PopUpService.this;
        }
    }

}