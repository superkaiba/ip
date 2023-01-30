import java.util.Scanner;
import java.util.Arrays;
public class Duke {
    private static final String[] INVALID_INPUT_MESSAGE = {
            "Invalid input. Available commands are:",
            "- list",
            "- mark [TASK NUMBER]",
            "- unmark [TASK NUMBER]",
            "- todo [TASK DESCRIPTION]",
            "- event [EVENT DESCRIPTION] /from [START DATE] /to [END DATE]",
            "- deadline [DEADLINE DESCRIPTION] /by [DUE DATE]"
    };
    private static final String INDENT = "      ";
    private static Task[] tasks = new Task[100];
    private static int currentStoredTaskIndex = 0;

    public static void main(String[] args) {
        printIntro();
        mainLoop();
        printExit();

    }

    public static void mainLoop() {
        Scanner in = new Scanner(System.in);
        String currentInput = in.nextLine();
        while (!currentInput.equals("bye")) {
            String[] words = currentInput.split(" ");
            switch (words[0]) {
            case "deadline":
            case "todo":
            case "event":
                handleAddTask(currentInput);
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
                printInvalidInputMessage();
                break;
            }
            currentInput = in.nextLine();
        }
    }

    // COMMAND HANDLERS
    public static void list() {
        printMessage(getFormattedList());
    }

    public static void mark(String[] words) {
        int taskIndex = Integer.parseInt(words[1]) - 1;
        tasks[taskIndex].setDone(true);
        String[] message = {
                "Cool! I've marked this task as done:",
                tasks[taskIndex].toString()
        };
        printMessage(message);
    }

    public static void unmark(String[] words) {
        int taskIndex = Integer.parseInt(words[1]) - 1;
        tasks[taskIndex].setDone(false);
        String[] message = {
                "Ok, I've marked this task as not done yet:",
                tasks[taskIndex].toString()
        };
        printMessage(message);
    }
    public static void handleAddTask(String input) {
        Task addedTask = addTask(input);
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
                "Now you have " + (currentStoredTaskIndex) + " tasks in the list."
        };
        return message;
    }
    public static Task addTask(String input) {
        String[] inputSections = input.split("/");
        String taskType = inputSections[0].split(" ", 2)[0];
        String taskDescription = inputSections[0].split(" ", 2)[1];

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
            printInvalidInputMessage();
            return null;
        }

        tasks[currentStoredTaskIndex] = taskToAdd;
        currentStoredTaskIndex ++;
        return taskToAdd;
    }
    public static Deadline getNewDeadline(String taskDescription, String[] inputSections) {
        return new Deadline(
                taskDescription,
                inputSections[1].replaceFirst("by", "")
        );
    }
    public static Event getNewEvent(String taskDescription, String[] inputSections) {
        return new Event(
                taskDescription,
                inputSections[1].replaceFirst("from", ""),
                inputSections[2].replaceFirst("to", "")
        );
    }
    public static ToDo getNewTodo(String taskDescription) {
        return new ToDo(taskDescription);
    }

    // PRINTING UTILITIES

    public static void printInvalidInputMessage() {
        printMessage(INVALID_INPUT_MESSAGE);
    }

    public static String getFormattedTask(Task task, int number) {
        return number + ". " + task.toString();
    }

    public static String[] getFormattedList() {
        String[] formattedList = new String[currentStoredTaskIndex + 1];
        formattedList[0] = "Here are the tasks in your list:";
        for (int i = 0; i < currentStoredTaskIndex; i++) {
            formattedList[i + 1] = getFormattedTask(tasks[i], i + 1);
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
            printIndent();
            System.out.print(line + "\n");
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
