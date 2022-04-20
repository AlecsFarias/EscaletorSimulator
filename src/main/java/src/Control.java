package src;

import java.util.ArrayList;
import java.util.UUID;
import src.EscaletorProcess.EscaletorType;



/**
 *
 * @author alecsanderfarias
 */
public class Control {

    //variaveis para config
    public volatile ArrayList<EscaletorProcess> processesConfig;
    public EscaletorType escaletorTypeSelected = EscaletorType.SJF;
    public volatile int QueueTime = 10;
    

//    EscaletorType.SJF.name()
//    EscaletorType.SRTN.name()
//    EscaletorType.RoundRobin.name()
//    EscaletorType.Priority.name()
//    EscaletorType.SJF.MultipleQueues()
//    
    //variaveis para execução
    public Thread t;
    public int time = 0;
    public ArrayList<Integer> finisheds;
    public ArrayList<EscaletorProcess> processesRunnning;
    public int RunMaxTime = 10;
    public Boolean exit = false;
    public Boolean finished = false;

    public void addProcess() {
        EscaletorProcess p;

        if (this.processesConfig == null) {
            this.processesConfig = new ArrayList<>();
            p = new EscaletorProcess(0);
        }else {
            p = new EscaletorProcess(this.processesConfig.size());
        }

        processesConfig.add(p);
    }

    public void addManyProcesses(int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            addProcess();
        }
    }

    public void editProcess(int index, EscaletorProcess process) {
        processesConfig.set(index, process);
    }

    public void printAllProcess(){
        if(this.processesRunnning != null){
            for(int i =0; i < this.processesConfig.size(); i++){
                EscaletorProcess pr = this.processesConfig.get(i);


            }
        }
    }
    
    
    public ArrayList<EscaletorProcess> getClone(){
        ArrayList<EscaletorProcess> cloneList = new ArrayList();
        
        for(int i=0; i < this.processesConfig.size(); i++){
            EscaletorProcess pr = this.processesConfig.get(i);
            
            cloneList.add(pr.clone());
        }
        
        return cloneList;

    }
    
    public void start() {
        exit = true;
        
        
        printAllProcess();
        
        
        if (processesConfig == null || processesConfig.size() <= 0) {
            return;
        }
        
       
        
  
        //inicializar variaveis
        this.time = 0;
        this.RunMaxTime = this.QueueTime;
        if(this.finisheds != null){
            this.finisheds.clear();
        }
        
        if(this.processesRunnning != null){
            this.processesRunnning.clear();
        }
        
        
        //clone para não afetar memoria
        ArrayList<EscaletorProcess> clone = this.getClone();
        

        
        //criar nova thread
        switch (this.escaletorTypeSelected) {
            case SJF:
                this.t = new SJF(this,clone);
                break;
            case SRTN:
                this.t = new SRTN(this,clone);
                break;
            case Priority:
                this.t = new Priority(this,clone);  
                break;
            case RoundRobin:
                this.t = new RoundRobin(this,clone);   
                break;
            case MultipleQueues:
                this.t = new MultipleQueues(this,clone);
                break;
            default:
                return;
                
        }

        
        this.exit = false;
        
        //iniciar nova thread
        this.t.start();
    }

    public void removeProcess(int id){
        
        for(int i =0; i < this.processesConfig.size(); i++){
            EscaletorProcess test = this.processesConfig.get(i);
            
            if(test.id == id){
                this.processesConfig.remove(i);
                break;
            }
        } 
    }
    
    public void updateProcessConfig(int id, int column , String value){
        EscaletorProcess pr = null;
        
        for(int i =0; i < this.processesConfig.size(); i++){
            EscaletorProcess test = this.processesConfig.get(i);
            
            if(test.id == id){
                pr = test;
                break;
            }
        }
        
        if(pr != null){
            switch (column) {
                case 1:
                    pr.burstTime = Integer.parseInt(value);
                    break;
                case 2:
                    pr.arrivalTime = Integer.parseInt(value);
                    break;
                case 3:
                    pr.priority = Integer.parseInt(value);
                    break;
                default:
                    break;
            }
        }
        
        
    }
    
    public void addFinished(int id){
        if(this.finisheds == null){
            this.finisheds = new ArrayList<>();
            
            finisheds.add(id);
        }else {
            Boolean existis = finisheds.contains(id);
            
            if(!existis){
                finisheds.add(id);
            }
        }
    }
    
    
}
