module imageservice {
    exports com.udacity.commonservices.imageservice;
    requires transitive org.slf4j;
    requires transitive software.amazon.awssdk.auth;
    requires transitive software.amazon.awssdk.core;
    requires transitive software.amazon.awssdk.regions;
    requires transitive software.amazon.awssdk.services.rekognition;
    requires java.desktop;
}