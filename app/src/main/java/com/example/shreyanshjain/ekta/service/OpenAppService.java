package com.example.shreyanshjain.ekta.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

import com.example.shreyanshjain.ekta.Main2Activity;

import static android.view.KeyEvent.KEYCODE_BACK;

public class OpenAppService extends IntentService implements View.OnKeyListener {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */

    static int count = 0;
    public OpenAppService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {


    }


    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KEYCODE_BACK)
        {
            count++;
            if(count == 3)
            {
                count = 0;
                Intent dialogIntent = new Intent(this,Main2Activity.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(dialogIntent);
                return true;
            }
        }
        return false;

    }
}
