package truelancer.noteapp.noteapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import truelancer.noteapp.noteapp.Services.PopUpService;

/**
 * Created by Sarvesh Palav on 12/27/2017.
 */

public class FloatingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button addButton;
    private Button removeButton;
    private Button removeAllButtons;
    private Button toggleButton;
    private Button updateBadgeCount;
    private PopUpService popUpService;
    private boolean bound;
    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            PopUpService.LocalBinder binder = (PopUpService.LocalBinder) service;
            popUpService = binder.getService();
            bound = true;
            popUpService.minimize();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, PopUpService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        setupButtons();
    }

    private void setupButtons() {
        setContentView(R.layout.chat_head_activity);

        addButton = (Button) findViewById(R.id.add_head);
        removeButton = (Button) findViewById(R.id.remove_head);
        removeAllButtons = (Button) findViewById(R.id.remove_all_heads);
        toggleButton = (Button) findViewById(R.id.toggle_arrangement);
        updateBadgeCount = (Button) findViewById(R.id.update_badge_count);

        addButton.setOnClickListener(this);
        removeButton.setOnClickListener(this);
        removeAllButtons.setOnClickListener(this);
        toggleButton.setOnClickListener(this);
        updateBadgeCount.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (bound) {
            if (v == addButton) {
               // popUpService.addChatHead();
            } else if (v == removeButton) {
                popUpService.removeChatHead();
            } else if (v == removeAllButtons) {
                popUpService.removeAllChatHeads();
            } else if (v == toggleButton) {
                popUpService.toggleArrangement();
            } else if (v == updateBadgeCount) {
                popUpService.updateBadgeCount();
            }
        } else {
            Toast.makeText(this, "Service not bound", Toast.LENGTH_SHORT).show();
        }
    }

}