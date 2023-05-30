package com.udacity.commonservices.securityservice;

import com.udacity.commonservices.imageservice.ImageService;
import com.udacity.commonservices.securityservice.data.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.rekognition.endpoints.internal.Value;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)

class SecurityServiceTest {
    @Mock
    private ImageService imageService;

    @Mock
    private SecurityRepository securityRepository;

    private SecurityService securityService;

    Set<Sensor> sensors;

    @BeforeEach
    void init() {
        securityService = new SecurityService(securityRepository,imageService);
    //    MockitoAnnotations.initMocks(this);
        Sensor sensorA = new Sensor("sensorA", SensorType.DOOR);
        Sensor sensorB = new Sensor("sensorB", SensorType.WINDOW);
        sensors = new HashSet<>();
        sensors.add(sensorA);
        sensors.add(sensorB);
    }

    @AfterEach
    void destroy() {
        sensors = null;
    }

    /**
     * 1.If alarm is armed and a sensor becomes activated, put the system into pending alarm status.
     */

    @Test
    void whenAlarmOn_And_SensorActive_Then_SystemPending(){
      //  securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
     //   securityService.setAlarmStatus(AlarmStatus.NO_ALARM);

        //when( securityRepository.getSensors()).thenReturn(sensors);
        when( securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        securityService.changeSensorActivationStatus(sensorToActivate, true);
        ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMost(1)).setAlarmStatus(captor.capture());

        assertEquals(captor.getValue(), AlarmStatus.PENDING_ALARM);

    }

    /**
     *2. If alarm is armed and a sensor becomes activated and the system is already pending alarm,
     * set the alarm status to alarm.
     */
    @Test
    void whenAlarmOn_SensorActive_And_SystemPending_Then_AlarmStatus_Alarm(){
        when( securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        securityService.changeSensorActivationStatus(sensorToActivate, true);
        ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);

        verify(securityRepository, atMost(1)).setAlarmStatus(captor.capture());
        assertEquals(captor.getValue(), AlarmStatus.ALARM);

    }

    /**
     * 3. If pending alarm and all sensors are inactive, return to no alarm state.
     */
   @Test
    void whenPendingAlarm_And_AllSensorsInactive_Then_NoAlarmState(){
       when( securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
       // when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.DISARMED);
       Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
       sensorToActivate.setActive(true);
       securityService.changeSensorActivationStatus(sensorToActivate, false);

       ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);
       verify(securityRepository, atMost(1)).setAlarmStatus(captor.capture());

       assertEquals(captor.getValue(), AlarmStatus.NO_ALARM);

    }

    /**
     * 4. If alarm is active, change in sensor state should not affect the alarm state.
     */

    @ParameterizedTest
    @ValueSource(booleans={true,false})
    void whenAlarmActive_Then_ChangeInSensorState_NotAffectAlarm(Boolean active){
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.ALARM);
        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        securityService.setAlarmStatus(AlarmStatus.ALARM);
        securityService.changeSensorActivationStatus(sensorToActivate, active);
        assertEquals(securityService.getAlarmStatus(), AlarmStatus.ALARM);

        sensorToActivate.setActive(true);
        securityService.changeSensorActivationStatus(sensorToActivate, active);
        assertEquals(securityService.getAlarmStatus(), AlarmStatus.ALARM);
    }
    /**
     * 5. If a sensor is activated while already active and the system is in pending state, change it to alarm state.
     */
    @Test
    void whenSensorActivated_whileAlreadyActive_And_SystemPending_Then_SetAlarm(){
        when(securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        sensorToActivate.setActive(true);
        securityService.changeSensorActivationStatus(sensorToActivate, true);
        ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMost(1)).setAlarmStatus(captor.capture());
        assertEquals(captor.getValue(),AlarmStatus.ALARM);
    }
    /**
     * 6. If a sensor is deactivated while already inactive, make no changes to the alarm state.
     */
    @Test
    void whenSensorDeactivated_whileAlreadyInactive_Then_NoChange(){
        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        sensorToActivate.setActive(false);
        securityService.changeSensorActivationStatus(sensorToActivate, false);
        verify(securityRepository,times(0)).setAlarmStatus(any());
    }
    /**
     * If the image service identifies an image containing a cat while the system is armed-home,
     * put the system into alarm status.
     */
    @Test
    void whenImage_identifiedas_cat_then_alarmStatus() {
        when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        when(imageService.imageContainsCat(any(), anyFloat())).thenReturn(true);
        BufferedImage catImage = new BufferedImage(1,1,1);
        securityService.processImage(catImage);
        ArgumentCaptor<AlarmStatus> captor = ArgumentCaptor.forClass(AlarmStatus.class);
        verify(securityRepository, atMost(1)).setAlarmStatus(captor.capture());
        assertEquals(captor.getValue(),AlarmStatus.ALARM);
    }

}
