package com.example.jktmuhip.eodfirebase;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase dataBase;
    private DatabaseReference databaseRef;
    ConnectionClass koneksi;
    JobScheduler jobScheduler;
    private static final int MYJOBID = 1;
    Chronometer chronometer;

    EditText editTextRef;
    EditText editTextVal;
    Button buttonSubmit;

    String reference1;
    String value1;
    String reference2;
    String value2;
    String tgl;
    String proses;
    String Query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        koneksi = new ConnectionClass();
        dataBase = FirebaseDatabase.getInstance();
        editTextRef = (EditText) findViewById(R.id.ETRef);
        editTextVal = (EditText) findViewById(R.id.ETVal);
        buttonSubmit = (Button) findViewById(R.id.BTNSubmit);

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

    }

    public void submit(View view) {
        //buttonSubmit.setOnClickListener(new View.OnClickListener(){

            //@Override
           // public void onClick(View v) {
                start();
            //}});
    }

    public void firebase()
    {
//        reference = editTextRef.getText().toString();
//        value = editTextVal.getText().toString();

        reference1 = "EODDate";
        reference2 = "ProcessName";

//        if(TextUtils.isEmpty(reference1))
//        {
//            Toast.makeText(this, "Please input reference", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if(TextUtils.isEmpty(value1))
//        {
//            Toast.makeText(this, "Please input value", Toast.LENGTH_SHORT).show();
//            return;
//        }

        if(reference1 == "EODDate")
        {
            value1 = tgl;
            databaseRef = dataBase.getReference(reference1);
            databaseRef.setValue(value1);
        }

        if(reference2 == "ProcessName")
        {
            value2 = proses;
            databaseRef = dataBase.getReference(reference2);
            databaseRef.setValue(value2);
        }
    }

    public class DoLogin extends AsyncTask<String, String, String>
    {
        String z = "";
        Boolean isSuccess = false;

        @Override
        protected String doInBackground(String... params) {
            Connection kon = koneksi.CONN();

            if (kon == null) {
                z = "Error koneksi SQL";
            } else {
                Query = "select * from CheckEOD with (nolock)";
                Statement stmt = null;

                try {
                    stmt = kon.createStatement();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    ResultSet rs = stmt.executeQuery(Query);

                    if (rs.next()) {
                        StringBuilder sb = new StringBuilder();
                        StringBuilder sb2 = new StringBuilder();

                        String TanggalEOD = rs.getString(1);
                        String Description = rs.getString(2);

                        sb.append(TanggalEOD);
                        sb2.append(Description);

                        tgl = sb.toString();
                        proses = sb2.toString();

                        Log.d("Disini", tgl);
                        Log.d("Disini", proses);

//                        if (Hasil.equals("") || Hasil.isEmpty()) {
//                            //Log.d("Error","Error COY");
//                            Hasil = "EOD Belum Mulai";
//                        }

                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            return z;
        }

        protected void onPostExecute(String result)
        {
            firebase();
        }

    }

    public void start()
    {
        DoLogin doLogin = new DoLogin();
        doLogin.execute("");

//        chronometer.setBase(SystemClock.elapsedRealtime());
//        chronometer.start();
//
//        ComponentName jobService =
//                new ComponentName(getPackageName(), MyJobService.class.getName());
//        JobInfo jobInfo =
//                new JobInfo.Builder(MYJOBID, jobService).setPeriodic(10000).build();
//
//        int jobId = jobScheduler.schedule(jobInfo);
//        if(jobScheduler.schedule(jobInfo)>0){
//            Toast.makeText(MainActivity.this,
//                    "Successfully scheduled job: " + jobId,
//                    Toast.LENGTH_SHORT).show();
//        }else{
//            Toast.makeText(MainActivity.this,
//                    "RESULT_FAILURE: " + jobId,
//                    Toast.LENGTH_SHORT).show();
//        }
    }

}
