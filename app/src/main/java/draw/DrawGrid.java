package draw;

import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Color;

import java.util.List;
import java.util.ArrayList;

import com.example.masha.tetris.Controller;
import com.example.masha.tetris.Figure;

import api.exception.HexagonalGridCreationException;
import api.Hexagon;
import api.HexagonOrientation;
import api.HexagonalGrid;
import api.HexagonalGridBuilder;
import api.HexagonalGridCalculator;
import api.HexagonalGridLayout;
import api.Point;

import internal.impl.HexagonData;

import static api.HexagonOrientation.POINTY_TOP;
import static api.HexagonalGridLayout.RECTANGULAR;
import static com.example.masha.tetris.Settings.width;
import static com.example.masha.tetris.Settings.height;
import static com.example.masha.tetris.Main.scrw;
import static com.example.masha.tetris.Main.scrh;


public class DrawGrid {

    private static final HexagonOrientation DEFAULT_ORIENTATION = POINTY_TOP;
    private static final HexagonalGridLayout DEFAULT_GRID_LAYOUT = RECTANGULAR;
    private HexagonalGrid hexagonalGrid;
    private HexagonalGridCalculator hexagonalGridCalculator;
    private Controller controller;
    private HexagonOrientation orientation = DEFAULT_ORIENTATION;
    private HexagonalGridLayout hexagonGridLayout = DEFAULT_GRID_LAYOUT;
    private int gridWidth = width , gridHeight = height;
    double radius;

    public DrawGrid () {

        if (gridHeight == 0) gridHeight = 15; //эти строчки надо удалить когда разберусь с preferen и переходами между activity
        if (gridWidth == 0) gridWidth = 8;
        radius = radGame();

        try {
            HexagonalGridBuilder builder = new HexagonalGridBuilder()
                    .setGridWidth(gridWidth)
                    .setGridHeight(gridHeight)
                    .setRadius(radius)
                    .setOrientation(orientation)
                    .setGridLayout(hexagonGridLayout);
            hexagonalGrid = builder.build();
            hexagonalGridCalculator = builder.buildCalculatorFor(hexagonalGrid);
            controller = new Controller(builder.getCustomStorage());
        } catch (HexagonalGridCreationException e) {}

    }


    public void useBuilder(Canvas canvas, String movement , int act) {

        switch (movement) {
            case "UP":
                break;

            case "DOWN":
                hexagonalGrid.setHexagonStorage(controller.movedown());
                break;

            case "RIGHT":
                hexagonalGrid.setHexagonStorage(controller.moveright());
                break;

            case "LEFT":
                hexagonalGrid.setHexagonStorage(controller.moveleft());
                break;
        }

        for (Hexagon hexagon : hexagonalGrid.getHexagons()) { //сетка
            int[] array = new int[12];
            drawPoly(canvas, convertToPointsArr(hexagon.getPoints(), array), 250, 175 ,  6, Style.STROKE, act);
        }

        for (HexagonData hexagon : hexagonalGrid.getHexagonStorage()) {
            int[] array = new int[12];

            if ((hexagonalGrid.getByAxialCoordinate(hexagon.coordinate).isPresent())&(hexagon.partOfLocked == false)) {

                drawPoly(canvas, convertToPointsArr(hexagonalGrid.getByAxialCoordinate(hexagon.coordinate).get().getPoints(), array), 250, 175, 6, Style.FILL, act); //фигруа

                if ((hexagon.coordinate.getGridZ()%2==1)) {
                    if ((hexagon.coordinate.getGridZ() == height - 1) || (hexagon.X == width-1  ) || (hexagon.X == 0))
                        for (HexagonData data : hexagonalGrid.getHexagonStorage())
                            data.partOfLocked = true;
                }
                else  if ((hexagon.coordinate.getGridZ() == height - 1) || (hexagon.X == width -3 ) || (hexagon.X == -1))
                    for (HexagonData data : hexagonalGrid.getHexagonStorage())
                        data.partOfLocked = true;


            }
        }

        for (HexagonData hexagon : hexagonalGrid.getHexagonStorage()) {
            int[] array = new int[12];
            if ((hexagonalGrid.getByAxialCoordinate(hexagon.coordinate).isPresent()) & (hexagon.partOfLocked == true))
                drawPoly(canvas, convertToPointsArr(hexagonalGrid.getByAxialCoordinate(hexagon.coordinate).get().getPoints(), array), 233,219,  193, Style.FILL_AND_STROKE , act);
        }
    }



    private void drawPoly(Canvas canvas, int[] array, int color, int color15 , int color1,  Style style , int act) {

        if (array.length < 12)
            return;

        Paint p = new Paint();
        p.setColor(Color.rgb(color, color15, color1));
        p.setStyle(style);
        p.setStrokeWidth(5);
        Path polyPath = new Path();
        polyPath.moveTo(array[0], array[1]); //первая точка

        for (int i = 0; i < 12;  i=i+2 )
            polyPath.lineTo(array[i], array[i+1]);

        polyPath.lineTo(array[0], array[1]);
        canvas.drawPath(polyPath, p);
        p.setStrokeWidth(1);
        p.setStyle(Style.FILL_AND_STROKE);
        p.setTextSize(40);

        if (act == 0)
            canvas.drawText("score:" , 30 , (float)scrh-15, p);
        else
        {
            canvas.drawText("add" , 30 , (float)scrh-15, p);
            canvas.drawText("play" , (float)scrw/2 , (float)scrh-15, p);
        }
    }


    private int[] convertToPointsArr (List <Point> points, int[] array) {
        int idx = 0;
        for (Point point : points) {
            array[idx] = (int) Math.round(point.getCoordinateX());
            array[idx+1] = (int) Math.round(point.getCoordinateY());
            idx=idx+2;
        }
        return array;
    }


    public double radGame()
    {
        radius = 2*scrw/(Math.sqrt(3)*(2*gridWidth+1)); //расчитываем радиус по ширине
        int dfkj = 50; //отступ для score или
        if ((radius*(gridHeight / 2 + gridHeight + (Math.sqrt(3) / 2 / 2))) > (scrh-dfkj) && gridHeight % 2 == 0)  // если в итоге он больше а колво в высоту четное
            radius = (scrh-dfkj) / (gridHeight / 2 + gridHeight + (Math.sqrt(3) / 2 / 2)); //выравнивание по высоте для четного
        else if ((radius*( gridHeight + ((gridHeight+1) /2))) > (scrh-dfkj) && gridHeight % 2 != 0) //если больше и кол во нч
            radius = (scrh-dfkj) / ( gridHeight + ((gridHeight+1) /2));  //выравнивание по высоте для нч

        return radius;
    }


}