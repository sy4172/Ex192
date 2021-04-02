package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.VisibilitySetterAction;
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

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		19/03/2021
 *
 *  * This InputActivity.class displays the input screen and make the submitting actions
 *  and a menu move to the whole activities
 *  */
public class InputActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, AdapterView.OnItemSelectedListener {

    ToggleButton tbt;
    EditText nameET, familyET, classET, gradeET, locationET;
    LinearLayout vaccineLayoutView;
    TextView displayDate;
    Spinner sp;
    String [] vaccineTypes;
    ArrayList<String> stuIdList;
    ArrayAdapter<String> adp;
    int currentPosVac;

    // For the update mode
    boolean updateMode;
    String studentTitle;
    Vaccines vac1, vac2;

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

        // To display on a Spinner object the types of the vaccines
        vaccineTypes = new String[]{"1st Vac.", "2nd Vac."};
        sp.setOnItemSelectedListener(this);
        adp = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, vaccineTypes);
        sp.setAdapter(adp);

        classET.setHint("Class");
        gradeET.setHint("Grade");

        stuIdList = new ArrayList<String>();

        // What is getting from the UpdateActivity.class after the 'Update Data' was selected
        gi = getIntent();
        if (gi != null){
            studentTitle = gi.getStringExtra("StudentTitle");
            boolean status = gi.getBooleanExtra("AllergicStatus", false);
            updateMode = gi.getBooleanExtra("updateMode", false);
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
                            classET.setText(String.valueOf(temp.getClassNum()));
                        }

                        if (temp.getGradeNum() == 0){
                            gradeET.setText("");
                        }
                        else{
                            gradeET.setText(String.valueOf(temp.getGradeNum()));
                        }

                        if (temp.getV1() != null){
                            locationET.setText(temp.getV1().getVaccineLocation());
                            displayDate.setText(temp.getV1().getDate());
                        }
                        studentToDisplay = temp;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
    }

    /**
     * Open calendar method is opening the Calendar object in order to get the date of the vaccine.
     * And display the selected date on the layout.
     * @param view the DatePickerDialog object
     */
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
    }

    /**
     * Change layout method is changing the layout of this activity based on the ToggleButton object.
     * And clearing the selected date and location
     *
     * @param view the ToggleButton object
     */
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

    /**
     * sendToFB method is sending the student information into the FireBase.
     * By checking the input and the existence of a student and making au update student's details.
     *
     * @param view the view
     */
    public void sendToFB(View view) {
        boolean checkData = checkAll();
        if (checkData){
            String name = nameET.getText().toString();
            String familyName = familyET.getText().toString();
            int classNum = Integer.parseInt(classET.getText().toString());
            int gradeNum = Integer.parseInt(gradeET.getText().toString());
            boolean isAllergic = tbt.isChecked();

            if (!updateMode){
                String title = classNum+""+name+""+familyName+""+gradeNum;
                boolean isExist = checkExistence(title); // true  - if the student exist otherwise - false

                if (isAllergic && !isExist){
                    Student student2 = new Student(name, familyName, classNum, gradeNum, isAllergic, null, null);
                    refStudents.child(title).setValue(student2);
                    Toast.makeText(this, "Saved successfully", Toast.LENGTH_SHORT).show();

                    nameET.setText("");
                    familyET.setText("");
                    classET.setText("");
                    gradeET.setText("");
                }
                else if (isAllergic)
                    Toast.makeText(this, "This student has been inserted", Toast.LENGTH_SHORT).show();
                else {
                    isExist = checkExistence(title);
                    if (vaccineTypes[currentPosVac].equals("2nd Vac.") && isExist){
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
                    else if (vaccineTypes[currentPosVac].equals("1st Vac.") && !isExist){
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
                    else if (vaccineTypes[currentPosVac].equals("1st Vac.") && isExist){
                        Toast.makeText(this, "This Student has been inserted", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(this, "Please enter the 1st vaccine before.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            else{
                refStudents.child(studentTitle).removeValue(); // Removing the student with the previous details
                vac1 = new Vaccines();
                vac2 = new Vaccines();
                String location = locationET.getText().toString();
                dateStr = displayDate.getText().toString();
                if (dateStr.isEmpty() || location.isEmpty()){
                    Toast.makeText(this, "Empty filed do not accepted.", Toast.LENGTH_SHORT).show();
                }

                if (currentPosVac == 0){
                    vac1.setDate(dateStr);
                    vac1.setVaccineLocation(location);
                    if (studentToDisplay.getV2() != null){
                        vac2 = studentToDisplay.getV2();
                    }
                }
                else if (currentPosVac == 1){
                    vac1 = studentToDisplay.getV1();
                    vac2.setVaccineLocation(location);
                    vac2.setDate(dateStr);
                }
                else if (vac1 == null){
                    Toast.makeText(this, "Please enter the 1st vaccine", Toast.LENGTH_SHORT).show();
                }

                Student studentToUpdate = new Student(name, familyName, classNum, gradeNum, isAllergic, vac1, vac2);
                studentTitle = classNum+""+name+""+familyName+""+gradeNum;
                refStudents.child(studentTitle).setValue(studentToUpdate);
                Toast.makeText(this, name+" "+familyName+" has successfully saved", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(this, "Enter the details as necessary", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * The checkExistence method checks if the student is exist in the FireBase and
     * returns false if he isn't, otherwise, true.
     *
     * @param studentKey the key of the student in the FireBase.
     */
    private boolean checkExistence(String studentKey) {
        boolean flag = false;

        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()){
                    for (DataSnapshot data: dS.getChildren()) {
                        stuIdList.add(data.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        int i = 0;
        while (i < stuIdList.size() && !flag){
            flag = stuIdList.get(i).equals(studentKey);
            i ++;
        }

        return flag;
    }

    /**
     * The checkAll method checks all the כields of the input
     * returns false if there is a mistake, otherwise, true.
     *
     */
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
}