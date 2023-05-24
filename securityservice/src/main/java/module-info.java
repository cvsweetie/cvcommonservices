module securityservice {
    exports com.udacity.commonservices.securityservice;
    exports com.udacity.commonservices.securityservice.data;
    exports com.udacity.commonservices.securityservice.application;
    requires imageservice;
    requires java.desktop;
    requires com.google.common;

}