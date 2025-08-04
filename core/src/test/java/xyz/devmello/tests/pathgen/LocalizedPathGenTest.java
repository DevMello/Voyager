package xyz.devmello.tests.pathgen;
import org.junit.jupiter.api.Test;
import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.math.geometry.Rectangle;
import xyz.devmello.voyager.pathgen.FTCField;
import xyz.devmello.voyager.pathgen.LocalizedPathGen;
import xyz.devmello.voyager.pathgen.zones.Zone;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LocalizedPathGenTest {
    @Test
    void testUnobstructedPath() {
        // FTC field dimensions in inches
        double fieldWidth = 144.0;
        double fieldHeight = 144.0;

        // The center of the field is (0,0)
        double minX = -fieldWidth / 2.0;
        double maxX = fieldWidth / 2.0;
        double minY = -fieldHeight / 2.0;
        double maxY = fieldHeight / 2.0;

        List<Zone> zones = new ArrayList<>();

        LocalizedPathGen pathGen = new LocalizedPathGen(zones,minX, maxX, minY, maxY);

        PointXY startPoint = new PointXY(-60, -60);
        PointXY endPoint = new PointXY(60, 60);

        List<PointXY> path = pathGen.getPath(startPoint, endPoint,3 );

        assertNotNull(path, "Path should not be null");
        assertTrue(path.size() >= 2, "Path should have at least two points (start and end)");
        // Validate the points, should return the start and the end
        assertEquals(startPoint, path.get(0), "First point should be the start point");
        assertEquals(endPoint, path.get(path.size() - 1), "Last point should be the end point");

        for (PointXY point : path) {
            assertTrue(point.x() >= minX && point.x() <= maxX, "Point X is out of bounds");
            assertTrue(point.y() >= minY && point.y() <= maxY, "Point Y is out of bounds");
        }
    }

    @Test
    void testFullyObstructedPath() {
        double fieldWidth = 144.0;
        double fieldHeight = 144.0;
        double minX = -fieldWidth / 2.0;
        double maxX = fieldWidth / 2.0;
        double minY = -fieldHeight / 2.0;
        double maxY = fieldHeight / 2.0;

        List<Zone> zones = new ArrayList<>();
        // Create a wall that blocks the path
        zones.add(new Zone(new Rectangle(new PointXY(-10, -72), new PointXY(10, 72))));

        LocalizedPathGen pathGen = new LocalizedPathGen(zones, minX, maxX, minY, maxY);

        PointXY startPoint = new PointXY(-60, 0);
        PointXY endPoint = new PointXY(60, 0);

        List<PointXY> path = pathGen.getPath(startPoint, endPoint, 3);

        // A fully obstructed path should probably return an empty list or just the start point
        assertNull(path, "Path should be null or empty when fully obstructed");
    }

    @Test
    void testPartiallyObstructedPath() {
        double fieldWidth = 144.0;
        double fieldHeight = 144.0;
        double minX = -fieldWidth / 2.0;
        double maxX = fieldWidth / 2.0;
        double minY = -fieldHeight / 2.0;
        double maxY = fieldHeight / 2.0;

        List<Zone> zones = new ArrayList<>();
        // Create an obstacle in the middle
        zones.add(new Zone(new Rectangle(new PointXY(-10, -10), new PointXY(10, 10))));

        LocalizedPathGen pathGen = new LocalizedPathGen(zones, minX, maxX, minY, maxY);

        PointXY startPoint = new PointXY(-60, 0);
        PointXY endPoint = new PointXY(60, 0);

        List<PointXY> path = pathGen.getPath(startPoint, endPoint, 3);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.size() > 2, "Path should have more than two points to navigate around the obstacle");
        assertEquals(startPoint, path.get(0), "First point should be the start point");
        assertEquals(endPoint, path.get(path.size() - 1), "Last point should be the end point");

        // Check that no point on the path is inside the zone
        Zone obstacle = zones.get(0);
        for (PointXY point : path) {
            assertFalse(obstacle.isPointInShape(point), "Path should not go through the obstacle");
        }
    }

    @Test
    void testUnobstructedNegativePath() {
        double fieldWidth = 144.0;
        double fieldHeight = 144.0;
        double minX = -fieldWidth / 2.0;
        double maxX = fieldWidth / 2.0;
        double minY = -fieldHeight / 2.0;
        double maxY = fieldHeight / 2.0;

        List<Zone> zones = new ArrayList<>();
        LocalizedPathGen pathGen = new LocalizedPathGen(zones, minX, maxX, minY, maxY);

        PointXY startPoint = new PointXY(60, 60);
        PointXY endPoint = new PointXY(-60, -60);

        List<PointXY> path = pathGen.getPath(startPoint, endPoint, 3);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.size() >= 2, "Path should have at least two points");
        assertEquals(startPoint, path.get(0), "First point should be the start point");
        assertEquals(endPoint, path.get(path.size() - 1), "Last point should be the end point");
    }

    @Test
    void testObstructedNegativePath() {
        double fieldWidth = 144.0;
        double fieldHeight = 144.0;
        double minX = -fieldWidth / 2.0;
        double maxX = fieldWidth / 2.0;
        double minY = -fieldHeight / 2.0;
        double maxY = fieldHeight / 2.0;

        List<Zone> zones = new ArrayList<>();
        // Create an obstacle in the middle
        zones.add(new Zone(new Rectangle(new PointXY(-15.3, 24.5), new PointXY(14.8, -23.8))));
        zones.add(new Zone(new Rectangle(new PointXY(-25.2, -24), new PointXY(24.9, -22))));
        zones.add(new Zone(new Rectangle(new PointXY(-25.4, 25.0), new PointXY(24.7, 23))));
//        zones.add(FTCField.leftWall);
//        zones.add(FTCField.rightWall);
//        zones.add(FTCField.topWall);
//        zones.add(FTCField.bottomWall);
//
//        LocalizedPathGen pathGen = new LocalizedPathGen(zones, minX, maxX, minY, maxY);
//
//        pathGen = LocalizedPathGen.withInflatedZones(pathGen,10, 14);

        FTCField field = new FTCField(zones, 10, 14);

        PointXY startPoint = new PointXY(56.0, 0.4);
        PointXY endPoint = new PointXY(-52.5, 0.4);

        List<PointXY> path = field.getPath(startPoint, endPoint, 3);

        assertNotNull(path, "Path should not be null");
        assertTrue(path.size() > 2, "Path should have more than two points to navigate around the obstacle");
        assertEquals(startPoint, path.get(0), "First point should be the start point");
        assertEquals(endPoint, path.get(path.size() - 1), "Last point should be the end point");

        PathVisualizer.visualize(zones, path, startPoint,endPoint,minX,maxX,minY,maxY);

        // Check that no point on the path is inside the zone
        Zone obstacle = zones.get(0);
        for (PointXY point : path) {
            System.out.println("Checking point: " + point);
            assertFalse(obstacle.isPointInShape(point), "Path should not go through the obstacle");
        }
    }
}