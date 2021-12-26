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
        // Error abfangen wenn ungültige Eingabe in Geschwindigkeit geschieht
         try {
          int geschwindigkeit = Integer.parseInt(yField.getText());
          // Error abfangen wenn ungültige Eingabe in Lokomotive geschieht
          try {
              Integer.parseInt(xField.getText());
              System.out.println("DCC Adresse: " + xField.getText());
              System.out.println("Geschwindigkeit: " + yField.getText());
             JMRI.getInterface().addLocomotive("S"+xField.getText(), "S"+xField.getText());
             JMRI.getInterface().setSpeed("S"+xField.getText(), geschwindigkeit);
            }
            catch(NumberFormatException e) {
               System.out.println("Die DCC Adresse einer Lokomotive ist eine Integer"); 
            }
          
        }
        catch (NumberFormatException e) {
          System.out.println("Geschwindigkeits Eingabe muss ein Integer sein");
        }
      }
    } 
}

