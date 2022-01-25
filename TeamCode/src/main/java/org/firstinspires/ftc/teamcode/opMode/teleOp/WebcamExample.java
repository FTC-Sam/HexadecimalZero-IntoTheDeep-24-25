/*
 * Copyright (c) 2019 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.opMode.teleOp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvPipeline;
import org.openftc.easyopencv.OpenCvWebcam;


@TeleOp
public class WebcamExample extends LinearOpMode
{
    OpenCvWebcam webcam;

    @Override
    public void runOpMode()
    {
        /*
         * Instantiate an OpenCvCamera object for the camera we'll be using.
         * In this sample, we're using a webcam. Note that you will need to
         * make sure you have added the webcam to your configuration file and
         * adjusted the name here to match what you named it in said config file.
         *
         * We pass it the view that we wish to use for camera monitor (on
         * the RC phone). If no camera monitor is desired, use the alternate
         * single-parameter constructor instead (commented out below)
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "camera"), cameraMonitorViewId);

        // OR...  Do Not Activate the Camera Monitor View
        //webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"));

        /*
         * Specify the image processing pipeline we wish to invoke upon receipt
         * of a frame from the camera. Note that switching pipelines on-the-fly
         * (while a streaming session is in flight) *IS* supported.
         */
        webcam.setPipeline(new kellen());

        /*
         * Open the connection to the camera device. New in v1.4.0 is the ability
         * to open the camera asynchronously, and this is now the recommended way
         * to do it. The benefits of opening async include faster init time, and
         * better behavior when pressing stop during init (i.e. less of a chance
         * of tripping the stuck watchdog)
         *
         * If you really want to open synchronously, the old method is still available.
         */
        webcam.setMillisecondsPermissionTimeout(2500); // Timeout for obtaining permission is configurable. Set before opening.
        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                /*
                 * Tell the webcam to start streaming images to us! Note that you must make sure
                 * the resolution you specify is supported by the camera. If it is not, an exception
                 * will be thrown.
                 *
                 * Keep in mind that the SDK's UVC driver (what OpenCvWebcam uses under the hood) only
                 * supports streaming from the webcam in the uncompressed YUV image format. This means
                 * that the maximum resolution you can stream at and still get up to 30FPS is 480p (640x480).
                 * Streaming at e.g. 720p will limit you to up to 10FPS and so on and so forth.
                 *
                 * Also, we specify the rotation that the webcam is used in. This is so that the image
                 * from the camera sensor can be rotated such that it is always displayed with the image upright.
                 * For a front facing camera, rotation is defined assuming the user is looking at the screen.
                 * For a rear facing camera or a webcam, rotation is defined assuming the camera is facing
                 * away from the user.
                 */
                webcam.startStreaming(320, 180, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {
                /*
                 * This will be called if the camera could not be opened
                 */
            }
        });

        telemetry.addLine("Waiting for start");
        telemetry.update();

        /*
         * Wait for the user to press start on the Driver Station
         */
        waitForStart();

        while (opModeIsActive())
        {
            /*
             * Send some stats to the telemetry
             */
            telemetry.addData("Frame Count", webcam.getFrameCount());
            telemetry.addData("FPS", String.format("%.2f", webcam.getFps()));
            telemetry.addData("Total frame time ms", webcam.getTotalFrameTimeMs());
            telemetry.addData("Pipeline time ms", webcam.getPipelineTimeMs());
            telemetry.addData("Overhead time ms", webcam.getOverheadTimeMs());
            telemetry.addData("Theoretical max FPS", webcam.getCurrentPipelineMaxFps());
            telemetry.update();
            FtcDashboard.getInstance().startCameraStream(webcam, 0);
            /*
             * NOTE: stopping the stream from the camera early (before the end of the OpMode
             * when it will be automatically stopped for you) *IS* supported. The "if" statement
             * below will stop streaming from the camera when the "A" button on gamepad 1 is pressed.
             */
            if(gamepad1.a)
            {
                /*
                 * IMPORTANT NOTE: calling stopStreaming() will indeed stop the stream of images
                 * from the camera (and, by extension, stop calling your vision pipeline). HOWEVER,
                 * if the reason you wish to stop the stream early is to switch use of the camera
                 * over to, say, Vuforia or TFOD, you will also need to call closeCameraDevice()
                 * (commented out below), because according to the Android Camera API documentation:
                 *         "Your application should only have one Camera object active at a time for
                 *          a particular hardware camera."
                 *
                 * NB: calling closeCameraDevice() will internally call stopStreaming() if applicable,
                 * but it doesn't hurt to call it anyway, if for no other reason than clarity.
                 *
                 * NB2: if you are stopping the camera stream to simply save some processing power
                 * (or battery power) for a short while when you do not need your vision pipeline,
                 * it is recommended to NOT call closeCameraDevice() as you will then need to re-open
                 * it the next time you wish to activate your vision pipeline, which can take a bit of
                 * time. Of course, this comment is irrelevant in light of the use case described in
                 * the above "important note".
                 */
                webcam.stopStreaming();
                //webcam.closeCameraDevice();
            }

            /*
             * For the purposes of this sample, throttle ourselves to 10Hz loop to avoid burning
             * excess CPU cycles for no reason. (By default, telemetry is only sent to the DS at 4Hz
             * anyway). Of course in a real OpMode you will likely not want to do this.
             */
            sleep(100);
        }
    }

    /*
     * An example image processing pipeline to be run upon receipt of each frame from the camera.
     * Note that the processFrame() method is called serially from the frame worker thread -
     * that is, a new camera frame will not come in while you're still processing a previous one.
     * In other words, the processFrame() method will never be called multiple times simultaneously.
     *
     * However, the rendering of your processed image to the viewport is done in parallel to the
     * frame worker thread. That is, the amount of time it takes to render the image to the
     * viewport does NOT impact the amount of frames per second that your pipeline can process.
     *
     * IMPORTANT NOTE: this pipeline is NOT invoked on your OpMode thread. It is invoked on the
     * frame worker thread. This should not be a problem in the vast majority of cases. However,
     * if you're doing something weird where you do need it synchronized with your OpMode thread,
     * then you will need to account for that accordingly.
     */
    class kellen extends OpenCvPipeline
    {
        public kellen() {

        }
        boolean viewportPaused;

        private Mat workingMatrix = new Mat();

        private double regionValue;

        private Rect RO1;
        private Rect RO2;
        private Rect RO3;

        @Override
        public final Mat processFrame(Mat input){
            //cvtColor converts a feed from a certain color format to another color format; in this case, we are converting from RGB to HSV
            Imgproc.cvtColor(input, workingMatrix, Imgproc.COLOR_RGB2HSV);

        /*lowHSV and highHSV are our thresholds
        IMPORTANT NOTE: openCV defines HSV parameters as such (Hue, Saturation, Value) where Hue is Range 0-179,
        Saturation is in range 0-255 and Value is in range 0-255 (all INCLUSIVE).
        NORMALLY, HSV is defined like so: Hue in range 0-360, Saturation in range 0.0-1.0 and Value in range 0.0-1.0.
        All of this is also technically BGR to HSV, so take the absolute value of your Hue minus 180 to get the right number
        */
            Scalar lowHSV = new Scalar(40, 50, 50);
            Scalar highHSV = new Scalar(75, 255, 255);
            //This creates our mask, and filters out all colors except for whats within our defined bound
            /* IGNORE ALL OF THIS FOR NOW, but essentially we'll use this to tell where our capstone is by counting pixels

            creates the submat that we want to work with
            Mat region = workingMatrix.submat(ROI);
            //this counts the number of white pixels and divides it by the area of our ROI to figure out the percentage.
            regionValue = Core.sumElems(region).val[0] / ROI.area()/255;
            //you need to release the channel that we worked with or smthn smhtn; in this case we have to release region for some reason
            region.release();
            //line color
            Scalar lines = new Scalar(25,255,255);
            //Create the rectangle so that when testing we can see the ROI that we are working with
            Imgproc.rectangle(workingMatrix,ROI,lines);*/
            RO1 = new Rect(
                    new Point(
                            input.cols()/8,
                            input.rows()/4),
                    new Point(
                            input.cols()*(3f/8f),
                            input.rows()*(3f/4f))
            );
            RO2 = new Rect(
                    new Point(
                            input.cols()*(3f/8f),
                            input.rows()/4),
                    new Point(
                            input.cols()*(5f/8f),
                            input.rows()*(3f/4f))
            );
            RO3 = new Rect(
                    new Point(
                            input.cols()*(5f/8f),
                            input.rows()/4),
                    new Point(
                            input.cols()*(7f/8f),
                            input.rows()*(3f/4f))
            );
            Imgproc.rectangle(workingMatrix, RO1, new Scalar(60, 255, 255), 10);
            Imgproc.rectangle(workingMatrix, RO2, new Scalar(60, 255, 255), 10);
            Imgproc.rectangle(workingMatrix, RO3, new Scalar(60, 255, 255), 10);
            Core.inRange(workingMatrix, lowHSV, highHSV, workingMatrix);

            //Submats for boxes, these are the regions that'll detect the color

            Mat box1 = workingMatrix.submat(RO1);
            Mat box2 = workingMatrix.submat(RO2);
            Mat box3 = workingMatrix.submat(RO3);
            //How much in each region is white aka the color we filtered
            double b1p = Core.sumElems(box1).val[0] / RO1.area()/255;
            double b2p = Core.sumElems(box2).val[0] / RO2.area()/255;
            double b3p = Core.sumElems(box3).val[0] / RO3.area()/255;
            //Compare amount of color in each region
            if(b1p > b2p && b1p > b3p) {
                Imgproc.rectangle(workingMatrix, RO1, new Scalar(60, 255, 255), 10);
            }else if(b2p > b1p && b2p > b3p) {
                Imgproc.rectangle(workingMatrix, RO2, new Scalar(60, 255, 255), 10);
            }else if(b3p > b2p & b3p > b1p) {
                Imgproc.rectangle(workingMatrix, RO3, new Scalar(60, 255, 255), 10);
            }
            //return the frame
            return workingMatrix;
        }
        @Override
        public void onViewportTapped()
        {
            /*
             * The viewport (if one was specified in the constructor) can also be dynamically "paused"
             * and "resumed". The primary use case of this is to reduce CPU, memory, and power load
             * when you need your vision pipeline running, but do not require a live preview on the
             * robot controller screen. For instance, this could be useful if you wish to see the live
             * camera preview as you are initializing your robot, but you no longer require the live
             * preview after you have finished your initialization process; pausing the viewport does
             * not stop running your pipeline.
             *
             * Here we demonstrate dynamically pausing/resuming the viewport when the user taps it
             */

            viewportPaused = !viewportPaused;

            if(viewportPaused)
            {
                webcam.pauseViewport();
            }
            else
            {
                webcam.resumeViewport();
            }
        }
    }
}