package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.provider.ContactsContract;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.MyView> {
    List<Contact> contacts;
    Context activity;
    Context itemContext;
    String timeStampString;
    String inout = "";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");

    static final int RESULT_PICK_CONTACT = 4;


    public ContactAdapter(Activity activity, List<Contact> contacts) {
        this.activity = activity;
        this.contacts = contacts;
    }


    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_contact, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        executeit(holder, position);
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        public TextView contactName, contactNum, calledName, calledNumber, call_txt, date_time,savedDetails,contactId1,contactId2;
        public CardView contactCardView;
        public ImageView overflow, state_of_call;

        public MyView(View itemView) {

            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactNum = (TextView) itemView.findViewById(R.id.contact_number);
            call_txt = (TextView) itemView.findViewById(R.id.callText);
            contactCardView = (CardView) itemView.findViewById(R.id.contact_cardView);
            calledName = (TextView) itemView.findViewById(R.id.called_contact);
            calledNumber = (TextView) itemView.findViewById(R.id.called_number);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateOfCall);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            savedDetails =(TextView)itemView.findViewById(R.id.saved_details);
            contactId1 = (TextView)itemView.findViewById(R.id.contact_id);
            contactId2 =(TextView)itemView.findViewById(R.id.contact_id2);

            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    public void executeit(final MyView holder, final int position) {


        if(contacts.get(position).isSavedFromApp()){
holder.state_of_call.setImageResource(R.drawable.ic_saved_from_app);
inout="Saved From App";
        }else{
            if (!contacts.get(position).isIncoming()) {

                holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
                inout = "Call To";

            } else {
                holder.state_of_call.setImageResource(R.drawable.ic_incoming);
                inout = "Call By";
            }
        }

        if(!MyApp.defaultTheme){
            holder.contactCardView.setCardBackgroundColor(itemContext.getResources().getColor(R.color.darker_card));
            holder.call_txt.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledNumber.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactNum.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.date_time.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.savedDetails.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId1.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId2.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.overflow.setColorFilter(itemContext.getResources().getColor(R.color.white));

        }




        Log.d("god",""+contacts.get(position).isIncoming());
        String tsMilli = contacts.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        holder.call_txt.setText(inout);
        timeStampString = getDate(tsLong);
        holder.date_time.setText("" + timeStampString);
        holder.contactNum.setText(contacts.get(position).getPhoneno());
        holder.contactName.setText(contacts.get(position).getName());
        holder.calledName.setText(contacts.get(position).getCalledName());
        holder.calledNumber.setText(contacts.get(position).getCalledNumber());

        holder.contactCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(itemContext,""+position,Toast.LENGTH_LONG).show();

                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());


                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share

                            String shareText = "DateTime: [" + timeStampString + "]\n"
                                    + inout + " " + contacts.get(position).getCalledName() + "\n"
                                    + contacts.get(position).getCalledNumber() + "\n"
                                    + "Saved Details\n\n"
                                    + "Contact Name : " + contacts.get(position).getName() + "\n"
                                    + "Contact Number : " + contacts.get(position).getPhoneno();

                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);

                            try {
                                activity.startActivity(Intent.createChooser(shareIntent,activity.getResources().getText(R.string.send_to)));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else if (temp.equals("Delete")) {//Delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setTitle("You want to delete");
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    contacts.get(position).getId();
                                    Contact contact = Contact.findById(Contact.class, contacts.get(position).getId());
                                    contact.delete();
                                    contacts.remove(position);
                                    notifyDataSetChanged();
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
                        }else if(temp.equals("Edit")){
                            final Dialog dialog = new Dialog(itemContext);
                            dialog.setContentView(R.layout.edit_dialog2);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final TextInputLayout contactNameTextInputLayout =(TextInputLayout)dialog.findViewById(R.id.field1);
                            final TextInputLayout contactNumberTextInputLayout =(TextInputLayout)dialog.findViewById(R.id.field2);
                            final TextInputLayout calledNameTextInputLayout =(TextInputLayout)dialog.findViewById(R.id.field3);
                            final TextInputLayout calledNumberTextInputLayout =(TextInputLayout)dialog.findViewById(R.id.field4);

                            contactNameTextInputLayout.setHint(itemContext.getString(R.string.hint_contact_name));
                            contactNumberTextInputLayout.setHint(itemContext.getString(R.string.hint_contact_number));
                            calledNameTextInputLayout.setHint(itemContext.getString(R.string.hint_called_name));
                            calledNumberTextInputLayout.setHint(itemContext.getString(R.string.hint_called_number));

                            final TextInputEditText contactName = (TextInputEditText) dialog.findViewById(R.id.editField1);
                            final TextInputEditText contactNumber = (TextInputEditText)dialog.findViewById(R.id.editField2);
                            final TextInputEditText calledName =(TextInputEditText)dialog.findViewById(R.id.editField3);
                            final TextInputEditText calledNumber = (TextInputEditText)dialog.findViewById(R.id.editField4);


                            final ImageView tick1 = (ImageView)dialog.findViewById(R.id.tick_1);
                            final ImageView tick2 = (ImageView)dialog.findViewById(R.id.tick_2);
                            final ImageView tick3 = (ImageView)dialog.findViewById(R.id.tick_3);
                            final ImageView tick4 = (ImageView)dialog.findViewById(R.id.tick_4);

                            contactName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    if(String.valueOf(charSequence).length()>0){
                                        tick1.setVisibility(View.VISIBLE);
                                    }else{
                                        tick1.setVisibility(View.INVISIBLE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });


                            contactNumber.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                    if(Utils.isValidMobile(String.valueOf(charSequence))){
                                        tick2.setVisibility(View.VISIBLE);
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





                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            Button btnDone = (Button)dialog.findViewById(R.id.btnDone);
                            Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
                            String options[] = {"Incoming","Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext,   android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            calledState.setAdapter(adminSpinnerArrayAdapter);

                            if(contacts.get(position).isIncoming()){
                                calledState.setSelection(0);
                            }
                            else {
                                calledState.setSelection(1);
                            }





                            btnDone.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View view) {

                                    if(contactName.getText().toString().length()<=0){
                                        contactName.setError(itemContext.getString(R.string.hint_contact_name));
                                    }else if(!Utils.isValidMobile(contactNumber.getText().toString())){
                                        contactNumber.setError(itemContext.getString(R.string.hint_contact_number));
                                    }else if(calledName.getText().toString().length()<=0){
                                        calledName.setError(itemContext.getString(R.string.hint_called_name));
                                    }else if(!Utils.isValidMobile(calledNumber.getText().toString())){
                                        calledNumber.setError(itemContext.getString(R.string.hint_called_number));
                                    }else{
                                        Contact contact =  Contact.findById(Contact.class,contacts.get(position).getId());
                                        contact.setPhoneno(contactNumber.getText().toString());
                                        contact.setName(contactName.getText().toString());
                                        contact.setCalledName(calledName.getText().toString());
                                        contact.setCalledNumber(calledNumber.getText().toString());
                                        if(calledState.getSelectedItemPosition()==0){
                                            contact.setIncoming(true);
                                        }
                                        else {
                                            contact.setIncoming(false);
                                        }
                                        contact.save();
                                        EventBus.getDefault().post(new EventB("1"));
                                        dialog.dismiss();
                                    }





                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });


                            contactName.setText(contacts.get(position).getName());
                            contactNumber.setText(contacts.get(position).getPhoneno());
                            calledName.setText(contacts.get(position).getCalledName());
                            calledNumber.setText(contacts.get(position).getCalledNumber());
                            dialog.show();
                        }




                        else {//Save

                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setMessage("Create a new contact or add to an existing contact?");

                            builder.setPositiveButton("EXISTING", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {
                                    update_contact(position);

                                }
                            });

                            builder.setNegativeButton("NEW", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    save_contact(position);
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

    private void save_contact(int position) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.NAME, contacts.get(position).getName());
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, contacts.get(position).getPhoneno());

        activity.startActivity(intent);
    }

    private void update_contact(int position) {

        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        //i.putExtra("positionItem",position);
        MainActivity.dataFromAdapter = contacts.get(position).getPhoneno();
        ((Activity) activity).startActivityForResult(i, RESULT_PICK_CONTACT);

    }


}
