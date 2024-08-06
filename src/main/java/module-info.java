module com.jingcaisky.dictatewords {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi.ooxml;
    requires poi;
    requires jacob;
    requires java.sql;


    exports com.jingcaisky.dictatewords.domain;
    exports com.jingcaisky.dictatewords.controller;
    opens com.jingcaisky.dictatewords.controller to javafx.fxml;
    exports com.jingcaisky.dictatewords;
    opens com.jingcaisky.dictatewords to javafx.fxml;
}