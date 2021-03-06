package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.example.ex192_vaccineapp.FBref.refStudents;

import java.util.ArrayList;
import java.util.Objects;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		26/03/2021
 *
 *  * This SortActivity.class displays the sorting options and the results
 *  and a menu move to the whole activities
 *  */
public class SortActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView showOptions, showResult;
    ArrayList<String> results, details, statusList;
    String[] options;
    ArrayAdapter<String> adp;
    CustomAdapter customadp;
    // For the Queries
    ValueEventListener vel;
    int selectedClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        showOptions = findViewById(R.id.showOptions);
        showResult = findViewById(R.id.showResult);

        showOptions.setOnItemClickListener(this);
        showOptions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        results = new ArrayList<>();
        details = new ArrayList<>();
        statusList = new ArrayList<>();
        // The options to display on the showOptions ListView object
        options = new String[]{"Options","All vaccinated students", "All allergic", "Order by grade", "Order by class"};

        adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, options);
        showOptions.setAdapter(adp);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String optionsDescription = options[position];
        showResult.setAdapter(null);

        switch (optionsDescription) {
            case "Options":{
                showResult.setAdapter(null);

                break;
            }

            case "All vaccinated students":{
                Query q = refStudents.orderByChild("isAllergic").equalTo(false);
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        results.clear();
                        statusList.clear();
                        details.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                            details.add("Class: "+ studentTemp.getClassNum() + " Grade: " + studentTemp.getGradeNum());


                            if (studentTemp.getV2() != null){
                                statusList.add("All the vaccines were documented");
                            }
                            else{
                                statusList.add("First vaccination was documented");
                            }
                        }
                        customadp = new CustomAdapter(getApplicationContext(), results, details, statusList);
                        showResult.setAdapter(customadp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                q.addListenerForSingleValueEvent(vel);

                break;
            }

            case "All allergic": {
                Query q = refStudents.orderByChild("isAllergic").equalTo(true);
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        results.clear();
                        statusList.clear();
                        details.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                            details.add("Class: "+ studentTemp.getClassNum() + " Grade: " + studentTemp.getGradeNum());
                            statusList.add("Allergic student");
                        }
                        customadp = new CustomAdapter(getApplicationContext(), results, details, statusList);
                        showResult.setAdapter(customadp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                q.addListenerForSingleValueEvent(vel);

                break;
            }

            case "Order by grade":{
                openAlertDialogAndDisplay();
                break;
            }

            case "Order by class":{
                Query q = refStudents.orderByChild("classNum");
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        results.clear();
                        statusList.clear();
                        details.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            if (!Objects.requireNonNull(studentTemp).getIsAllergic()){
                                results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                                details.add("Class: "+ studentTemp.getClassNum() + " Grade: " + studentTemp.getGradeNum());

                                if (studentTemp.getV2() != null){
                                    statusList.add("All the vaccines were documented");
                                }
                                else{
                                    statusList.add("First vaccination was documented");
                                }
                            }
                        }
                        customadp = new CustomAdapter(getApplicationContext(), results, details, statusList);
                        showResult.setAdapter(customadp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                q.addListenerForSingleValueEvent(vel);

                break;
            }

            default:{ }
        }
    }

    /**
     * Open openAlertDialogAndDisplay method is opening an alertDialog in oder to get the class number to display
     * 'Order by grade' option.
     *
     */
    private void openAlertDialogAndDisplay() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final EditText classET = new EditText(this);
        classET.setInputType(InputType.TYPE_CLASS_NUMBER);
        adb.setTitle("Enter the class: (7 - 12)");
        adb.setView(classET);
        adb.setCancelable(false);
        adb.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        adb.setPositiveButton("CHOOSE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedClass = Integer.parseInt(classET.getText().toString());
                if (selectedClass >= 7 && selectedClass <= 12) {
                    Query q = refStudents.orderByChild("classNum").equalTo(selectedClass);
                    vel = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dS) {
                            results.clear();
                            statusList.clear();
                            details.clear();
                            for (DataSnapshot data : dS.getChildren()) {
                                Student studentTemp = data.getValue(Student.class);
                                if (!Objects.requireNonNull(studentTemp).getIsAllergic()) {
                                    results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                                    details.add("Class: " + studentTemp.getClassNum() + " Grade: " + studentTemp.getGradeNum());

                                    if (studentTemp.getV2() != null) {
                                        statusList.add("All the vaccines were documented");
                                    } else {
                                        statusList.add("First vaccination was documented");
                                    }
                                }
                            }
                            customadp = new CustomAdapter(getApplicationContext(), results, details, statusList);
                            showResult.setAdapter(customadp);
                            if (results.isEmpty()){
                                Toast.makeText(SortActivity.this, "No students in this class", Toast.LENGTH_SHORT).show();
                            }
                            selectedClass = 0;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    };
                    q.addListenerForSingleValueEvent(vel);
                }
                else{
                    Toast.makeText(SortActivity.this, "Enter correctly the class number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog ad = adb.create();
        ad.show();
    }


    @Override
    protected void onPause() {
        if (vel != null) {
            refStudents.removeEventListener(vel);
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        Intent si;

        if (id == R.id.Input){
            si = new Intent(this, InputActivity.class);
            startActivity(si);
        }
        else if (id == R.id.UpgradeData){
            si = new Intent(this, UpdateActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Credits){
            si = new Intent(this, CreditsActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}