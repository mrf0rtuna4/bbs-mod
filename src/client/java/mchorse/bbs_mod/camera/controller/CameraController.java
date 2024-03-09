package mchorse.bbs_mod.camera.controller;

import mchorse.bbs_mod.camera.Camera;
import org.joml.Vector3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CameraController implements ICameraController
{
    public Camera camera = new Camera();
    private ICameraController current;
    private List<ICameraController> controllers = new ArrayList<>();

    private Vector3d prevPosition = new Vector3d();

    public void updateCurrent()
    {
        ICameraController current = null;

        for (ICameraController controller : this.controllers)
        {
            if (current == null)
            {
                current = controller;
            }
            else if (controller.getPriority() > current.getPriority())
            {
                current = controller;
            }
        }

        this.current = current;
    }

    public ICameraController getCurrent()
    {
        return this.current;
    }

    public void add(ICameraController controller)
    {
        this.controllers.add(controller);
        this.updateCurrent();
    }

    public void remove(Class clazz)
    {
        Iterator<ICameraController> it = this.controllers.iterator();

        while (it.hasNext())
        {
            if (it.next().getClass() == clazz)
            {
                it.remove();
            }
        }

        this.updateCurrent();
    }

    public void remove(ICameraController controller)
    {
        Iterator<ICameraController> it = this.controllers.iterator();

        while (it.hasNext())
        {
            if (it.next() == controller)
            {
                it.remove();
            }
        }

        this.updateCurrent();
    }

    @Override
    public void update()
    {
        if (this.current != null)
        {
            this.current.update();
        }
    }

    @Override
    public void setup(Camera camera, float transition)
    {
        if (this.current != null)
        {
            this.current.setup(camera, transition);
        }
    }

    public void resize(int width, int height)
    {
        this.camera.updatePerspectiveProjection(width, height);
    }

    public boolean has(ICameraController controller)
    {
        return this.controllers.contains(controller);
    }

    public boolean has(Class clazz)
    {
        for (ICameraController controller : this.controllers)
        {
            if (controller.getClass() == clazz)
            {
                return true;
            }
        }

        return false;
    }
}