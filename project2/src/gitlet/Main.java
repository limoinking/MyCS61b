package gitlet;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

import static gitlet.Utils.*;
import static gitlet.MyUtils.*;
import static gitlet.Repository.*;
import static gitlet.Commit.*;
import static gitlet.Blob.*;
/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        // TODO: what if args is empty?
        if(args.length == 0)
        {
            printErrorWithExit("Please enter a command");
        }
        String firstArg = args[0];
        switch(firstArg) {
            case "init"://初始化
                validateOperands("init",args,1);
                Repository.initCommand("initial commit");
                break;
            case "add"://加入
                validateInitAndOperands("add",args,2);
                Repository.addCommand(args[1]);
                break;
            case "commit"://提交
                validateInitAndOperands("commit",args,2);
                Repository.commitCommand(args[1]);
            case "rm"://移除
                validateInitAndOperands("rm",args,2);
            case "log"://打印日记
                validateInitAndOperands("log", args, 1);
                Repository.logCommand();
                break;
            case "global-log"://全局日记??
                validateInitAndOperands("global-log", args, 1);
                Repository.globalLogCommand();
                break;
            case "find"://查找(不知道是文件还是分支)
                validateInitAndOperands("global-log", args, 2);
                Repository.findCommand(args[1]);
                break;
            case "status"://打印状态,分支和暂存区的状态
                validateInitAndOperands("status", args, 1);
                Repository.statusCommand();
                break;
            case "checkout"://可能是切换并创立新的分支?
                if (args.length == 2 || args.length == 3 || args.length == 4) {
                    validateInitAndOperands("checkout", args, args.length);
                } else {
                    printErrorWithExit("Incorrect operands.");
                }
                Repository.checkoutCommand(args);
                break;
            case "reset"://版本回跳
                validateInitAndOperands("reset", args, 2);
                Repository.resetCommand(args[1]);
                break;
            case "branch"://解决版本冲突
                validateInitAndOperands("branch", args, 2);
                Repository.branchCommand(args[1]);
                break;
            case "rm-branch"://移除某一个分支
                validateInitAndOperands("rm-branch", args, 2);
                Repository.rmBranchCommand(args[1]);
                break;
            case "merge"://进行版本的合并
                validateInitAndOperands("merge", args, 2);
                Repository.mergeCommand(args[1]);
                break;
            // remote
            case "add-remote"://添加到远端
                validateInitAndOperands("add-remote", args, 3);
                Repository.addRemoteCommand(args[1], args[2]);
                break;
            case "rm-remote"://从远端中移除
                validateInitAndOperands("rm-remote", args, 2);
                Repository.rmRemoteCommand(args[1]);
                break;
            case "push"://推送
                validateInitAndOperands("push", args, 3);
                Repository.pushCommand(args[1], args[2]);
                break;
            case "fetch"://另一种拉取,和merge一块用
                validateInitAndOperands("fetch", args, 3);
                Repository.fetchCommand(args[1], args[2]);
                break;
            case "pull"://相当于是fetch和merge(和本地的分支)合并的操作
                validateInitAndOperands("pull", args, 3);
                Repository.pullCommand(args[1], args[2]);
                break;
            // If a user inputs a command that doesn’t exist,
            // print the message No command with that name exists. and exit.
            default:
                printErrorWithExit("No command with that name exists.");
            // TODO: FILL THE REST IN
        }
    }

    //下面就是检验是否有满足初始化和操作符的情况
    public static void validateInitAndOperands(String cmd,String[] args,int n)
    {
        if(!MyUtils.validateDirAndFolder())//先判断是否初始化过
        {
            printErrorWithExit("Not in an initialized Gitlet directory");
        }
        validateOperands(cmd,args,n);//再来判断对于该操作符
        //是否有合理操作
    }

    //这些是检验字符的,当然,需要鉴定一下路径是否是存在的
    public static void validateOperands(String cmd,String[] args,int n)
    {
        if(args.length != n)//总字符不对
        {
            printErrorWithExit("Incorrect operands");
        }
        String firstAsg = args[0];
        switch(firstAsg)
        {
            case "add":
                matchFileName(args[1]);//看看文件名是否合法
                break;
            case "commit":
                if(args[1].equals(""))
                {
                    printErrorWithExit("Please enter a commit message.");
                }
                matchMessage(args[1]);//看看字符串是否合法
                break;
        }
    }

    //下面这些就是检验表达式是否合理
    //要检验输入的内容是否是合法的
    private static void matchFileName(String fileName)//确保文件名称是正确的
            //文件名称中不能包含下面这些符号
            //需要将所有的字符都匹配一遍才能返回1
            //如果返回0，那就说明有不是这些字符外的字符
            //也就是这些字符
            //就会终止程序
    {
        //如果有下面这些字符就不匹配
        String fileNamePattern = "[^\\/\\\\\\:\\*\\\"\\>\\|\\?]+\\.[^\\/\\\\\\:\\*\\\"\\>\\|\\?]+";
        if(!Pattern.matches(fileNamePattern,fileName))
        {
            printErrorWithExit("Incorrect operands");
        }
    }
    private static void matchMessage(String message)
    {
        String messagePattren = ".+";//保证这个字符串不是空的
        if(!Pattern.matches(messagePattren,message))
        {
            printErrorWithExit("Incorrect operands.");
        }
    }

    private static void matchTowLines(String TwoLines)
    {
        String twoLinesPattern = "--";
        if (!Pattern.matches(twoLinesPattern, TwoLines)) {
            printErrorWithExit("Incorrect operands.");
        }
    }
}
