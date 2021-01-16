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
        var result = findPathRecursively(width, height, startPoint, endPoint, obstacles.map(HashSet::new).orElseGet(HashSet::new));
        return Optional.ofNullable(result);
    }

    /**
     * 再帰的に経路を探索を行う.
     *
     * @param width        区域の幅
     * @param height       区域の高さ
     * @param startPoint   起点
     * @param endPoint     通過不可の地点の一覧
     * @param closedPoints 経路
     * @return true:終点に到達可  false:終点に到達不可
     */
    private static List<Point> findPathRecursively(
            int width,
            int height,
            Point startPoint,
            Point endPoint,
            Set<Point> closedPoints) {
        return null;
    }

    /**
     * 引数elementを引数listの先頭に結合したリストを返却する。
     *
     * @param element
     * @param list
     * @return 第一引数の要素を第二引数のリストの先頭に追加したリスト
     */
    private static List<Point> union(Point element, List<Point> list) {

        return Stream.of(List.of(element), list)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

}
