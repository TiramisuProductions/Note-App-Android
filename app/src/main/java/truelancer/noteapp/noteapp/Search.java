package truelancer.noteapp.noteapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import truelancer.noteapp.noteapp.Adapters.BankAccountAdapter;
import truelancer.noteapp.noteapp.Adapters.ContactAdapter;
import truelancer.noteapp.noteapp.Adapters.EmailAdapter;
import truelancer.noteapp.noteapp.Adapters.NoteAdapter;
import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;

public class Search extends AppCompatActivity {

    private EditText search;
    private ImageView notfoundimg;
    private TextView contacttxt, emailtxt, banktxt, notetxt, notfoundtxt;

    private RecyclerView contactSearchRecycler, emailSearchRecycler, bankSearchRecycler, noteSearchRecycler;
    private ContactAdapter contactSearchAdapter;
    private EmailAdapter emailSearchAdapter;
    private BankAccountAdapter bankSearchAdapter;
    private NoteAdapter noteSearchAdapter;

    View view = this.getCurrentFocus();

    List<Contact> contactFilterList = new ArrayList<Contact>();
    List<Email> emailFilterList = new ArrayList<Email>();
    List<BankAccount> bankFilterList = new ArrayList<BankAccount>();
    List<Note> noteFilterList = new ArrayList<Note>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        showInputMethod();

        search = (EditText) findViewById(R.id.search);

        contacttxt = (TextView) findViewById(R.id.contacttxt);
        emailtxt = (TextView) findViewById(R.id.emailtxt);
        banktxt = (TextView) findViewById(R.id.banktxt);
        notetxt = (TextView) findViewById(R.id.notetxt);
        notfoundtxt = (TextView) findViewById(R.id.not_found_txt);
        notfoundimg = (ImageView) findViewById(R.id.not_found_img);
        notfoundtxt.setVisibility(View.GONE);
        notfoundimg.setVisibility(View.GONE);

        if (contactFilterList.isEmpty()) {
            contacttxt.setVisibility(View.GONE);
        } else {
            contacttxt.setVisibility(View.VISIBLE);
        }
        if (emailFilterList.isEmpty()) {
            emailtxt.setVisibility(View.GONE);
        } else {
            emailtxt.setVisibility(View.VISIBLE);
        }
        if (bankFilterList.isEmpty()) {
            banktxt.setVisibility(View.GONE);
        } else {
            banktxt.setVisibility(View.VISIBLE);
        }
        if (noteFilterList.isEmpty()) {
            notetxt.setVisibility(View.GONE);
        } else {
            notetxt.setVisibility(View.VISIBLE);
        }

        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(getApplicationContext());
        contactSearchRecycler = (RecyclerView) findViewById(R.id.contact_search_recycler);
        contactSearchRecycler.setLayoutManager(mLayoutManager1);

        RecyclerView.LayoutManager mLayoutManager2 = new LinearLayoutManager(getApplicationContext());
        emailSearchRecycler = (RecyclerView) findViewById(R.id.email_search_recycler);
        emailSearchRecycler.setLayoutManager(mLayoutManager2);

        RecyclerView.LayoutManager mLayoutManager3 = new LinearLayoutManager(getApplicationContext());
        bankSearchRecycler = (RecyclerView) findViewById(R.id.bank_search_recycler);
        bankSearchRecycler.setLayoutManager(mLayoutManager3);

        RecyclerView.LayoutManager mLayoutManager4 = new LinearLayoutManager(getApplicationContext());
        noteSearchRecycler = (RecyclerView) findViewById(R.id.note_search_recycler);
        noteSearchRecycler.setLayoutManager(mLayoutManager4);

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (search.getText().toString().equals("")) {
                    //Toast.makeText(getApplicationContext(), "Nothing to serach", Toast.LENGTH_SHORT).show();
                    contacttxt.setVisibility(View.GONE);
                    emailtxt.setVisibility(View.GONE);
                    banktxt.setVisibility(View.GONE);
                    notetxt.setVisibility(View.GONE);

                    contactFilterList.clear();
                    emailFilterList.clear();
                    bankFilterList.clear();
                    noteFilterList.clear();
                } else {
                    contactFilterList.clear();
                    emailFilterList.clear();
                    bankFilterList.clear();
                    noteFilterList.clear();
                    execute(search.getText().toString());
                }

                // Toast.makeText(getApplicationContext(),""+search.getText().toString(),Toast.LENGTH_SHORT).show();
            }
        });

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {//keyboard search key pressed
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if (search.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "Nothing to search", Toast.LENGTH_SHORT).show();
                        contactFilterList.clear();
                        emailFilterList.clear();
                        bankFilterList.clear();
                        noteFilterList.clear();
                    } else {
                        contactFilterList.clear();
                        emailFilterList.clear();
                        bankFilterList.clear();
                        noteFilterList.clear();
                        execute(search.getText().toString());
                    }

                    return true;
                }
                return false;
            }
        });


    }

    public void showInputMethod() {
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void execute(String searchWord1) {

        String searchWord = searchWord1.toLowerCase();

        List<Contact> contacts = Contact.listAll(Contact.class);
        Collections.reverse(contacts);

        for (int i = 0; i < contacts.size(); i++) {
            String contactnameAll = contacts.get(i).getName().toLowerCase();
            String phoneAll = contacts.get(i).getPhoneno().toLowerCase();
            String callednoAll = contacts.get(i).getCalledNumber().toLowerCase();
            String callednameAll = contacts.get(i).getCalledName().toLowerCase();
            boolean savedFromApp = contacts.get(i).isSavedFromApp();

            if (contactnameAll.contains(searchWord)) {
                contactFilterList.add(contacts.get(i));
            } else if (phoneAll.contains(searchWord)) {
                contactFilterList.add(contacts.get(i));
            } else if (callednameAll.contains(searchWord)&&!savedFromApp) {
                contactFilterList.add(contacts.get(i));
            } else if (callednoAll.contains(searchWord)&&!savedFromApp) {
                contactFilterList.add(contacts.get(i));
            } else {
            }

        }
        contactSearchAdapter = new ContactAdapter(this, contactFilterList);
        contactSearchRecycler.setAdapter(contactSearchAdapter);
        List<Email> emails = Email.listAll(Email.class);
        Collections.reverse(emails);

        for (int i = 0; i < emails.size(); i++) {
            String contactnameAll = emails.get(i).getName().toLowerCase();
            String emailAll = emails.get(i).getEmailId().toLowerCase();
            String callednoAll = emails.get(i).getCalledNumber().toLowerCase();
            String callednameAll = emails.get(i).getCalledName().toLowerCase();

            if (contactnameAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else if (emailAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else if (callednameAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else if (callednoAll.contains(searchWord)) {
                emailFilterList.add(emails.get(i));
            } else {
            }

        }
        emailSearchAdapter = new EmailAdapter(this, emailFilterList);
        emailSearchRecycler.setAdapter(emailSearchAdapter);
        List<BankAccount> bankAccounts = BankAccount.listAll(BankAccount.class);
        Collections.reverse(bankAccounts);

        for (int i = 0; i < bankAccounts.size(); i++) {
            String contactnameAll = bankAccounts.get(i).getName().toLowerCase();
            String accNoAll = bankAccounts.get(i).getAccountNo().toLowerCase();
            String ifscAll = bankAccounts.get(i).getIfscCode().toLowerCase();
            String callednoAll = bankAccounts.get(i).getCalledNumber().toLowerCase();
            String callednameAll = bankAccounts.get(i).getCalledName().toLowerCase();

            if (contactnameAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (accNoAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (ifscAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (callednameAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else if (callednoAll.contains(searchWord)) {
                bankFilterList.add(bankAccounts.get(i));
            } else {
            }

        }
        bankSearchAdapter = new BankAccountAdapter(this, bankFilterList);
        bankSearchRecycler.setAdapter(bankSearchAdapter);
        List<Note> notes = Note.listAll(Note.class);
        Collections.reverse(notes);

        for (int i = 0; i < notes.size(); i++) {
            String noteAll = notes.get(i).getNote().toLowerCase();
            String callednoAll = notes.get(i).getCalledNumber().toLowerCase();
            String callednameAll = notes.get(i).getCalledName().toLowerCase();

             if (noteAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            } else if (callednameAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            } else if (callednoAll.contains(searchWord)) {
                noteFilterList.add(notes.get(i));
            } else {
            }

        }
        noteSearchAdapter = new NoteAdapter(this, noteFilterList);
        noteSearchRecycler.setAdapter(noteSearchAdapter);

        if (contactFilterList.isEmpty()) {
            contacttxt.setVisibility(View.GONE);
        } else {
            contacttxt.setVisibility(View.VISIBLE);
        }
        if (emailFilterList.isEmpty()) {
            emailtxt.setVisibility(View.GONE);
        } else {
            emailtxt.setVisibility(View.VISIBLE);
        }
        if (bankFilterList.isEmpty()) {
            banktxt.setVisibility(View.GONE);
        } else {
            banktxt.setVisibility(View.VISIBLE);
        }
        if (noteFilterList.isEmpty()) {
            notetxt.setVisibility(View.GONE);
        } else {
            notetxt.setVisibility(View.VISIBLE);
        }



        if (contactFilterList.isEmpty()) {
            if (emailFilterList.isEmpty()) {
                if (bankFilterList.isEmpty()) {
                    if (noteFilterList.isEmpty()) {
                        notfoundimg.setVisibility(View.VISIBLE);
                        notfoundtxt.setVisibility(View.VISIBLE);
                    }
                    else {
                        notfoundimg.setVisibility(View.INVISIBLE);
                        notfoundtxt.setVisibility(View.INVISIBLE);
                    }
                }
            }

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onPause() {
        super.onPause();

        view = this.getCurrentFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(this,MainActivity.class);
        setResult(RESULT_OK,intent);
        finish();

    }
}
