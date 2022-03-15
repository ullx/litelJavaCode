package com.games;

import java.util.ArrayList;

public class Grid {

    private String[][] GRID = null;
    private String[][] gridBackup = null;

    public static Grid createGrid(String letters, int size) {
        return new Grid(letters, size);
    }

    public void removeCordinatesFromGRID(ArrayList<Coordinate> toMark) {
        for (Coordinate c : toMark) {
            GRID[c.getX_POSITION()][c.getY_POSITON()] = "-";
        }
        setDownCoords();
    }

    private void setDownCoords() {
        for (int i = 0; i < GRID.length; i++) {
            for(int j = 0; j < GRID.length; j++) {
                Coordinate co = new Coordinate(i, j);
                setDownCoords(co);
            }
        }
    }

    private void setDownCoords(Coordinate baseCoord) {
//		System.out.println("setDownCoords " + baseCoord + " " + baseCoord.getX_POSITION() + " " + GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()]);
        Coordinate above = getAboveNeighbour(baseCoord);
        if(above != null) {
            String charToMoveDown = GRID[above.getX_POSITION()][above.getY_POSITON()];
            String currentChar = GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()];

            if(currentChar.equals("-")) {
                GRID[baseCoord.getX_POSITION()][baseCoord.getY_POSITON()] = charToMoveDown;
                GRID[above.getX_POSITION()][above.getY_POSITON()] = "-";
            }
            setDownCoords(above);
        }
    }

    public Coordinate getAboveNeighbour(Coordinate coor) {
        int baseX = coor.getX_POSITION();
        int baseY = coor.getY_POSITON();
        Coordinate aboveCoord = null;
        for(int i = 0; i < GRID.length && aboveCoord == null; i++) {
            for (int j = 0; j < GRID.length && aboveCoord == null; j++) {
                try {
                    if(i == baseX - 1 && j == baseY) {
                        aboveCoord =  new Coordinate(i,j);
                    }
                } catch (IndexOutOfBoundsException e) {
//					e.printStackTrace();
                }
            }
        }
        return aboveCoord;
    }

    public static String[][] cloneGrid(Grid originalGrid) {
        String[][] original = originalGrid.getGrid();
        String[][] backup = new String[original.length][original.length];
        for(int i = 0; i < original.length; i++) {
            for(int j = 0; j < original.length; j++) {
                backup[i][j] = new String(original[i][j]);
            }
        }
        return backup;
    }

    public ArrayList<Coordinate> getAdjacentIndexesByLetter(int x, int y, String charToFind) {
        ArrayList<Coordinate> cords = new ArrayList<Coordinate>();

        for (int dx = -1; dx <= 1; ++dx) {
            for (int dy = -1; dy <= 1; ++dy) {
                try {
                    if (dx != 0 || dy != 0) {
                        String ch = GRID[x + dx][y + dy];
                        Integer[] newPosition = new Integer[] { x + dx, y + dy };
                        Coordinate coordinate = new Coordinate(newPosition);
                        if (ch.equals(charToFind) && !usedCoordinates.contains(coordinate)) {
                            cords.add(coordinate);
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                    // System.out.println("Out of bounds");
                }
            }
        }
        return cords;
    }

    private Grid (String letters, int size) {
        String[][] grid = new String[size][size];
        int charIdx = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                String value  =  letters.charAt(charIdx++) + "";
                grid[i][j] = value;
            }
        }
        return new;
    }

    public static void printGridWithCoords(Grid grid) {
        printGrid(grid, true, null);
    }

    public static void printGridWithoutCoords(Grid grid) {
        printGrid(grid,false, null);
    }

    public static void printAndMarkCoordsInGrid( ArrayList<Coordinate> toRemove, Grid grid) {
        printGrid(grid, true, toRemove);
    }

    public String[][] getGrid() {
        return this.GRID;
    }

    public static void printGrid(Grid grid, boolean withCoords, ArrayList<Coordinate> markCoords) {
        StringBuilder sb = new StringBuilder();
        String[][] ggrid = grid.getGrid();
        for (int i = 0; i < ggrid.length; i++) {
            for (int j = 0; j < ggrid.length; j++) {
                if(markCoords != null && lookCoordinate(markCoords, new Coordinate(i,j))) {
                    sb.append("*");
                }
                if(withCoords) {
                    sb.append(ggrid[i][j]).append("[").append(i).append(",").append(j).append("]");
                } else {
                    sb.append(ggrid[i][j]);
                }
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    public static void printFlatGrid(Grid ggrid) {
        String[][] grid = ggrid.getGrid();
        StringBuilder sb = new StringBuilder();
        for(int i =0; i < grid.length; i ++) {
            for(int j =0; j < grid.length; j ++) {
                sb.append(grid[i][j]);
            }
        }
        System.out.println(sb);
    }

    public ArrayList<Coordinate> getIndexesByLetter(Character ch) {
//		printLog("Getting indexes by letter " + ch);
        ArrayList<Coordinate> indexes = new ArrayList<Coordinate>();
        for (int idx = 0; idx < GRID.length; idx++) {
            for (int idy = 0; idy < GRID[idx].length; idy++) {
                if (GRID[idx][idy].equals(ch + "")) {
                    indexes.add(new Coordinate(idx, idy));
                }
            }
        }
        return indexes;
    }

    public ArrayList<String> getIndexesByLetter(Character[][] charGrid, Character ch) {

        ArrayList<String> indexes = new ArrayList<String>();
        for (int i = 0; i < charGrid.length; i++) {
            for (int y = 0; y < charGrid[i].length; y++) {
                if(charGrid[i][y].equals(ch)) {
                    indexes.add(i + "," + y);
                }
            }
        }
        return indexes;
    }

}
