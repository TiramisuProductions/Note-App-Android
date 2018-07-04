package truelancer.noteapp.noteapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.Fragments.BankAccountFragment;
import truelancer.noteapp.noteapp.Fragments.ContactFragment;
import truelancer.noteapp.noteapp.Fragments.EmailFragment;
import truelancer.noteapp.noteapp.Fragments.NoteFragment;
import truelancer.noteapp.noteapp.Fragments.RecordingFragment;

import static truelancer.noteapp.noteapp.MainActivity.contactText;
import static truelancer.noteapp.noteapp.MainActivity.jsonString;
import static truelancer.noteapp.noteapp.MainActivity.noteSearchAdapter;
import static truelancer.noteapp.noteapp.Utils.retrieveContents;

public class Utils {

    public static boolean isValidMobile(String phone) {
        boolean check;
        if (!Pattern.matches("[a-zA-Z]+", phone)) {
            if (phone.length() < 6 || phone.length() > 13) {
                //for only indian ic_phone no

                check = false;//if(ic_phone.length() != 10) {
            } else {
                check = true;
            }
        } else {
            check = false;
        }
        return check;
    }


    public static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public  static void circleReveal(int viewID, int posFromRight, boolean containsOverflow, final boolean isShow,Activity activity) {
        final View myView = activity.findViewById(viewID);

        int width = myView.getWidth();

        if (posFromRight > 0)
            width -= (posFromRight * activity.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material)) - (activity.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_material) / 2);
        if (containsOverflow)
            width -= activity.getResources().getDimensionPixelSize(R.dimen.abc_action_button_min_width_overflow_material);

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
                    MainActivity.homeTabLayout.setVisibility(View.VISIBLE);
                }
            }
        });

        // make the view visible and start the animation
        if (isShow)
            myView.setVisibility(View.VISIBLE);

        // start the animation
        anim.start();
    }

    public static GoogleSignInClient buildGoogleSignInClient(Activity activity) {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                        .build();
        return GoogleSignIn.getClient(activity, signInOptions);

    }


    public static void createFileInAppFolder(final String jsonString,final  DriveResourceClient mDriveResourceClient,Activity activity) {

        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getAppFolder();

        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();

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

                    .addOnSuccessListener(activity,
                            new OnSuccessListener<DriveFile>() {
                                @Override
                                public void onSuccess(DriveFile driveFile) {
                              //      HideProgressDialog();
                                    Log.d("abc", "Working");

                                }
                            })
                    .addOnFailureListener(activity, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // Log.e(TAG, "Unable to create file", e);
                            //finish();
                            //HideProgressDialog();
                        }
                    });

        }
    }


    public static void getDriveId(final boolean isImport,final DriveResourceClient mDriveResourceClient,final Activity activity) {

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, Config.BACKUP_FILE_NAME))
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(activity,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                if (isImport) {//import
                                    Log.d("countlol", "" + metadataBuffer.getCount());
                                    if (metadataBuffer.getCount() == 0) {
                                        Toast.makeText(activity, "No Backups found", Toast.LENGTH_SHORT).show();
                                        //HideProgressDialog();
                                    } else {//export
                                        MainActivity.driveId = metadataBuffer.get(0).getDriveId();
                                        retrieveContents(MainActivity.driveId.asDriveFile(), null, null, null, null, null, false,mDriveResourceClient,activity);
                                    }
                                } else {//export
                                    if (metadataBuffer.getCount() == 0) {//no backup

                                        List<Contact> contactList = Contact.listAll(Contact.class);
                                        List<Email> emailList = Email.listAll(Email.class);
                                        List<BankAccount> bankAccountList = BankAccount.listAll(BankAccount.class);
                                        List<Note> noteList = Note.listAll(Note.class);
                                        List<truelancer.noteapp.noteapp.Database.Task> taskList = truelancer.noteapp.noteapp.Database.Task.listAll(truelancer.noteapp.noteapp.Database.Task.class);
                                        Modelgson modelgson = new Modelgson(contactList, emailList, bankAccountList, noteList, taskList);
                                        jsonString = new Gson().toJson(modelgson);
                                        Utils.createFileInAppFolder(jsonString,mDriveResourceClient,activity);
                                    } else {//backup exists
                                        List<Contact> contactList = Contact.listAll(Contact.class);
                                        List<Email> emailList = Email.listAll(Email.class);
                                        List<BankAccount> bankAccountList = BankAccount.listAll(BankAccount.class);
                                        List<Note> noteList = Note.listAll(Note.class);
                                        List<truelancer.noteapp.noteapp.Database.Task> taskList = truelancer.noteapp.noteapp.Database.Task.listAll(truelancer.noteapp.noteapp.Database.Task.class);

                                        retrieveContents(metadataBuffer.get(0).getDriveId().asDriveFile(), contactList,
                                                emailList,
                                                bankAccountList,
                                                noteList, taskList, true,mDriveResourceClient,activity);
                                    }
                                }
                                //metadataBuffer.release();

                            }
                        })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                     //   Log.d(TAG1, "get drive id failed : ");
                       // HideProgressDialog();
                    }
                });
    }


    public static void  retrieveContents(DriveFile file, final List<Contact> contactList, final List<Email> emailList, final List<BankAccount> bankAccountList, final List<Note> noteList, final List<truelancer.noteapp.noteapp.Database.Task> taskList, final Boolean backupExists,final DriveResourceClient mDriveResourceClient,final Activity activity) {

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

                               // Log.d(TAG, builder.toString());

                                Modelgson m = new Gson().fromJson(builder.toString(), Modelgson.class);

                            //    Log.d(TAG, "" + m.getClist().size());

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
                                deleteDriveFile(jsonString,mDriveResourceClient,activity);

                            }
                            return mDriveResourceClient.discardContents(contents);
                        }
                    })

                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          //  HideProgressDialog();
                            EventBus.getDefault().post(new EventB("1"));
                            EventBus.getDefault().post(new EventB("2"));
                            EventBus.getDefault().post(new EventB("3"));
                            EventBus.getDefault().post(new EventB("4"));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                           // HideProgressDialog();
                            //Log.e(TAG, "Unable to read contents", e);
                            //finish();
                        }
                    });
        }
        // fromExport=false;
    }

    public static String findNoteIDForTask(String noteTSfromtasklist) {

        List<Note> notes2 = Note.listAll(Note.class);

        for (int i = 0; i < notes2.size(); i++) {
            if (noteTSfromtasklist.equals(notes2.get(i).getTsMilli())) {
                return notes2.get(i).getId().toString();
            }
        }
        return null;
    }

    private static void deleteDriveFile(final String jsonString, final DriveResourceClient mDriveResourceClient, final Activity activity) {
        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, Config.BACKUP_FILE_NAME))
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);
        queryTask
                .addOnSuccessListener(activity,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                Log.d("abc", "" + metadataBuffer.getCount());

                                for (Metadata m : metadataBuffer) {
                                    DriveResource driveResource = m.getDriveId().asDriveResource();
                                    //   Log.i( TAG, "Deleting file: " + sFilename + "  DriveId:(" + m.getDriveId() + ")" );
                                    mDriveResourceClient.delete(driveResource);
                                }
                                Log.d("abc", "success");
                                Utils.createFileInAppFolder(jsonString,mDriveResourceClient,activity);
                            }
                        })
                .addOnFailureListener(activity, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle failure...
                        Log.d("abc", "failed");
                    }
                });
    }


    public static boolean dataAlreadyExists(String tsMilli_fromBackup, String number) {

        if (number.equals("1")) {

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
        } else if (number.equals("2")) {

            List<Email> emails = Email.listAll(Email.class);
            //boolean exists = false;

            for (int i = 0; i < emails.size(); i++) {

                String tsMilli_fromLocalDb = emails.get(i).getTsMilli();

                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    return true;
                }
            }
            return false;
        } else if (number.equals("3")) {

            List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
            //boolean exists = false;

            for (int i = 0; i < bankAccounts.size(); i++) {
                String tsMilli_fromLocalDb = bankAccounts.get(i).getTsMilli();

                if (tsMilli_fromBackup.equals(tsMilli_fromLocalDb)) {
                    return true;
                }
            }
            return false;
        } else if (number.equals("4")) {
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

    public static void executeSearch(String searchWord, Activity activity) {


        if (searchWord.equals("")) {
            //Toast.makeText(getApplicationContext(), "Nothing to serach", Toast.LENGTH_SHORT).show();
            MainActivity.contactText.setVisibility(View.GONE);
            MainActivity.emailText.setVisibility(View.GONE);
            MainActivity.bankText.setVisibility(View.GONE);
            MainActivity.noteText.setVisibility(View.GONE);

            MainActivity.contactFilterList.clear();
            MainActivity.emailFilterList.clear();
            MainActivity.bankFilterList.clear();
            MainActivity.noteFilterList.clear();
        } else {
            MainActivity.contactFilterList.clear();
            MainActivity.emailFilterList.clear();
            MainActivity.bankFilterList.clear();
            MainActivity.noteFilterList.clear();


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
                    MainActivity.contactFilterList.add(contacts.get(i));
                } else if (phoneAll.contains(searchWord)) {
                    MainActivity.contactFilterList.add(contacts.get(i));
                } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.contactFilterList.add(contacts.get(i));
                } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.contactFilterList.add(contacts.get(i));
                } else {

                }

            }

            MainActivity.contactSearchAdapter = new ContactAdapter(activity, MainActivity.contactFilterList);
            MainActivity.contactSearchRecycler.setAdapter(MainActivity.contactSearchAdapter);
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
                    MainActivity.emailFilterList.add(emails.get(i));
                } else if (emailAll.contains(searchWord)) {
                    MainActivity.emailFilterList.add(emails.get(i));
                } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.emailFilterList.add(emails.get(i));
                } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.emailFilterList.add(emails.get(i));
                } else {
                }
            }

            MainActivity.emailSearchAdapter = new EmailAdapter(activity, MainActivity.emailFilterList);
            MainActivity.emailSearchRecycler.setAdapter(MainActivity.emailSearchAdapter);
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
                    MainActivity.bankFilterList.add(bankAccounts.get(i));
                } else if (accNoAll.contains(searchWord)) {
                    MainActivity.bankFilterList.add(bankAccounts.get(i));
                } else if (ifscAll.contains(searchWord)) {
                    MainActivity.bankFilterList.add(bankAccounts.get(i));
                } else if (calledNameAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.bankFilterList.add(bankAccounts.get(i));
                } else if (calledNoAll.contains(searchWord) && !savedFromApp) {
                    MainActivity.bankFilterList.add(bankAccounts.get(i));
                } else {
                }
            }

            MainActivity.bankSearchAdapter = new BankAccountAdapter(activity, MainActivity.bankFilterList);
            MainActivity.bankSearchRecycler.setAdapter(MainActivity.bankSearchAdapter);
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
                    MainActivity.noteFilterList.add(notes.get(i));
                } else if (callednameAll.contains(searchWord)) {
                    MainActivity.noteFilterList.add(notes.get(i));
                } else if (callednoAll.contains(searchWord)) {
                    MainActivity.noteFilterList.add(notes.get(i));
                }
            }

            noteSearchAdapter = new NoteAdapter(activity, MainActivity.noteFilterList);
            MainActivity.noteSearchRecycler.setAdapter(noteSearchAdapter);

            if (MainActivity.contactFilterList.isEmpty()) {
                contactText.setVisibility(View.GONE);
            } else {
                contactText.setVisibility(View.VISIBLE);
                MainActivity.notFoundText.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.emailFilterList.isEmpty()) {
                MainActivity.emailText.setVisibility(View.GONE);
            } else {
                MainActivity.emailText.setVisibility(View.VISIBLE);
                MainActivity.notFoundText.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.bankFilterList.isEmpty()) {
                MainActivity.bankText.setVisibility(View.GONE);
            } else {
                MainActivity.bankText.setVisibility(View.VISIBLE);
                MainActivity.notFoundText.setVisibility(View.INVISIBLE);
            }
            if (MainActivity.noteFilterList.isEmpty()) {
                MainActivity.noteText.setVisibility(View.GONE);
            } else {
                MainActivity.noteText.setVisibility(View.VISIBLE);
                MainActivity.notFoundText.setVisibility(View.INVISIBLE);
            }

            if (MainActivity.contactFilterList.isEmpty()) {
                if (MainActivity.emailFilterList.isEmpty()) {
                    if (MainActivity.bankFilterList.isEmpty()) {
                        if (MainActivity.noteFilterList.isEmpty()) {
                            MainActivity.notFoundText.setVisibility(View.VISIBLE);
                            MainActivity.notFoundImg.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = null;
        if (cm != null) {
            netInfo = cm.getActiveNetworkInfo();
        }
        return netInfo != null && netInfo.isConnected();
    }

    public static void Visibility_no_data(int no, boolean isVisible) {
        switch (no) {
            case 1:
                if (isVisible) {
                    ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                } else {
                    ContactFragment.RContact_no_data.setVisibility(View.GONE);
                }
                break;
            case 2:
                if (isVisible) {
                    EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                } else {
                    EmailFragment.REmail_no_data.setVisibility(View.GONE);
                }
                break;
            case 3:
                if (isVisible) {
                    BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                } else {
                    BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                }

                break;
            case 4:
                if (isVisible) {
                    NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                } else {
                    NoteFragment.RNote_no_data.setVisibility(View.GONE);
                }
                break;
            case 5:
                if (isVisible) {
                    RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                } else {
                    RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
                }
        }
    }
}
