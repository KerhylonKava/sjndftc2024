/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;


/*
 * This file contains an example of a Linear "OpMode".
 * An OpMode is a 'program' that runs in either the autonomous or the teleop period of an FTC match.
 * The names of OpModes appear on the menu of the FTC Driver Station.
 * When a selection is made from the menu, the corresponding OpMode is executed.
 *
 * This particular OpMode illustrates driving a 4-motor Omni-Directional (or Holonomic) robot.
 * This code will work with either a Mecanum-Drive or an X-Drive train.
 * Both of these drives are illustrated at https://gm0.org/en/latest/docs/robot-design/drivetrains/holonomic.html
 * Note that a Mecanum drive must display an X roller-pattern when viewed from above.
 *
 * Also note that it is critical to set the correct rotation direction for each motor.  See details below.
 *
 * Holonomic drives provide the ability for the robot to move in three axes (directions) simultaneously.
 * Each motion axis is controlled by one Joystick axis.
 *
 * 1) Axial:    Driving forward and backward               Left-joystick Forward/Backward
 * 2) Lateral:  Strafing right and left                     Left-joystick Right and Left
 * 3) Yaw:      Rotating Clockwise and counter clockwise    Right-joystick Right and Left
 *
 * This code is written assuming that the right-side motors need to be reversed for the robot to drive forward.
 * When you first test your robot, if it moves backward when you push the left stick forward, then you must flip
 * the direction of all 4 motors (see code below).
 *
 * Use Android Studio to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this OpMode to the Driver Station OpMode list
 */

@TeleOp(name="Omni-Drive-with-Lift", group="Linear OpMode")
//@Disabled
public class OmniMotorLift extends LinearOpMode {

    // Declare OpMode members for each of the 4 motors.
    public ElapsedTime runtime = new ElapsedTime();
    public DcMotor leftFrontDrive = null;
    public DcMotor leftBackDrive = null;
    public DcMotor rightFrontDrive = null;
    public DcMotor rightBackDrive = null;

    public DcMotor george = null; //lift but peter said to name it george
    public DcMotor whack = null; //arm hinge

    public final static double LEFT_HAND_HOME = 0.4;
    /* use/integrate this later \|/ */
    public final static double ARM_MIN_RANGE = 0.4;
    public final static double ARM_MAX_RANGE = 1;
    double gripper_lateral_pos = LEFT_HAND_HOME;
    final double ARM_SPEED = 0.01;
    
    private Servo left_hand;

    @Override
    public void runOpMode() {

        // Initialize the hardware variables. Note that the strings used here must correspond
        // to the names assigned during the robot configuration step on the DS or RC devices.
        leftFrontDrive  = hardwareMap.get(DcMotor.class, "left_front_drive");
        leftBackDrive  = hardwareMap.get(DcMotor.class, "left_back_drive");
        rightFrontDrive = hardwareMap.get(DcMotor.class, "right_front_drive");
        rightBackDrive = hardwareMap.get(DcMotor.class, "right_back_drive");

        george = hardwareMap.get(DcMotor.class, "sir_george_liftington"); //lift
        
        whack = hardwareMap.get(DcMotor.class, "whackamole"); // arm hinge

        /* use/integrate this later
        arm = hardwareMap.servo.get("arm");
        */
        
        left_hand = hardwareMap.get(Servo.class, "left_hand");
        left_hand.setPosition(gripper_lateral_pos);

        // ########################################################################################
        // !!!            IMPORTANT Drive Information. Test your motor directions.            !!!!!
        // ########################################################################################
        // Most robots need the motors on one side to be reversed to drive forward.
        // The motor reversals shown here are for a "direct drive" robot (the wheels turn the same direction as the motor shaft)
        // If your robot has additional gear reductions or uses a right-angled drive, it's important to ensure
        // that your motors are turning in the correct direction.  So, start out with the reversals here, BUT
        // when you first test your robot, push the left joystick forward and observe the direction the wheels turn.
        // Reverse the direction (flip FORWARD <-> REVERSE ) of any wheel that runs backward
        // Keep testing until ALL the wheels move the robot forward when you push the left joystick forward.
        leftFrontDrive.setDirection(DcMotor.Direction.FORWARD);
        leftBackDrive.setDirection(DcMotor.Direction.FORWARD);
        rightFrontDrive.setDirection(DcMotor.Direction.REVERSE);
        rightBackDrive.setDirection(DcMotor.Direction.FORWARD);

        george.setDirection(DcMotor.Direction.FORWARD);
        whack.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses START)
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            double max;

            telemetry.update();
            left_hand.scaleRange(0.4, 1);
            move_gripper_laterally();
            open_gripper();
            close_gripper();

            // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
            //double lateral = 0;
            
            double axial   =  -gamepad1.right_stick_x;  // Note: pushing stick forward gives negative value
            double lateral =  gamepad1.left_stick_x; // strafe
            double yaw     =  gamepad1.left_stick_y; // forward/backward

            /*
            //Servo Code
            if (gamepad1.a)
                armPosition += ARM_SPEED;
            else if (gamepad1.y)
                armPosition -= ARM_SPEED;

            //clips the servo range
            armPosition = Range.clip(armPosition, ARM_MIN_RANGE, ARM_MAX_RANGE);
            arm.setPosition(armPosition);
            */ 

            //double axial   =  gamepad1.left_stick_x;  // Note: pushing stick forward gives negative value
            //double lateral =  -gamepad1.right_stick_y; // left joystick forward, backward, turn, right no worky
            //double yaw     =  -gamepad1.left_stick_y;

            // Combine the joystick requests for each axis-motion to determine each wheel's power.
            // Set up a variable for each drive wheel to save the power level for telemetry.
            double leftFrontPower  = axial + lateral + yaw; //DON'T MESS WITH THESE VALUES!
            double rightFrontPower = axial + lateral - yaw; //WILL BREAK MANY THINGS!
            double leftBackPower   = axial - lateral + yaw; //DON'T MESS WITH THESE VALUES!
            double rightBackPower  = axial - lateral - yaw; // WILL BREAK MANY THINGS!
            double georgePower = 0;
            double whackPower = 0;

            //double georgePower = gamepad1.dpad_up; //does not work
            if (gamepad1.dpad_up) {
              georgePower = 1;
            }
            if (gamepad1.dpad_down) {
              georgePower = -1;
            }

            if (gamepad1.y) {
              whackPower = 0.2;
            }
            
            if (gamepad1.a) {
              whackPower = -0.2;
            }

            // Normalize the values so no wheel power exceeds 100%
            // This ensures that the robot maintains the desired motion.
            max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
            max = Math.max(max, Math.abs(leftBackPower));
            max = Math.max(max, Math.abs(rightBackPower));

            if (max > 1.0) {
                leftFrontPower  /= max;
                rightFrontPower /= max;
                leftBackPower   /= max;
                rightBackPower  /= max;
            }

            // This is test code:
            //
            // Uncomment the following code to test your motor directions.
            // Each button should make the corresponding motor run FORWARD.
            //   1) First get all the motors to take to correct positions on the robot
            //      by adjusting your Robot Configuration if necessary.
            //   2) Then make sure they run in the correct direction by modifying the
            //      the setDirection() calls above.
            // Once the correct motors move in the correct direction re-comment this code.

            /*
            leftFrontPower  = gamepad1.x ? 1.0 : 0.0;  // X gamepad
            leftBackPower   = gamepad1.a ? 1.0 : 0.0;  // A gamepad
            rightFrontPower = gamepad1.y ? 1.0 : 0.0;  // Y gamepad
            rightBackPower  = gamepad1.b ? 1.0 : 0.0;  // B gamepad
            */

            // Send calculated power to wheels
            leftFrontDrive.setPower(leftFrontPower);
            rightFrontDrive.setPower(rightFrontPower);
            leftBackDrive.setPower(leftBackPower);
            rightBackDrive.setPower(rightBackPower);
            george.setPower(georgePower);
            whack.setPower(whackPower);

            // Show the elapsed game time and wheel power.
            telemetry.addData("left hand", "%.2f", gripper_lateral_pos); //what position is the servo at?

            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Front left/Right", "%4.2f, %4.2f", leftFrontPower, rightFrontPower);
            telemetry.addData("Back  left/Right", "%4.2f, %4.2f", leftBackPower, rightBackPower);
            telemetry.addData("Sir George Liftington the THIRD. (Just to be clear, this is not the third iteration of George).","%4.2f", georgePower);
            telemetry.addData("Lord Whackamole the Umpteenth","%4.2f", whackPower);
            telemetry.update();
        }
    }
    private void move_gripper_laterally() {
        while (gamepad1.a && gripper_lateral_pos > ARM_MIN_RANGE) {
          gripper_lateral_pos += -0.0037;
          left_hand.setPosition(gripper_lateral_pos);
        }
        while (gamepad1.b && gripper_lateral_pos < ARM_MAX_RANGE) {
          gripper_lateral_pos += 0.0037;
          left_hand.setPosition(gripper_lateral_pos);
        }
      }
    
      /**
       * opens gripper
       */
      private void open_gripper() {
        if (gamepad1.left_bumper) {
          gripper_lateral_pos = ARM_MIN_RANGE;
          left_hand.setPosition(gripper_lateral_pos);
        }
      }
    
      /**
       * closes gripper
       */
      private void close_gripper() {
        if (gamepad1.right_bumper) {
          gripper_lateral_pos = ARM_MAX_RANGE;
          left_hand.setPosition(gripper_lateral_pos);
        }
      }
    
      /**
       * does what the name tells you
       */
      /* Commented out b/c it does not make logical sense
      private void slowly_close_then_stop_gripper() {
        if (gamepad1.dpad_right) {
          while (!!gamepad1.dpad_right) {
          } //not needed ^
          while (!(gamepad1.dpad_right || gripper_lateral_pos == 1 || !opModeIsActive())) {
            gripper_lateral_pos += 0.0013;
            left_hand.setPosition(gripper_lateral_pos);
          }
          gripper_lateral_pos = ARM_MAX_RANGE;
        }
      }  
      */  
    }
