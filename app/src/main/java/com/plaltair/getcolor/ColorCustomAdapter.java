package com.plaltair.getcolor;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

/**
 * Created by pierlucalippi on 27/02/18.
 */

public class ColorCustomAdapter extends BaseAdapter {

    private Context context;
    private List<ColorRowItem> rowItems;

    public ColorCustomAdapter(Context context, List<ColorRowItem> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    private class ViewHolder {
        TextView redValue;
        TextView greenValue;
        TextView blueValue;
        TextView hexValue;
        View color;
        View buttonContainer;
        Button delete;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_saved_colors, null);

            holder.redValue = convertView.findViewById(R.id.redValue);
            holder.greenValue = convertView.findViewById(R.id.greenValue);
            holder.blueValue = convertView.findViewById(R.id.blueValue);
            holder.hexValue = convertView.findViewById(R.id.hexValue);
            holder.color = convertView.findViewById(R.id.color);
            holder.buttonContainer = convertView.findViewById(R.id.buttonContainer);
            holder.delete = convertView.findViewById(R.id.delete);
            
            holder.color.getLayoutParams().height = PercentageSize.percentageVertical(20);
            holder.buttonContainer.getLayoutParams().height = PercentageSize.percentageVertical(15);
            holder.delete.getLayoutParams().height = PercentageSize.percentageVertical(holder.buttonContainer, 50);

            ScaledFont.fixTextSize(holder.redValue, 15);
            ScaledFont.fixTextSize(holder.greenValue, 15);
            ScaledFont.fixTextSize(holder.blueValue, 15);
            ScaledFont.fixTextSize(holder.hexValue, 15);
            ScaledFont.fixTextSize(holder.delete, 15);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        final ColorRowItem row_pos = rowItems.get(position);

        String hexString = row_pos.getHexValue();

        String redHex = hexString.substring(0, 2);
        String greenHex = hexString.substring(2, 4);
        String blueHex = hexString.substring(4, 6);

        int red = Integer.decode("0x" + redHex);
        int green = Integer.decode("0x" + greenHex);
        int blue = Integer.decode("0x" + blueHex);

        String redValue = String.valueOf(red);
        String greenValue = String.valueOf(green);
        String blueValue = String.valueOf(blue);

        holder.redValue.setText("Red: " + redValue);
        holder.greenValue.setText("Green: " + greenValue);
        holder.blueValue.setText("Blue: " + blueValue);
        holder.hexValue.setText("Hex: " + hexString);

        holder.color.setBackgroundColor(Color.parseColor("#" + hexString));

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.plaltair.getcolor/databases/database", null, SQLiteDatabase.OPEN_READWRITE);

                database.delete("Colors", "Hex=?", new String[]{row_pos.getHexValue()});

                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

        return convertView;
    }
}
