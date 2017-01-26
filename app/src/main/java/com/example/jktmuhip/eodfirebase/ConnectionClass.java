package com.example.jktmuhip.eodfirebase;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.util.Log;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Created by jktmuhip on 8/24/2016.
 */
public class ConnectionClass {
    String IP = "172.16.0.73";
    String classs = "net.sourceforge.jtds.jdbc.Driver";
    String DB = "ITProduction";
    String UN = "sa";
    String PASS = "AdIns2009";


    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        String ConnURL = null;
        try {

            Class.forName(classs);
            ConnURL = "jdbc:jtds:sqlserver://" + IP + ";"
                    + "databaseName=" + DB + ";user=" + UN + ";password="
                    + PASS + ";";
            conn = DriverManager.getConnection(ConnURL);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
