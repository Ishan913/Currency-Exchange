package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String convertTo="";
    String convertFrom="";
    String amount="";
    EditText editText;
    Button button;
    TextView resultTextView;
    Spinner fromSpinner;
    Spinner toSpinner;
    int indexFrom=0;
    int indexTo=0;
    String[] currencies;
    String[] currencyCode;
    ConstraintLayout constraintLayout;
    ConstraintLayout infoLayout;

    public void infoShow(View view){
        infoLayout.setVisibility(View.VISIBLE);
        button.setVisibility(View.INVISIBLE);
        resultTextView.setVisibility(View.INVISIBLE);
    }

    public void exitInfo(View view){
        infoLayout.setVisibility(View.INVISIBLE);
        button.setVisibility(View.VISIBLE);
        resultTextView.setVisibility(View.VISIBLE);
    }

    public  void convert(View view){
        String result="";
        convertFrom = currencyCode[indexFrom];
        convertTo = currencyCode[indexTo];
        amount = editText.getText().toString();
        try {
            DownloadRates task = new DownloadRates();
            result = task.execute("https://api.exchangeratesapi.io/latest?base="+convertFrom).get();
            Log.i("JSON data",result);

            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);

        }catch (Exception e){
            e.printStackTrace();
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoLayout = findViewById(R.id.infoLayout);
        button = findViewById(R.id.button);
        infoLayout.setVisibility(View.INVISIBLE);
        editText = findViewById(R.id.editText);
        resultTextView = findViewById(R.id.resultTextView);
        fromSpinner = findViewById(R.id.fromSpinner);
        toSpinner = findViewById(R.id.toSpinner);
        currencies = new String[]{"US dollar","Japanese yen","Bulgarian lev","Czech koruna","Danish krone","Pound sterling","Hungarian forint","Polish zloty","Romanian leu","Swedish krona","Swiss franc","Icelandic krona","Norwegian krone","Croatian kuna","Russian rouble","Turkish lira","Australian dollar","Brazilian real","Canadian dollar","Chinese yuan renminbi","Hong Kong dollar","Indonesian rupiah","Israeli shekel","Indian rupee","South Korean won","Mexican peso","Malaysian ringgit","New Zealand dollar","Philippine peso","Singapore dollar","Thai baht","South African rand"};
        currencyCode = new String[]{"USD","JPY","BGN","CZK","DKK","GBP","HUF","PLN","RON","SEK","CHF","ISK","NOK","HRK","RUB","TRY","AUD","BRL","CAD","CNY","HKD","IDR","ILS","INR","KRW","MXN","MYR","NZD","PHP","SGD","THB","ZAR"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,currencies);
        toSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(adapter);

        fromSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
                return false;
            }
        });

        toSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(editText.getWindowToken(),0);
                return false;
            }
        });


        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexFrom = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                indexTo = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    public class DownloadRates extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                String input = null;
                StringBuffer stringBuffer = new StringBuffer();
                while (true){
                    if (!((input = in.readLine()) != null))
                        break;
                    stringBuffer.append(input);
                }
                in.close();
                return  stringBuffer.toString();

            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                String rates = jsonObject.getString("rates");
                JSONObject ratesInfo =new JSONObject(rates);

                String message="";

                String curr = ratesInfo.getString(convertTo);
                double y = (double) Math.round((Double.parseDouble(curr)*Double.parseDouble(amount))*100)/100;
                resultTextView.setText(Double.toString(y) + " "+currencyCode[indexTo]);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}