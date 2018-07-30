package com.example.shreyanshjain.ekta.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shreyanshjain.ekta.R;
import com.example.shreyanshjain.ekta.models.NotificationList;

import java.util.ArrayList;

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
                                                    "Longitude: " + notificationList.get(position).getLocation().getLongitude());
        ((ViewHolder)holder).date_time.setText("date_time");
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
