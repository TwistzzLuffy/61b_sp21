package gitlet;



import java.io.File;
import java.util.*;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */


    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /**
     * store the bob,tree,commit directory
     */
    public static final File OBJECT_DIR = join(GITLET_DIR, "object");

    public static final File HEAD_DIR = join(GITLET_DIR, "heads");

    public static final File BOB_DIR = join(GITLET_DIR, "bob");

    public static final File BRANCH_DIR = join(GITLET_DIR, "branch");

    public static final File STAGE_ADD = join(GITLET_DIR, "StageAdd");
    public static final File STAGE_REMOVE = join(GITLET_DIR, "StageRemove");

    public static final File GIT_SHORT_ID_DIR = join(GITLET_DIR, "short");
    // finish the link between add and commit command
    public static TreeMap<String, String> StageAdd = new TreeMap<String, String>();
    // finish the link between rm and commit command
    public static TreeMap<String, String> StageRemove = new TreeMap<String, String>();

    public static TreeMap<String, String> bobIndex = new TreeMap<String, String>();
    /* TODO: fill in the rest of this class. */
    /** create .gitlet*/


    /**
     * init
     */
    public static void gitInit() {
//        if(GITLET_DIR.exists()){
//            System.out.println("A Gitlet version-control system already exists in the current directory.");
//            System.exit(0);
//        }
        GITLET_DIR.mkdir();
        OBJECT_DIR.mkdir();
        HEAD_DIR.mkdir();
        BOB_DIR.mkdir();
        BRANCH_DIR.mkdir();
        GIT_SHORT_ID_DIR.mkdir();
        File branch = join(BRANCH_DIR, "master");
        File heads = join(HEAD_DIR, "master");
        Commit commit_0 = new Commit();
        commit_0.Save();
        commit_0.shortSave();
        writeObject(heads, commit_0);
        writeObject(branch, commit_0);
    }

    /**
     * add
     */
    public static void gitAdd(String fileName) {
        Blob blob = new Blob(fileName);
        blob.SaveForAdd();
    }

    /**
     * staging added file
     */
    public static void Staging(String fileName, String sha1) {
        File stagFile = join(GITLET_DIR, "StageAdd");
        StageAdd.put(fileName, sha1);
        writeObject(stagFile, StageAdd);
    }

    /**
     * commit
     */
    public static void gitCommit(String message) {
        File stagAddFile = join(GITLET_DIR, "StageAdd");
        File stagRemoveFile = join(GITLET_DIR, "StageRemove");
        StageAdd = readObject(stagAddFile, TreeMap.class);
        if (StageAdd.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        File heads = join(HEAD_DIR, "master");
        // head refer to the previous commit
        Commit head = readObject(heads, Commit.class);
        Commit commit = new Commit(message, head.getsha1());
        if (head.getBobIndex() != null) {
            commit.addPrviousCommit(head.getBobIndex());
        }
        //store commit in OBJECT_DIR
        commit.Save();
        commit.shortSave();
        //
        commit.addStaging(StageAdd);
        //make heads store the new commit
        writeObject(heads, commit);
        //clean the StageAdd and save StagAdd
        StageAdd.clear();
        writeObject(stagAddFile, StageAdd);
        //delete the bob file in stageRemove
        if (stagRemoveFile.exists()) {
            StageRemove = readObject(stagRemoveFile, TreeMap.class);
            if (!StageRemove.isEmpty()) {
                commit.deleteStageRemoveFile(StageRemove);
            }
        }
        StageRemove.clear();
        writeObject(stagRemoveFile, StageRemove);
    }


    public static void gitRm(String filename) {
        Commit head = readObject(join(HEAD_DIR, "master"), Commit.class);
        StageAdd = readObject(join(GITLET_DIR, "StageAdd"), TreeMap.class);
        File stagRemoveFile = join(GITLET_DIR, "StageRemove");
        // if file in stageAdd , unstage it
        if (StageAdd.containsKey(filename)) {
            StageAdd.remove(filename);
        }
        //if file in current commit ,add to stageRemove and delete it in CWD
        TreeMap<String, String> bobIndex = head.getBobIndex();
        if (bobIndex.containsKey(filename)) {
            StageRemove.put(filename, bobIndex.get(filename));
            restrictedDelete(join(CWD, filename));
            writeObject(stagRemoveFile, StageRemove);
        }
    }

    public static void log() {
        File heads = join(HEAD_DIR, "master");
        Commit head = readObject(heads, Commit.class);
        Commit log;
        String parentSha1;
        boolean sign = true;
        head.printfCommit();
        while (sign) {
            parentSha1 = head.getParentIndex();
            if (parentSha1 == null)
                break;
            File parentFile = join(OBJECT_DIR, parentSha1);
            log = readObject(parentFile, Commit.class);
            sign = log.printfCommit();
        }
    }

    public static void globaLog() {
        List<String> file = plainFilenamesIn(OBJECT_DIR);
        Commit globalCommit;
        for (String i : file) {
            globalCommit = readObject(join(OBJECT_DIR, i), Commit.class);
            globalCommit.printfCommit();
        }
    }

    public static void find(String message) {
        List<String> file = plainFilenamesIn(OBJECT_DIR);
        for (String i : file) {
            Commit findCommit = readObject(join(OBJECT_DIR, i), Commit.class);
            if (findCommit.getMessage().equals(message)) {
                System.out.println(findCommit.getsha1());
            }
        }
        System.out.println("Found no commit with that message.");
        System.exit(0);
    }

    public static void status() {
        System.out.println("=== Branches ===");
        List<String> plainBranch = plainFilenamesIn(BRANCH_DIR);
        Collections.sort(plainBranch, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        List<String> plainHead = plainFilenamesIn(HEAD_DIR);
        for (String i : plainBranch) {
            if (plainHead.get(0).equals(i)) {
                System.out.println("*" + plainHead.get(0));
                continue;
            }
            System.out.println(i);
        }
        System.out.println();
        //
        System.out.println("=== Staged Files ===");
        if (STAGE_ADD.exists()) {
            StageAdd = readObject(STAGE_ADD, TreeMap.class);
            for (Map.Entry<String, String> i : StageAdd.entrySet()) {
                System.out.println(i.getKey());
            }
        }
        System.out.println();
        //
        System.out.println("=== Removed Files ===");
        if (STAGE_REMOVE.exists()) {
            StageRemove = readObject(STAGE_REMOVE, TreeMap.class);
            for (Map.Entry<String, String> i : StageRemove.entrySet()) {
                System.out.println(i.getKey());
            }
        }
        System.out.println();
        //deleted
        System.out.println("=== Modifications Not Staged For Commit ===");
        Commit heads = readObject(join(HEAD_DIR, "master"), Commit.class);
        for (Map.Entry<String, String> i : StageAdd.entrySet()) {
            File examinFile = join(CWD, i.getKey());
            if (!examinFile.exists() && !StageRemove.containsKey(i.getKey())) {
                System.out.println(i.getKey() + "(deleted)");
            }
        }

        //modified
        List<String> filesInDir = plainFilenamesIn(CWD);
        for (String cwdFileName : filesInDir) {
            String cwdBob = sha1(readContents(join(CWD, cwdFileName)));
            for (Map.Entry<String, String> i : StageAdd.entrySet()) {
                if (i.getKey().equals(cwdFileName)
                        && !i.getValue().equals(cwdBob)) {
                    System.out.println(cwdFileName + " (modified)");
                }
            }
        }
        System.out.println();
        List<String> bobDir = plainFilenamesIn(BOB_DIR);
        int sign;
        System.out.println("=== Untracked Files ===");
        for (String cwdFileName : filesInDir) {
            String cwdBob = sha1(readContents(join(CWD, cwdFileName)));
            for (String j : bobDir) {
                if (j.equals(cwdBob) || !(StageAdd.containsValue(cwdBob))) {
                    System.out.println(cwdFileName);
                }
            }
        }
        System.out.println();
    }

    public static boolean checkInStage(String fileName) {
        StageAdd = readObject(STAGE_ADD, TreeMap.class);
        for (Map.Entry<String, String> i : StageAdd.entrySet()) {
            if (i.getKey() == fileName)
                return true;
        }
        return false;
    }

    public static void checkoutFile(String fileName) {
        Commit heads = readObject(join(HEAD_DIR, "master"), Commit.class);
        if (!checkInStage(fileName)){
            heads.checkout(fileName);
        }

    }

    public static void checkoutCommitFile(String commitId, String fileName) {
        File shortFile = join(GIT_SHORT_ID_DIR, commitId.substring(0,6));
        if (!shortFile.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit fileCommit = readObject(shortFile, Commit.class);
        if (!checkInStage(fileName)) {
            fileCommit.checkout(fileName);
        }
    }

    public static void checkoutBranch(String branchName){

    }
}



