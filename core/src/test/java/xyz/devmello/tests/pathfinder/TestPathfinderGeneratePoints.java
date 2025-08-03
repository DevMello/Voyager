package xyz.devmello.tests.pathfinder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.math.geometry.Rectangle;
import xyz.devmello.voyager.pathgen.LocalizedPathGen;
import xyz.devmello.voyager.zones.Zone;

import java.util.ArrayList;
import java.util.List;

public class TestPathfinderGeneratePoints {
    @Test
    public void testUnobstructedPath() {
        List<Zone> zones = new ArrayList<>();

        LocalizedPathGen gen = new LocalizedPathGen(zones, 0.5, 0.5);

        PointXY start = new PointXY(0, 0);
        PointXY end = new PointXY(10, 10);

        List<PointXY> path = gen.getPath(start, end);
        System.out.println(path);
    }

    @Test
    public void testFullyObstructedPath() {
        List<Zone> zones = new ArrayList<Zone>() {

            {
                add(new Zone(new Rectangle(0, 3, 10, 4)));
            }
        };

        LocalizedPathGen gen = new LocalizedPathGen(zones, 0.5, 0.5);

        PointXY start = new PointXY(0, 0);
        PointXY end = new PointXY(10, 10);

        List<PointXY> path = gen.getPath(start, end);

        Assertions.assertNull(path);
        System.out.println(path);
    }

    @Test
    public void testPartiallyObstructedPath() {
        List<Zone> zones = new ArrayList<Zone>() {

            {
                add(new Zone(new Rectangle(1, 3, 10, 4)));
            }
        };

        LocalizedPathGen gen = new LocalizedPathGen(zones, 0.5, 0.5);

        PointXY start = new PointXY(0, 0);
        PointXY end = new PointXY(10, 10);

        List<PointXY> path = gen.getPath(start, end);

        for (PointXY point : path) {
            Assertions.assertFalse(zones.get(0).isPointInShape(point));
        }

        Assertions.assertNotNull(path);
        System.out.println(path);

    }

    @Test
    public void testUnobstructedNegativePathfinding() {
        List<Zone> zones = new ArrayList<>();
        LocalizedPathGen gen = new LocalizedPathGen(zones, 0.5, 0.5);
        PointXY start = new PointXY(-5, -5);
        PointXY end = new PointXY(5, 5);
        List<PointXY> path = gen.getPath(start, end);
        Assertions.assertNotNull(path);
        Assertions.assertEquals(2, path.size());
        System.out.println(path);
    }

    @Test
    public void testObstructedNegativePathfinding() {
        List<Zone> zones = new ArrayList<Zone>() {

            {
                add(new Zone(new Rectangle(-3, -2, 8, 3)));
            }
        };
        LocalizedPathGen gen = new LocalizedPathGen(zones, 0.5, 0.5);
        PointXY start = new PointXY(-5, -5);
        PointXY end = new PointXY(5, 5);
        List<PointXY> path = gen.getPath(start, end);
        Assertions.assertNotNull(path);
        System.out.println(path);
    }
}
