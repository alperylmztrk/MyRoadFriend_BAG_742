package yilmazturk.alper.myroadfriend_bag_742;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

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

import yilmazturk.alper.myroadfriend_bag_742.Model.UniList;

public class SearchTripActivity extends AppCompatActivity {

    Spinner uniSpinner;
    int spinSelectPos;
    TextView uniDestTxtView;
    UniList uniList;
    List<String> spinnerData;
    ArrayAdapter<String> dataAdapter;
    String selUniName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trip);

        uniSpinner= findViewById(R.id.searchTripUniSpinner);
        uniDestTxtView=findViewById(R.id.textViewUniDest);

        uniList=new UniList();
        spinnerData=new ArrayList<>();

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
        uniSpinner.setAdapter(dataAdapter);

        uniSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinSelectPos = position;
                for (int i = 0; i < uniList.getUniDetailList().size(); i++) {
                    if (spinnerData.get(position).equals(uniList.getUniDetailList().get(i).getName())) {
                        selUniName = uniList.getUniDetailList().get(i).getName();
                        uniDestTxtView.setText(selUniName);
                        Log.d("if", uniList.getUniDetailList().get(i).getName() + " Şehir: " + uniList.getUniDetailList().get(i).getCity());

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}