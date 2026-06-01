/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.datasaving.simpleconfigs;

import fr.clementgre.pdf4teachers.datasaving.Config;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import javafx.application.Platform;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ExerciseCorrectionData extends SimpleConfig {
    
    private static final ScheduledExecutorService saveScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
        Thread thread = new Thread(runnable, "ExerciseCorrectionData saver");
        thread.setDaemon(true);
        return thread;
    });
    private static ScheduledFuture<?> scheduledSave;
    
    public ExerciseCorrectionData(){
        super("exercisecorrection");
    }
    
    public static synchronized void requestSave(){
        if(scheduledSave != null) scheduledSave.cancel(false);
        scheduledSave = saveScheduler.schedule(() -> new ExerciseCorrectionData().saveData(), 1, TimeUnit.SECONDS);
    }
    
    @Override
    protected void manageLoadedData(Config config){
        Platform.runLater(() -> {
            if(MainWindow.footerBar == null) return;
            
            MainWindow.footerBar.setSelectedExerciseKey(config.getString("selectedExercise"));
            applyPageIndexes(MainWindow.footerBar.getExercisePageMapping(), config.getSection("pageIndexes"));
            MainWindow.footerBar.refreshExerciseChoices();
            MainWindow.filesTab.preloadNeighborExercisePages();
        });
    }
    
    @Override
    protected void unableToLoadConfig(){
    }
    
    @Override
    protected void addDataToConfig(Config config){
        if(MainWindow.footerBar == null) return;
        
        config.set("selectedExercise", MainWindow.footerBar.getSelectedExerciseKey());
        
        config.set("pageIndexes", toConfigMap(MainWindow.footerBar.getExercisePageMapping()));
    }
    
    static LinkedHashMap<String, Object> toConfigMap(fr.clementgre.pdf4teachers.panel.sidebar.grades.ExercisePageMapping mapping){
        LinkedHashMap<String, Object> pageIndexes = new LinkedHashMap<>();
        mapping.getPageIndexes().forEach(pageIndexes::put);
        return pageIndexes;
    }
    
    static void applyPageIndexes(fr.clementgre.pdf4teachers.panel.sidebar.grades.ExercisePageMapping mapping, Map<String, Object> pageIndexes){
        for(Map.Entry<String, Object> entry : pageIndexes.entrySet()){
            try{
                mapping.setPageIndex(entry.getKey(), Integer.parseInt(entry.getValue().toString()));
            }catch(NumberFormatException ignored){
                // Ignore malformed user config entries.
            }
        }
    }
}
