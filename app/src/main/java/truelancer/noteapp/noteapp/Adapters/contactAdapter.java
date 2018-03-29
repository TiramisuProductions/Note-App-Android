package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.contactActivity;

import static android.app.Activity.RESULT_OK;


public class contactAdapter extends RecyclerView.Adapter<contactAdapter.MyView> {
    List<Contact> contacts;
    Context activity;
    Context itemContext;
    String timeStampString;
    String inout = "";

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");

    static final int RESULT_PICK_CONTACT = 4;


    public contactAdapter(Activity activity, List<Contact> contacts) {
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
        public TextView contactName, contactNum, calledName, calledNumber, call_txt, date_time;
        public CardView contactCardView;
        public ImageView overflow, state_of_call;

        public MyView(View itemView) {

            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contact_name);
            contactNum = (TextView) itemView.findViewById(R.id.contact_number);
            call_txt = (TextView) itemView.findViewById(R.id.calltxt);
            contactCardView = (CardView) itemView.findViewById(R.id.contact_cardView);
            calledName = (TextView) itemView.findViewById(R.id.called_contact);
            calledNumber = (TextView) itemView.findViewById(R.id.called_number);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateofcall);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
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
holder.state_of_call.setVisibility(View.INVISIBLE);
        }else{
            if (!contacts.get(position).isIncoming()) {
                holder.call_txt.setText("Call To");
                holder.state_of_call.setImageResource(R.drawable.outgoing_call);
                inout = "Call To";

            } else {
                inout = "Call By";
            }
        }



        Log.d("god",""+contacts.get(position).isIncoming());
        String tsMilli = contacts.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);

        timeStampString = getDate(tsLong);
        holder.date_time.setText("" + timeStampString);
        holder.contactNum.setText(contacts.get(position).getPhoneno());
        holder.contactName.setText(contacts.get(position).getName());
        holder.calledName.setText(contacts.get(position).getCalledName());
        holder.calledNumber.setText(contacts.get(position).getCalledNumber());

        holder.contactCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, contactActivity.class);
                intent.putExtra("contactName", contacts.get(position).getName());
                intent.putExtra("contactNo", contacts.get(position).getPhoneno());
                intent.putExtra("calledName", contacts.get(position).getCalledName());
                intent.putExtra("calledNumber", contacts.get(position).getCalledNumber());
                String income = "" + contacts.get(position).isIncoming();
                intent.putExtra("incoming", income);

                String tsMilli = contacts.get(position).getTsMilli();
                long tsLong = Long.parseLong(tsMilli);
                String timeStampString2 = getDate(tsLong);
                intent.putExtra("timestamp", timeStampString2);
                //Toast.makeText(activity, "Contact timestamp: " + timeStampString2, Toast.LENGTH_SHORT).show();
                activity.startActivity(intent);
            }
        });


        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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

                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
                            try {
                                activity.startActivity(whatsappIntent);
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
                            dialog.setContentView(R.layout.edit_dialog);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final EditText contactName = (EditText)dialog.findViewById(R.id.editContactName);
                            final EditText contactNumber = (EditText)dialog.findViewById(R.id.editContactNumber);
                            final EditText calledName =(EditText)dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText)dialog.findViewById(R.id.editCalledNumber);
                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            Button btnDone = (Button)dialog.findViewById(R.id.btnSelect);
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
                                    dialog.dismiss();
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
        MainActivity.dataFromAapter = contacts.get(position).getPhoneno();
        ((Activity) activity).startActivityForResult(i, RESULT_PICK_CONTACT);

    }


}
