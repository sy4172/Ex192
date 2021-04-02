package com.example.ex192_vaccineapp;

public class Student {
    private String name, familyName;
    private int classNum, gradeNum;
    private boolean isAllergic;
    private Vaccines v1, v2;


    public Student() {
    }

    public Student(String name, String familyName, int classNum, int gradeNum, boolean isAllergic, Vaccines v1, Vaccines v2) {
        this.name = name;
        this.familyName = familyName;
        this.classNum = classNum;
        this.gradeNum = gradeNum;
        this.isAllergic = isAllergic;
        this.v1 = v1;
        this.v2 = v2;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public int getClassNum() {
        return classNum;
    }

    public void setClassNum(int classNum) {
        this.classNum = classNum;
    }

    public int getGradeNum() {
        return gradeNum;
    }

    public void setGradeNum(int gradeNum) {
        this.gradeNum = gradeNum;
    }

    public boolean getIsAllergic() {
        return isAllergic;
    }

    public void setAllergic(boolean isAllergic) {
        this.isAllergic = isAllergic;
    }

    public Vaccines getV1() {
        return v1;
    }

    public void setV1(Vaccines v1) {
        this.v1 = v1;
    }

    public Vaccines getV2() {
        return v2;
    }

    public void setV2(Vaccines v2) {
        this.v2 = v2;
    }
}

