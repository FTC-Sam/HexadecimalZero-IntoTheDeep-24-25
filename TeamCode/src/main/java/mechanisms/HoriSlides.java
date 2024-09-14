package mechanisms;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class HoriSlides {

    private final ServoImplEx servoLeft;
    private final ServoImplEx servoRight;
    private final Telemetry telemetry;
    public HoriSlides(HardwareMap hardwareMap, Telemetry telemetry) {
        servoLeft = (ServoImplEx) hardwareMap.servo.get("servoLeft");
        servoRight = (ServoImplEx) hardwareMap.servo.get("servoRight");
        servoRight.setDirection(Servo.Direction.REVERSE);
        this.telemetry = telemetry;
    }

    public void manualIn() {
        servoLeft.setPosition(servoLeft.getPosition()-0.01);
        servoRight.setPosition(servoRight.getPosition()-0.01);
    }

    public void manualOut() {
        servoLeft.setPosition(servoLeft.getPosition()+0.01);
        servoRight.setPosition(servoRight.getPosition()+0.01);
    }

    public void out() {
        servoLeft.setPosition(1);
        servoRight.setPosition(1);
    }
    public void in() {
        servoLeft.setPosition(0);
        servoRight.setPosition(0);
    }

    public boolean isReset() {
        return servoLeft.getPosition() == 0;
    }
}
