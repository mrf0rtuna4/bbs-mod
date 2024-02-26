package mchorse.bbs_mod.settings.values;

import mchorse.bbs_mod.l10n.L10n;

/**
 * Value language.
 *
 * <p>This value subclass stores language localization ID. IMPORTANT: the
 * language strings don't get reloaded automatically! You need to attach a
 * callback to the value.</p>
 */
public class ValueLanguage extends ValueString
{
    public ValueLanguage(String id)
    {
        super(id, L10n.DEFAULT_LANGUAGE);
    }

//    TODO: @Override
//    public List<UIElement> getFields(UIElement ui)
//    {
//        UIButton button = new UIButton(UIKeys.LANGUAGE_PICK, (b) ->
//        {
//            List<Label<String>> labels = BBS.getL10n().getSupportedLanguageLabels();
//            UILabelOverlayPanel<String> panel = new UILabelOverlayPanel<>(UIKeys.LANGUAGE_PICK_TITLE, labels, (str) -> this.set(str.value));
//
//            panel.set(this.get());
//            UIOverlay.addOverlay(ui.getContext(), panel);
//        });
//
//        button.w(90);
//
//        UIText credits = new UIText().text(UIKeys.LANGUAGE_CREDITS).updates();
//
//        return Arrays.asList(UIValueFactory.column(button, this), credits.marginBottom(8));
//    }
}