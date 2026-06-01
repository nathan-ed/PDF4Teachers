/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar.grades;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class ExercisePageMapping {
    
    public static final String PATH_SEPARATOR = "/";
    private final LinkedHashMap<String, Integer> pageIndexes = new LinkedHashMap<>();
    
    public static List<String> buildQuestionKeys(int questionCount){
        if(questionCount < 0) throw new IllegalArgumentException("questionCount must be positive or zero.");
        
        ArrayList<String> questions = new ArrayList<>();
        for(int i = 1; i <= questionCount; i++){
            questions.add("Q" + i);
        }
        return questions;
    }
    
    public void setOneBasedPage(String exerciseKey, int oneBasedPage, int pagesCount){
        if(oneBasedPage < 1 || oneBasedPage > pagesCount){
            throw new IllegalArgumentException("Page must be between 1 and " + pagesCount + ".");
        }
        setPageIndex(exerciseKey, oneBasedPage - 1);
    }
    
    public void setPageIndex(String exerciseKey, int pageIndex){
        validateExerciseKey(exerciseKey);
        if(pageIndex < 0) throw new IllegalArgumentException("pageIndex must be positive or zero.");
        
        pageIndexes.put(exerciseKey, pageIndex);
    }
    
    public void clearPageIndex(String exerciseKey){
        validateExerciseKey(exerciseKey);
        pageIndexes.remove(exerciseKey);
    }
    
    public OptionalInt getPageIndex(String exerciseKey){
        Integer pageIndex = pageIndexes.get(exerciseKey);
        if(pageIndex == null) return OptionalInt.empty();
        return OptionalInt.of(pageIndex);
    }
    
    public OptionalInt resolvePageIndex(String gradePath){
        validateExerciseKey(gradePath);
        
        String path = gradePath;
        while(!path.isEmpty()){
            OptionalInt exact = getPageIndex(path);
            if(exact.isPresent()) return exact;
            
            int separator = path.lastIndexOf(PATH_SEPARATOR);
            if(separator < 0) break;
            path = path.substring(0, separator);
        }
        return OptionalInt.empty();
    }
    
    public int resolvePageIndexOrDefault(String gradePath, int fallbackPageIndex){
        return resolvePageIndex(gradePath).orElse(fallbackPageIndex);
    }
    
    public Map<String, Integer> getPageIndexes(){
        return Collections.unmodifiableMap(pageIndexes);
    }
    
    public static String childKey(String parent, String child){
        validateExerciseKey(parent);
        validateExerciseKey(child);
        return parent + PATH_SEPARATOR + child;
    }
    
    private static void validateExerciseKey(String exerciseKey){
        if(exerciseKey == null || exerciseKey.isBlank()){
            throw new IllegalArgumentException("exerciseKey must not be blank.");
        }
    }
}
