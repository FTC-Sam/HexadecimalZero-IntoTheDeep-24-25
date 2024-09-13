package mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class VertiSlides {
    private final DcMotorEx slideLeft;
    private final DcMotorEx slideRight;
    public int targetPos;
    private final ElapsedTime timer = new ElapsedTime();
    private double integralSum = 0;
    private double lastError = 0;
    final private Telemetry telemetry;

    public VertiSlides(HardwareMap hardwareMap, Telemetry telemetry) {
        slideLeft = (DcMotorEx) hardwareMap.dcMotor.get("slideLeft");
        slideRight = (DcMotorEx) hardwareMap.dcMotor.get("slideRight");

        slideLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        slideRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        slideRight.setDirection(DcMotorSimple.Direction.REVERSE);

        this.telemetry = telemetry;
        targetPos = 0;
    }

    public void update() {
        if (Math.abs(slideRight.getCurrentPosition() - targetPos) > 30) {
            slideLeft.setPower(returnPower());
            slideRight.setPower(returnPower());
        }
        else {
            slideLeft.setPower(0);
            slideRight.setPower(0);
        }
    }

    private double returnPower(){
        double currentPos = slideRight.getCurrentPosition();
        double error = targetPos - currentPos;
        integralSum += error * timer.seconds();
        double derivative = (error - lastError)/ timer.seconds();
        lastError = error;
        timer.reset();
        telemetry.addData("error: ", error);
        double kp = 0.0037;
        double ki = 0.000000375;
        double kd = 0.0;
        return ((error * kp) + (derivative * kd) + (integralSum * ki));
    }

}