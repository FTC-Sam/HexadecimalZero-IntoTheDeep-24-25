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
                take();
                manualHoriSlides();
                resetHori();
                break;

            case EXTENSION:
                //claw code



        }
        moveVertiSlides();
        if (!isVertiManual) vertiSlides.update();

    }





    //extension related

    public void setHeight() { //gamepad2 up, left, right, down, x
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

    public void moveVertiSlides() { //gamepad2 right bumper, left bumper
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
        if (horiSlides.isReset() && timer1.seconds() > 0.5) {
            setHeight();
        }
    }

    public void armDeposit() {
        //if (vertiSlides.getCurrentPos() > )
    }






    //---------------------------------------------------------------------------------------------
    //ground related
    public void manualHoriSlides() { //gamepad1 right bumper, left bumper
        if (gamepad1.right_bumper) {
            horiSlides.manualOut();
        }
        if (gamepad1.left_bumper) {
            horiSlides.manualIn();
        }
    }
    public void take() { //gamepad1 a, x
        if ((horiSlides.getPosition() >= horiThreshold) && gamepad1.a && timer2.seconds() > 0.3) {
            box.intake();
        }
        else if ((horiSlides.getPosition() >= horiThreshold) && gamepad1.x && timer2.seconds() > 0.3) {
            box.outtake();
        }
        else if (gamepad1.a || gamepad1.x) {
            horiSlides.setPosition(horiThreshold);
            timer2.reset();
            timer2.startTime();
        }
        else {
            box.rest();
        }
    }

    public void resetHori() { //gamepad1 b
        if (gamepad1.b) {
            horiSlides.in();
            timer1.reset();
            timer1.startTime();
        }
    }
}
