package truelancer.noteapp.noteapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.support.v4.widget.NestedScrollView;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
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

    com.github.clans.fab.FloatingActionButton contactFab;
    com.github.clans.fab.FloatingActionButton emailFab;
    com.github.clans.fab.FloatingActionButton bankAccountFab;
    com.github.clans.fab.FloatingActionButton lastNoteFab;
    SharedPreferences pref;
    TabLayout homeTabLayout;
    ViewPager homeViewPager;
    InputMethodManager inputMethodManager;
    public static FloatingActionMenu floatingActionMenu;
    TabLayout filterTabs;
    Menu searchMenu;
    MenuItem itemSearch;
    ScrollView searchScrollView;
    TextView contactText,emailText,noteText,bankText;
    List<Contact> contactFilterList = new ArrayList<Contact>();
    List<Email> emailFilterList = new ArrayList<Email>();
    List<BankAccount> bankFilterList = new ArrayList<BankAccount>();
    List<Note> noteFilterList = new ArrayList<Note>();
    RecyclerView contactSearchRecycler,emailSearchRecycler,noteSearchRecycler,bankSearchRecycler;
    TextView notFoundText;
    ImageView notFoundImg;
    static ProgressDialog progressDialog;
    RelativeLayout rootLayout;
    ConstraintLayout searchLayout;
    NestedScrollView nestedScrollView;
    DriveResourceClient driveResourceClient;
    GoogleSignInClient googleSignInClient;
    DriveId driveId = null;
    String jsonString;
    private DriveClient driveClient;
    private String TAG = "MainActivity";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    static final int RESULT_PICK_CONTACT_C = 4;
    static final int RESULT_PICK_CONTACT_E = 5;
    public static String dataFromAdapter;
    Boolean isImport = false;
    Boolean isSearchShowing = false;


    public boolean isFiltertabsShowing_home = false;
    private  Toolbar searchToolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();







    }


    private void init(){
        contactFab = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_contact);
        emailFab = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_email);
        bankAccountFab = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_bank_account);
        lastNoteFab = (com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_last_notes);
        filterTabs = (TabLayout)findViewById(R.id.filterTabs);
        homeTabLayout = (TabLayout)findViewById(R.id.homeTabLayout);
        homeViewPager = (ViewPager)findViewById(R.id.homeViewPager);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        searchToolbar = (Toolbar)findViewById(R.id.searchtoolbar);
        searchScrollView = (ScrollView)findViewById(R.id.searchScrollView);
        contactText = (TextView)findViewById(R.id.contacttxt);
        emailText = (TextView)findViewById(R.id.emailtxt);
        noteText = (TextView)findViewById(R.id.notetxt);
        bankText = (TextView)findViewById(R.id.banktxt);
        contactSearchRecycler = (RecyclerView)findViewById(R.id.contact_search_recycler);
        emailSearchRecycler = (RecyclerView)findViewById(R.id.email_search_recycler);
        noteSearchRecycler = (RecyclerView)findViewById(R.id.note_search_recycler);
        bankSearchRecycler = (RecyclerView)findViewById(R.id.bank_search_recycler);
        emailText = (TextView)findViewById(R.id.emailtxt) ;
        notFoundText = (TextView)findViewById(R.id.not_found_txt);
        notFoundImg = (ImageView)findViewById(R.id.not_found_img);
        progressDialog = new ProgressDialog(this);
        rootLayout = (RelativeLayout)findViewById(R.id.rootLayout);
        searchLayout = (ConstraintLayout)findViewById(R.id.searchLayout);
        nestedScrollView = (NestedScrollView)findViewById(R.id.nestedScrollView);
        searchMenu = searchToolbar.getMenu();





        pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), 0);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        contactFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
        bankAccountFab.setOnClickListener(this);
        lastNoteFab.setOnClickListener(this);
        homeTabLayout.setupWithViewPager(homeViewPager);
        setupViewPager(homeViewPager);
        setupTabIcons();
        setupFilterTabs();
        setSearchToolbar();
        initSearchLayout();
        homeViewPager.setOffscreenPageLimit(5);
        MyApp.nightMode = pref.getBoolean(getString(R.string.key_night_mode), true);
        MyApp.keepBubble = pref.getBoolean(getString(R.string.key_keep_bubble),true);
        Log.d("hello",""+MyApp.nightMode);
        if(MyApp.nightMode){
            rootLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            searchScrollView.setBackgroundColor(getResources().getColor(R.color.dark));
            searchLayout.setBackgroundColor(getResources().getColor(R.color.dark));
            nestedScrollView.setBackgroundColor(getResources().getColor(R.color.dark));
        }

    }


    public static void showProgressDialog(String message){
        progressDialog.setTitle(message);
        progressDialog.show();

    }

    public static void hideProgressDialog(){
       progressDialog.dismiss();

    }




    private void initSearchView(){
        final SearchView searchView = (SearchView) searchMenu.findItem(R.id.action_filter_search).getActionView();
        searchView.setSubmitButtonEnabled(false);
        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setImageResource(R.drawable.ic_close);


        searchView.setSubmitButtonEnabled(false);



        // Change search close button image



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
                callSearch(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                callSearch(newText);
                return true;
            }

            public void callSearch(String query) {
                //Do searching
                Log.i("query", "" + query);
                if (query.equals("")) {
                    //Toast.makeText(getApplicationContext(), "Nothing to serach", Toast.LENGTH_SHORT).show();
                    contactText.setVisibility(View.GONE);
                    emailText.setVisibility(View.GONE);
                    bankText.setVisibility(View.GONE);
                    noteText.setVisibility(View.GONE);

                    contactFilterList.clear();
                    emailFilterList.clear();
                    bankFilterList.clear();
                    noteFilterList.clear();
                } else {
                    contactFilterList.clear();
                    emailFilterList.clear();
                    bankFilterList.clear();
                    noteFilterList.clear();
                    execute(query);
                }

            }
        });

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


    public void execute(String searchWord) {

        searchWord = searchWord.toLowerCase();

        List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);

        for (int i = 0; i < contacts.size(); i++) {
            String contactNameAll = contacts.get(i).getName().toLowerCase();
            String phoneAll = contacts.get(i).getPhoneno().toLowerCase();
            String calledNoAll = "";
            String calledNameAll = "";
            boolean savedFromApp = contacts.get(i).isSavedFromApp();


            if (!savedFromApp) {
                calledNoAll = contacts.get(i).getCalledNumber().toLowerCase();
                calledNameAll = contacts.get(i).getCalledName().toLowerCase();
            }

            if (contactNameAll.contains(searchWord)) {
                contactFilterList.add(contacts.get(i));
            } else if (phoneAll.contains(searchWord)) {
                contactFilterList.add(contacts.get(i));
            } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                contactFilterList.add(contacts.get(i));
            } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                contactFilterList.add(contacts.get(i));
            }
        }

        ContactAdapter contactSearchAdapter = new ContactAdapter(this, contactFilterList);
        contactSearchRecycler.setAdapter(contactSearchAdapter);
        List<Email> emails = Email.listAll(Email.class);
        Collections.reverse(emails);

        for (int i = 0; i < emails.size(); i++) {
            String contactNameAll = emails.get(i).getName().toLowerCase();
            String emailAll = emails.get(i).getEmailId().toLowerCase();
            String calledNoAll = "";
            String calledNameAll = "";
            boolean savedFromApp = emails.get(i).isSavedFromApp();


            if (!savedFromApp) {
                calledNoAll = emails.get(i).getCalledNumber().toLowerCase();
                calledNameAll = emails.get(i).getCalledName().toLowerCase();
            }

            if (contactNameAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else if (emailAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                emailFilterList.add(emails.get(i));
            } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                emailFilterList.add(emails.get(i));
            }
        }

        EmailAdapter emailSearchAdapter = new EmailAdapter(this, emailFilterList);
        emailSearchRecycler.setAdapter(emailSearchAdapter);
        List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
        Collections.reverse(bankAccounts);

        for (int i = 0; i < bankAccounts.size(); i++) {
            String contactNameAll = bankAccounts.get(i).getName().toLowerCase();
            String accNoAll = bankAccounts.get(i).getAccountNo().toLowerCase();
            String ifscAll = bankAccounts.get(i).getIfscCode().toLowerCase();
            String calledNoAll = "";
            String calledNameAll = "";
            boolean savedFromApp = bankAccounts.get(i).isSavedFromApp();

            if (!savedFromApp) {
                calledNoAll = bankAccounts.get(i).getCalledNumber().toLowerCase();
                calledNameAll = bankAccounts.get(i).getCalledName().toLowerCase();
            }

            if (contactNameAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (accNoAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (ifscAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                bankFilterList.add(bankAccounts.get(i));
            }
        }

        BankAccountAdapter bankSearchAdapter = new BankAccountAdapter(this, bankFilterList);
        bankSearchRecycler.setAdapter(bankSearchAdapter);
        List<Note> notes = Note.listAll(Note.class);
        Collections.reverse(notes);

        for (int i = 0; i < notes.size(); i++) {
            String noteAll = notes.get(i).getNote().toLowerCase();
            boolean savedFromApp = notes.get(i).isSavedFromApp();
            String callednoAll = "";
            String callednameAll = "";
            if (!savedFromApp) {
                callednoAll = notes.get(i).getCalledNumber().toLowerCase();
                callednameAll = notes.get(i).getCalledName().toLowerCase();
            }

            if (noteAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            } else if (callednameAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            } else if (callednoAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            }
        }

        NoteAdapter noteSearchAdapter = new NoteAdapter(this, noteFilterList);
        noteSearchRecycler.setAdapter(noteSearchAdapter);

        if (contactFilterList.isEmpty()) {
            contactText.setVisibility(View.GONE);
        } else {
            contactText.setVisibility(View.VISIBLE);
            notFoundText.setVisibility(View.INVISIBLE);
        }
        if (emailFilterList.isEmpty()) {
            emailText.setVisibility(View.GONE);
        } else {
            emailText.setVisibility(View.VISIBLE);
            notFoundText.setVisibility(View.INVISIBLE);
        }
        if (bankFilterList.isEmpty()) {
            bankText.setVisibility(View.GONE);
        } else {
            bankText.setVisibility(View.VISIBLE);
            notFoundText.setVisibility(View.INVISIBLE);
        }
        if (noteFilterList.isEmpty()) {
            noteText.setVisibility(View.GONE);
        } else {
            noteText.setVisibility(View.VISIBLE);
            notFoundText.setVisibility(View.INVISIBLE);
        }

        if (contactFilterList.isEmpty()) {
            if (emailFilterList.isEmpty()) {
                if (bankFilterList.isEmpty()) {
                    if (noteFilterList.isEmpty()) {
                        notFoundText.setVisibility(View.VISIBLE);
                        notFoundImg.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private void setSearchToolbar(){
        searchToolbar = (Toolbar) findViewById(R.id.searchtoolbar);
        if (searchToolbar != null) {
            Log.d("yay","yay");
            searchToolbar.inflateMenu(R.menu.menu_search);
            searchMenu = searchToolbar.getMenu();

            searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("wow","hello");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                     circleReveal(R.id.searchtoolbar, 1, true, false);
                    else {
                        searchToolbar.setVisibility(View.GONE);
                        homeTabLayout.setVisibility(View.VISIBLE);
                    }
                    isSearchShowing = false;
                }
            });


            itemSearch = searchMenu.findItem(R.id.action_filter_search);



            MenuItemCompat.setOnActionExpandListener(itemSearch, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    Log.d("wow","wow");
                    // Do something when collapsed
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        circleReveal(R.id.searchtoolbar, 1, true, false);
                        homeTabLayout.setVisibility(View.GONE);

                    //    searchScrollView.setVisibility(View.GONE);

                    } else {
                        searchToolbar.setVisibility(View.GONE);
                    }
                    isSearchShowing = true;
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


    }

        initSearchView();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow) {
        final View myView = findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))
                    - (getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

        int cx = width;
        int cy = myView.getHeight() / 2;

        Animator anim;
        if (isShow)
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, (float) width);
        else
            anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float) width, 0);

        anim.setDuration((long) 220);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!isShow) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                    homeTabLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
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


    private void setupFilterTabs(){
        filterTabs.addTab(filterTabs.newTab().setText("All"));
        filterTabs.addTab(filterTabs.newTab().setText("Incoming"));
        filterTabs.addTab(filterTabs.newTab().setText("Outgoing"));
        filterTabs.addTab(filterTabs.newTab().setText("Saved from app"));
        filterTabs.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor("#424242"));
        filterTabs.setSelectedTabIndicatorHeight(0);

        filterTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0://All
                        MyApp.isIncomingFilterHighlighted = false;
                        MyApp.isOutgoingFilterHighlighted = false;
                        MyApp.isSavedFromAppFilterHighlighted = false;
                        setupViewPager(homeViewPager);
                        setupTabIcons();
                        Log.d("wood", "All");
                        break;

                    case 1://Incoming
                        MyApp.isIncomingFilterHighlighted = true;
                        MyApp.isOutgoingFilterHighlighted = false;
                        MyApp.isSavedFromAppFilterHighlighted = false;
                        AsyncTaskFilterModel asyncTaskFilterModel1 = new AsyncTaskFilterModel(MainActivity.this, 1);
                        asyncTaskFilterModel1.execute();
                        Log.d("wood", "Incoming");
                        break;

                    case 2://Outgoing
                        MyApp.isOutgoingFilterHighlighted = true;
                        MyApp.isIncomingFilterHighlighted = false;
                        MyApp.isSavedFromAppFilterHighlighted = false;
                        AsyncTaskFilterModel asyncTaskFilterModel2 = new AsyncTaskFilterModel(MainActivity.this, 2);
                        asyncTaskFilterModel2.execute();
                        Log.d("wood", "Outgoing");
                        break;

                    case 3://Saved_from_app
                        MyApp.isSavedFromAppFilterHighlighted=true;
                        MyApp.isIncomingFilterHighlighted = false;
                        MyApp.isOutgoingFilterHighlighted = false;
                        AsyncTaskFilterModel asyncTaskFilterModel3 = new AsyncTaskFilterModel(MainActivity.this, 3);
                        asyncTaskFilterModel3.execute();
                        Log.d("wood", "Saved from App");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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

                            homeViewPager.setCurrentItem(0,true);

                            if (MyApp.isFilterTabsShowing){

                                EventBus.getDefault().post(new EventB("6"));

                            }else {

                                EventBus.getDefault().post(new EventB("1"));
                            }
                            //EventBus.getDefault().post(new EventB("1"));
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

                            homeViewPager.setCurrentItem(1,true);
                            if (MyApp.isFilterTabsShowing){

                                EventBus.getDefault().post(new EventB("7"));

                            }else {

                                EventBus.getDefault().post(new EventB("2"));
                            }

                            //EventBus.getDefault().post(new EventB("2"));
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

                            homeViewPager.setCurrentItem(2,true);
                            if (MyApp.isFilterTabsShowing){
                                Log.d("wood", "filter tabs is showing");
                                EventBus.getDefault().post(new EventB("8"));

                            }else {
                                Log.d("wood", "filter tabs not showing");
                                EventBus.getDefault().post(new EventB("3"));
                            }
                            // EventBus.getDefault().post(new EventB("3"));
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

                            homeViewPager.setCurrentItem(4,true);
                            if (MyApp.isFilterTabsShowing){
                                Log.d("wood", "filter tabs is showing");
                                EventBus.getDefault().post(new EventB("9"));

                            }else {
                                Log.d("wood", "filter tabs not showing");
                                EventBus.getDefault().post(new EventB("4"));
                            }
                            //EventBus.getDefault().post(new EventB("4"));
                        }
                    }
                });
                dialog.show();

                break;
            }
        }

    }


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
                    showProgressDialog("Exporting");
                    initGoogleLogin();
                } else {
                    Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.menu_import:
                if (Utils.isOnline(getApplicationContext())) {
                    Toast.makeText(this, "Internet Available", Toast.LENGTH_SHORT).show();
                    showProgressDialog("Importing");
                    isImport = true;
                    initGoogleLogin();
                } else {
                    Toast.makeText(this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                }
                return true;

            case R.id.menu_search:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    homeTabLayout.setVisibility(View.GONE);
                    filterTabs.setVisibility(View.GONE);
                    circleReveal(R.id.searchtoolbar, 1, true, true);
                }
                else {
                    searchToolbar.setVisibility(View.VISIBLE);

                }
                isSearchShowing = true;
                floatingActionMenu.setVisibility(View.GONE);
                homeViewPager.setVisibility(View.GONE);
                searchScrollView.setVisibility(View.VISIBLE);

                homeTabLayout.setVisibility(View.GONE);
                itemSearch.expandActionView();

                return true;



            case R.id.filter:

                if (isFiltertabsShowing_home) {
                    filterTabs.setVisibility(View.GONE);
                    setupViewPager(homeViewPager);
                    setupTabIcons();
                    MyApp.isIncomingFilterHighlighted = false;
                    MyApp.isOutgoingFilterHighlighted = false;
                    MyApp.isSavedFromAppFilterHighlighted = false;
                    MyApp.isFilterTabsShowing = false;
                    Log.d("wood", "isFiltertabs showing if"+MyApp.isFilterTabsShowing);
                } else {
                    filterTabs.setVisibility(View.VISIBLE);
                    MyApp.isFilterTabsShowing = true;
                    Log.d("wood", "isFiltertabs showing else"+MyApp.isFilterTabsShowing);
                }
                isFiltertabsShowing_home = !isFiltertabsShowing_home;

                return true;



            case R.id.settings:
                //startActivity(new Intent(this, Settings.class));

                startActivity(new Intent(MainActivity.this, Settings.class));

                overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

            default:
                return super.onOptionsItemSelected(item);
        }
    }




    //Google Drive Code Starts here

    private void initGoogleLogin() {

        googleSignInClient = buildGoogleSignInClient();
        startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }


    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                Log.i(TAG, "Sign in request code");

                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "Signed in successfully.");

                    driveClient = Drive.getDriveClient(this, GoogleSignIn.getLastSignedInAccount(this));

                    driveResourceClient =
                            Drive.getDriveResourceClient(this, GoogleSignIn.getLastSignedInAccount(this));

                    if (!isImport) {//export
                        getDriveId();
                    } else {//import
                        getDriveId();
                    }
                }
                break;

            case RESULT_PICK_CONTACT_C:



                Cursor mCursor = null;
                int mLookupKeyIndex;
                int mIdIndex;
                String mCurrentLookupKey;
                long mCurrentId;
                Uri mSelectedContactUri;

                if (data != null) {
                    Uri uri = data.getData();
                    mCursor = getContentResolver().query(uri, null, null, null, null);
                    if (mCursor != null) {
                        mCursor.moveToFirst();
                    }

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


    private void getDriveId() {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, Config.BACKUP_FILE_NAME))
                .build();

        Task<MetadataBuffer> queryTask = driveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                if (isImport) {//import
                                    Log.d("countlol", "" + metadataBuffer.getCount());
                                    if (metadataBuffer.getCount() == 0) {
                                        Toast.makeText(MainActivity.this, "No Backups found", Toast.LENGTH_SHORT).show();
                                        hideProgressDialog();
                                    } else {//export
                                        driveId = metadataBuffer.get(0).getDriveId();
                                        retrieveContents(driveId.asDriveFile(), null, null, null, null, null, false);
                                    }
                                    isImport = false;
                                } else {//export
                                    if (metadataBuffer.getCount() == 0) {//no backup

                                        List<Contact> contactList = Contact.listAll(Contact.class);
                                        List<Email> emailList = Email.listAll(Email.class);
                                        List<BankAccount> bankAccountList = BankAccount.listAll(BankAccount.class);
                                        List<Note> noteList = Note.listAll(Note.class);
                                        List<truelancer.noteapp.noteapp.Database.Task> taskList = truelancer.noteapp.noteapp.Database.Task.listAll(truelancer.noteapp.noteapp.Database.Task.class);
                                        Modelgson modelgson = new Modelgson(contactList, emailList, bankAccountList, noteList, taskList);
                                        jsonString = new Gson().toJson(modelgson);
                                        createFileInAppFolder(jsonString);
                                    } else {//backup exists
                                        List<Contact> contactList = Contact.listAll(Contact.class);
                                        List<Email> emailList = Email.listAll(Email.class);
                                        List<BankAccount> bankAccountList = BankAccount.listAll(BankAccount.class);
                                        List<Note> noteList = Note.listAll(Note.class);
                                        List<truelancer.noteapp.noteapp.Database.Task> taskList = truelancer.noteapp.noteapp.Database.Task.listAll(truelancer.noteapp.noteapp.Database.Task.class);

                                        retrieveContents(metadataBuffer.get(0).getDriveId().asDriveFile(), contactList,
                                                emailList,
                                                bankAccountList,
                                                noteList, taskList, true);
                                    }
                                }
                                //metadataBuffer.release();

                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.d(TAG, "get drive id failed : ");
                        hideProgressDialog();
                    }
                });
    }

    private void createFileInAppFolder(final String jsonString) {

        final Task<DriveFolder> appFolderTask = driveResourceClient.getAppFolder();

        final Task<DriveContents> createContentsTask = driveResourceClient.createContents();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {

            Tasks.whenAll(appFolderTask, createContentsTask)
                    .continueWithTask(new Continuation<Void, Task<DriveFile>>() {
                        @Override
                        public Task<DriveFile> then(@NonNull Task<Void> task) throws Exception {
                            DriveFolder parent = appFolderTask.getResult();
                            DriveContents contents = createContentsTask.getResult();

                            OutputStream outputStream = contents.getOutputStream();

                            try (Writer writer = new OutputStreamWriter(outputStream)) {
                                writer.write(jsonString);
                            }

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setTitle(Config.BACKUP_FILE_NAME)
                                    .setMimeType("json/application")
                                    .setStarred(true)
                                    .build();

                            return driveResourceClient.createFile(parent, changeSet, contents);
                        }
                    })

                    .addOnSuccessListener(this,
                            new OnSuccessListener<DriveFile>() {
                                @Override
                                public void onSuccess(DriveFile driveFile) {
                                    hideProgressDialog();
                                    Log.d("abc", "Working");

                                }
                            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Unable to create file", e);
                            //finish();
                            hideProgressDialog();
                        }
                    });

        }
    }

    private void retrieveContents(DriveFile file, final List<Contact> contactList, final List<Email> emailList, final List<BankAccount> bankAccountList, final List<Note> noteList, final List<truelancer.noteapp.noteapp.Database.Task> taskList, final Boolean backupExists) {

        Task<DriveContents> openFileTask =
                driveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            openFileTask
                    .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                        @Override
                        public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                            DriveContents contents = task.getResult();

                            try (BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(contents.getInputStream()))) {
                                StringBuilder builder = new StringBuilder();
                                String line;
                                while ((line = reader.readLine()) != null) {
                                    builder.append(line).append("\n");
                                }

                                Log.d(TAG, builder.toString());

                                Modelgson m = new Gson().fromJson(builder.toString(), Modelgson.class);

                                Log.d(TAG, "" + m.getClist().size());

                                Log.d("abc", "contact list before add : " + contactList);
                                for (int i = 0; i < m.getClist().size(); i++) {
                                    if (backupExists) {
                                        contactList.add(m.clist.get(i));
                                        Log.d("abc", "contact list after add : " + contactList);

                                    } else {
                                        Contact contact = new Contact(m.getClist().get(i).getName(),
                                                m.getClist().get(i).getPhoneno(),
                                                m.getClist().get(i).getCalledNumber(),
                                                m.getClist().get(i).getCalledName(),
                                                m.getClist().get(i).isIncoming(),
                                                m.getClist().get(i).getTsMilli());
                                        if (!dataAlreadyExists(m.getClist().get(i).getTsMilli(), "1")) {
                                            contact.save();
                                        }
                                    }
                                }

                                for (int i = 0; i < m.getElist().size(); i++) {
                                    if (backupExists) {
                                        emailList.add(m.elist.get(i));
                                    } else {
                                        Email email = new Email(m.getElist().get(i).getName(),
                                                m.getElist().get(i).getEmailId(),
                                                m.getElist().get(i).getCalledNumber(),
                                                m.getElist().get(i).getCalledName(),
                                                m.getElist().get(i).isIncoming(),
                                                m.getElist().get(i).getTsMilli());
                                        if (!dataAlreadyExists(m.getElist().get(i).getTsMilli(), "2")) {
                                            email.save();
                                        }
                                    }
                                }

                                for (int i = 0; i < m.getBlist().size(); i++) {
                                    if (backupExists) {
                                        bankAccountList.add(m.blist.get(i));
                                    } else {
                                        BankAccount bankAccount = new BankAccount(m.getBlist().get(i).getName(),
                                                m.getBlist().get(i).getAccountNo(),
                                                m.getBlist().get(i).getIfscCode(),
                                                m.getBlist().get(i).getCalledNumber(),
                                                m.getBlist().get(i).getCalledName(),
                                                m.getBlist().get(i).isIncoming(),
                                                m.getBlist().get(i).getTsMilli());
                                        if (!dataAlreadyExists(m.getBlist().get(i).getTsMilli(), "3")) {
                                            bankAccount.save();
                                        }
                                    }

                                    //bankAccount.save();
                                }

                                for (int i = 0; i < m.getNlist().size(); i++) {
                                    if (backupExists) {
                                        noteList.add(m.nlist.get(i));
                                    } else {
                                        Note note = new Note(
                                                m.getNlist().get(i).getNote(),
                                                m.getNlist().get(i).getCalledName(),
                                                m.getNlist().get(i).getCalledNumber(),
                                                m.getNlist().get(i).getTsMilli(),
                                                m.getNlist().get(i).isIncoming()

                                        );
                                        if (!dataAlreadyExists(m.getNlist().get(i).getTsMilli(), "4")) {
                                            note.save();
                                        }
                                    }
                                }

                                for (int i = 0; i < m.gettList().size(); i++) {
                                    if (backupExists) {//used during export
                                        taskList.add(m.tList.get(i));
                                    } else {
                                        String noteID = findNoteIDForTask(m.gettList().get(i).getNoteTimeStamp());
                                        truelancer.noteapp.noteapp.Database.Task task1 =
                                                new truelancer.noteapp.noteapp.Database.Task(
                                                        m.gettList().get(i).getTaskText(),
                                                        noteID,
                                                        m.gettList().get(i).isDone,
                                                        m.gettList().get(i).getNoteTimeStamp()
                                                );
                                        task1.save();

                                       /* truelancer.noteapp.noteapp.Database.Task task1 =
                                                new truelancer.noteapp.noteapp.Database.Task(
                                                m.gettList().get(i).getTaskText(),
                                                m.gettList().get(i).getNoteId(),
                                                m.gettList().get(i).isDone,
                                                m.gettList().get(i).getNoteTimeStamp()
                                        );
                                        task1.save();*/
                                    }
                                }
                            }
                            if (backupExists) {
                                Modelgson modelgson = new Modelgson(contactList, emailList, bankAccountList, noteList, taskList);
                                jsonString = new Gson().toJson(modelgson);
                                deleteDriveFile(jsonString);

                            }
                            return driveResourceClient.discardContents(contents);
                        }
                    })

                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            hideProgressDialog();
                            EventBus.getDefault().post(new EventB("1"));
                            EventBus.getDefault().post(new EventB("2"));
                            EventBus.getDefault().post(new EventB("3"));
                            EventBus.getDefault().post(new EventB("4"));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            hideProgressDialog();
                            Log.e(TAG, "Unable to read contents", e);
                            finish();
                        }
                    });
        }
        // fromExport=false;
    }

    private void deleteDriveFile(final String jsonString) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, Config.BACKUP_FILE_NAME))
                .build();

        Task<MetadataBuffer> queryTask = driveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                Log.d("abc", "" + metadataBuffer.getCount());

                                for (Metadata m : metadataBuffer) {
                                    DriveResource driveResource = m.getDriveId().asDriveResource();
                                    //   Log.i( TAG, "Deleting file: " + sFilename + "  DriveId:(" + m.getDriveId() + ")" );
                                    driveResourceClient.delete(driveResource);
                                }
                                Log.d("abc", "success");
                                createFileInAppFolder(jsonString);
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure...
                        Log.d("abc", "failed");
                    }
                });
    }

    public boolean dataAlreadyExists(String tsMilli_fromBackup, String number) {

        switch (number) {
            case "1":

                List<Contact> contacts = Contact.listAll(Contact.class);
                // boolean exists = false;

                Log.d("cricket", "then: " + tsMilli_fromBackup);

                for (int i = 0; i < contacts.size(); i++) {

                    String tsMilli_fromLocalDb = contacts.get(i).getTsMilli();
                    Log.d("cricket2", "then: " + tsMilli_fromLocalDb);

                    if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                        Log.d("cricket3", "then: " + true);
                        return true;
                    }
                }
                //Log.d(TAG + "baaghi", "" + exists);
                return false;
            case "2":

                List<Email> emails = Email.listAll(Email.class);
                //boolean exists = false;

                for (int i = 0; i < emails.size(); i++) {

                    String tsMilli_fromLocalDb = emails.get(i).getTsMilli();

                    if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                        return true;
                    }
                }
                return false;
            case "3":

                List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
                //boolean exists = false;

                for (int i = 0; i < bankAccounts.size(); i++) {
                    String tsMilli_fromLocalDb = bankAccounts.get(i).getTsMilli();

                    if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                        return true;
                    }
                }
                return false;
            case "4":
                List<Note> notes = Note.listAll(Note.class);
                //boolean exists = false;

                for (int i = 0; i < notes.size(); i++) {
                    String tsMilli_fromLocalDb = notes.get(i).getTsMilli();
                    if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                        return true;
                    }
                }
                return false;
        }
        return false;
    }
    private String findNoteIDForTask(String noteTSfromtasklist) {

        List<Note> notes2 = Note.listAll(Note.class);

        for (int i = 0; i < notes2.size(); i++) {
            if (noteTSfromtasklist.equals(notes2.get(i).getTsMilli())) {
                return notes2.get(i).getId().toString();
            }
        }
        return null;
    }


    @Override
    public void onBackPressed() {
        Log.d("pressed back","pressed");

        if(isSearchShowing){

            Log.d("it reaches here","it reavhes here");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                circleReveal(R.id.searchtoolbar, 1, true, false);
            else {
                searchToolbar.setVisibility(View.GONE);
                homeTabLayout.setVisibility(View.VISIBLE);
            }

            homeTabLayout.setVisibility(View.GONE);
            floatingActionMenu.setVisibility(View.VISIBLE);
            homeViewPager.setVisibility(View.VISIBLE);
            EventBus.getDefault().post(new EventB("0"));
            isSearchShowing = false;
        }else{
            super.onBackPressed();
        }

       //
    }
}
