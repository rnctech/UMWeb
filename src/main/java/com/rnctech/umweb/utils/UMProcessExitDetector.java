package com.rnctech.umweb.utils;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public class UMProcessExitDetector extends Thread {
	 
    private Process process;
    private List<ProcessListener> listeners = new ArrayList<>();
 
    public UMProcessExitDetector(Process process) {
        try {
            process.exitValue();
            throw new IllegalArgumentException("The process is already ended");
        } catch (IllegalThreadStateException exc) {
            this.process = process;
        }
    }

    public Process getProcess() {
        return process;
    }
 
    public void run() {
        try {
            process.waitFor();
            for (ProcessListener listener : listeners) {
                listener.processFinished(process);
            }
        } catch (InterruptedException e) {
        }
    }
 
    public void addProcessListener(ProcessListener listener) {
        listeners.add(listener);
    }
 
    public void removeProcessListener(ProcessListener listener) {
        listeners.remove(listener);
    }
    
    public interface ProcessListener extends EventListener {
        void processFinished(Process process);
    }
}