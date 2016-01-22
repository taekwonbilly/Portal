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

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ARBMultisample;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

import backbone.Constants;

import com.ardor3d.annotation.MainThread;
import com.ardor3d.framework.CanvasRenderer;
import com.ardor3d.framework.DisplaySettings;
import com.ardor3d.framework.Scene;
import com.ardor3d.math.ColorRGBA;
import com.ardor3d.math.Vector3;
import com.ardor3d.renderer.Camera;
import com.ardor3d.renderer.ContextCapabilities;
import com.ardor3d.renderer.ContextManager;
import com.ardor3d.renderer.RenderContext;
import com.ardor3d.renderer.Renderer;
import com.ardor3d.renderer.Camera.ProjectionMode;
import com.ardor3d.renderer.lwjgl.LwjglContextCapabilities;
import com.ardor3d.renderer.lwjgl.LwjglRenderer;
import com.ardor3d.util.Ardor3dException;

// TODO: Auto-generated Javadoc
/**
 * The Class LwjglCanvasRenderer.
 */
public class LwjglCanvasRenderer implements CanvasRenderer {
    
    /** The _scene. */
    protected Scene _scene;
    
    /** The _camera. */
    protected Camera _camera;
    
    /** The _do swap. */
    protected boolean _doSwap;
    
    /** The _renderer. */
    protected LwjglRenderer _renderer;
    
    /** The _context. */
    protected Object _context = new Object();
    
    /** The _frame clear. */
    protected int _frameClear = Renderer.BUFFER_COLOR_AND_DEPTH;

    /** The _current context. */
    private RenderContext _currentContext;
    
    /** The _canvas callback. */
    private LwjglCanvasCallback _canvasCallback;

    /**
	 * Instantiates a new lwjgl canvas renderer.
	 * 
	 * @param scene
	 *            the scene
	 */
    public LwjglCanvasRenderer(final Scene scene) {
        _scene = scene;
        initCamera(Constants.settings);
    }

    /**
	 * Creates the context capabilities.
	 * 
	 * @return the context capabilities
	 */
    @MainThread
    protected ContextCapabilities createContextCapabilities() {
        return new LwjglContextCapabilities(GLContext.getCapabilities());
    }
    
    /**
	 * Inits the camera.
	 * 
	 * @param settings
	 *            the settings
	 */
    public void initCamera(final DisplaySettings settings){
    	 if (_camera == null) {
             /** Set up how our camera sees. */
             _camera = new Camera(settings.getWidth(), settings.getHeight());
             _camera.setFrustumPerspective(45.0f, (float) settings.getWidth() / (float) settings.getHeight(), 1, 1000);
             _camera.setProjectionMode(ProjectionMode.Perspective);

             final Vector3 loc = new Vector3(0.0f, 0.0f, 10.0f);
             final Vector3 left = new Vector3(-1.0f, 0.0f, 0.0f);
             final Vector3 up = new Vector3(0.0f, 1.0f, 0.0f);
             final Vector3 dir = new Vector3(0.0f, 0f, -1.0f);
             /** Move our camera to a correct place and orientation. */
             _camera.setFrame(loc, left, up, dir);
         } else {
             // use new width and height to set ratio.
             _camera.setFrustumPerspective(_camera.getFovY(),
                     (float) settings.getWidth() / (float) settings.getHeight(), _camera.getFrustumNear(), _camera
                             .getFrustumFar());
         }
    }
    
    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#init(com.ardor3d.framework.DisplaySettings, boolean)
     */
    @MainThread
    public void init(final DisplaySettings settings, final boolean doSwap) {
        _doSwap = doSwap;

        // Look up a shared context, if a shared LwjglCanvasRenderer is given.
        // XXX: Shared contexts will probably not work... lwjgl does not seem to have a way to make a new glcontext that
        // shares lists, textures, etc.
        RenderContext sharedContext = null;
        if (settings.getShareContext() != null) {
            sharedContext = ContextManager.getContextForKey(settings.getShareContext().getRenderContext()
                    .getContextKey());
        }

        try {
            _canvasCallback.makeCurrent();
            GLContext.useContext(_context);
        } catch (final LWJGLException e) {
            throw new Ardor3dException("Unable to init CanvasRenderer.", e);
        }

        final ContextCapabilities caps = createContextCapabilities();
        _currentContext = new RenderContext(this, caps, sharedContext);

        ContextManager.addContext(this, _currentContext);
        ContextManager.switchContext(this);

        _renderer = new LwjglRenderer();

        if (settings.getSamples() != 0 && caps.isMultisampleSupported()) {
            GL11.glEnable(ARBMultisample.GL_MULTISAMPLE_ARB);
        }

        _renderer.setBackgroundColor(ColorRGBA.BLACK);

    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#draw()
     */
    @MainThread
    public boolean draw() {

        if (Constants.blue != null) {
            Constants.blue.count = 0;
        }
        if (Constants.orange != null) {
            Constants.orange.count = 0;
        }
        // set up context for rendering this canvas
        makeCurrentContext();

        // render stuff, first apply our camera if we have one
        if (_camera != null) {
            if (Camera.getCurrentCamera() != _camera) {
                _camera.update();
            }
            _camera.apply(_renderer);
        }
        _renderer.clearBuffers(_frameClear);

        final boolean drew = _scene.renderUnto(_renderer);
        _renderer.flushFrame(drew && _doSwap);

        // release the context if we're done (swapped and all)
        if (_doSwap) {
            releaseCurrentContext();
        }

        return drew;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#getCamera()
     */
    public Camera getCamera() {
        return _camera;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#getScene()
     */
    public Scene getScene() {
        return _scene;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#setScene(com.ardor3d.framework.Scene)
     */
    public void setScene(final Scene scene) {
        _scene = scene;
    }

    /**
	 * Gets the canvas callback.
	 * 
	 * @return the canvas callback
	 */
    public LwjglCanvasCallback getCanvasCallback() {
        return _canvasCallback;
    }

    /**
	 * Sets the canvas callback.
	 * 
	 * @param canvasCallback
	 *            the new canvas callback
	 */
    public void setCanvasCallback(final LwjglCanvasCallback canvasCallback) {
        _canvasCallback = canvasCallback;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#getRenderer()
     */
    public Renderer getRenderer() {
        return _renderer;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#makeCurrentContext()
     */
    public void makeCurrentContext() throws Ardor3dException {
        try {
            _canvasCallback.makeCurrent();
            GLContext.useContext(_context);
            ContextManager.switchContext(this);
        } catch (final LWJGLException e) {
            throw new Ardor3dException("Failed to claim OpenGL context.", e);
        }
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#releaseCurrentContext()
     */
    public void releaseCurrentContext() {
        try {
            GLContext.useContext(null);
            _canvasCallback.releaseContext();
        } catch (final LWJGLException e) {
            throw new RuntimeException(e);
        }
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#setCamera(com.ardor3d.renderer.Camera)
     */
    public void setCamera(final Camera camera) {
        _camera = camera;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#getRenderContext()
     */
    public RenderContext getRenderContext() {
        return _currentContext;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#getFrameClear()
     */
    public int getFrameClear() {
        return _frameClear;
    }

    /* (non-Javadoc)
     * @see com.ardor3d.framework.CanvasRenderer#setFrameClear(int)
     */
    public void setFrameClear(final int buffers) {
        _frameClear = buffers;
    }
}