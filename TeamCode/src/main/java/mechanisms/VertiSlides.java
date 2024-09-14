package mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;


public class VertiSlides {
    private final DcMotorEx slideLeft;
    private final DcMotorEx slideRight;
    private int targetPos;
    private final ElapsedTime timer = new ElapsedTime();
    private double integralSum = 0;
    private double lastError = 0;
    private final Telemetry telemetry;

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
        int tolerance = 30;
        if ((slideRight.getCurrentPosition() - targetPos) < -tolerance) {
            slideLeft.setPower(returnPowerUp());
            slideRight.setPower(returnPowerUp());
        }
        else if ((slideRight.getCurrentPosition() - targetPos) > tolerance) {
            slideLeft.setPower(returnPowerDown());
            slideRight.setPower(returnPowerDown());
        }
        else {
            slideLeft.setPower(0);
            slideRight.setPower(0);
        }
    }

    public void manualUp() {
        slideLeft.setPower(0.7);
        slideRight.setPower(0.7);
    } //when running manual, the update method needs to be halted

    public void manualDown() {
        slideLeft.setPower(-0.5);
        slideRight.setPower(-0.5);
    }

    public void setTargetPos(int targetPos) {
        this.targetPos = targetPos;
    }

    public int getCurrentPos() {
        return slideRight.getCurrentPosition();
    }

    private double returnPowerUp(){
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

    private double returnPowerDown(){
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