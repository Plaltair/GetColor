package com.plaltair.getcolor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.plaltair.getcolor.colorpicker.ColorPickerDialog;
import com.plaltair.getcolor.colorpicker.ColorPickerDialogListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main extends AppCompatActivity implements ColorPickerDialogListener {

    private final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private int red = 0;
    private int green = 0;
    private int blue = 0;

    private String photoPath;

    private EditText editTextRed;
    private EditText editTextGreen;
    private EditText editTextBlue;
    private EditText editTextHex;
    private SeekBar seekRed;
    private SeekBar seekGreen;
    private SeekBar seekBlue;
    private View color;

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File file = getDatabasePath("database");

        if (!file.exists()) {
            SQLiteDatabase database = openOrCreateDatabase("database", MODE_PRIVATE, null);

            String query = "CREATE TABLE Colors (Hex CHARACTER(6) PRIMARY KEY);";

            database.execSQL(query);

            database.close();
        }

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int id = menuItem.getItemId();

                        if (id == R.id.action_get_color_from_image) {
                            getImage();
                        }
                        else if (id == R.id.action_take_photo) {
                            takeAPhoto();
                        }
                        else if (id == R.id.action_get_saved_colors) {
                            editTextRed.clearFocus();
                            editTextGreen.clearFocus();
                            editTextBlue.clearFocus();
                            editTextHex.clearFocus();

                            startActivity(new Intent(Main.this, GetSavedColors.class));
                        }
                        else if (id == R.id.action_get_my_pictures) {
                            startActivity(new Intent(Main.this, MyPictures.class));
                        }

                        mDrawerLayout.closeDrawers();

                        return false;
                    }
                });

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.menu_icon);


        final TextView redText = findViewById(R.id.redText);
        final TextView greenText = findViewById(R.id.greenText);
        final TextView blueText = findViewById(R.id.blueText);
        seekRed = findViewById(R.id.seekRed);
        seekGreen = findViewById(R.id.seekGreen);
        seekBlue = findViewById(R.id.seekBlue);
        color = findViewById(R.id.color);
        editTextRed = findViewById(R.id.editTextRed);
        editTextGreen = findViewById(R.id.editTextGreen);
        editTextBlue = findViewById(R.id.editTextBlue);
        editTextHex = findViewById(R.id.editTextHex);
        final Button other = findViewById(R.id.other);

        ScaledFont.fixTextSize(other, 20);

        ScaledFont.fixTextSize(redText, 13);
        ScaledFont.fixTextSize(greenText, 13);
        ScaledFont.fixTextSize(blueText, 13);

        ScaledFont.fixTextSize(editTextRed, 13);
        ScaledFont.fixTextSize(editTextGreen, 13);
        ScaledFont.fixTextSize(editTextBlue, 13);
        ScaledFont.fixTextSize(editTextHex, 13);

        other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog.newBuilder()
                        .setDialogTitle(R.string.chooseAColor)
                        .setPresetsButtonText(R.string.defaultColors)
                        .setSelectedButtonText(R.string.confirm)
                        .setCustomButtonText(R.string.customColor)
                        .show(Main.this);
            }
        });

        seekRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextRed.clearFocus();
                    editTextGreen.clearFocus();
                    editTextBlue.clearFocus();
                    editTextHex.clearFocus();
                    red = progress;
                    updateViewColor(red, green, blue);
                    updateTexts(red, green, blue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextRed.clearFocus();
                    editTextGreen.clearFocus();
                    editTextBlue.clearFocus();
                    editTextHex.clearFocus();
                    green = progress;
                    updateViewColor(red, green, blue);
                    updateTexts(red, green, blue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextRed.clearFocus();
                    editTextGreen.clearFocus();
                    editTextBlue.clearFocus();
                    editTextHex.clearFocus();
                    blue = progress;
                    updateViewColor(red, green, blue);
                    updateTexts(red, green, blue);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editTextRed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextRed.hasFocus()) {
                    if (s.toString().equals("")) {
                        red = 0;
                    }
                    else {
                        red = Integer.valueOf(s.toString());
                    }

                    if (red > 255) {
                        red = 255;
                        editTextRed.setText("255");
                        editTextRed.setSelection(3);
                    }

                    updateViewColor(red, green, blue);
                    updateHex(red, green, blue);
                    updateSeekBars(red, green, blue);
                }
            }
        });
        editTextGreen.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextGreen.hasFocus()) {
                    if (s.toString().equals("")) {
                        green = 0;
                    }
                    else {
                        green = Integer.valueOf(s.toString());
                    }

                    if (green > 255) {
                        green = 255;
                        editTextGreen.setText("255");
                        editTextGreen.setSelection(3);
                    }

                    updateViewColor(red, green, blue);
                    updateHex(red, green, blue);
                    updateSeekBars(red, green, blue);
                }
            }
        });
        editTextBlue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editTextBlue.hasFocus()) {
                    if (s.toString().equals("")) {
                        blue = 0;
                    }
                    else {
                        blue = Integer.valueOf(s.toString());
                    }

                    if (blue > 255) {
                        blue = 255;
                        editTextBlue.setText("255");
                        editTextBlue.setSelection(3);
                    }

                    updateViewColor(red, green, blue);
                    updateHex(red, green, blue);
                    updateSeekBars(red, green, blue);
                }
            }
        });
        editTextHex.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() == 6 && editTextHex.hasFocus()) {
                    updateRedGreenBlue(s.toString());
                    updateSeekBars(red, green, blue);
                    updateViewColor(red, green, blue);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            Intent intent = new Intent(Main.this, GetFromImage.class);
            intent.putExtra("uri", uri);
            startActivity(intent);
        }
        else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(Main.this, GetFromImage.class);
                intent.putExtra("uri", Uri.fromFile(new File(photoPath)));
                startActivity(intent);
            }
            else {
                File file = new File(photoPath);
                file.delete();
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = new File(getFilesDir() + "/SavedPhotos");

        if (!storageDir.exists()) {
            storageDir.mkdir();
        }

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        photoPath = image.getAbsolutePath();

        return image;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText editTextRed = findViewById(R.id.editTextRed);
        EditText editTextGreen = findViewById(R.id.editTextGreen);
        EditText editTextBlue = findViewById(R.id.editTextBlue);
        EditText editTextHex = findViewById(R.id.editTextHex);

        int id = item.getItemId();

        if (id == R.id.action_save) {
            editTextRed.clearFocus();
            editTextGreen.clearFocus();
            editTextBlue.clearFocus();
            editTextHex.clearFocus();

            if (editTextHex.getText().toString().length() == 6) {

                SQLiteDatabase database = SQLiteDatabase.openDatabase("/data/data/com.plaltair.getcolor/databases/database", null, SQLiteDatabase.OPEN_READWRITE);

                String query = "INSERT INTO Colors (Hex) Values ('" + editTextHex.getText().toString() + "');";

                try {
                    database.execSQL(query);
                    Toast.makeText(Main.this, getResources().getString(R.string.colorSaved), Toast.LENGTH_SHORT).show();
                }
                catch (SQLiteConstraintException e) {
                    Toast.makeText(Main.this, getResources().getString(R.string.alreadySavedColor), Toast.LENGTH_SHORT).show();
                }

                database.close();
            }
            else {
                Toast.makeText(Main.this, getResources().getString(R.string.errorColor), Toast.LENGTH_SHORT).show();
            }
        }
        else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getImage() {
        if (!checkPermission()) {
            requestPermission();
        }
        else {
            Intent i = new Intent(Intent.ACTION_PICK);
            i.setType("image/*");
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
    }

    private void takeAPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            }
            catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.plaltair.getcolor.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private void updateViewColor( int red, int green, int blue) {
        String redHex = String.format("%02X", red);
        String greenHex = String.format("%02X", green);
        String blueHex = String.format("%02X", blue);

        color.setBackgroundColor(Color.parseColor("#" + redHex + greenHex + blueHex));
    }

    private void updateTexts(int red, int green, int blue) {
        editTextRed.setText(String.valueOf(red));
        editTextGreen.setText(String.valueOf(green));
        editTextBlue.setText(String.valueOf(blue));

        String redHex = String.format("%02X", red);
        String greenHex = String.format("%02X", green);
        String blueHex = String.format("%02X", blue);

        String hex = redHex + greenHex + blueHex;

        editTextHex.setText(hex);
    }

    private void updateRedGreenBlue(String hexString) {
        String redHex = hexString.substring(0, 2);
        String greenHex = hexString.substring(2, 4);
        String blueHex = hexString.substring(4, 6);

        red = Integer.decode("0x" + redHex);
        green = Integer.decode("0x" + greenHex);
        blue = Integer.decode("0x" + blueHex);

        String redValue = String.valueOf(red);
        String greenValue = String.valueOf(green);
        String blueValue = String.valueOf(blue);

        editTextRed.setText(redValue);
        editTextGreen.setText(greenValue);
        editTextBlue.setText(blueValue);
    }

    private void updateHex(int red, int green , int blue) {
        String redHex = String.format("%02X", red);
        String greenHex = String.format("%02X", green);
        String blueHex = String.format("%02X", blue);

        String hex = redHex + greenHex + blueHex;

        editTextHex.setText(hex);
    }

    private void updateSeekBars(int red, int green, int blue) {
        seekRed.setProgress(red);
        seekGreen.setProgress(green);
        seekBlue.setProgress(blue);
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(Main.this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else {
            return false;
        }
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Main.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        String hexString = Integer.toHexString(color).toUpperCase().substring(2);

        String redHex = hexString.substring(0, 2);
        String greenHex = hexString.substring(2, 4);
        String blueHex = hexString.substring(4, 6);

        red = Integer.decode("0x" + redHex);
        green = Integer.decode("0x" + greenHex);
        blue = Integer.decode("0x" + blueHex);

        String redValue = String.valueOf(red);
        String greenValue = String.valueOf(green);
        String blueValue = String.valueOf(blue);

        updateRedGreenBlue(hexString);
        updateHex(red, green , blue);
        updateSeekBars(red, green, blue);
        updateViewColor(red, green , blue);
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }
}
