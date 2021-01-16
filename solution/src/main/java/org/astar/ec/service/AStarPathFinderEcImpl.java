package org.astar.ec.service;

import org.astar.ec.value_object.ChainNodeEc;
import org.astar.ec.value_object.CoordinateEc;
import org.astar.ec.value_object.CoordinateSystemEc;
import org.eclipse.collections.api.factory.Sets;
import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.MutableSet;
import org.eclipse.collections.impl.list.mutable.FastList;
import org.eclipse.collections.impl.tuple.Tuples;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A star path finder.
 */
public class AStarPathFinderEcImpl {

    /**
     * 座標系
     */
    private CoordinateSystemEc coordinateSystemEc;

    /**
     * 暫定的に探索済みの最短経路で無いと判定された経路(Node)を格納したリスト
     */
    private MutableSet<ChainNodeEc> provisionalOpenSet = Sets.mutable.of();

    /**
     * 暫定的に最短経路であると評価された経路(Node)を格納したリスト
     */
    private MutableSet<ChainNodeEc> provisionalCloseSet = Sets.mutable.of();

    /**
     * 起点から終点までの最短経路を探索する
     * <p>
     * 探索した結果を通過した順のリストを返却.
     * 経路が存在しない場合nullを返却.
     * </p>
     *
     * @param width             　最大幅
     * @param height            　最大高さ
     * @param startCoordinateEc 　起点
     * @param goalCoordinateEc  　終点
     * @param obstacles         　障害物の位置の一覧
     * @return 起点から終点までの経路
     */
    public Optional<FastList<CoordinateEc>> findPath(
            int width,
            int height,
            CoordinateEc startCoordinateEc,
            CoordinateEc goalCoordinateEc,
            Optional<ImmutableSet<CoordinateEc>> obstacles) {

        ImmutableSet<CoordinateEc> concreteObstacles = obstacles.orElseGet(Sets.immutable::empty);
        this.coordinateSystemEc = new CoordinateSystemEc(width, height, startCoordinateEc, goalCoordinateEc, concreteObstacles);

        //発散
        diverge(startCoordinateEc, Optional.empty());

        FastList<CoordinateEc> result = findPath();
        return Optional.ofNullable(result);
    }

    /**
     * 最短経路の探索
     *
     * @return
     */
    private FastList<CoordinateEc> findPath() {

        ChainNodeEc shortestNode = null;
        while (isContinue()) {
            //最短経路の収束
            shortestNode = converge();
            //経路の発散
            this.divergeAroundWith(shortestNode);
        }

        if (isUnreachableGoal(shortestNode)) {
            return null;
        } else {
            var path = new FastList<CoordinateEc>();
            path.add(shortestNode.getCoordinateEc());
            while (shortestNode.getPreviousNode().isPresent()) {
                shortestNode = shortestNode.getPreviousNode().get();
                path.add(shortestNode.getCoordinateEc());
            }

            return path.reverseThis();
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
                && !this.provisionalCloseSet
                .anySatisfy(node -> this.coordinateSystemEc.isGoal(node.getCoordinateEc()));
    }

    /**
     * 目標地点に到達しないか？
     *
     * @param chainNodeEc
     * @return true:到達しない場合
     * false:到達する場合
     */
    private boolean isUnreachableGoal(ChainNodeEc chainNodeEc) {
        return Objects.isNull(chainNodeEc)
                || !this.provisionalCloseSet
                .anySatisfy(node -> this.coordinateSystemEc.isGoal(node.getCoordinateEc()));
    }

    /**
     * 1以上の経路から最短経路を評価し経路を一つに収束させる。
     *
     * @return 推定最短経路を保持するノード
     */
    private ChainNodeEc converge() {
        //最小ノードを取得
        ChainNodeEc shortestNode = findShortestChainNodeInOpenList();
        this.provisionalCloseSet.add(shortestNode);
        this.provisionalOpenSet.remove(shortestNode);
        return shortestNode;
    }

    /**
     * OpenList内の最短距離のNodeを取得
     *
     * @return 最短経路を返却
     */
    private ChainNodeEc findShortestChainNodeInOpenList() {
        return this.provisionalOpenSet
                .min(ChainNodeEc::compareTo);
    }

    /**
     * 引数の{@code centerNode}を中心とした4方向の経路の内、妥当性のある1以上の経路を最短経路の候補として発散させる。
     *
     * @param centerNode 発散時に中央に存在する座標を持つNode
     */
    private void divergeAroundWith(ChainNodeEc centerNode) {
        final CoordinateEc center = centerNode.getCoordinateEc();

        final var previousNode = Optional.of(centerNode);

        final var aroundPoints = FastList.newListWith(
                Tuples.pair(center.left(), this.coordinateSystemEc.isValidLeft()),
                Tuples.pair(center.up(), this.coordinateSystemEc.isValidUp()),
                Tuples.pair(center.right(), this.coordinateSystemEc.isValidRight()),
                Tuples.pair(center.down(), this.coordinateSystemEc.isValidDown())
        );
        aroundPoints.forEach(e -> divergeIf(e.getOne(), previousNode, e.getTwo()));

    }

    /**
     * 妥当性のある座標の場合、経路の発散処理を行う
     *
     * @param coordinateEc      座標
     * @param previousNode      一つ前のNode
     * @param isValidCoordinate 座標の妥当性を判定する関数
     */
    private void divergeIf(CoordinateEc coordinateEc, Optional<ChainNodeEc> previousNode, Predicate<CoordinateEc> isValidCoordinate) {
        if (isValidCoordinate.test(coordinateEc)) {
            diverge(coordinateEc, previousNode);
        }
    }

    /**
     * 新規Node(経路)のコストが新規または、探索済みの同じ座標を持つNode(経路)より総コストが低い場合、経路をOpenSetに追加し、経路を発散させる。
     *
     * @param processCoordinateEc 処理中の座標
     * @param previousNode        一つ前のNode
     */
    private void diverge(CoordinateEc processCoordinateEc, Optional<ChainNodeEc> previousNode) {
        //新しいNodeのコスト計算
        ChainNodeEc newNode = newNode(processCoordinateEc, previousNode);
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
    private ChainNodeEc newNode(CoordinateEc currentCoordinates, Optional<ChainNodeEc> previousNode) {
        return ChainNodeEc.newNode(currentCoordinates, previousNode, this.coordinateSystemEc);
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
    private boolean absentsAfterRemoveIfInCloseList(ChainNodeEc newNode) {

        //新規Nodeと同じ座標の計算済みNodeの最短経路のコストが新規Nodeより大きい場合リストから削除
        this.provisionalCloseSet
                .select(node -> node.equalsCoordinate(newNode))
                .removeIf(node -> newNode.lessThanTotalCost(node));

        //計算済みNode内の方が最短経路のコストが小さい場合
        if (this.provisionalCloseSet.contains(newNode)) {
            return false;
        } else {
            return true;
        }
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
    private void addOrUpdateInOpenList(ChainNodeEc newNode) {
        this.provisionalOpenSet
                .detectOptional(node -> node.equalsCoordinate(newNode))
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
