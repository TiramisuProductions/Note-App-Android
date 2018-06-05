package truelancer.noteapp.noteapp;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import truelancer.noteapp.noteapp.Adapters.TaskAdapter;
import truelancer.noteapp.noteapp.Database.Task;



public class NoteActivity extends AppCompatActivity {

    private TextView noteText1, moreD;
    private EditText addTaskEditText;
    private View seprator;
    private FloatingActionButton addTaskButton;
    private RecyclerView mRecyclerView1,mRecyclerView2;
    private TaskAdapter mAdapter1,mAdapter2;
    private ConstraintLayout layout;
    private ArrayList<Task> taskDone = new ArrayList<>();
    // GoogleAccountCredential mCredential;

    String noteId;

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
        // moreD = (TextView) findViewById(R.id.calledtemp);
        addTaskEditText = (EditText) findViewById(R.id.editTextTask);
        addTaskButton = (FloatingActionButton) findViewById(R.id.add_task_button);
        mRecyclerView1 = (RecyclerView) findViewById(R.id.recyclerViewNoteTask1);
        mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewNoteTask2);

        layout = (ConstraintLayout) findViewById(R.id.layout);
        seprator = (View) findViewById(R.id.seprator);


        RecyclerView.LayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());


        mRecyclerView1.setLayoutManager(layoutManager1);


        if (!MyApp.defaultTheme) {
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

        noteId = getIntent().getStringExtra("noteId");


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(addTaskEditText.getText().toString())) {
                    addTaskEditText.setError("No Task Entered");
                } else {
                    Task task = new Task(addTaskEditText.getText().toString().trim(), noteId, false);
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

        List<Task> tasks = Task.findWithQuery(Task.class, "Select * from Task where note_id=?", noteId);

        for (Task task : tasks) {
            if (task.isDone) {
                taskDone.add(task);
            }

        }

        for (Task task : tasks) {
            if (!task.isDone) {
                taskDone.add(task);
            }

        }


        mAdapter1 = new TaskAdapter(NoteActivity.this, taskDone);
        mRecyclerView1.setAdapter(mAdapter1);
        mAdapter2 = new TaskAdapter(NoteActivity.this, taskDone);
        mRecyclerView2.setAdapter(mAdapter2);

    }
}
