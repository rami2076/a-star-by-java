package org.astar.vo;


import java.awt.Point;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A star path finder.
 */
public class AStarPathFinder {

    /**
     * 起点から終点までの最短経路を再帰的に探索する
     * <p>
     * 探索した結果を通過した順のリストを返却.
     * 経路が存在しない場合nullを返却.
     * </p>
     *
     * @param width      　区域の幅
     * @param height     　区域の高さ
     * @param startPoint 　起点
     * @param endPoint   　終点
     * @param obstacles  　障害物の位置の一覧
     * @return 起点から終点までの経路
     */
    public static Optional<List<Point>> findPath(
            int width,
            int height,
            Point startPoint,
            Point endPoint,
            Optional<Set<Point>> obstacles) {
        return null;
    }
}
