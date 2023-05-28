package com.udacity.commonservices.securityservice;

import com.udacity.commonservices.imageservice.ImageService;
import com.udacity.commonservices.securityservice.data.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
        Sensor sensorA = new Sensor("sensorA", SensorType.DOOR);
        Sensor sensorB = new Sensor("sensorB", SensorType.WINDOW);
        sensors = new HashSet<>();
        sensors.add(sensorA);
        sensors.add(sensorB);
    }

    @Test
    void whenAlarmOn_And_SensorActive_Then_SystemPending(){
      //  securityService.setArmingStatus(ArmingStatus.ARMED_HOME);
     //   securityService.setAlarmStatus(AlarmStatus.NO_ALARM);

        //when( securityRepository.getSensors()).thenReturn(sensors);
        when( securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.NO_ALARM);
        when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);

        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        securityService.changeSensorActivationStatus(sensorToActivate, true);
        verify(securityRepository, atMost(1)).setAlarmStatus(eq(AlarmStatus.PENDING_ALARM));

    }

    @Test
    void whenAlarmOn_SensorActive_And_SystemPending_Then_AlarmStatus_Alarm(){
        when( securityRepository.getAlarmStatus()).thenReturn(AlarmStatus.PENDING_ALARM);
        when (securityRepository.getArmingStatus()).thenReturn(ArmingStatus.ARMED_HOME);
        Sensor sensorToActivate = (Sensor) (sensors.stream().toArray())[0];
        securityService.changeSensorActivationStatus(sensorToActivate, true);
        verify(securityRepository, atMost(1)).setAlarmStatus(eq(AlarmStatus.ALARM));
    }


}
