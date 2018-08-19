package com.example.shreyanshjain.ekta.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.shreyanshjain.ekta.R;
import com.example.shreyanshjain.ekta.adapters.NotificationAdapter;
import com.example.shreyanshjain.ekta.models.NotificationList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class NotificationsFragment extends android.support.v4.app.Fragment {

//    @BindView(R.id.notify_recycler_view)
    RecyclerView recyclerView;
    TextView noNotifications;
    DatabaseReference mDatabaseReference;

    ValueEventListener mValueEventListener;

    ArrayList<NotificationList> notificationList;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
//        ButterKnife.bind(view);

        recyclerView = view.findViewById(R.id.notify_recycler_view);
        noNotifications = view.findViewById(R.id.no_notifications);
        mAuth = FirebaseAuth.getInstance();

        notificationList = new ArrayList<>();
        notificationList.clear();

        /*
            TODO: Change the notification content when the user is changed
            TODO: Save the notification in SQLite database for offline access
            TODO: Add onClickListener() for every notification that will intent to Google Maps with received latitude and longitude passed in it
         */

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mDatabaseReference.child("Notification").child("flag").setValue("true");

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                DataSnapshot dataSnapshot1 = dataSnapshot.child("Notification").child(mAuth.getCurrentUser().getUid());
//                notificationList.clear();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                for (DataSnapshot data : children) {
                    NotificationList notification = data.getValue(NotificationList.class);
                    notificationList.add(notification);
                }
                setAdapter(notificationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.child("Notification").child(mAuth.getCurrentUser().getUid()).addValueEventListener(mValueEventListener);
//    }
//                else
//    {
//        recyclerView.setVisibility(View.GONE);
//        noNotifications.setVisibility(View.VISIBLE);
//    }
        return view;
    }

    void setAdapter(ArrayList<NotificationList> notificationList)
    {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        NotificationAdapter adapter = new NotificationAdapter(getContext(),notificationList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
