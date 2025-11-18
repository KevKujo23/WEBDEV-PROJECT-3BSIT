/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.model;

/**
 *
 * @author Alexander
 */
public class AuditLog {

    private int auditId, userId;
    private String action, entity;
    private Integer entityId;
    private String details;

    public int getAuditId() {
        return auditId;
    }

    public void setAuditId(int v) {
        auditId = v;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        userId = v;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String v) {
        action = v;
    }

    public String getEntity() {
        return entity;
    }

    public void setEntity(String v) {
        entity = v;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer v) {
        entityId = v;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String v) {
        details = v;
    }
}
