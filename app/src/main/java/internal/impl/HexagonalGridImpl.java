package internal.impl;


import api.AxialCoordinate;
import api.CoordinateConverter;
import api.Hexagon;
import api.HexagonalGrid;
import api.HexagonalGridBuilder;
import api.Point;
import internal.GridData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Iterator;
import backport.Optional;
import java.util.Set;

import static api.AxialCoordinate.fromCoordinates;
import static api.Point.fromPosition;
import static internal.impl.HexagonImpl.newHexagon;


@SuppressWarnings("PMD.UnusedPrivateField")
public final class HexagonalGridImpl implements HexagonalGrid {

    private static final int[][] NEIGHBORS = {{+1, 0}, {+1, -1}, {0, -1}, {-1, 0}, {-1, +1}, {0, +1}};
    private static final int NEIGHBOR_X_INDEX = 0;
    private static final int NEIGHBOR_Z_INDEX = 1;

    private final GridData gridData;
    private final Map<AxialCoordinate, Object> hexagonStorage;
    private final Set<AxialCoordinate> coordinates;


    public HexagonalGridImpl(final HexagonalGridBuilder builder) {
        this.gridData = builder.getGridData();
        this.hexagonStorage = builder.getCustomStorage();
        this.coordinates = builder.getGridLayoutStrategy().fetchGridCoordinates(builder);
    }


    @Override
    public Iterable<Hexagon> getHexagons() {
        ArrayList <Hexagon> Hexagons = new  ArrayList();
        Iterator<AxialCoordinate> iterator = coordinates.iterator();
        do {
            Hexagons.add(newHexagon(gridData,iterator.next(), hexagonStorage));
        } while(iterator.hasNext());
        return Hexagons;
    }

    @Override
    public Iterable<Hexagon> getHexagonsByAxialRange(final AxialCoordinate from, final AxialCoordinate to) {
        ArrayList <Hexagon> Hexagons = new  ArrayList();
        for (int x = from.getGridX(); x <= to.getGridX(); x++)
        {
            for (int z = from.getGridZ(); z <= to.getGridZ(); z++)
            {
                if(getByAxialCoordinate(fromCoordinates(x, z)).isPresent())
                {
                    Hexagons.add(getByAxialCoordinate(fromCoordinates(x, z)).get());
                }
            }
        }
        return Hexagons;
    }

    @Override
    public Iterable<Hexagon> getHexagonsByOffsetRange(final int gridXFrom, final int gridXTo, final int gridYFrom, final int gridYTo) {
        ArrayList<Hexagon> Hexagons = new ArrayList();
        for (int x = gridXFrom; x <= gridXTo; x++) {
            for (int y = gridYFrom; y <= gridYTo; y++) {
                final int axialX = CoordinateConverter.convertOffsetCoordinatesToAxialX(x, y);
                final int axialZ = CoordinateConverter.convertOffsetCoordinatesToAxialZ(x, y);
                if(getByAxialCoordinate(fromCoordinates(axialX, axialZ)).isPresent())
                {
                    Hexagons.add(getByAxialCoordinate(fromCoordinates(axialX, axialZ)).get());
                }
            }
        }


        return Hexagons;
    }

    @Override
    public boolean containsAxialCoordinate(final AxialCoordinate coordinate) {
        for( AxialCoordinate neighbor : this.coordinates)
        {
            if ((coordinate.getGridX()==neighbor.getGridX())&&(coordinate.getGridZ()==neighbor.getGridZ()))
                return true;
        }
        return false;
    }

    @Override
    public Optional<Hexagon> getByAxialCoordinate(final AxialCoordinate coordinate) {
        return containsAxialCoordinate(coordinate)
                ? Optional.of(newHexagon(gridData, coordinate, hexagonStorage))
                : Optional.empty();
    }

    @Override
    public Optional<Hexagon> getByPixelCoordinate(final double coordinateX, final double coordinateY) {
        int estimatedGridX = (int) (coordinateX / gridData.getHexagonWidth());
        int estimatedGridZ = (int) (coordinateY / gridData.getHexagonHeight());
        estimatedGridX = CoordinateConverter.convertOffsetCoordinatesToAxialX(estimatedGridX, estimatedGridZ);
        estimatedGridZ = CoordinateConverter.convertOffsetCoordinatesToAxialZ(estimatedGridX, estimatedGridZ);
        // it is possible that the estimated coordinates are off the grid so we
        // create a virtual hexagon
        final AxialCoordinate estimatedCoordinate = fromCoordinates(estimatedGridX, estimatedGridZ);
        final Hexagon tempHex = newHexagon(gridData, estimatedCoordinate, hexagonStorage);


        Hexagon trueHex = refineHexagonByPixel(tempHex, fromPosition(coordinateX, coordinateY));


        if (hexagonsAreAtTheSamePosition(tempHex, trueHex)) {
            return getByAxialCoordinate(estimatedCoordinate);
        } else {
            return containsAxialCoordinate(trueHex.getAxialCoordinate()) ? Optional.of(trueHex) : Optional.empty();
        }
    }

    @Override
    public Iterable<Hexagon> getNeighborsOf(final Hexagon hexagon) {
        final Set<Hexagon> neighbors = new HashSet<>();
        for (final int[] neighbor : NEIGHBORS) {
            Hexagon retHex;
            final int neighborGridX = hexagon.getGridX() + neighbor[NEIGHBOR_X_INDEX];
            final int neighborGridZ = hexagon.getGridZ() + neighbor[NEIGHBOR_Z_INDEX];
            final AxialCoordinate neighborCoordinate = fromCoordinates(neighborGridX, neighborGridZ);
            if (containsAxialCoordinate(neighborCoordinate)) {
                retHex = getByAxialCoordinate(neighborCoordinate).get();
                neighbors.add(retHex);
            }
        }
        return neighbors;
    }

    @Override
    public GridData getGridData() {
        return gridData;
    }

    private boolean hexagonsAreAtTheSamePosition(final Hexagon hex0, final Hexagon hex1) {
        return hex0.getGridX() == hex1.getGridX() && hex0.getGridZ() == hex1.getGridZ();
    }

    private Hexagon refineHexagonByPixel(final Hexagon hexagon, final Point clickedPoint) {
        Hexagon refined = hexagon;
        double smallestDistance = clickedPoint.distanceFrom(fromPosition(refined.getCenterX(), refined.getCenterY()));
        for (final Hexagon neighbor : getNeighborsOf(hexagon)) {
            final double currentDistance = clickedPoint.distanceFrom(fromPosition(neighbor.getCenterX(), neighbor.getCenterY()));
            if (currentDistance < smallestDistance) {
                refined = neighbor;
                smallestDistance = currentDistance;
            }
        }
        return refined;
    }
}