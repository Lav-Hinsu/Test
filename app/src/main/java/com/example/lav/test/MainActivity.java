package com.example.lav.test;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {
    Button submitfile,selectfile;
    TextView textView;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    Uri pdfUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        selectfile=findViewById(R.id.selectfile);
        submitfile=findViewById(R.id.submitfile);
        textView=findViewById(R.id.textView);
        selectfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)
                {
                    selectpdf();
                }
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},9);
                    
                                        }
        });
        submitfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pdfUri!=null)
                    uploadFile(pdfUri);
                else
                    Toast.makeText(MainActivity.this,"Please Select A File",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void uploadFile(Uri pdfUri) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading..");
        progressDialog.setProgress(0);
        progressDialog.show();
        final String fileName=System.currentTimeMillis()+"";
        StorageReference storagereference = storage.getReference();
        storagereference.child("uploads").child(fileName).putFile(pdfUri)
            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                       Toast.makeText(getApplicationContext(),"Uploaded Succesfully!",Toast.LENGTH_SHORT).show();


                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Upload Failed"+e.getMessage(),Toast.LENGTH_SHORT).show();

                
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                 int CurrentProgress =(int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                 progressDialog.setProgress(CurrentProgress);
            }
        });
        
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
      if(requestCode==9 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
          selectpdf();
      }
      else
      {
          Toast.makeText(MainActivity.this,"Please Provide Storage Reading Permission",Toast.LENGTH_SHORT).show();
          
      }

    }

    private void selectpdf()
    {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,86);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==86&&resultCode==RESULT_OK&&data!=null)
        {
         pdfUri=data.getData();
         textView.setText("a File is Selected"+data.getData().getLastPathSegment());
         
        }
        else
            Toast.makeText(MainActivity.this,"Please Select a file",Toast.LENGTH_SHORT).show();
        }

}

