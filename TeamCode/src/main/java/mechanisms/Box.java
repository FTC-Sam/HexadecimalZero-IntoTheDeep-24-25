package mechanisms;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Box {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad1;
    private CRServoImplEx spin;
    private ServoImplEx hinge;
    private ElapsedTime timer;
    private double motorPow = 0.85;
    private double armPos = 0.0;
    private double bottomPos = 0.302;
    private int posIndex = 0;
    public Box(HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.gamepad1 = gamepad1;

        spin = (CRServoImplEx) hardwareMap.servo.get("spin");
        hinge = (ServoImplEx) hardwareMap.servo.get("hinge");

        timer = new ElapsedTime();
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

    public void deposit() {
        hinge.setPosition(1);
        spin.setPower(-0.5);
    }



}