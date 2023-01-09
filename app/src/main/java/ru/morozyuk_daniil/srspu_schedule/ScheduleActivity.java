package ru.morozyuk_daniil.srspu_schedule;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Iterator;

public class ScheduleActivity extends AppCompatActivity {

    public JSONObject schedule_obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        TextView group_label = (TextView) findViewById(R.id.group_field);
        RecyclerView schedule_list = (RecyclerView) findViewById(R.id.schedule_list);

        Intent incoming_intent = getIntent();

        String faculty_code = incoming_intent.getStringExtra("faculty_code");
        String year = incoming_intent.getStringExtra("year");
        String group_code = incoming_intent.getStringExtra("group_code");

        Log.d("schedule_debug", String.format("Faculty code: %1$s, year: %2$s, group code: %3$s", faculty_code, year, group_code));

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest schedule_request = new JsonObjectRequest(Request.Method.GET, getString(R.string.api_student_schedule, faculty_code, year, group_code), null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                schedule_obj = response;
                try {
                    group_label.setText(schedule_obj.getString("group"));
                    ScheduleAdapter adapter = new ScheduleAdapter(LayoutInflater.from(ScheduleActivity.this), schedule_obj.getJSONArray("classes"));
                    schedule_list.setAdapter(adapter);
                    schedule_list.setLayoutManager(new LinearLayoutManager(ScheduleActivity.this));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ScheduleActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(schedule_request);
    }
}