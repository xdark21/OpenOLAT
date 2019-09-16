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
package org.olat.course.nodes.portfolio;

import org.olat.core.CoreSpringFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.stack.BreadcrumbPanel;
import org.olat.core.gui.components.stack.TooledStackedPanel;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.id.Identity;
import org.olat.course.assessment.AssessmentManager;
import org.olat.course.assessment.handler.AssessmentConfig;
import org.olat.course.assessment.handler.AssessmentHandler;
import org.olat.course.assessment.ui.tool.AssessmentCourseNodeController;
import org.olat.course.nodes.CourseNode;
import org.olat.course.nodes.PortfolioCourseNode;
import org.olat.course.run.scoring.AssessmentEvaluation;
import org.olat.course.run.scoring.ScoreCalculator;
import org.olat.course.run.userview.UserCourseEnvironment;
import org.olat.group.BusinessGroup;
import org.olat.modules.assessment.AssessmentEntry;
import org.olat.modules.assessment.ui.AssessmentToolContainer;
import org.olat.modules.assessment.ui.AssessmentToolSecurityCallback;
import org.olat.modules.portfolio.handler.BinderTemplateResource;
import org.olat.modules.portfolio.ui.PortfolioAssessmentDetailsController;
import org.olat.portfolio.manager.EPStructureManager;
import org.olat.repository.RepositoryEntry;
import org.springframework.stereotype.Service;

/**
 * 
 * Initial date: 20 Aug 2019<br>
 * @author uhensler, urs.hensler@frentix.com, http://www.frentix.com
 *
 */
@Service
public class PortfolioAssessmentHandler implements AssessmentHandler {

	@Override
	public String acceptCourseNodeType() {
		return PortfolioCourseNode.TYPE;
	}

	@Override
	public AssessmentConfig getAssessmentConfig(CourseNode courseNode) {
		return new PortfolioAssessmentConfig(courseNode.getModuleConfiguration());
	}

	@Override
	public AssessmentEntry getAssessmentEntry(CourseNode courseNode, UserCourseEnvironment userCourseEnvironment) {
		String referenceSoftkey = getReferenceSoftkey(courseNode);
		if(referenceSoftkey != null) {
			AssessmentManager am = userCourseEnvironment.getCourseEnvironment().getAssessmentManager();
			Identity assessedIdentity = userCourseEnvironment.getIdentityEnvironment().getIdentity();
			return am.getAssessmentEntry(courseNode, assessedIdentity);
		}
		return null;
	}

	private String getReferenceSoftkey(CourseNode courseNode) {
		String referenceSoftkey = (String)courseNode.getModuleConfiguration().get(PortfolioCourseNodeConfiguration.REPO_SOFT_KEY);
		if(referenceSoftkey == null) {
			Long mapKey = (Long)courseNode.getModuleConfiguration().get(PortfolioCourseNodeConfiguration.MAP_KEY);
			if(mapKey != null) {
				RepositoryEntry re = CoreSpringFactory.getImpl(EPStructureManager.class)
						.loadPortfolioRepositoryEntryByMapKey(mapKey);
				if(re != null) {
					referenceSoftkey = re.getSoftkey();
				}
			}
		}
		return referenceSoftkey;
	}

	@Override
	public AssessmentEvaluation getCalculatedScoreEvaluation(CourseNode courseNode,
			UserCourseEnvironment userCourseEnvironment) {
		return null;
	}

	@Override
	public ScoreCalculator getScoreCalculator(CourseNode courseNode) {
		return null;
	}

	@Override
	public Controller getDetailsEditController(UserRequest ureq, WindowControl wControl, BreadcrumbPanel stackPanel,
			CourseNode courseNode, UserCourseEnvironment coachCourseEnv, UserCourseEnvironment assessedUserCourseEnv) {
		RepositoryEntry mapEntry = courseNode.getReferencedRepositoryEntry();
		if(mapEntry != null && BinderTemplateResource.TYPE_NAME.equals(mapEntry.getOlatResource().getResourceableTypeName())) {
			Identity assessedIdentity = assessedUserCourseEnv.getIdentityEnvironment().getIdentity();
			RepositoryEntry courseEntry = assessedUserCourseEnv.getCourseEnvironment().getCourseGroupManager().getCourseEntry();
			return new PortfolioAssessmentDetailsController(ureq, wControl, courseEntry, courseNode, mapEntry,
					assessedIdentity);
		}
		return new PortfolioResultDetailsController(ureq, wControl, stackPanel, courseNode, assessedUserCourseEnv);
	}

	@Override
	public boolean hasCustomIdentityList() {
		return false;
	}

	@Override
	public AssessmentCourseNodeController getIdentityListController(UserRequest ureq, WindowControl wControl,
			TooledStackedPanel stackPanel, CourseNode courseNode, RepositoryEntry courseEntry, BusinessGroup group,
			UserCourseEnvironment coachCourseEnv, AssessmentToolContainer toolContainer,
			AssessmentToolSecurityCallback assessmentCallback) {
		return null;
	}

}