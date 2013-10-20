package atmosphere.android.util;

import android.graphics.Bitmap;

public class BitmapUtil {
	public static Bitmap resize(Bitmap cntBitMap) {
		int cntWidth = cntBitMap.getWidth();
		int cntHeight = cntBitMap.getHeight();

		int dstWidth;
		int dstHeight;
		if (cntWidth < cntHeight) {
			dstHeight = 84;
			dstWidth = (84 * cntWidth) / cntHeight;
		} else {
			dstWidth = 84;
			dstHeight = (84 * cntHeight) / cntWidth;
		}

		Bitmap dst = Bitmap.createScaledBitmap(cntBitMap, dstWidth, dstHeight, false);
		return dst;
	}
}
