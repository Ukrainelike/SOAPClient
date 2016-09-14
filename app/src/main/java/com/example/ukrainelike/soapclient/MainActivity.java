package com.example.ukrainelike.soapclient;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView error_msg;
    private EditText login;
    private EditText password;
    private Button loginButton;
    private ProgressBar progressBar;
    private final static String envelope= "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<env:Envelope xmlns:env=\"http://www.w3.org/2003/05/soap-envelope\" xmlns:ns1=\"urn:General.Intf-IGeneral\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:enc=\"http://www.w3.org/2003/05/soap-encoding\">" +
            "<env:Body>+" +
            "<ns1:Login env:encodingStyle=\"http://www.w3.org/2003/05/soap-encoding\">" +
            "<UserName xsi:type=\"xsd:string\">%s</UserName><Password xsi:type=\"xsd:string\">" +
            "%s" +
            "</Password>" +
            "<IP xsi:type=\"xsd:string\">" +
            "</IP>" +
            "</ns1:Login>" +
            "</env:Body>" +
            "</env:Envelope>";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        error_msg=(TextView) findViewById(R.id.error_message);
        login=(EditText) findViewById(R.id.LoginDate);
        password=(EditText) findViewById(R.id.PasswordData);
        loginButton=(Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(this);
        progressBar=(ProgressBar) findViewById(R.id.progressBar2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.LoginButton:
                if(checkInternetConnection() && login.getText().length()!=0 && password.getText().length()!=0) {
                    final String requestEnvelope = String.format(envelope, login.getText(), password.getText());
                    MyTask mt = new MyTask();
                    mt.execute(requestEnvelope);
                } else {stateConectrion(Color.RED,"No internet connection or login and password not ready");}
                break;
            default:
                break;
        }
    }

    class MyTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loginButton.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
        }
        @Override
        protected String doInBackground(String... voids) {
            for (String s:voids) {
                return CallWebService("http://isapi.mekashron.com/StartAJob/General.dll/soap/IGeneral", "urn:General.Intf-IGeneral#Login", s);
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            parseXML_and_check_Authorization(result);
            loginButton.setEnabled(true);
            progressBar.setVisibility(View.INVISIBLE);
        }

    }
    private String CallWebService(String url, String soapAction, String envelope)  {
        final DefaultHttpClient httpClient=new DefaultHttpClient();
        HttpParams params = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 15000);
        HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);
        HttpPost httppost = new HttpPost(url);
        httppost.setHeader("soapaction", soapAction);
        httppost.setHeader("Content-Type", "text/xml; charset=utf-8");
        String responseString="";
        try {
            HttpEntity entity = new StringEntity(envelope);
            httppost.setEntity(entity);
            ResponseHandler<String> rh=new ResponseHandler<String>() {
                public String handleResponse(HttpResponse response) throws IOException {
                    HttpEntity entity = response.getEntity();
                    StringBuffer out = new StringBuffer();
                    byte[] b = EntityUtils.toByteArray(entity);
                    out.append(new String(b, 0, b.length));
                    return out.toString();
                }
            };
            responseString=httpClient.execute(httppost, rh);
        }
        catch (Exception e) {
            Log.v("exception", e.toString());
        }
        httpClient.getConnectionManager().shutdown();
        return responseString;

    }

    private void parseXML_and_check_Authorization(String response) {
        try {
            Document document = DocumentHelper.parseText(response);
            Element classElement = document.getRootElement();
            JSONObject jObject = new JSONObject(classElement.getStringValue());
            if(!jObject.isNull("ResultCode")) {
                stateConectrion(Color.RED,jObject.getString("ResultMessage"));
            } else if (!jObject.isNull("EntityId")) {
                stateConectrion(Color.GREEN,"Successful you №"+jObject.getInt("EntityId"));
            } else {
                stateConectrion(Color.RED, "Response no valid");
            }
        } catch (DocumentException | JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean checkInternetConnection() {

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return (cm.getActiveNetworkInfo() != null) && cm.getActiveNetworkInfo().isAvailable()
                && cm.getActiveNetworkInfo().isConnected();
    }

    private void stateConectrion(int color, String string) {
        error_msg.setText(string);
        error_msg.setTextColor(color);
    }
}
