package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.MyView> {
    List<Email> emails;
    Activity activity;
    String timeStampString;
    Context itemContext;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    String inout = "";

    static final int RESULT_PICK_CONTACT_E= 5;

    public EmailAdapter(Activity activity, List<Email> emails) {
        this.activity = activity;
        this.emails = emails;

        //Toast.makeText(activity,"emails coming2! "+this.emails.size(),Toast.LENGTH_LONG).show();
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_email, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {


        if(MyApp.nightMode){
            holder.emailCardView.setCardBackgroundColor(itemContext.getResources().getColor(R.color.darker_card));
            holder.call_txt.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledNumber.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.emailId.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.date_time.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.savedDetails.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId1.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId2.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.overflow.setColorFilter(itemContext.getResources().getColor(R.color.white));
        }


        if(emails.get(position).isBackedUp()){
            holder.isBackedUp.setImageResource(R.drawable.ic_cloud_done_black_24dp);
        }else {
            holder.isBackedUp.setImageResource(R.drawable.ic_cloud_off_black_24dp);
        }



      //Checking incoming,outgoing call or saved from app
       if(emails.get(position).isSavedFromApp()){
           holder.state_of_call.setImageResource(R.drawable.ic_saved_from_app);
           inout="Saved From App";
        }
        else{
            if (!emails.get(position).isIncoming()) {
                inout = "Call To";
                holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
            }
            else {
                holder.state_of_call.setImageResource(R.drawable.ic_incoming);
                inout = "Call By";

            }
        }
        String tsMilli = emails.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);
        holder.call_txt.setText(inout);
        holder.date_time.setText("" + timeStampString);
        holder.contactName.setText(emails.get(position).getName());
        holder.emailId.setText(emails.get(position).getEmailId());
        holder.calledName.setText(emails.get(position).getCalledName());
        holder.calledNumber.setText(emails.get(position).getCalledNumber());

       /* holder.emailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.floatingActionMenu.close(true);
                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());

                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share
                            if(holder.calledNumber.getText().length()==0 && holder.calledNumber.getText().length()==0) {
                                String shareText = "Date&Time: [" + timeStampString + "]\n"
                                        +inout+"\n"+"Saved Details\n"
                                        + "Contact Name : " + emails.get(position).getName() + "\n"
                                        + "Email Id : " + emails.get(position).getEmailId();

                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
                                try {
                                    activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.send_to)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                if (!emails.get(position).isIncoming()) {
                                    inout = "Call To";
                                    holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
                                }
                                else {
                                    inout = "Call By";
                                    holder.state_of_call.setImageResource(R.drawable.ic_incoming);
                                }
                                String shareText = "Date&Time: [" + timeStampString + "]\n"
                                        + inout + "\n" + emails.get(position).getCalledName()+":  "
                                        + emails.get(position).getCalledNumber() + "\n"
                                        + "Saved Details\n"
                                        + "Contact Name : " + emails.get(position).getName() + "\n"
                                        + "Email Id : " + emails.get(position).getEmailId();

                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
                                try {
                                    activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.send_to)));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }

                        } else if (temp.equals("Delete")){//Delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setTitle("You want to delete");
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    emails.get(position).getId();
                                    Email email = Email.findById(Email.class, emails.get(position).getId());
                                    email.delete();
                                    emails.remove(position);
                                    notifyDataSetChanged();
                                    if(emails.size()==0){
                                        Utils.Visibility_no_data(2,true);
                                    }else {
                                        Utils.Visibility_no_data(2,false);
                                    }
                                    dialog.dismiss();
                                }
                            });

                            builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        //Overflow Edit Dialog
                        else if(temp.equals("Edit")){
                            final Dialog dialog = new Dialog(itemContext);
                            dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
                            dialog.setContentView(R.layout.edit_dialog_email);
                            dialog.setCancelable(false);
                            final TextInputLayout contactNameTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field1);//Edit Dialog Contact Name
                            final TextInputLayout emailTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field2);//Edit Dialog Email
                            TextInputLayout calledNumberTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field3);//Edit Dialog Called Number
                            TextInputLayout calledNameTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field4);//Edit Dialog Called Name
                            final EditText contactName = (EditText)dialog.findViewById(R.id.editContactName);
                            final EditText contactEmail = (EditText)dialog.findViewById(R.id.editContactEmail);
                            final EditText calledName =(EditText)dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText)dialog.findViewById(R.id.editCalledNumber);
                            final ImageView tick1 = (ImageView)dialog.findViewById(R.id.tick_1);//Edit Dialog Contact Name
                            final ImageView tick2 = (ImageView)dialog.findViewById(R.id.tick_2);//Edit Dialog Email
                            final ImageView tick3 = (ImageView)dialog.findViewById(R.id.tick_3);//Edit Dialog Called Name
                            final ImageView tick4 = (ImageView)dialog.findViewById(R.id.tick_4);//Edit Dialog Called number
                            Button btnDone = (Button)dialog.findViewById(R.id.button_done);//Edit Dialog Done Button
                            Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);//Edit Dialog Cancel Button
                            TextView title=(TextView)dialog.findViewById(R.id.edit_dialog_text);
                            title.setText(R.string.edit_dialog_email);


                            //Edit Dialog editText change
                            contactName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }
                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    if(String.valueOf(charSequence).length()>0){
                                        tick1.setVisibility(View.VISIBLE);
                                        contactNameTextInputLayout.setError(null);
                                    }else{
                                        tick1.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            contactEmail.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }
                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if(Utils.isValidEmail(String.valueOf(charSequence))){
                                        tick2.setVisibility(View.VISIBLE);
                                        emailTextInputLayout.setError(null);
                                    }else{
                                        tick2.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            calledName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }
                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    if(String.valueOf(charSequence).length()>0){
                                        tick3.setVisibility(View.VISIBLE);
                                    }else{
                                        tick3.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            calledNumber.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }
                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if(Utils.isValidMobile(String.valueOf(charSequence))){
                                        tick4.setVisibility(View.VISIBLE);
                                    }else{
                                        tick4.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            //Edit Dialog set  Spinner

                            //Edit Dialog Done Button
                            btnDone.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View view) {

                                    if (TextUtils.isEmpty(contactName.getText().toString())) {
                                        contactNameTextInputLayout.setError(itemContext.getString(R.string.hint_contact_number));
                                    }else if (!Utils.isValidEmail(contactEmail.getText().toString())){
                                        emailTextInputLayout.setError(itemContext.getString(R.string.error_email_edit_text));
                                    }else {

                                        Email email = Email.findById(Email.class, emails.get(position).getId());
                                        email.setEmailId(contactEmail.getText().toString());
                                        email.setName(contactName.getText().toString());
                                        email.setCalledName(calledName.getText().toString());
                                        email.setCalledNumber(calledNumber.getText().toString());

                                        email.save();
                                        EventBus.getDefault().post(new EventB("2"));
                                        dialog.dismiss();
                                    }
                                }
                            });
                            //Edit Dialog Cancel Button
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            contactName.setText(emails.get(position).getName());
                            contactEmail.setText(emails.get(position).getEmailId());
                            calledName.setText(emails.get(position).getCalledName());
                            calledNumber.setText(emails.get(position).getCalledNumber());
                            dialog.show();
                        }
                        //Add To Contact
                        else{//Save
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setMessage("Create a new contact or add to an existing contact?");

                            builder.setPositiveButton("EXISTING", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    update_email(position);

                                }
                            });

                            builder.setNegativeButton("NEW", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    save_email(position);
                                }
                            });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        return true;
                    }
                });
                popup_overflow.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return emails.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        private TextView contactName, emailId, call_txt, calledName, calledNumber, date_time,savedDetails,contactId1,contactId2;
        private CardView emailCardView;
        private ImageView state_of_call, overflow,isBackedUp;



        public MyView(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contact_nameEmail);
            emailId = (TextView) itemView.findViewById(R.id.emailID);
            calledName = (TextView) itemView.findViewById(R.id.called_contactEmail);
            calledNumber = (TextView) itemView.findViewById(R.id.called_numberEmail);
            emailCardView = (CardView) itemView.findViewById(R.id.email_cardView);
            call_txt = (TextView) itemView.findViewById(R.id.callText);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateOfCallEmail);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            savedDetails = (TextView)itemView.findViewById(R.id.saved_details);
            contactId1 = (TextView)itemView.findViewById(R.id.contact_idEmail);
            contactId2 = (TextView)itemView.findViewById(R.id.contact_id2Email);
            isBackedUp = (ImageView)itemView.findViewById(R.id.isBackedUp);
            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
    //Add to contact add new contact
    private void save_email(int position){
        Intent intent=new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, emails.get(position).getName());
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, emails.get(position).getEmailId());

        activity.startActivity(intent);
    }
    //Add to contact update existing contact
    private void update_email(int position){
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

        MainActivity.dataFromAdapter = emails.get(position).getEmailId();
        ((Activity) activity).startActivityForResult(i, RESULT_PICK_CONTACT_E);
    }
}
