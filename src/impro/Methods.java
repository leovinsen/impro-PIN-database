/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impro;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
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

    public static boolean isValidIP(String inputIP) {
        return inputIP.startsWith("IP") && isNumeric(inputIP.substring(2)) && inputIP.length() == 10;
    }

    public static boolean isValidPIN(String inputPIN) {
        return inputPIN.length() == 10;
    }
    
    public static boolean isValidRekening(String inputRekening){
        return isNumeric(inputRekening) && inputRekening.length() == 10;
    }
  
    public static void makeRed(Node n) {
        n.setStyle("-fx-text-fill: red");
    }

    public static void makeGreen(Node n) {
        n.setStyle("-fx-text-fill: derive(green,70%)");
    }
    
    public static void showValidMessage(Label l, String s) {
        String message = s + " valid.";
        l.setText(message);
        makeGreen(l);
    }
    
    public static void showInvalidMessage(Label l, String s) {
        String message = s + " tidak valid.";
        l.setText(message);
        makeRed(l);
    }
    
    public static void createAlert(Exception ex) {
        Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Database Error");
        alert.setHeaderText(null);
        alert.setContentText("Coba lagi. Hubungi vinsen jika masih bermasalah.");
        alert.showAndWait();
    }

}

