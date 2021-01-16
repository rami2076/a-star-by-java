package org.astar.a.value_object;

import java.util.Set;
import java.util.function.Predicate;

/**
 * 座標系(2次元)
 *
 * <p>
 * 座標のオリジナルは(0,0)
 * </p>
 */
public record CoordinateSystem(int width, int height, Coordinate startCoordinate, Coordinate goalCoordinate,
                               Set<Coordinate> obstacles) {

    /**
     * コンストラクタ
     *
     * @param width           最大幅:0以下の場合例外
     * @param height          最大高さ:0以下の場合例外
     * @param startCoordinate 開始地点:(0,0)以下の場合例外
     * @param goalCoordinate  目標地点:(0,0)以下の場合例外
     * @param obstacles       障害地点リスト
     */
    public CoordinateSystem {
        if (height < 0) {
            throw new IllegalArgumentException("Error:[height] Expected more than 1, but Invalid parameter height:[%d].");
        }
        if (width < 0) {
            throw new IllegalArgumentException("Error:[width] Expected more than 1, but Invalid parameter width:[%d].");
        }
        if (startCoordinate.x() < 0 || startCoordinate.y() < 0) {
            throw new IllegalArgumentException("Error:[startPoint] Expected more than 1, but Invalid parameter startPoint:[(%d,%d)].");
        }
        if (goalCoordinate.x() < 0 || goalCoordinate.y() < 0) {
            throw new IllegalArgumentException("Error:[endPoint] Expected more than 1, but Invalid parameter endPoint:[(%d,%d)].");
        }
    }

    /**
     * 座標{@link Coordinate}がX軸の左方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標の{@link Coordinate#x()}が-1より大きいかつ、障害物で無い場合
     * false:判定対象の座標のxが-1以下または、障害物である場合
     */
    public Predicate<Coordinate> isValidLeft() {
        return coordinate -> coordinate.x() > -1 && isNotObstacle(coordinate);

    }

    /**
     * 座標{@link Coordinate}がX軸の右方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のxが{@link #width}より小さいかつ、障害物で無い場合
     * false:判定対象の座標のxが{@link #width}以上または、障害物である場合
     */
    public Predicate<Coordinate> isValidRight() {
        return coordinate -> coordinate.x() < width() && isNotObstacle(coordinate);
    }

    /**
     * 座標{@link Coordinate}がY軸の上方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のyが{@link #height}より小さいかつ、障害物で無い場合
     * false:判定対象の座標のyが{@link #height}以上または、障害物である場合
     */
    public Predicate<Coordinate> isValidUp() {
        return coordinate -> coordinate.y() < height() && isNotObstacle(coordinate);

    }

    /**
     * 座標{@link Coordinate}がY軸の下方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のyが{@link Coordinate#y()}より大きいかつ、障害物で無い場合
     * false:判定対象の座標のyが-1以上または、障害物である場合
     */
    public Predicate<Coordinate> isValidDown() {
        return coordinate -> coordinate.y() > -1 && isNotObstacle(coordinate);
    }

    /**
     * 引数:{@code coordinate}が障害物でないか判定する
     *
     * @param coordinate 座標
     * @return true:{@link #obstacles}に含まれない場合
     * false:{@link #obstacles}に含まる場合
     */
    private boolean isNotObstacle(Coordinate coordinate) {
        return !obstacles().contains(coordinate);
    }

    /**
     * 引数が開始地点か判定する
     *
     * @param coordinate 座標
     * @return true:開始地点の場合
     * false:開始地点でない場合
     */
    public boolean isStart(Coordinate coordinate) {
        return this.startCoordinate().equals(coordinate);
    }

    /**
     * 引数が目標地点か判定する
     *
     * @param coordinate 座標
     * @return true:目標地点の場合
     * false:目標地点でない場合
     */
    public boolean isGoal(Coordinate coordinate) {
        return this.goalCoordinate().equals(coordinate);
    }

    /**
     * 引数で指定された地点からスタート地点までの障害物を考慮しない推定最小コストを求める
     *
     * @param coordinate
     * @return
     */
    public int g(Coordinate coordinate) {
        return calculateDistance(coordinate, this.startCoordinate());
    }

    /**
     * 引数で指定された地点からゴール地点までの障害物を考慮しない推定最小コストを求める
     *
     * @param coordinate
     * @return
     */
    public int h(Coordinate coordinate) {
        return calculateDistance(coordinate, this.goalCoordinate());
    }

    /**
     * 地点1と地点2の距離を求める
     *
     * @param coordinates1
     * @param coordinates2
     * @return
     */
    private static int calculateDistance(Coordinate coordinates1, Coordinate coordinates2) {
        return Math.abs(coordinates1.x() - coordinates2.x()) + Math.abs(coordinates1.y() - coordinates2.y());
    }
}
