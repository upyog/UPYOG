package com.cdac.esign.eventListener;

import java.util.HashSet;
import java.util.Set;
import com.itextpdf.kernel.pdf.canvas.parser.EventType;
import com.itextpdf.kernel.pdf.canvas.parser.data.TextRenderInfo;
import com.itextpdf.kernel.pdf.canvas.parser.listener.IEventListener;

//Custom listener to track last text position
public class LastPositionListener implements IEventListener {
    private float lastX = -1;
    private float lastY = -1;
    private boolean found = false;

    @Override
    public void eventOccurred(com.itextpdf.kernel.pdf.canvas.parser.data.IEventData data, EventType type) {
        if (type.equals(EventType.RENDER_TEXT)) {
            TextRenderInfo renderInfo = (TextRenderInfo) data;
            // Get baseline start point of the text
            float x = renderInfo.getBaseline().getEndPoint().get(0);
            float y = renderInfo.getBaseline().getEndPoint().get(1);
            lastX = x;
            lastY = y;
            found = true;
        }
    }

    @Override
    public Set<EventType> getSupportedEvents() {
    	Set<EventType> set = new HashSet<>();
    	set.add(EventType.RENDER_TEXT);
        return set;
    }

    public boolean hasPosition() {
        return found;
    }

    public float getLastX() {
        return lastX;
    }

    public float getLastY() {
        return lastY;
    }
}
