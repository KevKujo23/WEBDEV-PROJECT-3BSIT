/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package asia.uap.dao;

/**
 *
 * @author Alexander
 */
import java.sql.*;

public class AuditLogDAO {

    public void log(int userId, String action, String entity, Integer entityId, String details) throws SQLException {
        String sql = "INSERT INTO Audit_Log (user_id, action, entity, entity_id, details) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            if (userId <= 0) {
                ps.setNull(1, java.sql.Types.INTEGER);
            } else {
                ps.setInt(1, userId);
            }
            ps.setString(2, action);
            ps.setString(3, entity);
            if (entityId == null) {
                ps.setNull(4, java.sql.Types.INTEGER);
            } else {
                ps.setInt(4, entityId);
            }
            ps.setString(5, details);
            ps.executeUpdate();
        }
    }
}
