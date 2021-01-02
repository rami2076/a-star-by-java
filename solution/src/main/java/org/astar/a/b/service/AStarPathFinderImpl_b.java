package org.astar.a.b.service;

import org.astar.a.b.value_object.ChainNode_b;
import org.astar.a.b.value_object.Parameter;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * A star path finder.
 */
public class AStarPathFinderImpl_b {

    /**
     * 探索時のパラメータ
     */
    private Parameter parameter;

    /**
     * 計算中のノードを格納したリスト
     */
    private Set<ChainNode_b> openSet = new HashSet<>();

    /**
     * 暫定の計算済みのノードを格納したリスト
     */
    private Set<ChainNode_b> provisionalCloseSet = new HashSet<>();

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
    public Optional<List<Point>> findPath(
            int width,
            int height,
            Point startPoint,
            Point endPoint,
            Optional<Set<Point>> obstacles) {

        Set<Point> concreteObstacles = obstacles.orElseGet(Collections::emptySet);
        this.parameter = new Parameter(width, height, startPoint, endPoint, concreteObstacles);

        //1巡目
        ChainNode_b newNode = newNode(startPoint, Optional.empty());
        addOpenList(newNode);

        //2巡目以降
        List<Point> result = findPath();
        return Optional.ofNullable(result);
    }

    /**
     * OpenPointを追加する
     *
     * @param newNode
     */
    private void addOpenList(ChainNode_b newNode) {

        //新しいnodeが計残済みリストに含まれる場合,
        Optional<ChainNode_b> closeOldNode = this.provisionalCloseSet.stream()
                .filter(position -> position.equals(newNode))
                .findFirst();

        if (closeOldNode.isPresent()) {
            if (newNode.lessThanTotalCost(closeOldNode.get())) {
                this.provisionalCloseSet.remove(closeOldNode.get());
            } else {
                //計算済みの方がコストが小さい場合OpenListを更新しない。
                return;
            }
        }

        //計算中Nodeに存在するか？
        Optional<ChainNode_b> openOldNode = this.openSet.stream()
                .filter(position -> position.equals(newNode))
                .findFirst();
        if (openOldNode.isPresent()) {
            if (newNode.lessThanTotalCost(openOldNode.get())) {
                openOldNode.get().update(newNode);
            }
        } else {
            //追加
            this.openSet.add(newNode);
        }
    }


    /**
     * 最短経路を探索する
     *
     * @return 座標位置のリストを返却する
     */
    private List<Point> findPath() {


        ChainNode_b currentNode = null;
        while (isContinue()) {
            currentNode = calculateMinLengthPosition();
            this.provisionalCloseSet.add(currentNode);
            this.openSet.remove(currentNode);
            this.openSurroundingPoints(currentNode);
        }


        if (isUnreachableGoal(currentNode)) {
            return null;
        } else {
            var path = new ArrayList<Point>();
            path.add(currentNode.getPoint());
            while (currentNode.getPreviousNode().isPresent()) {
                currentNode = currentNode.getPreviousNode().get();
                path.add(currentNode.getPoint());
            }

            Collections.reverse(path);
            return path;
        }

    }


    /**
     * OpenList内の最短コストののNodeを取得
     */
    private ChainNode_b calculateMinLengthPosition() {
        var minLengthPosition = this.openSet.stream().findFirst().get();
        int minLength = minLengthPosition.totalLength();
        for (var openPoint : this.openSet) {
            if (openPoint.totalLength() < minLength) {
                minLength = openPoint.totalLength();
                minLengthPosition = openPoint;
            }
        }

        return minLengthPosition;
    }

    /**
     * 周囲のPointを展開する
     *
     * @param ChainNode_b
     */
    private void openSurroundingPoints(ChainNode_b ChainNode_b) {
        int targetPointX = ChainNode_b.getPoint().x;
        int targetPointY = ChainNode_b.getPoint().y;

        var nextPoint = new Point(targetPointX - 1, targetPointY);
        if (targetPointX > 0 && !this.parameter.obstacles().contains(nextPoint)) {
            ChainNode_b newNode = newNode(nextPoint, Optional.of(ChainNode_b));
            addOpenList(newNode);
        }

        nextPoint = new Point(targetPointX, targetPointY + 1);
        if (targetPointY < this.parameter.height() - 1 && !this.parameter.obstacles().contains(nextPoint)) {
            ChainNode_b newNode = newNode(nextPoint, Optional.of(ChainNode_b));
            addOpenList(newNode);
        }

        nextPoint = new Point(targetPointX + 1, targetPointY);
        if (targetPointX < this.parameter.width() - 1 && !this.parameter.obstacles().contains(nextPoint)) {
            ChainNode_b newNode = newNode(nextPoint, Optional.of(ChainNode_b));
            addOpenList(newNode);
        }

        nextPoint = new Point(targetPointX, targetPointY - 1);
        if (targetPointY > 0 && !this.parameter.obstacles().contains(nextPoint)) {
            ChainNode_b newNode = newNode(nextPoint, Optional.of(ChainNode_b));
            addOpenList(newNode);
        }
    }


    /**
     * 探索継続か？
     *
     * @return
     */
    private boolean isContinue() {
        return this.openSet.size() != 0
                && !this.provisionalCloseSet.stream()
                .anyMatch(node -> node.getPoint().equals(this.parameter.endPoint()));

    }

    /**
     * 目標に到達可能か?
     *
     * @param ChainNode_b
     * @return
     */
    private boolean isUnreachableGoal(ChainNode_b ChainNode_b) {
        return Objects.isNull(ChainNode_b)
                || !this.provisionalCloseSet.stream()
                .anyMatch(node -> node.getPoint().equals(this.parameter.endPoint()));
    }

    /**
     * 新規ノードの作成
     *
     * @param currentPoint
     * @param previousNode
     * @return
     */
    private ChainNode_b newNode(Point currentPoint, Optional<ChainNode_b> previousNode) {
        return ChainNode_b.newNode(currentPoint, previousNode, this.parameter.endPoint());
    }
}
