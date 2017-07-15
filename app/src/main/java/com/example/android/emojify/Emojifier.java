package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by ${farhanarnob} on ${06-Oct-16}.
 * class for detecting faces
 */

class Emojifier {

    private static final String LOG_TAG = Emojifier.class.getName();

    static void detectFaces(Bitmap image, Context context) {

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
                getClassifications(faces.valueAt(i));
            }
        }
        faceDetector.release();
    }

    private static void getClassifications(Face face) {
        Log.d(LOG_TAG, "Smiling probability : " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "Left eye probability : " + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "Right eye probability : " + face.getIsRightEyeOpenProbability());
    }

}
