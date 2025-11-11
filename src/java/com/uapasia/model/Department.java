/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.model;

/**
 *
 * @author Kevin
 */
import java.io.Serializable;

public class Department implements Serializable {
    private int deptId;
    private String name;

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

