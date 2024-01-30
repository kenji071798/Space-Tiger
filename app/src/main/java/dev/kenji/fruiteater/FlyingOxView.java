package dev.kenji.fruiteater;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class FlyingOxView extends View
{
    private Bitmap Ox[] = new Bitmap[2];
    private int OxX = 10;
    private  int OxY;
    private  int OxSpeed;
    private  int canvasWidth, canvasHeight;

    private  int appleX, appleY, appleSpeed = 16;
    private Bitmap appleBitmapResized;

    private  int grapeX, grapeY, grapeSpeed = 20;
    private Bitmap grapeBitmapResized;


    private  int bombX, bombY, bombSpeed = 25;
    private Bitmap bombBitmapResized;


    private  int score, lifeCounterOfOx;


    private boolean touch = false;

    private Bitmap backgroundImage;
    private Paint scorePaint = new Paint();
    private Bitmap life[] = new Bitmap[2];


    public FlyingOxView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Ox[0] = BitmapFactory.decodeResource(getResources(), R.drawable.ox11);
        Ox[1] = BitmapFactory.decodeResource(getResources(), R.drawable.ox22);

        backgroundImage = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
        backgroundImage = Bitmap.createScaledBitmap(backgroundImage,1080 ,2150 , true);


        // ... (other initialization code)


        Bitmap yellowBallBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.apple_red);
        appleBitmapResized = Bitmap.createScaledBitmap(yellowBallBitmapOriginal, 100, 100, true);

        Bitmap greenBallBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.grape_violet);
        grapeBitmapResized = Bitmap.createScaledBitmap(greenBallBitmapOriginal, 100, 100, true);


        Bitmap redBallBitmapOriginal = BitmapFactory.decodeResource(getResources(), R.drawable.dead_bomb);
        bombBitmapResized = Bitmap.createScaledBitmap(redBallBitmapOriginal, 100, 100, true);



        scorePaint.setColor(Color.WHITE);
        scorePaint.setTextSize(70);
        scorePaint.setTypeface(Typeface.DEFAULT_BOLD);
        scorePaint.setAntiAlias(true);

        life[0] = BitmapFactory.decodeResource(getResources(), R.drawable.hearts);
        life[1] = BitmapFactory.decodeResource(getResources(), R.drawable.heart_grey);

        OxY = 550;
        score = 0;
        lifeCounterOfOx = 3;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvasWidth = canvas.getWidth();
        canvasHeight = canvas.getHeight();


        canvas.drawBitmap(backgroundImage, 0, 0, null);
        int minOxY = Ox[0].getHeight();
        int maxOxY = canvasHeight - Ox[0].getHeight() * 3;
        OxY = OxY + OxSpeed;

        if (OxY < minOxY) {
            OxY = minOxY;
        }

        if (OxY > maxOxY) {
            OxY = maxOxY;
        }
        OxSpeed = OxSpeed + 2;

        if (touch) {
            canvas.drawBitmap(Ox[1], OxX, OxY, null);
            touch = false;
        } else {
            canvas.drawBitmap(Ox[0], OxX, OxY, null);

        }

        appleX = appleX - appleSpeed;
        if (hitBallChecker(appleX, appleY)) {
            score = score + 10;
            appleX = -100;
        }
        if (appleX < 0) {
            appleX = canvasWidth + 21;
            appleY = (int) Math.floor(Math.random() * (maxOxY - minOxY)) + minOxY;
        }
        canvas.drawBitmap(appleBitmapResized, appleX, appleY, null);



        grapeX = grapeX - grapeSpeed;
        if (hitBallChecker(grapeX, grapeY)) {
            score = score + 20;
            grapeX = -100;
        }
        if (grapeX < 0) {
            grapeX = canvasWidth + 21;
            grapeY = (int) Math.floor(Math.random() * (maxOxY - minOxY)) + minOxY;
        }
        canvas.drawBitmap(grapeBitmapResized, grapeX, grapeY, null);


        bombX = bombX - bombSpeed;
        if (hitBallChecker(bombX, bombY)) {

            bombX = -100;
            lifeCounterOfOx--;

            if (lifeCounterOfOx == 0) {
                Toast.makeText(getContext(), "Game Over", Toast.LENGTH_SHORT).show();

                Intent gameOverIntent = new Intent(getContext(), GameOverActivity.class);
                gameOverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                gameOverIntent.putExtra("score", score);
                getContext().startActivity(gameOverIntent);

            }
        }
        if (bombX < 0) {
            bombX = canvasWidth + 21;
            bombY = (int) Math.floor(Math.random() * (maxOxY - minOxY)) + minOxY;
        }
        canvas.drawBitmap(bombBitmapResized, bombX, bombY, null);


        canvas.drawText("Score : " + score, 20, 60, scorePaint);
        for (int i = 0; i < 3; i++) {
            int x = (int) (580 + life[0].getWidth() * 1.5 * i);
            int y = 30;

            if (i < lifeCounterOfOx) {
                canvas.drawBitmap(life[0], x, y, null);
            } else {
                canvas.drawBitmap(life[1], x, y, null);
            }
        }


    }

    public boolean hitBallChecker(int x, int y)
    {
        if (OxX < x && x < (OxX + Ox[0].getWidth()) && OxY < y && y < (OxY + Ox[0].getHeight()))
        {
            return  true;
        }
        return  false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            touch = true;
            OxSpeed = -22;
        }
        return true;
    }
}
