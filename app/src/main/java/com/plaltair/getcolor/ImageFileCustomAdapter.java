package com.plaltair.getcolor;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Created by pierlucalippi on 02/03/18.
 */

public class ImageFileCustomAdapter extends BaseAdapter {

    private Context context;
    private List<ImageFileRowItem> rowItems;

    public ImageFileCustomAdapter(Context context, List<ImageFileRowItem> rowItems) {
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
        ImageView image;
        Button delete;
        Button saveInTheGallery;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        holder = new ViewHolder();
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_my_pictures, null);

            holder.image = convertView.findViewById(R.id.image);
            holder.delete = convertView.findViewById(R.id.delete);
            holder.saveInTheGallery = convertView.findViewById(R.id.saveInTheGallery);

            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            holder.image.getLayoutParams().height = PercentageSize.percentageVertical(25);
            holder.delete.getLayoutParams().height = PercentageSize.percentageVertical(holder.image, 25);
            holder.saveInTheGallery.getLayoutParams().height = PercentageSize.percentageVertical(holder.image, 25);

            ScaledFont.fixTextSize(holder.delete, 30);
            ScaledFont.fixTextSize(holder.saveInTheGallery, 30);

            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }

        final ImageFileRowItem row_pos = rowItems.get(position);

        String path = row_pos.getPath();

        final File file = new File(path);

        final Uri uri = Uri.fromFile(file);

        Glide.with(context)
                .load(uri)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GetFromImage.class);
                intent.putExtra("uri", Uri.fromFile(file));
                context.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                file.delete();
                ((Activity) context).finish();
                context.startActivity(((Activity) context).getIntent());
            }
        });

        holder.saveInTheGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                }
                else {
                    File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Get Color");

                    if (!folder.exists()) {
                        folder.mkdir();
                    }

                    File destination = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Get Color/" + file.getName());

                    try {
                        copyFile(file, destination);

                        Toast.makeText(context, context.getResources().getString(R.string.imageSaved), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e) {
                        Toast.makeText(context, context.getResources().getString(R.string.error), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        return convertView;
    }

    private void copyFile(File sourceFile, File destFile) throws IOException {
        if (!sourceFile.exists()) {
            return;
        }

        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }
}
