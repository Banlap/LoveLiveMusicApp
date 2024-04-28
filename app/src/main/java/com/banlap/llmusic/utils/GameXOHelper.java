package com.banlap.llmusic.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/**
 * 井字游戏整体类
 * */
public class GameXOHelper {

    /**
     * 当前玩家是否使用X作为棋
     * */
    public static boolean useChessType = true;  //false=o true=x
    /**
     * 棋类型：X=1 O=-1
     * */
    public static int[] chessType = {-1, 1};  //值1=x -1=o
    public static String chessTypeX= "X";
    public static String chessTypeO= "O";

    /**
     * 棋位置
     * */
    public static String locTopLeft = "locTopLeft";
    public static String locTop = "locTop";
    public static String locTopRight = "locTopRight";
    public static String locCenterLeft = "locCenterLeft";
    public static String locCenter= "locCenter";
    public static String locCenterRight = "locCenterRight";
    public static String locBottomLeft = "locBottomLeft";
    public static String locBottom= "locBottom";
    public static String locBottomRight = "locBottomRight";

    /**
     * 当前赛局值 0表示空位置
     * */
    public static int[][] chessLocTotal = {{0,0,0}, {0,0,0}, {0,0,0}};

    public static List<int[]> listPlayLocHistory = new ArrayList<>();
    public static List<int[]> listAILocHistory = new ArrayList<>();

    /**
     * 玩家需要消失的棋位置
     * */
    public static String playerDeleteLoc="";
    /**
     * ai需要消失的棋位置
     * */
    public static String aiDeleteLoc="";

    /**
     * 开始游戏
     * @param isPlayerSet 是否玩家先手
     * */
    public static void startGame(boolean isPlayerSet, String chessLoc, GameXOCallback callback) {
        if(isPlayerSet) {
            switch(chessLoc) {
                case "locTopLeft": playerSetChessLoc(0, 0, callback); break;
                case "locTop": playerSetChessLoc(0, 1, callback); break;
                case "locTopRight": playerSetChessLoc(0, 2, callback); break;
                case "locCenterLeft": playerSetChessLoc(1, 0, callback); break;
                case "locCenter": playerSetChessLoc(1, 1, callback); break;
                case "locCenterRight": playerSetChessLoc(1, 2, callback); break;
                case "locBottomLeft": playerSetChessLoc(2, 0, callback); break;
                case "locBottom": playerSetChessLoc(2, 1, callback); break;
                case "locBottomRight": playerSetChessLoc(2, 2, callback); break;
            }
        } else {
            aiSetChessLoc(callback);
        }
    }

    /**
     * 根据xy坐标返回棋盘位置
     * */
    public static String returnLocStr(int x, int y) {
        if(x==0 && y==0 ) { return locTopLeft; }
        else if(x==0 && y==1) { return locTop; }
        else if(x==0 && y==2) { return locTopRight; }
        else if(x==1 && y==0) { return locCenterLeft; }
        else if(x==1 && y==1) { return locCenter; }
        else if(x==1 && y==2) { return locCenterRight; }
        else if(x==2 && y==0) { return locBottomLeft; }
        else if(x==2 && y==1) { return locBottom; }
        else if(x==2 && y==2) { return locBottomRight; }
        return ""; //异常返回
    }

    /**
     * 根据字符串返回棋盘xy坐标
     * */
    public static int[] returnLocXY(String locStr) {
        int[] xy = {-1, -1};
        if(GameXOHelper.locTopLeft.equals(locStr)) {
            xy[0] = 0;
            xy[1] = 0;
        } else if(GameXOHelper.locTop.equals(locStr)) {
            xy[0] = 0;
            xy[1] = 1;
        } else if(GameXOHelper.locTopRight.equals(locStr)) {
            xy[0] = 0;
            xy[1] = 2;
        } else if(GameXOHelper.locCenterLeft.equals(locStr)) {
            xy[0] = 1;
            xy[1] = 0;
        } else if(GameXOHelper.locCenter.equals(locStr)) {
            xy[0] = 1;
            xy[1] = 1;
        } else if(GameXOHelper.locCenterRight.equals(locStr)) {
            xy[0] = 1;
            xy[1] = 2;
        } else if(GameXOHelper.locBottomLeft.equals(locStr)) {
            xy[0] = 2;
            xy[1] = 0;
        } else if(GameXOHelper.locBottom.equals(locStr)) {
            xy[0] = 2;
            xy[1] = 1;
        } else if(GameXOHelper.locBottomRight.equals(locStr)) {
            xy[0] = 2;
            xy[1] = 2;
        }
        return xy;
    }


    /**
     * 判断使用哪个类型的棋
     * */
    public static int useChessType() {
        return useChessType? chessType[1]: chessType[0];
    }

    //设置使用哪个类型的棋
    public static void isUseChessTypeX(boolean isUse) { //true使用x false使用o
        useChessType = isUse;
    }

    /**
     * 判断哪个类型的棋胜利时使用的值
     * @param isUse true使用x; false使用O
     * @return 3则x胜利 -3则o胜利
     * */
    public static int useChessWinType(boolean isUse) {
        return isUse? 3: -3;
    }

    /**
     * 判断哪个类型的棋将要胜利时使用的值
     * @param isUse true使用x; false使用O
     * @return 3则x胜利 -3则o胜利
     * */
    public static int useChessWillWinType(boolean isUse) {
        return isUse? 2: -2;
    }

    /**
     * 设置玩家下棋的位置
     * */
    public static void playerSetChessLoc(int x, int y, GameXOCallback callback) {
        //当前局已胜利，无需执行，需要重置游戏
        if(checkWhoIsWin()!=0) {
            callback.onResult(false, "", "");
            return;
        }
        //当前位置是否空
        if(chessLocTotal[x][y] == 0) {
            chessLocTotal[x][y] = useChessType();
            //记录玩家下棋位置
            setLocHistory(true, x, y);
            if(listPlayLocHistory != null && listPlayLocHistory.size()>=3) {
                int[] xyHistory = listPlayLocHistory.get(0);
                String playerWillDeleteLoc = returnLocStr(xyHistory[0], xyHistory[1]);
                if(!TextUtils.isEmpty(playerDeleteLoc)) {
                    int[] xyPlayer = returnLocXY(playerDeleteLoc);
                    int valueX = xyPlayer[0];
                    int valueY = xyPlayer[1];
                    deleteLoc(valueX, valueY);
                }
                callback.onDismissLoc("","", playerWillDeleteLoc ,playerDeleteLoc);
                playerDeleteLoc = playerWillDeleteLoc;
            }
            int whoWin = checkWhoIsWin();
            if(whoWin == 0) {
                //处理ai下棋的位置
                aiSetChessLoc(callback);
            } else {
                //返回已经胜利
                callback.onResult(true, whoWin == 1? chessTypeX : chessTypeO, "");
            }
        } else {
            callback.onResult(false, "", "");
        }
    }

    /**
     * 检查当前赛局是否存在胜方
     * @return 1=X胜利 -1=O胜利 0=没有胜利
     * */
    public static int checkWhoIsWin() {
        if(chessLocTotal[0][0] + chessLocTotal[0][1] + chessLocTotal[0][2] == useChessWinType(true)  // 上横
                || chessLocTotal[1][0] + chessLocTotal[1][1] + chessLocTotal[1][2] == useChessWinType(true)  // 中横
                || chessLocTotal[2][0] + chessLocTotal[2][1] + chessLocTotal[2][2] == useChessWinType(true)  // 下横
                || chessLocTotal[0][0] + chessLocTotal[1][0] + chessLocTotal[2][0] == useChessWinType(true)  // 左竖
                || chessLocTotal[0][1] + chessLocTotal[1][1] + chessLocTotal[2][1] == useChessWinType(true)  // 中竖
                || chessLocTotal[0][2] + chessLocTotal[1][2] + chessLocTotal[2][2] == useChessWinType(true)  // 右竖
                || chessLocTotal[0][0] + chessLocTotal[1][1] + chessLocTotal[2][2] == useChessWinType(true)  // 左斜
                || chessLocTotal[0][2] + chessLocTotal[1][1] + chessLocTotal[2][0] == useChessWinType(true)  // 右斜

        ) {
            return chessType[1];
        } else if(chessLocTotal[0][0] + chessLocTotal[0][1] + chessLocTotal[0][2] == useChessWinType(false)  // 上横
                || chessLocTotal[1][0] + chessLocTotal[1][1] + chessLocTotal[1][2] == useChessWinType(false)  // 中横
                || chessLocTotal[2][0] + chessLocTotal[2][1] + chessLocTotal[2][2] == useChessWinType(false)  // 下横
                || chessLocTotal[0][0] + chessLocTotal[1][0] + chessLocTotal[2][0] == useChessWinType(false)  // 左竖
                || chessLocTotal[0][1] + chessLocTotal[1][1] + chessLocTotal[2][1] == useChessWinType(false)  // 中竖
                || chessLocTotal[0][2] + chessLocTotal[1][2] + chessLocTotal[2][2] == useChessWinType(false)  // 右竖
                || chessLocTotal[0][0] + chessLocTotal[1][1] + chessLocTotal[2][2] == useChessWinType(false)  // 左斜
                || chessLocTotal[0][2] + chessLocTotal[1][1] + chessLocTotal[2][0] == useChessWinType(false)  // 右斜

        ) {
            return chessType[0];
        }
        return 0;
    }

    /**
     * ai处理棋位置
     * */
    public static void aiSetChessLoc(GameXOCallback callback){
        //伪随机
        Random random = new Random();
//        int x = random.nextInt(3);
//        int y = random.nextInt(3);

        //加入算法
        int[] xy = aiDetermineSetChessLoc(useChessType);
        int x = xy[0];
        int y = xy[1];

        if(x == -1 || y == -1) {
            x = random.nextInt(3);
            y = random.nextInt(3);
        }

        if(chessLocTotal[x][y] == 0) {
            chessLocTotal[x][y] = useChessType? chessType[0] : chessType[1];
            setLocHistory(false, x, y);
            if(listAILocHistory != null && listAILocHistory.size()>=3) {
                int[] xyHistory = listAILocHistory.get(0);
                String aiWillDeleteLoc = returnLocStr(xyHistory[0], xyHistory[1]);
                if(!TextUtils.isEmpty(aiDeleteLoc)) {
                    int[] xyAi = returnLocXY(aiDeleteLoc);
                    int valueX = xyAi[0];
                    int valueY = xyAi[1];
                    deleteLoc(valueX, valueY);
                }
                callback.onDismissLoc(aiWillDeleteLoc, aiDeleteLoc, "","");
                aiDeleteLoc = aiWillDeleteLoc;
            }
            int whoWin = checkWhoIsWin();
            if(whoWin == 0) {
                //返回ai下的位置
                callback.onResult(false, "", returnLocStr(x, y));
            } else {
                //返回已经胜利
                callback.onResult(true, whoWin == 1? chessTypeX : chessTypeO, returnLocStr(x, y));
            }
        } else if(chessLocTotal[0][0] != 0 && chessLocTotal[0][1] != 0 && chessLocTotal[0][2] != 0
            && chessLocTotal[1][0] != 0 && chessLocTotal[1][1] != 0 && chessLocTotal[1][2] != 0
            && chessLocTotal[2][0] != 0 && chessLocTotal[2][1] != 0 && chessLocTotal[2][2] != 0){
            //已结束
            callback.onResult(true, "", "");
        } else {
            aiSetChessLoc(callback);
        }
    }

    /**
     * ai下棋算法 ps: 待优化
     * <p>先考虑自身是否存在胜算再考虑玩家</p>
     * */
    public static int[] aiDetermineSetChessLoc(boolean isUseTypeX) {
        Random random = new Random();
        int[] xy = checkWhoWillWin(useChessWillWinType(false));
        if(xy[0] == -1 || xy[1] == -1) {
            xy = checkWhoWillWin(useChessWillWinType(true));
        }

        if(xy[0] == -1 || xy[1] == -1) {
            xy[0] = random.nextInt(3);
            xy[1] = random.nextInt(3);
        }

        return xy;

    }


    /**
     * 检查是否存在胜方
     * */
    public static int[] checkWhoWillWin(int value) {
        int[] xy = {-1, -1};
        if(chessLocTotal[0][0] + chessLocTotal[0][1] + chessLocTotal[0][2] == value) {  // 上横
            if(chessLocTotal[0][0] == 0) {
                xy[0] = 0;
                xy[1] = 0;
            } else if(chessLocTotal[0][1] == 0) {
                xy[0] = 0;
                xy[1] = 1;
            } else if(chessLocTotal[0][2] == 0)  {
                xy[0] = 0;
                xy[1] = 2;
            }
        } else if (chessLocTotal[1][0] + chessLocTotal[1][1] + chessLocTotal[1][2] == value){ // 中横
            if(chessLocTotal[1][0] == 0) {
                xy[0] = 1;
                xy[1] = 0;
            } else if(chessLocTotal[1][1] == 0) {
                xy[0] = 1;
                xy[1] = 1;
            } else if(chessLocTotal[1][2] == 0)  {
                xy[0] = 1;
                xy[1] = 2;
            }
        } else if (chessLocTotal[2][0] + chessLocTotal[2][1] + chessLocTotal[2][2] == value){ // 下横
            if(chessLocTotal[2][0] == 0) {
                xy[0] = 2;
                xy[1] = 0;
            } else if(chessLocTotal[2][1] == 0) {
                xy[0] = 2;
                xy[1] = 1;
            } else if(chessLocTotal[2][2] == 0)  {
                xy[0] = 2;
                xy[1] = 2;
            }
        } else if (chessLocTotal[0][0] + chessLocTotal[1][0] + chessLocTotal[2][0] == value){ // 左竖
            if(chessLocTotal[0][0] == 0) {
                xy[0] = 0;
                xy[1] = 0;
            } else if(chessLocTotal[1][0] == 0) {
                xy[0] = 1;
                xy[1] = 0;
            } else if(chessLocTotal[2][0] == 0)  {
                xy[0] = 2;
                xy[1] = 0;
            }
        } else if (chessLocTotal[0][1] + chessLocTotal[1][1] + chessLocTotal[2][1] == value){ // 中竖
            if(chessLocTotal[0][1] == 0) {
                xy[0] = 0;
                xy[1] = 1;
            } else if(chessLocTotal[1][1] == 0) {
                xy[0] = 1;
                xy[1] = 1;
            } else if(chessLocTotal[2][1] == 0)  {
                xy[0] = 2;
                xy[1] = 1;
            }
        } else if (chessLocTotal[0][2] + chessLocTotal[1][2] + chessLocTotal[2][2] == value){ // 右竖
            if(chessLocTotal[0][2] == 0) {
                xy[0] = 0;
                xy[1] = 2;
            } else if(chessLocTotal[1][2] == 0) {
                xy[0] = 1;
                xy[1] = 2;
            } else if(chessLocTotal[2][2] == 0)  {
                xy[0] = 2;
                xy[1] = 2;
            }
        } else if (chessLocTotal[0][0] + chessLocTotal[1][1] + chessLocTotal[2][2] == value){ // 左斜
            if(chessLocTotal[0][0] == 0) {
                xy[0] = 0;
                xy[1] = 0;
            } else if(chessLocTotal[1][1] == 0) {
                xy[0] = 1;
                xy[1] = 1;
            } else if(chessLocTotal[2][2] == 0)  {
                xy[0] = 2;
                xy[1] = 2;
            }
        } else if (chessLocTotal[0][2] + chessLocTotal[1][1] + chessLocTotal[2][0] == value){ // 右斜
            if(chessLocTotal[0][2] == 0) {
                xy[0] = 0;
                xy[1] = 2;
            } else if(chessLocTotal[1][1] == 0) {
                xy[0] = 1;
                xy[1] = 1;
            } else if(chessLocTotal[2][0] == 0)  {
                xy[0] = 2;
                xy[1] = 0;
            }
        }

        return xy;

    }

    /**
     * 保存下棋的位置
     * @param isSavePlayer 保存类型： true保存玩家下棋位置 false保存AI下棋位置
     * */
    public static void setLocHistory(boolean isSavePlayer, int x, int y) {
        int[] xy = {-1, -1};
        xy[0] = x;
        xy[1] = y;
        if (isSavePlayer) {
            if(listPlayLocHistory.size() >=3) { //存储3个位置信息
                listPlayLocHistory.remove(0);
            }
            listPlayLocHistory.add(xy);
        } else {
            if(listAILocHistory.size() >=3) {
                listAILocHistory.remove(0);
            }
            listAILocHistory.add(xy);
        }
    }

    /**
     * 删除当前棋盘位置下的棋值
     * */
    public static void deleteLoc(int x, int y) {
        chessLocTotal[x][y] = 0;
    }

    /**
     * 重置游戏
     * */
    public static void resetGame() {
        chessLocTotal = new int[][] {{0,0,0}, {0,0,0}, {0,0,0}};
        listPlayLocHistory.clear();
        listPlayLocHistory = new ArrayList<>();
        listAILocHistory.clear();
        listAILocHistory = new ArrayList<>();
        playerDeleteLoc = "";
        aiDeleteLoc = "";
    }

    public interface GameXOCallback {
        /**
         * @param isOver 是否结束赛局/是否点击不存在的位置
         * @param winUser 赢家 isOver结束时 空白则打和
         * @param aiLoc ai返回的下棋位置
         * */
        void onResult(boolean isOver, String winUser, String aiLoc);  //返回结果

        /**
         * @param aiLocWD 返回ai将要消失的棋位置
         * @param aiLocD 返回ai消失的棋位置
         * @param playerLocWD 返回玩家将要消失的棋位置
         * @param playerLocD 返回玩家消失的棋位置
         * */
        void onDismissLoc(String aiLocWD, String aiLocD, String playerLocWD, String playerLocD);

    }


}
