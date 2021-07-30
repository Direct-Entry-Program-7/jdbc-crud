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

import java.sql.*;

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
    private Connection connection;
    private Statement stmSearch;

    public void initialize() {
        tblCustomers.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        tblCustomers.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        tblCustomers.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("address"));
        tblCustomers.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("nic"));

        ChangeListener<String> listener = (observable, oldValue, newValue) -> {
            String id = txtID.getText();
            String name = txtName.getText();
            String address = txtAddress.getText();
            String nic = txtNIC.getText();

            btnSave.setDisable(!(id.matches("C\\d{3}") &&
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

        tblCustomers.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedCustomer) -> {
            if (selectedCustomer != null) {
                txtID.setText(selectedCustomer.getId());
                txtNIC.setText(selectedCustomer.getNic());
                txtName.setText(selectedCustomer.getName());
                txtAddress.setText(selectedCustomer.getAddress());
                txtID.setDisable(true);
                btnDelete.setDisable(false);
                btnSave.setText("Update");
            } else {
                btnSave.setText("Save");
                btnDelete.setDisable(true);
            }
        });

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dep7", "root", "mysql");
            stmSearch = connection.createStatement();
        } catch (SQLException | ClassNotFoundException ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to connect to the database server.").showAndWait();
            ex.printStackTrace();
            System.exit(1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }));

        try {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM customer");

            while (rst.next()) {
                String id = rst.getString("id");
                String nic = rst.getString("nic");
                String name = rst.getString(3);
                String address = rst.getString(4);
                tblCustomers.getItems().add(new CustomerTM(id, nic, name, address));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            try {

                String sql = "SELECT * FROM customer WHERE id LIKE '%QUERY%' OR name LIKE '%QUERY%' OR address LIKE '%QUERY%' OR nic LIKE '%QUERY%';";
                sql = sql.replaceAll("QUERY", newValue);
//                String sql = "SELECT * FROM customer WHERE id LIKE '%%%1$s%%' OR name LIKE '%%%1$s%%' OR address LIKE '%%%1$s%%' OR nic LIKE '%%%1$s%%';";
//                String sql = "SELECT * FROM customer WHERE id LIKE CONCAT('%%','%1$s','%%') OR name LIKE CONCAT('%%','%1$s','%%') OR address LIKE CONCAT('%%','%1$s','%%') OR nic LIKE CONCAT('%%','%1$s','%%');";
//                sql = String.format(sql, newValue);
//                System.out.println(sql);

                ResultSet rst = stmSearch.executeQuery(sql);
//                ResultSet rst = stmSearch.executeQuery("SELECT * FROM customer WHERE id LIKE '%" + newValue + "%' OR name LIKE '%" + newValue + "%' OR address LIKE '%" + newValue + "%' OR nic LIKE '%" + newValue + "%';");

                tblCustomers.getItems().clear();
                while (rst.next()) {
                    String id = rst.getString("id");
                    String nic = rst.getString("nic");
                    String name = rst.getString(3);
                    String address = rst.getString(4);
                    tblCustomers.getItems().add(new CustomerTM(id, nic, name, address));
                }
            } catch (SQLException e) {
                new Alert(Alert.AlertType.ERROR, "Failed to fetch the data, try again").show();
                e.printStackTrace();
            }
        });

    }

    public void btnSave_OnAction(ActionEvent actionEvent) {
        String id = txtID.getText();
        String nic = txtNIC.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();

        if (btnSave.getText().equals("Save")) {

            try {
                Statement stm = connection.createStatement();
                String sql;

                sql = "SELECT id FROM customer WHERE id='" + id + "'";
                if (stm.executeQuery(sql).next()) {
                    new Alert(Alert.AlertType.ERROR, "Customer ID already exists").show();
                    txtID.requestFocus();
                    return;
                }

                sql = "SELECT nic FROM customer WHERE nic='" + nic + "'";
                if (stm.executeQuery(sql).next()) {
                    new Alert(Alert.AlertType.ERROR, "NIC already exists").show();
                    txtNIC.requestFocus();
                    return;
                }

                sql = "INSERT INTO customer VALUES ('%s','%s','%s','%s');";
                sql = String.format(sql, id, nic, name, address);
                int affectedRows = stm.executeUpdate(sql);

                if (affectedRows == 1) {
                    tblCustomers.getItems().add(new CustomerTM(id, nic, name, address));
                    btnClear.fire();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to save the customer, retry").show();
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
//                String errMsg = "Failed to save the customer";
//                switch (ex.getErrorCode()){
//                    case 1062:
//                        errMsg = "Duplicate record, please check the customer ID and NIC";
//                }
                new Alert(Alert.AlertType.ERROR, "Failed to save the customer, retry").show();
            }
        } else {

            try {

                Statement stm = connection.createStatement();
                String sql = "SELECT * FROM customer WHERE nic = '" + nic + "';";
                ResultSet rst = stm.executeQuery(sql);
                //String sql = "SELECT * FROM customer WHERE nic = '"+ nic + "' AND id <> '" + id +"' ;";
                if (rst.next() && !rst.getString("id").equals(id)) {
                    new Alert(Alert.AlertType.ERROR, "NIC already exists").show();
                    txtNIC.requestFocus();
                    return;
                }

                sql = "UPDATE customer SET name='%s', address='%s', nic='%s' WHERE id='%s'";
                sql = String.format(sql, name, address, nic, id);
                int affectedRows = stm.executeUpdate(sql);
//                System.out.println(affectedRows);

                if (affectedRows == 1) {
                    CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();

                    selectedCustomer.setNic(nic);
                    selectedCustomer.setName(name);
                    selectedCustomer.setAddress(address);
                    tblCustomers.refresh();
                    btnClear.fire();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to update the customer, retry").show();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to update the customer, retry").show();
            }

        }
    }

    public void btnDelete_OnAction(ActionEvent actionEvent) {
        CustomerTM selectedCustomer = tblCustomers.getSelectionModel().getSelectedItem();

        try {
            Statement stm = connection.createStatement();
            int affectedRows = stm.executeUpdate("DELETE FROM customer WHERE id='" + selectedCustomer.getId() + "'");

            if (affectedRows == 1) {
                tblCustomers.getItems().remove(selectedCustomer);
                tblCustomers.refresh();
                btnClear.fire();
            } else {
                new Alert(Alert.AlertType.ERROR, "Failed to delete the customer, retry").show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to delete the customer, retry").show();
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
