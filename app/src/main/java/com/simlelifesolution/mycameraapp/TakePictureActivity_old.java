package com.simlelifesolution.mycameraapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;

public class TakePictureActivity_old extends AppCompatActivity
{

    // This tag is used for error or debug log.
    private static final String TAG_LOG_TAKE_PICTURE = "TAKE_PICTURE";

    // This is the request code when start camera activity use implicit intent.
    public static final int REQUEST_CODE_TAKE_PICTURE = 1;

    // This imageview is used to show camera taken picture.
    private ImageView takePictureImageView;

    // This output image file uri is used by camera app to save taken picture.
    private Uri outputImgUri;

    // Save the camera taken picture in this folder.
    private File pictureSaveFolderPath;

    // Save imageview currently displayed picture index in all camera taken pictures..
    private int currentDisplayImageIndex = 0;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        Log.d("myCamTag", "oncreate:: "+ Build.VERSION.SDK_INT );

        setTitle("dev2qa.com - Android Take Picture Example");

        // Get this app's external cache directory, manipulate this directory in app do not need android os system permission check.
        // The cache folder is application specific, when the app is uninstalled it will be removed also.
     //  pictureSaveFolderPath = getExternalCacheDir();




        // Get the display camera taken picture imageview object.
        takePictureImageView = (ImageView)findViewById(R.id.take_picture_image_view);

        //region ...When you click the image view object, all the taken pictures will be shown one by one like slide.
      /*  takePictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(pictureSaveFolderPath!=null) {
                    // Get all camera taken pictures in picture save folder.
                    File imageFiles[] = pictureSaveFolderPath.listFiles();
                    if (imageFiles!=null)
                    {
                        // Get content resolver object.
                        ContentResolver contentResolver = getContentResolver();

                        int allImagesCount = imageFiles.length;

                        // If current display picture index is bigger than image count.
                        if(currentDisplayImageIndex >= allImagesCount-1)
                        {
                            currentDisplayImageIndex = 0;
                        }else
                        {
                            currentDisplayImageIndex++;
                        }

                        // Get to be displayed image file object.
                        File displayImageFile = imageFiles[currentDisplayImageIndex];

                        // Get display image Uri wrapped object.
                        Uri displayImageFileUri = getImageFileUriByOsVersion(displayImageFile);

                        try {
                            // Open display image input stream.
                            InputStream inputStream = contentResolver.openInputStream(displayImageFileUri);

                            // Decode the image input stream to a bitmap use BitmapFactory.
                            Bitmap pictureBitmap = BitmapFactory.decodeStream(inputStream);

                            // Set the image bitmap in the image view component to display it.
                            takePictureImageView.setImageBitmap(pictureBitmap);

                        }catch(FileNotFoundException ex) {
                            Log.e(TAG_LOG_TAKE_PICTURE, ex.getMessage(), ex);
                        }
                    }
                }
            }
        });*/

        //endregion

        // Get the take picture button object.
        Button takePictureButton = (Button)findViewById(R.id.take_picture_button);

        // When the button is clicked.
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    // Create a random image file name.
                  //  String imageFileName = "outputImage_" + System.currentTimeMillis() + ".png";

                    // Construct a output file to save camera taken picture temporary.
                 //  File outputImageFile = new File(pictureSaveFolderPath, imageFileName);

                   File outputImageFile = func_createFolder();
                    if(outputImageFile!=null) {

                        // If cached temporary file exist then delete it.
                       /* if (outputImageFile.exists()) {
                            outputImageFile.delete();
                        }*/

                        // Create a new temporary file.
                        //outputImageFile.createNewFile();

                        // Get the output image file Uri wrapper object.
                        outputImgUri = getImageFileUriByOsVersion(outputImageFile);

                        // Startup camera app.
                        // Create an implicit intent which require take picture action..
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Specify the output image uri for the camera app to save taken picture.
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputImgUri);
                        // Start the camera activity with the request code and waiting for the app process result.
                        startActivityForResult(cameraIntent, REQUEST_CODE_TAKE_PICTURE);
                    }
                    else{
                        Toast.makeText(TakePictureActivity_old.this, "Problem creating file",Toast.LENGTH_SHORT).show();}

                }catch(Exception ex)
                {
                    Log.e(TAG_LOG_TAKE_PICTURE, ex.getMessage(), ex);
                }
            }
        });
    }

    /* Get the file Uri object by android os version.
    *  return a Uri object. */
    private Uri getImageFileUriByOsVersion(File file)
    {
        Log.d("myCamTag", "getImageFileUriByOsVersion:: "+ Build.VERSION.SDK_INT );

        Uri ret = null;

        // Get output image unique resource identifier. This uri is used by camera app to save taken picture temporary.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
//region ...extra codes
            // /sdcard/ folder link to /storage/41B7-12F1 folder
            // so below code return /storage/41B7-12F1
            File externalStorageRootDir = Environment.getExternalStorageDirectory();

            // contextRootDir = /data/user/0/com.dev2qa.example/files in my Huawei mate 8.
            File contextRootDir = getFilesDir();

            // contextCacheDir = /data/user/0/com.dev2qa.example/cache in my Huawei mate 8.
            File contextCacheDir = getCacheDir();

            // For android os version bigger than or equal to 7.0 use FileProvider class.
            // Otherwise android os will throw FileUriExposedException.
            // Because the system considers it is unsafe to use local real path uri directly.
//endregion

            Context ctx = getApplicationContext();
         // ret = FileProvider.getUriForFile(ctx, "data/data/com.simlelifesolution.mycameraapp", file);
            ret = FileProvider.getUriForFile(ctx, BuildConfig.APPLICATION_ID + ".provider", file);

            Log.d("myCamTag", "Inside v>24:: "+ Build.VERSION.SDK_INT );


        }else
        {
            // For android os version less than 7.0 there are no safety issue,
            // So we can get the output image uri by file real local path directly.
            ret = Uri.fromFile(file);
            Log.d("myCamTag", "Inside old versions:: "+ Build.VERSION.SDK_INT );
        }

        Log.d("myCamTag", "URI:: "+ ret );

        return ret;
    }

    /* This method is used to process the result of camera app. It will be invoked after camera app return.
    It will show the camera taken picture in the image view component. */
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
            Log.e(TAG_LOG_TAKE_PICTURE, ex.getMessage(), ex);
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
        catch(Exception exp){Log.d("TAG_LOG_TAKE_PICTURE", ""+exp);}

         return outputImageFile;

    }



    class TestAsync extends AsyncTask<Void, Integer, Bitmap>
    {
        String TAG = getClass().getSimpleName();

        protected void onPreExecute (){
            super.onPreExecute();
            Log.d(TAG + " PreExceute","On pre Exceute......");
        }

        protected Bitmap doInBackground(Void...arg0) {
            Bitmap pictureBitmap=null;
            Log.d(TAG + " DoINBackGround","On doInBackground...");
                try {
                    ContentResolver contentResolver = getContentResolver();

                    // Use the content resolver to open camera taken image input stream through image uri.
                    InputStream inputStream = contentResolver.openInputStream(outputImgUri);

                    // Decode the image input stream to a bitmap use BitmapFactory.
                     pictureBitmap = BitmapFactory.decodeStream(inputStream);

                    // Set the camera taken image bitmap in the image view component to display.

                }
                catch(Exception ex) {}

            return pictureBitmap;
        }

        protected void onProgressUpdate(Integer...a){
            super.onProgressUpdate(a);
            Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);


        }

        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            Log.d(TAG + " onPostExecute", "" + result);

            takePictureImageView.setImageBitmap(result);
        }
    }

}
