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
package org.olat.user.ui.admin.bulk.move;

import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.form.flexible.impl.Form;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.generic.wizard.BasicStep;
import org.olat.core.gui.control.generic.wizard.PrevNextFinishConfig;
import org.olat.core.gui.control.generic.wizard.StepFormController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;

/**
 * 
 * Initial date: 27 mai 2019<br>
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 *
 */
public class UserBulkMove_1_ChooseRoleStep extends BasicStep {
	
	private final UserBulkMove userBulkMove;
	
	public UserBulkMove_1_ChooseRoleStep(UserRequest ureq, UserBulkMove userBulkMove) {
		super(ureq);
		this.userBulkMove = userBulkMove;
		setI18nTitleAndDescr("wizard.choose.roles", "wizard.choose.roles");
		setNextStep(new UserBulkMove_2_OverviewStep(ureq, userBulkMove));
	}

	@Override
	public PrevNextFinishConfig getInitialPrevNextFinishConfig() {
		return new PrevNextFinishConfig(false, true, false);
	}

	@Override
	public StepFormController getStepController(UserRequest ureq, WindowControl wControl,
			StepsRunContext stepsRunContext, Form form) {
		return new ChooseRolesController(ureq, wControl, form, userBulkMove, stepsRunContext);
	}
}
