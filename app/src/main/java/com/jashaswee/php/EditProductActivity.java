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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class EditProductActivity extends Activity {

    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PRODUCT = "product";
    private static final String TAG_PID = "pid";
    private static final String TAG_NAME = "name";
    private static final String TAG_PRICE = "price";
    private static final String TAG_DESCRIPTION = "description";
    private static String editProductUrl = "http://localhost/update_product.php";
    private static String getProductDetailsUrl = "http://localhost/get_product_details.php";
    private static String deleteProductUrl = "http://localhost/delete_product.php";
    //JSON node for success
    private static String TAG_SUCCESS = "success";
    private final String TAG = EditProductActivity.class.getSimpleName();
    EditText name, price, desc;
    JSONParser jsonParser;
    ProgressDialog pDialog;
    String pid = "";
    JSONArray products = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_product);




        jsonParser = new JSONParser();

        Intent intent = getIntent();
        pid = intent.getStringExtra(TAG_PID);

        //Button to save changes
        Button btnSave;

        //Button to delete changes
        Button btnDelete;

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    class EditProduct extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Updating products...");
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
            params.add(TAG_PID, )
            params.add(new BasicNameValuePair(TAG_NAME, inputName));
            params.add(new BasicNameValuePair(TAG_PRICE, inputPrice));
            params.add(new BasicNameValuePair(TAG_DESCRIPTION, inputDesc));

            try {
                JSONObject json = jsonParser.makeHttpRequest(editProductUrl, "POST", params);
                Log.d(TAG, "doInBackground: " + json.toString());
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (success == 1) {
                //success
                //Now navigate to show all products
                Intent intent = getIntent();

                setResult(100, intent);
                finish();
            } else {
                //failure
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }
        }
    }

    class GetProductDetails extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Getting product details..");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... strings) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    int success = 0;
                    JSONObject json = null;

                    try {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair(TAG_PID, pid));

                        json = jsonParser.makeHttpRequest(getProductDetailsUrl, "GET", params);
                        Log.d(TAG, "run: " + json.toString());
                        success = json.getInt(TAG_SUCCESS);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(success == 1){
                        //success
                        //Show product details
                        try {
                            JSONArray productArray = json.getJSONArray(TAG_PRODUCT);
                            JSONObject product = productArray.getJSONObject(0);

                            //product with this pid found
                            name = findViewById(R.id.inputName);
                            price = findViewById(R.id.inputPrice);
                            desc = findViewById(R.id.inputDesc);

                            name.setText(product.getString(TAG_NAME));
                            price.setText(product.getString(TAG_PRICE));
                            desc.setText(product.getString(TAG_DESCRIPTION));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        //product with pid not found
                    }
                }
            });
            return null;

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
        }
    }

    class DeleteProductActivity extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Deleting said product..");
            pDialog.setCancelable(false);
            pDialog.setIndeterminate(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            int success = 0;
            JSONObject json = null;

            try{

                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair(TAG_PID, pid));

                json = jsonParser.makeHttpRequest(deleteProductUrl, "POST", params);

                Log.d(TAG, "doInBackground: " + json.toString());
                success = json.getInt(TAG_SUCCESS);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(success == 1) {
                //success
                //notify previous activity that deleted
                Intent intent = getIntent();
                setResult(100, i);
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
