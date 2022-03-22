package yilmazturk.alper.myroadfriend_bag_742;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import yilmazturk.alper.myroadfriend_bag_742.Model.Driver;
import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;

public class PostTripActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    Spinner spinnerUni;
    int spinSelecPos;
    UniList uniList;
    List<String> spinnerData;
    ArrayAdapter<String> dataAdapter;
    SwitchMaterial switchMon, switchTue, switchWed, switchThu, switchFri;
    TextView monInTxt, monOutTxt, tueInTxt, tueOutTxt, wedInTxt, wedOutTxt, thuInTxt, thuOutTxt, friInTxt, friOutTxt;
    int hour, minute;
    Button btnPost;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference database;
    String selUniID;

    Driver driver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_trip);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        String userID = firebaseUser.getUid();
        database = FirebaseDatabase.getInstance().getReference();

        driver = new Driver();

        spinnerUni = findViewById(R.id.uniSpinner);
        uniList = new UniList();

        try {
            //Load File
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(this.getResources().openRawResource(R.raw.universities)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }

            Gson gson = new Gson();
            uniList = gson.fromJson(jsonBuilder.toString(), UniList.class);

        } catch (FileNotFoundException e) {
            Log.e("jsonFile", "file not found");
        } catch (IOException e) {
            Log.e("jsonFile", "IOerror");
        }

        spinnerData = new ArrayList<>();

        for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
            spinnerData.add(uniList.getUniDetailList().get(i).getName());
        }
        //Sorting the unis according to Turkish alphabet
        Collections.sort(spinnerData, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                Collator collator = Collator.getInstance(new Locale("tr", "TR"));
                return collator.compare(s1, s2);
            }
        });
        spinnerData.add(0, "Select your university");

        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_expandable_list_item_1);
        spinnerUni.setAdapter(dataAdapter);

        spinnerUni.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinSelecPos = position;
                for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
                    if (spinnerData.get(position).equals(uniList.getUniDetailList().get(i).getName())) {
                        selUniID = uniList.getUniDetailList().get(i).getId();
                        Log.d("for", uniList.getUniDetailList().get(i).getName() + " Şehir: " + uniList.getUniDetailList().get(i).getCity());

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switchMon = findViewById(R.id.switchMon);
        switchTue = findViewById(R.id.switchTue);
        switchWed = findViewById(R.id.switchWed);
        switchThu = findViewById(R.id.switchThu);
        switchFri = findViewById(R.id.switchFri);

        monInTxt = findViewById(R.id.monCheckIn);
        monOutTxt = findViewById(R.id.monCheckOut);
        tueInTxt = findViewById(R.id.tueCheckIn);
        tueOutTxt = findViewById(R.id.tueCheckOut);
        wedInTxt = findViewById(R.id.wedCheckIn);
        wedOutTxt = findViewById(R.id.wedCheckOut);
        thuInTxt = findViewById(R.id.thuCheckIn);
        thuOutTxt = findViewById(R.id.thuCheckOut);
        friInTxt = findViewById(R.id.friCheckIn);
        friOutTxt = findViewById(R.id.friCheckOut);

        btnPost = findViewById(R.id.btnPost);

        switchMon.setOnCheckedChangeListener(this);
        switchTue.setOnCheckedChangeListener(this);
        switchWed.setOnCheckedChangeListener(this);
        switchThu.setOnCheckedChangeListener(this);
        switchFri.setOnCheckedChangeListener(this);


        monInTxt.setOnClickListener(this);
        monOutTxt.setOnClickListener(this);
        tueInTxt.setOnClickListener(this);
        tueOutTxt.setOnClickListener(this);
        wedInTxt.setOnClickListener(this);
        wedOutTxt.setOnClickListener(this);
        thuInTxt.setOnClickListener(this);
        thuOutTxt.setOnClickListener(this);
        friInTxt.setOnClickListener(this);
        friOutTxt.setOnClickListener(this);


        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinSelecPos == 0) {
                    TextView errorText = (TextView) spinnerUni.getSelectedView();
                    errorText.setError("");
                    Toast.makeText(PostTripActivity.this, "Please select your university", Toast.LENGTH_SHORT).show();
                } else {
                    driver.postTrip(database, selUniID, days, userID, times);
                    startActivity(new Intent(PostTripActivity.this, MainActivity.class));
                }
            }
        });


    }


    ArrayList<String> days = new ArrayList<>();

    Time monTime, tueTime, wedTime, thuTime, friTime;
    //0 for monTime 4 for friTime
    ArrayList<Time> times = new ArrayList<>();


    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.switchMon:
                if (isChecked) {
                    days.add(switchMon.getText().toString());
                    monInTxt.setVisibility(View.VISIBLE);
                    monOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove(switchMon.getText().toString());
                    monInTxt.setVisibility(View.INVISIBLE);
                    monOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.switchTue:
                if (isChecked) {
                    days.add(switchTue.getText().toString());
                    tueInTxt.setVisibility(View.VISIBLE);
                    tueOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove(switchTue.getText().toString());
                    tueInTxt.setVisibility(View.INVISIBLE);
                    tueOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.switchWed:
                if (isChecked) {
                    days.add(switchWed.getText().toString());
                    wedInTxt.setVisibility(View.VISIBLE);
                    wedOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove(switchWed.getText().toString());
                    wedInTxt.setVisibility(View.INVISIBLE);
                    wedOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.switchThu:
                if (isChecked) {
                    days.add(switchThu.getText().toString());
                    thuInTxt.setVisibility(View.VISIBLE);
                    thuOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove(switchThu.getText().toString());
                    thuInTxt.setVisibility(View.INVISIBLE);
                    thuOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.switchFri:
                if (isChecked) {
                    days.add(switchFri.getText().toString());
                    friInTxt.setVisibility(View.VISIBLE);
                    friOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove(switchFri.getText().toString());
                    friInTxt.setVisibility(View.INVISIBLE);
                    friOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
        }
    }

    String day;
    String check;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.monCheckIn:
                day = "monday";
                check = "check-in";
                break;
            case R.id.monCheckOut:
                day = "monday";
                check = "check-out";
                break;
            case R.id.tueCheckIn:
                day = "tuesday";
                check = "check-in";
                break;
            case R.id.tueCheckOut:
                day = "tuesday";
                check = "check-out";
                break;
            case R.id.wedCheckIn:
                day = "wednesday";
                check = "check-in";
                break;
            case R.id.wedCheckOut:
                day = "wednesday";
                check = "check-out";
                break;
            case R.id.thuCheckIn:
                day = "thursday";
                check = "check-in";
                break;
            case R.id.thuCheckOut:
                day = "thursday";
                check = "check-out";
                break;
            case R.id.friCheckIn:
                day = "friday";
                check = "check-in";
                break;
            case R.id.friCheckOut:
                day = "friday";
                check = "check-out";
                break;
        }

        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                hour = selectedHour;
                minute = selectedMinute;

                switch (view.getId()) {
                    case R.id.monCheckIn:
                        monInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.monCheckOut:
                        monOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.tueCheckIn:
                        tueInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.tueCheckOut:
                        tueOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.wedCheckIn:
                        wedInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.wedCheckOut:
                        wedOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.thuCheckIn:
                        thuInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.thuCheckOut:
                        thuOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.friCheckIn:
                        friInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.friCheckOut:
                        friOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                }
                monTime = new Time(monInTxt.getText().toString(), monOutTxt.getText().toString());
                tueTime = new Time(tueInTxt.getText().toString(), tueOutTxt.getText().toString());
                wedTime = new Time(wedInTxt.getText().toString(), wedOutTxt.getText().toString());
                thuTime = new Time(thuInTxt.getText().toString(), thuOutTxt.getText().toString());
                friTime = new Time(friInTxt.getText().toString(), friOutTxt.getText().toString());

                times.add(0, monTime);
                times.add(1, tueTime);
                times.add(2, wedTime);
                times.add(3, thuTime);
                times.add(4, friTime);
                Log.i("Timemon", times.get(0).getCheckIn() + " " + times.get(0).getCheckOut());
                Log.i("Timetue", times.get(1).getCheckIn() + " " + times.get(1).getCheckOut());
                Log.i("Timewed", times.get(2).getCheckIn() + " " + times.get(2).getCheckOut());
                Log.i("Timethu", times.get(3).getCheckIn() + " " + times.get(3).getCheckOut());
                Log.i("Timefri", times.get(4).getCheckIn() + " " + times.get(4).getCheckOut());

            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(PostTripActivity.this, style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select " + day + " " + check);
        timePickerDialog.show();

    }


}