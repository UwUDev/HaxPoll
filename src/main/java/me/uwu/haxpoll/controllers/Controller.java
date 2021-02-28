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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
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
    public Label errorStaus;
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
        errorStaus.setText("");

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

        resetThread();
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

    public void run(MouseEvent mouseEvent) throws InterruptedException, IOException {
        startHaxxing();
    }

    public void startHaxxing() throws InterruptedException, IOException {
        if (!running) {
            if (checkFields()) {
                if (comboBox.getValue().equals("Cookies checking") || comboBox.getValue().equals("No checking")) {
                    mainBtn.setStyle("-fx-background-color: #d32f2f");
                    mainBtn.setText("Error");
                    running = true;

                    resetThread();

                    t.url = url.getText();
                    t.target = Integer.parseInt(quantity.getText());
                    t.boxSlot = String.valueOf(comboSlot.getValue());
                    t.cancel = false;
                    t.secure = false;
                    t.start();

                    System.out.println("?");
                    Thread.sleep(500);
                    mainBtn.setStyle("-fx-background-color: #ffb300");
                    mainBtn.setText("Stop");
                }
                if (comboBox.getValue().equals("Ip checking")) {
                    mainBtn.setStyle("-fx-background-color: #d32f2f");
                    mainBtn.setText("Error");
                    running = true;

                    t.url = url.getText();
                    t.target = Integer.parseInt(quantity.getText());
                    t.boxSlot = String.valueOf(comboSlot.getValue());
                    t.cancel = false;
                    t.secure = true;

                    Thread.sleep(500);
                    mainBtn.setStyle("-fx-background-color: #ffb300");
                    mainBtn.setText("Stop");
                    Checker.run(proxyArea.getText(), t);
                }
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

    private boolean checkFields(){
        boolean status = true;
        if (quantity.getText().isEmpty()) {
            quantity.setFocusColor(Color.web("#d32f2f"));
            errorStaus.setText("Empty quantity");
            status = false;
            quantity.requestFocus();
        } else try { Integer.parseInt(quantity.getText()); }
        catch (Exception ignored) {
            quantity.setFocusColor(Color.web("#d32f2f"));
            errorStaus.setText("Invalid quantity");
            status = false;
            quantity.requestFocus();
        }

        if (url.getText().isEmpty()) {
            errorStaus.setText("Empty URL");
            status = false;
            url.setStyle("-jfx-focus-color: #d32f2f");
            url.requestFocus();
        } else if (!url.getText().startsWith("http")){
            errorStaus.setText("Not an URL");
            status = false;
            url.setStyle("-jfx-focus-color: #d32f2f");
            url.requestFocus();
        }else if (!(url.getText().startsWith("https://www.strawpoll.me/") || url.getText().startsWith("http://www.strawpoll.me/"))){
            errorStaus.setText("Unsupported URL");
            status = false;
            url.setStyle("-jfx-focus-color: #d32f2f");
            url.requestFocus();
        }

        return status;
    }

    public void updateURLColor(KeyEvent keyEvent) {
        url.setStyle("-jfx-focus-color: #8700ff");
    }

    public void updateQuantityColor(KeyEvent keyEvent) {
        quantity.setFocusColor(Color.web("#8700ff"));
    }

    public void resetThread(){
        t = new HaxPoll() {
            @Override
            public void finishCallback() {
                try {
                    startHaxxing();
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void updateStats(boolean error) {
                if (error) {
                    Controller.this.error.getAndIncrement();
                } else {
                    Controller.this.good.getAndIncrement();
                }

                Controller.this.updateStats();
            }
        };
    }
}
