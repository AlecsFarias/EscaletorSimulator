/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import src.EscaletorProcess.EscaletorType;
import src.EscaletorProcess.Status;

/**
 *
 * @author alecsanderfarias
 */
public class SJF extends Thread {

    Control control;
    private ArrayList<EscaletorProcess> processes;
    public int executingId;
    public Boolean exit;

    public SJF(Control control, ArrayList<EscaletorProcess> processes) {
        this.control = control;
        this.executingId = -1;
        this.processes = new ArrayList<>();
        this.processes = (ArrayList) processes.clone();

        this.exit = false;
    }

    public void updatListWithExecutablesOrFinisheds() {

        ArrayList<EscaletorProcess> updatedProcesses = new ArrayList<>();


        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess ep = this.processes.get(i);

            if (ep.canExecute(control.time)) {
                
                if(ep.isFinished()){
                    ep.status = Status.FINISHED;
                    
                    //ainda não foi finalizado
                    if(control.finisheds == null || !control.finisheds.contains(ep.id)){
                        ep.finishTime = control.time;
                    }
                    
                    
                    control.addFinished(ep.id);
                }else {
                   ep.status = (executingId != -1 && executingId == ep.id) ? Status.EXECUTING : Status.WAITING; 
                }

                
                updatedProcesses.add(ep);
            }

        }


        control.processesRunnning = (ArrayList) updatedProcesses.clone();
    }

    public Boolean isThreadFinshed() {
        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess test = this.processes.get(i);

            if (!test.isFinished()) {
                return false;
            }
        }

        return true;
    }

    public EscaletorProcess getNext() {

        EscaletorProcess current = null;

        for (int i = 0; i < this.processes.size(); i++) {

            EscaletorProcess test = this.processes.get(i);

            if (test.canExecute(control.time) && !test.isFinished()) {

                if ((current == null) || (test.burstTime < current.burstTime)) {
                    current = test;
                }else if(test.burstTime == current.burstTime){
                    
                    //first comes first logic
                    if(test.arrivalTime < current.burstTime){
                        current = test;
                    }
                }
            }
        }

        return current;
    }

    @Override
    public void run() {

        EscaletorProcess current = this.getNext();

        while (!isThreadFinshed() && !this.exit && !control.exit) {
            try {

                //dormir 10 milisegundos para ir mais devagar
                TimeUnit.MILLISECONDS.sleep(15);

                if (current == null || current.isFinished() ) {
                    
                    current = this.getNext();
                } else {
                    current.execute(false);
                }
                
                 
                this.executingId = (current != null) ? current.id : -1;
                updatListWithExecutablesOrFinisheds();
                control.time++;
            } catch (InterruptedException ex) {
                exit = true;
            }
        }
        
        
        control.finished = true;
        this.exit = true;
        
        //control.stopCurrentThread();

    }

}
