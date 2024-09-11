package mechanisms;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Box {
    private HardwareMap hardwareMap;
    private Telemetry telemetry;
    private Gamepad gamepad1;
    private Servo intakeServoL;
    private Servo intakeServoR;
    private DcMotorEx rollerMotor;
    private ElapsedTime timer = new ElapsedTime();
    private double motorPow = 0.85;
    private double armPos = 0.0;
    private double bottomPos = 0.302;
    private int posIndex = 0;
    public enum IntakeStates{
        INTAKE,
        EXTAKE,

        TOGGLE,
        GROUND


    }
    private IntakeStates currentState = IntakeStates.GROUND;

    public Box(HardwareMap hardwareMap, Telemetry telemetry, Gamepad gamepad1){
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.gamepad1 = gamepad1;


        //Motor Init
        rollerMotor = (DcMotorEx) hardwareMap.dcMotor.get("intake");
        rollerMotor.setDirection(DcMotorEx.Direction.REVERSE);

    }






    public IntakeStates getCurrentState(){
        return currentState;
    }

    public void setMotorPow(double n){
        motorPow = n;
    }





}