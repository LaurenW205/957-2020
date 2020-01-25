<<<<<<< HEAD
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

=======
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

>>>>>>> 95b55def0721c748cf6653657e1f82811854f827
}