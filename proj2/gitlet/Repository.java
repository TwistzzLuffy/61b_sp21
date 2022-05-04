package gitlet;

// TODO: any imports you need here
import java.io.File;
import java.util.*;

import static gitlet.Utils.*;
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
    public static TreeMap<String, String> StageAdd = new TreeMap<>();
    // finish the link between rm and commit command
    public static TreeMap<String, String> StageRemove = new TreeMap<>();

    public static TreeMap<String, String> bobIndex = new TreeMap<>();
    /* TODO: fill in the rest of this class. */
    /** create .gitlet*/


    /**
     * init
     */
    public static void gitInit() {
        if (GITLET_DIR.exists()) {
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
        File CwdFile = join(CWD, fileName);
        if (!CwdFile.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (STAGE_ADD.exists()) {
            StageAdd = readObject(STAGE_ADD, TreeMap.class);
        }
        // if add file which exist StageRemoveArea , the file should be remove away from this area.
        if (STAGE_REMOVE.exists()){
            StageRemove = readObject(STAGE_REMOVE,TreeMap.class);
            for (Map.Entry<String,String> entry : StageRemove.entrySet()){
                if (entry.getKey().equals(fileName)){
                    StageRemove.remove(fileName);
                }
            }
            writeObject(STAGE_REMOVE,StageRemove);
        }
        Blob blob = new Blob(fileName);
        File currentCommitFile = join(HEAD_DIR,plainFilenamesIn(HEAD_DIR).get(0));
        Commit heads = readObject(currentCommitFile,Commit.class);
        TreeMap<String,String> trackedBobIndex = heads.getBobIndex();
        if (!(trackedBobIndex.containsValue(blob.getBobSha1()) && trackedBobIndex.containsKey(fileName))){
            blob.SaveForAdd();
            StageAdd.put(fileName, blob.getBobSha1());
            writeObject(STAGE_ADD, StageAdd);
        }
    }

    /**
     * staging added file
     */
    public static void Staging(String fileName, String sha1) {
        File stagFile = join(GITLET_DIR, "StageAdd");
        StageAdd.put(fileName, sha1);
        writeObject(stagFile, StageAdd);
    }

    public static void creatCommit(String message,String secondparent){
        //need modified branch name
        File heads = join(HEAD_DIR, plainFilenamesIn(HEAD_DIR).get(0));
        // head refer to the previous commit
        Commit head = readObject(heads, Commit.class);
        Commit commit = new Commit(message, head.getsha1(),secondparent);
        //
        commit.addStaging(StageAdd);
        if (head.getBobIndex() != null) {
            commit.addPrviousCommit(head.getBobIndex());
        }

        //gain the active branch ,then store the branch
        List<String> plainHead = plainFilenamesIn(HEAD_DIR);
        writeObject(join(BRANCH_DIR, plainHead.get(0)), commit);

        //clean the StageAdd and save StagAdd
        StageAdd.clear();
        writeObject(STAGE_ADD, StageAdd);
        //delete the bob file in stageRemove
        if (STAGE_REMOVE.exists()) {
            StageRemove = readObject(STAGE_REMOVE, TreeMap.class);
            if (!StageRemove.isEmpty()) {
                commit.deleteStageRemoveFile(StageRemove);
            }
        }
        StageRemove.clear();
        writeObject(STAGE_REMOVE, StageRemove);

        //store commit in OBJECT_DIR
        commit.Save();
        commit.shortSave();

        //make heads store the new commit Hint: must be after stage clean
        writeObject(heads, commit);
    }
    /**
     * commit
     */
    public static void gitCommit(String message) {
        if (STAGE_ADD.exists()){
            StageAdd = readObject(STAGE_ADD, TreeMap.class);
        }
        if (STAGE_REMOVE.exists()){
            StageRemove = readObject(STAGE_REMOVE,TreeMap.class);
        }
        if (StageAdd.isEmpty() && StageRemove.isEmpty()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isBlank()) {
            System.out.println("Please enter a commit message.");
            return;
        }
        creatCommit(message,null);
    }


    public static void gitRm(String filename) {
        Commit head = readObject(join(HEAD_DIR, plainFilenamesIn(HEAD_DIR).get(0)), Commit.class);
        TreeMap<String, String> bobIndex = head.getBobIndex();
        if (STAGE_ADD.exists()){
            StageAdd = readObject(STAGE_ADD, TreeMap.class);
        }
        if (!(StageAdd.containsKey(filename) || bobIndex.containsKey(filename))){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
        // if file in stageAdd , unstage it and add to stageRemov
        if (StageAdd.containsKey(filename)) {
//            StageRemove.put(filename,StageAdd.get(filename));
            StageAdd.remove(filename);
            writeObject(STAGE_ADD, StageAdd);
//            writeObject(STAGE_REMOVE,StageRemove);
        }
        //if file in current commit ,add to stageRemove and delete it in CWD
        if (bobIndex.containsKey(filename)) {
            StageRemove.put(filename, bobIndex.get(filename));
            restrictedDelete(join(CWD, filename));
            writeObject(STAGE_REMOVE, StageRemove);
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
        boolean sign = true;
        for (String i : file) {
            Commit findCommit = readObject(join(OBJECT_DIR, i), Commit.class);
            if (findCommit.getMessage().equals(message)) {
                System.out.println(findCommit.getsha1());
                sign = false;
            }
        }
        if (sign){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }

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
            if (!(StageAdd.containsValue(cwdBob) || tracks.containsKey(cwdFileName))) {
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
        if (!checkInStage(fileName)) {
            heads.checkout(fileName);
        }

    }

    public static void checkoutCommitFile(String commitId, String fileName) {
        File shortFile = join(GIT_SHORT_ID_DIR, commitId.substring(0, 6));
        if (!shortFile.exists()) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit fileCommit = readObject(shortFile, Commit.class);
        if (!checkInStage(fileName)) {
            fileCommit.checkout(fileName);
        }
    }

    public static void checkoutBranch(String branchName) {
        File givenBranchFile = join(BRANCH_DIR, branchName);
        if (!givenBranchFile.exists()) {
            System.out.println("No such branch exists.");
            System.exit(0);
        }
        List<String> plainHead = plainFilenamesIn(HEAD_DIR);
        // currentBranch index to head before modified
        Commit currentBranch = readObject(join(HEAD_DIR, plainHead.get(0)), Commit.class);
        if (plainHead.get(0).equals(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit givenBranch = readObject(givenBranchFile, Commit.class);
        TreeMap<String, String> currentBobFile = currentBranch.getBobIndex();
        TreeMap<String, String> givenBobFile = givenBranch.getBobIndex();
        List<String> cwdFileList = plainFilenamesIn(CWD);
        for (String filename : cwdFileList) {
            File cwdfile = join(CWD, filename);
            String bobSha1 = sha1(readContents(cwdfile));
            if (!currentBobFile.containsValue(bobSha1) || StageAdd.containsValue(bobSha1)
                    || StageRemove.containsValue(bobSha1)) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
            if (!givenBobFile.containsKey(filename)){
                restrictedDelete(cwdfile);
            }
        }
        //put all the file of giveBranch to cwd;
        byte[] fileContent;
        for (Map.Entry<String, String> entry : givenBobFile.entrySet()) {
            fileContent = readContents(join(BOB_DIR, entry.getValue()));
            writeContents(join(CWD, entry.getKey()), fileContent);
        }
        //delete file which track in currentBranch ,not in given branch
//        for (Map.Entry<String, String> i : currentBobFile.entrySet()) {
//            for (Map.Entry<String, String> j : givenBobFile.entrySet()) {
//                if (i.equals(j)) {
//                    continue;
//                }
//                restrictedDelete(join(CWD, i.getKey()));
//            }
//        }
        StageAdd.clear();
        StageRemove.clear();
        //the given commit will be the current branch
        join(HEAD_DIR, plainHead.get(0)).delete();
        // head store the given branch
        writeObject(join(HEAD_DIR,branchName), givenBranch);

    }

    public static void branch(String branchName) {
        File branchFile = join(BRANCH_DIR, branchName);
        if (branchFile.exists()) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        File headBranchName = join(HEAD_DIR, plainFilenamesIn(HEAD_DIR).get(0));
        Commit branchCommit = readObject(headBranchName, Commit.class);
        writeObject(branchFile, branchCommit);
    }

    public static void rmBranch(String branchName) {
        File branchNameFile = join(BRANCH_DIR, branchName);
        if (!branchNameFile.exists()) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (branchName.equals(plainFilenamesIn(HEAD_DIR).get(0))) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        branchNameFile.delete();
    }

    public static void reset(String commitId) {
        File resetComitId = join(OBJECT_DIR, commitId);
        if (!resetComitId.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit resetCommit = readObject(resetComitId,Commit.class);
        TreeMap<String, String> resetBobfile = resetCommit.getBobIndex();
        List<String> cwdFile = plainFilenamesIn(CWD);
        File currentBranchName = join(HEAD_DIR, plainFilenamesIn(HEAD_DIR).get(0));
        Commit currentCommit = readObject(currentBranchName, Commit.class);
        TreeMap<String, String> currentBobFile = currentCommit.getBobIndex();
        // delete current dir file which are not exit in given commit
        for (String i : cwdFile) {
            File mycwdFile = join(CWD, i);
            String bobSha1 = sha1(readContents(mycwdFile));
            if (!resetBobfile.containsValue(bobSha1) || StageAdd.containsValue(bobSha1)
                    || StageRemove.containsValue(bobSha1)) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
            if(!currentBobFile.containsKey(i)){
                restrictedDelete(mycwdFile);
            }
            //copy or overwrite given commit file to CWD
            byte[] fileContent;
            for (Map.Entry<String, String> entry : resetBobfile.entrySet()) {
                fileContent = readContents(join(BOB_DIR, entry.getValue()));
                writeContents(join(CWD, entry.getKey()), fileContent);
            }

            StageAdd.clear();
            writeObject(STAGE_ADD,StageAdd);
            // store this branch to head
            join(HEAD_DIR, plainFilenamesIn(HEAD_DIR).get(0)).delete();
            writeObject(currentBranchName, resetCommit);
        }
    }

    public static void merge(String branchName){
        String currBranchName = plainFilenamesIn(HEAD_DIR).get(0);
        List<String> branchNames = plainFilenamesIn(BRANCH_DIR);
        File headName = join(HEAD_DIR,plainFilenamesIn(HEAD_DIR).get(0));
        Commit currentCommit = readObject(headName,Commit.class);
        Commit givenCommit = readObject(join(BRANCH_DIR,branchName),Commit.class);
        Commit splitCommit = getSplitCommit(currentCommit,givenCommit);
        StageAdd = readObject(STAGE_ADD,TreeMap.class);
        StageRemove = readObject(STAGE_REMOVE,TreeMap.class);
        if (!StageAdd.isEmpty() || !StageRemove.isEmpty()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!branchNames.contains(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (currBranchName.equals(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        ////
        if (splitCommit == null) {
            System.out.println("Does not find split point.");
            System.exit(0);
        }
        if (splitCommit.equals(givenCommit)){
            System.out.println(" Given branch is an ancestor of the current branch.");
            System.exit(0);
        }
        if (splitCommit.equals(currentCommit)){
            checkoutBranch(branchName);
            System.out.println("Current branch fast-forwarded.");
            System.exit(0);
        }

        TreeMap<String,String> currentTrack = currentCommit.getBobIndex();
        TreeMap<String,String> givenTrack = givenCommit.getBobIndex();
        TreeMap<String,String> splitTrack = splitCommit.getBobIndex();

        Set<String> currentSet = currentTrack.keySet();
        Set<String> givenSet = givenTrack.keySet();
        Set<String> splitSet = splitTrack.keySet();
        Set<String> tempSet = currentSet;
        //not in Split nor HEAD but in Other -> Other (Hint : file
        // can't be modified in currentBranch,because don't exit this file )
        tempSet = givenSet;
        givenSet.removeAll(splitSet);
        givenSet.removeAll(currentSet);
        //case 4,5
        for (String file1 : givenSet){
            StageAdd.put(file1,givenTrack.get(file1));
            writeContents(join(CWD,file1),
                    readContentsAsString(join(BOB_DIR,givenTrack.get(file1))));
        }
        givenSet = tempSet;
        //file in the 3 sets
        tempSet = currentSet;
        currentSet.retainAll(splitSet);
        currentSet.retainAll(givenSet);
        for (String file2 : currentSet){
            boolean modSignCurrent = false;
            boolean modSignGiven = false;
            boolean conflit = false;
            if (!currentTrack.get(file2).equals(splitTrack.get(file2))){
                modSignCurrent = true;
            }
            if (!givenTrack.get(file2).equals(splitTrack.get(file2))) {
                modSignGiven = true;
            }
            // file in currentBranch and givenBranch are modifide by different way
            if (modSignCurrent && modSignGiven &&
                    !givenTrack.get(file2).equals(currentTrack.get(file2))){
                conflit = true;
                String content = "<<<<<<< HEAD\n";
                content += currentTrack.get(file2)== null ? ""
                        : readContentsAsString(join(BOB_DIR,currentTrack.get(file2)));
                content += "=======\n";
                content += givenTrack.get(file2)== null ? ""
                        : readContentsAsString(join(BOB_DIR,givenTrack.get(file2)));
                content += ">>>>>>>\n";
                byte[] conflictContentsByte = content.getBytes();
                String newBlobId = sha1(conflictContentsByte);
                StageAdd.put(file2,newBlobId);
                //store the new confilcted file in bobDir
                writeContents(join(BOB_DIR,newBlobId),content);
                //renew the cwd file
                writeContents(join(CWD,file2),content);
            }else if (modSignGiven && !modSignCurrent){
                //case 1
                StageAdd.put(file2,givenTrack.get(file2));
                writeContents(join(CWD,file2),
                        readContentsAsString(join(BOB_DIR,givenTrack.get(file2))));
            }
            if (conflit){
                System.out.println("Encountered a merge conflict.");
            }
        }
        tempSet.retainAll(splitSet);
        tempSet.removeAll(givenSet);
        for (String file3 : tempSet){
            boolean modSignCurrent = false;
            if (!currentTrack.get(file3).equals(splitTrack.get(file3))){
                modSignCurrent = true;
            }
            if (!modSignCurrent){
                StageRemove.put(file3,currentTrack.get(file3));
                restrictedDelete(join(CWD,file3));
            }
        }

        String mergelog = "Merged"+branchName+"into"+headName+".";
        writeObject(STAGE_REMOVE,StageRemove);
        writeObject(STAGE_ADD,StageAdd);
        creatCommit(mergelog,givenCommit.getsha1());
    }

    public static Commit getSplitCommit(Commit currentCommit,Commit givenCommit){
        List<String> currentPath = new ArrayList<>();
        Set<String> givenPath = new HashSet<>();

        Queue<Commit> queCommit = new LinkedList<>();
        //add current commit path
        queCommit.add(currentCommit);
        while(!queCommit.isEmpty()){
            Commit presentCommit = queCommit.poll();
            currentPath.add(presentCommit.getsha1());
            if (presentCommit.getParentIndex() != null){
                Commit parent = readObject(join(OBJECT_DIR,currentCommit.getParentIndex()),Commit.class);
                queCommit.add(parent);
            }
            if (presentCommit.getSecondParentIndex() != null){
                Commit secondparent = readObject(join(OBJECT_DIR,currentCommit.getSecondParentIndex()),Commit.class);
                queCommit.add(secondparent);
            }
        }
        //add given commmit path
        queCommit.add(givenCommit);
        while(!queCommit.isEmpty()){
            Commit presentCommit = queCommit.poll();
            givenPath.add(presentCommit.getsha1());
            if (!presentCommit.getParentIndex().equals(null)){
                Commit parent = readObject(join(OBJECT_DIR,currentCommit.getParentIndex()),
                        Commit.class);
                queCommit.add(parent);
            }
            if (!presentCommit.getSecondParentIndex().equals(null)){
                Commit secondparent = readObject(join(OBJECT_DIR,currentCommit.getSecondParentIndex()),
                        Commit.class);
                queCommit.add(secondparent);
            }
        }

        // find spitCommit
        for (String id : currentPath){
            if (givenPath.contains(id)){
                return readObject(join(OBJECT_DIR,id),Commit.class);
            }
        }
        return null;
    }
}



