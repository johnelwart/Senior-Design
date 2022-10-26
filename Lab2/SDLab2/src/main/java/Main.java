//package main;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello world");

        SerialPort[] ports = SerialPort.getCommPorts();

        for (SerialPort port: ports) {
            System.out.println(port.getSystemPortName());
        }
        SerialPort comPort = SerialPort.getCommPorts()[0];
        comPort.openPort();
        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() { return SerialPort.LISTENING_EVENT_DATA_AVAILABLE; }
            @Override
            public void serialEvent(SerialPortEvent event)
            {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE)
                    return;
                byte[] newData = new byte[comPort.bytesAvailable()];
                //System.out.println(comPort.readBytes());
                int numRead = comPort.readBytes(newData, newData.length);
                //System.out.println("Read " + numRead + " bytes.");
                String response = new String(newData, 0, numRead);
                System.out.println(response);
            }
        });
    }

}