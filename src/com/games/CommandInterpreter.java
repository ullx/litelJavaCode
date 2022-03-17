package com.games;

import java.util.StringJoiner;

public class CommandInterpreter {

    String word;
    String lang;
    String dicPath;
    Integer wordLength;
    String lettersGrid;

    Integer gridSize;
    Command command;

    public String getWord() {
        return word;
    }

    public String getDicPath() {
        return dicPath;
    }

    public String getLang() {
        return lang;
    }

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

    public static CommandInterpreter interpretCommands(String[] args) {
        if (args == null || args.length < 3) {
            throw new WrongCommandsException("Not enough arguments.");
        }
        return new CommandInterpreter(args);
    }


    private CommandInterpreter (String[] args) {

        this.command = Command.fromString( args[0]);

        int requiredParams = switch (command) {
            case FIND_WORDS -> 4;
            case FIND_PATHS -> 3;
            //TODO: pending
            case RESOLVE_GRID -> -1;
        };

        this.lettersGrid = args[1];
        this.gridSize = Integer.parseInt(args[2]);

        if(Command.FIND_WORDS == command) {
            this.wordLength = Integer.parseInt(args[3]);
            this.lang = args[4];
            if(args.length > 5) {
                this.dicPath = args[5];
            }
        } else if (Command.FIND_PATHS == command) {
            this.word = args[3];
        }

        System.out.println("this.toString() = " + this);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CommandInterpreter.class.getSimpleName() + "[", "]")
                .add("command=" + command.getTxtCmd())
                .add("lang='" + lang + "'")
                .add("gridSize=" + gridSize)
                .add("lettersGrid='" + lettersGrid + "'")
                .add("wordLength='" + wordLength + "'")
                .add("dicPath='" + dicPath + "'")
                .add("word='" + word + "'")
                .toString();
    }

    enum Command {
        RESOLVE_GRID("resolveGrid"), FIND_WORDS("findWords"), FIND_PATHS("findPaths");

        private final String txtCmd;

        Command(String command) {
            this.txtCmd = command;
        }

        public String getTxtCmd() {
            return txtCmd;
        }

        public static Command fromString(String strCmd) {
            for(Command c : Command.values()) {
                if(c.getTxtCmd().equals(strCmd)) {
                    return c;
                }
            }
            throw new IllegalArgumentException("command " + strCmd + " is not valid");
        }



    }

}
