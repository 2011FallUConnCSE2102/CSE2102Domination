package graphics.maps;

import gameplay.Base;
import gameplay.PlayerRunner;
import graphics.GraphicsUtilities;
import graphics.Sprite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import networking.NetUtils;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
public class Map {

    private ArrayList<Base> bases = new ArrayList<Base>();
    private final BufferedImage fullImage;
    private final BufferedImage gridImage;
    private byte maxHeight;
    private byte maxWidth;
    private boolean showGrid = false;
    private ArrayList<SpawnPoint> spawnPoints = new ArrayList<SpawnPoint>();
    private final short[] tileHeight;
    private final short[] tileWidth;
    private final Tile[][][] tiles;

    public Map(String name) throws IOException {
        InputStream input = GraphicsUtilities.openMap(name);
        byte[] temp = new byte[2];
        input.read(temp);
        Image[] images = new Image[NetUtils.bytesToShort(temp)];
        ImageReader reader = ImageIO.getImageReadersByFormatName("png").next();
        for (short i = 0; i < images.length; i++) {
            temp = new byte[4];
            input.read(temp);
            byte[] bytes = new byte[NetUtils.bytesToInt(temp)];
            int index = 0;
            while (index < bytes.length) {
                index += input.read(bytes, index, bytes.length - index);
            }
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            reader.setInput(ImageIO.createImageInputStream(bais));
            images[i] = reader.read(0);
        }
        tiles = new Tile[input.read()][][];
        tileHeight = new short[tiles.length];
        tileWidth = new short[tiles.length];
        for (byte i = 0; i < tiles.length; i++) {
            temp = new byte[2];
            input.read(temp);
            short rows = NetUtils.bytesToShort(temp);
            temp = new byte[2];
            input.read(temp);
            short columns = NetUtils.bytesToShort(temp);
            tiles[i] = new Tile[rows][columns];
            temp = new byte[2];
            input.read(temp);
            tileHeight[i] = NetUtils.bytesToShort(temp);
            if (tileHeight[i] * tiles[i][0].length > tileHeight[maxHeight] * tiles[maxHeight][0].length) {
                maxHeight = i;
            }
            temp = new byte[2];
            input.read(temp);
            tileWidth[i] = NetUtils.bytesToShort(temp);
            if (tileWidth[i] * tiles[i].length > tileWidth[maxWidth] * tiles[maxWidth].length) {
                maxWidth = i;
            }
            for (int j = 0; j < tiles[i].length; j++) {
                for (int k = 0; k < tiles[i][j].length; k++) {
                    temp = new byte[5];
                    input.read(temp);
                    TileAttributes attributes = TileAttributes.toTileAttributes(temp);
                    Base base = null;
                    if (attributes.get(TileAttributes.BASE_ONE)) {
                        base = new Base(Base.BASE_ONE);
                    } else if (attributes.get(TileAttributes.BASE_TWO)) {
                        base = new Base(Base.BASE_TWO);
                    } else if (attributes.get(TileAttributes.BASE_THREE)) {
                        base = new Base(Base.BASE_THREE);
                    }
                    if (base != null && !bases.contains(base)) {
                        bases.add(base);
                    }
                    Point p = new Point(k * tileWidth[i], j * tileHeight[i]);
                    if (attributes.get(TileAttributes.SPAWN_ONE)) {
                        spawnPoints.add(new SpawnPoint(p, SpawnPoint.SPAWN_ONE));
                    } else if (attributes.get(TileAttributes.SPAWN_TWO)) {
                        spawnPoints.add(new SpawnPoint(p, SpawnPoint.SPAWN_TWO));
                    } else if (attributes.get(TileAttributes.SPAWN_THREE)) {
                        spawnPoints.add(new SpawnPoint(p, SpawnPoint.SPAWN_THREE));
                    } else if (attributes.get(TileAttributes.SPAWN_OTHER)) {
                        spawnPoints.add(new SpawnPoint(p, SpawnPoint.SPAWN_OTHER));
                    }
                    temp = new byte[2];
                    input.read(temp);
                    tiles[i][j][k] = new Tile(attributes, NetUtils.bytesToShort(temp));
                }
            }
        }

        fullImage = new BufferedImage(tileWidth[maxWidth] * tiles[maxWidth].length,
                tileHeight[maxHeight] * tiles[maxHeight][0].length, BufferedImage.TYPE_INT_RGB);
        gridImage = new BufferedImage(tileWidth[maxWidth] * tiles[maxWidth].length,
                tileHeight[maxHeight] * tiles[maxHeight][0].length, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = fullImage.createGraphics();
        Graphics2D gridG = gridImage.createGraphics();
        gridG.setColor(Color.RED);
        gridG.drawRect(0, 0, gridImage.getWidth(), gridImage.getHeight());
        for (int i = 0; i < tiles.length; i++) {
            Image[] tempImages = new Image[images.length];
            for (int j = 0; j < tiles[i].length; j++) {
                for (int k = 0; k < tiles[i][j].length; k++) {
                    int imageID = tiles[i][j][k].getImageID() + Short.MAX_VALUE;
                    if (imageID != -1) {
                        double rotation = Math.toRadians(tiles[i][j][k].getAttributes().getRotationAngle());
                        g.rotate(rotation, k * tileWidth[i] + tileWidth[i] / 2.0,
                                j * tileHeight[i] + tileHeight[i] / 2.0);
                        if (tempImages[imageID] == null) {
                            tempImages[imageID] = images[imageID].getScaledInstance(
                                    tileWidth[i], tileHeight[i], Image.SCALE_DEFAULT);
                        }
                        g.drawImage(tempImages[imageID],
                                k * tileWidth[i], j * tileHeight[i], null);
                        g.rotate(-rotation, k * tileWidth[i] + tileWidth[i] / 2.0,
                                j * tileHeight[i] + tileHeight[i] / 2.0);
                    }

                    Color color = tiles[i][j][k].getAttributes().getColorRepresentation();
                    color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 128);
                    gridG.setColor(color);
                    for (int l = 0; l < tiles.length - i; l++) {
                        gridG.drawRect(k * tileWidth[i] + l, j * tileHeight[i] + l, tileWidth[i], tileHeight[i]);
                    }

                }
            }
        }
        g.dispose();
        gridG.dispose();
    }

    public boolean canMoveTo(Sprite sprite, Point p) {
        Point point = new Point(p);
        point.translate(PlayerRunner.SCREEN_WIDTH / 2, PlayerRunner.SCREEN_HEIGHT / 2);
        int halfWidth = sprite.getWidth() / 2;
        int halfHeight = sprite.getHeight() / 2;
        if (point.x < halfWidth
                || point.x > tileWidth[maxWidth] * tiles[maxWidth].length - halfWidth
                || point.y < halfHeight
                || point.y > tileHeight[maxHeight] * tiles[maxHeight][0].length - halfHeight) {
            return false;
        }
        Point startPoint = new Point(point);
        startPoint.translate(-halfWidth, -halfHeight);
        Point endPoint = new Point(point);
        endPoint.translate(halfWidth, halfHeight);
        return !isInTile(TileAttributes.SOLID, startPoint, endPoint)
                && (sprite.getSolidity() == TileAttributes.SEMI_SOLID
                || !isInTile(TileAttributes.SEMI_SOLID, startPoint, endPoint));
    }

    public Base[] getBases() {
        Base[] baseArray = new Base[bases.size()];
        for (int i = 0; i < baseArray.length; i++) {
            baseArray[i] = new Base(bases.get(i).getBaseType());
        }
        return baseArray;
    }

    public Point getRandomPoint(Sprite sprite) {
        Point point = null;
        while (point == null || !canMoveTo(sprite, point)) {
            point = new Point(sprite.getWidth() / 2 + (int) (Math.random()
                    * (tileWidth[maxWidth] * tiles[maxWidth].length - sprite.getWidth())),
                    sprite.getHeight() / 2 + (int) (Math.random()
                    * (tileHeight[maxHeight] * tiles[maxHeight].length - sprite.getHeight())));
        }
        return point;
    }

    public ArrayList<SpawnPoint> getSpawnPoints() {
        ArrayList<SpawnPoint> copy = new ArrayList<SpawnPoint>();
        for (SpawnPoint point : spawnPoints) {
            copy.add(point);
        }
        return copy;
    }

    public boolean isInTile(int tileType, Point startPoint, Point endPoint) {
        for (int i = 0; i < tiles.length; i++) {
            int startCol = startPoint.x / tileWidth[i];
            int endCol = (endPoint.x - 1) / tileWidth[i];
            int startRow = startPoint.y / tileHeight[i];
            int endRow = (endPoint.y - 1) / tileHeight[i];
            for (int j = startRow; j <= endRow; j++) {
                for (int k = startCol; k <= endCol; k++) {
                    if (tiles[i][j][k].getAttributes().get(tileType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void paint(Graphics g, Point startPoint, int width, int height) {
        int pointX = startPoint.x;
        int pointY = startPoint.y;
        int x = 0;
        int y = 0;
        if (pointX < 0) {
            x = -pointX;
            pointX = 0;
        }
        if (pointY < 0) {
            y = -pointY;
            pointY = 0;
        }
        if (pointX + width > fullImage.getWidth()) {
            width = fullImage.getWidth() - pointX;
        }
        if (pointY + height > fullImage.getHeight()) {
            height = fullImage.getHeight() - pointY;
        }
        if (width > 0 && height > 0) {
            if (showGrid) {
                g.drawImage(gridImage.getSubimage(pointX, pointY, width, height), x, y, null);
            } else {
                g.drawImage(fullImage.getSubimage(pointX, pointY, width, height), x, y, null);
            }
        }
    }

    public void toggleGrid() {
        showGrid = !showGrid;
    }
}
