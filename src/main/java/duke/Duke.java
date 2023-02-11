package duke;
import duke.addable.*;
import duke.exception.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.ArrayList;
public class Duke {
    private static final String[] INVALID_INPUT_MESSAGE = {
            "Invalid input format",
            "Please see below for a list of valid commands",
            "- list",
            "- mark [TASK NUMBER]",
            "- unmark [TASK NUMBER]",
            "- todo [TASK DESCRIPTION]",
            "- event [EVENT DESCRIPTION] /from [START DATE] /to [END DATE]",
            "- deadline [DEADLINE DESCRIPTION] /by [DUE DATE]",
            "- delete [TASK NUMBER]"
    };
    private static final String INDENT = "      ";
    private static ArrayList<Task> tasks = new ArrayList<>();
    private static FileManager fm;

    public static void main(String[] args) {
        fm = new FileManager();
        tasks = fm.getTasks();
        printIntro();
        printMessage("Loading tasks from hard disk...");
        list();
        mainLoop();
        printExit();
    }

    public static void mainLoop() {
        Scanner in = new Scanner(System.in);
        String currentInput = in.nextLine();
        while (!currentInput.equals("bye")) {
            String[] words = currentInput.split(" ");
            try {
                switch (words[0]) {
                case "deadline":
                case "todo":
                case "event":
                    handleAddTask(currentInput);
                    break;
                case "delete":
                    delete(words);
                    break;
                case "list":
                    list();
                    break;
                case "mark":
                    mark(words);
                    break;
                case "unmark":
                    unmark(words);
                    break;
                default:
                    throw new UnknownCommandException(words[0]);
                }
                fm.saveCurrentTaskList();
            } catch (MarkNonexistentTaskException e) {
                printInvalidInputMessage("task " + e.taskIndex + " does not currently exist.");
            } catch (ArrayIndexOutOfBoundsException e) {
                printInvalidInputMessage("Unknown command");
            } catch (UnknownCommandException e) {
                printInvalidInputMessage("Unknown command \'" + e.unknownCommand + "\'");
            } catch (NumberFormatException e) {
                printInvalidInputMessage("Argument for mark/unmark/delete must be an integer");
            }
            currentInput = in.nextLine();
        }
    }
    // COMMAND HANDLERS

    public static void delete(String[] words) throws MarkNonexistentTaskException {
        int taskIndex = getTaskIndex(words[1]);
        String[] taskDeletedMessage = {
                "Noted. I've removed this task:",
                tasks.get(taskIndex).toString(),
                "Now you have " + (tasks.size() - 1) + " tasks in the list."
        };
        tasks.remove(taskIndex );
        printMessage(taskDeletedMessage);
    }
    public static void list() {
        printMessage(getFormattedList());
    }

    public static int getTaskIndex(String index) throws MarkNonexistentTaskException {
        int taskIndex = Integer.parseInt(index) - 1;
        if (taskIndex > tasks.size() - 1) {
            throw new MarkNonexistentTaskException(taskIndex + 1);
        }
        return taskIndex;
    }
    public static void mark(String[] words) throws MarkNonexistentTaskException {
        int taskIndex = getTaskIndex(words[1]);
        tasks.get(taskIndex).setDone(true);
        String[] message = {
                "Cool! I've marked this task as done:",
                tasks.get(taskIndex).toString()
        };
        printMessage(message);
    }

    public static void unmark(String[] words) throws MarkNonexistentTaskException {
        int taskIndex = getTaskIndex(words[1]);
        tasks.get(taskIndex).setDone(false);
        String[] message = {
                "Ok, I've marked this task as not done yet:",
                tasks.get(taskIndex).toString()
        };
        printMessage(message);
    }
    public static void handleAddTask(String input) {
        Task addedTask = null;
        try {
            addedTask = addTask(input);
        } catch (ArrayIndexOutOfBoundsException e) {
            printInvalidInputMessage();
        } catch (ArgumentBlankException e) {
            printInvalidInputMessage(
                    "Argument \'" + e.argumentType + "\' cannot be blank for command \'" +
                            e.commandType + "\'"
                    );
        } catch (UnknownCommandException e) {
            printInvalidInputMessage("Unknown command \'" + e.unknownCommand + "\'");
        }
        if (addedTask == null) {
            return;
        }
        String[] message = getAddTaskMessage(addedTask);
        printMessage(message);
    }
    public static String[] getAddTaskMessage(Task addedTask) {
        String[] message = {
                "Got it. I've added this task:",
                INDENT + addedTask.toString(),
                "Now you have " + (tasks.size()) + " tasks in the list."
        };
        return message;
    }
    public static Task addTask(String input) throws ArgumentBlankException, UnknownCommandException {
        String[] inputSections = input.split("/");
        String[] firstSectionArguments = inputSections[0].split(" ", 2);
        String taskType = firstSectionArguments[0];
        if (firstSectionArguments.length < 2) {
            throw new ArgumentBlankException(taskType, "description");
        }
        String taskDescription = firstSectionArguments[1];

        Task taskToAdd;

        switch (taskType) {
        case "deadline":
            taskToAdd = getNewDeadline(taskDescription, inputSections);
            break;
        case "event":
            taskToAdd = getNewEvent(taskDescription, inputSections);
            break;
        case "todo":
            taskToAdd = getNewTodo(taskDescription);
            break;
        default:
            throw new UnknownCommandException(taskType);
        }
        tasks.add(taskToAdd);
        return taskToAdd;
    }
    public static Deadline getNewDeadline(String taskDescription, String[] inputSections) throws ArgumentBlankException {
        String date = inputSections[1].replaceFirst("by", "");
        return new Deadline(
                taskDescription,
                date,
                false
        );
    }
    public static Event getNewEvent(String taskDescription, String[] inputSections) throws ArgumentBlankException {
        return new Event(
                taskDescription,
                inputSections[1].replaceFirst("from", ""),
                inputSections[2].replaceFirst("to", ""),
                false
        );
    }
    public static ToDo getNewTodo(String taskDescription) throws ArgumentBlankException {
        return new ToDo(taskDescription, false);
    }

    // PRINTING UTILITIES

    public static void printInvalidInputMessage() {
        printMessage(INVALID_INPUT_MESSAGE);
    }
    public static void printInvalidInputMessage(String extraClarification) {
        String[] message = Arrays.copyOf(INVALID_INPUT_MESSAGE, INVALID_INPUT_MESSAGE.length);
        message[0] = extraClarification;
        printMessage(message);
    }
    public static String getFormattedTask(Task task, int number) {
        return number + ". " + task.toString();
    }

    public static String[] getFormattedList() {
        String[] formattedList = new String[tasks.size() + 1];
        formattedList[0] = "Here are the tasks in your list:";
        for (int i = 0; i < tasks.size(); i++) {
            formattedList[i + 1] = getFormattedTask(tasks.get(i), i + 1);
        }
        return formattedList;
    }

    public static void printIntro() {
        String[] intro = {"Hello! I'm Tom", "What can I do for you?"};
        printMessage(intro);
    }

    public static void printExit() {
        printMessage("Bye. Hope to see you again soon!");
    }

    public static void printMessage(String message) {
        printSeparator();
        printIndent();
        System.out.print(message + "\n");
        printSeparator();
    }

    public static void printMessage(String[] message) {
        printSeparator();
        for (String line : message) {
            if (!line.equals("")) {
                printIndent();
                System.out.print(line + "\n");
            }
        }
        printSeparator();
    }

    public static void printIndent() {
        System.out.print(INDENT);
    }

    public static void printSeparator() {
        System.out.print("   ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
    }
}