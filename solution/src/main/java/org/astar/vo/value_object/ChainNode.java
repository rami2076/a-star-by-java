package org.astar.vo.value_object;

import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

/**
 * 重み付きの一次元の連結ノード
 *
 * <p>
 * AStarアルゴリズム内で用いる関数を以下に記載
 * g(n) はスタートノードからnまでの最小コスト
 * h(n) はn からゴールノードまでの最小コストである。
 * f(n)= g(n) + h(n)　最短経路
 * </p>
 */
@Getter
public class ChainNode {


    /**
     * 座標
     */
    private Coordinate coordinate;
    /**
     * 一つ前のNode
     */
    private Optional<ChainNode> previousNode;
    /**
     * スタートノードからnまでの推定最小コスト g(n)の結果
     */
    private Integer cost;
    /**
     * nからゴールノードまでの推定最小コスト h(n)の結果
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
     * @param coordinate   座標
     * @param previousNode 前のNode
     * @param cost         スタート地点までのコスト
     * @param distance     ゴール地点までのコスト
     */
    private ChainNode(Coordinate coordinate, Optional<ChainNode> previousNode, Integer cost, int distance) {
        this.coordinate = coordinate;
        this.previousNode = previousNode;
        this.cost = Objects.isNull(cost) ? 0 : cost;
        this.distance = distance;
    }

    /**
     * 等価性の判定
     *
     * @param o Object
     * @return true:{link #coordinate}が同じ場合
     * false:{link #coordinate}が異なる場合
     */
    @Override
    public boolean equals(Object o) {
        if (Objects.isNull(o)) {
            return false;
        }

        ChainNode other;
        if (o instanceof ChainNode) {
            other = (ChainNode) o;
        } else {
            return false;
        }

        return this.coordinate.equals(other.coordinate);
    }

    /**
     * 座標が同じであるか判定する
     *
     * @param other
     * @return true:{link #coordinate}が同じ場合
     * false:{link #coordinate}が異なる場合
     */
    public boolean equalsCoordinate(ChainNode other) {
        return this.coordinate.equals(other.coordinate);
    }

    /**
     * hash値を返却
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(coordinate);
    }


    /**
     * パラメータの文字列を返却
     * <p>
     * 座標 %s
     * スタート地点までの推定最小コスト g(n):%d
     * ゴール地点までの推定最小コスト　h(n):%d
     * 総コスト(g(n)+h(n))　f(n):%d
     * </p>
     *
     * @return パラメータを文字列にした値を返却
     * <p>
     * 例{@code
     * Coordinate[x=0,y=0]
     * g(n):0
     * h(n):0
     * f(n):0
     * }
     * </p>
     */
    @Override
    public String toString() {
        return """
                %s
                g(n):%d
                h(n):%d
                f(n):%d
                """.formatted(
                coordinate.toString(),
                cost,
                distance,
                totalLength());
    }

    /**
     * 新しい重み付きのNodeを返却する
     *
     * <p>
     * {@code previousNode}がnullの場合、座標位置(0,0)として重みの計算を行う。
     * </p>
     *
     * @param coordinate       座標
     * @param previousNode     一つ前のNode
     * @param coordinateSystem 座標系
     * @return 新しい重み付きのNode
     */
    public static ChainNode newNode(Coordinate coordinate, Optional<ChainNode> previousNode, CoordinateSystem coordinateSystem) {
        int cost = g(previousNode);
        int distance = coordinateSystem.h(coordinate);

        //次の計算対象の位置を取得
        var currentNode = new ChainNode(
                coordinate,
                previousNode,
                cost,
                distance
        );

        return currentNode;
    }

    /**
     * 引数のNodeより総コストが小さいか？
     *
     * @param other ChainNode
     * @return true:引数のNodeよりトータルコストが小さい場合
     * false:引数のNode以上の総コストの場合
     */
    public boolean lessThanTotalCost(ChainNode other) {
        return this.totalLength() < other.totalLength();
    }

    /**
     * Nodeのパラメータを更新する。
     *
     * @param other ChainNode
     */
    public void update(ChainNode other) {
        this.cost = other.cost;
        this.distance = other.distance;
        this.previousNode = other.previousNode;
    }

    /**
     * Nodeの重みを比較する。
     *
     * @param other ChainNode
     * @return <p>
     * -1:{@code this.totalLength() < other.totalLength()}
     * 0:{@code this.totalLength() == other.totalLength()}
     * 1:{@code this.totalLength() > other.totalLength()}
     * </p>
     */
    public int compareTo(ChainNode other) {
        return this.totalLength().compareTo(other.totalLength());
    }

    /**
     * スタート地点までのコスト
     *
     * @param previousNode 一つ前のNode
     * @return
     */
    private static int g(Optional<ChainNode> previousNode) {
        return previousNode.map(ChainNode::getCost).orElse(0) + 1;
    }

}
