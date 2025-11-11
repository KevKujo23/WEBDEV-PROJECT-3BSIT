/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.model;

/**
 *
 * @author Kevin
 */
public enum Term {
    ONE("1"), TWO("2"), SUMMER("Summer");
    private final String dbValue;
    Term(String v){ this.dbValue = v; }
    public String getDbValue(){ return dbValue; }
    public static Term fromDb(String v){
        if ("1".equals(v)) return ONE;
        if ("2".equals(v)) return TWO;
        return SUMMER; // default
    }
}
