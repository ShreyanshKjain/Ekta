package com.example.shreyanshjain.ekta.fragments;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shreyanshjain.ekta.BuildConfig;
import com.example.shreyanshjain.ekta.MainActivity;
import com.example.shreyanshjain.ekta.R;
import com.example.shreyanshjain.ekta.app.Config;
import com.example.shreyanshjain.ekta.models.Contacts;
import com.example.shreyanshjain.ekta.models.LocationInfo;
import com.example.shreyanshjain.ekta.models.Users;
import com.example.shreyanshjain.ekta.service.RecorderService;
import com.example.shreyanshjain.ekta.utils.NotificationUtils;
import com.example.shreyanshjain.ekta.utils.ObjectSerializer;
import com.example.shreyanshjain.ekta.utils.SmsHelper;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.OnClick;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MainPageFragment extends android.support.v4.app.Fragment {

    public MainPageFragment() {
        // Required empty public constructor
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    TextView txtLocationResult, txtUpdatedOn, txtMessage;

    Button btnStartUpdates, btnStopUpdates,btnCheck;

    SharedPreferences sharedPreferences;

    // location last updated time
    private String mLastUpdateTime,mLastUpdateDate;

    // location updates interval - 10sec
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private static final int RC_SIGN_IN = 1;
    private static final int SMS_PERMISSION_CODE = 0;

    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    //Bunch of Firebase objects
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthStateListener;
    ValueEventListener mValueEventListener;
    DatabaseReference mDatabaseReference;

    private BroadcastReceiver mRegistrationBroadcastReceiver;

    List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.PhoneBuilder().build());

    // boolean flag to toggle the ui
    private Boolean mRequestingLocationUpdates;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main_page, container, false);

//        ButterKnife.bind(view);

        txtLocationResult = view.findViewById(R.id.location_result);
        txtMessage = view.findViewById(R.id.txt_push_message);
        txtUpdatedOn = view.findViewById(R.id.updated_on);

        btnStartUpdates = view.findViewById(R.id.btn_start_location_updates);
        btnStopUpdates = view.findViewById(R.id.btn_stop_location_updates);
        btnCheck = view.findViewById(R.id.btn_check);

        // initialize the necessary libraries
        init();

        btnStartUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationButtonClick();
            }
        });

        btnStopUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationButtonClick();
            }
        });

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCheckSMS();
            }
        });
//         restore the values from saved instance state
        restoreValuesFromBundle(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                //checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                }
                else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    final String lat = message.substring(11,21);
                    final String lng = message.substring(37);
                    Toast.makeText(getContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                    txtMessage.setText("Lat:"+lat+ "\nLong:" + lng);

                    txtMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                                    Uri.parse("http://maps.google.com/maps?saddr="+mCurrentLocation.getLatitude()+","+mCurrentLocation.getLongitude()+"&daddr="+lat+","+lng ));
//                            startActivity(intent);
                            Uri locationUri = Uri.parse("" +
                                    "google.navigation:q="+lat+","+lng);
                            Intent resultIntent = new Intent(Intent.ACTION_VIEW,locationUri);
                            resultIntent.setPackage("com.google.android.apps.maps");
//                            if (resultIntent.resolveActivity(getPackageManager()) != null) {
                                startActivity(resultIntent);
//                            }
                        }
                    });
                }
            }
        };

        return view;
    }

    // Fetches reg id from shared preferences
    // and displays on the screen

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        firebaseAuth();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mSettingsClient = LocationServices.getSettingsClient(getContext());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                mLastUpdateDate = DateFormat.getDateInstance().format(new Date());
                Toast.makeText(getContext(), "" + mCurrentLocation.getTime(), Toast.LENGTH_SHORT).show();

                updateLocationUI();
            }
        };

        mRequestingLocationUpdates = false;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        sharedPreferences = getActivity().getSharedPreferences("com.example.shreyanshjain.ekta",Context.MODE_PRIVATE);
    }

    /**
     * Restoring values from saved instance state
     */
    private void restoreValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("is_requesting_updates")) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean("is_requesting_updates");
            }

            if (savedInstanceState.containsKey("last_known_location")) {
                mCurrentLocation = savedInstanceState.getParcelable("last_known_location");
            }

            if (savedInstanceState.containsKey("last_updated_on")) {
                mLastUpdateTime = savedInstanceState.getString("last_updated_on");
            }
        }
        updateLocationUI();
    }

    /**
     * Update the UI displaying the location data
     * and toggling the buttons
     */
    private void updateLocationUI() {
        if (mCurrentLocation != null) {
            txtLocationResult.setText(
                    "Lat: " + mCurrentLocation.getLatitude() + ", " +
                            "Lng: " + mCurrentLocation.getLongitude());

            firebaseData();
            // giving a blink animation on TextView
            txtLocationResult.setAlpha(0);
            txtLocationResult.animate().alpha(1).setDuration(300);

            // location last updated time
            txtUpdatedOn.setText("Last updated on: " + mLastUpdateTime);
        }

        toggleButtons();
    }

    public void firebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
//                    Toast.makeText(getContext(), "You are now signed in", Toast.LENGTH_SHORT).show();
                    Log.d("Firebase Auth","You are now signed in");
                } else {
                    startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .setAvailableProviders(providers)
                            .build(), RC_SIGN_IN);
                }
            }
        };
    }

//    public void signOut()
//    {
//        AuthUI.getInstance().signOut(getActivity().getApplicationContext());
//    }
    // Code to sign out the user
//        AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                startActivity();   // Go back to sign in activity'
//            }
//        });


    public void firebaseData() {

        LocationInfo loc = new LocationInfo(mCurrentLocation.getLatitude()
                ,mCurrentLocation.getLongitude());
        String token = FirebaseInstanceId.getInstance().getToken();
        String uid = mAuth.getCurrentUser().getUid();
        Users user = new Users(uid,loc,token,true);

        mDatabaseReference.child("Users")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(user);

//        HashMap<String,String> token = new HashMap<>();
//        token.put("Token ID", FirebaseInstanceId.getInstance().getToken());
//        mDatabaseReference.child(mAuth.getCurrentUser().getUid()).setValue(token);

        attachDatabaseReadListener();
    }

    public void attachDatabaseReadListener() {
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Users users = dataSnapshot.child("Users")
                            .child(mAuth.getCurrentUser().getUid())
                            .getValue(Users.class);
                    Log.i("Latitude", "" + users.getLocation().getLatitude());

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(users.getLocation().getTimestamp());

                    Log.i("Date and Time", "" + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(calendar.getTime()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("Firebase Cancelled", databaseError.getMessage());
                }
            };
        }
        mDatabaseReference.addValueEventListener(mValueEventListener);
    }

    public void detachDatabaseReadListener() {
        if (mValueEventListener != null)
        {
            mDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("is_requesting_updates", mRequestingLocationUpdates);
        outState.putParcelable("last_known_location", mCurrentLocation);
        outState.putString("last_updated_on", mLastUpdateTime);
    }

    private void toggleButtons() {
        if (mRequestingLocationUpdates) {
            btnStartUpdates.setEnabled(false);
            btnStopUpdates.setEnabled(true);
            btnStartUpdates.setAlpha(0.5f);
            btnStopUpdates.setAlpha(1f);
        } else {
            btnStartUpdates.setEnabled(true);
            btnStopUpdates.setEnabled(false);
            btnStartUpdates.setAlpha(1f);
            btnStopUpdates.setAlpha(0.5f);
        }
    }

    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        if(btnStartUpdates.isEnabled()) {
                            Log.i(TAG, "All location settings are satisfied.");

                            Toast.makeText(getContext(), "Started location updates!", Toast.LENGTH_SHORT).show();

                            //noinspection MissingPermission
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                    mLocationCallback, Looper.myLooper());

                            updateLocationUI();
                        }
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

//                                Toast.makeText(, errorMessage, Toast.LENGTH_LONG).show();
                        }
                        updateLocationUI();
                    }
                });
    }

    @OnClick(R.id.btn_start_location_updates)
    public void startLocationButtonClick() {

        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        mRequestingLocationUpdates = true;
                        startLocationUpdates();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanentlystop
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @OnClick(R.id.btn_stop_location_updates)
    public void stopLocationButtonClick() {
        mRequestingLocationUpdates = false;
        mDatabaseReference.child("Users")
                .child(mAuth.getCurrentUser().getUid())
                .child("flag")
                .setValue(false);
        stopLocationUpdates();


    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                        toggleButtons();
                    }
                });
    }

   /* public void showLastKnownLocation() {
        if (mCurrentLocation != null) {
            Toast.makeText(getApplicationContext(), "Lat: " + mCurrentLocation.getLatitude()
                    + ", Lng: " + mCurrentLocation.getLongitude(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Last known location is not available!", Toast.LENGTH_SHORT).show();
        }
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
            case RC_SIGN_IN :
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
//                        Toast.makeText(getContext(),"Signed IN",Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "Sign in Cancelled");
//                        Toast.makeText(getContext(),"Sign IN cancelled",Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        break;
                }
                break;
        }
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resuming location updates depending on button state and
        // allowed permissions
        if (mRequestingLocationUpdates && checkLocPermissions()) {
//            startLocationUpdates();
            startLocationButtonClick();
        }
        mAuth.addAuthStateListener(mAuthStateListener);

        // register FCM registration complete receiver
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getActivity().getApplicationContext());
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();

        if (mRequestingLocationUpdates) {
            // pausing location updates
            stopLocationButtonClick();
        }

        mAuth.removeAuthStateListener(mAuthStateListener);
        detachDatabaseReadListener();
    }

    private boolean checkLocPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //SMS Sending
    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }

    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasValidPreConditions(String phone) {
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
            return false;
        }

        if (!SmsHelper.isValidPhoneNumber(phone)) {
            Log.e("SMS Error","Wrong Phone Number");
            return false;
        }
        return true;
    }

    @OnClick(R.id.btn_check)
    public void sendCheckSMS()
    {
        Log.i("Check SMS","clicked");
        ArrayList<String > contacts = new ArrayList<>();

        try {

            contacts = (ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("Contacts",ObjectSerializer.serialize(new ArrayList<Contacts>())));

        } catch (IOException e) {
            e.printStackTrace();
        }

        for(String con : contacts)
        {

            Log.d("Contact",con);
            if(!hasValidPreConditions(con))
                return;

            String sms = "Hi " + ",\n Please keep checking on me after every 10 mins. Location : "
                    + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude();
            SmsHelper.sendDebugSms(con, sms);
            Log.i("SMS Sending",sms);

        }
    }
}
