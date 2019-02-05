package com.example.hasith.canu;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TakePictureCamera extends AppCompatActivity {
    private static final int PERMISSION_CODE = 1000;
    ImageView profilPic;
    private Button imageUpdate;
    Uri image_uri;
    Button fultUpload;
    private Spinner fultInput;
    public static String faultId;
    static Bitmap bitmap;


    final static int RESULT_CODE = 456345;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture_camera);

//        fultUpload = (Button) findViewById(R.id.fultUpload);
        Button btnCamera = (Button) findViewById(R.id.btnCamera);
        profilPic = (ImageView) findViewById(R.id.imageView);
        imageUpdate = (Button) findViewById(R.id.fultUpload);
        fultInput = (Spinner) findViewById(R.id.spinner);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndroidVersion();
            }
        });

        imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fultsetupload(fultInput.getSelectedItem().toString());
            }
        });


    }

    private void fultsetupload(final String fult) {
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            String url = "http://209.97.176.164:3000/faults";
        final ProgressDialog dialog = new ProgressDialog(TakePictureCamera.this);
        dialog.setMessage("Loading...");
        dialog.show();
            final StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
//                        faultId = jsonObject.getString("createdFault");
                        faultId = jsonObject.getJSONObject("createdFault").getString("_id");

                        Toast.makeText(TakePictureCamera.this,faultId,Toast.LENGTH_LONG).show();
                        if(response!= null){
                            Intent intent = new Intent();
                            intent.putExtra("fault_id", faultId);
//                            intent.putExtra("image", bitmap.get);

                            setResult(RESULT_CODE, intent);
                            finish();
                            //startActivity(new Intent(TakePictureCamera.this,MainActivity.class));

                        }
//                        Toast.makeText(TakePictureCamera.this,faultId,Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(TakePictureCamera.this,error.toString(),Toast.LENGTH_LONG).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> map = new HashMap<>();
                    map.put("Content-Type", "application/json");
                    map.put("Accept", "application/json");
                    map.put("X-Requested-With", "XMLHttpRequest");
                    return map;
                }

//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> map = new HashMap<>();
//                map.put("email", email);
//                map.put("password", password);
//                map.put("remember_me", "1");
//                return map;
//            }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    JSONObject jsonObject = new JSONObject();
                    byte[] b = new byte[0];
                    try {
                        jsonObject.put("faultName", fult);
                        b = jsonObject.toString().getBytes("utf-8");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    return b;
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }
            };

            stringRequest.setRetryPolicy(new DefaultRetryPolicy(50000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            requestQueue.add(stringRequest);

    }

    public void checkAndroidVersion(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 555);
            }catch (Exception e){

            }
        } else {
            pickImage();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 555 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            pickImage();
        } else {
            checkAndroidVersion();
        }
    }

    public void pickImage() {
        CropImage.startPickImageActivity(this);
    }

    private void croprequest(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //RESULT FROM SELECTED IMAGE
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            croprequest(imageUri);
        }

        //RESULT FROM CROPING ACTIVITY
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), result.getUri());

                    ((ImageView)findViewById(R.id.imageView)).setImageBitmap(bitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Log.d("supun","ghgf");
    }
}
