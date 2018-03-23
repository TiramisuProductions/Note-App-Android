package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.Database.Note;

public class Add_New extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int option = getIntent().getIntExtra("option",0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
        String dateString = simpleDateFormat.format(new Date());//get current timestamp direct to string

        Date startDate = null;
        try {
            startDate = simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

       final String timeStampMilli = "" + startDate.getTime();
        switch (option){
            case 0:
                setContentView(R.layout.contact_add);
                final EditText EditContactName = (EditText)findViewById(R.id.contact_name_et);
                final EditText EditContactNo = (EditText)findViewById(R.id.contact_no_et);

                Button saveContact = (Button)findViewById(R.id.savecontact_btn);


                saveContact.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String contactName = EditContactName.getText().toString();
                        String contactNumber = EditContactNo.getText().toString();
                        Contact contact = new Contact(contactName, contactNumber, "", "", false, timeStampMilli);


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
                        // Contact.deleteAll(Contact.class);

                    }
                });
                break;
            case 1:
                setContentView(R.layout.email_add);
                final EditText ContactName2 = (EditText) findViewById(R.id.contact_name2_et);
                final EditText EmailID = (EditText) findViewById(R.id.emailId_et);
                Button saveEmail = (Button) findViewById(R.id.saveemail_btn);

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

                            Email email = new Email(contactName, emailId, "", "", false, timeStampMilli);
                            email.save();

                            //List<Email> emails = Email.listAll(Email.class);

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
                setContentView(R.layout.account_add);
                final EditText ContactName3 = (EditText) findViewById(R.id.contact_name3_et);
                final EditText AccountNo = (EditText) findViewById(R.id.account_no_et);
                final EditText Others = (EditText)findViewById(R.id.others_et);
                Button saveAccount = (Button) findViewById(R.id.saveaccount_btn);

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
                            AccountNo.setError("Enter no ");
                            return;
                        }
                        if (TextUtils.isEmpty(others)) {
                            Others.setError("Enter code ");
                            return;
                        }

                        BankAccount bankAccount = new BankAccount(contactName, accountNumber, others,"", "", false, timeStampMilli);
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
                setContentView(R.layout.notes_add);
                final EditText ContactName4 = (EditText) findViewById(R.id.contact_name4_et);
                final EditText Note1 = (EditText)findViewById(R.id.note_et);
                Button saveNote = (Button)findViewById(R.id.savenote_btn);

                saveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String contactName = ContactName4.getText().toString();
                        String note = Note1.getText().toString();

                        if (TextUtils.isEmpty(contactName)) {
                            ContactName4.setError("Enter name");
                            return;
                        }
                        if (TextUtils.isEmpty(note)) {
                            Note1.setError("Enter note ");
                            return;
                        }

                        Note noteN = new Note( note,"", "",  timeStampMilli,false,false);
                        noteN.save();

                        ContactName4.setText("");
                        Note1.setText("");
                        //List<Note> noteList = Note.listAll(Note.class);

                        //Toast.makeText(mContext, "" + contactName + " " + note + " " + noteList, Toast.LENGTH_SHORT).show();
                    }
                });



                break;
        }

    }


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

    public final static boolean isValidEmail(String target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
