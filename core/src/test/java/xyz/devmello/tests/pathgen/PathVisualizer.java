package xyz.devmello.tests.pathgen;

import java.util.List;
import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.pathgen.zones.Zone;
import xyz.devmello.voyager.math.geometry.Shape;
import xyz.devmello.voyager.math.geometry.Rectangle;

public class PathVisualizer {

    private static final int TERMINAL_WIDTH = 80;
    private static final int TERMINAL_HEIGHT = 40;
    private static final char PATH_CHAR = '#';
    private static final char ZONE_CHAR = 'X';
    private static final char START_CHAR = 'S';
    private static final char END_CHAR = 'E';
    private static final char EMPTY_CHAR = ' ';

    public static void visualize(List<Zone> zones, List<PointXY> path, PointXY startPoint, PointXY endPoint, double minX, double maxX, double minY, double maxY) {
        char[][] grid = new char[TERMINAL_HEIGHT][TERMINAL_WIDTH];

        // Initialize grid with empty spaces
        for (int i = 0; i < TERMINAL_HEIGHT; i++) {
            for (int j = 0; j < TERMINAL_WIDTH; j++) {
                grid[i][j] = EMPTY_CHAR;
            }
        }

        // Draw zones
        for (Zone zone : zones) {
            Shape<?> shape = zone.getShape();
            if (shape instanceof Rectangle) {
                Rectangle rectangle = (Rectangle) shape;

                double zoneMinX = rectangle.getMinimumX();
                double zoneMaxX = rectangle.getMaximumX();
                double zoneMinY = rectangle.getMinimumY();
                double zoneMaxY = rectangle.getMaximumY();

                int startGridX = (int) mapValue(zoneMinX, minX, maxX, 0, TERMINAL_WIDTH - 1);
                int endGridX = (int) mapValue(zoneMaxX, minX, maxX, 0, TERMINAL_WIDTH - 1);
                int startGridY = (int) mapValue(zoneMinY, minY, maxY, 0, TERMINAL_HEIGHT - 1);
                int endGridY = (int) mapValue(zoneMaxY, minY, maxY, 0, TERMINAL_HEIGHT - 1);

                for (int y = Math.max(0, startGridY); y <= Math.min(TERMINAL_HEIGHT - 1, endGridY); y++) {
                    for (int x = Math.max(0, startGridX); x <= Math.min(TERMINAL_WIDTH - 1, endGridX); x++) {
                        grid[y][x] = ZONE_CHAR;
                    }
                }
            } else {
                System.err.println("Warning: Encountered a non-Rectangle shape. Only rectangular zones will be displayed.");
            }
        }

        // Draw path
        if (path != null && path.size() > 0) {
            for (PointXY point : path) {
                int gridX = (int) mapValue(point.x(), minX, maxX, 0, TERMINAL_WIDTH - 1);
                int gridY = (int) mapValue(point.y(), minY, maxY, 0, TERMINAL_HEIGHT - 1);
                if (gridX >= 0 && gridX < TERMINAL_WIDTH && gridY >= 0 && gridY < TERMINAL_HEIGHT) {
                    grid[gridY][gridX] = PATH_CHAR;
                }
            }
        }

        // Draw start and end points (overwriting any previous characters)
        if (startPoint != null) {
            int startGridX = (int) mapValue(startPoint.x(), minX, maxX, 0, TERMINAL_WIDTH - 1);
            int startGridY = (int) mapValue(startPoint.y(), minY, maxY, 0, TERMINAL_HEIGHT - 1);
            if (startGridX >= 0 && startGridX < TERMINAL_WIDTH && startGridY >= 0 && startGridY < TERMINAL_HEIGHT) {
                grid[startGridY][startGridX] = START_CHAR;
            }
        }

        if (endPoint != null) {
            int endGridX = (int) mapValue(endPoint.x(), minX, maxX, 0, TERMINAL_WIDTH - 1);
            int endGridY = (int) mapValue(endPoint.y(), minY, maxY, 0, TERMINAL_HEIGHT - 1);
            if (endGridX >= 0 && endGridX < TERMINAL_WIDTH && endGridY >= 0 && endGridY < TERMINAL_HEIGHT) {
                grid[endGridY][endGridX] = END_CHAR;
            }
        }

        // Print the grid
        System.out.println("Path Visualization:");
        System.out.println("--------------------");
        // We print from top to bottom, so we invert the y-axis for a standard Cartesian coordinate system.
        for (int i = TERMINAL_HEIGHT - 1; i >= 0; i--) {
            for (int j = 0; j < TERMINAL_WIDTH; j++) {
                System.out.print(grid[i][j]);
            }
            System.out.println();
        }
        System.out.println("--------------------");
        System.out.println("S = Start, E = End, # = Path, X = Zone");
    }

    private static double mapValue(double value, double fromLow, double fromHigh, double toLow, double toHigh) {
        return toLow + (value - fromLow) * (toHigh - toLow) / (fromHigh - fromLow);
    }
}