/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package helperclass;

import impro.AddMemberController;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static model.Database.checkMember;

public class ChildFocusListener extends FocusListener {

    private boolean traversed;
    private ChildFocusListener dependency;

    public ChildFocusListener(TextField field, Label label) {
        super(field, label);
        this.traversed = false;
        this.dependency = null;
    }

    public void setDependency(ChildFocusListener cfl) {
        this.dependency = cfl;
    }

    public ChildFocusListener getDependency() {
        return this.dependency;
    }

    public void declareTraversed() {
        this.traversed = true;
    }

    public void makeDependencyTraversed() {
        this.dependency.declareTraversed();
    }

    public String getInput() {
        return this.getField().getText();
    }

     @Override
    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        try {
            String input = getInput();
            String otherInput = getDependency().getInput();
            //When Node is receiving focus, check if the input is
            //valid and then set the other ChildFocusListener traversed = true.
            if (newValue && !input.isEmpty()) {
                doChildValidityCheck(input, otherInput);
                makeDependencyTraversed();
            }
            //When node is not receiving focus, check if the input is valid.
            //The method is called only if the Node's ChildFocusListener traversed = true.
            //Which will not be true if it has not been traversed(clicked).
            else if (input.isEmpty()) {
                // do nothing
            } 
            else {
                doChildValidityCheck(input, otherInput);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AddMemberController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Do check on this field's input and the other field's input if this field
     * is already traversed.
     *
     * @param input <tt>String</tt> obtained from this.field.getText()
     * @param otherInput <tt>String</tt> obtained from
     * this.dependency.getInput()
     * @throws SQLException
     */
    private void doChildValidityCheck(String input, String otherInput) throws SQLException {
        if (traversed) {
            if (checkMember(input) && input.length() == 10 && !otherInput.equals(input)) {
                getLabel().setText("IP yang dimasukkan valid.");
                getLabel().setStyle("-fx-text-fill: derive(green,70%)");
            } else {
                getLabel().setStyle("-fx-text-fill: red");
                if (input.length() < 10) {
                    getLabel().setText("IP harus diikuti 8 angka.");
                } else if (otherInput.equals(input)) {
                    getLabel().setText("Kaki 1 dan Kaki 2 duplikat");
                } else {
                    getLabel().setText("IP tidak ditemukkan di database");
                }
            }
        }
    }

}
