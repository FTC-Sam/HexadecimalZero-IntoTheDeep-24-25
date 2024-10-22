package TeleOp;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServoImplEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;

import mechanisms.Box;
import mechanisms.Drivetrain;


@TeleOp(name = "PrepTeleOp")
@Config
public class PrepTeleOp extends LinearOpMode {



    //DO NOT MODIFY THIS CLASS WHATSOEVER UNLESS AUTHORIZED-> MAKE A COPY
    private Drivetrain drivetrain;
    private CRServoImplEx spin;
    private ServoImplEx hinge;

    private ServoImplEx servoLeft;
    private ServoImplEx servoRight;




    private void initialize() {

        drivetrain = new Drivetrain(hardwareMap, telemetry, gamepad1);
        spin = (CRServoImplEx) hardwareMap.crservo.get("spin");
        hinge = (ServoImplEx) hardwareMap.servo.get("hinge");
        servoLeft = (ServoImplEx) hardwareMap.servo.get("servoLeft");
        servoRight = (ServoImplEx) hardwareMap.servo.get("servoRight");
        servoRight.setDirection(Servo.Direction.REVERSE);

    }




    @Override
    public void runOpMode() {
        initialize();
        while (opModeInInit()) {
        }
        while (opModeIsActive()) {
            /*drivetrain.drive();
            if (gamepad1.a) spin.setPower(-1);
            else if (gamepad1.b) spin.setPower(1);
            else spin.setPower(0);
            if (gamepad1.x) hinge.setPosition(0.431);

            if (gamepad1.y) hinge.setPosition(0.559); //0.559 intake
            //0.431 rest
               */
            if (gamepad1.dpad_up) {
                servoLeft.setPosition(1);
                servoRight.setPosition(1);
            }
            if (gamepad1.dpad_down) {
                servoLeft.setPosition(0);
                servoRight.setPosition(0);
            }


            telemetry.update();

        }

    }

}