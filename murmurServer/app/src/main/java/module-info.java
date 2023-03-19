module org.helmo.reseau {
    requires com.google.gson;

    opens org.helmo.reseau.infrastructures to com.google.gson;
    exports org.helmo.reseau;
}