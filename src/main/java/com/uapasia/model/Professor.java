package com.uapasia.model;

public class Professor {

    private int id;
    private String name, dept, submittedBy;

    public Professor() {

    }

    public Professor(int id, String name, String dept, String by) {
        this.id = id;
        this.name = name;
        this.dept = dept;
        this.submittedBy = by;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDept() {
        return dept;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    @Override
    public String toString() {
        return "Professor{" + "id=" + id + ", name=" + name + ", dept=" + dept;
    }
}
