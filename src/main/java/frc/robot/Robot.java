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
  Finger m_finger = Finger.getInstance();
  Climber m_climber = Climber.getInstance();
  RobotState m_state = RobotState.getInstance();

  Joystick m_joystick = new Joystick(0);
  Joystick m_xbox = new Joystick(1);

  int m_switchIntake = 0;
  int m_switchFinger = 0;
  int m_switchClimber = 0; 

  int k_switchIntake = 3;
  int k_ifEject = 2;
  int k_ifShoot = 1;
  int k_ifGrabDisable = 9;
  int k_reverseAll = 10;
  int k_reverseIntake = 8;
  int k_fingerActive = 5;
  int k_fingerSpin = 6;
  int k_fingerPosition = 4;
  int k_drain = 2;
  

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

    m_climber.operateAnalog(m_xbox.getRawAxis(3)-m_xbox.getRawAxis(2));

    switch(m_switchIntake){
      case 0:
        if(m_joystick.getRawButton(k_switchIntake) && m_state.state() == State.WAITING){
          m_switchIntake = 1;
        }
        break;
      case 1:
        if(!m_joystick.getRawButton(k_switchIntake) && m_state.state() == State.WAITING){      
          m_pc.record();
          m_state.setState(State.GRAB_CELL);
          m_pc.setArm(true);
          m_switchIntake = 2;
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

    if(m_joystick.getRawButton(k_ifEject)){
      m_state.setState(State.EJECT);
    }

    if(m_joystick.getRawButton(k_ifShoot)){
      m_state.setState(State.SHOOT);
    }
    
    if(m_joystick.getRawButton(k_fingerSpin)){
      if(State.GRAB_CELL == m_state.state() || State.WAITING == m_state.state()){
        m_state.setState(State.SPIN);
      }
    }else if(m_state.state() == State.SPIN){
      m_state.setState(State.WAITING);
    }

    if(m_joystick.getRawButton(k_fingerPosition)){
      if(State.GRAB_CELL == m_state.state() || State.WAITING == m_state.state()){
        m_state.setState(State.COLOR_SELECT);
      }
    }else if(m_state.state() == State.COLOR_SELECT){
      m_state.setState(State.WAITING);
    }

    switch(m_switchFinger){
      case 0:
        if(m_joystick.getRawButton(k_fingerActive)){
          m_switchFinger = 1;
        }
        break;
      case 1:
        if(!m_joystick.getRawButton(k_fingerActive)){      
          m_finger.up();
          m_switchFinger = 2;
        }
        break;
      case 2:
        if(m_joystick.getRawButton(k_fingerActive)){
          m_switchFinger = 3;
        }
        break;     
      case 3:
        if(!m_joystick.getRawButton(k_fingerActive)){
          m_finger.down();
          m_switchFinger = 0;

        }
        break;
    }

    switch(m_switchClimber){
      case 0:
        if(m_xbox.getRawButton(k_drain)){
          m_switchClimber = 1;
        }
        break;
      case 1:
        if(!m_xbox.getRawButton(k_drain)){      
          m_climber.up();
          m_switchClimber = 2;
        }
        break;
      case 2:
        if(m_xbox.getRawButton(k_drain)){
          m_switchClimber= 3;
        }
        break;     
      case 3:
        if(!m_xbox.getRawButton(k_drain)){
          m_climber.down();
          m_switchClimber = 0;

        }
        break;
    }
    m_pc.run();
  }

  @Override
  public void testPeriodic() {
  }
}
