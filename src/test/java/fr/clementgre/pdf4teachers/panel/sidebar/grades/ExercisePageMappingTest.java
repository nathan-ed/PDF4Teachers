/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar.grades;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ExercisePageMappingTest {
    
    @Test
    void buildsSelectorKeysFromQuestionCount(){
        assertEquals(List.of("Q1", "Q2", "Q3"), ExercisePageMapping.buildQuestionKeys(3));
    }
    
    @Test
    void rejectsNegativeQuestionCount(){
        assertThrows(IllegalArgumentException.class, () -> ExercisePageMapping.buildQuestionKeys(-1));
    }
    
    @Test
    void storesOneBasedPagesAsZeroBasedIndexes(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        
        mapping.setOneBasedPage("Q3", 6, 12);
        
        assertEquals(5, mapping.getPageIndex("Q3").orElseThrow());
    }
    
    @Test
    void rejectsPagesOutsideDocumentBounds(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        
        assertThrows(IllegalArgumentException.class, () -> mapping.setOneBasedPage("Q1", 0, 4));
        assertThrows(IllegalArgumentException.class, () -> mapping.setOneBasedPage("Q1", 5, 4));
    }
    
    @Test
    void subQuestionInheritsNearestParentPage(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        mapping.setOneBasedPage("Q3", 4, 10);
        
        assertEquals(3, mapping.resolvePageIndex("Q3/a").orElseThrow());
        assertEquals(3, mapping.resolvePageIndex("Q3/a/i").orElseThrow());
    }
    
    @Test
    void explicitSubQuestionPageOverridesParentPage(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        mapping.setOneBasedPage("Q3", 4, 10);
        mapping.setOneBasedPage("Q3/b", 7, 10);
        
        assertEquals(6, mapping.resolvePageIndex("Q3/b").orElseThrow());
        assertEquals(6, mapping.resolvePageIndex("Q3/b/i").orElseThrow());
        assertEquals(3, mapping.resolvePageIndex("Q3/a").orElseThrow());
    }
    
    @Test
    void correctionModeCanFallbackWhenExerciseIsUnmapped(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        mapping.setOneBasedPage("Q2", 5, 8);
        
        assertEquals(4, mapping.resolvePageIndexOrDefault("Q2", 0));
        assertEquals(2, mapping.resolvePageIndexOrDefault("Q3", 2));
    }
    
    @Test
    void pageIndexesAreReadOnlyFromOutside(){
        ExercisePageMapping mapping = new ExercisePageMapping();
        mapping.setOneBasedPage("Q1", 2, 5);
        
        Map<String, Integer> snapshot = mapping.getPageIndexes();
        
        assertThrows(UnsupportedOperationException.class, () -> snapshot.put("Q2", 3));
        assertEquals(1, mapping.getPageIndex("Q1").orElseThrow());
    }
    
    @Test
    void childKeysUseStableSeparator(){
        assertEquals("Q4/partA", ExercisePageMapping.childKey("Q4", "partA"));
    }
}
