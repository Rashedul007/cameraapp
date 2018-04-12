package com.simlelifesolution.mycameraapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class TakePictureActivity extends AppCompatActivity
{
    private static final String TAG_LOG_TAKE_PICTURE = "TAKE_PICTURE";
    public static final int REQUEST_CODE_TAKE_PICTURE = 1;
    private ImageView takePictureImageView;
    private Uri outputImgUri;
    private File pictureSaveFolderPath;
    private int currentDisplayImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        Log.d("myCamTag", "oncreate:: "+ Build.VERSION.SDK_INT );

        setTitle("dev2qa.com - Android Take Picture Example");


        takePictureImageView = (ImageView)findViewById(R.id.take_picture_image_view);

        Button takePictureButton = (Button)findViewById(R.id.take_picture_button);

        // When the button is clicked.
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   File outputImageFile = func_createFolder();
                    if(outputImageFile!=null) {

                        outputImgUri = getImageFileUriByOsVersion(outputImageFile);
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputImgUri);

                        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
                    }
                    else{
                        Toast.makeText(TakePictureActivity.this, "Problem creating file",Toast.LENGTH_SHORT).show();}

                }catch(Exception ex)
                {
                   String errmsg = "Insideoncreate()BtnClk:: \t"+ex;
                    Log.e(TAG_LOG_TAKE_PICTURE, errmsg);
                    Toast.makeText(TakePictureActivity.this, errmsg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private Uri getImageFileUriByOsVersion(File file)
    {
        Log.d("myCamTag", "getImageFileUriByOsVersion:: "+ Build.VERSION.SDK_INT );

        Uri ret = null;

        // Get output image unique resource identifier. This uri is used by camera app to save taken picture temporary.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            Context ctx = getApplicationContext();
         // ret = FileProvider.getUriForFile(ctx, "data/data/com.simlelifesolution.mycameraapp", file);
            ret = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file);

            Log.d("myCamTag", "Inside v>24:: "+ Build.VERSION.SDK_INT );


        }else
        {
            ret = Uri.fromFile(file);
            Log.d("myCamTag", "Inside old versions:: "+ Build.VERSION.SDK_INT );
        }

        Log.d("myCamTag", "URI:: "+ ret );

        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            // Process result for camera activity.
            if (requestCode == REQUEST_CODE_TAKE_PICTURE) {

                // If camera take picture success.
                if (resultCode == RESULT_OK) {

                    // Get content resolver.
                    ContentResolver contentResolver = getContentResolver();

                    // Use the content resolver to open camera taken image input stream through image uri.
                    InputStream inputStream = contentResolver.openInputStream(outputImgUri);

                    // Decode the image input stream to a bitmap use BitmapFactory.
                    Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);

                    // Set the camera taken image bitmap in the image view component to display.
                    takePictureImageView.setImageBitmap(pictureBitmap);

                    //new TestAsync().execute();

                }
            }
        }catch(Exception ex)
        {
            String errmsg = "inside onActivityResult:: \t"+ex;
            Log.e(TAG_LOG_TAKE_PICTURE, errmsg);
            Toast.makeText(TakePictureActivity.this, errmsg, Toast.LENGTH_SHORT).show();
        }
    }


    private File func_createFolder()
    {
        File outputImageFile = null;

      //  File mydir = Environment.getExternalStorageDirectory();
        String baseDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        String pathDir = baseDir + "/Android/data/com.simlelifesolution.mycameraapp/";

        /*if (!mydir.exists())
            {  if (!mydir.mkdirs()) { Log.d("myCamTag", "failed to create directory");
                                        return null;}
            }
*/
        String imageFileName = "colorAppImg_" + System.currentTimeMillis() + ".png";
//        outputImageFile = new File(mydir, imageFileName);
       // outputImageFile = new File(mydir+"/"+"news", imageFileName);

        outputImageFile = new File(pathDir + File.separator + imageFileName);
       // outputImageFile = new File(baseDir + File.separator + imageFileName);




        try {outputImageFile.createNewFile();}
        catch(Exception exp){
            String errmsg = "Inside func_CreateFolder():: \t"+exp;
            Log.d("TAG_LOG_TAKE_PICTURE", errmsg);
            Toast.makeText(TakePictureActivity.this, errmsg, Toast.LENGTH_SHORT).show();}

         return outputImageFile;

    }



}
