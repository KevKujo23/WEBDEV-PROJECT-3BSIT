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
import java.util.*;

public class ProfessorSubjectDAO {

    public List<Integer> getSubjectIdsByProfessor(int profId) throws SQLException {
        String sql = "SELECT subject_id FROM Professor_Subjects WHERE prof_id=? ORDER BY subject_id";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, profId);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
                return ids;
            }
        }
    }

    /**
     * Replace links: delete all then insert the provided distinct subjectIds
     * (in a single TX).
     */
    public void replaceProfessorSubjects(int profId, Collection<Integer> subjectIds) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                try (PreparedStatement del = con.prepareStatement(
                        "DELETE FROM Professor_Subjects WHERE prof_id=?")) {
                    del.setInt(1, profId);
                    del.executeUpdate();
                }
                if (subjectIds != null && !subjectIds.isEmpty()) {
                    String insSql = "INSERT INTO Professor_Subjects (prof_id, subject_id) VALUES (?,?)";
                    try (PreparedStatement ins = con.prepareStatement(insSql)) {
                        // de-duplicate set
                        for (Integer sid : new LinkedHashSet<>(subjectIds)) {
                            if (sid == null) {
                                continue;
                            }
                            ins.setInt(1, profId);
                            ins.setInt(2, sid);
                            ins.addBatch();
                        }
                        ins.executeBatch();
                    }
                }
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }
}
