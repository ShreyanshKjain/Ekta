package com.example.shreyanshjain.ekta;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class VideoUploading extends AppCompatActivity {

    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_uploading);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        final Uri file = Uri.fromFile(new File("/Internal storage/DCIM/Camera/sample.mp4"));
        final StorageReference reference = mStorageRef.child("videos/sample.mp4");

        Button upload = findViewById(R.id.upload);
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reference.putFile(file)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                Task<Uri> download = reference.getDownloadUrl();
                                Log.d("Download Url",downloadUrl.toString());
                                Log.d("Download",download.toString());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(this,"Video upload failed",Toast.LENGTH_SHORT).show();
                                Log.e("Video Upload Error", "Video upload failed");
                            }
                        });
            }
        });

    }
}
