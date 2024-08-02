module com.jingcaisky.dictatewords {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi.ooxml;
    requires poi;
    requires jacob;
    requires java.sql;


    opens com.jingcaisky.dictatewords to javafx.fxml;
    exports com.jingcaisky.dictatewords;
    exports com.jingcaisky.dictatewords.domain;
}