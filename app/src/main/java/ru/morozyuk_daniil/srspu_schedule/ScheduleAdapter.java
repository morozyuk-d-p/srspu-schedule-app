package ru.morozyuk_daniil.srspu_schedule;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

    private final LayoutInflater inflater;
    private final JSONArray schedule;

    public ScheduleAdapter(LayoutInflater inflater, JSONArray schedule) {
        this.inflater = inflater;
        this.schedule = schedule;
    }

    @NonNull
    @Override
    public ScheduleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.schedule_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ScheduleAdapter.ViewHolder holder, int position) {
        JSONObject lesson = null;
        try {
            lesson = schedule.getJSONObject(position);
            holder.auditoriumView.setText(lesson.getString("auditorium"));
            holder.lessonView.setText(String.format("%1$s %2$s", lesson.getString("type") , lesson.getString("discipline")));
            holder.lecturerView.setText(lesson.getString("lecturer"));
            holder.timeView.setText(String.format(
                    "%1$s неделя, %2$s день, %3$s пара",
                    String.valueOf(lesson.getInt("week")),
                    String.valueOf(lesson.getInt("day")),
                    String.valueOf(lesson.getInt("class")))
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return schedule.length();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView auditoriumView, lessonView, lecturerView, timeView;

        ViewHolder(View view) {
            super(view);
            auditoriumView = view.findViewById(R.id.auditorium_field);
            lessonView = view.findViewById(R.id.lesson_field);
            lecturerView = view.findViewById(R.id.lecturer_field);
            timeView = view.findViewById(R.id.time_field);
        }
    }
}
