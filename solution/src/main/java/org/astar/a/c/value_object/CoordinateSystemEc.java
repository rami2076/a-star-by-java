package org.astar.a.c.value_object;

import org.eclipse.collections.api.set.ImmutableSet;

import java.util.function.Predicate;

/**
 * 座標系(2次元)
 *
 * <p>
 * 座標のオリジナルは(0,0)
 * </p>
 */
public record CoordinateSystemEc(int width, int height, CoordinateEc startCoordinateEc, CoordinateEc goalCoordinateEc,
                                 ImmutableSet<CoordinateEc> obstacles) {

    /**
     * コンストラクタ
     *
     * @param width             最大幅:0以下の場合例外
     * @param height            最大高さ:0以下の場合例外
     * @param startCoordinateEc 開始地点:(0,0)以下の場合例外
     * @param goalCoordinateEc  目標地点:(0,0)以下の場合例外
     * @param obstacles         障害地点リスト
     */
    public CoordinateSystemEc {
        if (height < 0) {
            throw new IllegalArgumentException("Error:[height] Expected more than 1, but Invalid parameter height:[%d].");
        }
        if (width < 0) {
            throw new IllegalArgumentException("Error:[width] Expected more than 1, but Invalid parameter width:[%d].");
        }
        if (startCoordinateEc.x() < 0 || startCoordinateEc.y() < 0) {
            throw new IllegalArgumentException("Error:[startPoint] Expected more than 1, but Invalid parameter startPoint:[(%d,%d)].");
        }
        if (goalCoordinateEc.x() < 0 || goalCoordinateEc.y() < 0) {
            throw new IllegalArgumentException("Error:[endPoint] Expected more than 1, but Invalid parameter endPoint:[(%d,%d)].");
        }
    }

    /**
     * 座標{@link CoordinateEc}がX軸の左方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標の{@link CoordinateEc#x()}が-1より大きいかつ、障害物で無い場合
     * false:判定対象の座標のxが-1以下または、障害物である場合
     */
    public Predicate<CoordinateEc> isValidLeft() {
        return coordinateEc -> coordinateEc.x() > -1 && isNotObstacle(coordinateEc);

    }

    /**
     * 座標{@link CoordinateEc}がX軸の右方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のxが{@link #width}より小さいかつ、障害物で無い場合
     * false:判定対象の座標のxが{@link #width}以上または、障害物である場合
     */
    public Predicate<CoordinateEc> isValidRight() {
        return coordinateEc -> coordinateEc.x() < width() && isNotObstacle(coordinateEc);
    }

    /**
     * 座標{@link CoordinateEc}がY軸の上方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のyが{@link #height}より小さいかつ、障害物で無い場合
     * false:判定対象の座標のyが{@link #height}以上または、障害物である場合
     */
    public Predicate<CoordinateEc> isValidUp() {
        return coordinateEc -> coordinateEc.y() < height() && isNotObstacle(coordinateEc);

    }

    /**
     * 座標{@link CoordinateEc}がY軸の下方向の最大区域内かつ、障害の無い座標であるか判定する関数を返却する。
     *
     * @return true:判定対象の座標のyが{@link CoordinateEc#y()}より大きいかつ、障害物で無い場合
     * false:判定対象の座標のyが-1以上または、障害物である場合
     */
    public Predicate<CoordinateEc> isValidDown() {
        return coordinateEc -> coordinateEc.y() > -1 && isNotObstacle(coordinateEc);
    }

    /**
     * 引数:{@code coordinate}が障害物でないか判定する
     *
     * @param coordinateEc 座標
     * @return true:{@link #obstacles}に含まれない場合
     * false:{@link #obstacles}に含まる場合
     */
    private boolean isNotObstacle(CoordinateEc coordinateEc) {
        return !obstacles().contains(coordinateEc);
    }

    /**
     * 引数が開始地点か判定する
     *
     * @param coordinateEc 座標
     * @return true:開始地点の場合
     * false:開始地点でない場合
     */
    public boolean isStart(CoordinateEc coordinateEc) {
        return this.startCoordinateEc().equals(coordinateEc);
    }

    /**
     * 引数が目標地点か判定する
     *
     * @param coordinateEc 座標
     * @return true:目標地点の場合
     * false:目標地点でない場合
     */
    public boolean isGoal(CoordinateEc coordinateEc) {
        return this.goalCoordinateEc().equals(coordinateEc);
    }

    /**
     * 引数で指定された地点からスタート地点までの障害物を考慮しない推定最小コストを求める
     *
     * @param coordinateEc
     * @return
     */
    public int g(CoordinateEc coordinateEc) {
        return calculateDistance(coordinateEc, this.startCoordinateEc());
    }

    /**
     * 引数で指定された地点からゴール地点までの障害物を考慮しない推定最小コストを求める
     *
     * @param coordinateEc
     * @return
     */
    public int h(CoordinateEc coordinateEc) {
        return calculateDistance(coordinateEc, this.goalCoordinateEc());
    }

    /**
     * 地点1と地点2の距離を求める
     *
     * @param coordinates1
     * @param coordinates2
     * @return
     */
    private static int calculateDistance(CoordinateEc coordinates1, CoordinateEc coordinates2) {
        return Math.abs(coordinates1.x() - coordinates2.x()) + Math.abs(coordinates1.y() - coordinates2.y());
    }
}
