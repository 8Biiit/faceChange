package com.example.a10060.fakeface;


import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {


    private static final int TAKE_PHOTO_REQUEST_ONE = 111;
    private static final int TAKE_PHOTO_REQUEST_TWO = 222;
    private ImageButton btn_camera , btn_photo;
    private Button btn_upload;

    private ImageView iv_image;
    private Uri imageUri; //图片路径
    private String fileName; //图片名称
    private File picture;

    Service service;
    public static String BASE_URL = "http://192.168.3.84:8000/";
    String filePath;
    String mCurrentPhotoPath;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        btn_photo = (ImageButton) findViewById(R.id.btn_photo);
        btn_camera = (ImageButton) findViewById(R.id.btn_camera);
        iv_image = (ImageView) findViewById(R.id.iv_image);
        btn_upload = (Button) findViewById(R.id.btn_upload);



        btn_photo.setOnClickListener(new View.OnClickListener() {
            //相册按钮点击事件
            @Override
            public void onClick(View view) {
                pickImageFromAlbum();
            }
        });


        btn_camera.setOnClickListener(new View.OnClickListener() {
            //拍照按钮点击事件

            @Override
            public void onClick(View view) {

                dispatchTakePictureIntent();

            }
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (filePath != null) {
                    //imageUpload(filePath);
                    Intent intent=new Intent(MainActivity.this, ShowActivity.class);
                    intent.putExtra("id", imageUri.toString());
                    intent.putExtra("filepath",filePath);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Image not selected!", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    public void pickImageFromAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_ONE );

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        switch (requestCode) {
            case TAKE_PHOTO_REQUEST_ONE :  //如果选择相册
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "点击取消从相册选择", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    imageUri = data.getData();
                    filePath = getPath(imageUri);
                    Log.d("imageUri", imageUri.toString());
                    Log.d("filePath", filePath);

                    iv_image.setImageURI(imageUri);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case TAKE_PHOTO_REQUEST_TWO:   //如果选择拍照
                if (resultCode == RESULT_CANCELED) {
                    delteImageUri(MainActivity.this, imageUri);
                    return;
                }
              //广播刷新相册
                Intent intentBc = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                intentBc.setData(imageUri);
                this.sendBroadcast(intentBc);

                try
                {
                    Bitmap bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    iv_image.setImageBitmap(bitmap);
                    Log.d("imageUri", imageUri.toString());
                    Log.d("filePath", filePath);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }


                break;

            default:
                break;

        }


    }


    public static void delteImageUri(Context context, Uri uri) {
        context.getContentResolver().delete(uri, null, null);

    }


    /**
     * 获取压缩图片的options
     *
     * @return
     */
    public static BitmapFactory.Options getOptions(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 4;      //此项参数可以根据需求进行计算
        options.inJustDecodeBounds = false;

        return options;
    }


    private String getPath(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    //保存照片

    private File createImageFile() throws IOException {
     // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        filePath = mCurrentPhotoPath;
        Log.d("ImagePath", mCurrentPhotoPath );

        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
         // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                imageUri= FileProvider.getUriForFile(this,
                        "com.example.a10060.fakeface",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//指定图片输出地址
                startActivityForResult(takePictureIntent, TAKE_PHOTO_REQUEST_TWO );
            }
        }
    }











}
