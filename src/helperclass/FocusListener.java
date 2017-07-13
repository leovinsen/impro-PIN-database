/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperclass;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
     * Class added as a listener to focusedProperty() of a Node, 
     * which triggers when said class is in focus. A getText() will be called on
     * the passed-in TextField when the Node is in focus and a validity check will
     * be performed on the entered data. The result of the validity check is reflected
     * in the passed-in Label.
     */
public abstract class FocusListener implements ChangeListener<Boolean> {

    private final TextField field;
    private final Label label;

    public FocusListener(TextField field, Label label) {
        this.field = field;
        this.label = label;
    }

    @Override
    public abstract void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue);

    public TextField getField() {
        return this.field;
    }

    public Label getLabel() {
        return this.label;
    }
}

