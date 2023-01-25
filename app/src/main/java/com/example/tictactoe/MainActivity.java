package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity implements JGameLib.GameEvent {
    int cellCount = 3, turns = 0;
    JGameLib gameLib = null;
    JGameLib.Card[][] cellCards = new JGameLib.Card[cellCount][cellCount];
    final int cellColor = Color.WHITE;
    final int edgeColor = Color.BLACK;
    final float edgeThick = 0.1f;
    final String markCom = "O", markUser = "X";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gameLib = findViewById(R.id.gameLib);
        initGame();
    }

    @Override
    protected void onDestroy() {
        if(gameLib != null)
            gameLib.clearMemory();
        super.onDestroy();
    }

    void initGame() {
        gameLib.setScreenGrid(cellCount+edgeThick*2, cellCount+edgeThick*2);
        gameLib.listener(this);
        gameLib.addCardColor(edgeColor);
        for(int y=0; y < cellCount; y++) {
            for(int x=0; x < cellCount; x++) {
                cellCards[y][x] = gameLib.addCardColor(cellColor, x+edgeThick, y+edgeThick, 1, 1);
                cellCards[y][x].edge(edgeColor, edgeThick);
                cellCards[y][x].text("", edgeColor, 0.7);
                cellCards[y][x].set(y*10 + x);
            }
        }
        Restart();
    }

    void Restart() {
        for(int y=0; y < cellCount; y++) {
            for (int x = 0; x < cellCount; x++) {
                cellCards[y][x].text = "";
            }
        }
        turns = 0;
        turnComputer();
    }

    Point turnComputer() {
        Point po = new Point(-1,-1);
        while(true) {
            int y = gameLib.random(cellCount);
            int x = gameLib.random(cellCount);
            if(cellCards[y][x].text.isEmpty()) {
                cellCards[y][x].text(markCom);
                turns ++;
                po.set(x, y);
                break;
            }
        }
        return po;
    }

    boolean findSame3(int x, int y, String mark) {
        int countH = 0, countV = 0, countN = 0, countZ = 0;
        for(int i=0; i < cellCount; i++) {
            if(cellCards[i][x].text == mark)
                countH ++;
            if(cellCards[y][i].text == mark)
                countV ++;
            if(cellCards[i][i].text == mark)
                countN ++;
            if(cellCards[i][cellCount-i-1].text == mark)
                countZ ++;
        }
        if(countH == cellCount || countV == cellCount
            || countN == cellCount || countZ== cellCount)
            return true;
        return false;
    }

    // User Event start ====================================

    public void onBtnRestart(View v) {
        Restart();
    }

    // User Event end ====================================

    // Game Event start ====================================

    @Override
    public void onGameWorkEnded(JGameLib.Card card, JGameLib.WorkType workType) {}

    @Override
    public void onGameTouchEvent(JGameLib.Card card, int action, float x, float y) {
        if(action == MotionEvent.ACTION_UP) {
            if(card.text != null || card.text.isEmpty()) {
                card.text(markUser);
                turns ++;
                int axis = card.getInt();
                int col = axis % 10, row = axis / 10;
                if(turns >= 6 && findSame3(col, row, markUser)) {
                    gameLib.popupDialog(null, "Congratulation! You won.", "Close");
                } else {
                    Point po = turnComputer();
                    if(turns >= 5 && findSame3(po.x, po.y, markCom))
                        gameLib.popupDialog(null, "You loose. Try again.", "Close");
                    else if(turns >= 9)
                        gameLib.popupDialog(null, "The game tied. Try again.", "Close");
                }
            }
        }
    }

    @Override
    public void onGameSensor(int sensorType, float x, float y, float z) {}

    @Override
    public void onGameCollision(JGameLib.Card card1, JGameLib.Card card2) {}

    @Override
    public void onGameTimer() {}

    // Game Event end ====================================

}
