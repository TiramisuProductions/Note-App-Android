package truelancer.noteapp.noteapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import truelancer.noteapp.noteapp.Database.Contact;

public class ContactForm extends AppCompatActivity {


    @BindView(R.id.contactname)
    EditText contactNameEditText;
    @BindView(R.id.contactnumber)
    EditText contactNumberEditText;
    @BindView(R.id.done)
    Button done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_form);
        ButterKnife.bind(this);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Contact contact = new Contact(contactNameEditText.getText().toString(),contactNumberEditText.getText().toString());
                //  contact.save();


            }
        });
    }


    @Override
    public void setContentView(View view) {
        super.setContentView(view);

        FontChangeCrawler fontChanger = new FontChangeCrawler(getAssets(), "font.ttf");
        fontChanger.replaceFonts((ViewGroup) this.findViewById(android.R.id.content));
    }
}
