/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;

/**
 *
 * @author asus
 */
public class Methods {
    
    public static void addTextLimiter(TextField tf, int maxLength) {
        tf.textProperty().addListener(new ChangeListener<String>() {

            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().length() > 10) {
                    String s = tf.getText().substring(0, 10);
                    tf.setText(s);
                }
            }
        });
    }

    public static boolean isNumeric(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");

    }
}
