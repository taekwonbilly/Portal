/**
 * Copyright (c) 2008-2011 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package com.ardor3d.framework.lwjgl;

import java.awt.Dimension;
import java.util.concurrent.CountDownLatch;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.AWTGLCanvas;
import org.lwjgl.opengl.PixelFormat;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.Canvas;
import backbone.*;

// TODO: Auto-generated Javadoc
/**
 * The Class LwjglAwtCanvas.
 */
public class LwjglAwtCanvas extends AWTGLCanvas implements Canvas {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The _inited. */
    private boolean _inited = false;

    /** The _updated. */
    private volatile boolean _updated = false;
    // _latch would have to be volatile if we are not careful with the order of reads and writes between this one and
    // '_updated'
    /** The _latch. */
    private CountDownLatch _latch = null;

    /**
	 * Instantiates a new lwjgl awt canvas.
	 * 
	 * @throws LWJGLException
	 *             the lWJGL exception
	 */
    public LwjglAwtCanvas() throws LWJGLException {
        super(new PixelFormat(Constants.settings.getColorDepth(), Constants.settings.getAlphaBits(), Constants.settings.getDepthBits(), Constants.settings
                .getStencilBits(), Constants.settings.getSamples()).withStereo(Constants.settings.isStereo()));
        backbone.Main.canvasRenderer.setCanvasCallback(new LwjglCanvasCallback() {
     //       @Override
            public void makeCurrent() throws LWJGLException {
                LwjglAwtCanvas.this.makeCurrent();
            }

      //      @Override
            public void releaseContext() throws LWJGLException {
                LwjglAwtCanvas.this.releaseContext();
            }
        });
        setFocusable(true);
        addKeyListener(Main._controlHandle);
		addMouseListener(Main._controlHandle);
		addMouseMotionListener(Main._controlHandle);
        setSize(new Dimension(Constants.drawX, Constants.drawY));
        setVisible(true);
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.Canvas#draw(java.util.concurrent.CountDownLatch)
     */
    public void draw(final CountDownLatch latch) {
        if (!shouldDraw(latch)) {
            latch.countDown();
            return;
        }

        // need to set _latch before _updated, for memory consistency reasons
        _latch = latch;
        _updated = true;
        repaint();
    }

    /**
	 * Should draw.
	 * 
	 * @param latch
	 *            the latch
	 * @return true, if successful
	 */
    private boolean shouldDraw(final CountDownLatch latch) {
        final boolean showing = isShowing();
        final boolean lastUpdateComplete = latch == null || !_updated;
        return showing && lastUpdateComplete;
    }

    /* (non-Javadoc)
     * @see org.lwjgl.opengl.AWTGLCanvas#paintGL()
     */
    @Override
    @MainThread
    protected void paintGL() {
        if (!_inited) {
            getCanvasRenderer().init(Constants.settings, false); // false - do not do back buffer swap, awt will do that.
            getCanvasRenderer().getCamera().resize(getWidth(), getHeight());
            _inited = true;
        }

        if (_latch != null && !_updated) {
            return;
        }

        try {
            if (getCanvasRenderer().draw()) {
                swapBuffers();
            }
        } catch (final LWJGLException e) {
            throw new RuntimeException(e);
        } finally {
            // release our context - because swap is external, we release here instead.
        	getCanvasRenderer().releaseCurrentContext();
        }

        if (_latch != null) {
            _updated = false;
            _latch.countDown();
        }
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.Canvas#init()
     */
    public void init() {
        ; // ignore - can only be inited inside our paintGL
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.Canvas#getCanvasRenderer()
     */
    public LwjglCanvasRenderer getCanvasRenderer() {
        return Main.canvasRenderer;
    }
}
