package com.udacity.commonservices.securityservice.application;


import com.udacity.commonservices.securityservice.data.AlarmStatus;

/**
 * Identifies a component that should be notified whenever the system status changes
 */
public interface StatusListener {
    void notify(AlarmStatus status);
    void catDetected(boolean catDetected);
    void sensorStatusChanged();
}
