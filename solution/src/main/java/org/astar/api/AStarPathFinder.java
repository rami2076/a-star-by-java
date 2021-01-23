package org.astar.api;

import org.astar.vo.value_object.Coordinate;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Path FinderのIF
 */
public interface AStarPathFinder {

    /**
     * 最短距離を探索し結果をリストで返却する
     *
     * @param width
     * @param height
     * @param startPoint
     * @param endPoint
     * @param obstacles
     * @return
     */
    Optional<List<Coordinate>> findPath(
            int width,
            int height,
            Coordinate startPoint,
            Coordinate endPoint,
            Optional<Set<Coordinate>> obstacles);
}
