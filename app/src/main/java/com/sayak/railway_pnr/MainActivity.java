package com.sayak.railway_pnr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.sayak.railway_pnr.MainActivity.name;

public class MainActivity extends AppCompatActivity {

    EditText ed;
    Button submit;
    static String pnrno="";
    static String journey="";
    static String trainno="";
    static String name="";
    static String from="";
    static String to="";
    static String reservation="";
    static String boardingPoint="";
    static String classs="";
    static String noPassenger="";
    static String endpoint="";
    static String hmac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ed = (EditText)findViewById(R.id.editText);
        submit=(Button)findViewById(R.id.btnStat);

        //endpoint = "http://railpnrapi.com/test/check_pnr/pnr/1234567890/format/json/pbapikey/6a9c31798c24db78975edf5bb3d53a02/pbapisign/7ecead7eda905f5d33935dea575d8f8386b85870";
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(this,pnr,Toast.LENGTH_SHORT).show();
                String pnr = ed.getText().toString();
                hmac = new HMAC().getHMAC(pnr);
                endpoint = "http://railpnrapi.com/test/check_pnr/pnr/"+pnr+"/format/json/pbapikey/6a9c31798c24db78975edf5bb3d53a02/pbapisign/"+hmac;
                new getStat().execute();
            }
        });

    }

    private class getStat extends AsyncTask<Void,Void,Boolean>{

        private ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog=new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            // Create a new HttpClient and Post Header
            String resp = "";
            HttpClient client = new DefaultHttpClient();
            String postURL = (endpoint);
            Log.i("HIT", postURL);
            HttpGet get = new HttpGet(postURL);
            try {
                HttpResponse response = client.execute(get);
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    resp = EntityUtils.toString(resEntity);
                    Log.i("HIT", resp);

                    JSONObject j1 = new JSONObject(resp);
                    pnrno = j1.getString("pnr");
                    trainno = j1.getString("train_num");
                    name = j1.getString("train_name");
                    journey = j1.getString("doj");
                    classs = j1.getString("class");
                    noPassenger = j1.getString("no_of_passengers");

                    JSONObject j2 = j1.getJSONObject("from_station");
                    from = j2.getString("name");

                    j2=j1.getJSONObject("to_station");
                    to = j2.getString("name");

                    j2=j1.getJSONObject("reservation_upto");
                    reservation = j2.getString("name");

                    j2=j1.getJSONObject("boarding_point");
                    boardingPoint = j2.getString("name");
                }
            } catch (UnsupportedEncodingException uee) {
                uee.printStackTrace();
            } catch (ClientProtocolException cpe) {
                cpe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.setContentView(R.layout.stat);
            dialog.setTitle("Status");
            final TextView tv=(TextView) dialog.findViewById(R.id.textView);
            tv.setText("");
            tv.append("\nPNR : "+pnrno+"" +
                    "\nTrain Name : "+name+"" +
                    "\nTrain No. : "+trainno+"" +
                    "\nDate of Journey : "+journey+"" +
                    "\nFrom : "+from+
                    "\nTo : "+to+
                    "\nReservation Upto : "+reservation+
                    "\nBoarding Point : "+boardingPoint+
                    "\nClass : "+classs+
                    "\nNumber of Passengers : "+noPassenger);
            dialog.show();
        }
    }

}
