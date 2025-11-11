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
import java.time.Instant;

public class AuditLog implements Serializable {
    private int logId;
    private int actorId;      // admin user id
    private String action;    // e.g., DELETE_RATING
    private String target;    // e.g., rating:123
    private String reason;
    private Instant createdAt;

    public int getLogId() { return logId; }
    public void setLogId(int logId) { this.logId = logId; }

    public int getActorId() { return actorId; }
    public void setActorId(int actorId) { this.actorId = actorId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}

