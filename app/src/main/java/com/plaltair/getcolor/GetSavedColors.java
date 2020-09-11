package com.plaltair.getcolor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GetSavedColors extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_saved_colors);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.listView);
        TextView emptyListView = findViewById(R.id.emptyListView);
        listView.setEmptyView(emptyListView);

        ScaledFont.fixTextSize(emptyListView, 10);

        List<ColorRowItem> rowItems = new ArrayList<>();

        SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.plaltair.getcolor/databases/database", null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = database.rawQuery("SELECT Hex FROM Colors", null);
        cursor.moveToLast();

        String colors[] = new String[cursor.getCount()];

        for (int i = 0; i < cursor.getCount(); i++) {
            colors[i] = cursor.getString(0);
            cursor.moveToPrevious();
        }

        cursor.close();
        database.close();

        for (int i = 0; i < colors.length; i++) {
            ColorRowItem item = new ColorRowItem(colors[i]);
            rowItems.add(item);
        }

        ColorCustomAdapter customAdapter = new ColorCustomAdapter(GetSavedColors.this, rowItems);
        listView.setAdapter(customAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
