package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Email;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.R;

public class emailAdapter extends RecyclerView.Adapter<emailAdapter.MyView> {
    List<Email> emails;
    Activity activity;
    String timeStampString;
    Context itemContext;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
    String inout = "";

    static final int RESULT_PICK_CONTACT_E= 5;

    public emailAdapter(FragmentActivity activity, List<Email> emails) {
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


       if(emails.get(position).isSavedFromApp()){
           holder.state_of_call.setVisibility(View.INVISIBLE);
        }else{
            if (!emails.get(position).isIncoming()) {
                holder.call_txt.setText("Call To");
                inout = "Call To";
                holder.state_of_call.setImageResource(R.drawable.outgoing_call);
            } else {
                inout = "Call By";
            }
        }




        String tsMilli = emails.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);

        holder.date_time.setText("" + timeStampString);
        holder.contactName.setText(emails.get(position).getName());
        holder.emailId.setText(emails.get(position).getEmailId());
        holder.calledName.setText(emails.get(position).getCalledName());
        holder.calledNumber.setText(emails.get(position).getCalledNumber());

        holder.emailCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());

                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share


                            String shareText = "DateTime: [" + timeStampString + "]\n"
                                    + inout + " " + emails.get(position).getCalledName() + "\n"
                                    + emails.get(position).getCalledNumber() + "\n"
                                    + "Saved Details\n\n"
                                    + "Contact Name : " + emails.get(position).getName() + "\n"
                                    + "Email Id : " + emails.get(position).getEmailId();

                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
                            try {
                                activity.startActivity(whatsappIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
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
                        else if(temp.equals("Edit")){
                            final Dialog dialog = new Dialog(itemContext);
                            dialog.setContentView(R.layout.edit_dialog_email);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final EditText contactName = (EditText)dialog.findViewById(R.id.editContactName);
                            final EditText contactEmail = (EditText)dialog.findViewById(R.id.editContactEmail);
                            final EditText calledName =(EditText)dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText)dialog.findViewById(R.id.editCalledNumber);
                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            Button btnDone = (Button)dialog.findViewById(R.id.btnSelect);
                            Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
                            String options[] = {"Incoming","Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext,   android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            calledState.setAdapter(adminSpinnerArrayAdapter);

                            if(emails.get(position).isIncoming()){
                                calledState.setSelection(0);
                            }
                            else {
                                calledState.setSelection(1);
                            }





                            btnDone.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View view) {
                                    Email email =  Email.findById(Email.class,emails.get(position).getId());
                                    email.setEmailId(contactEmail.getText().toString());
                                    email.setName(contactName.getText().toString());
                                    email.setCalledName(calledName.getText().toString());
                                    email.setCalledNumber(calledNumber.getText().toString());
                                    if(calledState.getSelectedItemPosition()==0){
                                        email.setIncoming(true);
                                    }
                                    else {
                                        email.setIncoming(false);
                                    }
                                    email.save();
                                    dialog.dismiss();
                                }
                            });

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

                        else{//Save
                            save_email(position);
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
        private TextView contactName, emailId, call_txt, calledName, calledNumber, date_time;
        private CardView emailCardView;
        private ImageView state_of_call, overflow;

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
            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }

    private void save_email(int position){
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);

        MainActivity.dataFromAapter = emails.get(position).getEmailId();
        ((Activity) activity).startActivityForResult(i, RESULT_PICK_CONTACT_E);
    }
}
