package com.games;

public class CommandInterpreter {

    LANG lang;
    Integer gridSize;
    String lettersGrid;
    Integer wordLength;
    Command command;

    public Command getCommand() {
        return command;
    }

    public Integer getGridSize() {
        return gridSize;
    }


    public Integer getWordLength() {
        return wordLength;
    }

    public String getLettersGrid() {
        return lettersGrid;
    }

    public static CommandInterpreter interpretCommands(String[] args){

        int cIdx = 0;
        if (args == null || args.length < 3) {
            throw new WrongCommandsException("Not enough arguments.");
        }

        String command = args[cIdx++];
        String lettersGrid = args[cIdx++];
        int gridSize = Integer.parseInt(args[cIdx++]);
        System.out.println("lettersGrid " + lettersGrid);
        System.out.println("gridSize " + gridSize);

        return new CommandInterpreter(args);
    }


    private CommandInterpreter (String[] args){

    }

    private enum Command {
        resolveGrid, findWords, findPaths
    }

}
