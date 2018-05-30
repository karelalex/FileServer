package ru.naztrans.Filecloud.guiclient.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;


import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import ru.naztrans.Filecloud.common.*;
import ru.naztrans.Filecloud.guiclient.nonGuiServices.FileService;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    private ObservableList<FileView> fileList;
    private Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    @FXML
    private TableColumn<FileView, String> colFileName;

    @FXML
    private TableColumn<FileView, Long> colSize;
    @FXML
    private TableView<FileView> table;

    @FXML
    private TextField loginField;

    @FXML
    private TextField passField;

    @FXML
    private TextField secondPassFieald;

    @FXML
    private VBox authControls;

    @FXML

    private VBox fileControls;

    private boolean authorized;

    public void initialize(URL location, ResourceBundle resources) {
        fileList = FXCollections.observableArrayList();
        colFileName.setCellValueFactory(new PropertyValueFactory<FileView, String>("fileName"));
        colSize.setCellValueFactory(new PropertyValueFactory<FileView, Long>("size"));

        table.setItems(fileList);


    }
    public void uploadFile() {
        FileChooser fileChooser = new FileChooser();
        File f=fileChooser.showOpenDialog(table.getScene().getWindow());
        FileService.sendFile(f, out);


    }
    public void deleteFile() {
        FileView selected=table.getSelectionModel().getSelectedItem();
        if (selected!=null){
            FileService.deleteFile(selected.getFileName(), out);
        }
    }
    public void downloadFile(){
        FileView selected=table.getSelectionModel().getSelectedItem();
        if (selected!=null){
            FileService.askFile(selected.getFileName(), out);
        }
    }
    public void connect() {
        try {
            socket = new Socket("localhost", 8189);
            System.out.println("Подключился");
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());


            System.out.println("создал стримы");
            Thread t = new Thread(new Runnable() {
                public void run() {
                    try {

                        while (true) {

                            try {
                                Object obj=in.readObject();
                                if (obj instanceof AuthMsg) {
                                    if(((AuthMsg) obj).getAct()==AuthAction.LOGIN_SUCCESS){
                                        System.out.println("Авторизация успешна");
                                        setAuthorized(true);
                                        FileService.askFilelist(out);
                                    }
                                }
                                if (obj instanceof FileListMsg){
                                    FileListMsg message=(FileListMsg)obj;
                                    System.out.println("Обновляю лист файлов");
                                    Platform.runLater(()->{ //так не вылетае ошибка о неверном потоке
                                        fileList.clear();
                                        fileList.addAll(message.fileList);
                                            }

                                    );


                                }
                                if (obj instanceof FileClass) {
                                    FileClass fc = (FileClass)obj;
                                    FileChooser fileChooser = new FileChooser();
                                    fileChooser.setInitialFileName(fc.name);
                                    Platform.runLater(()->{
                                        File file=fileChooser.showSaveDialog(table.getScene().getWindow());
                                        FileService.saveFile(file, fc.body);
                                    });




                                }
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        //showAlert("Произошло отключение от сервера");
                        //setAuthorized(false);
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            t.setDaemon(true);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAuthorized(boolean b) {
        this.authorized = b;
        if (this.authorized) {
            authControls.setVisible(false);
            authControls.setManaged(false);
            fileControls.setVisible(true);
            fileControls.setManaged(true);

        } else {
            authControls.setVisible(true);
            authControls.setManaged(true);
            fileControls.setVisible(false);
            fileControls.setManaged(false);
            String loginnick = "";
        }
    }

    public void sendAuth() {
        connect();
        try {
            out.writeObject(new AuthMsg(AuthAction.SING_IN, loginField.getText(), passField.getText()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void enterNewUser(ActionEvent actionEvent) {
    }

    public void createNewUser(ActionEvent actionEvent) {
    }
}
