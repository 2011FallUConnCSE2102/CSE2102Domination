package gameplay;

import graphics.Sprite;
import java.awt.Point;
import networking.NetUtils;

/**
 *
 * @author Scott
 */
public class DisplayableGridObject extends GridObject {

    private static final int BYTE_COUNT = 9;
    private byte[] direction = new byte[4];
    private byte[] positionX = new byte[2];
    private byte[] positionY = new byte[2];
    private Sprite sprite;

    public DisplayableGridObject(Grid grid) {
        super(grid);
    }

    public DisplayableGridObject(Grid grid, Sprite sprite) {
        super(grid);
        setSprite(sprite);
    }

    public DisplayableGridObject(Grid gr, Sprite sprite, byte[] dir,
            byte[] posX, byte[] posY) {
        this(gr);
        this.sprite = sprite;
        direction = dir;
        positionX = posX;
        positionY = posY;
    }

    public final float getDirection() {
        return NetUtils.bytesToFloat(direction);
    }

    @Override
    int getInputSize() {
        return super.getInputSize() + BYTE_COUNT;
    }

    @Override
    int getOutputSize() {
        return super.getOutputSize() + BYTE_COUNT;
    }

    public final Point getPosition() {
        return new Point(getPositionX(), getPositionY());
    }

    public final short getPositionX() {
        return NetUtils.bytesToShort(positionX);
    }

    public final short getPositionY() {
        return NetUtils.bytesToShort(positionY);
    }

    public final Sprite getSprite() {
        return sprite;
    }

    void set(byte[] bytes, int startPosition) {
        sprite = Sprite.getSprite(bytes[startPosition++]);
        direction[0] = bytes[startPosition++];
        direction[1] = bytes[startPosition++];
        direction[2] = bytes[startPosition++];
        direction[3] = bytes[startPosition++];
        positionX[0] = bytes[startPosition++];
        positionX[1] = bytes[startPosition++];
        positionY[0] = bytes[startPosition++];
        positionY[1] = bytes[startPosition++];
    }

    public final void setDirection(float dir) {
        NetUtils.floatToBytes(dir, direction, 0);
    }

    final void setDirection(byte[] dir) {
        direction = dir;
    }

    public final void setPosition(Point p) {
        setPositionX((short) p.x);
        setPositionY((short) p.y);
    }

    final void setPositionX(short posX) {
        NetUtils.shortToBytes(posX, positionX, 0);
    }

    final void setPositionY(short posY) {
        NetUtils.shortToBytes(posY, positionY, 0);
    }

    public final void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    @Override
    void toByteArray(byte[] bytes, int offset) {
        super.toByteArray(bytes, offset);
        offset += super.getOutputSize();
        bytes[offset++] = sprite.getID();
        System.arraycopy(direction, 0, bytes, offset, direction.length);
        offset += direction.length;
        System.arraycopy(positionX, 0, bytes, offset, positionX.length);
        offset += positionX.length;
        System.arraycopy(positionY, 0, bytes, offset, positionY.length);
    }
}
