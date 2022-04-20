package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

    public static TreeMap<String,String> Staging = new TreeMap<String,String>();
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
        blob.Save();
    }

    /**
     * staging added file
     *
     */
    public static void Staging(String fileName,String sha1){
        File stagFile = join(Object_DIR,"stage");
        Staging.put(fileName,sha1);
        writeObject(stagFile,Staging);
    }

    /**
     * commit
     */
    public static void gitCommit(String message){
        File stagFile = join(Object_DIR,"stage");
        Staging = readObject(stagFile,TreeMap.class);
        if (Staging.isEmpty()){
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
        //
        commit.addStaging(Staging);
        //make heads store the new commit
        writeObject(heads,commit);
    }
}
