package comefindme.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.View;

public class SMSView extends View {

	private static final float MINP = 0.25f;
	private static final float MAXP = 0.75f;

	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Path mPath;
	private Paint mPaint;
	private Paint mBitmapPaint;
	private float mX, mY;
	private float smsX, smsY;
	private float startX, startY;
	private static final float TOUCH_TOLERANCE = 4;

	public SMSView(Context c) {
		super(c);

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(0xFFFF0000);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(12);

		mBitmap = Bitmap.createBitmap(320, 400, Bitmap.Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mPath = new Path();
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawColor(0xFFAAAAAA);

		canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

		canvas.drawPath(mPath, mPaint);
	}

	public void start(float x, float y) {

		Log.d("TAG", "X: " + x + " - Y: " + y);
		startX = x;
		startY = y;

		mPath.reset();

		mX = mBitmap.getWidth() / 2;
		mY = mBitmap.getHeight() / 2;
		mPath.moveTo(mX, mY);
		smsX = x;
		smsY = y;

		invalidate();
	}

	public void clear() {
		mPaint.setXfermode(new PorterDuff(PorterDuff.Mode.CLEAR));
	}

	public void move(float x, float y) {

		Log.d("TAG", startX + "-" + startY + " " + x + "-" + y);

		int offset = 10;

		float moveX = 0;
		float moveY = 0;

		boolean move = false;
		if (x > startX && x > (startX + offset)) {
			moveX = x - (startX + offset);
			move = true;
		}
		if (y > startY && y > (startY + offset)) {
			move = true;
			moveY = y - (startY + offset);
		}
		if (x < startX && x < (startX - offset)) {
			move = true;
			moveX = x - (startX - offset);
		}
		if (y < startY && y < (startY - offset)) {
			move = true;
			moveY = y - (startY - offset);
		}

		if (move) {
			float nextX = mX + (moveX / 4);
			float nextY = mY + (moveY / 4);
			mPath.quadTo(mX, mY, nextX, nextY);
			mX = nextX;
			mY = nextY;
			invalidate();
		}

	}

}