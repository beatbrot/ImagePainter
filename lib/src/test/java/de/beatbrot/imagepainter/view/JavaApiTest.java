package de.beatbrot.imagepainter.view;


import android.content.Context;
import android.graphics.Bitmap;

import de.beatbrot.imagepainter.RedoStatusChangeListener;
import de.beatbrot.imagepainter.UndoStatusChangeListener;


/**
 * This test only checks whether the API of the library is callable from Java. The class will never be ran.
 */
public class JavaApiTest {

    private Context context;

    public void intializeView() {
        ImagePainterView painterView = new ImagePainterView(context);

        painterView.setRedoStatusChangeListener((RedoStatusChangeListener) (boolx) -> System.out.println("Hello" + boolx));
        painterView.setUndoStatusChangeListener((UndoStatusChangeListener) (newProp) -> System.out.println("oYyy" + newProp));

        painterView.exportImage();
        painterView.exportImage(Bitmap.Config.ALPHA_8);
    }
}