package com.example.imagetransfertest;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    String selectedImagePath;
    Uri selectedImageUri;
    File selectedFile;
    Bitmap bitmap;

    private static final String postUrl = "http://54.180.8.235:5000/image";

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView imageview;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyStoragePermissions(this);

        imageview = (ImageView)findViewById(R.id.imageView);
    }

    public void connectServer(View v){
        Log.i("Data","Send server start 01");
        postRequest();

    }

    public void postRequest() {
        try{
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file",selectedFile.getName(), RequestBody.create(MultipartBody.FORM,selectedFile))
                    .build();

            Request request = new Request.Builder()
                    .url(postUrl + "/" + "7" + "/" + "21600212@handong.edu")
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Data","fail: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String server_response = response.body().string();
                    Log.i("Data","Response: " + server_response);
                }
            });
        }catch (Exception e){
            Log.i("androidTest","okhttp3 request exception: "+e.getMessage());
        }
    }

    // 출처: https://webnautes.tistory.com/1302
    public void selectImage(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GET_GALLERY_IMAGE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                // 선택한 이미지에서 비트맵 생성
                InputStream in = getContentResolver().openInputStream(data.getData());
                Bitmap img = BitmapFactory.decodeStream(in);
                selectedImageUri = data.getData();
                in.close();

                imageview.setImageURI(selectedImageUri);
                selectedFile = new File(getPath(selectedImageUri));
                selectedImagePath = getPath(selectedImageUri);
                EditText imgPath = findViewById(R.id.imgPath);
                imgPath.setText(selectedImagePath);
                Toast.makeText(getApplicationContext(), selectedImagePath, Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 출처: https://stackoverflow.com/questions/20322528/uploading-images-to-server-android
    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String imagePath = cursor.getString(column_index);
        Log.i("Data", imagePath);
        return imagePath;
    }

    //출처: https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android?page=1&tab=votes#tab-top
    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void receiveServer(View v){
        Log.i("Data","Receive Server Start");
        try{


            Request request = new Request.Builder()
                    .url(postUrl + "/" + "7" + "/" + "21600212@handong.edu")
                    .build();

            OkHttpClient client = new OkHttpClient();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.i("Data","fail: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    InputStream inputStream = response.body().byteStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);


                    Log.i("Data", "Response: " + response.body().string());
                }
            });

            imageview.setImageBitmap(bitmap);
        }catch (Exception e){
            Log.i("androidTest","okhttp3 request exception: "+e.getMessage());
        }

    }
}