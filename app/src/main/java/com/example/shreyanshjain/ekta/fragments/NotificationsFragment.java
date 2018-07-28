package com.example.shreyanshjain.ekta.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.shreyanshjain.ekta.R;
import com.example.shreyanshjain.ekta.models.NotificationList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class NotificationsFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.notify_recycler_view)
    RecyclerView notifyRecycler;

    DatabaseReference mDatabaseReference;

    ValueEventListener mValueEventListener;

    ArrayList<NotificationList> notificationList;
    FirebaseAuth mAuth;

    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(view);

        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        mDatabaseReference.child("Notification").child("flag").setValue("true");

        /* TODO: Get a list of all the Notifications sent to a particular user by its user id
         */

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                DataSnapshot dataSnapshot1 = dataSnapshot.child("Notification").child(mAuth.getCurrentUser().getUid());

                for(DataSnapshot data: dataSnapshot1.getChildren()){
                    // TODO: Solve null pointer exception in notificationList.add()
                    NotificationList notification = data.getValue(NotificationList.class);
                    notificationList.add(notification);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        mDatabaseReference.addValueEventListener(mValueEventListener);

        return view;
    }

}
