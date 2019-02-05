package com.example.hasith.canu;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class qrScaner extends AppCompatActivity implements ZXingScannerView.ResultHandler {
    private ZXingScannerView zXingScannerView;
    private static int REQUEST_CAMERA = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        zXingScannerView = new ZXingScannerView(this);
        setContentView(zXingScannerView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                Toast.makeText(qrScaner.this, "permission is granted", Toast.LENGTH_LONG).show();
            } else {
                requestPermission();
            }
        }
    }

    private boolean checkPermission() {
        return (ContextCompat.checkSelfPermission(qrScaner.this, CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0) {
            boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (cameraAccepted) {
                Toast.makeText(qrScaner.this, "permission Granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(qrScaner.this, "permission Denied", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(CAMERA)) {
                        displayAlertMessage("you need to allow access for both permission", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(new String[]{CAMERA}, REQUEST_CAMERA);
                            }
                        });
                        return;
                    }
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if (zXingScannerView == null) {
                    zXingScannerView = new ZXingScannerView(this);
                    setContentView(zXingScannerView);

                }
                zXingScannerView.setResultHandler(this);
                zXingScannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        zXingScannerView.stopCamera();
    }

    private void displayAlertMessage(String message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(qrScaner.this)
                .setMessage(message)
                .setPositiveButton("ok", listener)
                .setNegativeButton("cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {

        Toast.makeText(qrScaner.this,result.toString(),Toast.LENGTH_LONG).show();
        searchId(result.toString());
//        getMachine(result.toString());
        String scanResult = result.getText();
//        View dialogView = getLayoutInflater().inflate(R.layout.activity_dialog,null);





//        builder.setMessage(scanResult);

    }


    public void searchId(String result){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scan result");
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://10.10.4.175:5000/api/machines/check/serialNumber";
        JSONObject body = new JSONObject();
        try {
            body.put("serialNumber",result);
//            body.put("password", "123456");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final ProgressDialog dialog = new ProgressDialog(qrScaner.this);
        dialog.setMessage("Loading...");
        dialog.show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, body, new Response.Listener<JSONObject>() {


            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(qrScaner.this, response.toString(), Toast.LENGTH_LONG).show();

                try {
                    final String machineId =  response.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        zXingScannerView.resumeCameraPreview(qrScaner.this);
                    }
                });
                builder.setMessage(response.toString());

                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startIntent = new Intent(qrScaner.this,MachineDetail.class);
                        startActivity(startIntent);
                    }
                });
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(qrScaner.this, error.getMessage(), Toast.LENGTH_LONG).show();
                dialog.dismiss();

                builder.setMessage(error.toString());
                AlertDialog alertDialog = builder.create();

                alertDialog.show();
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
        };
        queue.add(request);
        queue.start();

    }
}
