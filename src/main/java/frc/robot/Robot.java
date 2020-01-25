/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.wpilibj.Joystick;


public class Robot extends TimedRobot {

  Drivetrain m_drivetrain = Drivetrain.getInstance();

  Joystick m_joystick = new Joystick(0);

  @Override
  public void robotInit() {
  }

  @Override
  public void robotPeriodic() {
  }

  public void disabledInit() {
    m_drivetrain.setIdleMode(IdleMode.kBrake);
  }
  @Override
  public void autonomousInit() {
    m_drivetrain.setIdleMode(IdleMode.kCoast);
    m_drivetrain.resetEncoders();
  }

  @Override
  public void autonomousPeriodic() {
    m_drivetrain.turnTo(180);
  }

  @Override
  public void teleopPeriodic() {
    m_drivetrain.arcadeDrive(m_joystick.getRawAxis(4), m_joystick.getRawAxis(1));
  }

  @Override
  public void testPeriodic() {
  }
}
