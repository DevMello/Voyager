package xyz.devmello.voyager.pathgen;

import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.math.geometry.Rectangle;
import xyz.devmello.voyager.pathgen.zones.Zone;

import java.util.List;

/**
 * Represents the FTC field with defined dimensions and wall zones.
 * Provides path generation and zone management functionality.
 *
 * @author Pranav Yerramaneni
 */
public class FTCField {

    public static  final double  fieldWidth = 144.0;
    public static final double fieldHeight = 144.0;
    public static final double minX = -fieldWidth / 2.0;
    public static final double maxX = fieldWidth / 2.0;
    public static final double minY = -fieldHeight / 2.0;
    public static final double maxY = fieldHeight / 2.0;

    LocalizedPathGen pathGen;

    private List<Zone> zones;

    public static final Zone leftWall = new Zone(new Rectangle(new PointXY(minX, minY), new PointXY(minX, maxY),
            new PointXY(minX + 1, maxY), new PointXY(minX + 1, minY)));

    public static final Zone rightWall = new Zone(new Rectangle(new PointXY(maxX - 1, minY), new PointXY(maxX - 1, maxY),
            new PointXY(maxX, maxY), new PointXY(maxX, minY)));

    public static final Zone topWall = new Zone(new Rectangle(new PointXY(minX, maxY - 1), new PointXY(maxX, maxY - 1),
            new PointXY(maxX, maxY), new PointXY(minX, maxY)));

    public static final Zone bottomWall = new Zone(new Rectangle(new PointXY(minX, minY), new PointXY(maxX, minY),
            new PointXY(maxX, minY + 1), new PointXY(minX, minY + 1)));


    public FTCField(List<Zone> zones, double robotX, double robotY) {
        this.zones = zones;
        zones.add(leftWall);
        zones.add(rightWall);
        zones.add(topWall);
        zones.add(bottomWall);
        this.pathGen = LocalizedPathGen.withInflatedZones(new LocalizedPathGen(zones, minX, maxX, minY, maxY), robotX, robotY);
    }

    public List<Zone> getZones() {
        return zones;
    }

    public void setZones(List<Zone> zones) {
        this.zones = zones;
    }

    public List<PointXY> getPath(PointXY start, PointXY end, double epsilon) {
        return pathGen.getPath(start, end, epsilon);
    }
}
