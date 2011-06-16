package graphics;

import gameplay.Base;
import gameplay.DisplayableGridObject;
import gameplay.Player;
import gameplay.PlayerRunner;
import gameplay.Team;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import sound.SoundSystem;

/**
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 * @author Joe Stein
 */
class PlayerCanvas extends Canvas {

    public static final int CLIP_SIZE = 20;
    private BufferStrategy bufferStrategy;
    private int clipCount;
    private boolean countdownActive = false;
    private final InputHandler inputHandler;
    private boolean pauseScreen;
    private final byte playerID;
    private final PlayerRunner playerRunner;
    private final PlayerScreen playerScreen;
    private boolean playing;
    private boolean soundPlayed = false;
    private boolean spectating = false;
    private byte spectateID = Byte.MIN_VALUE;

    PlayerCanvas(PlayerRunner pr, PlayerScreen ps, byte playerID) {
        super(GraphicsUtilities.getGraphicsConfiguration());
        playerRunner = pr;
        playerScreen = ps;
        this.playerID = playerID;
        setFocusable(true);
        setIgnoreRepaint(true);
        setSize(PlayerRunner.SCREEN_WIDTH, PlayerRunner.SCREEN_HEIGHT);
        inputHandler = new InputHandler(this, playerID);
        inputHandler.start();
    }

    int getClipCount() {
        return clipCount;
    }

    PlayerScreen getPlayerScreen() {
        return playerScreen;
    }

    PlayerRunner getPlayerRunner() {
        return playerRunner;
    }

    void initGraphics() {
        createBufferStrategy(2);
        bufferStrategy = getBufferStrategy();
    }

    boolean isPlaying() {
        return playing;
    }

    boolean isSpectating() {
        return spectating;
    }

    void kill() {
        inputHandler.kill();
    }

    private void paintAmmoBar(Graphics2D g, int clipCount, int clipSize) {
        int bulletWidth = 4;
        int bulletHeight = 20;
        int gap = 8;
        g.setColor(new Color(255, 255, 255, 120));
        int x = clipSize * gap + 10;
        for (int i = 0; i < clipCount; i++) {
            g.fillRect(PlayerRunner.SCREEN_WIDTH - x, PlayerRunner.SCREEN_HEIGHT - bulletHeight - 10, bulletWidth, bulletHeight);
            x -= gap;
        }
        g.setColor(new Color(84, 84, 84, 120));
        for (int i = 0; i < clipSize - clipCount; i++) {
            g.fillRect(PlayerRunner.SCREEN_WIDTH - x, PlayerRunner.SCREEN_HEIGHT - bulletHeight - 10, bulletWidth, bulletHeight);
            x -= gap;
        }
    }

    private void paintBasePercentages(Graphics2D g) {
        g.setFont(g.getFont().deriveFont(25f));
        ArrayList<Base> bases = playerRunner.getGrid().getBases();
        for (int i = 0; i < bases.size(); i++) {
            Base base = bases.get(i);

            if (base.getProgressTeam() == Team.TEAM_GREEN) {
                g.setColor(Color.GREEN);
            } else if (base.getProgressTeam() == Team.TEAM_RED) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.GRAY);
            }
            g.drawString("Base " + (i + 1) + ": " + base.getProgress() + "%",
                    PlayerRunner.SCREEN_WIDTH - 620,
                    PlayerRunner.SCREEN_HEIGHT - 440 + 30 * i);
        }
    }

    private void paintCountdownScreen(Graphics2D g, int countdownNumber) {
        g.setColor(Color.BLUE);
        g.setFont(g.getFont().deriveFont(36f));
        g.drawString("Game starting in " + String.valueOf(countdownNumber), PlayerRunner.SCREEN_WIDTH / 8, PlayerRunner.SCREEN_HEIGHT / 2);
    }

    private void paintGridObjects(Graphics2D g, Player player) {
        ArrayList<DisplayableGridObject> gridObjects = playerRunner.getGrid().getDisplayableGridObjects();
        for (DisplayableGridObject object : gridObjects) {
            if (!(object instanceof Player) || (((Player) object).isAlive()
                    && ((Player) object).getPlayerID() != player.getPlayerID())) {
                int posX = PlayerRunner.SCREEN_WIDTH / 2
                        + (object.getPositionX() - player.getPositionX())
                        - (player.getSprite().getWidth() - object.getSprite().getWidth()) / 2;
                int posY = PlayerRunner.SCREEN_HEIGHT / 2
                        + (object.getPositionY() - player.getPositionY())
                        - (player.getSprite().getHeight() - object.getSprite().getHeight()) / 2;
                g.rotate(object.getDirection(), posX, posY);

                g.drawImage(object.getSprite().getImage(),
                        posX - object.getSprite().getWidth() / 2,
                        posY - object.getSprite().getHeight() / 2, null);
                g.rotate(-object.getDirection(), posX, posY);
                if (object instanceof Player) {
                    paintProgressBar(g, posX - object.getSprite().getWidth() / 2 - 20,
                            posY - object.getSprite().getHeight() / 2 - 25, 100, 20,
                            Color.GREEN, ((Player) object).getHealth(), 100);
                    paintAmmoBar(g, getClipCount(), CLIP_SIZE);
                }
            }
        }
    }

    private void paintMap(Graphics2D g, Player player) {
        Point p = new Point(player.getPositionX() - (PlayerRunner.SCREEN_WIDTH - player.getSprite().getWidth()) / 2,
                player.getPositionY() - (PlayerRunner.SCREEN_HEIGHT - player.getSprite().getHeight()) / 2);
        playerRunner.getGrid().getMap().paint(g, p, PlayerRunner.SCREEN_WIDTH, PlayerRunner.SCREEN_HEIGHT);
    }

    private void paintPauseScreen(Graphics2D g) {
        g.setColor(new Color(128, 128, 128, 128));
        g.fillRect(20, 20, 600, 440);
        g.setColor(new Color(0, 0, 0, 192));
        g.setFont(g.getFont().deriveFont(36f).deriveFont(Font.BOLD));
        g.drawString("Scoreboard", 220, 60);
        g.fillRect(20, 65, 600, 1);
        g.fillRect(320, 65, 1, 395);
        g.setFont(g.getFont().deriveFont(18f).deriveFont(Font.BOLD));
        g.drawString("Green Team - " + playerRunner.getGrid().getTeam(Team.TEAM_GREEN).getScore(), 120, 90);
        g.drawString("Red Team - " + playerRunner.getGrid().getTeam(Team.TEAM_RED).getScore(), 430, 90);
        g.setFont(g.getFont().deriveFont(16f));
        g.drawString("Player   Score   Kills   Deaths", 50, 115);
        g.drawString("Player   Score   Kills   Deaths", 350, 115);
        g.setFont(g.getFont().deriveFont(15f).deriveFont(Font.ITALIC));
        int y1 = 135;
        int y2 = 135;
        ArrayList<Player> playerList = playerRunner.getGrid().getPlayers();
        Player[] players = new Player[playerList.size()];
        playerList.toArray(players);
        Arrays.sort(players, new Comparator<Player>() {

            public int compare(Player p1, Player p2) {
                int diff = p2.getScore() - p1.getScore();
                if (diff == 0) {
                    return p1.getUsername().compareTo(p2.getUsername());
                } else {
                    return diff;
                }
            }
        });
        for (Player pl : players) {
            if (pl.getSprite().getTeamID() == Team.TEAM_GREEN) {
                g.drawString("" + pl.getUsername(), 50, y1);
                g.drawString(String.valueOf(pl.getScore()), 120, y1);
                g.drawString(String.valueOf(pl.getKillCount()), 190, y1);
                g.drawString(String.valueOf(pl.getDeathCount()), 260, y1);
                y1 += 20;
            } else if (pl.getSprite().getTeamID() == Team.TEAM_RED) {
                g.drawString("" + pl.getUsername(), 350, y2);
                g.drawString(String.valueOf(pl.getScore()), 420, y2);
                g.drawString(String.valueOf(pl.getKillCount()), 490, y2);
                g.drawString(String.valueOf(pl.getDeathCount()), 560, y2);
                y2 += 20;
            }
        }
    }

    private void paintPlayer(Graphics2D g, Player player) {
        float dir = player.getDirection();
        g.rotate(dir, PlayerRunner.SCREEN_WIDTH / 2, PlayerRunner.SCREEN_HEIGHT / 2);
        g.drawImage(player.getSprite().getImage(),
                (PlayerRunner.SCREEN_WIDTH - player.getSprite().getWidth()) / 2,
                (PlayerRunner.SCREEN_HEIGHT - player.getSprite().getHeight()) / 2, null);
        g.rotate(-dir, PlayerRunner.SCREEN_WIDTH / 2, PlayerRunner.SCREEN_HEIGHT / 2);
        paintProgressBar(g, (PlayerRunner.SCREEN_WIDTH - player.getSprite().getWidth()) / 2 - 20,
                (PlayerRunner.SCREEN_HEIGHT - player.getSprite().getHeight()) / 2 - 25, 100, 20,
                Color.GREEN, player.getHealth(), 100);
        paintAmmoBar(g, getClipCount(), CLIP_SIZE);
    }

    private void paintProgressBar(Graphics2D g, int x, int y, int width, int height,
            Color color, int value, int maxValue) {
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 96));
        g.fillRect(x + 1, y + 1, (width - 2) * value / maxValue, height - 2);
        g.setColor(new Color(0, 0, 0, 128));
        g.drawRect(x, y, width, height);
        g.drawRect(x + 1, y + 1, width - 2, height - 2);
        g.drawString(value + "/" + maxValue, x + width / 2 - 18, y + height / 2 + 5);
    }

    private void paintTimeLeft(Graphics2D g) {
        g.setFont(g.getFont().deriveFont(25f));
        short secondsLeft = playerRunner.getGrid().getSecondsLeft();
        if (secondsLeft != -1) {
            g.setColor(Color.WHITE);
            String minutes = String.valueOf(secondsLeft / 60);
            String seconds = String.valueOf(secondsLeft % 60);
            if (seconds.length() == 1) {
                seconds = 0 + seconds;
            }
            g.drawString(minutes + ":" + seconds,
                    PlayerRunner.SCREEN_WIDTH - 580, PlayerRunner.SCREEN_HEIGHT - 10);
        }
    }

    private void paintScores(Graphics2D g) {
        g.setFont(g.getFont().deriveFont(25f));
        g.setColor(Color.GREEN);
        g.drawString(Short.toString(playerRunner.getGrid().getTeam(Team.TEAM_GREEN).getScore()),
                PlayerRunner.SCREEN_WIDTH - 620, PlayerRunner.SCREEN_HEIGHT - 35);
        g.setColor(Color.RED);
        g.drawString(Short.toString(playerRunner.getGrid().getTeam(Team.TEAM_RED).getScore()),
                PlayerRunner.SCREEN_WIDTH - 620, PlayerRunner.SCREEN_HEIGHT - 10);
    }

    private void paintSpawnScreen(Graphics2D g, Player player) {
        g.setColor(Color.BLUE);
        g.setFont(g.getFont().deriveFont(36f));
        if (player == null) {
            g.drawString("Waiting to spawn...", PlayerRunner.SCREEN_WIDTH / 8, PlayerRunner.SCREEN_HEIGHT / 2);
        } else {
            g.drawString("Waiting to respawn...", PlayerRunner.SCREEN_WIDTH / 8, PlayerRunner.SCREEN_HEIGHT / 2);
        }
    }

    synchronized void renderImage() {
        Player player;
        if (isSpectating()) {
            player = playerRunner.getGrid().getPlayer(spectateID);
        } else {
            player = playerRunner.getGrid().getPlayer(playerID);
        }
        Graphics2D g = (Graphics2D) bufferStrategy.getDrawGraphics();
        g.setBackground(Color.BLACK);
        g.clearRect(0, 0, PlayerRunner.SCREEN_WIDTH, PlayerRunner.SCREEN_HEIGHT);

        if (playerRunner.getGrid().isGameOver()) {
            inputHandler.kill();
            paintPauseScreen(g);
        } else if (playerRunner.isRetrievingMap()) {
            paintSpawnScreen(g, player);
        } else if ((player == null || !player.isAlive()) && !playerRunner.isCountdownActive()) {
            if (isSpectating()) {
                spectateNext();
            }
            clipCount = CLIP_SIZE;
            if (playing && !soundPlayed) {
                playerRunner.getSoundSystem().playSound(SoundSystem.WILHELM_SCREAM, 0);
                soundPlayed = true;
            }
            playing = false;
            paintSpawnScreen(g, player);
        } else if (playerRunner.isCountdownActive()) {
            paintCountdownScreen(g, playerRunner.getCountdownNumber());
        } else {
            playing = true;
            soundPlayed = false;

            paintMap(g, player);
            paintPlayer(g, player);
            paintGridObjects(g, player);

            if (pauseScreen) {
                paintPauseScreen(g);
            } else {
                paintBasePercentages(g);
                paintScores(g);
                paintTimeLeft(g);
            }
        }
        g.dispose();
        bufferStrategy.show();
        Toolkit.getDefaultToolkit().sync();
    }

    void setClipCount(int count) {
        clipCount = count;
    }

    void setCountdownActive(boolean isActive) {
        countdownActive = isActive;
    }

    void setSpectating(boolean isSpectating) {
        spectating = isSpectating;
    }

    synchronized void spectateNext() {
        byte original = spectateID;
        Player player = null;
        do {
            player = playerRunner.getGrid().getPlayer(++spectateID);
        } while (original != spectateID && (player == null || !player.isAlive() /*|| player.getSprite().getTeamID() != playerRunner.getGrid().getPlayer(playerID).getSprite().getTeamID()*/));
    }

    synchronized void spectatePrevious() {
        byte original = spectateID;
        Player player = null;
        do {
            player = playerRunner.getGrid().getPlayer(--spectateID);
        } while (original != spectateID && (player == null || !player.isAlive() /*|| player.getSprite().getTeamID() != playerRunner.getGrid().getPlayer(playerID).getSprite().getTeamID()*/));
    }

    void togglePauseScreen() {
        pauseScreen = !pauseScreen;
    }
}
