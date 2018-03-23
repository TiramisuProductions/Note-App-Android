package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class contactActivity extends AppCompatActivity {

    TextView contactName, contactNo, moreD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        contactName = (TextView) findViewById(R.id.contact_name);
        contactNo = (TextView) findViewById(R.id.contact_no);
        moreD = (TextView) findViewById(R.id.temp);

        contactName.setText(getIntent().getStringExtra("contactName"));
        contactNo.setText(getIntent().getStringExtra("contactNo"));

        String temp1 = getIntent().getStringExtra("calledName") + "\n"
                + getIntent().getStringExtra("calledNumber") + "\n"
                + getIntent().getStringExtra("incoming")
                +getIntent().getStringExtra("timestamp");

        moreD.setText("" + temp1);


    }


}
