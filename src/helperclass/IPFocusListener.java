/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperclass;

import impro.AddMemberController;
import static impro.Methods.makeGreen;
import static impro.Methods.makeRed;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static model.Database.checkMember;

/**
 *
 * @author asus
 */
public class IPFocusListener extends FocusListener {
    
    private final String initialInput;

    public IPFocusListener(TextField field, Label label, String initInput) {
        super(field, label);
        this.initialInput = initInput;
    }

    @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        String input = getField().getText();
        Label labelMessage = getLabel();
        //When field is out of focus
        if (!newValue) {
            try {
                if (checkMember(input) && !input.equals(initialInput)) {
                    labelMessage.setText("IP sudah di dalam database.");
                    makeRed(labelMessage);
                } else {
                    if (input.length() != 10) {
                        labelMessage.setText("IP harus 8 angka.");
                        makeRed(labelMessage);
                    } else {
                        labelMessage.setText("IP valid.");
                        makeGreen(labelMessage);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(IPFocusListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
//                try {
//            String input = getField().getText();
//            Label labelMessage = getLabel();
//            if (newValue) {
//                if (checkMember(input)) {
//                    labelMessage.setText("IP sudah di dalam database.");
//                    makeRed(labelMessage);
//                } else {
//                    if (input.length() != 10) {
//                        labelMessage.setText("IP harus 8 angka.");
//                        makeRed(labelMessage);
//                    } else {
//                        labelMessage.setText("IP valid.");
//                        makeGreen(labelMessage);
//
//                    }
//                }
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

}
