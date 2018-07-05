package truelancer.noteapp.noteapp;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.TaskAdapter;
import truelancer.noteapp.noteapp.Adapters.TaskDoneAdapter;
import truelancer.noteapp.noteapp.Database.Task;



public class NoteActivity extends AppCompatActivity {

    private TextView noteText1, dateTime,calledTemp,calledName,calledNumber;
    private TextInputLayout textInputLayout;
    private EditText addTaskEditText;
    private View seprator;
    private FloatingActionButton addTaskButton;
    private RecyclerView mRecyclerView1,mRecyclerView2;
    TaskAdapter mAdapter1;
    TaskDoneAdapter mAdapter2;
    private ConstraintLayout layout;
    private ArrayList<Task> taskNotDone = new ArrayList<>();
    private ArrayList<Task>taskDone=new ArrayList<>();
    private String noteTimeStamp;
    private  String noteId;

    public NoteActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        noteText1 = (TextView) findViewById(R.id.note_id);
        calledTemp=(TextView)findViewById(R.id.calledTemp);
        calledName=(TextView)findViewById(R.id.called_name);
        calledNumber=(TextView)findViewById(R.id.called_number);
        dateTime=(TextView)findViewById(R.id.date_time_text);
        addTaskEditText = (EditText) findViewById(R.id.editTextTask);
        addTaskButton = (FloatingActionButton) findViewById(R.id.add_task_button);
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recyclerViewNoteTask1);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewNoteTask2);
        textInputLayout=(TextInputLayout)findViewById(R.id.textInputLayout);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        seprator = (View) findViewById(R.id.seprator);


        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        mRecyclerView1.setLayoutManager(layoutManager1);
        RecyclerView.LayoutManager layoutManager2=new LinearLayoutManager(getApplicationContext());
        mRecyclerView2.setLayoutManager(layoutManager2);


        if (MyApp.nightMode) {
            noteText1.setTextColor(getResources().getColor(R.color.white));
            mRecyclerView1.setBackgroundColor(getResources().getColor(R.color.dark));
            layout.setBackgroundColor(getResources().getColor(R.color.dark));
            addTaskEditText.setTextColor(getResources().getColor(R.color.white));
            addTaskEditText.setHintTextColor(getResources().getColor(R.color.white));
            seprator.setBackgroundColor(getResources().getColor(R.color.white));
        }


        noteText1.setText(getIntent().getStringExtra("noteText"));

        String temp = getIntent().getStringExtra("calledName") + "\n" + getIntent().getStringExtra("calledNumber")
                + "\n" + getIntent().getStringExtra("incoming")
                + "\n" + getIntent().getStringExtra("timestamp");

        // moreD.setText(""+temp);
        String calledName1=getIntent().getStringExtra("calledName");
        String calledNo=getIntent().getStringExtra("calledNumber");
        String date_time=getIntent().getStringExtra("timestamp");
        String calledTemp1=getIntent().getStringExtra("incoming");
        calledName.setText(calledName1);
        calledNumber.setText(calledNo);
        dateTime.setText(date_time);
        calledTemp.setText(calledTemp1);

        noteId = getIntent().getStringExtra("noteId");
        noteTimeStamp = getIntent().getStringExtra("noteTimeStamp");


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(addTaskEditText.getText().toString())) {
                    textInputLayout.setError("No Task Entered");
                } else {
                    Task task = new Task(addTaskEditText.getText().toString().trim(), noteId, false,noteTimeStamp);
                    task.save();
                    addTaskEditText.setText(null);
                    getTask();
                }


            }
        });


        getTask();


    }


    @Subscribe
    public void onEvent(EventB event) {
        if (event.getMessage().equals("updateTask")) {
            getTask();
        }
    }


    public void getTask() {
        taskDone.clear();
        taskNotDone.clear();
        List<Task> tasks = Task.findWithQuery(Task.class, "Select * from Task where note_id=?", noteId);
        for (Task task : tasks) {

            if (!task.isDone) {
                taskNotDone.add(task);
            }
            else{
                taskDone.add(task);
            }

        }
        mAdapter1 = new TaskAdapter(NoteActivity.this, taskNotDone);
        mRecyclerView1.setAdapter(mAdapter1);
        mAdapter2=new TaskDoneAdapter(NoteActivity.this,taskDone);
        mRecyclerView2.setAdapter(mAdapter2);

    }
}
