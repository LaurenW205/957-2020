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
}