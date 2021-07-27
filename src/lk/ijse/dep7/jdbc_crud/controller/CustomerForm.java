package lk.ijse.dep7.jdbc_crud.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.dep7.jdbc_crud.tm.CustomerTM;

public class CustomerForm {
    public TableView<CustomerTM> tblCustomers;
    public TextField txtID;
    public TextField txtNIC;
    public TextField txtName;
    public TextField txtAddress;
    public Button btnSave;
    public Button btnDelete;
    public Button btnClear;
    public TextField txtSearch;

    public void initialize(){
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("nic"));

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            String id = txtID.getText();
            String name = txtName.getText();
            String address = txtAddress.getText();
            String nic = txtNIC.getText();

            btnSave.setDisable (!(id.matches("C\\d{3}") &&
                name.matches("[A-Za-z ]{3,}") &&
                address.matches(".{4,}") &&
                nic.matches("\\d{9}[Vv]")));
        };

        txtID.textProperty().addListener(listener);
        txtName.textProperty().addListener(listener);
        txtAddress.textProperty().addListener(listener);
        txtNIC.textProperty().addListener(listener);

        btnSave.setDefaultButton(true);
        btnSave.setDisable(true);
        btnDelete.setDisable(true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {

            /* Todo: Find the appropriate customer(s) from the DB and list them in the table */
        });

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCustomer) -> {
            if (selectedCustomer != null){
                txtID.setText(selectedCustomer.getId());
                txtNIC.setText(selectedCustomer.getNic());
                txtName.setText(selectedCustomer.getName());
                txtAddress.setText(selectedCustomer.getAddress());
                txtID.setDisable(true);
                btnDelete.setDisable(false);
                btnSave.setText("Update");
            }else{
                btnSave.setText("Save");
                btnDelete.setDisable(true);
            }
        });
    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String id = txtID.getText();
        String nic = txtNIC.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();

        if (btnSave.getText().equals("Save")) {

            /* Todo: Save the customer in DB */

            if (true) {
                tblCustomers.getItems().add(new CustomerTM(id, nic, name, address));
                btnClear.fire();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer").show();

                /* Todo: Transfer the focus to right text field */
            }
        }else{

            /* Todo: Update the customer in DB */

            if (true) {
                CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();

                selectedCustomer.setNic(nic);
                selectedCustomer.setName(name);
                selectedCustomer.setAddress(address);
                tblCustomers.refresh();
                btnClear.fire();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer").show();

                /* Todo: Transfer the focus to right text field */
            }

        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();

        // Todo: Delete the customer in DB

        if (true){
            tblCustomers.getItems().remove(selectedCustomer);
            tblCustomers.refresh();
            btnClear.fire();
        }else{
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer").show();
        }
    }

    public void btnClear_OnAction(ActionEvent actionEvent) {
        txtID.clear();
        txtNIC.clear();
        txtName.clear();
        txtAddress.clear();
        txtSearch.clear();
        txtID.setDisable(false);
        tblCustomers.getSelectionModel().clearSelection();
        txtID.requestFocus();
    }
}
