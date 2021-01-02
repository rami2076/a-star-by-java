package org.astar.a.c.value_object;


import java.util.function.Supplier;

/**
 * 座標
 */
public record CoordinateEc(int x, int y) {

    /**
     * デフォルト値の返却
     * <p>
     * Note
     *
     * @see CoordinateSystemEc
     * </p>
     */
    private static CoordinateEc defaultInstance = new CoordinateEc(0, 0);

    /**
     * 一つ上の座標を返却する
     *
     * @return (+ 0, + 1)の座標を返却
     */
    public CoordinateEc up() {
        return new CoordinateEc(this.x, this.y + 1);
    }

    /**
     * 一つ下の座標を返却する
     *
     * @return (+ 0, - 1)の座標を返却
     */
    public CoordinateEc down() {
        return new CoordinateEc(this.x, this.y - 1);
    }

    /**
     * 一つ右の座標を返却する
     *
     * @return (+ 1, + 0)の座標を返却
     */
    public CoordinateEc right() {
        return new CoordinateEc(this.x + 1, this.y);
    }

    /**
     * 一つ左の座標を返却する
     *
     * @return (- 1, + 0)の座標を返却
     */
    public CoordinateEc left() {
        return new CoordinateEc(this.x - 1, this.y);
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
        if (obj instanceof CoordinateEc) {
            CoordinateEc coordinateEc = (CoordinateEc) obj;
            return (x == coordinateEc.x) && (y == coordinateEc.y);
        }
        return false;
    }

    /**
     * デフォルトの座標を返却する
     * <p>default:(0,0)</p>
     */
    public static Supplier<CoordinateEc> defaultInstance() {
        return () -> defaultInstance;
    }
}
