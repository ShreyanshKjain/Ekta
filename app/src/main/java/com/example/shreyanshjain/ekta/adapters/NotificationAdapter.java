package com.example.shreyanshjain.ekta.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shreyanshjain.ekta.R;
import com.example.shreyanshjain.ekta.models.NotificationList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class NotificationAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<NotificationList> notificationList;

    public NotificationAdapter(Context context, ArrayList<NotificationList> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ((ViewHolder)holder).heading.setText(notificationList.get(position).getFrom());
        ((ViewHolder)holder).sub_heading.setText("Latitude: " + notificationList.get(position).getLocation().getLatitude() +
                                                    "\nLongitude: " + notificationList.get(position).getLocation().getLongitude());
        /*
            TODO: Correct the time displayed in the notifications
         */
        Date date = new Date(notificationList.get(position).getTimestamp());
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("IST"));
        String dateFormatted = formatter.format(date);
        ((ViewHolder)holder).date_time.setText(dateFormatted);

        final double lat = notificationList.get(position).getLocation().getLatitude();
        final double lng = notificationList.get(position).getLocation().getLongitude();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri locationUri = Uri.parse("" +
                        "google.navigation:q="+lat+","+lng);
                Intent resultIntent = new Intent(Intent.ACTION_VIEW,locationUri);
                resultIntent.setPackage("com.google.android.apps.maps");
//                            if (resultIntent.resolveActivity(getPackageManager()) != null) {
                context.startActivity(resultIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView heading,sub_heading,date_time;
        public ViewHolder(View itemView) {
            super(itemView);

            heading = itemView.findViewById(R.id.heading);
            sub_heading = itemView.findViewById(R.id.sub_heading);
            date_time = itemView.findViewById(R.id.date_time);
        }
    }
}
