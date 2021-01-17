package org.astar.vo.service;

import org.astar.vo.value_object.ChainNode;
import org.astar.vo.value_object.Coordinate;
import org.astar.vo.value_object.CoordinateSystem;
import org.astar.api.AStarPathFinder;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A star path finder.
 */
public class AStarPathFinderImpl implements AStarPathFinder {

    /**
     * 座標系
     */
    private CoordinateSystem coordinateSystem;

    /**
     * 暫定的に探索済みの最短経路で無いと判定された経路(Node)を格納したリスト
     */
    private Set<ChainNode> provisionalOpenSet = new HashSet<>();

    /**
     * 暫定的に最短経路であると評価された経路(Node)を格納したリスト
     */
    private Set<ChainNode> provisionalCloseSet = new HashSet<>();

    /**
     * 起点から終点までの最短経路を探索する
     * <p>
     * 探索した結果を通過した順のリストを返却.
     * 経路が存在しない場合nullを返却.
     * </p>
     *
     * @param width           　最大幅
     * @param height          　最大高さ
     * @param startCoordinate 　起点
     * @param goalCoordinate  　終点
     * @param obstacles       　障害物の位置の一覧
     * @return 起点から終点までの経路
     */
    @Override
    public Optional<List<Coordinate>> findPath(
            int width,
            int height,
            Coordinate startCoordinate,
            Coordinate goalCoordinate,
            Optional<Set<Coordinate>> obstacles) {

        Set<Coordinate> concreteObstacles = obstacles.orElseGet(Collections::emptySet);
        this.coordinateSystem = new CoordinateSystem(width, height, startCoordinate, goalCoordinate, concreteObstacles);

        //発散
        diverge(startCoordinate, Optional.empty());

        List<Coordinate> result = findPath();
        return Optional.ofNullable(result);
    }

    /**
     * 最短経路の探索
     *
     * @return
     */
    private List<Coordinate> findPath() {

        ChainNode shortestNode = null;
        while (isContinue()) {
            //最短経路の収束
            shortestNode = converge();
            //経路の発散
            this.divergeAroundWith(shortestNode);
        }

        if (isUnreachableGoal(shortestNode)) {
            return null;
        } else {
            var path = new ArrayList<Coordinate>();
            path.add(shortestNode.getCoordinate());
            while (shortestNode.getPreviousNode().isPresent()) {
                shortestNode = shortestNode.getPreviousNode().get();
                path.add(shortestNode.getCoordinate());
            }
            Collections.reverse(path);
            return path;
        }

    }

    /**
     * 探索を継続するか?
     *
     * @return true:継続の場合
     * false:終了の場合
     */
    private boolean isContinue() {
        return this.provisionalOpenSet.size() != 0
                && !this.provisionalCloseSet.stream()
                .anyMatch(node -> this.coordinateSystem.isGoal(node.getCoordinate()));
    }

    /**
     * 目標地点に到達しないか？
     *
     * @param chainNode
     * @return true:到達しない場合
     * false:到達する場合
     */
    private boolean isUnreachableGoal(ChainNode chainNode) {
        return Objects.isNull(chainNode)
                || !this.provisionalCloseSet.stream()
                .anyMatch(node -> this.coordinateSystem.isGoal(node.getCoordinate()));
    }

    /**
     * 1以上の経路から最短経路を評価し経路を一つに収束させる。
     *
     * @return 推定最短経路を保持するノード
     */
    private ChainNode converge() {
        //最小ノードを取得
        ChainNode shortestNode = findShortestChainNodeInOpenList();
        this.provisionalCloseSet.add(shortestNode);
        this.provisionalOpenSet.remove(shortestNode);
        return shortestNode;
    }

    /**
     * OpenList内の最短距離のNodeを取得
     *
     * @return 最短経路を返却
     */
    private ChainNode findShortestChainNodeInOpenList() {
        return this.provisionalOpenSet.stream()
                .min(ChainNode::compareTo)
                .get();
    }

    /**
     * 引数の{@code centerNode}を中心とした4方向の経路の内、妥当性のある1以上の経路を最短経路の候補として発散させる。
     *
     * @param centerNode 発散時に中央に存在する座標を持つNode
     */
    private void divergeAroundWith(ChainNode centerNode) {
        final Coordinate center = centerNode.getCoordinate();

        final var previousNode = Optional.of(centerNode);

        final var aroundPoints = Stream.of(
                Map.entry(center.left(), this.coordinateSystem.isValidLeft()),
                Map.entry(center.up(), this.coordinateSystem.isValidUp()),
                Map.entry(center.right(), this.coordinateSystem.isValidRight()),
                Map.entry(center.down(), this.coordinateSystem.isValidDown())
        );
        aroundPoints.forEach(e -> divergeIf(e.getKey(), previousNode, e.getValue()));

    }

    /**
     * 妥当性のある座標の場合、経路の発散処理を行う
     *
     * @param coordinate        座標
     * @param previousNode      一つ前のNode
     * @param isValidCoordinate 座標の妥当性を判定する関数
     */
    private void divergeIf(Coordinate coordinate, Optional<ChainNode> previousNode, Predicate<Coordinate> isValidCoordinate) {
        if (isValidCoordinate.test(coordinate)) {
            diverge(coordinate, previousNode);
        }
    }

    /**
     * 新規Node(経路)のコストが新規または、探索済みの同じ座標を持つNode(経路)より総コストが低い場合、経路をOpenSetに追加し、経路を発散させる。
     *
     * @param processCoordinate 処理中の座標
     * @param previousNode      一つ前のNode
     */
    private void diverge(Coordinate processCoordinate, Optional<ChainNode> previousNode) {
        //新しいNodeのコスト計算
        ChainNode newNode = newNode(processCoordinate, previousNode);
        //計算済みのNodeの方より新しくコスト算出したノードの方がコストが低いか判定
        if (absentsAfterRemoveIfInCloseList(newNode)) {
            //新しいNodeをOpenリストに追加
            addOrUpdateInOpenList(newNode);
        }
    }

    /**
     * 新規Nodeの生成
     *
     * @param currentCoordinates 処理中の座標
     * @param previousNode       一つ前のNode
     * @return 重みづけされた新規Node
     */
    private ChainNode newNode(Coordinate currentCoordinates, Optional<ChainNode> previousNode) {
        return ChainNode.newNode(currentCoordinates, previousNode, this.coordinateSystem);
    }

    /**
     * 暫定最短経路一覧から条件が合致する場合にNode削除処理を行った後に暫定的最短経路一覧に新規Nodeが存在しないか?
     *
     * <p>
     * 暫定最短経路一覧に新規Nodeが存在しない状態かを判定する
     * </p>
     *
     * @param newNode 新規Node(経路)
     * @return <p>
     * true:新規Nodeのheadの座標が暫定最短経路一覧内のheadの座標がすべて異なる場合
     * true:新規Nodeのheadの座標が暫定最短経路一覧内のheadの座標と同じ場合に新規Nodeの総コストが低い場合、既存のNodeを一覧から削除する。
     * false:新規Nodeのheadの座標が暫定最短経路一覧内のheadの座標と同じ場合に既存Nodeの総コストが低い場合、既存のNodeを一覧から削除しない。
     * </p>
     */
    private boolean absentsAfterRemoveIfInCloseList(ChainNode newNode) {

        //新規Nodeと同じ座標の計算済みNodeの最短経路のコストが新規Nodeより大きい場合リストから削除
        this.provisionalCloseSet.stream()
                .filter(node -> node.equalsCoordinate(newNode))
                .filter(node -> newNode.lessThanTotalCost(node))
                .findFirst()
                .ifPresent(oldNode -> this.provisionalCloseSet.remove(oldNode));
        return !this.provisionalCloseSet.contains(newNode);
    }

    /**
     * 暫定探索済みの最短経路で無いと判定された経路(Node)一覧に追加または、一覧内のNodeの更新または、何もしない。
     * <p>
     * 新規Nodeのheadの座標が暫定探索済みの最短経路で無いと判定された経路(Node)一覧に存在しない場合、追加する。
     * 新規Nodeのheadの座標が暫定探索済みの最短経路で無いと判定された経路(Node)一覧に存在し、総コストが既存より低い場合、更新する。
     * 新規Nodeのheadの座標が暫定探索済みの最短経路で無いと判定された経路(Node)一覧に存在し、総コストが既存より高い場合、何もしない。
     * </p>
     *
     * @param newNode 新規Node
     */
    private void addOrUpdateInOpenList(ChainNode newNode) {
        this.provisionalOpenSet.stream()
                .filter(node -> node.equalsCoordinate(newNode))
                .findFirst()
                .ifPresentOrElse(
                        oldNode -> {
                            if (newNode.lessThanTotalCost(oldNode)) {
                                oldNode.update(newNode);
                            }
                        },
                        () -> this.provisionalOpenSet.add(newNode)
                );
    }

}
