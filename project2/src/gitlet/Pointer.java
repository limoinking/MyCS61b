package gitlet;

import java.io.Serializable;

import static gitlet.MyUtils.*;
import static gitlet.Utils.*;
import static gitlet.Repository.*;

public class Pointer implements Serializable {
    private String commitID;
    private String branchName;
    private String initCommitID;
    private String activeBranchName;
    public Pointer(boolean isHead, String Name, String ID)
    {
        //由于可能出现指向对象为主分支和副分支的情况
        //所以用两个不同的变量来记录值
        if(isHead == true)
        {
            this.initCommitID = ID;
            this.activeBranchName = Name;
        }
        else
        {
            this.commitID = ID;
            this.branchName = Name;
        }
    }

    public void saveBranchFile()
    {
        saveObj(BRANCH_FOLDER,this.branchName,this);
    }

    public void saveHEADFile()
    {
        saveObj(BRANCH_FOLDER,headName,this);
    }

    public String getActiveBranchName()
    {
        return this.activeBranchName;
    }
    public String getInitCommitID() {
        return this.initCommitID;
    }

    // get CommitID in branch
    public String getCommitID() {
        return this.commitID;
    }
}
