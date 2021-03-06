package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.ex192_vaccineapp.FBref.refStudents;

/**
 *  * @author		Shahar Yani
 *  * @version  	1.0
 *  * @since		26/03/2021
 *
 *  * This UpdateActivity.class displays the whole students and a ContextMenu object to do some options
 *  and a menu move to the whole activities
 *  */
public class UpdateActivity extends AppCompatActivity implements View.OnCreateContextMenuListener {

    ListView lv;
    ArrayList<String> studentNames, statusList, studentTitle, details;
    CustomAdapter customadp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        lv = findViewById(R.id.lv);

        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setOnCreateContextMenuListener(this);

        statusList = new ArrayList<String>();
        studentNames = new ArrayList<String>();
        // put the student names to studentNames
        refStudents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dS) {
                if (dS.exists()){
                    ArrayList<String> studentNames1 = new ArrayList<>();
                    ArrayList<String> statusList1 = new ArrayList<>();
                    ArrayList<String> details1 = new ArrayList<>();
                    ArrayList<String> studentTitle1 = new ArrayList<>();
                    for (DataSnapshot data: dS.getChildren()) {
                        Student temp = data.getValue(Student.class);
                        studentTitle1.add(data.getKey());
                        studentNames1.add(Objects.requireNonNull(temp).getName() + " " + Objects.requireNonNull(temp).getFamilyName());
                        details1.add("Class: "+Objects.requireNonNull(temp).getClassNum()+" Grade: "+ Objects.requireNonNull(temp).getGradeNum());
                        if (temp.getIsAllergic()){
                            statusList1.add("Allergic student");
                        }
                        else if (temp.getV2() != null){
                            statusList1.add("All the vaccines were documented");
                        }
                        else{
                            statusList1.add("First vaccination was documented");
                        }
                    }
                    customadp = new CustomAdapter(getApplicationContext(), studentNames1, details1, statusList1);
                    lv.setAdapter(customadp);
                    details = details1;
                    statusList = statusList1;
                    studentNames = studentNames1;
                    studentTitle = studentTitle1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        v.setOnCreateContextMenuListener(this);
        menu.setHeaderTitle("ACTIONS");
        menu.add("Remove Student");
        menu.add("Update Student");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo adpInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int pos = adpInfo.position;
        String action = item.getTitle().toString();
        if (action.equals("Update Student")){
            Intent si = new Intent(this, InputActivity.class);
            // Saving the right health status of the student
            if (statusList.get(pos).equals("Allergic student")){
                si.putExtra("AllergicStatus", true);
            }
            else{
                si.putExtra("AllergicStatus", false);
            }
            si.putExtra("updateMode",true); // In order to to update the details in InputActivity.class
            si.putExtra("StudentTitle", studentTitle.get(pos)); // Student name to display in the fields
            startActivity(si);
        }
        else if (action.equals("Remove Student")){
            studentNames.remove(pos);
            statusList.remove(pos);
            customadp.notifyDataSetChanged();
            lv.setAdapter(customadp);
            refStudents.child(studentTitle.get(pos)).removeValue(); // Removing from the FB
        }

        return super.onContextItemSelected(item);
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
        else if (id == R.id.sort){
            si = new Intent(this, SortActivity.class);
            startActivity(si);
        }
        else if (id == R.id.Credits){
            si = new Intent(this, CreditsActivity.class);
            startActivity(si);
        }

        return super.onOptionsItemSelected(item);
    }
}