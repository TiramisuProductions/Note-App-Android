package truelancer.noteapp.noteapp.Services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;

public class HeadService extends Service {

    private static final String TAG = "HeadService";
    private final IBinder mBinder = new LocalBinder();
    private DefaultChatHeadManager<String> chatHeadManager;
    private int chatHeadIdentifier = 0;
    private WindowManagerContainer windowManagerContainer;
    private Map<String, View> viewCache = new HashMap<>();


    private String calledNumber = "";
    private String calledName = "";
    private boolean incomingCall,isDone;
    private String timeStampMilli = "";


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

        public CustomPagerAdapter(Context context) {
            mContext = context;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {

            CustomPagerEnum customPagerEnum = CustomPagerEnum.values()[position];
            LayoutInflater inflater = LayoutInflater.from(mContext);
            ViewGroup layout = (ViewGroup) inflater.inflate(customPagerEnum.getLayoutResId(), collection, false);
            collection.addView(layout);

            switch (position) {

                case 0:
                    final EditText EditContactName = (EditText) layout.findViewById(R.id.contact_name_et);
                    final EditText EditContactNo = (EditText) layout.findViewById(R.id.contact_no_et);

                    TextWatcher textWatcher01 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                            //if (s.toString() != "") {MyApp.toSave = true;}
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {

                            MyApp.contactNumber0 = String.valueOf(s);

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
                        }


                    };
                    EditContactName.addTextChangedListener(textWatcher02);


                    Button saveContact = (Button) layout.findViewById(R.id.savecontact_btn);

                    //EditContactNo.setText(calledNumber);
                    saveContact.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String contactName = EditContactName.getText().toString();
                            String contactNumber = EditContactNo.getText().toString();
                            Contact contact = new Contact(contactName, contactNumber, calledNumber, calledName, incomingCall, timeStampMilli);


                            if (TextUtils.isEmpty(contactName)) {
                                EditContactName.setError("Enter Name");
                                return;

                            }
                            if (isValidMobile(contactNumber)) {

                                //Toast.makeText(mContext, "Contact Save"  + incomingCall, Toast.LENGTH_SHORT).show();
                                contact.save();

                                //List<Contact> contacts = Contact.listAll(Contact.class);
                                EditContactName.setText("");
                                EditContactNo.setText("");
                                //Toast.makeText(mContext, "Contact Size " + contacts.size() + " " + contactNumber, Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                EditContactNo.setError("not valid");
                                return;

                            }

                        }
                    });
                    break;

                case 1:
                    final EditText ContactName2 = (EditText) layout.findViewById(R.id.contact_name2_et);
                    final EditText EmailID = (EditText) layout.findViewById(R.id.emailId_et);
                    Button saveEmail = (Button) layout.findViewById(R.id.saveemail_btn);

                    TextWatcher textWatcher11 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName1 = String.valueOf(s);
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
                        }
                    };
                    EmailID.addTextChangedListener(textWatcher12);

                    saveEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String contactName = ContactName2.getText().toString();
                            String emailId = EmailID.getText().toString();


                            if (TextUtils.isEmpty(contactName)) {
                                ContactName2.setError("Enter name");
                                return;
                            }
                            if (isValidEmail(emailId)) {

                                Email email = new Email(contactName, emailId, calledNumber, calledName, incomingCall, timeStampMilli);
                                email.save();

                                ContactName2.setText("");
                                EmailID.setText("");

                                //Toast.makeText(mContext, "" + contactName + " " + emailId, Toast.LENGTH_SHORT).show();
                                //Toast.makeText(mContext, "emailsize " + emails + " ", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                EmailID.setError("not valid");
                                return;
                            }
                        }
                    });

                    break;

                case 2:

                    final EditText ContactName3 = (EditText) layout.findViewById(R.id.contact_name3_et);
                    final EditText AccountNo = (EditText) layout.findViewById(R.id.account_no_et);
                    final EditText Others = (EditText) layout.findViewById(R.id.others_et);
                    Button saveAccount = (Button) layout.findViewById(R.id.saveaccount_btn);

                    TextWatcher textWatcher21 = new TextWatcher() {

                        public void afterTextChanged(Editable s) {
                        }

                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        public void onTextChanged(CharSequence s, int start, int before,
                                                  int count) {
                            MyApp.contactName2 = String.valueOf(s);
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
                        }
                    };
                    Others.addTextChangedListener(textWatcher23);

                    saveAccount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String contactName = ContactName3.getText().toString();
                            String accountNumber = AccountNo.getText().toString();
                            String others = Others.getText().toString();
                            if (TextUtils.isEmpty(contactName)) {
                                ContactName3.setError("Enter name");
                                return;
                            }
                            if (TextUtils.isEmpty(accountNumber)) {
                                AccountNo.setError("Enter number");
                                return;
                            }
                            if (TextUtils.isEmpty(others)) {
                                Others.setError("Enter code");
                                return;
                            }

                            BankAccount bankAccount = new BankAccount(contactName, accountNumber, others, calledNumber, calledName, incomingCall, timeStampMilli);
                            bankAccount.save();

                            ContactName3.setText("");
                            AccountNo.setText("");
                            Others.setText("");
                            //List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
                            //Toast.makeText(mContext, "" + contactName + " " + accountNumber + " " + others, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case 3:

                    final EditText ContactName4 = (EditText) layout.findViewById(R.id.contact_name4_et);
                    final EditText Note1 = (EditText) layout.findViewById(R.id.note_et);
                    Button saveNote = (Button) layout.findViewById(R.id.savenote_btn);

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
                        }
                    };
                    Note1.addTextChangedListener(textWatcher32);

                    saveNote.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String note = Note1.getText().toString();


                            if (TextUtils.isEmpty(note)) {
                                Note1.setError("Enter note");
                                return;
                            }

                            Note noteN = new Note( note, calledNumber, calledName,  timeStampMilli,incomingCall,isDone);
                            noteN.save();

                            ContactName4.setText("");
                            Note1.setText("");
                            //List<Note> noteList = Note.listAll(Note.class);

                            //Toast.makeText(mContext, "" + contactName + " " + note + " " + noteList, Toast.LENGTH_SHORT).show();
                        }
                    });

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

    }


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        windowManagerContainer = new WindowManagerContainer(this);
        chatHeadManager = new DefaultChatHeadManager<String>(this, windowManagerContainer);

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

                    ViewPager pager = (ViewPager) view.findViewById(R.id.viewPager);
                    pager.setAdapter(new CustomPagerAdapter(getApplicationContext()));
                    SmartTabLayout viewPagerTab = (SmartTabLayout) view.findViewById(R.id.viewPagerTab);
                    viewPagerTab.setViewPager(pager);

                    Button b1 = (Button)view.findViewById(R.id.goToAppButton);
                    b1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent launchApp = getPackageManager().getLaunchIntentForPackage("truelancer.noteapp.noteapp");
                            startActivity(launchApp);
                            minimize();
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
                return HeadService.this.getChatHeadDrawable(key);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string


        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String tsMilli = "" + startDate.getTime();
        addChatHead("undefined", "undefined", true, "8768768768");
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
        Notification notification = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_template_icon_bg)
                .setContentTitle("Hello Note is Active")
                .setContentText("Click to Open App.")
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .build();

        startForeground(1, notification);
    }

    public void addChatHead(String calledNumber, String calledName, boolean incomingCall, String tsMilli) {
        this.calledNumber = calledNumber;
        this.calledName = calledName;
        this.incomingCall = incomingCall;
        this.timeStampMilli = tsMilli;
        chatHeadIdentifier++;

        Toast.makeText(getApplicationContext(), "inside AddChatHead " + incomingCall
                + "\n" + calledNumber, Toast.LENGTH_SHORT).show();
        // you can even pass a custom object instead of "head0"
        // a sticky chat head (passed as 'true') cannot be closed and will remain when all other chat heads are closed.
        /**
         * In this example a String object (identified by chatHeadIdentifier) is attached to each chat head.
         * You can instead attach any custom object, for e.g a Conversation object to denote each chat head.
         * This object will represent a chat head uniquely and will be passed back in all callbacks.
         */
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

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public HeadService getService() {
            // Return this instance of LocalService so clients can call public methods
            return HeadService.this;
        }
    }

    //Phone no validation
    private boolean isValidMobile(String phone) {
        boolean check = false;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                //for only indian phone no
                //if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }

    //Email validation
    public final static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

}