package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;
import static gitlet.Repository.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author Huang Jinhong
 */
public class Commit implements Serializable {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;//存提交的话的
    /** The date of committing. */
    private Date date;//存日期
    /** The ids(hash codes) of blobs in this Commit. */
    private List<String> blobIDs;//存当前所指向的blobID合集
    /** The ids(hash codes) of parent Commits, BUT at most two parents*/
    private List<String> parentIDs;//存上个版本的指向合集
    /** The id(hash code) of this Commit. */
    private String CommitID;//作为commit自己的版本号码
    /** The name of copied files in blobs. */
    private List<String> copiedFileNames;//指向文件名字
    /** The id(hash code)  of copied files in blobs. */
    private List<String> copiedFileIDs;//和他们的唯一ID ,可能会出现增加或者减少的可能
    /** the marked count in commit */
    private int markedCount;//应该是remote里面的东西
    //我先做好本地的吧
    /** the distance in commit */
    private int distance;

    public Commit(String message, Date date, List<String> parentIDs) {//初始化,没啥看的
        this.message = message;
        this.date = date;
        this.parentIDs = parentIDs;//这个是直接赋值了,给的指针应该是
        this.copiedFileNames = new LinkedList<>();
        this.blobIDs = new LinkedList<>();
        this.copiedFileIDs = new LinkedList<>();
        // get information(copied fileNames, copied fileIDs and blobIDs) from parent
        getInfoFromParent();
        // get information(copied fileNames, copied fileIDs and blobIDs) from staging folder
        getInfoFromStaging();
        this.markedCount = 0;
        this.distance = 0;
        this.CommitID = sha1(serialize(this));
    }

    /** using in constructor of Commit */
    // make blob
    public Blob makeBlob(File stagingFile) {//用文件(在缓存区里面的,重新创立一个blobs并且加入到Blobs文件夹中)
        Blob blob = new Blob(stagingFile);
        String blobID = blob.getBlobID();
        saveDirAndObjInBlobs(blob, BLOB_FOLDER, blobID);
        return blob;
    }

    // get information from parent
    // only consider one parent ==> get(0)
    private void getInfoFromParent() {//只考虑有一个父节点的情况
        // 22.9.28, only consider one parent ==> get(0)
        // get copied fileNames, copied fileIDs and blobIDs by parent
        if (this.parentIDs.size() == 1) {//从commit合集中找到那个ID
            Commit parentCommit = readObject(join(COMMITS_FOLDER, this.parentIDs.get(0)), Commit.class);
            this.copiedFileNames.addAll(parentCommit.getCopiedFileNames());//将父节点中的情况全部加进去,下面都是一样的
            this.blobIDs.addAll(parentCommit.getBlobIDs());
            this.copiedFileIDs.addAll(parentCommit.getCopiedFileIDs());
        }
    }

    // get information from staging folder and removed folder
    private void getInfoFromStaging() {//从当前缓存区里面读取
        //先是从增加的里面读取
        for (String fileName : plainFilenamesIn(ADDITION_FOLDER)) {
            File file = join(ADDITION_FOLDER, fileName);
            String fileID = getFileID(file);
            //如果这个在父版本中没有出现,那么就加入一个全新的玩意
            if (!this.copiedFileNames.contains(fileName) && !this.copiedFileIDs.contains(fileID)) {
                this.copiedFileNames.add(fileName);
                this.copiedFileIDs.add(fileID);
                this.blobIDs.add(makeBlob(file).getBlobID());
                //如果在父版本中出现了,但是内容不一样,就需要建立一个新的blobs,并在
                //注意:是在该版本中移除掉上一个版本的blobs,然后加入一个新的blobs
            }
            else if (this.copiedFileNames.contains(fileName) && !this.copiedFileIDs.contains(fileID)) {
                // deleted file(blobID, copiedFileID) from parent
                for (String blobID : this.blobIDs) {
                    Blob blob = readObject(join(BLOB_FOLDER, getDirID(blobID), blobID), Blob.class);
                    if (blob.getCopiedFileName().equals(fileName)) {//这里就是寻找过程
                        this.copiedFileIDs.remove(blob.getCopiedFileID());
                        this.blobIDs.remove(blob.getBlobID());
                        break;
                    }
                }
                // add the file from staging
                this.copiedFileIDs.add(fileID);
                this.blobIDs.add(makeBlob(file).getBlobID());
            }
            // same filename and same fileID, do nothing
        }
        //下面就是就是从移除的角度去看的
        //如果这个文件出现再来移除区,那么就把从父节点继承的该文件的信息
        //比如,name,nameid,blobs这些信息都要删除掉
        List<String> removedBlobIDs = new LinkedList<>();
        for (String fileName : plainFilenamesIn(REMOVED_FOLDER)) {
            File file = join(REMOVED_FOLDER, fileName);
            String fileID = getFileID(file);
            // same filename and same fileID, remove it from new commit
            if (this.copiedFileNames.contains(fileName) && this.copiedFileIDs.contains(fileID)) {
                this.copiedFileNames.remove(fileName);
                this.copiedFileIDs.remove(fileID);
                // note: you can't delete blobID one by one in this.blobIDs,
                // because you can't change iterator of blobIDs when using iterator of blobIDs
                for (String blobID : this.blobIDs) {
                    Blob blob = readObject(join(BLOB_FOLDER, getDirID(blobID), blobID), Blob.class);
                    if (fileID.equals(blob.getCopiedFileID())) {
                        removedBlobIDs.add(blobID);
                        break;
                    }
                }
            }
        }
        this.blobIDs.removeAll(removedBlobIDs);//因为可能有多个文件被移除,所以选择removeall的想法,一次全清除掉
        //当然,也可以仿照上面的删去的思路,看到有了break之后直接remove就好
    }

    //下面就是一些返回数值的操作了
    /** using in markBranch() */
    // reset the marked count
    public void resetMarkCount() {
        this.markedCount = 0;
    }

    // updated the marked count
    public void updatedMarkCount() {
        this.markedCount += 1;
    }

    // get the marked count
    public int getMarkCount() {
        return this.markedCount;
    }

    // reset the distance
    public void resetDistance() {
        this.distance = 0;
    }

    // updated the distance
    public void updatedDistance(int distance) {
        this.distance += distance;
    }

    // get the distance
    public int getDistance() {
        return this.distance;
    }

    // get currentCommitID
    public String getCommitID() {
        return this.CommitID;
    }

    /** get variable from commit */
    //get parentIDs
    public List<String> getParentIDs() {
        return this.parentIDs;
    }

    // get blobIDs
    public List<String> getBlobIDs() {
        return this.blobIDs;
    }

    // get copiedFileNames
    public List<String> getCopiedFileNames() {
        return this.copiedFileNames;
    }

    // get copiedFileIDs
    public List<String> getCopiedFileIDs() {
        return this.copiedFileIDs;
    }

    // get Date
    public Date getDate() {
        return this.date;
    }
    // get message
    public String getMessage() {
        return this.message;
    }

    // get blobs AS HashSet of blobs
    public HashSet<Blob> getBlobs() {
        HashSet<Blob> blobs = new HashSet<>();
        for (String ID : blobIDs) {
            Blob blob = readObject(join(BLOB_FOLDER, getDirID(ID), ID), Blob.class);
            blobs.add(blob);
        }
        return blobs;
    }
}
