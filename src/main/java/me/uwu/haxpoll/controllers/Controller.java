package me.uwu.haxpoll.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import me.uwu.haxpoll.HaxPoll;
import me.uwu.haxpoll.proxy.Checker;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

public class Controller implements Initializable {
    public JFXButton mainBtn;
    public Label stats;
    public Label proxyLabel;
    public JFXTextArea proxyArea;
    public JFXTextField url;
    public JFXTextField quantity;
    public JFXComboBox comboSlot;
    private boolean running = false;
    private HaxPoll t;

    public AtomicInteger good = new AtomicInteger(0), error = new AtomicInteger(0);

    public JFXComboBox comboBox;

    private static Controller instance;

    public static Controller getInstance(){
        return instance;
    }

    public HaxPoll getMainThread(){
        return t;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "Ip checking",
                        "Cookies checking",
                        "No checking"
                );
        comboBox.setItems(options);
        comboBox.setValue("Cookies checking");

        List<String> optn = new ArrayList<>();
        for (int i = 1; i<=30; i++)
            optn.add(String.valueOf(i));

        ObservableList<String> options2 =
                FXCollections.observableArrayList(optn);
        comboSlot.setItems(options2);
        comboSlot.setValue("1");

        t = new HaxPoll(){
            @Override
            public void updateStats(boolean error) {
                if(error){
                    Controller.this.error.getAndIncrement();
                }else{
                    Controller.this.good.getAndIncrement();
                }

                Controller.this.updateStats();
            }

            @Override
            public void finishCallback() {
                mainBtn.setStyle("-fx-background-color: #8700ff");
                mainBtn.setText("Start");
            }
        };
    }

    public void modeChanged(ActionEvent actionEvent) {
        if (comboBox.getValue().equals("Ip checking")){
            proxyLabel.setText("Proxies :");
            proxyArea.setDisable(false);
        } else {
            proxyLabel.setText("Proxies disabled");
            proxyArea.setDisable(true);
        }

    }

    public void run (MouseEvent mouseEvent) throws InterruptedException, IOException {
        if (!running) {
            if (comboBox.getValue().equals("Cookies checking") || comboBox.getValue().equals("No checking")) {
                mainBtn.setStyle("-fx-background-color: #ffb300");
                mainBtn.setText("Starting...");
                running = true;

                t.url = url.getText();
                t.target = Integer.parseInt(quantity.getText());
                t.boxSlot = String.valueOf(comboSlot.getValue());
                t.cancel = false;
                t.secure = false;
                t.start();

                System.out.println("?");
                Thread.sleep(500);
                mainBtn.setStyle("-fx-background-color: #d32f2f");
                mainBtn.setText("Stop");
            }
            if (comboBox.getValue().equals("Ip checking")) {
                mainBtn.setStyle("-fx-background-color: #ffb300");
                mainBtn.setText("Starting...");
                running = true;

                t.url = url.getText();
                t.target = Integer.parseInt(quantity.getText());
                t.boxSlot = String.valueOf(comboSlot.getValue());
                t.cancel = false;
                t.secure = true;

                Thread.sleep(500);
                mainBtn.setStyle("-fx-background-color: #d32f2f");
                mainBtn.setText("Stop");
                Checker.run(proxyArea.getText(), t);
            }
        } else {
            mainBtn.setStyle("-fx-background-color: #8700ff");
            mainBtn.setText("Start");

            t.cancel = true;
            t.threads.forEach(Thread::interrupt);
            t.interrupt();

            running = false;
        }
    }

    private void updateStats(){
        Platform.runLater(() -> this.stats.setText("Good: " + good.get() + " / Errors: " + error.get()));
    }
}
