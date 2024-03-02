package mchorse.bbs_mod.ui.forms.editors.forms;

import mchorse.bbs_mod.forms.forms.ParticleForm;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.framework.elements.buttons.UIButton;

public class UIParticleForm extends UIForm<ParticleForm>
{
    public UIParticleForm()
    {
        super();

        this.registerDefaultPanels();

        this.defaultPanel = this.panels.get(this.panels.size() - 1);

        this.defaultPanel.options.prepend(new UIButton(UIKeys.FORMS_EDITORS_BILLBOARD_PICK_TEXTURE, (b) ->
        {
            /* TODO: Link texture = this.form.texture.get();

            if (this.form.getEmitter() != null && texture == null)
            {
                texture = this.form.getEmitter().scheme.texture;
            }

            UITexturePicker.open(this.defaultPanel, texture, (l) -> this.form.texture.set(l)); */
        }).marginBottom(6));
    }
}