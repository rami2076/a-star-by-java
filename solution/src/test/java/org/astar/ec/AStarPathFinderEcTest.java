package org.astar.ec;


import org.astar.ec.service.AStarPathFinderEcImpl;
import org.astar.ec.value_object.CoordinateEc;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.impl.factory.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

/**
 * AStarPathFinderのTestクラス
 */
public class AStarPathFinderEcTest {

    /**
     * 5x5の障害物なしの区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害なし][起点終点逆転なし][終点到達可]")
    public void Size5x5_WithoutObstacles() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);

        final int expectedPathLength = 9;

        pathFinderTest(width, height, startPoint, goalPoint, null, expectedPathLength);
    }

    /**
     * 5x5の障害物なしの、起点と終点が逆転する区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害なし][起点終点逆転あり][終点到達可]")
    public void Size5x5_WithoutObstacles_Reverse() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(4, 4);
        var goalPoint = new CoordinateEc(0, 0);

        final int expectedPathLength = 9;

        pathFinderTest(width, height, startPoint, goalPoint, null, expectedPathLength);
    }

    /**
     * 5x5の障害物ありの区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害あり][起点終点逆転なし][終点到達可]")
    public void Size5x5_WithObstacles() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);
        var obstacles = Sets.immutable.of(
                new CoordinateEc(2, 3),
                new CoordinateEc(2, 4),
                new CoordinateEc(4, 3)
        );

        final int expectedPathLength = 9;

        pathFinderTest(width, height, startPoint, goalPoint, obstacles, expectedPathLength);
    }

    /**
     * 5x5の障害物ありの区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害あり][起点終点逆転なし][終点到達可]")
    public void Size5x5_WithObstacles_() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);
        var obstacles = Sets.immutable.of(
                new CoordinateEc(0, 2),
                new CoordinateEc(1, 2),
                new CoordinateEc(2, 2),
                new CoordinateEc(3, 2),
                new CoordinateEc(3, 1)
        );

        final int expectedPathLength = 9;

        pathFinderTest(width, height, startPoint, goalPoint, obstacles, expectedPathLength);
    }

    /**
     * 5x5の障害物ありの区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害あり][起点終点逆転なし][終点到達可]")
    public void Size5x5_WithObstacles__() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);
        var obstacles = Sets.immutable.of(
                new CoordinateEc(0, 1),
                new CoordinateEc(1, 1),
                new CoordinateEc(2, 1),
                new CoordinateEc(3, 1),
                new CoordinateEc(1, 3),
                new CoordinateEc(2, 3),
                new CoordinateEc(3, 3),
                new CoordinateEc(4, 3)
        );

        final int expectedPathLength = 17;

        pathFinderTest(width, height, startPoint, goalPoint, obstacles, expectedPathLength);
    }

    /**
     * 5x5の障害物ありの区域でのテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害あり][起点終点逆転なし][終点到達可]")
    public void Size5x5_WithObstacles___() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);
        var obstacles = Sets.immutable.of(
                new CoordinateEc(0, 1),
                new CoordinateEc(1, 1),
                //new Coordinate(2, 1),
                new CoordinateEc(3, 1),
                new CoordinateEc(4, 1),
                new CoordinateEc(1, 3),
                new CoordinateEc(2, 3),
                new CoordinateEc(3, 3),
                new CoordinateEc(4, 3)
        );

        final int expectedPathLength = 13;

        pathFinderTest(width, height, startPoint, goalPoint, obstacles, expectedPathLength);
    }


    /**
     * 5x5の区域で、終点に辿りつけないテスト
     */
    @Test
    @DisplayName(value = "[サイズ5x5][障害あり][起点終点逆転なし][終点到達不可]")
    public void Size5x5_NoAccess() {
        var width = 5;
        var height = 5;
        var startPoint = new CoordinateEc(0, 0);
        var goalPoint = new CoordinateEc(4, 4);
        var obstacles = Sets.immutable.of(
                new CoordinateEc(3, 3),
                new CoordinateEc(3, 4),
                new CoordinateEc(4, 3)
        );

        final int expectedPathLength = 0;

        pathFinderTest(width, height, startPoint, goalPoint, obstacles, expectedPathLength);
    }


    /**
     * 指定した入力情報で経路探索処理を実行し、その結果を検証
     *
     * @param width              区域の幅
     * @param height             区域の高さ
     * @param startPoint         起点
     * @param goalPoint          終点
     * @param obstacles          障害物の位置の一覧
     * @param expectedPathLength 経路の長さ 終点にたどり着けない場合は0
     */
    private static void pathFinderTest(
            int width,
            int height,
            CoordinateEc startPoint,
            CoordinateEc goalPoint,
            ImmutableSet<CoordinateEc> obstacles,
            int expectedPathLength) {
        CoordinateEc[] path = null;
        try {
            AStarPathFinderEcImpl finder = new AStarPathFinderEcImpl();
            path = finder.findPath(width, height, startPoint, goalPoint, Optional.ofNullable(obstacles))
                    .map(e -> e.toArray(CoordinateEc[]::new))
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }

        // 終点にたどり着けるかを確認します。
        var canReachEndPoint = expectedPathLength != 0;
        Assertions.assertEquals(canReachEndPoint, path != null);
        if (!canReachEndPoint) {
            return;
        }

        //Arrays.stream(path).map(Coordinate::toString).forEach(System.out::println);

        // 経路が最短かを確認します。
        Assertions.assertEquals(expectedPathLength, path.length);

        // 起点が入力情報と一致するかを確認します。
        var lastPosition = path[0];
        Assertions.assertEquals(startPoint, lastPosition);

        for (int index = 1; index < path.length; index++) {
            var currentPosition = path[index];
            int currentPositionX = currentPosition.x();
            int currentPositionY = currentPosition.y();
            int distanceX = Math.abs(currentPositionX - lastPosition.x());
            int distanceY = Math.abs(currentPositionY - lastPosition.y());

            if (obstacles != null) {
                // 障害物のある所を通過していないかを確認します。
                Assertions.assertFalse(obstacles.contains(currentPosition));
            }

            // 経路が連続しているかを確認します。
            Assertions.assertTrue(currentPositionX >= 0 && currentPositionX < width);
            Assertions.assertTrue(currentPositionY >= 0 && currentPositionY < height);
            Assertions.assertTrue(distanceX == 0 && distanceY == 1 || distanceX == 1 && distanceY == 0);

            lastPosition = currentPosition;
        }

        // 終点が入力情報と一致するかを確認します。
        Assertions.assertEquals(goalPoint, lastPosition);
    }

}
