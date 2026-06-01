/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar.grades;

import java.util.OptionalInt;

public class ExerciseCorrectionWorkflow {
    
    private ExerciseCorrectionWorkflow(){
    }
    
    public static OptionalInt getNavigationTarget(boolean exerciseCorrectionMode, OptionalInt selectedExercisePage, int pagesCount){
        if(!exerciseCorrectionMode || selectedExercisePage.isEmpty() || pagesCount <= 0) return OptionalInt.empty();
        return OptionalInt.of(Math.min(selectedExercisePage.getAsInt(), pagesCount - 1));
    }
    
    public static int getPrefetchLastPage(int targetPageIndex, int pagesCount){
        if(pagesCount <= 0) throw new IllegalArgumentException("pagesCount must be positive.");
        if(targetPageIndex < 0) throw new IllegalArgumentException("targetPageIndex must be positive or zero.");
        return Math.min(targetPageIndex + 1, pagesCount - 1);
    }
    
    public static String getNewGradeExerciseKey(boolean parentIsRoot, int parentChildrenCount, int topLevelParentIndex){
        if(parentIsRoot){
            if(parentChildrenCount < 0) throw new IllegalArgumentException("parentChildrenCount must be positive or zero.");
            return "Q" + (parentChildrenCount + 1);
        }
        if(topLevelParentIndex < 0) throw new IllegalArgumentException("topLevelParentIndex must be positive or zero.");
        return "Q" + (topLevelParentIndex + 1);
    }
}
