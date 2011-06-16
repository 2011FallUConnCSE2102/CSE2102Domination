package gameplay;

import networking.NetUtils;

/**
 *
 * @author Scott
 */
public class Team extends GridObject {

    private static final int BYTE_COUNT = 4;
    static final byte INCREMENT_BASE = 5;
    static final byte INCREMENT_KILL = 1;
    public static final byte TEAM_GREEN = -128;
    public static final byte TEAM_RED = -127;
    public static final byte TEAM_UNDEFINED = -126;
    private final byte[] score = new byte[2];
    private byte teamID;

    public Team(Grid grid, byte id) {
        super(grid);
        teamID = id;
    }

    public Team(Grid grid, byte[] bytes, int startPosition) {
        super(grid);
        set(bytes, startPosition);
    }

    @Override
    int getInputSize() {
        return super.getInputSize() + BYTE_COUNT;
    }

    @Override
    int getOutputSize() {
        return super.getOutputSize() + BYTE_COUNT;
    }

    public short getScore() {
        return NetUtils.bytesToShort(score);
    }

    byte getTeamID() {
        return teamID;
    }

    void incrementScore(int increment) {
        short newScore = (short) (getScore() + increment);
        setScore(newScore);
        if (getGrid().getOptions().getScoreLimit() != -1
                && newScore >= getGrid().getOptions().getScoreLimit()) {
            getGrid().setGameOver(true);
        }
    }

    void set(byte[] bytes, int startPosition) {
        System.arraycopy(bytes, startPosition, score, 0, score.length);
        startPosition += score.length;
        teamID = bytes[startPosition];
    }

    private void setScore(short s) {
        NetUtils.shortToBytes(s, score, 0);
    }

    @Override
    void toByteArray(byte[] bytes, int offset) {
        super.toByteArray(bytes, offset);
        offset += super.getOutputSize();
        System.arraycopy(score, 0, bytes, offset, score.length);
        offset += score.length;
        bytes[offset] = teamID;
    }
}
