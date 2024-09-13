package mechanisms;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

public class HoriSlides {

    private final ServoImplEx servoLeft;
    private final ServoImplEx servoRight;
    public HoriSlides(HardwareMap hardwareMap) {
        servoLeft = (ServoImplEx) hardwareMap.servo.get("servoLeft");
        servoRight = (ServoImplEx) hardwareMap.servo.get("servoRight");
        servoRight.setDirection(Servo.Direction.REVERSE);
    }

    public void manual(boolean out, boolean in) {
        if (in) {
            servoLeft.setPosition(servoLeft.getPosition()-0.01);
            servoRight.setPosition(servoRight.getPosition()-0.01);
        }
        else if (out) {
            servoLeft.setPosition(servoLeft.getPosition()+0.01);
            servoRight.setPosition(servoRight.getPosition()+0.01);
        }
    }

    public void out() {
        servoLeft.setPosition(1);
        servoRight.setPosition(1);
    }
    public void in() {
        servoLeft.setPosition(0);
        servoRight.setPosition(0);
    }
}
