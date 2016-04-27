package wrrrrrm;

/**
 * Created by Masha on 27.04.16.
 */

import java.math.BigInteger;
import java.util.ArrayList;

import api.AxialCoordinate;
import api.Hexagon;
import api.HexagonalGrid;

import static api.AxialCoordinate.fromCoordinates;

public class HeapFigure {

    private ArrayList<Hexagon> figure;  //
    ArrayList <ArrayList<Hexagon>> pack = new ArrayList<ArrayList<Hexagon>>(); //
    ArrayList<BigInteger> pseudoRandSeq = new ArrayList<>();
    private AxialCoordinate ax;
    private HexagonalGrid hexagonalGrid;

    public HeapFigure(HexagonalGrid hexagonalGrid ) {
        this.hexagonalGrid = hexagonalGrid;
        int amountUnits = 0;
        for (int i = 0; i < amountUnits; i++)
        {
//            figure = makeFigure();
            //TODO: здесь запихиваем все фигуры в "ПАК"
        }
    }

    public ArrayList<Hexagon> makeFigure(int memberLength) {
        ArrayList<Hexagon> figure = new ArrayList<Hexagon>();

        for (int i = 0; i < memberLength; i++)
        {
            ax = fromCoordinates(1, 1);
            figure.add(hexagonalGrid.getByAxialCoordinate(ax).get());
        }

        //TODO: тут будет создание фигуры
        return figure;
    }


    public void getFigure(int sourceLength , int sourceSeeds , int amountUnits) {
        Lcg randSlow = new Lcg(BigInteger.valueOf(sourceSeeds));
        //TODO: sequence starting mod amountUnits
        for (int i = 0; i < sourceLength; i++){ //sequence starting
            pseudoRandSeq.add(randSlow.getState());
            randSlow.next();
        }
    }
}
