package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

import static com.example.ex192_vaccineapp.FBref.refStudents;

public class InputActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    ToggleButton tbt;
    EditText nameET, familyET, classET, gradeET, locationET;
    LinearLayout vaccineLayoutView;
    TextView displayDate;
    Spinner sp;
    String [] vaccineTypes;
    ArrayAdapter<String> adp;
    int currentPosVac;

    Intent gi;
    Student studentToDisplay;
    String dateStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        tbt = findViewById(R.id.tbt);
        nameET = findViewById(R.id.nameET);
        familyET = findViewById(R.id.familyET);
        classET = findViewById(R.id.classET);
        gradeET = findViewById(R.id.gradeET);
        locationET = findViewById(R.id.locationET);
        vaccineLayoutView = findViewById(R.id.vaccineLayoutView);
        displayDate = findViewById(R.id.displayDate);
        sp = findViewById(R.id.sp);

        vaccineTypes = new String[]{"First Vac.", "Second Vac."};
        sp.setOnItemSelectedListener(this);
        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, vaccineTypes);
        sp.setAdapter(adp);

        classET.setHint("Class");
        gradeET.setHint("Grade");

        gi = getIntent();
        if (gi != null){
            String studentTitle = gi.getStringExtra("StudentTitle");
            boolean status = gi.getBooleanExtra("AllergicStatus", false);
            if (status){
                tbt.setChecked(true);
                vaccineLayoutView.setVisibility(View.INVISIBLE);
            }
            else{
                tbt.setChecked(false);
                vaccineLayoutView.setVisibility(View.VISIBLE);
            }
            studentToDisplay = new Student();
            refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dS) {
                    if (dS.exists()){
                        Student temp = new Student();
                        for (DataSnapshot data : dS.getChildren()){
                            if (Objects.requireNonNull(data.getKey()).equals(studentTitle)){
                                temp = data.getValue(Student.class);
                            }
                        }
                        nameET.setText(Objects.requireNonNull(temp).getName());
                        familyET.setText(temp.getFamilyName());
                        if (temp.getClassNum() == 0){
                            classET.setText("");
                        }
                        else{
                            classET.setText(String.valueOf(temp.getGradeNum()));
                        }

                        if (temp.getGradeNum() == 0){
                            gradeET.setText("");
                        }
                        else{
                            gradeET.setText(String.valueOf(temp.getGradeNum()));
                        }
                        studentToDisplay = temp;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });

            if (studentToDisplay.getV1() != null){
                locationET.setText(studentToDisplay.getV1().getVaccineLocation());
                displayDate.setText(studentToDisplay.getV1().getDate());
            }
        }
    }

    public void openCalendar(View view) {
        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month ++;
                dateStr = dayOfMonth+"/"+month+"/"+year;
                displayDate.setText(dateStr);
            }
        }, year, month, day);

        dpd.show();

    }

    public void changeLayout(View view) {
        if (tbt.isChecked()){
            vaccineLayoutView.setVisibility(View.INVISIBLE);
        }
        else {
            vaccineLayoutView.setVisibility(View.VISIBLE);
        }
        locationET.setText("");
        displayDate.setText("");
        dateStr = "";
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // this studentToDisplay isn't allergic student
        if (gi != null){
            if (Objects.isNull(studentToDisplay.getV1())){
                locationET.setText("");
                displayDate.setText("");
                dateStr = "";
            }
            else{
                if (position == 0){
                    locationET.setText(studentToDisplay.getV1().getVaccineLocation());
                    displayDate.setText(studentToDisplay.getV1().getDate());
                }
                else{
                    if (Objects.isNull(studentToDisplay.getV2())){
                        locationET.setText("");
                        displayDate.setText("");
                        dateStr = "";
                    }
                    else {
                        locationET.setText(studentToDisplay.getV2().getVaccineLocation());
                        displayDate.setText(studentToDisplay.getV2().getDate());
                    }
                }
            }
        }
        else{
            locationET.setText("");
            displayDate.setText("");
            dateStr = "";
        }
        currentPosVac = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void sendToFB(View view) {
        boolean checkData = checkAll();
        if (checkData){
            String name = nameET.getText().toString();
            String familyName = familyET.getText().toString();
            int classNum = Integer.parseInt(classET.getText().toString());
            int gradeNum = Integer.parseInt(gradeET.getText().toString());
            boolean isAllergic = tbt.isChecked();

            String title = name+""+familyName;
            boolean isExist = checkExistence(title); // checking in the fireBase based on the keyID

            if (isAllergic && !isExist){
                Student student2 = new Student(name, familyName, classNum, gradeNum, isAllergic, null, null);
                refStudents.child(title).setValue(student2);
                Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();

                nameET.setText("");
                familyET.setText("");
                classET.setText("");
                gradeET.setText("");
            }
            else  if (isAllergic)
                Toast.makeText(this, "This Student has been inserted", Toast.LENGTH_SHORT).show();
            else {
                isExist = checkExistence(title);
                if (vaccineTypes[currentPosVac].equals("Second Vac.") && !isExist){
                    String location = locationET.getText().toString();

                    if (location.isEmpty() || dateStr.isEmpty())
                        Toast.makeText(this, "Empty filed do not accepted.", Toast.LENGTH_SHORT).show();
                    else {
                        Vaccines v2  = new Vaccines(location, dateStr);
                        refStudents.child(title).child("v2").setValue(v2);
                        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();

                        nameET.setText("");
                        familyET.setText("");
                        classET.setText("");
                        gradeET.setText("");
                        dateStr = "";
                        locationET.setText("");
                        displayDate.setText("");
                    }
                }
                else if (vaccineTypes[currentPosVac].equals("First Vac.") && !isExist){
                    String location = locationET.getText().toString();
                    if (location.isEmpty() || dateStr.isEmpty())
                        Toast.makeText(this, "Empty filed do not accepted.", Toast.LENGTH_SHORT).show();
                    else {
                        Vaccines v1  = new Vaccines(location, dateStr);
                        Student student3 = new Student(name, familyName, classNum, gradeNum, isAllergic, v1, null);
                        refStudents.child(title).setValue(student3);
                        Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();
                    }
                }
                else if (vaccineTypes[currentPosVac].equals("First Vac.") && isExist){
                    Toast.makeText(this, "This Student has been inserted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Please enter the 1st vaccine before.", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Toast.makeText(this, "Enter the details as necessary", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkExistence(String studentKey) {
        boolean flag = false;
        ArrayList<String> stuIdList = new ArrayList<String>();

        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                for (DataSnapshot data: dS.getChildren()) {
                    stuIdList.add(data.getKey());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        int i = 0;
        while (i < stuIdList.size() && !flag){
            flag = stuIdList.get(i).equals(studentKey);
            i++;
        }

        return flag;
    }

    private boolean checkAll() {
        boolean flag = true;
        int classNum = Integer.parseInt(classET.getText().toString());
        int gradeNum = Integer.parseInt(gradeET.getText().toString());

        if (nameET.getText().toString().isEmpty() || familyET.getText().toString().isEmpty() || classET.getText().toString().isEmpty() || gradeET.getText().toString().isEmpty())
            flag = false;
        if (classNum < 6 || classNum > 13 || gradeNum < 0) flag = false;
        if (nameET.getText().toString().contains("\\d+") || familyET.getText().toString().startsWith("\\d+")) flag = false; // .contains("\\d+") - check if there is numbers in a String variable

        return flag;
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
        if (id == R.id.Credits){
            si = new Intent(this, CreditsActivity.class);
            startActivity(si);
        }
        else if (id == R.id.UpgradeData){
            si = new Intent(this, UpdateActivity.class);
            startActivity(si);
        }
        else if (id == R.id.sort){
            si = new Intent(this, SortActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    }
}