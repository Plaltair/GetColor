package com.plaltair.getcolor;

import android.Manifest;
import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MyPictures extends AppCompatActivity {

    private List<ImageFileRowItem> rowItems = new ArrayList<ImageFileRowItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_pictures);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.listView);
        TextView emptyListView = findViewById(R.id.emptyListView);
        listView.setEmptyView(emptyListView);

        ScaledFont.fixTextSize(emptyListView, 10);

        try {
            File files[] = getAllPictures();

            Collections.reverse(Arrays.asList(files));

            for (int i = 0; i < files.length; i++) {
                ImageFileRowItem item = new ImageFileRowItem(files[i].toString());
                rowItems.add(item);
            }

            ImageFileCustomAdapter customAdapter = new ImageFileCustomAdapter(MyPictures.this, rowItems);
            listView.setAdapter(customAdapter);

        }

        catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private File[] getAllPictures() {
        String path = getFilesDir().getAbsolutePath() + "/SavedPhotos";
        File directory = new File(path);
        File[] files = directory.listFiles();

        return files;
    }
}
