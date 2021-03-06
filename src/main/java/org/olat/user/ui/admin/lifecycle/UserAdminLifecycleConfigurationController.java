/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.user.ui.admin.lifecycle;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.FormItem;
import org.olat.core.gui.components.form.flexible.FormItemContainer;
import org.olat.core.gui.components.form.flexible.elements.FormLink;
import org.olat.core.gui.components.form.flexible.elements.MultipleSelectionElement;
import org.olat.core.gui.components.form.flexible.elements.StaticTextElement;
import org.olat.core.gui.components.form.flexible.elements.TextElement;
import org.olat.core.gui.components.form.flexible.impl.FormBasicController;
import org.olat.core.gui.components.form.flexible.impl.FormEvent;
import org.olat.core.gui.components.form.flexible.impl.FormLayoutContainer;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.closablewrapper.CloseableModalController;
import org.olat.core.util.i18n.ui.SingleKeyTranslatorController;
import org.olat.user.UserModule;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * Initial date: 18 mai 2020<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserAdminLifecycleConfigurationController extends FormBasicController {
	
	private static final String[] onKeys = new String[] { "on" };
	
	private MultipleSelectionElement enableDeactivationEl;
	private TextElement numberOfInactiveDayDeactivationEl;
	private TextElement numberOfDayBeforeDeactivationMailEl;
	private MultipleSelectionElement enableMailBeforeDeactivationEl;
	private MultipleSelectionElement enableMailAfterDeactivationEl;
	private TranslationBundles mailBeforeDeactivationBundles;
	private TranslationBundles mailAfterDeactivationBundles;
	
	private MultipleSelectionElement enableDeletionEl;
	private TextElement numberOfInactiveDayDeletionEl;
	private TextElement numberOfDayBeforeDeletionMailEl;
	private MultipleSelectionElement enableMailBeforeDeletionEl;
	private MultipleSelectionElement enableMailAfterDeletionEl;
	private TranslationBundles mailBeforeDeletionBundles;
	private TranslationBundles mailAfterDeletionBundles;
	
	private int counter = 0;
	
	private CloseableModalController cmc;
	private SingleKeyTranslatorController translatorCtrl;
	
	@Autowired
	private UserModule userModule;
	
	public UserAdminLifecycleConfigurationController(UserRequest ureq, WindowControl wControl) {
		super(ureq, wControl);
		
		initForm(ureq);
		updateUI();
	}

	@Override
	protected void initForm(FormItemContainer formLayout, Controller listener, UserRequest ureq) {
		setFormTitle("admin.configuration.title");
		
		initDeactivationForm(formLayout);
		uifactory.addSpacerElement("del-deac", formLayout, false);
		initDeletionForm(formLayout);
		
		FormLayoutContainer buttonsLayout = FormLayoutContainer.createButtonLayout("save", getTranslator());
		formLayout.add(buttonsLayout);
		uifactory.addFormSubmitButton("save", buttonsLayout);
	}
	

	protected void initDeactivationForm(FormItemContainer formLayout) {
		String[] onValues = new String[] { translate("enabled") };
		enableDeactivationEl = uifactory.addCheckboxesHorizontal("enable.deactivation", "enable.deactivation", formLayout, onKeys, onValues);
		enableDeactivationEl.addActionListener(FormEvent.ONCHANGE);
		enableDeactivationEl.select(onKeys[0], userModule.isUserAutomaticDeactivation());
		
		// day inactivity
		String daysBefore = Integer.toString(userModule.getNumberOfInactiveDayBeforeDeactivation());
		numberOfInactiveDayDeactivationEl = uifactory.addTextElement("num.inactive.day.deactivation", "num.inactive.day.deactivation", 4, daysBefore, formLayout);
		initDays(numberOfInactiveDayDeactivationEl);

		// enable mail before
		enableMailBeforeDeactivationEl = uifactory.addCheckboxesHorizontal("enable.mail.before.deactivation", "enable.mail.before.deactivation", formLayout, onKeys, onValues);
		enableMailBeforeDeactivationEl.addActionListener(FormEvent.ONCHANGE);
		enableMailBeforeDeactivationEl.select(onKeys[0], userModule.isMailBeforeDeactivation());
		
		// day before
		String daysBeforeMail = Integer.toString(userModule.getNumberOfDayBeforeDeactivationMail());
		numberOfDayBeforeDeactivationMailEl = uifactory.addTextElement("num.day.before.mail.deactivation", "num.day.before.mail.deactivation", 4, daysBeforeMail, formLayout);
		initDays(numberOfDayBeforeDeactivationMailEl);
		
		// subject + content mail
		TranslationBundle beforeBundleSubject = initForm("mail.before.deactivation.subject.label", "mail.before.deactivation.subject", formLayout);
		TranslationBundle beforeBundle = initForm("mail.before.deactivation.body.label", "mail.before.deactivation.body", formLayout);
		mailBeforeDeactivationBundles = new TranslationBundles(beforeBundleSubject, beforeBundle);

		// enable mail after
		enableMailAfterDeactivationEl = uifactory.addCheckboxesHorizontal("enable.mail.after.deactivation", "enable.mail.after.deactivation", formLayout, onKeys, onValues);
		enableMailAfterDeactivationEl.addActionListener(FormEvent.ONCHANGE);
		enableMailAfterDeactivationEl.select(onKeys[0], userModule.isMailAfterDeactivation());

		// subject + content mail
		TranslationBundle afterBundleSubject = initForm("mail.after.deactivation.subject.label", "mail.after.deactivation.subject", formLayout);
		TranslationBundle afterBundle = initForm("mail.after.deactivation.body.label", "mail.after.deactivation.body", formLayout);
		mailAfterDeactivationBundles = new TranslationBundles(afterBundleSubject, afterBundle);
	}

	protected void initDeletionForm(FormItemContainer formLayout) {
		String[] onValues = new String[] { translate("enabled") };
		enableDeletionEl = uifactory.addCheckboxesHorizontal("enable.deletion", "enable.deletion", formLayout, onKeys, onValues);
		enableDeletionEl.addActionListener(FormEvent.ONCHANGE);
		enableDeletionEl.select(onKeys[0], userModule.isUserAutomaticDeletion());
		
		String daysBefore = Integer.toString(userModule.getNumberOfInactiveDayBeforeDeletion());
		numberOfInactiveDayDeletionEl = uifactory.addTextElement("num.inactive.day.deletion", "num.inactive.day.deletion", 4, daysBefore, formLayout);
		initDays(numberOfInactiveDayDeletionEl);
		
		// enable mail before
		enableMailBeforeDeletionEl = uifactory.addCheckboxesHorizontal("enable.mail.before.deletion", "enable.mail.before.deletion", formLayout, onKeys, onValues);
		enableMailBeforeDeletionEl.addActionListener(FormEvent.ONCHANGE);
		enableMailBeforeDeletionEl.select(onKeys[0], userModule.isMailBeforeDeletion());
		
		// day before
		String daysBeforeMail = Integer.toString(userModule.getNumberOfDayBeforeDeletionMail());
		numberOfDayBeforeDeletionMailEl = uifactory.addTextElement("num.day.before.mail.deletion", "num.day.before.mail.deletion", 4, daysBeforeMail, formLayout);
		initDays(numberOfDayBeforeDeletionMailEl);
		
		// subject + content mail
		TranslationBundle beforeBundleSubject = initForm("mail.before.deletion.subject.label", "mail.before.deletion.subject", formLayout);
		TranslationBundle beforeBundle = initForm("mail.before.deletion.body.label", "mail.before.deletion.body", formLayout);
		mailBeforeDeletionBundles = new TranslationBundles(beforeBundleSubject, beforeBundle);
		
		// enable mail after
		enableMailAfterDeletionEl = uifactory.addCheckboxesHorizontal("enable.mail.after.deletion", "enable.mail.after.deletion", formLayout, onKeys, onValues);
		enableMailAfterDeletionEl.addActionListener(FormEvent.ONCHANGE);
		enableMailAfterDeletionEl.select(onKeys[0], userModule.isMailAfterDeletion());
		
		// subject + content mail
		TranslationBundle afterBundleSubject = initForm("mail.after.deletion.subject.label", "mail.after.deletion.subject", formLayout);
		TranslationBundle afterBundle = initForm("mail.after.deletion.body.label", "mail.after.deletion.body", formLayout);
		mailAfterDeletionBundles = new TranslationBundles(afterBundleSubject, afterBundle);
		
	}
	
	private TranslationBundle initForm(String labelI18nKey, String textI18nKey, FormItemContainer formLayout) {
		String text = translate(textI18nKey);
		StaticTextElement viewEl = uifactory.addStaticTextElement("view." + counter++, labelI18nKey, text, formLayout);
		FormLink translationLink = uifactory.addFormLink("translate." + counter++, "translate", null, formLayout, Link.LINK);
		TranslationBundle bundle = new TranslationBundle(textI18nKey, labelI18nKey, viewEl, translationLink);
		translationLink.setUserObject(bundle);
		return bundle;
	}
	
	private void initDays(TextElement textEl) {
		textEl.setDisplaySize(6);
		textEl.setMaxLength(6);
		textEl.setElementCssClass("form-inline");
		textEl.setTextAddOn("days");
	}
	
	private void updateUI() {
		// deactivation
		boolean enableDeactivation = enableDeactivationEl.isAtLeastSelected(1);
		enableMailBeforeDeactivationEl.setVisible(enableDeactivation);
		boolean enableMailBeforeDeactiviation = enableDeactivation && enableMailBeforeDeactivationEl.isAtLeastSelected(1);
		numberOfDayBeforeDeactivationMailEl.setVisible(enableMailBeforeDeactiviation);
		mailBeforeDeactivationBundles.setVisible(enableMailBeforeDeactiviation);
		enableMailAfterDeactivationEl.setVisible(enableDeactivation);
		mailAfterDeactivationBundles.setVisible(enableDeactivation && enableMailAfterDeactivationEl.isAtLeastSelected(1));
		
		// deletion
		boolean enableDeletion = enableDeletionEl.isAtLeastSelected(1);
		enableMailBeforeDeletionEl.setVisible(enableDeletion);
		boolean enableMailBeforeDeletion = enableDeletion && enableMailBeforeDeletionEl.isAtLeastSelected(1);
		numberOfDayBeforeDeletionMailEl.setVisible(enableMailBeforeDeletion);
		mailBeforeDeletionBundles.setVisible(enableMailBeforeDeletion);
		enableMailAfterDeletionEl.setVisible(enableDeletion);
		mailAfterDeletionBundles.setVisible(enableDeletion && enableMailAfterDeletionEl.isAtLeastSelected(1));
	}

	@Override
	protected void doDispose() {
		//
	}
	
	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(translatorCtrl == source) {
			doUpdate((TranslationBundle)translatorCtrl.getUserObject());
			cmc.deactivate();
			cleanUp();
		} else if(cmc == source) {
			cleanUp();
		}
		super.event(ureq, source, event);
	}
	
	private void cleanUp() {
		removeAsListenerAndDispose(translatorCtrl);
		removeAsListenerAndDispose(cmc);
		translatorCtrl = null;
		cmc = null;
	}

	@Override
	protected void formInnerEvent(UserRequest ureq, FormItem source, FormEvent event) {
		if(enableDeactivationEl == source || enableDeletionEl == source
				|| enableMailBeforeDeactivationEl == source || enableMailAfterDeactivationEl == source
				|| enableMailBeforeDeletionEl == source || enableMailAfterDeletionEl == source) {
			updateUI();
		} else if(source instanceof FormLink) {
			if(source.getUserObject() instanceof TranslationBundle) {
				doTranslate(ureq, (TranslationBundle)source.getUserObject());
			}
		}
		super.formInnerEvent(ureq, source, event);
	}

	@Override
	protected void formOK(UserRequest ureq) {
		boolean automaticDeactivation = enableDeactivationEl.isAtLeastSelected(1);
		userModule.setUserAutomaticDeactivation(automaticDeactivation);
		int daysBeforeInactivation = Integer.parseInt(numberOfInactiveDayDeactivationEl.getValue());
		userModule.setNumberOfInactiveDayBeforeDeactivation(daysBeforeInactivation);
		if(automaticDeactivation) {
			userModule.setMailBeforeDeactivation(enableMailBeforeDeactivationEl.isAtLeastSelected(1));
			if(enableMailBeforeDeactivationEl.isAtLeastSelected(1)) {
				int daysBeforeMail = Integer.parseInt(numberOfDayBeforeDeactivationMailEl.getValue());
				userModule.setNumberOfDayBeforeDeactivationMail(daysBeforeMail);
			}
			userModule.setMailAfterDeactivation(enableMailAfterDeactivationEl.isAtLeastSelected(1));
		}
		
		boolean automaticDeletion = enableDeletionEl.isAtLeastSelected(1);
		userModule.setUserAutomaticDeletion(automaticDeletion);
		int daysBeforeDeletion = Integer.parseInt(numberOfInactiveDayDeletionEl.getValue());
		userModule.setNumberOfInactiveDayBeforeDeletion(daysBeforeDeletion);
		if(automaticDeletion) {
			userModule.setMailBeforeDeletion(enableMailBeforeDeletionEl.isAtLeastSelected(1));
			if(enableMailBeforeDeletionEl.isAtLeastSelected(1)) {
				int daysBeforeMail = Integer.parseInt(numberOfDayBeforeDeletionMailEl.getValue());
				userModule.setNumberOfDayBeforeDeletionMail(daysBeforeMail);
			}
			userModule.setMailAfterDeletion(enableMailAfterDeletionEl.isAtLeastSelected(1));
		}
	}
	
	private void doTranslate(UserRequest ureq, TranslationBundle bundle) {
		if(guardModalController(translatorCtrl)) return;
		
		translatorCtrl = new SingleKeyTranslatorController(ureq, getWindowControl(), bundle.getI18nKey(),
				UserAdminLifecycleConfigurationController.class);
		translatorCtrl.setUserObject(bundle);
		listenTo(translatorCtrl);

		String title = translate("translate.title", new String[] { translate(bundle.getLabelI18nKey()) });
		cmc = new CloseableModalController(getWindowControl(), "close", translatorCtrl.getInitialComponent(), true, title);
		listenTo(cmc);
		cmc.activate();
	}
	
	private void doUpdate(TranslationBundle bundle) {
		bundle.getViewEl().setValue(translate(bundle.getI18nKey()));
	}
	
	private static class TranslationBundle {
		
		private final String i18nKey;
		private final String labelI18nKey;
		private final StaticTextElement viewEl;
		private final FormLink translationLink;
		
		public TranslationBundle(String i18nKey, String labelI18nKey, StaticTextElement viewEl, FormLink translationLink) {
			this.i18nKey = i18nKey;
			this.viewEl = viewEl;
			this.labelI18nKey = labelI18nKey;
			this.translationLink = translationLink;
		}

		public StaticTextElement getViewEl() {
			return viewEl;
		}

		public String getI18nKey() {
			return i18nKey;
		}
		
		public String getLabelI18nKey() {
			return labelI18nKey;
		}
		
		public void setVisible(boolean visible) {
			viewEl.setVisible(visible);
			translationLink.setVisible(visible);
		}
	}
	
	private static class TranslationBundles {
		
		private final TranslationBundle subjectBundle;
		private final TranslationBundle bodyBundle;
		
		public TranslationBundles(TranslationBundle subjectBundle, TranslationBundle bodyBundle) {
			this.subjectBundle = subjectBundle;
			this.bodyBundle = bodyBundle;
		}
		
		public void setVisible(boolean visible) {
			subjectBundle.setVisible(visible);
			bodyBundle.setVisible(visible);
		}
	}
}
