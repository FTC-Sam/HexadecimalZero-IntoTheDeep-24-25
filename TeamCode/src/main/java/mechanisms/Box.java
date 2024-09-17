package mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Box {
    private final Telemetry telemetry;
    private final CRServoImplEx spin;
    private final ServoImplEx hinge;

    public Box(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;

        spin = (CRServoImplEx) hardwareMap.servo.get("spin");
        hinge = (ServoImplEx) hardwareMap.servo.get("hinge");

    }

    public void rest() {
        spin.setPower(0.1);
        hinge.setPosition(0.6);
    }

    public void intake() {
        hinge.setPosition(0);
        spin.setPower(1);
    }

    public void outtake() {
        hinge.setPosition(0);
        spin.setPower(-1);
    }

    public void depositPosition() {
        hinge.setPosition(1);
    }
    public void deposit() {
        spin.setPower(-0.5);
    }

    public boolean isReset() {
        return hinge.getPosition() == 0.6;
    }
}