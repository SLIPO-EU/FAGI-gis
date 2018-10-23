package gr.athena.innovation.fagi.core.function.geo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import gr.athena.innovation.fagi.core.function.IFunction;
import gr.athena.innovation.fagi.exception.ApplicationException;
import gr.athena.innovation.fagi.specification.SpecificationConstants;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.apache.jena.rdf.model.Literal;
import gr.athena.innovation.fagi.core.function.IFunctionThreeLiteralStringParameters;
import gr.athena.innovation.fagi.utils.RDFUtils;
import org.opengis.geometry.MismatchedDimensionException;

/**
 * Function class that checks if the given geometries are closer than the given distance.
 * 
 * @author nkarag
 */
public class GeometriesCloserThan implements IFunction, IFunctionThreeLiteralStringParameters {

    private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger(GeometriesCloserThan.class);

    /**
     * Checks if the minimum distance (in meters) of the geometries are closer than the provided distance value.
     * The method transforms the geometries to 3857 CRS, computes the nearest points between them 
     * and finally it calculates the orthodromic distance between the nearest points.
     *
     * @param wktA the WKT literal of A.
     * @param wktB the WKT literal of B.
     * @param distance the distance in meters.
     * @return True if the geometries are closer than the distance, false otherwise.
     */
    @Override
    public boolean evaluate(Literal wktA, Literal wktB, String distance) {

        if(wktA == null || wktB == null){
            return false;
        }

        WKTReader reader = new WKTReader();
        Geometry geometryA;
        Geometry geometryB;
        double dis = 0;

        if (!StringUtils.isBlank(distance)) {
            try {
                dis = Double.parseDouble(distance);
            } catch (NumberFormatException ex) {
                throw new ApplicationException("Tolerance provided is not a double number: " + distance);
            }
        }

        try {
            String aLexical = RDFUtils.extractGeometry(wktA).getLexicalForm();
            geometryA = reader.read(aLexical);
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktA + "\nReturning false.");
            return false;
        }

        try {
            String bLexical = RDFUtils.extractGeometry(wktB).getLexicalForm();
            geometryB = reader.read(bLexical);
        } catch (ParseException ex) {
            LOG.warn(ex);
            LOG.warn("Could not parse WKT: " + wktB + "\nReturning false.");
            return false;
        }

        try {

            CoordinateReferenceSystem dataCRS = CRS.decode(SpecificationConstants.CRS_EPSG_4326);
            CoordinateReferenceSystem worldCRS = CRS.decode(SpecificationConstants.CRS_EPSG_3857);

            boolean lenient = true; // allow for some error due to different datums
            
            //tranforming with jts found faster compared to geotools geodetic calcutaror.
            MathTransform transform = CRS.findMathTransform(dataCRS, worldCRS, lenient);
            Geometry targetGeometryA = JTS.transform(geometryA, transform);
            Geometry targetGeometryB = JTS.transform(geometryB, transform);

            Coordinate[] nearest = DistanceOp.nearestPoints(targetGeometryA, targetGeometryB);

            double minimumDistance = JTS.orthodromicDistance(nearest[0], nearest[1], worldCRS);
            
            LOG.trace("Minimum distance: " + minimumDistance);
            
            return minimumDistance <= dis;
        } catch (MismatchedDimensionException | FactoryException | TransformException  ex) {
            LOG.warn("Fail to transform geometries. Evaluating to false.", ex);
            LOG.warn(wktA + "\n" + wktB);
            return false;
        }
    }

    @Override
    public String getName() {
        String className = this.getClass().getSimpleName().toLowerCase();
        return className;
    }
}