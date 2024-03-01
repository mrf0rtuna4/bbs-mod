package mchorse.bbs_mod.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs_mod.ui.framework.elements.input.UIColor;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs_mod.utils.colors.Color;
import mchorse.bbs_mod.utils.keyframes.generic.GenericKeyframe;

public class UIColorKeyframeFactory extends UIKeyframeFactory<Color>
{
    private UIColor color;

    public UIColorKeyframeFactory(GenericKeyframe<Color> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        this.color = new UIColor((c) -> this.editor.setValue(Color.rgba(c.intValue())));
        this.color.setColor(keyframe.getValue().getARGBColor());
        this.color.withAlpha();

        this.add(this.color);
    }
}