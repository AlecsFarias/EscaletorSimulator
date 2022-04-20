/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

/**
 *
 * @author alecsanderfarias
 */
public class Main {
   final Control control = new Control();
    
    
  private void start() {
    View view = new View(control);
    
    view.setVisible(true);

    new Thread(view).start();
  }

  public static void main(String[] args) throws InterruptedException {
    try {
      Main main = new Main();
      main.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
