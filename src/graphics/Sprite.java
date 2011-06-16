package graphics;

import gameplay.Team;
import graphics.maps.TileAttributes;
import java.awt.Image;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Scott
 */
public class Sprite {

    private static byte currID = Byte.MIN_VALUE;
    public static final Sprite AMMO = new Sprite("ammoSprite.png", 64, 64, TileAttributes.SOLID, Team.TEAM_UNDEFINED);
    public static final Sprite BULLET = new Sprite("bullet.png", 2, 2, TileAttributes.SEMI_SOLID, Team.TEAM_UNDEFINED);
    public static final Sprite HEALTH_KIT = new Sprite("health.png", 64, 64, TileAttributes.SOLID, Team.TEAM_UNDEFINED);
    public static final Sprite PLAYER = new Sprite("sprite1.png", 64, 64, TileAttributes.SOLID, Team.TEAM_GREEN);
    public static final Sprite PLAYER_RED = new Sprite("sprite2.png", 64, 64, TileAttributes.SOLID, Team.TEAM_RED);
    public static final Sprite TANK = new Sprite("tank.png", 64, 64, TileAttributes.SOLID, Team.TEAM_GREEN);
    public static final Sprite TANK_RED = new Sprite("tankRed.png", 64, 64, TileAttributes.SOLID, Team.TEAM_RED);
    private final int height;
    private final byte id = currID++;
    private Image image;
    private final String imageName;
    private final int solidity;
    private static Sprite[] sprites = {AMMO, BULLET, HEALTH_KIT, PLAYER, PLAYER_RED, TANK, TANK_RED};
    private byte teamID;
    private final int width;

    private Sprite(String imageName, int width, int height, int solidity, byte teamID) {
        this.imageName = imageName;
        this.width = width;
        this.height = height;
        this.solidity = solidity;
        this.teamID = teamID;
    }

    public boolean equals(Sprite sprite) {
        return id == sprite.id;
    }

    public int getHeight() {
        return height;
    }

    public byte getID() {
        return id;
    }

    public Image getImage() {
        if (image == null) {
            try {
                image = ImageIO.read(Sprite.class.getResourceAsStream(
                        "resources/" + imageName)).getScaledInstance(
                        width, height, Image.SCALE_DEFAULT);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return image;
    }

    public int getSolidity() {
        return solidity;
    }

    public static Sprite getSprite(byte id) {
        return sprites[id + 128];
    }

    public static Sprite getSpriteForTeam(byte teamID) {
        if (teamID == Team.TEAM_GREEN) {
            return PLAYER;
        } else if (teamID == Team.TEAM_RED) {
            return PLAYER_RED;
        }
        return null;
    }

    public byte getTeamID() {
        return teamID;
    }

    public int getWidth() {
        return width;
    }
}
