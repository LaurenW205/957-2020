package frc.robot;

public class RobotState{
    private static RobotState m_robotstate = null;

    private RobotState(){

    }

    public static RobotState getInstance(){
        if(m_robotstate == null)
        m_robotstate = new RobotState();

        return m_robotstate;
    }

}