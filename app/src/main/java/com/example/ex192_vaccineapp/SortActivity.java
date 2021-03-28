package com.example.ex192_vaccineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
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

public class SortActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView showOptions, showResult;
    ArrayList<String> results;
    String[] options;
    ArrayAdapter<String> adp;
    ValueEventListener vel;
    int selectedClass = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sort);

        showOptions = findViewById(R.id.showOptions);
        showResult = findViewById(R.id.showResult);

        showOptions.setOnItemClickListener(this);
        showOptions.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        results = new ArrayList<>();
        options = new String[]{"All vaccinated students", "All allergic", "Order by grade", "Order by class"};

        adp = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, options);
        showOptions.setAdapter(adp);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String optionsDescription = options[position];
        showResult.setAdapter(null);

        switch (optionsDescription) {
            case "All vaccinated students":{
                Query q = refStudents.orderByChild("isAllergic").equalTo(false);
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        results.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                        }
                        adp = new ArrayAdapter<String>(SortActivity.this, R.layout.support_simple_spinner_dropdown_item, results);
                        showResult.setAdapter(adp);
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
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                        }
                        adp = new ArrayAdapter<String>(SortActivity.this, R.layout.support_simple_spinner_dropdown_item, results);
                        showResult.setAdapter(adp);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                };
                q.addListenerForSingleValueEvent(vel);

                break;
            }

            case "Order by grade":{
                openAlertDialog();
                if (selectedClass != 0){
                    Query q = refStudents.orderByChild("isAllergic").equalTo(false).orderByChild("grade").equalTo(selectedClass);
                    vel = new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dS) {
                            results.clear();
                            for (DataSnapshot data : dS.getChildren()){
                                Student studentTemp = data.getValue(Student.class);
                                results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                            }
                            adp = new ArrayAdapter<String>(SortActivity.this, R.layout.support_simple_spinner_dropdown_item, results);
                            showResult.setAdapter(adp);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    };
                    q.addListenerForSingleValueEvent(vel);
                }
                else{
                    Toast.makeText(this, "Enter correctly the class number", Toast.LENGTH_SHORT).show();
                }

                break;
            }

            case "Order by class":{
                Query q = refStudents.orderByChild("isAllergic").equalTo(false).orderByChild("class");
                vel = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dS) {
                        results.clear();
                        for (DataSnapshot data : dS.getChildren()){
                            Student studentTemp = data.getValue(Student.class);
                            results.add(Objects.requireNonNull(studentTemp).getName() + " " + studentTemp.getFamilyName());
                        }
                        adp = new ArrayAdapter<String>(SortActivity.this, R.layout.support_simple_spinner_dropdown_item, results);
                        showResult.setAdapter(adp);
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

    private void openAlertDialog() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        final EditText classET = new EditText(this);
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
            }
        });
    }


    @Override
    protected void onPause() {
        if (vel != null) {
            refStudents.removeEventListener(vel);
        }
        super.onPause();
    }
}