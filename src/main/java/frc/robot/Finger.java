<<<<<<< HEAD
package frc.robot;

public class Finger{

    private static Finger m_finger = null;

    private Finger(){

    }

    public static Finger getInstance(){
        if(m_finger == null)
        m_finger = new Finger();

        return m_finger;
    }

=======
package frc.robot;

public class Finger{

    private static Finger m_finger = null;

    private Finger(){

    }

    public static Finger getInstance(){
        if(m_finger == null)
        m_finger = new Finger();

        return m_finger;
    }

>>>>>>> 95b55def0721c748cf6653657e1f82811854f827
}