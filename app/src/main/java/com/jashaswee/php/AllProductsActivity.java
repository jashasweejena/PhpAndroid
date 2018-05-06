package com.jashaswee.php;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AllProductsActivity extends ListActivity {

    private static final String TAG = AllProductsActivity.class.getSimpleName();

    //GET and POST methods
    private static final String method_Get = "GET";
    private static final String method_Post = "POST";

    //Create JSONParser object
    JSONParser jsonParser = new JSONParser();

    private ProgressDialog pDialog;

    ArrayList<HashMap<String,String>> productsList;

    private static String getAllProductsUrl = "http://localhost/get_all_products.php";

    //JSON node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PID = "pid";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_NAME = "name";

    //Products JSONArray
    JSONArray products = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_products);

        //Hashmap for listview
        productsList = new ArrayList<>();

        new LoadAllProducts().execute();

        //get listview
        ListView lv = getListView();

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //getting values from selected ListItem
                String pid = ((TextView) view.findViewById(R.id.pid)).getText().toString();

                //Starting new intent
                Intent in = new Intent(getApplicationContext(), EditProductActivity.class);

                //Sending pid to EditProductActivity
                in.putExtra(TAG_PID, pid);

                //Expecting some response from EditProductActivity
                startActivityForResult(in, 100);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //if resultcode is 100

        if(resultCode == 100) {
            //Means a product is edited
            //Restart AllProductsActivity to reflect the change

            Intent i = getIntent();
            finish();
            startActivity(i);
    }


}

    class LoadAllProducts extends AsyncTask<String, String, String>
    {
        JSONObject json;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllProductsActivity.this);
            pDialog.setMessage("Loading products. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            //dismiss the Progress Dialog
            pDialog.dismiss();

            //updating the UI from background thread
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /**
                     * Updating parsed JSON data into listview
                     */
                    ListAdapter adapter = new SimpleAdapter(
                            AllProductsActivity.this,
                            productsList,
                            R.layout.list_item,
                            new String[] {TAG_PID, TAG_NAME},
                            new int[] {R.id.pid, R.id.name});

                    //Updating listview
                    setListAdapter(adapter);

                }
            });
        }

        @Override
        protected String doInBackground(String... strings) {
            //Building Parameters
            List<NameValuePair> params = new ArrayList<>();
            //getting JSON String from URL
            try {
                json = jsonParser.makeHttpRequest(getAllProductsUrl, method_Get, params);
                Log.d(TAG, "doInBackground: " + json.toString());
                int success = json.getInt(TAG_SUCCESS);

                if(success == 1)
                {
                    //products found
                    //Getting 'products' array

                    products = json.getJSONArray(TAG_PRODUCTS);
                    for(int i=0 ; i < products.length() ; i++) {
                        JSONObject c = products.getJSONObject(i);

                        //Storing each JSON item in variable
                        String pid = c.getString(TAG_PID);
                        String name = c.getString(TAG_NAME);

                        //Creating new hashmap
                        HashMap<String, String> hashMap = new HashMap<>();

                        //Adding each node to hashmap key value pair
                        hashMap.put(TAG_PID, pid);
                        hashMap.put(TAG_NAME, name);

                        //Adding hashmap to arraylist
                        productsList.add(hashMap);
                    }
                }
                else {
                    //No products found
                    //Launch Add New Product Activity
                    Intent i = new Intent(getApplicationContext(), NewProductActivity.class);

                    //Closing all previous activities
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);

                }
            } catch (JSONException e) {
                e.printStackTrace();

                return null;
            }
        }




    }
}
