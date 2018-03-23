package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class noteActivity extends AppCompatActivity {

    private TextView noteContactName, noteText1, moreD;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        noteContactName = (TextView) findViewById(R.id.note_contactName);
        noteText1 = (TextView) findViewById(R.id.note_id);
        moreD = (TextView) findViewById(R.id.calledtemp);

        noteContactName.setText(getIntent().getStringExtra("noteContactName"));
        noteText1.setText(getIntent().getStringExtra("noteText"));

        String temp = getIntent().getStringExtra("calledName") +"\n"+ getIntent().getStringExtra("calledNumber")
                + "\n" +getIntent().getStringExtra("incoming")
                +"\n"+getIntent().getStringExtra("timestamp");

        moreD.setText(""+temp);
    }
}
