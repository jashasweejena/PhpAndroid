package com.jashaswee.php;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewProductActivity extends Activity {
    private static String addNewProductUrl = "http://localhost/create_product.php";
    //JSON node for success
    private static String TAG_SUCCESS = "success";
    private final String TAG = NewProductActivity.class.getSimpleName();
    EditText name, price, desc;

    JSONParser jsonParser;

    ProgressDialog pDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product);

        //Edit texts
        name = findViewById(R.id.inputName);
        price = findViewById(R.id.inputPrice);
        desc = findViewById(R.id.inputDesc);

        //Create button
        Button create = findViewById(R.id.btnCreateProduct);

        jsonParser = new JSONParser();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    class CreateNewProduct extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(NewProductActivity.this);
            pDialog.setMessage("Creating Product ... ");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            String inputName = name.getText().toString();
            String inputPrice = price.getText().toString();
            String inputDesc = desc.getText().toString();

            int success = 0;

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("name", inputName));
            params.add(new BasicNameValuePair("price", inputPrice));
            params.add(new BasicNameValuePair("description", inputDesc));


            //getting JSON object
            try {
                JSONObject json = jsonParser.makeHttpRequest(addNewProductUrl, "POST", params);
                Log.d(TAG, "NewProductActivity: " + json.toString());
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success == 1) {
                //Success
                Intent intent = new Intent(getApplicationContext(), AllProductsActivity.class);
                startActivity(intent);

                //Closing this screen
                finish();
            }

            return null;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }


    }
}