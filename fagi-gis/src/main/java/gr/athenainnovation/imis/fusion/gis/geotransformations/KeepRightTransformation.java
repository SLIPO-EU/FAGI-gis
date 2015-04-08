package gr.athenainnovation.imis.fusion.gis.geotransformations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Keeps right (nodeB) geometry.
 * @author Thomas Maroulis
 */
public class KeepRightTransformation extends AbstractFusionTransformation {

    private static final String ID = "Keep right";
    
    @Override
    public void fuse(final Connection connection, final String nodeA, final String nodeB) throws SQLException {
        final String queryString = "SELECT ST_asText(geom) FROM dataset_b_geometries WHERE subject=?";
            
        try (final PreparedStatement statement = connection.prepareStatement(queryString)) {
            statement.setString(1, nodeB);
            final ResultSet resultSet = statement.executeQuery();
            
            while(resultSet.next()) {
                final String geometry = resultSet.getString(1);
                insertFusedGeometry(connection, nodeA, nodeB, geometry);
            }
        }
    }

    @Override
    public double score(final Connection connection, final String nodeA, final String nodeB, Double threshold) throws SQLException {
        return 1.0;
    }

    @Override
    public String getID() {
        return ID;
    }
    
    @Override
    public void fuseAll(Connection connection) throws SQLException {
        final String queryString = "INSERT INTO fused_geometries (subject_A, subject_B, geom) "
                + "SELECT links.nodea, links.nodeb, dataset_b_geometries.geom "
                + "FROM links INNER JOIN dataset_b_geometries "
                + "ON (links.nodeb = dataset_b_geometries.subject)";
        
        try (final PreparedStatement stmt = connection.prepareStatement(queryString)) {
            
            stmt.executeUpdate();
        }
    }
    
}