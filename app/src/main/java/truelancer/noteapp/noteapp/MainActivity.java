package truelancer.noteapp.noteapp;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.rebound.ui.Util;
import com.github.clans.fab.FloatingActionMenu;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Adapters.ViewPagerAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.Fragments.BankAccountFragment;
import truelancer.noteapp.noteapp.Fragments.ContactFragment;
import truelancer.noteapp.noteapp.Fragments.EmailFragment;
import truelancer.noteapp.noteapp.Fragments.NoteFragment;
import truelancer.noteapp.noteapp.Fragments.RecordingFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String TAG1 = "dream";
    static final int RESULT_PICK_CONTACT_C = 4;
    static final int RESULT_PICK_CONTACT_E = 5;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    public static String dataFromAdapter;
    public static FloatingActionMenu floatingActionMenu;

    @BindView(R.id.fab_contact)
    com.github.clans.fab.FloatingActionButton contactFab;
    @BindView(R.id.fab_email)
    com.github.clans.fab.FloatingActionButton emailFab;
    @BindView(R.id.fab_bank_account)
    com.github.clans.fab.FloatingActionButton bankAccountFab;
    @BindView(R.id.fab_last_notes)
    com.github.clans.fab.FloatingActionButton lastNoteFab;
    //@BindView(R.id.fab_menu) FloatingActionMenu floatingActionMenu;
    @BindView(R.id.search_scrollview)
    ScrollView searchScrollView;
    @BindView(R.id.viewpager)
    ViewPager homeViewPager;
    public  static  TabLayout homeTabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.searchtoolbar)
    Toolbar searchToolbar;

     public  static  RecyclerView contactSearchRecycler;

    public  static RecyclerView emailSearchRecycler;

    public  static RecyclerView bankSearchRecycler;

    public  static RecyclerView noteSearchRecycler;

    public  static TextView contactText;

    public  static TextView emailText;

    public  static TextView bankText;

    public  static TextView noteText;

    public  static TextView notFoundText;

    public  static ImageView notFoundImg;
    @BindView(R.id.mainactivity)
    ConstraintLayout mainActivity;
    public static List<Contact> contactFilterList = new ArrayList<Contact>();
    public  static  List<Email> emailFilterList = new ArrayList<Email>();
    public static List<BankAccount> bankFilterList = new ArrayList<BankAccount>();
    public static List<Note> noteFilterList = new ArrayList<Note>();
    Menu search_menu;
    MenuItem item_search;
    Boolean isImport = false;
    public static  DriveId driveId = null;
    GoogleSignInClient mGoogleSignInClient;
    public  static  String jsonString = "";
    String TAG = "MainActivity";
    public static ContactAdapter contactSearchAdapter;
    public static EmailAdapter emailSearchAdapter;
    public static BankAccountAdapter bankSearchAdapter;
    public static NoteAdapter noteSearchAdapter;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    AlertDialog b;
    AlertDialog.Builder progressDialogBuilder;
    InputMethodManager inputMethodManager;

    SharedPreferences.Editor editor;
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), 0); // 0 - for private mode
        editor = pref.edit();
        Log.d("hola",""+pref.getBoolean("key_bubble_persists",true));
        MyApp.defaultTheme = pref.getBoolean(getString(R.string.defaulttheme), true);

        context = this;

        if (pref.getBoolean(getString(R.string.shared_pref_first_time), true)) {

            startActivity(new Intent(this, IntroActivity.class));
        }

        setContentView(R.layout.activity_main);

        contactSearchRecycler = (RecyclerView)findViewById(R.id.contact_search_recycler);
        emailSearchRecycler = (RecyclerView)findViewById(R.id.email_search_recycler);
        bankSearchRecycler  =(RecyclerView)findViewById(R.id.bank_search_recycler);
        noteSearchRecycler = (RecyclerView)findViewById(R.id.note_search_recycler);
        contactText = (TextView)findViewById(R.id.contacttxt);
        emailText = (TextView)findViewById(R.id.emailtxt);
        bankText = (TextView)findViewById(R.id.banktxt);
        noteText = (TextView)findViewById(R.id.notetxt);
        notFoundText =(TextView)findViewById(R.id.not_found_txt);
        notFoundImg = (ImageView) findViewById(R.id.not_found_img);
        homeTabLayout = (TabLayout)findViewById(R.id.tabs);




        ButterKnife.bind(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//Controls keyboard
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        floatingActionMenu.setVisibility(View.VISIBLE);
        if (MyApp.defaultTheme) {
        } else {
            Toast.makeText(this, "Default Theme", Toast.LENGTH_LONG).show();
            searchToolbar.setBackgroundColor(getResources().getColor(R.color.dark));
            mainActivity.setBackgroundColor(getResources().getColor(R.color.dark));
            searchScrollView.setBackgroundColor(getResources().getColor(R.color.dark));
            bankSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            emailSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            noteSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            bankSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
        }

        setSearchToolbar();
        contactFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
        bankAccountFab.setOnClickListener(this);
        lastNoteFab.setOnClickListener(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //To disable back button on toolbar

        homeTabLayout.setupWithViewPager(homeViewPager);

        setupViewPager(homeViewPager);
        homeTabLayout.setupWithViewPager(homeViewPager);

        homeTabLayout.setTabTextColors(R.color.grey, R.color.black);
        setupTabIcons();//icons on tabs

        homeViewPager.setOffscreenPageLimit(5);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ContactFragment(), getString(R.string.contacts));
        adapter.addFragment(new EmailFragment(), getString(R.string.emails));
        adapter.addFragment(new BankAccountFragment(), getString(R.string.accounts));
        adapter.addFragment(new NoteFragment(), getString(R.string.notes));
        adapter.addFragment(new RecordingFragment(), getString(R.string.records));
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {

        TextView tabContact = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabContact.setText("Contacts");
        tabContact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
        homeTabLayout.getTabAt(0).setCustomView(tabContact);

        TextView tabemail = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabemail.setText(" Emails");
        tabemail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_email, 0, 0);
        homeTabLayout.getTabAt(1).setCustomView(tabemail);

        TextView tabBankAccount = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabBankAccount.setText("Bank Accounts");
        tabBankAccount.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_bank, 0, 0);
        homeTabLayout.getTabAt(2).setCustomView(tabBankAccount);

        TextView tabLastNote = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabLastNote.setText("Last Notes");
        tabLastNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0);
        homeTabLayout.getTabAt(3).setCustomView(tabLastNote);

        TextView tabRecordings = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabRecordings.setText("Records");
        tabRecordings.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_note, 0, 0);
        homeTabLayout.getTabAt(4).setCustomView(tabRecordings);
    }

    public void initSearchLayout() {
        if (contactFilterList.isEmpty()) {
            contactText.setVisibility(View.GONE);
        } else {
            contactText.setVisibility(View.VISIBLE);
        }
        if (emailFilterList.isEmpty()) {
            emailText.setVisibility(View.GONE);
        } else {
            emailText.setVisibility(View.VISIBLE);
        }
        if (bankFilterList.isEmpty()) {
            bankText.setVisibility(View.GONE);
        } else {
            bankText.setVisibility(View.VISIBLE);
        }
        if (noteFilterList.isEmpty()) {
            noteText.setVisibility(View.GONE);
        } else {
            noteText.setVisibility(View.VISIBLE);
        }

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        contactSearchRecycler.setLayoutManager(mLayoutManager1);
        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        emailSearchRecycler.setLayoutManager(mLayoutManager2);
        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        bankSearchRecycler.setLayoutManager(mLayoutManager3);
        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(getApplicationContext());
        noteSearchRecycler.setLayoutManager(mLayoutManager4);
    }




    //Searching
    public void setSearchToolbar() {
        searchToolbar = (Toolbar) findViewById(R.id.searchtoolbar);
        if (searchToolbar != null) {
            searchToolbar.inflateMenu(R.menu.menu_search);
            search_menu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        Utils.circleReveal(R.id.searchtoolbar, 1, true, false,MainActivity.this);
                    else {
                        searchToolbar.setVisibility(View.GONE);
                        homeTabLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            item_search = search_menu.findItem(R.id.action_filter_search);

            MenuItemCompat.setOnActionExpandListener(item_search, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Utils.circleReveal(R.id.searchtoolbar, 1, true, false,MainActivity.this);
                        homeTabLayout.setVisibility(View.GONE);
                        searchScrollView.setVisibility(View.GONE);

                    } else {
                        searchToolbar.setVisibility(View.GONE);
                    }

                    homeTabLayout.setVisibility(View.GONE);
                    floatingActionMenu.setVisibility(View.VISIBLE);
                    homeViewPager.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new EventB("0"));

                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    // Do something when expanded
                    return true;
                }
            });

            initSearchView();
        } else
            Log.d("toolbar", "setSearchtollbar: NULL");
    }

    public void initSearchView() {
        initSearchLayout();

        Log.d(TAG, "SearchView");
        final SearchView searchView = (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

        // Enable/Disable Submit button in the keyboard

        searchView.setSubmitButtonEnabled(false);

        // Change search close button image

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);

        // set hint and the text colors

        EditText txtSearch = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        txtSearch.setHint("Search..");
        txtSearch.setHintTextColor(Color.DKGRAY);
        txtSearch.setTextColor(getResources().getColor(R.color.colorPrimary));


        // set the cursor

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.search_cursor); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Utils.executeSearch(query,MainActivity.this);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Utils.executeSearch(newText,MainActivity.this);
                return true;
            }


        });
    }





    ////////////////////////////Menu ////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_export:
                if (Utils.isOnline(getApplicationContext())) {
                    Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
                    ShowProgressDialog("Exporting");
                    signIn();
                } else {
                    Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.menu_search:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    Utils.circleReveal(R.id.searchtoolbar, 1, true, true,MainActivity.this);
                else {
                    searchToolbar.setVisibility(View.VISIBLE);
                }
                floatingActionMenu.setVisibility(View.GONE);
                homeViewPager.setVisibility(View.GONE);
                searchScrollView.setVisibility(View.VISIBLE);

                homeTabLayout.setVisibility(View.GONE);
                item_search.expandActionView();
                return true;

            case R.id.menu_import:
                if (Utils.isOnline(getApplicationContext())) {
                    Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
                    ShowProgressDialog("Importing");
                    isImport = true;
                    signIn();
                } else {
                    Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_nightmode:
                if(MyApp.defaultTheme){
                    editor.putBoolean("defaulttheme",false);
                    editor.commit();
                    MyApp.defaultTheme = false;
                }else{

                    MyApp.defaultTheme  = true;
                    editor.putBoolean("defaulttheme",true);
                    editor.commit();
                }

                EventBus.getDefault().post(new EventB("changeUIMode"));





                return true;



            case R.id.settings:
                startActivity(new Intent(this, Settings.class));

                //startActivity(new Intent(MainActivity.this,SettingsActivity.class));

                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    ////////////////////////////Drive////////////////////////////////////////////


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");

                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");

                    mDriveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));

                    mDriveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

                    if (!isImport) {//export

                    } else {//import
                        Utils.getDriveId(isImport,mDriveResourceClient,MainActivity.this);
                        isImport = false;

                    }
                }
                break;

            case RESULT_PICK_CONTACT_C:

                Log.d(TAG, "position : " + dataFromAdapter);

                Cursor mCursor = null;
                int mLookupKeyIndex;
                int mIdIndex;
                String mCurrentLookupKey;
                long mCurrentId;
                Uri mSelectedContactUri;

                if (data != null) {
                    Uri uri = data.getData();
                    mCursor = getContentResolver().query(uri, null, null, null, null);
                    mCursor.moveToFirst();

                    mLookupKeyIndex = mCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);

                    mCurrentLookupKey = mCursor.getString(mLookupKeyIndex);

                    mIdIndex = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
                    mCurrentId = mCursor.getLong(mIdIndex);
                    mSelectedContactUri = ContactsContract.Contacts.getLookupUri(mCurrentId, mCurrentLookupKey);

                    Intent editIntent = new Intent(Intent.ACTION_EDIT);

                    editIntent.setDataAndType(mSelectedContactUri, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    editIntent.putExtra(ContactsContract.Intents.Insert.PHONE, dataFromAdapter);
                    editIntent.putExtra("finishActivityOnSaveCompleted", true);
                    startActivity(editIntent);
                }

                break;
            case RESULT_PICK_CONTACT_E:

                Cursor mCursore = null;
                int mLookupKeyIndexe;
                int mIdIndexe;
                String mCurrentLookupKeye;
                long mCurrentIde;
                Uri mSelectedContactUrie;
                if (data != null) {
                    Uri urie = data.getData();
                    mCursore = getContentResolver().query(urie, null, null, null, null);
                    mCursore.moveToFirst();

                    mLookupKeyIndexe = mCursore.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);

                    mCurrentLookupKeye = mCursore.getString(mLookupKeyIndexe);

                    mIdIndexe = mCursore.getColumnIndex(ContactsContract.Contacts._ID);
                    mCurrentIde = mCursore.getLong(mIdIndexe);
                    mSelectedContactUrie = ContactsContract.Contacts.getLookupUri(mCurrentIde, mCurrentLookupKeye);

                    Intent editIntente = new Intent(Intent.ACTION_EDIT);

                    editIntente.setDataAndType(mSelectedContactUrie, ContactsContract.Contacts.CONTENT_ITEM_TYPE);
                    editIntente.putExtra(ContactsContract.Intents.Insert.EMAIL, dataFromAdapter);
                    editIntente.putExtra("finishActivityOnSaveCompleted", true);
                    startActivity(editIntente);
                }
                break;
        }
    }

    private void signIn() {
        Log.i(TAG, "Start sign in");
        mGoogleSignInClient = Utils.buildGoogleSignInClient(this);
        startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }














    ////////////////////////////OnClick////////////////////////////////////////////
    @Override
    public void onClick(View v) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string


        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final String tsMilli = "" + startDate.getTime();

        switch (v.getId()) {
            //Add Contact Dialog
            case R.id.fab_contact: {
                floatingActionMenu.close(true);
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.add_contact_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//Puts dialog on top of keyboard
                dialog.setCanceledOnTouchOutside(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                final TextInputLayout field1 = (TextInputLayout) dialog.findViewById(R.id.field_layout_1);//Add Dialog Contact Name
                final TextInputLayout field2 = (TextInputLayout) dialog.findViewById(R.id.field_layout_2);//Add Dialog Contact Number
                final ImageView tick1 = (ImageView) dialog.findViewById(R.id.tick1);//Add Dialog Contact Name
                final ImageView tick2 = (ImageView) dialog.findViewById(R.id.tick2);//Add Dialog Contact Number
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                final TextInputEditText contactName = (TextInputEditText) dialog.findViewById(R.id.field_1);//Add Dialog Contact Name
                final TextInputEditText contactNumber = (TextInputEditText) dialog.findViewById(R.id.field_2);//Add Dialog Contact Number
                title.setText("Contact");
                //Watches changes in contactName editText
                final TextWatcher textWatcher01 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick1.setVisibility(View.VISIBLE);
                            field1.setError(null);
                        } else {
                            tick1.setVisibility(View.INVISIBLE);
                        }
                    }
                };

                final TextWatcher textWatcher02 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (Utils.isValidMobile(String.valueOf(s))) {
                            tick2.setVisibility(View.VISIBLE);
                            field2.setError(null);
                        } else {
                            tick2.setVisibility(View.INVISIBLE);
                        }
                    }
                };
                contactName.addTextChangedListener(textWatcher01);
                contactNumber.addTextChangedListener(textWatcher02);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(contactName.getText().toString())) {
                            field1.setError(getString(R.string.hint_contact_name));
                            inputMethodManager.hideSoftInputFromWindow(contactName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);//removes keyboard

                        } else if (TextUtils.isEmpty(contactNumber.getText().toString())) {
                            field2.setError(getString(R.string.hint_contact_number));
                            inputMethodManager.hideSoftInputFromWindow(contactNumber.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);//removes keyboard

                        } else {
                            Contact contact = new Contact(contactName.getText().toString(), contactNumber.getText().toString(), tsMilli, true);
                            contact.save();
                            inputMethodManager.hideSoftInputFromWindow(contactNumber.getWindowToken(), 0);
                            dialog.dismiss();
                            EventBus.getDefault().post(new EventB("1"));
                        }
                    }
                });
                dialog.show();
                break;
            }

            //Add email Dialog
            case R.id.fab_email: {
                // do something for button 2 click
                floatingActionMenu.close(true);
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.add_email_dailog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//Puts dialog on top of keyboard
                dialog.setCanceledOnTouchOutside(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                final TextInputLayout field1 = (TextInputLayout) dialog.findViewById(R.id.field_layout_1);//Add Dialog Contact Name
                final TextInputLayout field2 = (TextInputLayout) dialog.findViewById(R.id.field_layout_2);//Add Dialog Contact email
                final ImageView tick1 = (ImageView) dialog.findViewById(R.id.tick1);//Add Dialog Contact Name
                final ImageView tick2 = (ImageView) dialog.findViewById(R.id.tick2);//Add Dialog Contact email
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                final TextInputEditText contactName = (TextInputEditText) dialog.findViewById(R.id.field_1);//Add Dialog Contact Name
                final TextInputEditText contactEmail = (TextInputEditText) dialog.findViewById(R.id.field_2);//Add Dialog Contact email
                title.setText("Email");
                //Watches change in EditText
                final TextWatcher textWatcher01 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick1.setVisibility(View.VISIBLE);
                            field1.setError(null);
                        } else {
                            tick1.setVisibility(View.INVISIBLE);
                        }

                    }
                };

                final TextWatcher textWatcher02 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (Utils.isValidEmail(String.valueOf(s))) {
                            tick2.setVisibility(View.VISIBLE);
                            field2.setError(null);
                        } else {
                            tick2.setVisibility(View.INVISIBLE);
                        }

                    }
                };
                contactName.addTextChangedListener(textWatcher01);
                contactEmail.addTextChangedListener(textWatcher02);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (TextUtils.isEmpty(contactName.getText().toString())) {
                            field1.setError(getString(R.string.hint_contact_name));
                            inputMethodManager.hideSoftInputFromWindow(contactName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        } else if (!Utils.isValidEmail(contactEmail.getText().toString())) {
                            field2.setError(getString(R.string.error_email_edit_text));
                            inputMethodManager.hideSoftInputFromWindow(contactEmail.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        } else {
                            Email email = new Email(contactName.getText().toString(), contactEmail.getText().toString(), tsMilli, true);
                            email.save();
                            inputMethodManager.hideSoftInputFromWindow(contactEmail.getWindowToken(), 0);
                            dialog.dismiss();

                            EventBus.getDefault().post(new EventB("2"));
                        }
                    }
                });
                dialog.show();
                break;

            }

            //Add bank account dialog
            case R.id.fab_bank_account: {
                // do something for button 2 click
                floatingActionMenu.close(true);
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.add_bank_account_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//Puts dialog on top of keyboard
                dialog.setCanceledOnTouchOutside(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                final TextInputLayout field1 = (TextInputLayout) dialog.findViewById(R.id.field_layout_1);//Add Dialog Contact Name
                final TextInputLayout field2 = (TextInputLayout) dialog.findViewById(R.id.field_layout_2);//Add Dialog Account No
                final TextInputLayout field3 = (TextInputLayout) dialog.findViewById(R.id.field_layout_3);//Add Dialog IFSC
                final ImageView tick1 = (ImageView) dialog.findViewById(R.id.tick1);//Add Dialog Contact Name
                final ImageView tick2 = (ImageView) dialog.findViewById(R.id.tick2);//Add Dialog Account No
                final ImageView tick3 = (ImageView) dialog.findViewById(R.id.tick3);//Add Dialog IFSC
                final TextInputEditText contactName = (TextInputEditText) dialog.findViewById(R.id.field_1);//Add Dialog Contact Name
                final TextInputEditText contactAccountNo = (TextInputEditText) dialog.findViewById(R.id.field_2);//Add Dialog Account No
                final TextInputEditText contactIFSC = (TextInputEditText) dialog.findViewById(R.id.field_3);//Add Dialog IFSC
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                title.setText("Bank Account");
                final TextWatcher textWatcher01 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick1.setVisibility(View.VISIBLE);
                            field1.setError(null);
                        } else {
                            tick1.setVisibility(View.INVISIBLE);
                        }
                    }
                };

                final TextWatcher textWatcher02 = new TextWatcher() {

                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick2.setVisibility(View.VISIBLE);
                            field2.setError(null);
                        } else {
                            tick2.setVisibility(View.INVISIBLE);
                        }
                    }
                };

                final TextWatcher textWatcher03 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick3.setVisibility(View.VISIBLE);
                            field3.setError(null);
                        } else {
                            tick3.setVisibility(View.INVISIBLE);
                        }
                    }
                };
                contactName.addTextChangedListener(textWatcher01);
                contactAccountNo.addTextChangedListener(textWatcher02);
                contactIFSC.addTextChangedListener(textWatcher03);
                dialog.show();

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if (TextUtils.isEmpty(contactName.getText().toString())) {
                            field1.setError(getString(R.string.hint_contact_name));
                            inputMethodManager.hideSoftInputFromWindow(contactName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        } else if (TextUtils.isEmpty(contactAccountNo.getText().toString())) {
                            field2.setError(getString(R.string.hint_ac_no));
                            inputMethodManager.hideSoftInputFromWindow(contactName.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        } else {
                            BankAccount bankAccount = new BankAccount(contactName.getText().toString(), contactAccountNo.getText().toString(), contactIFSC.getText().toString(), tsMilli, true);
                            bankAccount.save();
                            inputMethodManager.hideSoftInputFromWindow(contactIFSC.getWindowToken(), 0);
                            dialog.dismiss();
                            EventBus.getDefault().post(new EventB("3"));
                        }
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                break;
            }

            //add notes Dialog
            case R.id.fab_last_notes: {
                // do something for button 2 click
                floatingActionMenu.close(true);
                final Dialog dialog = new Dialog(this);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.add_contact_dialog);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);//Puts dialog on top of keyboard
                dialog.setCanceledOnTouchOutside(false);
                TextView title = (TextView) dialog.findViewById(R.id.title);
                final TextInputLayout field1 = (TextInputLayout) dialog.findViewById(R.id.field_layout_1);//Add Dialog Note
                TextInputLayout field2 = (TextInputLayout) dialog.findViewById(R.id.field_layout_2);
                final ImageView tick1 = (ImageView) dialog.findViewById(R.id.tick1);//Add Dialog Note
                final ImageView tick2 = (ImageView) dialog.findViewById(R.id.tick2);
                Button save = (Button) dialog.findViewById(R.id.save);
                Button cancel = (Button) dialog.findViewById(R.id.cancel);
                final TextInputEditText noteEditText = (TextInputEditText) dialog.findViewById(R.id.field_1);//Add Dialog Note
                field1.setHint(getString(R.string.hint_note));
                tick2.setVisibility(View.GONE);
                field2.setVisibility(View.GONE);
                title.setText("Notes");

                final TextWatcher textWatcher01 = new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        //if (s.toString() != "") {MyApp.toSave = true;}
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!TextUtils.isEmpty(String.valueOf(s))) {
                            tick1.setVisibility(View.VISIBLE);
                            field1.setError(null);
                        } else {
                            tick1.setVisibility(View.INVISIBLE);
                        }
                    }
                };

                noteEditText.addTextChangedListener(textWatcher01);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (TextUtils.isEmpty(noteEditText.getText().toString())) {
                            field1.setError(getString(R.string.hint_note));
                            inputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
                        } else {
                            Note note = new Note(noteEditText.getText().toString(), tsMilli, true);
                            note.save();
                            inputMethodManager.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);
                            dialog.dismiss();

                            EventBus.getDefault().post(new EventB("4"));
                        }
                    }
                });
                dialog.show();


                break;
            }


            //.... etc
        }

    }



    public void ShowProgressDialog(String progressText1) {
        progressDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        final TextView progressText = (TextView) dialogView.findViewById(R.id.progessText);
        progressText.setText(progressText1 + " ...");
        progressDialogBuilder.setView(dialogView);
        progressDialogBuilder.setCancelable(false);
        b = progressDialogBuilder.create();
        b.show();
    }

    public void HideProgressDialog() {
        b.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            Utils.circleReveal(R.id.toolbar, 1, true, true,MainActivity.this);
        else {
            toolbar.setVisibility(View.VISIBLE);
        }
        floatingActionMenu.setVisibility(View.VISIBLE);
        homeViewPager.setVisibility(View.VISIBLE);
        searchScrollView.setVisibility(View.GONE);

        homeTabLayout.setVisibility(View.VISIBLE);
        item_search.expandActionView();
        super.onBackPressed();
    }
}

