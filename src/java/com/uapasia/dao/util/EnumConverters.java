/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.uapasia.dao.util;

/**
 *
 * @author Kevin
 */
import com.uapasia.model.Role;
import com.uapasia.model.Term;

public final class EnumConverters {
    public static String roleToDb(Role r){ return r==null? "USER" : r.name(); }
    public static Role roleFromDb(String s){ return "ADMIN".equalsIgnoreCase(s)? Role.ADMIN : Role.USER; }

    public static String termToDb(Term t){ return t==null? "1" : t.getDbValue(); }
    public static Term termFromDb(String v){ return Term.fromDb(v); }
}

