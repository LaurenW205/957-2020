/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.RobotState.State;


public class Robot extends TimedRobot {

  Drivetrain m_drivetrain = Drivetrain.getInstance();
  PowerCell m_pc = PowerCell.getInstance();
  RobotState m_state = RobotState.getInstance();

  Joystick m_joystick = new Joystick(0);

  int m_switchIntake = 0;

  int k_switchIntake = 3;
  int k_ifEject = 2;
  int k_ifShoot = 1;
  int k_ifGrab = 4;
  int k_auto = 5;
  int k_ifGrabDisable = 9;
  int k_reverse = 6;
  int k_fingerActive = 7;
  int k_fingerSpin = 8;
  int k_drain1 = 11;
  int k_drain2 = 12;

  @Override
  public void robotInit() {
    
  }
    
  public double tracking() {

    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("<variablename").getDouble(0);
  
  }

  @Override
  public void robotPeriodic() {
  }

  public void disabledInit() {
    m_drivetrain.setIdleMode(IdleMode.kBrake);
    m_pc.setIdleMode(IdleMode.kBrake);
  }
  @Override
  public void autonomousInit() {
    m_drivetrain.setIdleMode(IdleMode.kCoast);
    m_drivetrain.resetEncoders();
    m_pc.setIdleMode(IdleMode.kCoast);

  }

  @Override
  public void teleopInit() {
    m_pc.setIdleMode(IdleMode.kCoast);
  }
  
  @Override
  public void autonomousPeriodic() {
    m_drivetrain.turnTo(180);
  }

  @Override
  public void teleopPeriodic() {
    m_drivetrain.arcadeDrive(m_joystick.getRawAxis(4), m_joystick.getRawAxis(1));
    

    switch(m_switchIntake){
      case 0:
        if(m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 1;
        }
        break;
      case 1:
        if(m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 2;
          m_pc.setArm(true);
        }
        break;
      case 2:
        if(m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 3;
        }
        break;     
      case 3:
        if(m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 0;
          m_pc.setArm(false);
          m_state.setState(State.WAITING);
        }
        break;
      }
    
    if(m_joystick.getRawButton(k_ifGrab)){
      m_state.setState(State.GRAB_CELL);
      m_pc.setArm(true);
    }

    if(m_joystick.getRawButton(k_ifGrabDisable)){
      m_state.setState(State.GRAB_CELL);
      m_pc.setArm(false);
    }

    if(m_joystick.getRawButton(k_ifEject)){
      m_state.setState(State.EJECT);
    }
    
    m_pc.run();
  }

  @Override
  public void testPeriodic() {
  }
}
