package com.uapasia.model;

import java.io.Serializable;
import java.time.Instant;

public class Professor implements Serializable {
    private int profId;
    private String firstName;
    private String lastName;
    private int deptId;           // FK -> departments
    private Instant createdAt;

    public int getProfId() { return profId; }
    public void setProfId(int profId) { this.profId = profId; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public String getFullName(){ return (firstName==null?"":firstName)+" "+(lastName==null?"":lastName); }
}
