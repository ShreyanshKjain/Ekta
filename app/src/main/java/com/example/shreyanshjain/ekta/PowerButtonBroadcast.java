package com.example.shreyanshjain.ekta;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class PowerButtonBroadcast extends BroadcastReceiver {

    public static int count = 0;

    /*
        TODO: Add a background service
     */

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("Broadcast","In broadcast receiver");
        if (Intent.ACTION_SCREEN_ON.equals(intent.getAction()) || Intent.ACTION_SCREEN_OFF.equals(intent.getAction()))
        {
            Log.d("In Broadcast class", "count incremented = " + count);
            count++;
            if(count == 5)
            {
                count = 0;
//                Toast.makeText(context, "MAIN ACTIVITY IS BEING CALLED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, Main2Activity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(i);
            }
        }
    }
}
