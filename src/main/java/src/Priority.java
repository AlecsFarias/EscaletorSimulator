/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author alecsanderfarias
 */
public class Priority extends Thread {

    Control control;
    private ArrayList<EscaletorProcess> processes;
    public int executingId;
    public Boolean exit;

    public Priority(Control control, ArrayList<EscaletorProcess> startProcesses) {
        this.control = control;
        
        this.processes = new ArrayList();

        for (int i = 0; i < startProcesses.size(); i++) {
            EscaletorProcess pr = startProcesses.get(i);

            pr.priority = 1;
            this.processes.add(pr);
        }

        this.exit = false;
     
    }

    public Boolean isThreadFinshed() {
        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess pr = this.processes.get(i);

            if (!pr.isFinished()) {
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
                if (current == null || test.priority < current.priority ) {
                    current = test;
                }

            }

        }

        return current;
    }
    
    public void updatListWithExecutablesOrFinisheds() {

        ArrayList<EscaletorProcess> updatedProcesses = new ArrayList<>();


        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess ep = this.processes.get(i);

            if (ep.canExecute(control.time)) {
                
                if(ep.isFinished()){
                    ep.status = EscaletorProcess.Status.FINISHED;
                    
                    //ainda não foi finalizado
                    if(control.finisheds == null || !control.finisheds.contains(ep.id)){
                        ep.finishTime = control.time;
                    }
                    
                    
                    control.addFinished(ep.id);
                }else {
                   ep.status = (executingId != -1 && executingId == ep.id) ? EscaletorProcess.Status.EXECUTING : EscaletorProcess.Status.WAITING; 
                }

                
                updatedProcesses.add(ep);
            }

        }


        control.processesRunnning = (ArrayList) updatedProcesses.clone();
    }
     
     
    @Override
    public void run(){
       
        while(!isThreadFinshed() && !this.exit && !control.exit){
            try {

                //dormir 10 milisegundos para ir mais devagar
                TimeUnit.MILLISECONDS.sleep(15);
                
                
                EscaletorProcess current = this.getNext();


                if (current != null) {
                   
                    current.execute(true);
                }

                this.executingId = (current != null) ? current.id : -1;
                updatListWithExecutablesOrFinisheds();
                control.time++;
            } catch (InterruptedException ex) {
                this.exit = true;
            } 
        }
        

        control.finished = true;
        this.exit = true;
        
       
        
        
    }

}
