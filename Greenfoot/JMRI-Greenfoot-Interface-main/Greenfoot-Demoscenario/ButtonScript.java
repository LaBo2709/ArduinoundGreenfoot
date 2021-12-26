import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot und MouseInfo)
import de.wwu.jmrigreenfootinterface.*;
import javax.swing.*;
/**
 * A button that lets a specific train execute a custom script.
 * 
 * @author Leonard Bienbeck,Lars Bockstette
 * @version 1.0.1
 */
public class ButtonScript extends Button
{

    /**
     * The name assigned to the locomotive in the JMRI system.
     */
    private String locomotiveSystemName = "";
    
    /**
     * Es wird mithilfe von Java Swing ein Eingabefenster mit zwei Textfeldern entworfen. Die Eingaben werden verwendet um die Geschwindigkeit der Lokomotiven zu steuern
     * 
     * 
     */
    @Override
    public void onClick() {
      JTextField xField = new JTextField(5);
      JTextField yField = new JTextField(5);

      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Lokomotive:"));
      myPanel.add(xField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Geschwindigkeit:"));
      myPanel.add(yField);

      int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "Bitte geben Sie die DCC Adresse und die Geschwindigkeit ein.", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        try {
                Integer.parseInt(yField.getText());
        }
            catch (NumberFormatException e) {
                System.out.println("Bitte als 2. Eingabe nur Integer verwenden");
        }
         System.out.println("DCC Adresse: " + xField.getText());
         System.out.println("Geschwindigkeit: " + yField.getText());
         JMRI.getInterface().addLocomotive("S"+xField.getText(), "S"+xField.getText());
         JMRI.getInterface().setSpeed("S"+xField.getText(), Integer.parseInt(yField.getText()));
      }
    } // perform some customized actions   // ...
    }

