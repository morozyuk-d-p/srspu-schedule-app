package ru.morozyuk_daniil.srspu_schedule;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {
    // Initiate common variables
    public JSONObject faculties;
    public JSONArray groups;
    public ArrayList<String> faculties_spinner_items = new ArrayList<>();
    public ArrayList<String> groups_spinner_items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get views
        TextView textview = (TextView) findViewById(R.id.main_textview_message);

        Spinner year_spinner = (Spinner) findViewById(R.id.main_year_spinner);
        Spinner faculty_spinner = (Spinner) findViewById(R.id.main_faculty_spinner);
        Spinner group_spinner = (Spinner) findViewById(R.id.main_group_spinner);

        Button open_button = (Button) findViewById(R.id.open_schedule_button);

        // Setup views
        year_spinner.setAdapter(ArrayAdapter.createFromResource(this, R.array.years, com.google.android.material.R.layout.support_simple_spinner_dropdown_item));
        open_button.setOnClickListener(v -> {
            Intent intent = new Intent(this, ScheduleActivity.class);

            // Determine required code of faculty
            int i = 0;
            String faculty_code = "";
            for (Iterator<String> it = faculties.keys(); it.hasNext(); i++) {
                if (i == faculty_spinner.getSelectedItemPosition()) {
                    faculty_code = it.next();
                } else {
                    it.next();
                }
            }

            intent.putExtra("faculty_code", faculty_code);
            intent.putExtra("year", String.valueOf(year_spinner.getSelectedItemPosition() + 1));
            try {
                intent.putExtra("group_code", groups.getJSONArray(group_spinner.getSelectedItemPosition()).getString(0));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            startActivity(intent);
        });

        // Create request queue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Fetch and process update date
        StringRequest update_date_req = new StringRequest(Request.Method.GET, getString(R.string.api_last_updated), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                textview.setText(getString(R.string.welcome_message) + "\n" + getString(R.string.update_date_message, response.toString().replace("\"", "")));
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

        // Fetch and process faculties data
        JsonObjectRequest faculties_req = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_faculties), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                // Copy response to faculties var to use later
                faculties = response;

                // Saving faculty keys to iterate them
                Iterator<String> keys = faculties.keys();
                while (keys.hasNext()) {
                    String i = keys.next();
                    try {
                        Log.d("net_debug", i + " is " + faculties.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    try {
                        faculties_spinner_items.add(faculties.getJSONObject(i).getString("code"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // Filling in spinner values
                faculty_spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, faculties_spinner_items));

                // Creating listener to fetch groups every time spinner changes
                faculty_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Determine required code of faculty
                        int i = 0;
                        String faculty_code = "";
                        for (Iterator<String> it = faculties.keys(); it.hasNext(); i++) {
                            if (i == faculty_spinner.getSelectedItemPosition()) {
                                faculty_code = it.next();
                            } else {
                                it.next();
                            }
                        }
                        queue.add(fetchGroups(faculty_code, year_spinner.getSelectedItemPosition() + 1, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("net_debug", response.toString());
                                groups_spinner_items = new ArrayList<>();
                                groups = response;
                                for (int i = 0; i < groups.length(); i++) {
                                    try {
                                        groups_spinner_items.add(response.getJSONArray(i).getString(1).toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                group_spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, groups_spinner_items));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("net_debug", "Error occurred: " + error.toString());
                            }
                        }));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                year_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        // Determine required code of faculty
                        int i = 0;
                        String faculty_code = "";
                        for (Iterator<String> it = faculties.keys(); it.hasNext(); i++) {
                            if (i == faculty_spinner.getSelectedItemPosition()) {
                                faculty_code = it.next();
                            } else {
                                it.next();
                            }
                        }
                        queue.add(fetchGroups(faculty_code, year_spinner.getSelectedItemPosition() + 1, new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.d("net_debug", response.toString());
                                groups_spinner_items = new ArrayList<>();
                                groups = response;
                                for (int i = 0; i < groups.length(); i++) {
                                    try {
                                        groups_spinner_items.add(response.getJSONArray(i).getString(1).toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                group_spinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, com.google.android.material.R.layout.support_simple_spinner_dropdown_item, groups_spinner_items));
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("net_debug", "Error occurred: " + error.toString());
                            }
                        }));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("net_debug", "Error occurred: " + error.toString());
                Toast.makeText(MainActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(update_date_req);
        queue.add(faculties_req);
    }

    public JsonArrayRequest fetchGroups(String faculty_code, int year, Response.Listener<JSONArray> onResponse, Response.ErrorListener onError) {
        return new JsonArrayRequest(Request.Method.GET, getString(R.string.api_group_list, faculty_code, String.valueOf(year)), null, onResponse, onError);
    }
}