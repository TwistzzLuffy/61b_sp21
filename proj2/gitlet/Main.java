package gitlet;

import java.io.IOException;

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
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                Repository.gitInit();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                Repository.gitAdd(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit":
                // TODO: handle the `add [filename]` command
                Repository.gitCommit(args[1]);
                break;
            case "rm":
                // TODO: handle the `add [filename]` command
                Repository.gitRm(args[1]);
                break;
            case "log":
                // TODO: handle the `add [filename]` command
                Repository.log();
                break;
            case "global-log":
                // TODO: handle the `add [filename]` command
                Repository.globaLog();
                break;
            case "find":
                // TODO: handle the `add [filename]` command
                Repository.find(args[1]);
                break;
            case "status":
                // TODO: handle the `add [filename]` command
                Repository.status();
                break;
            case "checkout":
                // TODO: handle the `add [filename]` command
                if (args.length == 3 && args[1].equals("--")) {
                    Repository.checkoutFile(args[2]);
                }else if (args.length == 4 && args[2].equals("--")) {
                    Repository.checkoutCommitFile(args[1], args[3]);
                }else if (args.length == 2) {
                    Repository.checkoutBranch(args[1]);
                }else{
                    System.out.println("Incorrect operands.");
                    System.exit(0);
                }
                break;
            case "branch":
                Repository.branch(args[1]);
                break;
            case "rm-branch":
                Repository.rmBranch(args[1]);
                break;
            case "reset":
                Repository.reset(args[1]);
                break;
        }
    }
}
