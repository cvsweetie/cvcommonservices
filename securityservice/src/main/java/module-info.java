module com.udacity.commonservices.securityservice {
    exports com.udacity.commonservices.securityservice;
    exports com.udacity.commonservices.securityservice.data;
    exports com.udacity.commonservices.securityservice.application;

    requires java.desktop;
    requires com.google.common;
    requires com.udacity.commonservices.imageservice;
    requires  org.junit.jupiter.api;
    requires  org.mockito;
    requires  org.junit.platform.commons;
    requires  org.mockito.junit.jupiter;
    requires  org.junit.jupiter.params;

//    opens org.junit.platform.commons.util to org.junit.platform.engine;
}