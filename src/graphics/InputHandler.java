package graphics;

import gameplay.Grid;
import gameplay.Player;
import gameplay.PlayerRunner;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import sound.SoundSystem;

/**
 *
 * @author Oliver Cai
 * @author Ian Dardik
 * @author Scott DellaTorre
 * @author Xurong Liu
 * @author Erik Scott
 * @author Ammar Zafar
 */
class InputHandler extends Thread implements FocusListener, KeyListener,
        MouseListener, MouseMotionListener {

    private static final int DELAY = 5;
    private final CheatCode KONAMI_CODE = new CheatCode(
            new int[]{KeyEvent.VK_UP, KeyEvent.VK_UP, KeyEvent.VK_DOWN,
                KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT,
                KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT, KeyEvent.VK_B, KeyEvent.VK_A,
                KeyEvent.VK_ENTER}, new Runnable() {

        public void run() {
            PlayerRunner runner = canvas.getPlayerRunner();
            runner.sendMessage(runner.getGrid().getBytesForChange(Grid.ACTIVATE_KONAMI_CODE, playerID, null));

        }
    });
    private final CheatCode SHOW_GRID_CODE = new CheatCode(
            new int[]{KeyEvent.VK_G, KeyEvent.VK_R, KeyEvent.VK_I, KeyEvent.VK_D},
            new Runnable() {

                public void run() {
                    canvas.getPlayerRunner().getGrid().getMap().toggleGrid();
                }
            });
    private final CheatCode SPECTATE_CODE = new CheatCode(
            new int[]{KeyEvent.VK_S, KeyEvent.VK_P, KeyEvent.VK_E, KeyEvent.VK_C,
                KeyEvent.VK_T, KeyEvent.VK_A, KeyEvent.VK_T, KeyEvent.VK_E},
            new Runnable() {

                public void run() {
                    canvas.setSpectating(!canvas.isSpectating());
                }
            });
    private final CheatCode[] CHEAT_CODES = {KONAMI_CODE, SHOW_GRID_CODE, SPECTATE_CODE};
    private boolean bulletFired = false;
    private PlayerCanvas canvas;
    private boolean down;
    private boolean fireSingle;
    private boolean left;
    private boolean mouse;
    private boolean mouseIn;
    private byte playerID;
    private boolean reloading;
    private boolean right;
    private boolean run = true;
    private boolean up;
    private int updateNum;

    InputHandler(PlayerCanvas c, byte playerID) {
        super("Input Handler Thread");
        canvas = c;
        this.playerID = playerID;
        canvas.addFocusListener(this);
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        setDaemon(true);
    }

    public void focusGained(FocusEvent evt) {
    }

    public void focusLost(FocusEvent evt) {
        down = false;
        left = false;
        mouse = false;
        right = false;
        up = false;
    }

    public void keyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == KeyEvent.VK_DOWN
                || evt.getKeyCode() == KeyEvent.VK_S) {
            down = true;
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT
                || evt.getKeyCode() == KeyEvent.VK_A) {
            left = true;
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT
                || evt.getKeyCode() == KeyEvent.VK_D) {
            right = true;
        } else if (evt.getKeyCode() == KeyEvent.VK_UP
                || evt.getKeyCode() == KeyEvent.VK_W) {
            up = true;
        }
    }

    public void keyReleased(KeyEvent evt) {
        for (CheatCode cc : CHEAT_CODES) {
            cc.update(evt);
        }
        if (evt.getKeyCode() == KeyEvent.VK_DOWN
                || evt.getKeyCode() == KeyEvent.VK_S) {
            down = false;
        } else if (evt.getKeyCode() == KeyEvent.VK_LEFT
                || evt.getKeyCode() == KeyEvent.VK_A) {
            left = false;
            if (canvas.isSpectating()) {
                canvas.spectatePrevious();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_RIGHT
                || evt.getKeyCode() == KeyEvent.VK_D) {
            right = false;
            if (canvas.isSpectating()) {
                canvas.spectateNext();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_UP
                || evt.getKeyCode() == KeyEvent.VK_W) {
            up = false;
        } else if (evt.getKeyCode() == KeyEvent.VK_P) {
            if (canvas.isPlaying()) {
                canvas.togglePauseScreen();
            }
        } else if (evt.getKeyCode() == KeyEvent.VK_R) {
            if (canvas.getClipCount() < PlayerCanvas.CLIP_SIZE && !reloading) {
                canvas.setClipCount(0);
                fireSingle = false;
                reloading = true;
                canvas.getPlayerRunner().getSoundSystem().playSound(SoundSystem.DROP_CLIP, 0);
            }
        }
    }

    public void keyTyped(KeyEvent evt) {
    }

    void kill() {
        if (run) {
            canvas.removeFocusListener(this);
            canvas.removeKeyListener(this);
            canvas.removeMouseListener(this);
            canvas.removeMouseMotionListener(this);
            run = false;
        }
    }

    public void mouseClicked(MouseEvent evt) {
    }

    public void mouseDragged(MouseEvent evt) {
        mouseMoved(evt);
    }

    public void mouseEntered(MouseEvent evt) {
        mouseIn = true;
    }

    public void mouseExited(MouseEvent evt) {
        mouse = false;
        mouseIn = false;
    }

    public void mouseMoved(MouseEvent evt) {
    }

    public void mousePressed(MouseEvent evt) {
        if (canvas.isPlaying()) {
            bulletFired = false;
            mouse = true;
        }
    }

    public void mouseReleased(MouseEvent evt) {
        mouse = false;
        if (canvas.isPlaying() && !bulletFired && !reloading) {
            fireSingle = true;
        }
    }

    @Override
    public void run() {
        while (run) {
            try {
                updateNum++;
                Player player = canvas.getPlayerRunner().getGrid().getPlayer(playerID);
                if (player != null) {
                    if (player.isAlive() && !canvas.isSpectating()) {
                        if (updateNum % 40 == 0) {
                            updateBullets(player);
                        }
                        updateDirection(player);
                        updateLocation();
                    }
                    if (updateNum % 10 == 0 && reloading) {
                        canvas.setClipCount(canvas.getClipCount() + 1);
                        if (canvas.getClipCount() == PlayerCanvas.CLIP_SIZE) {
                            canvas.getPlayerRunner().getSoundSystem().playSound(SoundSystem.LOAD_CLIP, 0);
                            reloading = false;
                        }
                    }
                    if (updateNum % 20 == 0) {
                        updatePlayer();
                    }
                }
                try {
                    sleep(DELAY);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            } catch (IllegalStateException ise) {
                ise.printStackTrace();
            }
        }
    }

    private void updateBullets(Player player) {
        PlayerRunner runner = canvas.getPlayerRunner();
        if ((fireSingle || mouse) && canvas.getClipCount() > 0 && !reloading) {
            canvas.setClipCount(canvas.getClipCount() - 1);
            bulletFired = true;
            fireSingle = false;
            canvas.getPlayerRunner().getSoundSystem().playSound(SoundSystem.ASSAULT_RIFLE, 0);
            short posX = (short) (player.getPositionX()
                    + (player.getSprite().getWidth() / 2)
                    * (Math.cos(player.getDirection() - Math.PI / 2) + 1));
            short posY = (short) (player.getPositionY()
                    + (player.getSprite().getHeight() / 2)
                    * (Math.sin(player.getDirection() - Math.PI / 2) + 1));
            runner.sendMessage(runner.getGrid().getBytesForChange(
                    Grid.FIRE_BULLET, player.getPlayerID(),
                    new Object[]{player.getDirection(), posX, posY}));
        }
        if (canvas.getClipCount() == 0 && (fireSingle || mouse)) {
            canvas.getPlayerRunner().getSoundSystem().playSound(SoundSystem.DRY_FIRE, 0);
            fireSingle = false;
        }
    }

    public void updateDirection(Player player) {
        if (mouseIn) {
            Point mousePos = MouseInfo.getPointerInfo().getLocation();
            mousePos.translate(-canvas.getPlayerScreen().getX(), -canvas.getPlayerScreen().getY());
            double x = (mousePos.getX() - PlayerRunner.SCREEN_WIDTH / 2) - 10;     // Subtract 10 to fine tweak the direction from mouse pos. Value found through trial and error.
            double y = (mousePos.getY() - PlayerRunner.SCREEN_HEIGHT / 2) - 27;    // Subtract 27 to fine tweak the direction from mouse pos. Value found through trial and error.

            float angle = 0;

            //this prevents the player from disappearing when the cursor is at (0, 0)
            if (y != 0) {
                angle = (float) Math.atan(y / x);
            }

            if (x < 0) {
                angle += Math.PI;
            } else if (x >= 0 && y < 0) {
                angle += 2 * Math.PI;
            }
            angle += Math.PI / 2;
            PlayerRunner runner = canvas.getPlayerRunner();
            if (player.getDirection() != angle) {
                player.setDirection(angle);
                runner.sendMessage(runner.getGrid().getBytesForChange(
                        Grid.CHANGE_DIRECTION, playerID,
                        new Object[]{angle}));
            }
        }
    }

    private void updateLocation() {
        PlayerRunner runner = canvas.getPlayerRunner();
        runner.sendMessage(runner.getGrid().getBytesForChange(
                Grid.MOVE, playerID, new Boolean[]{down, left, right, up}));
    }

    public void updatePlayer() {
        PlayerRunner runner = canvas.getPlayerRunner();
        runner.sendMessage(runner.getGrid().getBytesForChange(Grid.KEEP_ALIVE, playerID, null));
    }

    private class CheatCode {

        private Runnable action;
        private int[] cheatCode;
        private int index = 0;

        private CheatCode(int[] code, Runnable action) {
            this.action = action;
            cheatCode = code;
        }

        private void update(KeyEvent evt) {
            if (evt.getKeyCode() == cheatCode[index]) {
                if (++index == cheatCode.length) {
                    action.run();
                    index = 0;
                }
            } else {
                index = 0;
            }
        }
    }
}
