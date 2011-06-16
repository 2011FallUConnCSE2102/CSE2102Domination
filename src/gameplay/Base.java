package gameplay;

import graphics.maps.TileAttributes;

/**
 *
 * @author Scott
 */
public class Base extends GridObject {

    public static final byte BASE_ONE = TileAttributes.BASE_ONE;
    public static final byte BASE_TWO = TileAttributes.BASE_TWO;
    public static final byte BASE_THREE = TileAttributes.BASE_THREE;
    private static final int BYTE_COUNT = 4;
    private static final byte MAX_PROGRESS = 100;
    private static final byte MIN_PROGRESS = 0;
    private static final byte PROGRESS_INCREMENT = 2;
    private byte holder = Team.TEAM_UNDEFINED;
    private byte progress = MIN_PROGRESS;
    private byte progressTeam = Team.TEAM_UNDEFINED;
    private byte type;

    public Base(byte baseType) {
        type = baseType;
        if (type != BASE_ONE && type != BASE_TWO && type != BASE_THREE) {
            throw new IllegalArgumentException("Invalid base type: " + type);
        }
    }

    public Base(Grid grid, byte[] bytes, int startPosition) {
        super(grid);
        set(bytes, startPosition);
    }

    private void decrementProgress(byte teamID, int players) {
        int result = progress - PROGRESS_INCREMENT * players;
        if (result <= MIN_PROGRESS) {
            holder = Team.TEAM_UNDEFINED;
            if (result == MIN_PROGRESS) {
                progressTeam = Team.TEAM_UNDEFINED;
            } else {
                result = MIN_PROGRESS;
                progressTeam = teamID;
            }
            incrementProgress(teamID, MIN_PROGRESS - result);
        }
        progress = (byte) result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Base) {
            return type == ((Base) obj).getBaseType();
        } else {
            return false;
        }
    }

    public byte getBaseType() {
        return type;
    }

    public byte getHolder() {
        return holder;
    }

    @Override
    int getInputSize() {
        return super.getInputSize() + BYTE_COUNT;
    }

    @Override
    int getOutputSize() {
        return super.getOutputSize() + BYTE_COUNT;
    }

    public byte getProgress() {
        return progress;
    }

    public byte getProgressTeam() {
        return progressTeam;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + type;
        return hash;
    }

    private void incrementProgress(byte teamID, int players) {
        progressTeam = teamID;
        int result = progress + PROGRESS_INCREMENT * players;
        if (result > MAX_PROGRESS) {
            result = MAX_PROGRESS;
        }
        progress = (byte) result;
        if (progress == MAX_PROGRESS) {
            holder = teamID;
        }
    }

    void set(byte[] bytes, int startPosition) {
        holder = bytes[startPosition++];
        progress = bytes[startPosition++];
        progressTeam = bytes[startPosition++];
        type = bytes[startPosition++];
    }

    @Override
    void toByteArray(byte[] bytes, int offset) {
        super.toByteArray(bytes, offset);
        offset += super.getOutputSize();
        bytes[offset++] = holder;
        bytes[offset++] = progress;
        bytes[offset++] = progressTeam;
        bytes[offset] = type;
    }

    void updateProgress(byte teamID, int players) {
        if (holder == teamID) {
            incrementProgress(teamID, players);
        } else if (holder == Team.TEAM_UNDEFINED) {
            if (progressTeam == teamID || progressTeam == Team.TEAM_UNDEFINED) {
                incrementProgress(teamID, players);
            } else {
                decrementProgress(teamID, players);
            }
        } else {
            decrementProgress(teamID, players);
        }
    }
}
