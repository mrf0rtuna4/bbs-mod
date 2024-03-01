package mchorse.bbs_mod.ui.framework.elements.input.keyframes.generic.factories;

import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;
import mchorse.bbs_mod.ui.framework.elements.input.UITexturePicker;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.generic.UIPropertyEditor;
import mchorse.bbs_mod.utils.keyframes.generic.GenericKeyframe;

public class UILinkKeyframeFactory extends UIKeyframeFactory<Link>
{
    public UILinkKeyframeFactory(GenericKeyframe<Link> keyframe, UIPropertyEditor editor)
    {
        super(keyframe, editor);

        UIButton button = new UIButton(UIKeys.GENERIC_KEYFRAMES_LINK_PICK_TEXTURE, (b) ->
        {
            UITexturePicker.open(this.getParentContainer(), this.keyframe.getValue(), this.keyframe::setValue);
        });

        this.add(button);
    }
}