package org.firstinspires.ftc.teamcode.subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.teamcode.subsystems.settings.ConfigInfo;
import org.firstinspires.ftc.teamcode.util.Mechanism;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.tfod.TfodProcessor;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Camera extends Mechanism {
    private VisionPortal visionPortal;               // Used to manage the video source.
    private AprilTagProcessor aprilTag;              // Used for managing the AprilTag detection process.
    private AprilTagDetection desiredTag = null;     // Used to hold the data for a detected AprilTag
    boolean targetFound = false;
    final int exposureMS = 4;
    final int gain = 250;

    public static final int BLUE_LEFT_ID = 1;
    public static final int BLUE_CENTER_ID = 2;
    public static final int BLUE_RIGHT_ID = 3;
    public static final int RED_LEFT_ID = 4;
    public static final int RED_CENTER_ID = 5;
    public static final int RED_RIGHT_ID = 6;

    private TfodProcessor tfod;

    private static final String TFOD_MODEL_ASSET = "CenterStage.tflite";

    private static final String[] LABELS = {
        "Pixel"
    };

    @Override
    public void init(HardwareMap hwMap) {
        aprilTag = new AprilTagProcessor.Builder()
                .build();
        tfod = new TfodProcessor.Builder()
                .setModelAssetName(TFOD_MODEL_ASSET)
                .setModelLabels(LABELS)
                .build();
        visionPortal = new VisionPortal.Builder()
            .setCamera(hwMap.get(WebcamName.class, ConfigInfo.camera.getDeviceName()))
            .addProcessor(aprilTag)
            .addProcessor(tfod)
            .build();
        aprilTag.setDecimation(3);
        setManualExposure(exposureMS, gain);
        tfod.setMinResultConfidence(0.75f);
    }

    @Override
    public void telemetry(Telemetry telemetry) {
        telemetryTag(telemetry);
        telemetryTfod(telemetry);
    }

    public void setManualExposure(int exposureMS, int gain) {
        if (visionPortal == null || visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            return;
        }

        ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
        if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
            exposureControl.setMode(ExposureControl.Mode.Manual);
        }
        exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
        GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
        gainControl.setGain(gain);
    }

    public void checkAndSetDesiredTag(int desiredTagID) {
        List<AprilTagDetection> currentDetections = getDetections();
        boolean detectionFound = false; // Add this variable

        for (AprilTagDetection detection : currentDetections) {
            // Look to see if we have size info on this tag.
            if (detection.metadata != null) {
                // Check to see if we want to track towards this tag.
                if ((desiredTagID < 0) || (detection.id == desiredTagID)) {
                    // Yes, we want to use this tag.
                    targetFound = true;
                    desiredTag = detection;
                    detectionFound = true; // Set detectionFound to true
                    break;  // don't look any further.
                } else {
                    // This tag is in the library, but we do not want to track it right now.
                    // You can remove this line, as it's not necessary.
                }
            } else {
                // This tag is NOT in the library, so we don't have enough information to track to it.
                // You can remove this line, as it's not necessary.
            }
        }

        // After the loop, set targetFound based on detectionFound
        if (!detectionFound) {
            targetFound = false;
        }
    }

    public double[] getDesiredTagPoseData() {
        if (targetFound) {
            return new double[] {desiredTag.ftcPose.range, desiredTag.ftcPose.bearing, desiredTag.ftcPose.yaw};
        } else {
            return null;
        }
    }

    public VisionPortal.CameraState getCameraState() {
        return visionPortal.getCameraState();
    }

    public List<AprilTagDetection> getDetections() {
        return aprilTag.getDetections();
    }

    private void telemetryTag(Telemetry telemetry) {
        if (visionPortal.getCameraState() == VisionPortal.CameraState.STREAMING) {
            if (targetFound) {
                telemetry.addData("Tag ID ", desiredTag.id);
                telemetry.addData("Tag Name ", desiredTag.metadata.name);
                telemetry.addData("Tag X ", desiredTag.ftcPose.x);
                telemetry.addData("Tag Y ", desiredTag.ftcPose.y);
                telemetry.addData("Tag Z ", desiredTag.ftcPose.z);
                telemetry.addData("Tag Pitch ", desiredTag.ftcPose.pitch);
                telemetry.addData("Tag Roll ", desiredTag.ftcPose.roll);
                telemetry.addData("Tag Yaw ", desiredTag.ftcPose.yaw);
                telemetry.addData("Tag Range ", desiredTag.ftcPose.range);
                telemetry.addData("Tag Bearing ", desiredTag.ftcPose.bearing);
                telemetry.addData("Tag Elevation ", desiredTag.ftcPose.elevation);
            } else {
                telemetry.addLine("No TAG");
            }
        }
    }

    private void telemetryTfod(Telemetry telemetry) {

        List<Recognition> currentRecognitions = tfod.getRecognitions();
        telemetry.addData("# Objects Detected", currentRecognitions.size());

        // Step through the list of recognitions and display info for each one.
        for (Recognition recognition : currentRecognitions) {
            double x = (recognition.getLeft() + recognition.getRight()) / 2 ;
            double y = (recognition.getTop()  + recognition.getBottom()) / 2 ;

            telemetry.addData(""," ");
            telemetry.addData("Image", "%s (%.0f %% Conf.)", recognition.getLabel(), recognition.getConfidence() * 100);
            telemetry.addData("- Position", "%.0f / %.0f", x, y);
            telemetry.addData("- Size", "%.0f x %.0f", recognition.getWidth(), recognition.getHeight());
        }   // end for() loop

    }

    public int getTfodElementPos() {
        List<Recognition> currentRecognitions = tfod.getRecognitions();
        for (Recognition recognition : currentRecognitions) {
            if (recognition.getLabel().equals("Pixel")) {
                double x = (recognition.getLeft() + recognition.getRight()) / 2;
                double y = (recognition.getTop() + recognition.getBottom()) / 2;
                if (x < 213) {
                    return 1;
                } else if (x < 427) {
                    return 2;
                } else {
                    return 3;
                }
            }
        }
        return 1;
    }
}