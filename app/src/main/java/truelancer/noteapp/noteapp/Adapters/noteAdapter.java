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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.R;

import static com.orm.SugarRecord.findById;


public class noteAdapter extends RecyclerView.Adapter<noteAdapter.MyView> {

    List<Note> notes;
    Activity activity;
    String timeStampString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
    Context itemContext;
    String inout = "";

    public noteAdapter(FragmentActivity activity, List<Note> not) {
        this.activity = activity;
        this.notes = not;
    }

    @Override
    public MyView onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(activity).inflate(R.layout.item_note, parent, false);
        return new MyView(itemView);
    }

    @Override
    public void onBindViewHolder(final MyView holder, final int position) {

        if (!notes.get(position).isIncoming()) {
            holder.call_txt.setText("Call To");
            holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
            inout = "Call To";

        } else {
            inout = "Call By";
        }

        String tsMilli = notes.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);

        holder.date_time.setText("" + timeStampString);
        holder.note2.setText(notes.get(position).getNote());
        holder.calledName.setText(notes.get(position).getCalledName());
        holder.calledNumber.setText(notes.get(position).getCalledNumber());

        holder.noteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


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
                                    + inout + " " + notes.get(position).getCalledName() + "\n"
                                    + notes.get(position).getCalledNumber() + "\n"
                                    + "Saved Details\n\n"
                                    + "Note : " + notes.get(position).getNote();

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
                            dialog.setContentView(R.layout.edit_dialog_notes);
                            dialog.setTitle("Title...");
                            dialog.setCancelable(false);
                            final EditText contactName = (EditText)dialog.findViewById(R.id.editContactName);
                            final EditText contactNote = (EditText)dialog.findViewById(R.id.editContactNote);
                            final EditText calledName =(EditText)dialog.findViewById(R.id.editCalledName);
                            final EditText calledNumber = (EditText)dialog.findViewById(R.id.editCalledNumber);
                            final Spinner calledState =(Spinner)dialog.findViewById(R.id.callstate);
                            Button btnDone = (Button)dialog.findViewById(R.id.btnSelect);
                            Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
                            String options[] = {"Incoming","Outgoing"};
                            ArrayAdapter<String> adminSpinnerArrayAdapter = new ArrayAdapter<String>(itemContext,   android.R.layout.simple_spinner_item, options);
                            adminSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                            calledState.setAdapter(adminSpinnerArrayAdapter);

                            if(notes.get(position).isIncoming()){
                                calledState.setSelection(0);
                            }
                            else {
                                calledState.setSelection(1);
                            }





                            btnDone.setOnClickListener(new View.OnClickListener() {

                                public void onClick(View view) {
                                    Note note =  findById(Note.class,notes.get(position).getId());
                                    note.setNote(contactNote.getText().toString());
                                    note.setCalledName(calledName.getText().toString());
                                    note.setCalledNumber(calledNumber.getText().toString());
                                    if(calledState.getSelectedItemPosition()==0){
                                        note.setIncoming(true);
                                    }
                                    else {
                                        note.setIncoming(false);
                                    }
                                    note.save();
                                    dialog.dismiss();
                                }
                            });

                            btnCancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });


                            contactNote.setText(notes.get(position).getNote());
                            calledName.setText(notes.get(position).getCalledName());
                            calledNumber.setText(notes.get(position).getCalledNumber());
                            dialog.show();
                        }

                        else {//Delete
                            AlertDialog.Builder builder = new AlertDialog.Builder(itemContext);

                            builder.setTitle("You want to delete");
                            builder.setMessage("Are you sure?");

                            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                    notes.get(position).getId();
                                    Note note = findById(Note.class, notes.get(position).getId());
                                    note.delete();
                                    notes.remove(position);
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

        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
              if(isChecked)
              {
                  Note note= Note.findById(Note.class,1);
                  Log.d("chubby",""+note);

              }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }


    public class MyView extends RecyclerView.ViewHolder {
        private TextView contactName, note2, calledName, calledNumber, call_txt, date_time;
        private CardView noteCardView;
        private CheckBox checkBox;
        private ImageView state_of_call, overflow;

        public MyView(View itemView) {
            super(itemView);
            this.setIsRecyclable(false);


            /*contactName = (TextView) itemView.findViewById(R.id.contact_nameN);
            note2 = (TextView) itemView.findViewById(R.id.noteit);
            noteCardView = (CardView) itemView.findViewById(R.id.note_cardView);
            calledName = (TextView) itemView.findViewById(R.id.called_contactN);
            calledNumber = (TextView) itemView.findViewById(R.id.called_numberN);
            call_txt = (TextView) itemView.findViewById(R.id.calltxt);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateofcallN);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            checkBox=(CheckBox) itemView.findViewById(R.id.checkBoxNote);*/
            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
}
