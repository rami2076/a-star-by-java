package org.astar.vo.value_object;


import java.util.function.Supplier;

/**
 * 座標
 */
public record Coordinate(int x, int y) {

    /**
     * デフォルト値の返却
     * <p>
     * Note
     *
     * @see CoordinateSystem
     * </p>
     */
    private static Coordinate defaultInstance = new Coordinate(0, 0);

    /**
     * 一つ上の座標を返却する
     *
     * @return (+ 0, + 1)の座標を返却
     */
    public Coordinate up() {
        return new Coordinate(this.x, this.y + 1);
    }

    /**
     * 一つ下の座標を返却する
     *
     * @return (+ 0, - 1)の座標を返却
     */
    public Coordinate down() {
        return new Coordinate(this.x, this.y - 1);
    }

    /**
     * 一つ右の座標を返却する
     *
     * @return (+ 1, + 0)の座標を返却
     */
    public Coordinate right() {
        return new Coordinate(this.x + 1, this.y);
    }

    /**
     * 一つ左の座標を返却する
     *
     * @return (- 1, + 0)の座標を返却
     */
    public Coordinate left() {
        return new Coordinate(this.x - 1, this.y);
    }

    /**
     * 同じ座標か判定する
     *
     * @param obj Object
     * @return true:(x,y)が同じ場合
     * false:(x,y)どちらかまたは、どちらも異なる場合
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            var coordinate = (Coordinate) obj;
            return (x == coordinate.x) && (y == coordinate.y);
        }
        return false;
    }

    /**
     * デフォルトの座標を返却する
     * <p>default:(0,0)</p>
     */
    public static Supplier<Coordinate> defaultInstance() {
        return () -> defaultInstance;
    }
}
