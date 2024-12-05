/* Copyright (c) 2017 FIRST. All rights reserved.
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

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "mainnov2024 (Blocks to Java)")
public class mainnov2024 extends LinearOpMode {

  private Servo left_hand;

  double gripper_lateral_pos;
  //done 
  /**
   * This sample contains the bare minimum Blocks for any regular OpMode. The 3 blue
   * Comment Blocks show where to place Initialization code (runs once, after touching the
   * DS INIT button, and before touching the DS Start arrow), Run code (runs once, after
   * touching Start), and Loop code (runs repeatedly while the OpMode is active, namely not
   * Stopped).
   */
  @Override
  public void runOpMode() {
    left_hand = hardwareMap.get(Servo.class, "left_hand");

    // Made by Felix, ideas from whoever made the old main
    // Make sure to press START + A when the gamepad is connected to set it to gamepad1
    waitForStart();
    if (opModeIsActive()) {
      gripper_lateral_pos = 1;// did not use this
      while (opModeIsActive()) {
        telemetry.update();
        left_hand.scaleRange(0.4, 1);
        move_gripper_laterally();
        open_gripper();
        close_gripper();
      }
    }
  }

  /**
   * changes the gripper to close or open
   */
  private void move_gripper_laterally() {
    while (gamepad1.a && gripper_lateral_pos > 0.4) {
      gripper_lateral_pos += -0.0037;
      left_hand.setPosition(gripper_lateral_pos);
    }
    while (gamepad1.b && gripper_lateral_pos < 1) {
      gripper_lateral_pos += 0.0037;
      left_hand.setPosition(gripper_lateral_pos);
    }
  }

  /**
   * opens gripper
   */
  private void open_gripper() {
    if (gamepad1.left_bumper) {
      gripper_lateral_pos = 0.4;
      left_hand.setPosition(gripper_lateral_pos);
    }
  }

  /**
   * closes gripper
   */
  private void close_gripper() {
    if (gamepad1.right_bumper) {
      gripper_lateral_pos = 1;
      left_hand.setPosition(gripper_lateral_pos);
    }
  }

  /**
   * does what the name tells you
   */
  private void slowly_close_then_stop_gripper() {
    if (gamepad1.dpad_right) {
      while (!!gamepad1.dpad_right) {
      }
      while (!(gamepad1.dpad_right || gripper_lateral_pos == 1 || !opModeIsActive())) {
        gripper_lateral_pos += 0.0013;
        left_hand.setPosition(gripper_lateral_pos);
      }
      gripper_lateral_pos = 1;
    }
  }
}
