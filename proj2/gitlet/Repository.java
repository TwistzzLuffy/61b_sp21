package gitlet;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

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


    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** store the bob,tree,commit directory*/
    public static final File Object_DIR = join(GITLET_DIR,"object");

    public static final File Head_DIR = join(GITLET_DIR,"heads");

    public static final File bob_DIR = join(GITLET_DIR,"bob");
    // finish the link between add and commit command
    public static TreeMap<String,String> StageAdd = new TreeMap<String,String>();
    // finish the link between rm and commit command
    public static TreeMap<String,String> StageRemove = new TreeMap<String,String>();
    /* TODO: fill in the rest of this class. */
    /** create .gitlet*/


    /**
     * init
     */
    public static void gitInit(){
//        if(GITLET_DIR.exists()){
//            System.out.println("A Gitlet version-control system already exists in the current directory.");
//            System.exit(0);
//        }
        GITLET_DIR.mkdir();
        Object_DIR.mkdir();
        Head_DIR.mkdir();
        bob_DIR.mkdir();
        File heads = join(Head_DIR,"master");
        Commit commit_0 = new Commit();
        commit_0.Save();
        writeObject(heads,commit_0);
    }

    /**
     * add
     */
    public static void gitAdd(String fileName){
        Blob blob = new Blob(fileName);
        blob.SaveForAdd();
    }

    /**
     * staging added file
     *
     */
    public static void Staging(String fileName,String sha1){
        File stagFile = join(Object_DIR,"StageAdd");
        StageAdd.put(fileName,sha1);
        writeObject(stagFile, StageAdd);
    }

    /**
     * commit
     */
    public static void gitCommit(String message){
        File stagAddFile = join(Object_DIR,"StageAdd");
        File stagRemoveFile = join(Object_DIR,"StageRemove");
        StageAdd = readObject(stagAddFile,TreeMap.class);
        if (StageAdd.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }if (message.isBlank()){
            System.out.println("Please enter a commit message.");
            return;
        }
        File heads = join(Head_DIR,"master");
        // head refer to the previous commit
        Commit head = readObject(heads,Commit.class);
        Commit commit = new Commit(message,head.sha1());
        if(head.getBobIndex() != null){
            commit.addPrviousCommit(head.getBobIndex());
        }
        //store commit in object_DIR
        commit.Save();
        //
        commit.addStaging(StageAdd);
        //make heads store the new commit
        writeObject(heads,commit);
        //clean the StageAdd and save StagAdd
        StageAdd.clear();
        writeObject(stagAddFile,StageAdd);
        //delete the bob file in stageRemove
        if (stagRemoveFile.exists()) {
            StageRemove = readObject(stagRemoveFile,TreeMap.class);
            if (!StageRemove.isEmpty()){
                commit.deleteStageRemoveFile(StageRemove);
            }
        }
        StageRemove.clear();
        writeObject(stagRemoveFile,StageRemove);
    }


    public static void gitRm(String filename){
        Commit head = readObject(join(Head_DIR,"master"),Commit.class);
        StageAdd = readObject(join(Object_DIR,"StageAdd"),TreeMap.class);
        File stagRemoveFile = join(Object_DIR,"StageRemove");
        // if file in stageAdd , unstage it
        if (StageAdd.containsKey(filename)){
            StageAdd.remove(filename);
        }
        //if file in current commit ,add to stageRemove and delete it in CWD
        TreeMap<String,String> bobIndex = head.getBobIndex();
        if (bobIndex.containsKey(filename)){
            StageRemove.put(filename,bobIndex.get(filename));
            restrictedDelete(join(CWD,filename));
            writeObject(stagRemoveFile,StageRemove);
        }
    }

    public static void log(){
        File heads = join(Head_DIR,"master");
        Commit head = readObject(heads,Commit.class);
        Commit log;
        String parentSha1 ;
        boolean sign = true;
        head.printfCommit();
        while(sign){
            parentSha1 = head.getParentIndex();
            File parentFile = join(GITLET_DIR,parentSha1);
            log = readObject(parentFile,Commit.class);
            sign= log.printfCommit();
        }



    }
}
