package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
    public static final File Object_DIR = join(CWD,".gitlet/object");

    /**
     * create staging area
     */
    public static final File Staging_DIR = join(Object_DIR,"staging");
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
        Staging_DIR.mkdir();
        Commit commit_0 = new Commit();
        commit_0.Save();
    }

    /**
     * add
     */
    public static void gitAdd(String fileName){
        Blob blob = new Blob(fileName);
        blob.Save();
    }
}
