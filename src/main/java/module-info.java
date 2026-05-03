module drinkshop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;

    requires org.controlsfx.controls;

    opens drinkshop to javafx.fxml, javafx.graphics;
    exports drinkshop;

    opens drinkshop.domain;
    exports drinkshop.domain;
    exports drinkshop.ui;
    opens drinkshop.ui to javafx.fxml;

    exports drinkshop.repository;
    opens drinkshop.repository;
    exports drinkshop.service;
    opens drinkshop.service;
}