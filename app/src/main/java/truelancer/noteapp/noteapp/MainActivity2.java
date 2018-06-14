package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResourceClient;

import java.util.ArrayList;
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

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

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
    @BindView(R.id.tabs)
    TabLayout homeTabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.searchtoolbar)
    Toolbar searchToolbar;
    @BindView(R.id.contact_search_recycler)
    RecyclerView contactSearchRecycler;
    @BindView(R.id.email_search_recycler)
    RecyclerView emailSearchRecycler;
    @BindView(R.id.bank_search_recycler)
    RecyclerView bankSearchRecycler;
    @BindView(R.id.note_search_recycler)
    RecyclerView noteSearchRecycler;
    @BindView(R.id.contacttxt)
    TextView contactText;
    @BindView(R.id.emailtxt)
    TextView emailText;
    @BindView(R.id.banktxt)
    TextView bankText;
    @BindView(R.id.notetxt)
    TextView noteText;
    @BindView(R.id.not_found_txt)
    TextView notFoundText;
    @BindView(R.id.not_found_img)
    ImageView notFoundImg;
    @BindView(R.id.mainactivity)
    ConstraintLayout mainActivity;
    List<Contact> contactFilterList = new ArrayList<Contact>();
    List<Email> emailFilterList = new ArrayList<Email>();
    List<BankAccount> bankFilterList = new ArrayList<BankAccount>();
    List<Note> noteFilterList = new ArrayList<Note>();
    Menu search_menu;
    MenuItem item_search;
    Boolean isImport = false;
    DriveId driveId = null;
    GoogleSignInClient mGoogleSignInClient;
    String jsonString = "";
    String TAG = "MainActivity";
    SharedPreferences pref;
    private ContactAdapter contactSearchAdapter;
    private EmailAdapter emailSearchAdapter;
    private BankAccountAdapter bankSearchAdapter;
    private NoteAdapter noteSearchAdapter;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    AlertDialog b;
    AlertDialog.Builder progressDialogBuilder;
    InputMethodManager inputMethodManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);//Controls keyboard
        pref = getApplicationContext().getSharedPreferences(getString(R.string.shared_pref), MODE_PRIVATE);
        floatingActionMenu = (FloatingActionMenu) findViewById(R.id.fab_menu);
        floatingActionMenu.setVisibility(View.VISIBLE);
        if (MyApp.defaultTheme) {


        } else {
            Toast.makeText(this, "Default Theme", Toast.LENGTH_LONG).show();
            //searchToolbar.setBackgroundColor(getResources().getColor(R.color.dark));
            mainActivity.setBackgroundColor(getResources().getColor(R.color.dark));
            searchScrollView.setBackgroundColor(getResources().getColor(R.color.dark));
            bankSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            emailSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            noteSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
            bankSearchRecycler.setBackgroundColor(getResources().getColor(R.color.dark));
        }

        //setSearchToolbar();
        /*contactFab.setOnClickListener(this);
        emailFab.setOnClickListener(this);
        bankAccountFab.setOnClickListener(this);
        lastNoteFab.setOnClickListener(this);*/
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false); //To disable back button on toolbar

        homeTabLayout.setupWithViewPager(homeViewPager);

        setupViewPager(homeViewPager);
        homeTabLayout.setupWithViewPager(homeViewPager);

        homeTabLayout.setTabTextColors(R.color.grey, R.color.black);
        setupTabIcons();//icons on tabs

        homeViewPager.setOffscreenPageLimit(1);
    }


    @Override
    public void onClick(View v) {

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BlankFragment(), getString(R.string.contacts));
       /* adapter.addFragment(new EmailFragment(), getString(R.string.emails));
        adapter.addFragment(new BankAccountFragment(), getString(R.string.accounts));
        adapter.addFragment(new NoteFragment(), getString(R.string.notes));
        adapter.addFragment(new RecordingFragment(), getString(R.string.records));*/
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {

        TextView tabContact = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabContact.setText("Contacts");
        tabContact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_contact, 0, 0);
        homeTabLayout.getTabAt(0).setCustomView(tabContact);

        /*TextView tabemail = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
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
        homeTabLayout.getTabAt(4).setCustomView(tabRecordings);*/
    }
}
