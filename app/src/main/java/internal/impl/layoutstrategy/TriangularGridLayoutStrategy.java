package internal.impl.layoutstrategy;

import api.HexagonalGridBuilder;
import api.AxialCoordinate;
import api.HexagonalGrid;

import java.util.LinkedHashSet;
import java.util.Set;

import static api.AxialCoordinate.fromCoordinates;

/**
 * This strategy is responsible for generating a {@link HexagonalGrid} which has a triangular
 * shape.
 */
public final class TriangularGridLayoutStrategy implements GridLayoutStrategy {

    @Override
    public Set<AxialCoordinate> fetchGridCoordinates(HexagonalGridBuilder builder) {
        Set<AxialCoordinate> coordinates = new LinkedHashSet<>();
        final int gridSize = builder.getGridHeight();
        for (int gridZ = 0; gridZ < gridSize; gridZ++) {
            final int endX = gridSize - gridZ;
            for (int gridX = 0; gridX < endX; gridX++) {
                coordinates.add(fromCoordinates(gridX, gridZ));
            }
        }
        return coordinates;
    }

    @Override
    public boolean checkParameters(final int gridHeight, final int gridWidth) {
        final boolean superResult = GridLayoutStrategy.super.checkParameters(gridHeight, gridWidth);
        final boolean result = gridHeight == gridWidth;
        return superResult && result;
    }
}