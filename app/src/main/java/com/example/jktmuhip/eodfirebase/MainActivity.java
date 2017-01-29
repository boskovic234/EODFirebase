package com.example.jktmuhip.eodfirebase;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase dataBase;
    private DatabaseReference databaseRef;
    ConnectionClass koneksi;
    JobScheduler jobScheduler;
    private static final int MYJOBID = 1;
    Chronometer chronometer;
    Button buttonSubmit;
    Button buttonStop;

    String reference1;
    String value1;
    String reference2;
    String value2;
    String tgl;
    String proses;
    String Query;
    CountDownTimer timer;
    SimpleDateFormat formatjam;
    String jam1;
    String jam2;
    String jam3;
    Calendar c;
    Date time1 = new Date();
    Date time2 = new Date();
    Date time3 = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        koneksi = new ConnectionClass();


        dataBase = FirebaseDatabase.getInstance();
        buttonSubmit = (Button) findViewById(R.id.BTNSubmit);
        buttonStop = (Button) findViewById(R.id.BTNStop);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        formatjam = new SimpleDateFormat("HH:mm");

        buttonStop.setEnabled(false);

        //SET JAM
        c = Calendar.getInstance();
        jam2 = "23:50"; //END EOD MONITORING TIME
        jam3 = "19:00"; //START EOD MONITORING TIME

        jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);

        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();
                buttonStop.setEnabled(true);
                buttonSubmit.setEnabled(false);

                    timer = new CountDownTimer(86400000, 1800000) {
                    //timer = new CountDownTimer(120000, 20000) {
                        public void onFinish() {
                            // When timer is finished
                            // Execute your code here
                            Log.d("Disini", "EOD Selesai Woi");

                        }

                        public void onTick(long millisUntilFinished) {
                            // millisUntilFinished    The amount of time until finished.
                            jam1 = formatjam.format(c.getTime());
                            //jam1 = "18:58"; debug


                            try {
                                time1 = formatjam.parse(jam1);
                                time2 = formatjam.parse(jam2);
                                time3 = formatjam.parse(jam3);

                                if(time1.compareTo(time2)<0)
                                {
                                    if(time1.compareTo(time3)>0)
                                    {
                                    Log.d("Disini","EOD lagi jalan,"+time1+" "+time2+" "+time3);
                                    DoLogin doLogin = new DoLogin();
                                    doLogin.execute("");
                                    }
                                    else
                                    {
                                        Log.d("Disini","EOD lom mulai1,"+time1+" "+time2+" "+time3);
                                    }
                                }
                                else
                                {
                                    Log.d("Disini","EOD Sudah selesai,"+time1+" "+time2+" "+time3);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();


                //DoLogin doLogin = new DoLogin();
                //doLogin.execute("");

                /* BACKGROUND PROCESS START
                chronometer.setBase(SystemClock.elapsedRealtime());
                chronometer.start();

                ComponentName jobService =
                        new ComponentName(getPackageName(), MyJobService.class.getName());
                JobInfo jobInfo =
                        new JobInfo.Builder(MYJOBID, jobService).setPeriodic(5000).build();

                int jobId = jobScheduler.schedule(jobInfo);
                if (jobScheduler.schedule(jobInfo) > 0) {
                    Toast.makeText(MainActivity.this,
                            "Successfully scheduled job: " + jobId,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "RESULT_FAILURE: " + jobId,
                            Toast.LENGTH_SHORT).show();
                }BACKGROUND PROCESS END*/
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chronometer.stop();
                timer.cancel();
                buttonSubmit.setEnabled(true);
                buttonStop.setEnabled(false);
                Log.d("Disini", "Timer di stop");

                /*BACKGROUND PROCESS START
                List<JobInfo> allPendingJobs = jobScheduler.getAllPendingJobs();
                String s = "";
                for (JobInfo j : allPendingJobs) {
                    int jId = j.getId();
                    jobScheduler.cancel(jId);
                    s += "jobScheduler.cancel(" + jId + " )";
                }
                Toast.makeText(MainActivity.this,
                        s,
                        Toast.LENGTH_SHORT).show();
                BACKGROUND PROCESS STOP*/
            }
        });
    }

    public class DoLogin extends AsyncTask<String, String, String> {
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

                        //Log.d("Disini", tgl);
                        //Log.d("Disini", proses);

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

    public void firebase() {

        reference1 = "EODDate";
        reference2 = "ProcessName";

        if (reference1 == "EODDate") {
            value1 = tgl;
            databaseRef = dataBase.getReference(reference1);
            databaseRef.setValue(value1);
        }

        if (reference2 == "ProcessName") {
            value2 = proses;
            databaseRef = dataBase.getReference(reference2);
            databaseRef.setValue(value2);
        }
    }

}
