package gitlet;
import static gitlet.Utils.*;
import java.io.File;

public class myUtils {
    public myUtils(){

    }
    /**
         * create the diractory
     * .gitlet
     *  -object
     *      -XX
     *          -xxxxx..xxx
     */
    public static File makeFile(File dir,String sha1){
        File floder= join(dir,sha1.substring(0,2));
        floder.mkdir();
        File document = join(floder,sha1.substring(2));
        return document;
    }



}
