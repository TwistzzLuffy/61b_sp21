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
        if(GITLET_DIR.exists()){
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            System.exit(0);
        }
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
        File CwdFile = join(CWD,fileName);
        if (CwdFile.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }
        File StagAddFile = join(GITLET_DIR, "StageAdd");
        if (StagAddFile.exists()){
            StageAdd = readObject(StagAddFile,TreeMap.class);
        }
        Blob blob = new Blob(fileName);
        blob.SaveForAdd();
        StageAdd.put(fileName,blob.getBobSha1());
        writeObject(StagAddFile,StageAdd);
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
        //need modified branch name
        File heads = join(HEAD_DIR, "master");
        // head refer to the previous commit
        Commit head = readObject(heads, Commit.class);
        Commit commit = new Commit(message, head.getsha1());
        //
        commit.addStaging(StageAdd);
        if (head.getBobIndex() != null) {
            commit.addPrviousCommit(head.getBobIndex());
        }


        //make heads store the new commit
        writeObject(heads, commit);

        //gain the active branch ,then store the branch
        List<String> plainHead = plainFilenamesIn(HEAD_DIR);
        writeObject(join(BRANCH_DIR,plainHead.get(0)),commit);

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

        //store commit in OBJECT_DIR
        commit.Save();
        commit.shortSave();
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
        boolean sign = true;
        head.printfCommit();
        String parentSha1 = head.getParentIndex();
        while (sign) {
            if (parentSha1 == null)
                break;
            File parentFile = join(OBJECT_DIR, parentSha1);
            Commit log = readObject(parentFile, Commit.class);
            sign = log.printfCommit();
            parentSha1 = log.getParentIndex();
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
        TreeMap<String, String> tracks = heads.getBobIndex();
        System.out.println("=== Untracked Files ===");
        for (String cwdFileName : filesInDir) {
            String cwdBob = sha1(readContents(join(CWD, cwdFileName)));
            if (!StageAdd.containsValue(cwdBob) || tracks.containsKey(cwdFileName)){
                System.out.println(cwdFileName);
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
        File givenBranchFile = join(BRANCH_DIR,branchName);
        if (!givenBranchFile.exists()){
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        Commit currentBranch = readObject(HEAD_DIR,Commit.class);
        List<String> plainHead = plainFilenamesIn(HEAD_DIR);
        if (plainHead.get(0) == branchName){
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit givenBranch = readObject(givenBranchFile,Commit.class);
        TreeMap<String,String> currentBobFile = currentBranch.getBobIndex();
        TreeMap<String,String> givenBobFile = givenBranch.getBobIndex();
        List<String> cwdFileList = plainFilenamesIn(CWD);
        for (String filename : cwdFileList){
            String bobSha1 = sha1(readContents(join(CWD,filename)));
            if (!currentBobFile.containsValue(bobSha1) || StageAdd.containsValue(bobSha1)
            || StageRemove.containsValue(bobSha1)){
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        //put all the file of giveBranch to cwd;
        byte[] fileContent;
        for (Map.Entry<String,String> entry: givenBobFile.entrySet()){
            fileContent = readContents(join(BOB_DIR, entry.getValue()));
            writeContents(join(CWD,entry.getKey()), fileContent);
        }
        //delete file which track in currentBranch ,not in given branch
        for (Map.Entry<String,String> i : currentBobFile.entrySet()){
            for (Map.Entry<String,String> j : givenBobFile.entrySet()){
                if(i.equals(j)){
                   continue;
                }
                restrictedDelete(join(CWD,i.getKey()));
            }
        }
        StageAdd.clear();
        StageRemove.clear();
        //the given commit will be the current branch
        if(restrictedDelete(join(HEAD_DIR,plainHead.get(0)))){
            // head store the given branch
            writeObject(HEAD_DIR,branchName);
        }
    }
}



