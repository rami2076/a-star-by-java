package org.astar.b.value_object;

import java.awt.*;
import java.util.Set;

/**
 *
 */
public record Parameter(int width, int height, Point startPoint, Point endPoint,
                        Set<Point> obstacles) {

    /**
     * @param width
     * @param height
     * @param startPoint
     * @param endPoint
     * @param obstacles
     */
    public Parameter {

    }
}
