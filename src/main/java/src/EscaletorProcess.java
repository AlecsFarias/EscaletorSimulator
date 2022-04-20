/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package src;

import java.text.DecimalFormat;
import java.util.Random;

/**
 *
 * @author alecsanderfarias
 */
public class EscaletorProcess  {

    public int id;
    public int burstTime;
    public int arrivalTime;
    public int priority;
    public int progress = 0;
    public int finishTime = -1;

    public Status status = Status.NOT_INITIATED;
    
    public EscaletorProcess(int id, int burstTime, int arrivalTime, int priority) {
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
        this.id = id;
    }

    public EscaletorProcess(int minor) {
        this.burstTime = getRandomWithMinMax(1, 1000);
        this.arrivalTime = getRandomWithMinMax(1, 500);
        this.priority = getRandomWithMinMax(1, 100);
        this.id = minor + 1;
    }

    private int getRandomWithMinMax(int min, int max) {
        Random rn = new Random();
        int n = max - min + 1;

        int i = rn.nextInt() % n;

        return min + Math.abs(i);
    }

    public String getProgressFormated() {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        return df.format(((float) this.progress / (float) this.burstTime) * 100) + "%";
    }

    public Boolean isFinished() {
        return this.progress >= this.burstTime;
    }

    public Boolean canExecute(int timePassed) {
        return timePassed >= this.arrivalTime;
    }

    public void execute(Boolean usePriority) {

        this.progress++;

        if (usePriority) {
            this.priority++;
        }

    }

    public int compareTo(EscaletorProcess other, String escaletorTypeName, int timePassed) {

        Boolean isFinhed1 = this.isFinished();
        Boolean isFinhed2 = other.isFinished();

        if (!isFinhed1.equals(isFinhed2)) {

            return isFinhed1 ? +1 : -1;

        }

        Boolean canExecute1 = this.canExecute(timePassed);
        Boolean canExecute2 = other.canExecute(timePassed);

        if (!canExecute1.equals(canExecute2)) {

            return canExecute1 ? -1 : +1;

        }

        if (escaletorTypeName.equals(EscaletorType.SJF.name())) {

            int thisTime = this.burstTime;
            int otherTime = other.burstTime;

            if (thisTime < otherTime) {
                return -1;
            }

            if (thisTime > otherTime) {
                return 1;
            }

            return 0;
        }

        if (escaletorTypeName.equals(EscaletorType.SRTN.name())) {
            int thisTimeToProcess = this.burstTime - this.progress;
            int otherTimeToProcess = other.burstTime - other.progress;

            if (thisTimeToProcess < otherTimeToProcess) {
                return -1;
            }

            if (thisTimeToProcess > otherTimeToProcess) {
                return 1;
            }

            return 0;
        }

        if (escaletorTypeName.equals(EscaletorType.RoundRobin.name())) {
            //se esse estiver finalizado e esse não 

        }

        if (escaletorTypeName.equals(EscaletorType.Priority.name())) {
            if (this.priority > other.priority) {
                return -1;
            }

            if (this.priority < other.priority) {
                return 1;
            }

            return 0;
        }

        if (escaletorTypeName.equals(EscaletorType.MultipleQueues.name())) {

        }

        return 0;

    }

    public Boolean equals(EscaletorProcess other) {
        return this.id == other.id;
    }

    public enum Status {
        NOT_INITIATED,
        WAITING,
        EXECUTING,
        FINISHED,
    }

    public enum EscaletorType {
        SJF,
        SRTN,
        RoundRobin,
        Priority,
        MultipleQueues,
    }
    
    @Override
    public String toString(){
        return "Processo " + this.id + " BurstTime = " + this.burstTime + " ArrivalTime = " + this.arrivalTime  + " Progress = " + this.getProgressFormated() + " Priority = " + this.priority ;
    }
    

    public EscaletorProcess clone(){
        return new EscaletorProcess(this.id,this.burstTime,this.arrivalTime,this.priority);
    }

}
