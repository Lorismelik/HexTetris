package draw;

import android.graphics.Path;
import android.graphics.Paint;
import android.graphics.Canvas;
import android.graphics.Paint.Style;
import android.graphics.Color;

import java.util.List;
import java.util.Set;

import com.example.masha.tetris.Controller;
import com.example.masha.tetris.R;

import api.AxialCoordinate;
import api.exception.HexagonalGridCreationException;
import api.Hexagon;
import api.HexagonOrientation;
import api.HexagonalGrid;
import api.HexagonalGridBuilder;
import api.HexagonalGridCalculator;
import api.HexagonalGridLayout;
import api.Point;

import static api.HexagonOrientation.POINTY_TOP;
import static api.HexagonalGridLayout.RECTANGULAR;
import static com.example.masha.tetris.Settings.width;
import static com.example.masha.tetris.Settings.height;
import static com.example.masha.tetris.Main.scrw;
import static com.example.masha.tetris.Main.scrh;


public class DrawGrid {

    private static final HexagonOrientation DEFAULT_ORIENTATION = POINTY_TOP;
    private static final HexagonalGridLayout DEFAULT_GRID_LAYOUT = RECTANGULAR;
    public HexagonalGrid hexagonalGrid;
    private HexagonalGridCalculator hexagonalGridCalculator;
    private Controller controller;
    private HexagonOrientation orientation = DEFAULT_ORIENTATION;
    private HexagonalGridLayout hexagonGridLayout = DEFAULT_GRID_LAYOUT;
    private int gridWidth = width , gridHeight = height;
    NormalPack pack;
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
            controller = new Controller(builder.getCustomStorage(),hexagonalGrid.getLockedHexagons());
        } catch (HexagonalGridCreationException e) {}
        pack = new NormalPack(hexagonalGrid);
    }


    public void useBuilder(Canvas canvas, String movement) {

        switch (movement) {
            case "START":
                pack.getFigure(gridWidth);
                break;

            case "COUNTER_CLCK":
                controller.rotationCounterClockwise();
                break;

            case "CLCK":
                controller.rotationClockwise();
                break;

            case "DOWN_RIGHT":
                controller.moveDownRight(hexagonalGrid);
                break;

            case "DOWN_LEFT":
                controller.moveDownLeft(hexagonalGrid);
                break;

            case "RIGHT":
                controller.moveRight(hexagonalGrid);
                break;

            case "LEFT":
                controller.moveLeft(hexagonalGrid);
                break;
        }
        if (hexagonalGrid.getHexagonStorage().isEmpty())  // если фигура залочилась, достаем следующую
        {
            pack.getFigure(gridWidth);
        }


        canvas.drawRGB(11, 25, 25);
        for (Hexagon hexagon : hexagonalGrid.getHexagons()) {//сетка
            int[] array = new int[12];
            drawPoly(canvas, convertToPointsArr(hexagon.getPoints(), array), 250, 175 ,  6, Style.STROKE);
        }
        for (AxialCoordinate axialCoordinate : hexagonalGrid.getHexagonStorage()) {
            int[] array = new int[12];
            drawPoly(canvas, convertToPointsArr(hexagonalGrid.getByAxialCoordinate(axialCoordinate).get().getPoints(), array), 250, 175, 6, Style.FILL); //фигруа
        }
        Set <AxialCoordinate> coordinates = hexagonalGrid.getLockedHexagons().keySet();
        for (AxialCoordinate coordinate: coordinates)
        {
            int[] array = new int[12];
            drawPoly(canvas, convertToPointsArr(hexagonalGrid.getByAxialCoordinate(coordinate).get().getPoints(), array),  233,219,  193, Style.FILL_AND_STROKE);
        }

    }



    private void drawPoly(Canvas canvas, int[] array, int color, int color2 , int color3,  Style style) {

        if (array.length < 12)
            return;

        Paint p = new Paint();
        p.setColor(Color.rgb(color, color2, color3));
        p.setStyle(style);
        if (gridWidth > 15) {
            p.setStrokeWidth(2);
        }else if (gridWidth>30)p.setStrokeWidth(1); else p.setStrokeWidth(5);

        Path polyPath = new Path();
        polyPath.moveTo(array[0], array[1]); //первая точка

        for (int i = 0; i < 12;  i=i+2 )
            polyPath.lineTo(array[i], array[i+1]);

        polyPath.lineTo(array[0], array[1]);
        canvas.drawPath(polyPath, p);
        p.setStrokeWidth(1);
        p.setStyle(Style.FILL_AND_STROKE);
        p.setTextSize(40);
        canvas.drawText("score: "  /** + point*/, 30 , (float)scrh-15, p);

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