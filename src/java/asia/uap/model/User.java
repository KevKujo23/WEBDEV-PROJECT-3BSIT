/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.model;

/**
 *
 * @author Alexander
 */
public class User {

    private int userId;
    private String studentNumber;
    private String username;
    private String email;
    private String role;
    private int deptId;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        userId = v;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String v) {
        studentNumber = v;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String v) {
        username = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        email = v;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String v) {
        role = v;
    }

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int v) {
        deptId = v;
    }
}
