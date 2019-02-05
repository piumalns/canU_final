package com.example.hasith.canu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class jobDetailActivity extends AppCompatActivity {

    private ImageView ivMovieIcon;
    private TextView tvFault;
    private TextView tvStatus;
    private TextView tvSerial_number;
    private TextView tvDepartment;
    private TextView tvDate;
    private TextView tvDescription;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        init();

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // setting up text views and stuff
        setUpUIViews();

        // recovering data from MainActivity, sent via intent
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            String json = bundle.getString("jobModel"); // getting the model from MainActivity send via extras
            jobModel jobModel = new Gson().fromJson(json,jobModel.class);

            // Then later, to display image
            ImageLoader.getInstance().displayImage(jobModel.getImage(), ivMovieIcon, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar.setVisibility(View.GONE);
                }
            });

            tvFault.setText(jobModel.getFault());
            tvStatus.setText(jobModel.getStatus());
            tvSerial_number.setText("Serial Number: " + jobModel.getSerial_num());
            tvDepartment.setText("Department:" + jobModel.getDepartment());
            tvDate.setText("Date:" + jobModel.getDate());
            tvDescription.setText(jobModel.getDescription());

        }


    }

    private void setUpUIViews() {
        ivMovieIcon = (ImageView)findViewById(R.id.ivIcon);
        tvFault = (TextView)findViewById(R.id.tvFault);
        tvStatus = (TextView)findViewById(R.id.tvStatus);
        tvSerial_number = (TextView)findViewById(R.id.tvSerial_number);
        tvDepartment = (TextView)findViewById(R.id.tvDepartment);
        tvDate = (TextView)findViewById(R.id.tvDate);
        tvDescription = (TextView)findViewById(R.id.tvDescription);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);

    }

    public Button button3;

    public void init(){
        button3 = (Button)findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent loadnextPage = new Intent(jobDetailActivity.this,showOnlineMachine.class);
                startActivity(loadnextPage);
            }
        });
    }
}
