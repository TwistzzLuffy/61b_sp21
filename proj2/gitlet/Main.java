package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO Chenghao Sun 4.12 2020.
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        String firstArg = args[0];
        Repository.setupPersistence();
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.gitInit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                break;
            // TODO: FILL THE REST IN
        }
    }
}
