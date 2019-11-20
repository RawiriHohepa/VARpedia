package application.logic;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertBuilder {
    private static final AlertType DEFAULT_ALERT_TYPE = AlertType.INFORMATION;
    private static final String DEFAULT_TITLE = null;
    private static final String DEFAULT_HEADER_TEXT = null;
    private static final String DEFAULT_CONTENT_TEXT = null;

    private AlertType _alertType;
    private String _title;
    private String _headerText;
    private String _contentText;

    public AlertBuilder() {
        _alertType = DEFAULT_ALERT_TYPE;
        _title = DEFAULT_TITLE;
        _headerText = DEFAULT_HEADER_TEXT;
        _contentText = DEFAULT_CONTENT_TEXT;
    }

    public Alert getResult() {
        Alert alert = new Alert(_alertType);
        alert.getDialogPane().getStylesheets().add(("Alert.css"));
        alert.setTitle(_title);
        alert.setHeaderText(_headerText);
        alert.setContentText(_contentText);
        return alert;
    }

    public AlertBuilder setAlertType(AlertType alertType) {
        _alertType = alertType;
        return this;
    }

    public AlertBuilder setTitle(String title) {
        _title = title;
        return this;
    }

    public AlertBuilder setHeaderText(String headerText) {
        _headerText = headerText;
        return this;
    }

    public AlertBuilder setContentText(String contentText) {
        _contentText = contentText;
        return this;
    }
}
