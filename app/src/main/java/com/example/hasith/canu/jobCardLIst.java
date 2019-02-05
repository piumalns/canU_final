package com.example.hasith.canu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class jobCardLIst extends AppCompatActivity {

    private final String URL_TO_HIT = "https://api.myjson.com/bins/12imsg";
    private TextView tvData;
    private ListView lvJobs;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_card_list);

        dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Loading. Please wait...");


        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        ImageLoader.getInstance().init(config); // Do it on Application start

        lvJobs = (ListView)findViewById(R.id.lvJobs);


        // To start fetching the data when app start.
        new JSONTask().execute(URL_TO_HIT);
    }


    public class JSONTask extends AsyncTask<String,String, List<jobModel> > {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected List<jobModel> doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line ="";
                while ((line = reader.readLine()) != null){
                    buffer.append(line);
                }

                String finalJson = buffer.toString();

                JSONObject parentObject = new JSONObject(finalJson);
                JSONArray parentArray = parentObject.getJSONArray("movies");

                List<jobModel> jobModelList = new ArrayList<>();

                Gson gson = new Gson();
                for(int i=0; i<parentArray.length(); i++) {
                    JSONObject finalObject = parentArray.getJSONObject(i);

                    jobModel jobmodel = gson.fromJson(finalObject.toString(), jobModel.class);

                    jobModelList.add(jobmodel);
                }
                return jobModelList;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if(connection != null) {
                    connection.disconnect();
                }
                try {
                    if(reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return  null;
        }

        @Override
        protected void onPostExecute(final List<jobModel> result) {
            super.onPostExecute(result);
            dialog.dismiss();
            if(result != null) {
                MovieAdapter adapter = new MovieAdapter(getApplicationContext(), R.layout.row, result);
                lvJobs.setAdapter(adapter);
                lvJobs.setOnItemClickListener(new AdapterView.OnItemClickListener() {  // list item click opens a new detailed activity
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        jobModel jobModel = result.get(position); // getting the model
                        Intent intent = new Intent(jobCardLIst.this, jobDetailActivity.class);
                        intent.putExtra("jobModel", new Gson().toJson(jobModel)); // converting model json into string type and sending it via intent
                        startActivity(intent);
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }

        public class MovieAdapter extends ArrayAdapter {

            private List<jobModel> jobModelList;
            private int resource;
            private LayoutInflater inflater;
            public MovieAdapter(Context context, int resource, List<jobModel> objects) {
                super(context, resource, objects);
                jobModelList = objects;
                this.resource = resource;
                inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                ViewHolder holder = null;

                if(convertView == null){
                    holder = new ViewHolder();
                    convertView = inflater.inflate(resource, null);
                    holder.ivMovieIcon = (ImageView)convertView.findViewById(R.id.ivIcon);
                    holder.tvFault = (TextView)convertView.findViewById(R.id.tvFault);
                    holder.tvStatus = (TextView)convertView.findViewById(R.id.tvStatus);
                    holder.tvSerial_number = (TextView)convertView.findViewById(R.id.tvSerial_number);
                    holder.tvDepartment= (TextView)convertView.findViewById(R.id.tvDepartment);
                    holder.tvDate= (TextView)convertView.findViewById(R.id.tvDate);
                    holder.tvDescription = (TextView)convertView.findViewById(R.id.tvDescription);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final ProgressBar progressBar = (ProgressBar)convertView.findViewById(R.id.progressBar);

                // Then later, when you want to display image
                final ViewHolder finalHolder = holder;
                ImageLoader.getInstance().displayImage(jobModelList.get(position).getImage(), holder.ivMovieIcon, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        progressBar.setVisibility(View.VISIBLE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        progressBar.setVisibility(View.GONE);
                        finalHolder.ivMovieIcon.setVisibility(View.INVISIBLE);
                    }
                });

                holder.tvFault.setText(jobModelList.get(position).getFault());
                holder.tvStatus.setText(jobModelList.get(position).getStatus());
                holder.tvSerial_number.setText("Serial_number: " + jobModelList.get(position).getSerial_num());
                holder.tvDepartment.setText("Department:" + jobModelList.get(position).getDepartment());
                holder.tvDate.setText("Date:" + jobModelList.get(position).getDate());
                holder.tvDescription.setText(jobModelList.get(position).getDescription());
                return convertView;
            }


            class ViewHolder{
                private ImageView ivMovieIcon;
                private TextView tvFault;
                private TextView tvStatus;
                private TextView tvSerial_number;
                private TextView tvDepartment;
                private TextView tvDate;
                private TextView tvDescription;
            }

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
