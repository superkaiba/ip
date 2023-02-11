package duke.exception;
public class ArgumentBlankException extends Exception {
    public String argumentType;
    public String commandType;
    public ArgumentBlankException(String commandType, String argumentType) {
        this.argumentType = argumentType;
        this.commandType = commandType;
    }
}