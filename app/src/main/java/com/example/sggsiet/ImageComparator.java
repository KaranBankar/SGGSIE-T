package com.example.sggsiet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class ImageComparator {

    private Context context;

    public ImageComparator(Context context) {
        this.context = context;
    }

    // Compares selected image with a stored vulgar reference image
    public boolean isVulgarImage(Bitmap selectedImage) {
        int[] vulgarImages = {R.drawable.image, R.drawable.image2, R.drawable.image}; // Add reference images in drawable

        for (int vulgarImage : vulgarImages) {
            Bitmap referenceBitmap = getBitmapFromDrawable(vulgarImage);
            if (compareBitmaps(selectedImage, referenceBitmap)) {
                return true; // Image matches vulgar reference
            }
        }
        return false; // Image is safe
    }

    // Convert drawable resource to Bitmap
    private Bitmap getBitmapFromDrawable(int drawableId) {
        InputStream inputStream = context.getResources().openRawResource(drawableId);
        return BitmapFactory.decodeStream(inputStream);
    }

    // Simple pixel-based image comparison
    private boolean compareBitmaps(Bitmap bitmap1, Bitmap bitmap2) {
        if (bitmap1 == null || bitmap2 == null) return false;

        // Resize both bitmaps to a standard size for comparison
        Bitmap resized1 = Bitmap.createScaledBitmap(bitmap1, 100, 100, true);
        Bitmap resized2 = Bitmap.createScaledBitmap(bitmap2, 100, 100, true);

        int width = resized1.getWidth();
        int height = resized1.getHeight();

        int similarPixels = 0;
        int totalPixels = width * height;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (resized1.getPixel(x, y) == resized2.getPixel(x, y)) {
                    similarPixels++;
                }
            }
        }

        // If 85% or more pixels match, consider it vulgar
        double similarity = (similarPixels / (double) totalPixels) * 100;
        return similarity >= 85;
    }
}
