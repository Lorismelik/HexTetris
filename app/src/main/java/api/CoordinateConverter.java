package api;

/**
 * Utility class for converting coordinated from the offset system to
 * the axial one (the library uses the latter).
 */
public final class CoordinateConverter {

    public CoordinateConverter() {
        throw new UnsupportedOperationException("This utility class is not meant to be instantiated.");
    }

    /**
     * Calculates the axial X coordinate based on an offset coordinate pair.
     */
    public static int convertOffsetCoordinatesToAxialX(final int offsetX, final int offsetY, final HexagonOrientation orientation) {
        return HexagonOrientation.FLAT_TOP.equals(orientation) ? offsetX : offsetX - offsetY / 2;
    }

    /**
     * Calculates the axial Z coordinate based on an offset coordinate pair.
     */
    public static int convertOffsetCoordinatesToAxialZ(final int offsetX, final int offsetY, final HexagonOrientation orientation) {
        return HexagonOrientation.FLAT_TOP.equals(orientation) ? offsetY - offsetX / 2 : offsetY;
    }

}