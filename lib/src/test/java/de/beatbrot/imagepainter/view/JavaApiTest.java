package de.beatbrot.imagepainter.view;


import android.content.Context;
import android.graphics.Bitmap;


/**
 * This test only checks whether the API of the library is callable from Java. The class will never be ran.
 */
@SuppressWarnings("unused")
class JavaApiTest {

    private Context context;

    public void initializeView() {
        ImagePainterView painterView = new ImagePainterView(context);

        painterView.setRedoStatusChangeListener((bool) -> acceptsBool(bool, false));
        painterView.setUndoStatusChangeListener((newProp) -> acceptsBool(true, newProp));

        painterView.exportImage();
        painterView.exportImage(Bitmap.Config.ALPHA_8);
    }


    private void acceptsBool(boolean first, boolean second) {
        throw new UnsupportedOperationException();
    }
}
