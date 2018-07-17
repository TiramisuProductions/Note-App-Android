package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.EventB;
import truelancer.noteapp.noteapp.MainActivity;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.Utils;

public class BankAccountAdapter extends RecyclerView.Adapter<BankAccountAdapter.MyView> {
    List<BankAccount> bankAccounts;
    Activity activity;
    String timeStampString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss dd-MM-yyyy");
    Context itemContext;
    String inout = "";

    public BankAccountAdapter(Activity activity, List<BankAccount> bankAccounts) {
        this.bankAccounts = bankAccounts;
        this.activity = activity;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_bank, parent, false);

        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {


        if(MyApp.nightMode){
            holder.bankCardView.setCardBackgroundColor(itemContext.getResources().getColor(R.color.darker_card));
            holder.call_txt.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledNumber.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.accountName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.others.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.date_time.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.savedDetails.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId1.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId2.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.overflow.setColorFilter(itemContext.getResources().getColor(R.color.white));
            holder.contactId1.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId2.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId3.setTextColor(itemContext.getResources().getColor(R.color.white));

        }


        if(bankAccounts.get(position).isSavedFromApp()){
            inout="Saved From App";
            holder.state_of_call.setImageResource(R.drawable.ic_saved_from_app);
            }
        else {
            if (!bankAccounts.get(position).isIncoming()) {
                inout = "Call To";
                holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
            } else {
                inout = "Call By";
                holder.state_of_call.setImageResource(R.drawable.ic_incoming);
            }
        }
        String tsMilli = bankAccounts.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);
        holder.call_txt.setText(inout);
        holder.date_time.setText("" + timeStampString);
        holder.contactName.setText(bankAccounts.get(position).getName());
        holder.accountName.setText(bankAccounts.get(position).getAccountNo());
        holder.others.setText(bankAccounts.get(position).getIfscCode());
        holder.calledName.setText(bankAccounts.get(position).getCalledName());
        holder.calledNumber.setText(bankAccounts.get(position).getCalledNumber());

        holder.bankCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.floatingActionMenu.close(true);
                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());
                popup_overflow.getMenu().findItem(R.id.save).setVisible(false);


                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share

                           if (holder.calledNumber.getText().length()==0 && holder.calledNumber.getText().length()==0) {
                               String shareText = "Date&Time: [" + timeStampString + "]\n"
                                       + inout +"\n"
                                       + "Saved Details\n"
                                       + "Contact Name : " + bankAccounts.get(position).getName() + "\n"
                                       + "Account Number : " + bankAccounts.get(position).getAccountNo() + "\n"
                                       + "IFSC Number : " + bankAccounts.get(position).getIfscCode();

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
                               if (!bankAccounts.get(position).isIncoming()) {
                                   inout = "Call To";
                                   holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
                               }
                               else {
                                   inout = "Call By";
                                   holder.state_of_call.setImageResource(R.drawable.ic_incoming);
                               }
                               String shareText = "Date&Time: [" + timeStampString + "]\n"
                                       + inout + "\n" + bankAccounts.get(position).getCalledName() + ":  "
                                       + bankAccounts.get(position).getCalledNumber() + "\n"
                                       + "Saved Details\n"
                                       + "Contact Name : " + bankAccounts.get(position).getName() + "\n"
                                       + "Account Number : " + bankAccounts.get(position).getAccountNo() + "\n"
                                       + "IFSC Number : " + bankAccounts.get(position).getIfscCode();

                               Intent shareIntent = new Intent(Intent.ACTION_SEND);
                               shareIntent.setType("text/plain");
                               shareIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);

                               try {
                                   activity.startActivity(Intent.createChooser(shareIntent, activity.getResources().getText(R.string.send_to)));
                               } catch (Exception e) {
                                   e.printStackTrace();
                               }
                               }


                        } else if (temp.equals("Edit")) {//Overflow Edit
                            final Dialog dialog = new Dialog(itemContext);
                            dialog.setContentView(R.layout.edit_dialog_bank);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final TextInputLayout contactNameTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field1);//Edit Dialog Contact Name
                            final TextInputLayout accountTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field2);//Edit Dialog Account No
                            final TextInputLayout othersTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field3);//Edit Dialog ifsc Others
                            final TextInputLayout calledNameTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field4);//Edit Dialog Called Name
                            final TextInputLayout calledNumberTextInputLayout=(TextInputLayout)dialog.findViewById(R.id.field5);//Edit Dialog Called Number
                            final EditText contactName = (EditText) dialog.findViewById(R.id.editContactName);
                            final EditText contactAccountNo = (EditText) dialog.findViewById(R.id.editContactAccountNumber);
                            final EditText contactIFSC = (EditText) dialog.findViewById(R.id.editContactIFSC);
                            final EditText calledName = (EditText) dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText) dialog.findViewById(R.id.editCalledNumber);
                            final Spinner calledState = (Spinner) dialog.findViewById(R.id.callstate);
                            final ImageView tick1 = (ImageView)dialog.findViewById(R.id.tick_1);//Edit Dialog Contact Name
                            final ImageView tick2 = (ImageView)dialog.findViewById(R.id.tick_2);//Edit Dialog Account No
                            final ImageView tick3 = (ImageView)dialog.findViewById(R.id.tick_3);//Edit Dialog IFSC No and others
                            final ImageView tick4 = (ImageView)dialog.findViewById(R.id.tick_4);//Edit Dialog Called Name
                            final ImageView tick5 = (ImageView)dialog.findViewById(R.id.tick_5);//Edit Dialog Called Number
                            Button btnDone = (Button) dialog.findViewById(R.id.button_done);
                            Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
                            TextView title=(TextView)dialog.findViewById(R.id.edit_dialog_text);
                            title.setText(R.string.edit_dialog_bank);
                            String options[] = {"Incoming", "Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext, android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            //Dialog EditText change
                            contactName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                  if(String.valueOf(s).length()>0){
                                    tick1.setVisibility(View.VISIBLE);
                                    contactNameTextInputLayout.setError(null);
                                  }
                                  else { tick2.setVisibility(View.INVISIBLE);
                                  }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            contactAccountNo.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (String.valueOf(s).length()>0){
                                        tick2.setVisibility(View.VISIBLE);
                                        accountTextInputLayout.setError(null);
                                    }
                                    else {
                                        tick2.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            contactIFSC.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (String.valueOf(s).length()>0){
                                        tick3.setVisibility(View.VISIBLE);
                                        othersTextInputLayout.setError(null);
                                    }
                                    else {
                                        tick3.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            calledName.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (String.valueOf(s).length()>0){
                                        tick4.setVisibility(View.VISIBLE);
                                        calledNameTextInputLayout.setError(null);
                                    }
                                    else {
                                        tick4.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            calledNumber.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (String.valueOf(s).length()>0){
                                        tick5.setVisibility(View.VISIBLE);
                                        calledNumberTextInputLayout.setError(null);
                                    }
                                    else {
                                        tick5.setVisibility(View.INVISIBLE);
                                    }
                                }
                                @Override
                                public void afterTextChanged(Editable s) {

                                }
                            });

                            calledState.setAdapter(adminSpinnerArrayAdapter);
                            //Edit dialog set spinner
                            if (bankAccounts.get(position).isIncoming()) {
                                calledState.setSelection(0);
                            } else {
                                calledState.setSelection(1);
                            }

                            //Edit dialog done button
                            btnDone.setOnClickListener(new View.OnClickListener() {
                                public void onClick(View view) {
                                    if (TextUtils.isEmpty(contactName.getText().toString())){
                                        contactNameTextInputLayout.setError(itemContext.getString(R.string.hint_contact_number));
                                    }else if (contactAccountNo.getText().toString().length()<=0){
                                        accountTextInputLayout.setError(itemContext.getString(R.string.hint_ac_no));
                                    }
                                    else {
                                        BankAccount bankAccount = BankAccount.findById(BankAccount.class, bankAccounts.get(position).getId());
                                        bankAccount.setAccountNo(contactAccountNo.getText().toString());
                                        bankAccount.setIfscCode(contactIFSC.getText().toString());
                                        bankAccount.setName(contactName.getText().toString());
                                        bankAccount.setCalledName(calledName.getText().toString());
                                        bankAccount.setCalledNumber(calledNumber.getText().toString());
                                        if (calledState.getSelectedItemPosition() == 0) {
                                            bankAccount.setIncoming(true);
                                        } else {
                                            bankAccount.setIncoming(false);
                                        }
                                        bankAccount.save();
                                        EventBus.getDefault().post(new EventB("3"));
                                        dialog.dismiss();
                                    }
                                }
                            });
                            //Edit dialog cancel button
                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                            contactName.setText(bankAccounts.get(position).getName());
                            contactAccountNo.setText(bankAccounts.get(position).getAccountNo());
                            contactIFSC.setText(bankAccounts.get(position).getIfscCode());
                            calledName.setText(bankAccounts.get(position).getCalledName());
                            calledNumber.setText(bankAccounts.get(position).getCalledNumber());
                            dialog.show();
                        } else {// Overflow Delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setTitle("You want to delete");
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    bankAccounts.get(position).getId();
                                    BankAccount bankAccount = BankAccount.findById(BankAccount.class, bankAccounts.get(position).getId());
                                    bankAccount.delete();
                                    bankAccounts.remove(position);
                                    notifyDataSetChanged();
                                    if(bankAccounts.size()==0){
                                        Utils.Visibility_no_data(3,true);
                                    }else {
                                        Utils.Visibility_no_data(3,false);
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

                        return true;
                    }

                });
                popup_overflow.show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return bankAccounts.size();
    }

    public class MyView extends RecyclerView.ViewHolder {
        private TextView contactName, accountName, others, calledName, calledNumber, call_txt, date_time,savedDetails,contactId1,contactId2,contactId3;
        private CardView bankCardView;
        private ImageView state_of_call, overflow;

        public MyView(View itemView) {
            super(itemView);
            contactName = (TextView) itemView.findViewById(R.id.contact_nameB);
            accountName = (TextView) itemView.findViewById(R.id.acc_no);
            others = (TextView) itemView.findViewById(R.id.ifsc);
            calledName = (TextView) itemView.findViewById(R.id.called_contactB);
            calledNumber = (TextView) itemView.findViewById(R.id.called_numberB);
            bankCardView = (CardView) itemView.findViewById(R.id.bank_cardView);
            call_txt = (TextView) itemView.findViewById(R.id.calltxt);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateofcallB);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            savedDetails = (TextView)itemView.findViewById(R.id.saved_details);
            contactId1 = (TextView)itemView.findViewById(R.id.contact_idB);
            contactId2 = (TextView)itemView.findViewById(R.id.contact_id2B);
            contactId3 = (TextView)itemView.findViewById(R.id.contact_id3B);

            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
}
