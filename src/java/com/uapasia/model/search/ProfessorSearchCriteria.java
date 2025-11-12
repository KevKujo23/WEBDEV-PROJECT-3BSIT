/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.model.search;

/**
 *
 * @author Kevin
 */
import java.util.*;

public class ProfessorSearchCriteria {
    private String query;            // name keyword
    private String courseKeyword;    // course code/keyword
    private Integer deptId;
    private double minRating;
    private Set<String> tags = new LinkedHashSet<>();
    private String sort = "most";    // most|highest|recent|az

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getCourseKeyword() { return courseKeyword; }
    public void setCourseKeyword(String courseKeyword) { this.courseKeyword = courseKeyword; }

    public Integer getDeptId() { return deptId; }
    public void setDeptId(Integer deptId) { this.deptId = deptId; }

    public double getMinRating() { return minRating; }
    public void setMinRating(double minRating) { this.minRating = minRating; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = (tags == null ? new LinkedHashSet<>() : tags); }

    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }
}

