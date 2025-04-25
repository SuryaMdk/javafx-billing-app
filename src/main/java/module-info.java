module com.str.billing {
	requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
	requires java.sql;
	requires javafx.fxml;
	requires java.desktop;
	requires com.github.librepdf.openpdf;
	requires org.apache.poi.ooxml;

	opens com.str.billing.controller to javafx.fxml;
	opens com.str.billing.model to javafx.base;

    exports com.str.billing;
    exports com.str.billing.controller;
    exports com.str.billing.model;
}
