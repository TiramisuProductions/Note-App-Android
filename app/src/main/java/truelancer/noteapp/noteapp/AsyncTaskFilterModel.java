package truelancer.noteapp.noteapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.util.Collections;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Adapters.RecordingAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.CallRecording;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.Fragments.BankAccountFragment;
import truelancer.noteapp.noteapp.Fragments.ContactFragment;
import truelancer.noteapp.noteapp.Fragments.EmailFragment;
import truelancer.noteapp.noteapp.Fragments.NoteFragment;
import truelancer.noteapp.noteapp.Fragments.RecordingFragment;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class AsyncTaskFilterModel extends AsyncTask<String, String, String> {

    public AsyncTaskFilterModel() {
        super();
    }

    List<Contact> contacts = null;
    List<Email> emails = null;
    List<BankAccount> bankAccounts = null;
    List<Note> notes = null;
    List<CallRecording> callRecordings = null;

    public Activity context;
    public int no;

    AsyncTaskFilterModel(Activity activity, int no) {
        this.context = activity;
        this.no = no;
    }

    @Override
    protected String doInBackground(String... strings) {

        switch (no) {
            case 1://Incoming
                contacts = Contact.findWithQuery(Contact.class, "Select * from Contact where incoming = ? and is_saved_from_app = ?", "1", "0");
                emails = Email.findWithQuery(Email.class, "Select * from Email where incoming = ? and is_saved_from_app = ?", "1", "0");
                bankAccounts = BankAccount.findWithQuery(BankAccount.class, "Select * from Bank_account where incoming = ? and is_saved_from_app = ?", "1", "0");
                notes = Note.findWithQuery(Note.class, "Select * from Note where incoming = ? and is_saved_from_app = ?", "1", "0");
                callRecordings = CallRecording.findWithQuery(CallRecording.class, "Select * from Call_recording where incoming = ?", "1");

                Collections.reverse(contacts);
                Collections.reverse(emails);
                Collections.reverse(bankAccounts);
                Collections.reverse(notes);
                Collections.reverse(callRecordings);

                if (contacts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (emails.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (bankAccounts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (notes.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (callRecordings.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                break;

            case 2://Outgoing
                contacts = Contact.findWithQuery(Contact.class, "Select * from Contact where incoming = ? and is_saved_from_app = ?", "0", "0");
                emails = Email.findWithQuery(Email.class, "Select * from Email where incoming = ? and is_saved_from_app = ?", "0", "0");
                bankAccounts = BankAccount.findWithQuery(BankAccount.class, "Select * from Bank_account where incoming = ? and is_saved_from_app = ?", "0", "0");
                notes = Note.findWithQuery(Note.class, "Select * from Note where incoming = ? and is_saved_from_app = ?", "0");
                callRecordings = CallRecording.findWithQuery(CallRecording.class, "Select * from Call_recording where incoming = ?",  "0");

                Collections.reverse(contacts);
                Collections.reverse(emails);
                Collections.reverse(bankAccounts);
                Collections.reverse(notes);
                Collections.reverse(callRecordings);

                if (contacts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (emails.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (bankAccounts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (notes.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (callRecordings.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                break;

            case 3://Saved from app
                contacts = Contact.findWithQuery(Contact.class, "Select * from Contact where is_saved_from_app = ?", "1");
                emails = Email.findWithQuery(Email.class, "Select * from Email where is_saved_from_app = ?", "1");
                bankAccounts = BankAccount.findWithQuery(BankAccount.class, "Select * from Bank_account where is_saved_from_app = ?", "1");
                notes = Note.findWithQuery(Note.class, "Select * from Note where is_saved_from_app = ?", "1");
                callRecordings = CallRecording.listAll(CallRecording.class);
                callRecordings.clear();

                Collections.reverse(contacts);
                Collections.reverse(emails);
                Collections.reverse(bankAccounts);
                Collections.reverse(notes);
                Collections.reverse(callRecordings);

                if (contacts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            ContactFragment.RContact_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (emails.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            EmailFragment.REmail_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (bankAccounts.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (notes.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            NoteFragment.RNote_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                if (callRecordings.size() == 0) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
                        }
                    });
                }
                break;
        }

        return null;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        ContactAdapter contactAdapter = new ContactAdapter(context, contacts);
        ContactFragment.mRecyclerView.setAdapter(contactAdapter);

        EmailAdapter emailAdapter = new EmailAdapter(context, emails);
        EmailFragment.mRecyclerView.setAdapter(emailAdapter);

        BankAccountAdapter bankAccountAdapter = new BankAccountAdapter(context, bankAccounts);
        BankAccountFragment.mRecycleView.setAdapter(bankAccountAdapter);

        NoteAdapter noteAdapter = new NoteAdapter(context, notes);
        NoteFragment.mRecyclerView.setAdapter(noteAdapter);

        RecordingAdapter recordingAdapter = new RecordingAdapter(context, callRecordings);
        RecordingFragment.mRecyclerView.setAdapter(recordingAdapter);
    }
}
