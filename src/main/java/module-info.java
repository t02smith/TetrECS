@SuppressWarnings("all")
module uk.ac.soton.comp1206 {
    requires javafx.controls;
    requires java.scripting;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires org.apache.logging.log4j;
    requires nv.websocket.client;
    exports uk.ac.soton.comp1206;
}