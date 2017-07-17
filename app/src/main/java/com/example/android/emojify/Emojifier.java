package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import static com.example.android.emojify.Emoji.CLOSED_EYE_SMILE;
import static com.example.android.emojify.Emoji.LEFT_WINK;
import static com.example.android.emojify.Emoji.LEFT_WINK_FROWN;
import static com.example.android.emojify.Emoji.RIGHT_WINK;
import static com.example.android.emojify.Emoji.SMILE;

/**
 * Created by ${farhanarnob} on ${06-Oct-16}.
 * class for detecting faces
 */

class Emojifier {
    private static final float EMOJI_SCALE_FACTOR = .8f;
    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .5;

    private static final String LOG_TAG = Emojifier.class.getName();

    static Bitmap detectFacesAndOverlayEmoji(Bitmap image, Context context) {


        Bitmap resultBitmap = image;

        FaceDetector faceDetector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        // Build the frame
        Frame frame = new Frame.Builder().setBitmap(image).build();

        // detect faces
        SparseArray<Face> faces = faceDetector.detect(frame);

        Log.d(LOG_TAG, "Number of faces: " + faces.size());
        if (faces.size() <= 0) {
            Toast.makeText(context, R.string.no_faces_message, Toast.LENGTH_SHORT).show();
        } else {
            for (int i = 0; i < faces.size(); i++) {
                Bitmap emojiBitmap;
                Face face = faces.valueAt(i);
                switch (whichEmoji(face)) {
                    case LEFT_WINK:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwink);
                        break;
                    case RIGHT_WINK:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwink);
                        break;
                    case CLOSED_EYE_SMILE:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_smile);
                        break;
                    case SMILE:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.smile);
                        break;
                    case LEFT_WINK_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.leftwinkfrown);
                        break;
                    case RIGHT_WINK_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.rightwinkfrown);
                        break;
                    case CLOSED_EYE_FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.closed_frown);
                        break;
                    case FROWN:
                        emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                                R.drawable.frown);
                        break;
                    default:
                        emojiBitmap = null;
                        Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
                        break;
                }

                resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);
            }
        }
        faceDetector.release();
        return resultBitmap;
    }

    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // creating a mutable version of image Bitmap
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        float emojiScaleFactor = EMOJI_SCALE_FACTOR;

        int emojiNewWidth = (int) (face.getWidth() * emojiScaleFactor);
        int emojiNewHeight = (int) (emojiBitmap.getHeight() / emojiBitmap.getWidth()
                * emojiNewWidth * emojiScaleFactor);

        // scale the emoji bitmap
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, emojiNewWidth, emojiNewHeight, false);

        float emojoPositionX =
                (face.getPosition().x + face.getWidth() / 2) - (emojiBitmap.getWidth() / 2);
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;


        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojoPositionX, emojiPositionY, null);
        return resultBitmap;

    }


    private static Emoji whichEmoji(Face face) {
        Log.d(LOG_TAG, "Smiling probability : " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "Left eye probability : " + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "Right eye probability : " + face.getIsRightEyeOpenProbability());
        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;
        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;

        Emoji emoji;
        if (smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = LEFT_WINK;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = RIGHT_WINK;
            } else if (leftEyeClosed) {
                emoji = CLOSED_EYE_SMILE;
            } else {
                emoji = SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = LEFT_WINK_FROWN;
            } else if (rightEyeClosed && !leftEyeClosed) {
                emoji = Emoji.RIGHT_WINK_FROWN;
            } else if (leftEyeClosed) {
                emoji = Emoji.CLOSED_EYE_FROWN;
            } else {
                emoji = Emoji.FROWN;
            }
        }

        Log.d(LOG_TAG, "Emoji is : " + emoji.name());
        return emoji;
    }

}
