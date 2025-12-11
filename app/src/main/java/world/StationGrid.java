package world;

import station.Station;
import station.StationFactory;
import util.Direction;
import view.GamePanel;

public class StationGrid {

    private final Station[][] stations;

    public StationGrid(GamePanel gp) {
        stations = new Station[gp.maxScreenCol][gp.maxScreenRow];
        initializeStations(gp);
    }

    private void initializeStations(GamePanel gp) {
        for (int col = 0; col < gp.maxScreenCol; col++) {
            for (int row = 0; row < gp.maxScreenRow; row++) {

                int tileId = gp.tileM.mapTileNum[col][row];

                Station s = StationFactory.create(tileId);

                if (s != null) {
                    s.setGridPosition(col, row);
                    stations[col][row] = s;
                }
            }
        }
    }

    public Station getStationAt(int col, int row) {
        if (col < 0 || row < 0 || col >= stations.length || row >= stations[0].length)
            return null;
        return stations[col][row];
    }

    public Station getStationInFrontOf(int col, int row, Direction dir) {
        return switch (dir) {
            case UP    -> getStationAt(col, row - 1);
            case DOWN  -> getStationAt(col, row + 1);
            case LEFT  -> getStationAt(col - 1, row);
            case RIGHT -> getStationAt(col + 1, row);
        };
    }
}
