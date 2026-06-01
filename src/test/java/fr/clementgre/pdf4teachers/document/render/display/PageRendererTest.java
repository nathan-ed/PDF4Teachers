/*
 * Copyright (c) 2026. Clément Grennerat
 * All rights reserved. You must refer to the licence Apache 2.
 */

package fr.clementgre.pdf4teachers.document.render.display;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageRendererTest {
    
    @Test
    void renderWidthUsesSameFormulaForPreloadAndRenderCacheKeys(){
        assertEquals((int) (PageRenderer.PAGE_WIDTH * 1.4 * 1.5), PageRenderer.getRenderWidth(1.5));
    }
    
    @Test
    void renderWidthNeverDropsBelowOnePixel(){
        assertEquals(1, PageRenderer.getRenderWidth(0));
        assertEquals(1, PageRenderer.getRenderWidth(-10));
    }
}
