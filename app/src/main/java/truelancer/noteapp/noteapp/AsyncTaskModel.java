package truelancer.noteapp.noteapp;

import android.app.Activity;
import android.os.AsyncTask;
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

public class AsyncTaskModel extends AsyncTask<String, String, String> {
    public AsyncTaskModel() {
        super();
    }

    List<Contact> contacts = null;
    List<Email> emails = null;
    List<BankAccount> bankAccounts = null;
    List<Note> notes = null;
    List<CallRecording> callRecordings = null;

    public Activity context;
    public int no;


    public AsyncTaskModel(Activity context1, int no1) {
        this.context = context1;
        this.no = no1;
    }

    @Override
    protected String doInBackground(String... strings) {

        switch (no) {
            case 1:
                contacts = Contact.listAll(Contact.class);
                Collections.reverse(contacts);
                if (contacts.size()==0){
                   ContactFragment.RContact_no_data.setVisibility(View.VISIBLE);
                }else {
                    ContactFragment.RContact_no_data.setVisibility(View.GONE);
                }
                break;
            case 2:
                emails = Email.listAll(Email.class);
                Collections.reverse(emails);
                if (emails.size()==0){
                    EmailFragment.REmail_no_data.setVisibility(View.VISIBLE);
                }else {
                    EmailFragment.REmail_no_data.setVisibility(View.GONE);
                }
                break;
            case 3:
                bankAccounts = BankAccount.listAll(BankAccount.class);
                Collections.reverse(bankAccounts);
                if (bankAccounts.size()==0){
                    BankAccountFragment.RBank_no_data.setVisibility(View.VISIBLE);
                }else {
                    BankAccountFragment.RBank_no_data.setVisibility(View.GONE);
                }
                break;
            case 4:
                notes = Note.listAll(Note.class);
                Collections.reverse(notes);
                if (notes.size()==0){
                  NoteFragment.RNote_no_data.setVisibility(View.VISIBLE);
                }else {
                    NoteFragment.RNote_no_data.setVisibility(View.GONE);
                }
                break;
            case 5:
                callRecordings = CallRecording.listAll(CallRecording.class);
                Collections.reverse(callRecordings);
                if (callRecordings.size()==0){
                    RecordingFragment.RRecord_no_data.setVisibility(View.VISIBLE);
                }else {
                    RecordingFragment.RRecord_no_data.setVisibility(View.GONE);
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
        MainActivity.hideProgressDialog();

        switch (no) {
            case 1:
                ContactAdapter contactAdapter = new ContactAdapter(context, contacts);
                ContactFragment.mRecyclerView.setAdapter(contactAdapter);
                break;
            case 2:
                EmailAdapter emailAdapter = new EmailAdapter(context, emails);
                EmailFragment.mRecyclerView.setAdapter(emailAdapter);
                break;
            case 3:
                BankAccountAdapter bankAccountAdapter = new BankAccountAdapter(context, bankAccounts);
                BankAccountFragment.mRecyclerView.setAdapter(bankAccountAdapter);
                break;
            case 4:
                NoteAdapter noteAdapter = new NoteAdapter(context, notes);
                NoteFragment.mRecyclerView.setAdapter(noteAdapter);
                break;
            case 5:
                RecordingAdapter recordingAdapter = new RecordingAdapter(context, callRecordings);
                RecordingFragment.mRecyclerView.setAdapter(recordingAdapter);
                break;
        }
    }
}
