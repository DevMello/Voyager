/*
 * Copyright (c) 2021.
 *
 * This file is part of the "Pathfinder2" project, available here:
 * <a href="https://github.com/Wobblyyyy/Pathfinder2">GitHub</a>
 *
 * This project is licensed under the GNU GPL V3 license.
 * <a href="https://www.gnu.org/licenses/gpl-3.0.en.html">GNU GPL V3</a>
 */

package xyz.devmello.voyager.pathgen;

import java.util.ArrayList;
import java.util.List;
import xyz.devmello.voyager.math.geometry.PointXY;
import xyz.devmello.voyager.pathgen.zones.Zone;

/**
 * A localized implementation of the {@link PathGen} class. This allows you
 * to use {@link PointXY} instead of {@link Coord} or {@link Node} for paths.
 * If you're trying to dynamically generate paths, this is probably the class
 * you should use - it's designed to be as simple as possible.
 *
 * <p>
 * Please note that my experience with algorithms is... just about none, and
 * this is an absolutely horribly inefficient implementation of the A*
 * pathfinding algorithm. You should not expect performant pathfinding, and
 * you should not try to stress-test the path generator - it will break,
 * I promise you that.
 * </p>
 *
 * @author Colin Robertson
 * @author Pranav Yerramaneni
 * @since 0.1.0
 */
public class LocalizedPathGen {
    private final List<Zone> zones;
    private final double xScaling;
    private final double yScaling;
    private final double gridMinX;
    private final double gridMinY;
    private final double gridMaxX;
    private final double gridMaxY;

    /**
     * Create a new LocalizedPathGen with no zones and auto-calculated grid boundaries during pathfinding.
     *
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     */
    public LocalizedPathGen(double xScaling, double yScaling) {
        this(new ArrayList<>(0), xScaling, yScaling,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Create a new LocalizedPathGen with specified zones and auto-calculated grid boundaries during pathfinding.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     */
    public LocalizedPathGen(List<Zone> zones, double xScaling, double yScaling) {
        this(zones, xScaling, yScaling,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    /**
     * Create a new LocalizedPathGen with specified zones and grid boundaries.
     *
     * @param zones the zones to avoid
     * @param gridMinX the minimum x value of the grid
     * @param gridMinY the minimum y value of the grid
     * @param gridMaxX the maximum x value of the grid
     * @param gridMaxY the maximum y value of the grid
     */
    public LocalizedPathGen(List<Zone> zones, double gridMinX, double gridMaxX, double gridMinY, double gridMaxY) {
        this(zones, 1.0, 1.0, gridMinX, gridMinY, gridMaxX, gridMaxY);
    }

    /**
     * Create a new LocalizedPathGen with specified zones and grid boundaries.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     * @param gridMinX the minimum x value of the grid
     * @param gridMinY the minimum y value of the grid
     * @param gridMaxX the maximum x value of the grid
     * @param gridMaxY the maximum y value of the grid
     */
    public LocalizedPathGen(
            List<Zone> zones,
            double xScaling,
            double yScaling,
            double gridMinX,
            double gridMinY,
            double gridMaxX,
            double gridMaxY
    ) {
        this.zones = zones;
        this.xScaling = xScaling;
        this.yScaling = yScaling;
        this.gridMinX = gridMinX;
        this.gridMinY = gridMinY;
        this.gridMaxX = gridMaxX;
        this.gridMaxY = gridMaxY;
    }

    /**
     * Create a LocalizedPathGen with inflated zones based on robot radius.
     *
     * @param generator the generator to use
     * @param robotX the robot's x dimension
     * @param robotY the robot's y dimension
     * @return a new LocalizedPathGen
     */
    public static LocalizedPathGen withInflatedZones(
            LocalizedPathGen generator,
            double robotX, double robotY) {
        return new LocalizedPathGen(
                Zone.inflate(generator.zones, Math.hypot(robotX, robotY)),
                generator.xScaling,
                generator.yScaling,
                generator.gridMinX,
                generator.gridMinY,
                generator.gridMaxX,
                generator.gridMaxY);
    }

    /**
     * Create a LocalizedPathGen with inflated zones based on robot radius.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     * @param robotRadius the robot's radius
     * @param gridMinX the minimum x value of the grid
     * @param gridMinY the minimum y value of the grid
     * @param gridMaxX the maximum x value of the grid
     * @param gridMaxY the maximum y value of the grid
     * @return a new LocalizedPathGen
     */
    public static LocalizedPathGen withInflatedZones(
            List<Zone> zones,
            double xScaling,
            double yScaling,
            double robotRadius,
            double gridMinX,
            double gridMinY,
            double gridMaxX,
            double gridMaxY
    ) {
        return new LocalizedPathGen(
                Zone.inflate(zones, robotRadius),
                xScaling,
                yScaling,
                gridMinX,
                gridMinY,
                gridMaxX,
                gridMaxY
        );
    }

    /**
     * Create a LocalizedPathGen with inflated zones based on robot radius.
     * Uses auto-calculated grid boundaries during pathfinding.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     * @param robotRadius the robot's radius
     * @return a new LocalizedPathGen
     */
    public static LocalizedPathGen withInflatedZones(
            List<Zone> zones,
            double xScaling,
            double yScaling,
            double robotRadius
    ) {
        return withInflatedZones(
                zones,
                xScaling,
                yScaling,
                robotRadius,
                Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY
        );
    }

    /**
     * Create a LocalizedPathGen with inflated zones based on robot dimensions.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     * @param robotX the robot's x dimension
     * @param robotY the robot's y dimension
     * @param gridMinX the minimum x value of the grid
     * @param gridMinY the minimum y value of the grid
     * @param gridMaxX the maximum x value of the grid
     * @param gridMaxY the maximum y value of the grid
     * @return a new LocalizedPathGen
     */
    public static LocalizedPathGen withInflatedZones(
            List<Zone> zones,
            double xScaling,
            double yScaling,
            double robotX,
            double robotY,
            double gridMinX,
            double gridMinY,
            double gridMaxX,
            double gridMaxY
    ) {
        return new LocalizedPathGen(
                Zone.inflate(zones, Math.hypot(robotX, robotY)),
                xScaling,
                yScaling,
                gridMinX,
                gridMinY,
                gridMaxX,
                gridMaxY
        );
    }

    /**
     * Create a LocalizedPathGen with inflated zones based on robot dimensions.
     * Uses auto-calculated grid boundaries during pathfinding.
     *
     * @param zones the zones to avoid
     * @param xScaling the x scaling value
     * @param yScaling the y scaling value
     * @param robotX the robot's x dimension
     * @param robotY the robot's y dimension
     * @return a new LocalizedPathGen
     */
    public static LocalizedPathGen withInflatedZones(
            List<Zone> zones,
            double xScaling,
            double yScaling,
            double robotX,
            double robotY
    ) {
        return withInflatedZones(
                zones,
                xScaling,
                yScaling,
                robotX,
                robotY,
                Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY
        );
    }

    /**
     * Get a path from point A to point B.
     *
     * @param start the start point.
     * @param end   the end point.
     * @param epsilon the epsilon value to use for optimization.
     * @return a path between the two points. If there is no valid path,
     * this will return null. If there aren't any obstacles, this will return
     * a list containing the start and end points. Otherwise, this will
     * contain several points that allow you to go from point A to point B.
     */
    public List<PointXY> getPath(PointXY start, PointXY end, double epsilon) {
        // Determine grid boundaries - use custom boundaries if defined, otherwise calculate from points
        double minX = Double.isInfinite(gridMinX) ?
                PointXY.minimumX(start, end) :
                Math.min(gridMinX, PointXY.minimumX(start, end));

        double minY = Double.isInfinite(gridMinY) ?
                PointXY.minimumY(start, end) :
                Math.min(gridMinY, PointXY.minimumY(start, end));

        double maxX = Double.isInfinite(gridMaxX) ?
                PointXY.maximumX(start, end) :
                Math.max(gridMaxX, PointXY.maximumX(start, end));

        double maxY = Double.isInfinite(gridMaxY) ?
                PointXY.maximumY(start, end) :
                Math.max(gridMaxY, PointXY.maximumY(start, end));

        LocalizedGrid grid = LocalizedGrid.generateLocalizedGrid(
                xScaling,
                yScaling,
                minX,
                minY,
                maxX,
                maxY
        );

        NodeValidator.validateNodes(grid, zones);

        PathGen gen = new PathGen(
                grid.getGrid(),
                grid.getNode(start),
                grid.getNode(end)
        );

        List<PointXY> points = grid.toPoints(gen.findCoordPath());

        if (points.isEmpty()) return null;

        if (!points.get(0).equals(start)) points.add(0, start);
        if (!points.get(points.size() - 1).equals(end)) points.add(end);
        //return points;
        return PathOptimizer.optimize(points, epsilon);
    }

    /**
     * @return the current grid boundaries [minX, minY, maxX, maxY]
     */
    public double[] getGridBoundaries() {
        return new double[]{gridMinX, gridMinY, gridMaxX, gridMaxY};
    }
}