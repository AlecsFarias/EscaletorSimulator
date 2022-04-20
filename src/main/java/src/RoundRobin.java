/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author alecsanderfarias
 */
public class RoundRobin extends Thread {

    Control control;
    private ArrayList<EscaletorProcess> processes;
    private ArrayList<EscaletorProcess> readyQueue;
    public int executingId;
    private volatile boolean exit;

    public RoundRobin(Control control, ArrayList<EscaletorProcess> processes) {
        this.control = control;
        this.executingId = -1;
        this.processes = new ArrayList<>();
        this.processes = (ArrayList) processes.clone();
        this.readyQueue = new ArrayList<>();

        exit = false;
        
    }

    public void updatListWithExecutablesOrFinisheds() {

        ArrayList<EscaletorProcess> updatedProcesses = new ArrayList<>();

        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess ep = this.processes.get(i);

            if (ep.canExecute(control.time)) {

                if (ep.isFinished()) {
                    ep.status = EscaletorProcess.Status.FINISHED;

                    //ainda não foi finalizado
                    if (control.finisheds == null || !control.finisheds.contains(ep.id)) {
                        ep.finishTime = control.time;
                    }

                    control.addFinished(ep.id);
                } else {
                    ep.status = (executingId != -1 && executingId == ep.id) ? EscaletorProcess.Status.EXECUTING : EscaletorProcess.Status.WAITING;
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

    public void prepareReadyQueue(int executingId)   {
        if (this.readyQueue == null) {
            this.readyQueue = new ArrayList<>();
        }
        

        ArrayList<EscaletorProcess> temporalArrayList = new ArrayList<>();

        for (int i = 0; i < this.processes.size(); i++) {
            EscaletorProcess pr = this.processes.get(i);

            if (pr.canExecute(control.time) && !pr.isFinished() && !this.readyQueue.contains(pr)) {
                temporalArrayList.add(pr);
            }
        }
        

        temporalArrayList.sort(new Comparator<EscaletorProcess>() {
            @Override
            public int compare(EscaletorProcess p1, EscaletorProcess p2) {
                 if (p1.id == executingId) {
                    return +1;
                }

                if (p2.id == executingId) {
                    return -1;
                }
                
                
                if (p1.arrivalTime < p2.arrivalTime) {
                    return -1;
                }

                if (p1.arrivalTime > p2.arrivalTime) {
                    return +1;
                }

               

                return 0;
            }
        });
        
      

        //adiciona no final da lista de processos prontos para executar
        this.readyQueue.addAll(temporalArrayList);
        
        
        
        String ids = "";
        for(int i =0; i < this.readyQueue.size(); i ++ ){
            ids = ids + " [" + readyQueue.get(i).id + "] ";
        }
        
     

    }

    public EscaletorProcess getNext() {

        if (readyQueue != null && !readyQueue.isEmpty()) {
            
            EscaletorProcess pr = readyQueue.get(0);

            readyQueue.remove(0);
            return pr;

        }

        return null;
    }

    @Override
    public void run() {
        
        this.prepareReadyQueue(-1);
        
        EscaletorProcess current = this.getNext();
        
        

        for (int timeCurrent = 0; !isThreadFinshed() && !exit && !control.exit; timeCurrent++) {
            try {

                //dormir 10 milisegundos para ir mais devagar
                TimeUnit.MILLISECONDS.sleep(15);


                if (current != null) {
                    current.execute(false);
                }

                if (current == null || timeCurrent >= control.RunMaxTime ||  current.isFinished()) {
                    this.prepareReadyQueue(current == null ? -1 : current.id);
                    current = this.getNext();
                    timeCurrent = 0;
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


    }

}
