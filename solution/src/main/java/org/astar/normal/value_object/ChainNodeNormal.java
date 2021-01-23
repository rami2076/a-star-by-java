package org.astar.normal.value_object;

import lombok.Getter;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

/**
 * A Star用のPosition
 */
@Getter
public class ChainNodeNormal implements Comparable<ChainNodeNormal> {

    // g(n) はスタートノードからnまでの最小コスト
    // h(n) はn からゴールノードまでの最小コストである。
    // f(n)= g(n) + h(n)　最短経路

    /**
     * 座標
     */
    private Point point;
    /**
     * 　一つ前の要素
     */
    private Optional<ChainNodeNormal> previousNode;
    /**
     * 　スタートノードからnまでの推定最小コスト g(n)
     */
    private Integer cost;
    /**
     * nからゴールノードまでの推定最小コスト h(n)
     */
    private int distance;

    /**
     * スタートノードからゴールノードまでの最小コストf(n)
     */
    public Integer totalLength() {
        return cost + distance;
    }

    /**
     * コンストラクタ
     *
     * @param point
     * @param previousNode
     * @param cost
     * @param distance
     */
    private ChainNodeNormal(Point point, Optional<ChainNodeNormal> previousNode, Integer cost, int distance) {
        this.point = point;
        this.previousNode = previousNode;
        this.cost = Objects.isNull(cost) ? 0 : cost;
        this.distance = distance;
    }

    /**
     * 等価性の判定
     *
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {

        if (Objects.isNull(o)) {
            return false;
        }

        ChainNodeNormal other;
        if (o instanceof ChainNodeNormal) {
            other = (ChainNodeNormal) o;
        } else {
            return false;
        }

        return this.point.equals(other.point);

    }

    /**
     * 等価性の判定
     *
     * @param other
     * @return
     */
    public boolean equalsCoordinate(ChainNodeNormal other) {

        return this.point.equals(other.point);

    }

    /**
     * hash値を返却
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(point);
    }


    @Override
    public String toString() {
        return """
                %s
                g(n):%d
                h(n):%d
                f(n):%d
                """.formatted(
                point.toString(),
                cost,
                distance,
                totalLength());
    }

    /**
     * 新しいNodeを返却する
     *
     * @param point
     * @param previousNode
     * @param endPoint
     * @return
     */
    public static ChainNodeNormal newNode(Point point, Optional<ChainNodeNormal> previousNode, Point endPoint) {

        int cost = previousNode.map(ChainNodeNormal::getCost).orElse(0) + 1;//前の位置から1だけ離れているので+1している。
        int distance = calculateDistance(point, endPoint);

        //次の計算対象の位置を取得
        var currentNode = new ChainNodeNormal(
                point,
                previousNode,
                cost,
                distance
        );
        //System.out.println(currentNode);
        return currentNode;
    }

    /**
     * @param other
     * @return
     */
    public boolean lessThanTotalCost(ChainNodeNormal other) {
        return this.totalLength() < other.totalLength();
    }

    /**
     * @param other
     */
    public void update(ChainNodeNormal other) {
        this.cost = other.cost;
        this.distance = other.distance;
        this.previousNode = other.previousNode;
    }

    /**
     * @param other
     * @return
     */
    @Override
    public int compareTo(ChainNodeNormal other) {
        return this.totalLength().compareTo(other.totalLength());
    }

    /**
     * 地点1と地点2の距離を求める
     *
     * @param point1
     * @param point2
     * @return
     */
    private static int calculateDistance(Point point1, Point point2) {
        return Math.abs(point1.x - point2.x) + Math.abs(point1.y - point2.y);
    }
}
