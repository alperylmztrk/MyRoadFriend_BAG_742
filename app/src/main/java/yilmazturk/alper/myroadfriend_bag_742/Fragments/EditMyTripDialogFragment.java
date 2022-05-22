package yilmazturk.alper.myroadfriend_bag_742.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

import yilmazturk.alper.myroadfriend_bag_742.Model.Time;
import yilmazturk.alper.myroadfriend_bag_742.Model.Trip;
import yilmazturk.alper.myroadfriend_bag_742.R;


public class EditMyTripDialogFragment extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "EditMyTripDialogFrgmnt";

    SwitchMaterial switchMon, switchTue, switchWed, switchThu, switchFri;
    TextView monInTxt, monOutTxt, tueInTxt, tueOutTxt, wedInTxt, wedOutTxt, thuInTxt, thuOutTxt, friInTxt, friOutTxt;
    TextView uniName;
    private int hour, minute;
    Button btnCancel, btnSave;

    private Trip trip;

    public EditMyTripDialogFragment() {
        // Required empty public constructor
    }

    public EditMyTripDialogFragment(Trip trip) {
        this.trip = trip;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Edit Trip");
        LayoutInflater inflater = getLayoutInflater();
        View editMyTripDialog = inflater.inflate(R.layout.fragment_edit_my_trip_dialog, null);

        switchMon = editMyTripDialog.findViewById(R.id.editTripSwitchMon);
        switchTue = editMyTripDialog.findViewById(R.id.editTripSwitchTue);
        switchWed = editMyTripDialog.findViewById(R.id.editTripSwitchWed);
        switchThu = editMyTripDialog.findViewById(R.id.editTripSwitchThu);
        switchFri = editMyTripDialog.findViewById(R.id.editTripSwitchFri);
        monInTxt = editMyTripDialog.findViewById(R.id.editTripMonCheckIn);
        monOutTxt = editMyTripDialog.findViewById(R.id.editTripMonCheckOut);
        tueInTxt = editMyTripDialog.findViewById(R.id.editTripTueCheckIn);
        tueOutTxt = editMyTripDialog.findViewById(R.id.editTripTueCheckOut);
        wedInTxt = editMyTripDialog.findViewById(R.id.editTripWedCheckIn);
        wedOutTxt = editMyTripDialog.findViewById(R.id.editTripWedCheckOut);
        thuInTxt = editMyTripDialog.findViewById(R.id.editTripThuCheckIn);
        thuOutTxt = editMyTripDialog.findViewById(R.id.editTripThuCheckOut);
        friInTxt = editMyTripDialog.findViewById(R.id.editTripFriCheckIn);
        friOutTxt = editMyTripDialog.findViewById(R.id.editTripFriCheckOut);
        uniName = editMyTripDialog.findViewById(R.id.editTripUniName);
        btnCancel = editMyTripDialog.findViewById(R.id.btnCancelEditTrip);
        btnSave = editMyTripDialog.findViewById(R.id.btnSaveEditTrip);

        builder.create().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

        setDataOfTrip();

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateMyTripInfo();
                getDialog().dismiss();
                getActivity().recreate();
            }
        });

        builder.setView(editMyTripDialog);

        return builder.create();
    }

    ArrayList<String> days = new ArrayList<>();
    //0 for monTime 4 for friTime
    ArrayList<Time> times = new ArrayList<>();

    private void setDataOfTrip() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        database.child("Trips").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot tds : snapshot.getChildren()) {
                    for (DataSnapshot uniTds : tds.getChildren()) {
                        String tripID = uniTds.child("tripID").getValue().toString();
                        if (tripID.equals(trip.getTripID())) {
                            uniName.setText(tds.getKey());
                            for (DataSnapshot timeDS : uniTds.child("Time").getChildren()) {
                                Time time = timeDS.getValue(Time.class);
                                String day = timeDS.getKey();

                                if (day.equals("Monday")) {
                                    monInTxt.setText(time.getCheckIn());
                                    monOutTxt.setText(time.getCheckOut());
                                    switchMon.setChecked(true);
                                }
                                if (day.equals("Tuesday")) {
                                    tueInTxt.setText(time.getCheckIn());
                                    tueOutTxt.setText(time.getCheckOut());
                                    switchTue.setChecked(true);
                                }
                                if (day.equals("Wednesday")) {
                                    wedInTxt.setText(time.getCheckIn());
                                    wedOutTxt.setText(time.getCheckOut());
                                    switchWed.setChecked(true);
                                }
                                if (day.equals("Thursday")) {
                                    thuInTxt.setText(time.getCheckIn());
                                    thuOutTxt.setText(time.getCheckOut());
                                    switchThu.setChecked(true);
                                }
                                if (day.equals("Friday")) {
                                    friInTxt.setText(time.getCheckIn());
                                    friOutTxt.setText(time.getCheckOut());
                                    switchFri.setChecked(true);
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void updateMyTripInfo() {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

        DatabaseReference timeRef = database.child("Trips").child(uniName.getText().toString()).child(trip.getTripID()).child("Time");

        if (days.contains("Monday")) {
            timeRef.child("Monday").setValue(times.get(0));
        } else {
            timeRef.child("Monday").getRef().removeValue();
        }
        if (days.contains("Tuesday")) {
            timeRef.child("Tuesday").setValue(times.get(1));
        } else {
            timeRef.child("Tuesday").getRef().removeValue();
        }
        if (days.contains("Wednesday")) {
            timeRef.child("Wednesday").setValue(times.get(2));
        } else {
            timeRef.child("Wednesday").getRef().removeValue();
        }
        if (days.contains("Thursday")) {
            timeRef.child("Thursday").setValue(times.get(3));
        } else {
            timeRef.child("Thursday").getRef().removeValue();
        }
        if (days.contains("Friday")) {
            timeRef.child("Friday").setValue(times.get(4));
        } else {
            timeRef.child("Friday").getRef().removeValue();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.editTripSwitchMon:
                if (isChecked) {
                    days.add(switchMon.getText().toString());
                    monInTxt.setVisibility(View.VISIBLE);
                    monOutTxt.setVisibility(View.VISIBLE);
                } else {
                    days.remove("Monday");
                    monInTxt.setVisibility(View.INVISIBLE);
                    monOutTxt.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.editTripSwitchTue:
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
            case R.id.editTripSwitchWed:
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
            case R.id.editTripSwitchThu:
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
            case R.id.editTripSwitchFri:
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
        times.add(0, new Time(monInTxt.getText().toString(), monOutTxt.getText().toString()));
        times.add(1, new Time(tueInTxt.getText().toString(), tueOutTxt.getText().toString()));
        times.add(2, new Time(wedInTxt.getText().toString(), wedOutTxt.getText().toString()));
        times.add(3, new Time(thuInTxt.getText().toString(), thuOutTxt.getText().toString()));
        times.add(4, new Time(friInTxt.getText().toString(), friOutTxt.getText().toString()));

    }

    String day;
    String check;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editTripMonCheckIn:
                day = "monday";
                check = "check-in";
                break;
            case R.id.editTripMonCheckOut:
                day = "monday";
                check = "check-out";
                break;
            case R.id.editTripTueCheckIn:
                day = "tuesday";
                check = "check-in";
                break;
            case R.id.editTripTueCheckOut:
                day = "tuesday";
                check = "check-out";
                break;
            case R.id.editTripWedCheckIn:
                day = "wednesday";
                check = "check-in";
                break;
            case R.id.editTripWedCheckOut:
                day = "wednesday";
                check = "check-out";
                break;
            case R.id.editTripThuCheckIn:
                day = "thursday";
                check = "check-in";
                break;
            case R.id.editTripThuCheckOut:
                day = "thursday";
                check = "check-out";
                break;
            case R.id.editTripFriCheckIn:
                day = "friday";
                check = "check-in";
                break;
            case R.id.editTripFriCheckOut:
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
                    case R.id.editTripMonCheckIn:
                        monInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripMonCheckOut:
                        monOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripTueCheckIn:
                        tueInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripTueCheckOut:
                        tueOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripWedCheckIn:
                        wedInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripWedCheckOut:
                        wedOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripThuCheckIn:
                        thuInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripThuCheckOut:
                        thuOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripFriCheckIn:
                        friInTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.editTripFriCheckOut:
                        friOutTxt.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                }

                times.add(0, new Time(monInTxt.getText().toString(), monOutTxt.getText().toString()));
                times.add(1, new Time(tueInTxt.getText().toString(), tueOutTxt.getText().toString()));
                times.add(2, new Time(wedInTxt.getText().toString(), wedOutTxt.getText().toString()));
                times.add(3, new Time(thuInTxt.getText().toString(), thuOutTxt.getText().toString()));
                times.add(4, new Time(friInTxt.getText().toString(), friOutTxt.getText().toString()));
            }
        };

        int style = AlertDialog.THEME_HOLO_LIGHT;
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), style, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select " + day + " " + check);
        timePickerDialog.show();
    }

}