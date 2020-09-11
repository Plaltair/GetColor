package com.plaltair.getcolor;

import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class GetFromImage extends AppCompatActivity {

    private ImageView image;

    private TextView redValue;
    private TextView greenValue;
    private TextView blueValue;
    private TextView hexValue;

    private View color;

    private ImageView square;

    private boolean pointerBlack = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_from_image);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        image = findViewById(R.id.image);

        square = findViewById(R.id.square);
        square.setVisibility(View.INVISIBLE);
        square.setImageResource(R.drawable.square);

        Button changePointerColor = findViewById(R.id.changePointerColor);
        color = findViewById(R.id.color);
        redValue = findViewById(R.id.redValue);
        greenValue = findViewById(R.id.greenValue);
        blueValue = findViewById(R.id.blueValue);
        hexValue = findViewById(R.id.hexValue);

        ScaledFont.fixTextSize(redValue, 15);
        ScaledFont.fixTextSize(greenValue, 15);
        ScaledFont.fixTextSize(blueValue, 15);
        ScaledFont.fixTextSize(hexValue, 15);
        ScaledFont.fixTextSize(changePointerColor, 22);

        changePointerColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pointerBlack) {
                    square.setImageResource(R.drawable.square_white);
                }
                else {
                    square.setImageResource(R.drawable.square);
                }
                pointerBlack = !pointerBlack;
            }
        });

        start();
    }

    private void start() {
        Uri uri = getIntent().getParcelableExtra("uri");

        square.setVisibility(View.INVISIBLE);

        Glide.with(GetFromImage.this)
                .load(uri)
                .into(image);

        image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache();

        redValue.setText("Red: 0");
        greenValue.setText("Green: 0");
        blueValue.setText("Blue: 0");

        hexValue.setText("Hex: 000000");

        color.setBackgroundColor(Color.parseColor("#000000"));

        setImageTouchListener(image);
    }

    private void setImageTouchListener(final ImageView image) {

        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE || event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (square.getVisibility() == View.INVISIBLE) {
                        square.setVisibility(View.VISIBLE);
                    }
                    try {
                        square.setX(image.getX() + event.getX() - PercentageSize.percentageHorizontal(3));
                        square.setY(image.getY() + event.getY() - PercentageSize.percentageHorizontal(3) * 5);

                        int x = (int) event.getX();
                        int y = (int) event.getY() - PercentageSize.percentageHorizontal(3) * 4;
                        Bitmap bitmap = image.getDrawingCache();
                        int pixel = bitmap.getPixel(x, y);

                        int red = Color.red(pixel);
                        int green = Color.green(pixel);
                        int blue = Color.blue(pixel);

                        redValue.setText("Red: " + red);
                        greenValue.setText("Green: " + green);
                        blueValue.setText("Blue: " + blue);

                        String redHex = String.format("%02X", red);
                        String greenHex = String.format("%02X", green);
                        String blueHex = String.format("%02X", blue);

                        hexValue.setText("Hex: " + redHex.toUpperCase() + greenHex.toUpperCase() + blueHex.toUpperCase());

                        color.setBackgroundColor(Color.parseColor("#" + redHex + greenHex + blueHex));
                    }
                    catch (Exception e) {

                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.plaltair.getcolor/databases/database", null, SQLiteDatabase.OPEN_READWRITE);

            String query = "INSERT INTO Colors (Hex) Values ('" + hexValue.getText().toString().replace("Hex: ", "") + "');";

            try {
                database.execSQL(query);
                Toast.makeText(GetFromImage.this, getResources().getString(R.string.colorSaved), Toast.LENGTH_SHORT).show();
            }
            catch (SQLiteConstraintException e) {
                Toast.makeText(GetFromImage.this, getResources().getString(R.string.alreadySavedColor), Toast.LENGTH_SHORT).show();
            }

            database.close();
        }
        else if (id == android.R.id.home) {
            super.onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(GetFromImage.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(GetFromImage.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }
}
