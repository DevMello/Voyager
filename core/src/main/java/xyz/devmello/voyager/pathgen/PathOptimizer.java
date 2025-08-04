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

import xyz.devmello.voyager.math.geometry.Geometry;
import xyz.devmello.voyager.math.geometry.Line;
import xyz.devmello.voyager.math.geometry.PointXY;

/**
 * Utilities used for optimizing paths.
 *
 * @author Pranav Yerramaneni
 * @since 0.1.0
 */
public class PathOptimizer {

    /**
     * Optimize a path by removing collinear points. This reduces the size
     * of the path, and, in some cases, reduces the length of the path by
     * allowing your robot to move in ways it wouldn't be able to have
     * previously.
     *
     * @param path the path to optimize.
     * @return an optimized path.
     */
    /**
     * Optimizes a path by using the Ramer-Douglas-Peucker algorithm to remove
     * redundant points that are approximately on a straight line.
     *
     * @param path The original list of points.
     * @param epsilon The maximum allowed distance a point can be from a line segment
     * before it is considered a significant corner. A smaller value
     * results in a more detailed path; a larger value results in a
     * more simplified path. A good starting value might be 1.0.
     * @return A new list of points representing the simplified path.
     */
    public static List<PointXY> optimize(List<PointXY> path, double epsilon) {
        if (path.size() <= 2) {
            return new ArrayList<>(path);
        }

        // The RDP algorithm is recursive, so we use a helper method.
        List<PointXY> simplifiedPath = new ArrayList<>();
        simplifyPath(path, 0, path.size() - 1, epsilon, simplifiedPath);

        // The recursive function does not add the last point, so we add it here.
        simplifiedPath.add(path.get(path.size() - 1));

        return simplifiedPath;
    }

    /**
     * Recursive helper method for the Ramer-Douglas-Peucker algorithm.
     *
     * @param path The original list of points.
     * @param startIndex The starting index of the current segment to simplify.
     * @param endIndex The ending index of the current segment to simplify.
     * @param epsilon The tolerance for simplification.
     * @param simplifiedPath The list to which simplified points are added.
     */
    private static void simplifyPath(List<PointXY> path, int startIndex, int endIndex, double epsilon, List<PointXY> simplifiedPath) {
        double maxDistance = 0.0;
        int index = -1;

        // Find the point with the maximum perpendicular distance from the line segment.
        PointXY startPoint = path.get(startIndex);
        PointXY endPoint = path.get(endIndex);

        for (int i = startIndex + 1; i < endIndex; i++) {
            double distance = perpendicularDistance(startPoint, endPoint, path.get(i));
            if (distance > maxDistance) {
                maxDistance = distance;
                index = i;
            }
        }

        // If the max distance is greater than our tolerance, it's a significant corner.
        if (maxDistance > epsilon) {
            // Recursively simplify the two sub-segments.
            if (index != -1) {
                simplifyPath(path, startIndex, index, epsilon, simplifiedPath);
                simplifiedPath.add(path.get(index));
                simplifyPath(path, index, endIndex, epsilon, simplifiedPath);
            }
        } else {
            // If the max distance is within tolerance, the whole segment is considered straight.
            // We just add the start point to the final path. The end point will be
            // handled by the next recursive call or the final step in the wrapper.
            simplifiedPath.add(startPoint);
        }
    }

    /**
     * Calculates the perpendicular distance from a point to a line segment.
     *
     * @param lineStart The start point of the line segment.
     * @param lineEnd The end point of the line segment.
     * @param p The point to check.
     * @return The perpendicular distance.
     */
    private static double perpendicularDistance(PointXY lineStart, PointXY lineEnd, PointXY p) {
        double dx = lineEnd.x() - lineStart.x();
        double dy = lineEnd.y() - lineStart.y();

        double lineLengthSquared = dx * dx + dy * dy;

        // If the line segment has zero length, we just return the distance to the point.
        if (lineLengthSquared == 0.0) {
            return p.distance(lineStart);
        }

        // Calculate the projection of the point onto the line.
        double t = ((p.x() - lineStart.x()) * dx + (p.y() - lineStart.y()) * dy) / lineLengthSquared;

        // Clamp t to the range [0, 1] to ensure we're calculating the distance to the line segment.
        t = Math.max(0.0, Math.min(1.0, t));

        PointXY projection = new PointXY(lineStart.x() + t * dx, lineStart.y() + t * dy);
        return p.distance(projection);
    }
}
