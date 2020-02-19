package com.example.lat_flow_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import java.io.IOException;

public class Image_Analysis_Activity extends AppCompatActivity {

    private Button uploadbtn;
    private Button virusbtn;
    private Button analyzebtn;
    private ImageView imageview;
    private Bitmap bmp;
    private int GALLERY = 1, CAMERA = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE};
    private boolean haveInitializedPicture = false;
    private boolean haveChosenVirus = false;
    private String virus;

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__analysis_);
        verifyStoragePermissions(this);

        uploadbtn = (Button) findViewById(R.id.upload);
        uploadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        virusbtn = (Button) findViewById(R.id.chooseVirus);
        virusbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVirusDialog();
            }
        });

        analyzebtn = (Button) findViewById(R.id.analyze);
        analyzebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPicResDialog();
            }
        });
    }

    // code heavily borrowed from https://demonuts.com/pick-image-gallery-camera-android/
    private void showPictureDialog() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery"};
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                        }
                    }
                });
        pictureDialog.show();

    }

    // code heavily borrowed from https://demonuts.com/pick-image-gallery-camera-android/
    private void showVirusDialog() {
        AlertDialog.Builder virusDialog = new AlertDialog.Builder(this);
        virusDialog.setTitle("Select Target");
        String[] pictureDialogItems = {
                "CMV",
                "BKV",
                "CXCL9"};
        virusDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                haveChosenVirus = true;
                                virus = "CMV";
                                Toast toast = Toast.makeText(getApplicationContext(),
                                        "You have chosen CMV.",
                                        Toast.LENGTH_SHORT);
                                LinearLayout toastLayout = (LinearLayout) toast.getView();
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                TextView toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
                                toastTV.setTextSize(20);
                                toast.show();
                                break;
                            case 1:
                                haveChosenVirus = true;
                                virus = "BKV";
                                toast = Toast.makeText(getApplicationContext(),
                                        "You have chosen BKV.",
                                        Toast.LENGTH_SHORT);
                                toastLayout = (LinearLayout) toast.getView();
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
                                toastTV.setTextSize(20);
                                toast.show();
                                break;
                            case 2:
                                haveChosenVirus = true;
                                virus = "CMV";
                                toast = Toast.makeText(getApplicationContext(),
                                        "You have chosen CXCL9.",
                                        Toast.LENGTH_SHORT);
                                toastLayout = (LinearLayout) toast.getView();
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
                                toastTV.setTextSize(20);
                                toast.show();
                                break;
                        }
                    }
                });
        virusDialog.show();
    }

    // code heavily borrowed from https://demonuts.com/pick-image-gallery-camera-android/
    private void showPicResDialog() {
        AlertDialog.Builder resolutionDialog = new AlertDialog.Builder(this);
        resolutionDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Use lower resolution (faster, recommended for photos above 50dpi)",
                "Use full resolution (may take ~1 minute)"};
        resolutionDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                double resizeFactor = 0.5; // just halves it
                                analyzePicAndSendToPython(resizeFactor);
                                break;
                            case 1:
                                resizeFactor = 1.0;
                                analyzePicAndSendToPython(resizeFactor);
                                break;
                        }
                    }
                });
        resolutionDialog.show();
    }

    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();

            try {
                // get the bitmap for internal data purposes
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                bmp = bitmap;

                // now change the image that is visible to the user
                imageview = findViewById(R.id.display);
                imageview.setImageBitmap(bitmap);
                haveInitializedPicture = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // resize because using the full image is unnecessary and SLOOOOOW
    private Bitmap resizeImage(Bitmap bitmap, double scale){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int scaleWidth = (int) Math.round(width * scale);
        int scaleHeight = (int) Math.round(height * scale);

        Bitmap resized = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);

        return resized;
    }

    private void analyzePicAndSendToPython(double resizeFactor) {
        if (!haveInitializedPicture) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please upload a picture.",
                    Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
            toastTV.setTextSize(20);
            toast.show();
        }
        else if (!haveChosenVirus) {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Please choose a target.",
                    Toast.LENGTH_LONG);
            LinearLayout toastLayout = (LinearLayout) toast.getView();
            toast.setGravity(Gravity.CENTER, 0, 0);
            TextView toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
            toastTV.setTextSize(20);
            toast.show();
        }
        else {
            int checkWidth = bmp.getWidth();
            int checkHeight = bmp.getHeight();
            if (checkWidth * checkHeight < 10000) {
                // display output message
                Toast toast = Toast.makeText(getApplicationContext(),
                        "ERROR. Please acquire image with higher resolution or choose full resolution option. Image should not be significantly below 40-50dpi.",
                        Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                toast.setGravity(Gravity.CENTER, 0, 0);
                TextView toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
                toastTV.setTextSize(20);
                toast.show();
            }
            else {
                Bitmap resized = resizeImage(bmp, resizeFactor);
                // using a short because don't need full 32 bit integers
                short[][][] rgbValues = new short[resized.getHeight()][resized.getWidth()][3];
                //get the ARGB value from each pixel of the image and store it into the array
                for (int i = 0; i < resized.getHeight(); i++) {
                    for (int j = 0; j < resized.getWidth(); j++) {
                        //This is a great opportunity to filter the ARGB values
                        int colour = resized.getPixel(j, i); // the most confusing thing ever
                        rgbValues[i][j][0] = (short) Color.red(colour);
                        rgbValues[i][j][1] = (short) Color.blue(colour);
                        rgbValues[i][j][2] = (short) Color.green(colour);
                    }
                }

                // "context" must be an Activity, Service or Application object from your app.
                if (!Python.isStarted()) {
                    Python.start(new AndroidPlatform(this));
                }

                // actually connect to python
                Python py = Python.getInstance();
                PyObject mod = py.getModule("analyze_image");
                PyObject helloWorldString = mod.callAttr("main", (Object) rgbValues, virus);
                String output = helloWorldString.toString();

                // display output message
                Toast toast = Toast.makeText(getApplicationContext(),
                        output,
                        Toast.LENGTH_LONG);
                LinearLayout toastLayout = (LinearLayout) toast.getView();
                toast.setGravity(Gravity.CENTER, 0, 0);
                TextView toastTV = (TextView) toastLayout.getChildAt(0); // RISKY!!!!
                toastTV.setTextSize(20);
                toast.show();
            }
        }
    }
}
