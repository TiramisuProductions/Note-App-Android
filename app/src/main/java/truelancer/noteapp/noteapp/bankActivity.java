package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class bankActivity extends AppCompatActivity {
    private TextView bankContactName, accountNo, ifscCode, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        bankContactName = (TextView) findViewById(R.id.bank_contactName);
        accountNo = (TextView) findViewById(R.id.account_no);
        ifscCode = (TextView) findViewById(R.id.ifsc_code);

        temp = (TextView) findViewById(R.id.tempB);

        bankContactName.setText(getIntent().getStringExtra("bankContactName"));
        accountNo.setText(getIntent().getStringExtra("accountNo"));
        ifscCode.setText(getIntent().getStringExtra("ifscCode"));

        String temp1 = getIntent().getStringExtra("calledName") + "\n" + getIntent().getStringExtra("calledNumber")
                + "\n" + getIntent().getStringExtra("incoming")
                + "\n" + getIntent().getStringExtra("timestamp");

        temp.setText("" + temp1);
    }
}
