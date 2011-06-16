package gameplay;

/**
 *
 * @author Scott
 */
public abstract class GridObject {

    private static final int BYTE_COUNT = 1;
    static final byte TYPE_BASE = -128;
    static final byte TYPE_BULLET = -127;
    static final byte TYPE_HEALTH_PACK = -126;
    static final byte TYPE_PLAYER = -125;
    static final byte TYPE_TEAM = -124;
    static final byte TYPE_UNKNOWN = -123;
    private static int currID = Integer.MIN_VALUE;
    private Grid grid;
    private final int objectID = currID++;

    public GridObject() {
    }

    public GridObject(Grid gr) {
        setGrid(gr);
    }

    public final Grid getGrid() {
        return grid;
    }

    int getInputSize() {
        return BYTE_COUNT - 1;
    }

    public final int getObjectID() {
        return objectID;
    }

    int getOutputSize() {
        return BYTE_COUNT;
    }

    final byte getType() {
        if (this instanceof Base) {
            return TYPE_BASE;
        } else if (this instanceof Bullet) {
            return TYPE_BULLET;
        } else if (this instanceof HealthPack) {
            return TYPE_HEALTH_PACK;
        } else if (this instanceof Player) {
            return TYPE_PLAYER;
        } else if (this instanceof Team) {
            return TYPE_TEAM;
        } else {
            return TYPE_UNKNOWN;
        }
    }

    final void putInGrid() {
        grid.add(this);
    }

    final void removeFromGrid() {
        grid.remove(objectID);
    }

    final void set(byte[] bytes) {
        set(bytes, 0);
    }

    abstract void set(byte[] bytes, int startPosition);

    final void setGrid(Grid gr) {
        grid = gr;
    }

    final byte[] toByteArray() {
        byte[] bytes = new byte[getOutputSize()];
        toByteArray(bytes, 0);
        return bytes;
    }

    void toByteArray(byte[] bytes, int offset) {
        bytes[offset] = getType();
    }
}
