package com.example.atulweather.WeatherAdapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;

import com.example.atulweather.R;
import com.example.atulweather.WeatherModal.WeatherModal;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherModal> weatherModalArrayList;

    public WeatherAdapter(Context context, ArrayList<WeatherModal> weatherModalArrayList) {
        this.context = context;
        this.weatherModalArrayList = weatherModalArrayList;
    }

    @NonNull
    @Override
    public WeatherAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherAdapter.ViewHolder holder, int position) {

        WeatherModal modal = weatherModalArrayList.get(position);
        holder.temp.setText(modal.getTemp()+ "°C");

        Picasso.get().load("https:".concat(modal.getIcon())).into(holder.condition);

        holder.wind.setText(modal.getWindSpeed()+"km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm am");
        try {
            Date t = input.parse(modal.getTime());
            holder.time.setText(output.format(t));

        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherModalArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView time,wind,temp;
        private ImageView condition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.time);
            wind = itemView.findViewById(R.id.windSpeed);
            temp = itemView.findViewById(R.id.temp);

            condition = itemView.findViewById(R.id.condition);
        }
    }
}
