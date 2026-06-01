/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar.grades;

import org.junit.jupiter.api.Test;

import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseCorrectionWorkflowTest {
    
    @Test
    void navigationTargetIsEmptyWhenModeIsOff(){
        OptionalInt target = ExerciseCorrectionWorkflow.getNavigationTarget(false, OptionalInt.of(3), 10);
        
        assertTrue(target.isEmpty());
    }
    
    @Test
    void navigationTargetIsEmptyWithoutMappedExercise(){
        OptionalInt target = ExerciseCorrectionWorkflow.getNavigationTarget(true, OptionalInt.empty(), 10);
        
        assertTrue(target.isEmpty());
    }
    
    @Test
    void navigationTargetClampsToDocumentPageCount(){
        OptionalInt target = ExerciseCorrectionWorkflow.getNavigationTarget(true, OptionalInt.of(12), 8);
        
        assertTrue(target.isPresent());
        assertEquals(7, target.getAsInt());
    }
    
    @Test
    void navigationTargetRejectsEmptyDocuments(){
        OptionalInt target = ExerciseCorrectionWorkflow.getNavigationTarget(true, OptionalInt.of(0), 0);
        
        assertTrue(target.isEmpty());
    }
    
    @Test
    void prefetchRangeIncludesTargetAndNextPage(){
        assertEquals(4, ExerciseCorrectionWorkflow.getPrefetchLastPage(3, 10));
    }
    
    @Test
    void prefetchRangeClampsOnLastPage(){
        assertEquals(9, ExerciseCorrectionWorkflow.getPrefetchLastPage(9, 10));
    }
    
    @Test
    void prefetchRangeRejectsInvalidInput(){
        assertThrows(IllegalArgumentException.class, () -> ExerciseCorrectionWorkflow.getPrefetchLastPage(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> ExerciseCorrectionWorkflow.getPrefetchLastPage(0, 0));
    }
    
    @Test
    void newTopLevelGradeUsesNextExerciseKey(){
        assertEquals("Q4", ExerciseCorrectionWorkflow.getNewGradeExerciseKey(true, 3, -1));
    }
    
    @Test
    void newSubGradeInheritsTopLevelExerciseKey(){
        assertEquals("Q3", ExerciseCorrectionWorkflow.getNewGradeExerciseKey(false, -1, 2));
    }
    
    @Test
    void newGradeExerciseKeyRejectsInvalidIndexes(){
        assertThrows(IllegalArgumentException.class, () -> ExerciseCorrectionWorkflow.getNewGradeExerciseKey(true, -1, -1));
        assertThrows(IllegalArgumentException.class, () -> ExerciseCorrectionWorkflow.getNewGradeExerciseKey(false, -1, -1));
    }
}
