/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.RobotState.State;


public class Robot extends TimedRobot {

  Drivetrain m_drivetrain = Drivetrain.getInstance();
  PowerCell m_pc = PowerCell.getInstance();
  RobotState m_state = RobotState.getInstance();

  Joystick m_joystick = new Joystick(0);
  Joystick m_xbox = new Joystick(1);

  int m_switchIntake = 0;

  int k_switchIntake = 3;
  int k_ifEject = 2;
  int k_ifShoot = 1;
  int k_ifGrab = 4;
  int k_auto = 5;
  int k_ifGrabDisable = 9;
  int k_reverseAll = 1;
  int k_reverseIntake = 2;
  int k_fingerActive = 7;
  int k_fingerSpin = 8;
  int k_drain1 = 11;

  int k_vision = 12;

  @Override
  public void robotInit() {
    m_state.setState(State.WAITING);
    
  }
    
  public double tracking() {

    return NetworkTableInstance.getDefault().getTable("limelight").getEntry("<variablename").getDouble(0);
  
  }

  @Override
  public void robotPeriodic() {
  }

  public void disabledInit() {
    m_drivetrain.setIdleMode(IdleMode.kCoast);
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
    if(m_joystick.getRawButton(k_vision)){
      m_drivetrain.target(0);

    }else{
      m_drivetrain.arcadeDrive(m_joystick.getRawAxis(1), -m_joystick.getRawAxis(2));
    }
    switch(m_switchIntake){
      case 0:
        if(m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 1;
        }
        break;
      case 1:
        if(!m_joystick.getRawButton(k_switchIntake)){
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
        if(!m_joystick.getRawButton(k_switchIntake)){
          m_switchIntake = 0;
          m_pc.setArm(false);
          m_state.setState(State.WAITING);
        }
        break;
      }
    
    if(m_joystick.getRawButton(k_ifGrab)){
      if(State.WAITING == m_state.state()){
        m_pc.record();
        m_state.setState(State.GRAB_CELL);
        m_pc.setArm(true);
    
      }
      
    }

    if(m_xbox.getRawButton(k_reverseAll)){
      if(State.GRAB_CELL == m_state.state() || State.WAITING == m_state.state()){
        m_state.setState(State.REVERSE_ALL);
      }
    }else if(m_state.state() == State.REVERSE_ALL){
      m_state.setState(State.WAITING);

    }
    if(m_xbox.getRawButton(k_reverseIntake)){
      if(State.GRAB_CELL == m_state.state() || State.WAITING == m_state.state()){
        m_state.setState(State.REVERSE_INTAKE);
      }
    }else if(m_state.state() == State.REVERSE_INTAKE){

      m_state.setState(State.WAITING);

    }

    if(m_joystick.getRawButton(k_ifGrabDisable)){
      m_state.setState(State.GRAB_CELL);
      m_pc.setArm(false);
    }

    if(m_joystick.getRawButton(k_ifEject)){
      m_state.setState(State.EJECT);
    }

    if(m_joystick.getRawButton(k_ifShoot)){
      m_state.setState(State.SHOOT);
    }
    
    m_pc.run();
  }

  @Override
  public void testPeriodic() {
  }
}
