package frc.robot;

public class RobotState{
    private static RobotState m_robotstate = null;

    private RobotState(){

    }

    public State m_state = null;

    public static RobotState getInstance(){
        if(m_robotstate == null)
        m_robotstate = new RobotState();

        return m_robotstate;
    }

    public void setState(State state){
        m_state = state;
    }

    public State state(){
        return m_state;
    }


    public enum State{
        GRAB_CELL;
    }
}