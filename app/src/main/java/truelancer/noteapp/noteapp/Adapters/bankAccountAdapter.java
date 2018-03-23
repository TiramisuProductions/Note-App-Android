package truelancer.noteapp.noteapp.Adapters;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.BankAccount;
import truelancer.noteapp.noteapp.Database.Contact;
import truelancer.noteapp.noteapp.R;
import truelancer.noteapp.noteapp.bankActivity;

public class bankAccountAdapter extends RecyclerView.Adapter<bankAccountAdapter.MyView> {
    List<BankAccount> bankAccounts;
    Activity activity;
    String timeStampString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
    Context itemContext;
    String inout ="";

    public bankAccountAdapter(FragmentActivity activity, List<BankAccount> bankAccounts) {
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

        if (!bankAccounts.get(position).isIncoming()) {
            holder.call_txt.setText("Call To");
            inout = "Call To";
            holder.state_of_call.setImageResource(R.drawable.outgoing_call);
        } else {
            inout = "Call By";
        }

        String tsMilli = bankAccounts.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);

        holder.date_time.setText("" + timeStampString);

        holder.contactName.setText(bankAccounts.get(position).getName());
        holder.accountName.setText(bankAccounts.get(position).getAccountNo());
        holder.others.setText(bankAccounts.get(position).getIfscCode());

        holder.calledName.setText(bankAccounts.get(position).getCalledName());
        holder.calledNumber.setText(bankAccounts.get(position).getCalledNumber());

        holder.bankCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, bankActivity.class);
                intent.putExtra("bankContactName", bankAccounts.get(position).getName());
                intent.putExtra("accountNo", bankAccounts.get(position).getAccountNo());
                intent.putExtra("ifscCode", bankAccounts.get(position).getIfscCode());

                intent.putExtra("calledName", bankAccounts.get(position).getCalledName());
                intent.putExtra("calledNumber", bankAccounts.get(position).getCalledNumber());

                String income = "" + bankAccounts.get(position).isIncoming();
                intent.putExtra("incoming", income);

                String tsMilli = bankAccounts.get(position).getTsMilli();
                long tsLong = Long.parseLong(tsMilli);
                String timeStampString2 = getDate(tsLong);
                intent.putExtra("timestamp", timeStampString2);
                Toast.makeText(activity, "Contact timestamp: " + timeStampString2, Toast.LENGTH_SHORT).show();
                activity.startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PopupMenu popup_overflow = new PopupMenu(activity, holder.overflow);
                popup_overflow.getMenuInflater().inflate(R.menu.menu_overflow, popup_overflow.getMenu());
                popup_overflow.getMenu().findItem(R.id.save).setVisible(false);

                popup_overflow.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        String temp = item.getTitle().toString();
                        if (temp.equals("Share")) {//Share


                            String shareText = "DateTime: [" + timeStampString + "]\n"
                                    + inout + " " + bankAccounts.get(position).getCalledName() + "\n"
                                    + bankAccounts.get(position).getCalledNumber() + "\n"
                                    + "Saved Details\n\n"
                                    + "Contact Name : " + bankAccounts.get(position).getName() + "\n"
                                    + "Account Number : " + bankAccounts.get(position).getAccountNo() + "\n"
                                    + "IFSC Number : " + bankAccounts.get(position).getIfscCode();

                            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                            whatsappIntent.setType("text/plain");
                            whatsappIntent.setPackage("com.whatsapp");
                            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "" + shareText);
                            try {
                                activity.startActivity(whatsappIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }else if(temp.equals("Edit")){
                            final Dialog dialog = new Dialog(itemContext);
                            dialog.setContentView(R.layout.edit_dialog);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final EditText contactName = (EditText)dialog.findViewById(R.id.editContactName);
                            final EditText contactAccountNo = (EditText)dialog.findViewById(R.id.editContactNumber);
                            final EditText contactIFSC = (EditText)dialog.findViewById(R.id.editContactIFSC);
                            final EditText calledName =(EditText)dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText)dialog.findViewById(R.id.editCalledNumber);
                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            Button btnDone = (Button)dialog.findViewById(R.id.btnSelect);
                            Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
                            String options[] = {"Incoming","Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext,   android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            calledState.setAdapter(adminSpinnerArrayAdapter);

                            if(bankAccounts.get(position).isIncoming()){
                                calledState.setSelection(0);
                            }
                            else {
                                calledState.setSelection(1);
                            }





                            btnDone.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View view) {
                                    BankAccount bankAccount =  BankAccount.findById(BankAccount.class,bankAccounts.get(position).getId());
                                    bankAccount.setAccountNo(contactAccountNo.getText().toString());
                                    bankAccount.setIfscCode(contactIFSC.getText().toString());
                                    bankAccount.setName(contactName.getText().toString());
                                    bankAccount.setCalledName(calledName.getText().toString());
                                    bankAccount.setCalledNumber(calledNumber.getText().toString());
                                    if(calledState.getSelectedItemPosition()==0){
                                        bankAccount.setIncoming(true);
                                    }
                                    else {
                                        bankAccount.setIncoming(false);
                                    }
                                    bankAccount.save();
                                    dialog.dismiss();
                                }
                            });

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
                        }

                         else {//Delete
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
        private TextView contactName, accountName, others, calledName, calledNumber, call_txt, date_time;
        private CardView bankCardView;
        private ImageView state_of_call,overflow;

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
            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
}
