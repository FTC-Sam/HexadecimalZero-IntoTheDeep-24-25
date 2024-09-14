package mechanisms;


import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;

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

    CraneStates currentState = CraneStates.GROUND;

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

        }
        moveVertiSlides();
        if (!isVertiManual) vertiSlides.update();
    }






    public void setHeight() {
        if(gamepad2.dpad_up){
            vertiSlides.setTargetPos(highBucket);
            currentState = CraneStates.EXTENSION;
        }
        if(gamepad2.dpad_left) {
            vertiSlides.setTargetPos(lowBucket);
            currentState = CraneStates.EXTENSION;
        }
        if(gamepad2.dpad_right){
            vertiSlides.setTargetPos(highBar);
            currentState = CraneStates.EXTENSION;
        }
        if(gamepad2.x) {
            vertiSlides.setTargetPos(lowBar);
            currentState = CraneStates.EXTENSION;
        }
        if(gamepad2.dpad_down) {
            vertiSlides.setTargetPos(down);
        }
    }

    public void moveVertiSlides() {
        if (gamepad2.right_bumper) {
            vertiSlides.manualUp();
            isVertiManual = true;
        }
        else if (gamepad2.left_bumper) {
            vertiSlides.manualDown();
            isVertiManual = true;
        }
        else if (isVertiManual){
            isVertiManual = false;
            vertiSlides.setTargetPos(vertiSlides.getCurrentPos());
        }
        if (horiSlides.isReset()) {
            setHeight();
        }
    }
}
