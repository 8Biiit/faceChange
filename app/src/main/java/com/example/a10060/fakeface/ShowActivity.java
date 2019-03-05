package com.example.a10060.fakeface;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;


import com.roger.catloadinglibrary.CatLoadingView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class ShowActivity extends AppCompatActivity {


    private ImageView iv_image;
    private Uri imageUri; //图片路径
    private Button btn_origin , btn_happy, btn_sad, btn_surprise,btn_angry;
    CatLoadingView mView;


    Service service;
    String filePath;
    //public static String BASE_URL = "http://192.168.3.146:8000/";
    public static String BASE_URL = "http://218.193.183.249:8888/";
    public  int  FLAG;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        Intent intent=getIntent();


        mView = new CatLoadingView();




        imageUri = Uri.parse(intent.getStringExtra("id"));
        filePath = intent.getStringExtra("filepath");
        btn_origin = (Button) findViewById(R.id.btn_origin);
        btn_happy = (Button) findViewById(R.id.btn_happy);
        btn_sad = (Button) findViewById(R.id.btn_sad);
        btn_surprise = (Button) findViewById(R.id.btn_surprise);
        btn_angry = (Button) findViewById(R.id.btn_angry);



        iv_image = (ImageView) findViewById(R.id.imageView);
        iv_image.setImageURI(imageUri);


        btn_origin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Origin image selected", Toast.LENGTH_LONG).show();
                iv_image.setImageURI(imageUri);


            }
        });

        btn_happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Happy image selected", Toast.LENGTH_LONG).show();
                mView.show(getSupportFragmentManager(), "");
                mView.setCancelable(false);


                FLAG = 1;
                imageUpload(filePath);



            }
        });

        btn_sad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Sad image selected", Toast.LENGTH_LONG).show();

                mView.show(getSupportFragmentManager(), "");
                mView.setCancelable(false);
                FLAG = 2;
                imageUpload(filePath );


            }
        });

        btn_surprise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Surprise image selected", Toast.LENGTH_LONG).show();
                mView.show(getSupportFragmentManager(), "");
                mView.setCancelable(false);
                FLAG = 3;
                imageUpload(filePath);


            }
        });

        btn_angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Angry image selected", Toast.LENGTH_LONG).show();

                mView.show(getSupportFragmentManager(), "");
                mView.setCancelable(false);
                FLAG = 4;
                imageUpload(filePath);
            }
        });


    }


    //上传图片

    private void imageUpload(final String imagePath  ){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();


        // Change base URL to your upload server URL.
        service = new Retrofit.Builder().baseUrl(BASE_URL).client(client).build().create(Service.class);

        File file = new File(imagePath);

        RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("face", file.getName(), reqFile);



        switch(FLAG){
            case 1:
                retrofit2.Call<okhttp3.ResponseBody> req1 = service.happy_image(body);
                req1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        boolean FileDownloaded = DownloadImage(response.body());
                        Log.d("onResponse", "Image is downloaded and saved ? " + FileDownloaded);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;

            case 2:
                retrofit2.Call<okhttp3.ResponseBody> req2 = service.sad_image(body);
                req2.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        boolean FileDownloaded = DownloadImage(response.body());
                        Log.d("onResponse", "Image is downloaded and saved ? " + FileDownloaded);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;

            case 3:
                retrofit2.Call<okhttp3.ResponseBody> req3 = service.surprise_image(body);
                req3.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        boolean FileDownloaded = DownloadImage(response.body());
                        Log.d("onResponse", "Image is downloaded and saved ? " + FileDownloaded);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;

            case 4:
                retrofit2.Call<okhttp3.ResponseBody> req4 = service.angry_image(body);
                req4.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                        boolean FileDownloaded = DownloadImage(response.body());
                        Log.d("onResponse", "Image is downloaded and saved ? " + FileDownloaded);
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        t.printStackTrace();
                    }
                });
                break;



        }






    }






    private boolean DownloadImage(ResponseBody body) {
        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = body.byteStream();
                out = new FileOutputStream(getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            }
            catch (IOException e) {
                Log.d("DownloadImage",e.toString());
                return false;
            }
            finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            int width, height;
            Bitmap bMap = BitmapFactory.decodeFile(getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
            width = bMap.getWidth();
            height = bMap.getHeight();
            Bitmap bMap2 = Bitmap.createScaledBitmap(bMap, width, height, false);

            iv_image.setImageBitmap(bMap2);
            mView.onStop();

            return true;

        } catch (IOException e) {
            Log.d("DownloadImage",e.toString());
            return false;
        }
    }




}
