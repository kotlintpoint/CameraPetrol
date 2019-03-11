package com.example.admin.myapplication.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.myapplication.R;
import com.example.admin.myapplication.model.TimeLine;
import com.example.admin.myapplication.other.Const;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mlsdev.rximagepicker.RxImageConverters;
import com.mlsdev.rximagepicker.RxImagePicker;
import com.mlsdev.rximagepicker.Sources;

import net.alhazmy13.mediapicker.Video.VideoPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static java.security.AccessController.getContext;

public class NewTimeLineActivity extends AppCompatActivity {
    private static final int REQ_CAMERA_IMAGE = 100;
    Button btnUploadFile, btnSubmit, btnPickVideo;
    ImageView imageView;
    TextView tvVideoName;
    EditText etDescription;
    Context context;
    private StorageReference mStorageRef;
    private Bitmap bitmap;
    ProgressDialog pd;
    private FirebaseDatabase database;
    private ArrayList<String> mPaths;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time_line);
        database= FirebaseDatabase.getInstance();
        btnPickVideo=findViewById(R.id.btnPickVideo);
        tvVideoName=findViewById(R.id.tvVideoName);
        imageView=findViewById(R.id.imageView);
        etDescription=findViewById(R.id.etDescription);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        context=this;
        btnSubmit=findViewById(R.id.btnSubmit);
        btnUploadFile=findViewById(R.id.btnUploadFile);
        btnUploadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickerDialog();
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadDetails();
            }
        });
        btnPickVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new VideoPicker.Builder(NewTimeLineActivity.this)
                        .mode(VideoPicker.Mode.CAMERA_AND_GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .build();
            }
        });

    }

    private void uploadDetails() {
        if(bitmap!=null){
            uploadImage();
        }else if(mPaths!=null && mPaths.size()>0){
            try {
                uploadVideo();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context, "Pick at least one from video or image!!!", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadVideo() throws IOException {
        String fileName = System.currentTimeMillis() + "";
        StorageReference riversRef = mStorageRef.child("videos/" + fileName + ".mp4");
        pd = ProgressDialog.show(context, "", "");

        //InputStream inputStream=new FileInputStream(mPaths.get(0));
        File file=new File(mPaths.get(0));
        byte[] bytesArray = new byte[(int) file.length()];

        FileInputStream fis = new FileInputStream(file);
        fis.read(bytesArray); //read file into bytes[]
        fis.close();

        riversRef.putBytes(bytesArray)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                        saveOtherDetails(fileName);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                        pd.dismiss();
                        Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void uploadImage() {

            //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
            String fileName = System.currentTimeMillis() + "";
            StorageReference riversRef = mStorageRef.child("images/" + fileName + ".jpg");
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            pd = ProgressDialog.show(context, "", "");
            riversRef.putBytes(byteArray)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content
                            //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            pd.dismiss();
                            saveOtherDetails(fileName);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            pd.dismiss();
                            Toast.makeText(context, exception.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });

    }

    private void saveOtherDetails(String fileName) {
        DatabaseReference reference = database.getReference().child(Const.TIMELINE).push();
        TimeLine timeLine=new TimeLine();
        timeLine.setImage(fileName);
        timeLine.setDescription(etDescription.getText().toString());
        pd=ProgressDialog.show(context,"","");
        reference.setValue(timeLine).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showImagePickerDialog() {
        String[] imageUploadOptions={"Gallery","Camera"};
        new AlertDialog.Builder(context)
                .setItems(imageUploadOptions, (dialog, which) -> {
                    switch (which){
                        case 0:
                            pickImagesFromSource();
                            break;
                        case 1:
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, REQ_CAMERA_IMAGE);
                            break;
                    }
                })
                .create()
                .show();
    }

    private void pickImagesFromSource() {
        RxImagePicker.with(context).requestImage(Sources.GALLERY)
                .flatMap(new Function<Uri, ObservableSource<Bitmap>>() {
                    @Override
                    public ObservableSource<Bitmap> apply(@NonNull Uri uri) throws Exception {
                        return RxImageConverters.uriToBitmap(context, uri);
                    }
                }).subscribe(new Consumer<Bitmap>() {
            @Override
            public void accept(@NonNull Bitmap bmp) throws Exception {
                // Do something with Bitmap
                imageView.setImageBitmap(bmp);
                bitmap=bmp;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CAMERA_IMAGE && resultCode==RESULT_OK){
            //UtilityHelper.showLoading(context);
            //Uri filePath = Uri.parse(UtilityHelper.getOriginalImagePath(getActivity()));
            bitmap=(Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            //Uri filePath=saveImage(bitmap);

        }else if (requestCode == VideoPicker.VIDEO_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            mPaths =  data.getStringArrayListExtra(VideoPicker.EXTRA_VIDEO_PATH);

            //Your Code
        }
    }
}
