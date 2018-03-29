    package truelancer.noteapp.noteapp;


    import android.animation.Animator;
    import android.animation.AnimatorListenerAdapter;
    import android.app.Activity;
    import android.app.Dialog;
    import android.content.Intent;
    import android.content.pm.PackageManager;
    import android.content.pm.ResolveInfo;
    import android.database.Cursor;
    import android.graphics.Color;
    import android.graphics.drawable.ColorDrawable;
    import android.net.Uri;
    import android.os.Build;
    import android.provider.ContactsContract;
    import android.support.annotation.NonNull;
    import android.support.annotation.RequiresApi;
    import android.support.design.widget.TabLayout;
    import android.support.design.widget.TextInputEditText;
    import android.support.design.widget.TextInputLayout;
    import android.support.v4.app.ActivityCompat;
    import android.support.v4.content.ContextCompat;
    import android.support.v4.view.MenuItemCompat;
    import android.support.v4.view.ViewPager;
    import android.support.v7.app.AppCompatActivity;
    import android.os.Bundle;
    import android.support.v7.widget.SearchView;
    import android.support.v7.widget.Toolbar;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.MenuInflater;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewAnimationUtils;
    import android.widget.ArrayAdapter;
    import android.widget.AutoCompleteTextView;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.Spinner;
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
    import com.google.android.gms.drive.DriveResourceClient;
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
    import java.util.List;

    import butterknife.BindView;
    import butterknife.ButterKnife;
    import truelancer.noteapp.noteapp.Adapters.ViewPagerAdapter;
    import truelancer.noteapp.noteapp.Database.BankAccount;
    import truelancer.noteapp.noteapp.Database.Contact;
    import truelancer.noteapp.noteapp.Database.Email;
    import truelancer.noteapp.noteapp.Database.Note;
    import truelancer.noteapp.noteapp.Fragments.bankAccountFragment;
    import truelancer.noteapp.noteapp.Fragments.ContactFragment;
    import truelancer.noteapp.noteapp.Fragments.emailFragment;
    import truelancer.noteapp.noteapp.Fragments.lastNoteFragment;

    import static android.Manifest.permission.CALL_PHONE;
    import static android.Manifest.permission.CAPTURE_AUDIO_OUTPUT;
    import static android.Manifest.permission.READ_CONTACTS;
    import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
    import static android.Manifest.permission.READ_PHONE_STATE;
    import static android.Manifest.permission.RECORD_AUDIO;
    import static android.Manifest.permission.WRITE_CONTACTS;
    import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

    public class MainActivity extends AppCompatActivity implements View.OnClickListener {
        @BindView(R.id.fab_contact) com.github.clans.fab.FloatingActionButton contactFab;
        @BindView(R.id.fab_email) com.github.clans.fab.FloatingActionButton emailFab;
        @BindView(R.id.fab_bank_account) com.github.clans.fab.FloatingActionButton bankAccountfab;
        @BindView(R.id.fab_last_notes) com.github.clans.fab.FloatingActionButton lastNoteFab;
        @BindView(R.id.toolbar) Toolbar toolbar;
        @BindView(R.id.viewpager) ViewPager homeViewPager;
        @BindView(R.id.tabs) TabLayout homeTabLayout;
        @BindView(R.id.fab_menu) FloatingActionMenu floatingActionMenu;
        @BindView(R.id.searchtoolbar) Toolbar searchToolbar;
        Menu search_menu;
        MenuItem item_search;
        private static final int REQUEST_CODE_SIGN_IN = 0;
        private static final int REQUEST_CODE_CAPTURE_IMAGE = 1;
        private static final int RequestPermissionCode = 3;
        static final int RESULT_PICK_CONTACT_C = 4;
        static final int RESULT_PICK_CONTACT_E = 5;
        Boolean isImport = false;
        public static String dataFromAapter;
        GoogleSignInClient mGoogleSignInClient;
        private DriveClient mDriveClient;
        private DriveResourceClient mDriveResourceClient;
        String jsonString = "";
        String TAG ="MainActivity";


        public void setSearchtollbar()
        {
            searchToolbar = (Toolbar) findViewById(R.id.searchtoolbar);
            if (searchToolbar != null) {
                searchToolbar.inflateMenu(R.menu.menu_search);
                search_menu=searchToolbar.getMenu();

                searchToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            circleReveal(R.id.searchtoolbar,1,true,false);
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
                            circleReveal(R.id.searchtoolbar,1,true,false);
                        }
                        else
                            searchToolbar.setVisibility(View.GONE);
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


        public void initSearchView()
        {
            final SearchView searchView =
                    (SearchView) search_menu.findItem(R.id.action_filter_search).getActionView();

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

                }

            });

        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow)
        {
            final View myView = findViewById(viewID);

            int width=myView.getWidth();

            if(posFromRight>0)
                width-=(posFromRight*getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material))-(getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)/ 2);
            if(containsOverflow)
                width-=getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

            int cx=width;
            int cy=myView.getHeight()/2;

            Animator anim;
            if(isShow)
                anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0,(float)width);
            else
                anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, (float)width, 0);

            anim.setDuration((long)220);

            // make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if(!isShow)
                    {
                        super.onAnimationEnd(animation);
                        myView.setVisibility(View.INVISIBLE);
                        homeTabLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

            // make the view visible and start the animation
            if(isShow)
                myView.setVisibility(View.VISIBLE);

            // start the animation
            anim.start();


        }


        

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
            setSearchtollbar();
            contactFab.setOnClickListener(this);
            emailFab.setOnClickListener(this);
            bankAccountfab.setOnClickListener(this);
            lastNoteFab.setOnClickListener(this);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false); //To disable back button on toolbar

            homeTabLayout.setupWithViewPager(homeViewPager);

            setupViewPager(homeViewPager);
            homeTabLayout.setupWithViewPager(homeViewPager);
            setupTabIcons();//icons on tabs

            homeViewPager.setOffscreenPageLimit(4);



            if (checkPermission()) {
                initiateTabs();
            } else {
                requestPermission();
            }
        }

        private void requestPermission() {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]
                    {
                            READ_CONTACTS,
                            WRITE_CONTACTS,
                            READ_PHONE_STATE,
                            WRITE_EXTERNAL_STORAGE,
                            READ_EXTERNAL_STORAGE,
                            CALL_PHONE,
                            CAPTURE_AUDIO_OUTPUT,
                            RECORD_AUDIO
                    }, RequestPermissionCode);

        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
            switch (requestCode) {

                case RequestPermissionCode:

                    if (grantResults.length > 0) {

                        boolean ReadContactsPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean ReadPhoneStatePermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        boolean WriteExternalPermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                        boolean ReadExternalPermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                        boolean WriteContactPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;

                        if (WriteContactPermission && ReadContactsPermission && ReadPhoneStatePermission && WriteExternalPermission && ReadExternalPermission) {

                            Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();

                        }
                    }

                    break;
            }
        }

        public boolean checkPermission() {

            int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_CONTACTS);
            int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
            int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
            int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
            int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_CONTACTS);

            return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    ForthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                    FifthPermissionResult == PackageManager.PERMISSION_GRANTED;
        }

        public void initiateTabs() {


        }

        private void setupViewPager(ViewPager viewPager) {
            ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
            adapter.addFragment(new ContactFragment(), getString(R.string.contacts));
            adapter.addFragment(new emailFragment(), getString(R.string.emails));
            adapter.addFragment(new bankAccountFragment(), getString(R.string.accounts));
            adapter.addFragment(new lastNoteFragment(), getString(R.string.notes));
            viewPager.setAdapter(adapter);
        }

        private void setupTabIcons() {

            TextView tabContact = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabContact.setText("Contacts");
            tabContact.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contactimage, 0, 0);
            homeTabLayout.getTabAt(0).setCustomView(tabContact);

            TextView tabemail = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabemail.setText(" Emails");
            tabemail.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.emailimage, 0, 0);
            homeTabLayout.getTabAt(1).setCustomView(tabemail);

            TextView tabBankAccount = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabBankAccount.setText("Bank Accounts");
            tabBankAccount.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.bankaccountimage, 0, 0);
            homeTabLayout.getTabAt(2).setCustomView(tabBankAccount);

            TextView tabLastNote = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
            tabLastNote.setText("Last Notes");
            tabLastNote.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.lastnotesimage, 0, 0);
            homeTabLayout.getTabAt(3).setCustomView(tabLastNote);
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

                    List<Contact> contactList = Contact.listAll(Contact.class);
                    List<Email> emailList = Email.listAll(Email.class);
                    List<BankAccount> bankAccountList = BankAccount.listAll(BankAccount.class);
                    List<Note> noteList = Note.listAll(Note.class);

                    Modelgson modelgson = new Modelgson(contactList, emailList, bankAccountList, noteList);
                    jsonString = new Gson().toJson(modelgson);

                    signIn();
                    return true;

                case R.id.menu_search:

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        circleReveal(R.id.searchtoolbar,1,true,true);
                    else
                        searchToolbar.setVisibility(View.VISIBLE);

                    homeTabLayout.setVisibility(View.GONE);
                    item_search.expandActionView();
                    return true;

                case R.id.menu_import:

                    isImport = true;
                    signIn();
                    return true;

                case R.id.menu_email_to_admin:
                    final Dialog dialog = new Dialog(MainActivity.this);
                    String options[] = {getString(R.string.admin_option_1),getString(R.string.admin_option_2)};
                    dialog.setContentView(R.layout.dialog_admin);
                    final Spinner adminSpinner = (Spinner)dialog.findViewById(R.id.admin_chooser);
                    Button buttonSelect = (Button)dialog.findViewById(R.id.btnSelect);
                    ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, options);
                    adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                    adminSpinner.setAdapter(adminSpinnerArrayAdapter);

                    buttonSelect.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(adminSpinner.getSelectedItemPosition()==0){
                                String email[]={getString(R.string.admin_email)};
                                shareToGMail(email,getString(R.string.admin_option_1),getString(R.string.admin_subject_1));
                            }
                            else{
                                String email[]={getString(R.string.admin_email)};
                                shareToGMail(email,getString(R.string.admin_option_2),getString(R.string.admin_subject_2));
                            }
                        }
                    });



                    dialog.show();


                    return  true;

                default:
                    return super.onOptionsItemSelected(item);
            }
        }


        public void shareToGMail(String email[], String subject, String content) {
            Intent emailIntent = new Intent(Intent.ACTION_SEND);
            emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, content);
            final PackageManager pm = MainActivity.this.getPackageManager();
            final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
            ResolveInfo best = null;
            for(final ResolveInfo info : matches)
                if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                    best = info;
            if (best != null)
                emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
            startActivity(emailIntent);
        }

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

                        if (!isImport) {
                            createFileInAppFolder();
                        } else {
                            getDriveId();
                        }

                    }
                    break;
                case REQUEST_CODE_CAPTURE_IMAGE:
                    Log.i(TAG, "capture image request code");

                    if (resultCode == Activity.RESULT_OK) {
                        Log.i(TAG, "Image captured successfully.");

                    }
                    break;

                case RESULT_PICK_CONTACT_C:

                    Log.d(TAG, "position : " + dataFromAapter);

                    Cursor mCursor = null;
                    int mLookupKeyIndex;
                    int mIdIndex;
                    String mCurrentLookupKey;
                    long mCurrentId;
                    Uri mSelectedContactUri;

            if(data!=null){
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
            editIntent.putExtra(ContactsContract.Intents.Insert.PHONE, dataFromAapter);
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
                    if(data!=null) {
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
                        editIntente.putExtra(ContactsContract.Intents.Insert.EMAIL, dataFromAapter);
                        editIntente.putExtra("finishActivityOnSaveCompleted", true);
                        startActivity(editIntente);
                    }
                    break;
            }
        }

        private void signIn() {
            Log.i(TAG, "Start sign in");
            mGoogleSignInClient = buildGoogleSignInClient();
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }

        private GoogleSignInClient buildGoogleSignInClient() {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                            .build();
            return GoogleSignIn.getClient(this, signInOptions);

        }

        private void createFileInAppFolder() {

            final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();

            final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
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

                                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                                }
                            })

                            .addOnSuccessListener(this,
                                    new OnSuccessListener<DriveFile>() {
                                        @Override
                                        public void onSuccess(DriveFile driveFile) {
                                            Log.d(TAG, "Working");
                                        }
                                    })
                            .addOnFailureListener(this, new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e(TAG, "Unable to create file", e);

                                    //finish();
                                }
                            });
                }
            }
        }

        private void getDriveId() {
            Query query = new Query.Builder()
                    .addFilter(Filters.eq(SearchableField.TITLE, Config.BACKUP_FILE_NAME))
                    .build();

            Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
            queryTask
                    .addOnSuccessListener(this,
                            new OnSuccessListener<MetadataBuffer>() {
                                @Override
                                public void onSuccess(MetadataBuffer metadataBuffer) {
                                    Log.d("count", "" + metadataBuffer.getCount());

                                    DriveId driveId = metadataBuffer.get(0).getDriveId();
                                    retrieveContents(driveId.asDriveFile());
                                }
                            })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Handle failure...
                        }
                    });
        }

        private void retrieveContents(DriveFile file) {


            Task<DriveContents> openFileTask =
                    mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);

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


                                    for (int i = 0; i < m.getClist().size(); i++) {
                                        Contact contact = new Contact(m.getClist().get(i).getName(),
                                                m.getClist().get(i).getPhoneno(),
                                                m.getClist().get(i).getCalledNumber(),
                                                m.getClist().get(i).getCalledName(),
                                                m.getClist().get(i).isIncoming(),
                                                m.getClist().get(i).getTsMilli());
                                        contact.save();
                                    }

                                    for (int i = 0; i < m.getElist().size(); i++) {
                                        Email email = new Email(m.getElist().get(i).getName(),
                                                m.getElist().get(i).getEmailId(),
                                                m.getElist().get(i).getCalledNumber(),
                                                m.getElist().get(i).getCalledName(),
                                                m.getElist().get(i).isIncoming(),
                                                m.getElist().get(i).getTsMilli());
                                        email.save();
                                    }

                                    for (int i = 0; i < m.getBlist().size(); i++) {
                                        BankAccount bankAccount = new BankAccount(m.getBlist().get(i).getName(),
                                                m.getBlist().get(i).getAccountNo(),
                                                m.getBlist().get(i).getIfscCode(),
                                                m.getBlist().get(i).getCalledNumber(),
                                                m.getBlist().get(i).getCalledName(),
                                                m.getBlist().get(i).isIncoming(),
                                                m.getBlist().get(i).getTsMilli());
                                        bankAccount.save();
                                    }

                                    for (int i = 0; i < m.getNlist().size(); i++) {

                                        Note note=new Note(
                                                m.getNlist().get(i).getNote(),
                                                m.getNlist().get(i).getCalledName(),
                                                m.getNlist().get(i).getCalledNumber(),
                                                m.getNlist().get(i).getTsMilli(),
                                                m.getNlist().get(i).isIncoming(),
                                                m.getNlist().get(i).isDone()

                                        );
                                        note.save();


                                    }
                                }
                                Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                                return discardTask;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                Log.e(TAG, "Unable to read contents", e);
                                finish();
                            }
                        });
            }
        }


        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case  R.id.fab_contact: {
                    floatingActionMenu.close(true);
                    final Dialog dialog = new Dialog(this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.add_contact_dialog);
                    TextInputLayout field1 = (TextInputLayout)dialog.findViewById(R.id.field_layout_1);
                    TextInputLayout field2 = (TextInputLayout)dialog.findViewById(R.id.field_layout_2);
                    Button save = (Button)dialog.findViewById(R.id.save);
                    Button cancel = (Button)dialog.findViewById(R.id.cancel);
                    final TextInputEditText contactName = (TextInputEditText)dialog.findViewById(R.id.field_1);
                    final TextInputEditText contactNumber= (TextInputEditText)dialog.findViewById(R.id.field_2);
                    field1.setHint(getString(R.string.hint_contact_name));
                    field2.setHint(getString(R.string.hint_contact_number));

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           Contact contact = new Contact(contactName.getText().toString(),contactNumber.getText().toString(),"3243432",true);
                           contact.save();
                           dialog.dismiss();

                            EventBus.getDefault().post(new EventB("1"));
                        }
                    });




                    dialog.show();
                    break;
                }

                case R.id.fab_email: {
                    // do something for button 2 click
                    floatingActionMenu.close(true);
                    final Dialog dialog = new Dialog(this);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.add_contact_dialog);
                    TextInputLayout field1 = (TextInputLayout)dialog.findViewById(R.id.field_layout_1);
                    TextInputLayout field2 = (TextInputLayout)dialog.findViewById(R.id.field_layout_2);
                    Button save = (Button)dialog.findViewById(R.id.save);
                    Button cancel = (Button)dialog.findViewById(R.id.cancel);
                    final TextInputEditText contactName = (TextInputEditText)dialog.findViewById(R.id.field_1);
                    final TextInputEditText contactNumber= (TextInputEditText)dialog.findViewById(R.id.field_2);
                    field1.setHint(getString(R.string.hint__name));
                    field2.setHint(getString(R.string.hint_email));

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                           Email email = new Email(contactName.getText().toString(),contactNumber.getText().toString(),"45354",true);
                           email.save();
                            dialog.dismiss();

                            EventBus.getDefault().post(new EventB("2"));
                        }
                    });




                    dialog.show();



                    break;
                }

                case R.id.fab_bank_account: {
                    // do something for button 2 click



                    break;
                }


                case R.id.fab_last_notes: {
                        // do something for button 2 click
                    break;
                }


                //.... etc
            }

        }
    }

