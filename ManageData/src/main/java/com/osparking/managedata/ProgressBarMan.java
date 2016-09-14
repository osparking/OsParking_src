/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
/**
 * Modified by Open Source Parking, Inc. 
 * on Sept. 14, 2016.
 * Company website : www.osparking.com
 */
package com.osparking.managedata;

import java.awt.*;
import javax.swing.*;
import java.beans.*;
import java.util.Random;

public class ProgressBarMan extends JPanel
        implements PropertyChangeListener 
{
    private JProgressBar progressBar;
    private Task task;
    private JFrame frame;
    private int target = 0;
    private int current = 0;
    ProgressBarChanger changer = new ProgressBarChanger();

    public ProgressBarMan(DataGUI frame) {
        super(new BorderLayout());
        this.frame = frame;
        progressBar = frame.getProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(true);
    }
    
    private class ProgressBarChanger extends Thread  implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (evt.getPropertyName().equals("progress")) {
                int progress = (Integer) evt.getNewValue();

                System.out.println("Bar progress : " + progress);
                progressBar.setValue(progress);               
            }
        }
    }
    /**
     * @param target the target to set
     */
    public void initializeBar(int target) {
        this.target = target;
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //Instances of javax.swing.SwingWorker are not reusuable, so
        //we create new instances as needed.
        task = new Task();
        
        task.addPropertyChangeListener(changer);
//        task.addPropertyChangeListener(this);
        task.execute();        
    }

    /**
     * @param current the current to set
     */
    public void changeCurrent(int current) {
        this.current = current;
    }

    class Task extends SwingWorker<Void, Void> {
        /*
         * Main task. Executed in background thread.
         */
        @Override
        public Void doInBackground() {
            int progress = 0;
            int progBfre = 0;
            //Initialize progress property.
            setProgress(0);
            while (progress < 100) {
                //Sleep for up to one second.
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignore) {}
                //Make random progress.
                progBfre = progress;
                progress = (int)(current/(float)target * 100); 
                setProgress(Math.min(progress, 100));
                firePropertyChange("progress", progBfre, Math.min(progress, 100));                
                System.out.println("Progress in Loop : " + progress);
            }
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            Toolkit.getDefaultToolkit().beep();
            setCursor(null); //turn off the wait cursor
//            frame.dispose();
        }
    }

    /**
     * Invoked when task's progress property changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getPropertyName().equals("progress")) {
//            int progress = (Integer) evt.getNewValue();
//                
//            System.out.println("Bar progress : " + progress);
//            progressBar.setValue(progress);               
//        }
    }

    /**
     * Create the GUI and show it. As with all GUI code, this must run
     * on the event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("자료 생성율");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //Create and set up the content pane.
        JComponent newContentPane = new ProgressBarMan(null);
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
//        ((ProgressBar)newContentPane).startSimulation();
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
