/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.panel.sidebar.grades;

import fr.clementgre.pdf4teachers.document.editions.Edition;
import fr.clementgre.pdf4teachers.document.editions.elements.Element;
import fr.clementgre.pdf4teachers.document.editions.elements.TextElement;
import fr.clementgre.pdf4teachers.document.editions.undoEngine.UType;
import fr.clementgre.pdf4teachers.document.render.display.PageRenderer;
import fr.clementgre.pdf4teachers.interfaces.windows.MainWindow;
import fr.clementgre.pdf4teachers.utils.fonts.FontUtils;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;

public class PNFAnnotationManager {
    
    private static final Color PNF_COLOR = Color.web("#b31a1a");
    private static final Font PNF_FONT = FontUtils.getFont("Open Sans", false, false, 24);
    private static final Font PNF_TABLE_FONT = FontUtils.getFont("Open Sans", false, false, 18);
    private static final String PNF_TEXT = "PNF";
    private static final String PNF_MARK = "I";
    static final int MAX_MARKS_PER_EXERCISE = 4;
    static final int TABLE_X = 3199;
    static final int TABLE_HEADER_Y = 130459;
    static final int TABLE_ROW_STEP = (int) (Element.GRID_HEIGHT * .035);
    
    private PNFAnnotationManager(){
    }
    
    public static void addPNF(PageRenderer page, double pageX, double pageY){
        if(!MainWindow.mainScreen.hasDocument(false)) return;
        
        int exerciseIndex = getSelectedExerciseIndex();
        int rowCount = getPNFRowCount();
        if(exerciseIndex < 0 || exerciseIndex >= rowCount){
            MainWindow.footerBar.showToast(Color.web("#6a1b1b"), Color.WHITE, "Select an exercise row before adding PNF.");
            return;
        }
        
        addPNFAnnotation(page, pageX, pageY);
        incrementSummaryMark(exerciseIndex, rowCount);
        Edition.setUnsave("PNF annotation added");
        MainWindow.mainScreen.document.edition.save(false);
    }
    
    private static void addPNFAnnotation(PageRenderer page, double pageX, double pageY){
        TextElement element = new TextElement(page.toGridX(pageX), page.toGridY(pageY), page.getPage(),
                true, PNF_TEXT, PNF_COLOR, PNF_FONT, 0);
        page.addElement(element, true, UType.ELEMENT);
        element.centerOnCoordinatesY();
    }
    
    private static void incrementSummaryMark(int exerciseIndex, int rowCount){
        PageRenderer firstPage = MainWindow.mainScreen.document.getPage(0);
        ensureSummaryHeader(firstPage);
        ensureSummaryRows(firstPage, rowCount);
        
        TextElement row = getSummaryRows(firstPage).get(exerciseIndex);
        row.setText(addMarkToRowText(row.getText(), exerciseIndex));
    }
    
    private static void ensureSummaryHeader(PageRenderer firstPage){
        if(getSummaryHeader(firstPage).isPresent()) return;
        
        TextElement header = new TextElement(TABLE_X, TABLE_HEADER_Y, 0, true, PNF_TEXT, PNF_COLOR, PNF_TABLE_FONT, 0);
        firstPage.addElement(header, true, UType.ELEMENT);
    }
    
    private static void ensureSummaryRows(PageRenderer firstPage, int rowCount){
        ArrayList<TextElement> rows = getSummaryRows(firstPage);
        for(int i = rows.size(); i < rowCount; i++){
            TextElement row = new TextElement(TABLE_X, getRowY(i), 0, true, getRowLabel(i), PNF_COLOR, PNF_TABLE_FONT, 0);
            firstPage.addElement(row, true, UType.ELEMENT);
        }
    }
    
    private static Optional<TextElement> getSummaryHeader(PageRenderer firstPage){
        return firstPage.getElements().stream()
                .filter(TextElement.class::isInstance)
                .map(TextElement.class::cast)
                .filter(element -> element.getPageNumber() == 0)
                .filter(element -> PNF_TEXT.equals(element.getText()))
                .filter(element -> Math.abs(element.getRealX() - TABLE_X) < 1000)
                .filter(element -> Math.abs(element.getRealY() - TABLE_HEADER_Y) < 1000)
                .findFirst();
    }
    
    private static ArrayList<TextElement> getSummaryRows(PageRenderer firstPage){
        return firstPage.getElements().stream()
                .filter(TextElement.class::isInstance)
                .map(TextElement.class::cast)
                .filter(element -> element.getPageNumber() == 0)
                .filter(element -> isPNFMarkRow(element.getText()))
                .filter(element -> Math.abs(element.getRealX() - TABLE_X) < 1000)
                .filter(element -> element.getRealY() >= getRowY(0) - 1000)
                .sorted(Comparator.comparingInt(Element::getRealY))
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new));
    }
    
    static String addMarkToRowText(String text, int rowIndex){
        int marks = countMarks(text);
        if(marks >= MAX_MARKS_PER_EXERCISE) return text;
        
        String label = getRowLabel(rowIndex);
        String marksText = getMarksText(text);
        return label + (marksText.isBlank() ? PNF_MARK : marksText + "  " + PNF_MARK);
    }
    
    static boolean isPNFMarkRow(String text){
        return text.isBlank() || text.matches("\\d+\\.\\s*(I\\s*)*");
    }
    
    static int countMarks(String text){
        return (int) text.chars().filter(c -> c == 'I').count();
    }
    
    static String getMarksText(String text){
        return text.replaceFirst("^\\d+\\.\\s*", "");
    }
    
    static String getRowLabel(int rowIndex){
        return (rowIndex + 1) + ". ";
    }
    
    private static int getSelectedExerciseIndex(){
        String selectedExercise = MainWindow.footerBar.getSelectedExerciseKey();
        if(selectedExercise == null || !selectedExercise.startsWith("Q")) return -1;
        
        try{
            return Integer.parseInt(selectedExercise.substring(1)) - 1;
        }catch(NumberFormatException e){
            return -1;
        }
    }
    
    private static int getPNFRowCount(){
        return Math.max(0, MainWindow.footerBar.getExerciseCount() - 1);
    }
    
    static int getRowY(int rowIndex){
        return TABLE_HEADER_Y + TABLE_ROW_STEP * (rowIndex + 1);
    }
}
