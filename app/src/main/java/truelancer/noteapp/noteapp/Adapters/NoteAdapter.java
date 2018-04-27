package truelancer.noteapp.noteapp.Adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
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
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import truelancer.noteapp.noteapp.Database.Note;
import truelancer.noteapp.noteapp.Database.Task;
import truelancer.noteapp.noteapp.MyApp;
import truelancer.noteapp.noteapp.NoteActivity;
import truelancer.noteapp.noteapp.R;

import static com.orm.SugarRecord.findById;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyView> {

    List<Note> notes;
    Activity activity;
    String timeStampString;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("h:mm dd-MM-yyyy");
    Context itemContext;
    String inout = "";




    public NoteAdapter(FragmentActivity activity, List<Note> not) {
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

        if(!MyApp.defaultTheme){
            holder.noteCardView.setCardBackgroundColor(itemContext.getResources().getColor(R.color.darker_card));
            holder.noteText.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledName.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.calledNumber.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.date_time.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.savedDetails.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.noOfTasks.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.call_text.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.contactId2.setTextColor(itemContext.getResources().getColor(R.color.white));
            holder.overflow.setColorFilter(itemContext.getResources().getColor(R.color.white));



        }


        if(notes.get(position).isSavedFromApp()){
            holder.state_of_call.setImageResource(R.drawable.ic_saved_from_app);
            inout="Saved From App";
        }else{
            if (!notes.get(position).isIncoming()) {

                inout = "Call To";
                holder.state_of_call.setImageResource(R.drawable.ic_outgoing);
            } else {
                holder.state_of_call.setImageResource(R.drawable.ic_incoming);
                inout = "Call By";
            }
        }
        String tsMilli = notes.get(position).getTsMilli();
        long tsLong = Long.parseLong(tsMilli);
        timeStampString = getDate(tsLong);


        holder.call_text.setText(inout);
        holder.date_time.setText("" + timeStampString);
        holder.noteText.setText(notes.get(position).getNote());
        holder.calledName.setText(notes.get(position).getCalledName());
        holder.calledNumber.setText(notes.get(position).getCalledNumber());

        List<Task> tasks=Task.findWithQuery(Task.class,"Select * from Task where note_id=?",notes.get(position).getId().toString());

        if(tasks.size()!=0){
            holder.noOfTasks.setText(tasks.size()+" Tasks");
        }else{
            holder.noOfTasks.setText("No Tasks");
        }



        holder.noteCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(activity, NoteActivity.class);
                intent.putExtra("noteText", notes.get(position).getNote());

                intent.putExtra("calledName", notes.get(position).getCalledName());
                intent.putExtra("calledNumber", notes.get(position).getCalledNumber());
                intent.putExtra("noteId",""+notes.get(position).getId());

                String income = "" + notes.get(position).isIncoming();
                intent.putExtra("incoming", income);

                String tsMilli = notes.get(position).getTsMilli();
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

        //  holder.checkBox.setChecked(false);

        holder.sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent intent=new Intent(activity, CalendarActivity.class);
               // activity.startActivity(intent);


                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType("vnd.android.cursor.item/event");

                Calendar cal = Calendar.getInstance();
                long startTime = cal.getTimeInMillis();
                long endTime = cal.getTimeInMillis() + 60 * 60 * 1000;

                intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTime);
                intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime);
                intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);

                intent.putExtra(CalendarContract.Events.TITLE, notes.get(position).getNote());
                intent.putExtra(CalendarContract.Events.RRULE, "FREQ=YEARLY");

                itemContext.startActivity(intent);

            }
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();


    }


    public class MyView extends RecyclerView.ViewHolder {
        private TextView  noteText, calledName, calledNumber, call_text, date_time,savedDetails,contactId1,contactId2,noOfTasks;
        private CardView noteCardView;
        private CheckBox checkBox;
        private ImageView state_of_call, overflow;
        private Button sync;

        public MyView(View itemView) {
            super(itemView);



            noteText = (TextView) itemView.findViewById(R.id.noteTitle);
            noteCardView = (CardView) itemView.findViewById(R.id.note_cardView);
            calledName = (TextView) itemView.findViewById(R.id.called_contactNote);
            calledNumber = (TextView) itemView.findViewById(R.id.called_numberNote);
            call_text = (TextView) itemView.findViewById(R.id.callText);
            state_of_call = (ImageView) itemView.findViewById(R.id.stateOfCallNote);
            date_time = (TextView) itemView.findViewById(R.id.date_time_txt);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
            sync=(Button)itemView.findViewById(R.id.calendarSync);
            savedDetails =(TextView)itemView.findViewById(R.id.saved_details);
            contactId2 =(TextView)itemView.findViewById(R.id.contact_id2Note);
            noOfTasks = (TextView)itemView.findViewById(R.id.nooftasks);

            itemContext = itemView.getContext();
        }
    }

    public String getDate(long milliSeconds) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return simpleDateFormat.format(calendar.getTime());
    }
}
