package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class emailActivity extends AppCompatActivity {
    TextView emailContactName, emailId, moreD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        emailContactName = (TextView) findViewById(R.id.email_contactName);
        emailId = (TextView) findViewById(R.id.email_id);
        moreD = (TextView) findViewById(R.id.calledtemp);

        emailContactName.setText(getIntent().getStringExtra("emailContactName"));
        emailId.setText(getIntent().getStringExtra("emailId"));

        String temp = getIntent().getStringExtra("calledName") +"\n"+ getIntent().getStringExtra("calledNumber")
                + "\n" +getIntent().getStringExtra("incoming")
                +"\n"+getIntent().getStringExtra("timestamp");

        moreD.setText(""+temp);
    }
}
