package com.uapasia.model;

import java.io.Serializable;

public class Subject implements Serializable {
    private int subjectId;
    private String code;   // unique
    private String title;

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
