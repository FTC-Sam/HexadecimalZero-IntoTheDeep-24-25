package mechanisms;


import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Crane { //I got rid of hardwareMap variable and wanna try it as a disposable
                     //constructor variable since it's only needed during initialization
    private final Telemetry telemetry;
    private final Box box;
    private final HoriSlides horiSlides;
    private final VertiSlides vertiSlides;
    private final Gamepad gamepad1;
    private final Gamepad gamepad2;
    private final int down = 0;
    private final int lowBucket = 1000;
    private final int highBucket = 2000;
    private final int lowBar = 500;
    private final int highBar = 1500;
    private final int climbHeight = 2000;
    private boolean isVertiManual = false;
    private enum CraneStates{
        EXTENSION,
        GROUND,
        CLIMB
    }
    private final ElapsedTime timer1 = new ElapsedTime();
    private final ElapsedTime timer2 = new ElapsedTime();

    CraneStates currentState = CraneStates.GROUND;
    private final double horiThreshold = 0.1;
    private final int vertiThreshold = 2000;

    public Crane(HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1, Gamepad gamepad2) {

        this.telemetry = telemetry;
        box = new Box(hardwareMap, this.telemetry);
        horiSlides = new HoriSlides(hardwareMap, this.telemetry);
        vertiSlides = new VertiSlides(hardwareMap, this.telemetry);
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;

    }

    public void executeTeleOp() {
        switch (currentState) {
            case GROUND:
                boxTake();
                manualHoriSlides();
                moveHoriSlides();
                break;

            case EXTENSION:
                setArm();
                //claw code



        }
        manualVertiSlides();
        moveVertiSlides();
        if (!isVertiManual) vertiSlides.update();
    }





    //all mode related

    public void moveVertiSlides() { //gamepad2 up, left, right, down, x
        if (horiSlides.isReset() && timer1.seconds() > 0.5) {
            if (gamepad2.dpad_up) {
                vertiSlides.setTargetPos(highBucket);
                currentState = CraneStates.EXTENSION;
            }
            if (gamepad2.dpad_left) {
                vertiSlides.setTargetPos(lowBucket);
                currentState = CraneStates.EXTENSION;
            }
            if (gamepad2.dpad_right) {
                vertiSlides.setTargetPos(highBar);
                currentState = CraneStates.EXTENSION;
            }
            if (gamepad2.x) {
                vertiSlides.setTargetPos(lowBar);
                currentState = CraneStates.EXTENSION;
            }
            if (gamepad2.dpad_down) {
                vertiSlides.setTargetPos(down);
            }
        }
    }

    public void manualVertiSlides() { //gamepad2 right_stick_y
        if (-gamepad2.right_stick_y > 0.2) {
            vertiSlides.manualUp();
            isVertiManual = true;
        }
        else if (-gamepad2.right_stick_y < -0.2) {
            vertiSlides.manualDown();
            isVertiManual = true;
        }
        else if (isVertiManual){
            isVertiManual = false;
            vertiSlides.setTargetPos(vertiSlides.getCurrentPos());
        }
    }


    //---------------------------------------------------------------------------------------------
    //extension mode related

    public void setArm() {
        if((vertiSlides.getTargetPos() == highBucket) || (vertiSlides.getTargetPos() == lowBucket)){ //can add and here to threshold arm flipping
            box.depositPosition();
        }
        if(vertiSlides.getTargetPos() == down) {
            box.rest();
        }
    }








    //---------------------------------------------------------------------------------------------
    //ground mode related
    public void manualHoriSlides() { //gamepad2 left_stick_y
        if (-gamepad2.left_stick_y > 0.2) {
            horiSlides.manualOut();
        }
        if (-gamepad2.left_stick_y < -0.2) {
            horiSlides.manualIn();
        }
    }
    public void boxTake() { //gamepad1 a, x
        if ((horiSlides.getPosition() >= horiThreshold) && gamepad1.a && timer2.seconds() > 0.3) {
            box.intake();
        }
        else if ((horiSlides.getPosition() >= horiThreshold) && gamepad1.x && timer2.seconds() > 0.3) {
            box.outtake();
        }
        else {
            box.rest();
        }
    }

    public void moveHoriSlides() { //gamepad2 y, b
        if (gamepad2.b) {
            horiSlides.in();
            timer1.reset();
            timer1.startTime();
        }
        else if (gamepad2.y) {
            horiSlides.setPosition(horiThreshold);
            timer2.reset();
            timer2.startTime();
        }
    }
}
