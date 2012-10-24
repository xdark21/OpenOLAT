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
package org.olat.course.member;

import java.util.List;

import org.olat.core.CoreSpringFactory;
import org.olat.core.commons.persistence.DBFactory;
import org.olat.core.gui.UserRequest;
import org.olat.core.gui.components.Component;
import org.olat.core.gui.components.link.Link;
import org.olat.core.gui.components.link.LinkFactory;
import org.olat.core.gui.components.segmentedview.SegmentViewComponent;
import org.olat.core.gui.components.segmentedview.SegmentViewEvent;
import org.olat.core.gui.components.segmentedview.SegmentViewFactory;
import org.olat.core.gui.components.velocity.VelocityContainer;
import org.olat.core.gui.control.Controller;
import org.olat.core.gui.control.Event;
import org.olat.core.gui.control.WindowControl;
import org.olat.core.gui.control.controller.BasicController;
import org.olat.core.gui.control.generic.dtabs.Activateable2;
import org.olat.core.gui.control.generic.wizard.Step;
import org.olat.core.gui.control.generic.wizard.StepRunnerCallback;
import org.olat.core.gui.control.generic.wizard.StepsMainRunController;
import org.olat.core.gui.control.generic.wizard.StepsRunContext;
import org.olat.core.id.Identity;
import org.olat.core.id.OLATResourceable;
import org.olat.core.id.context.BusinessControlFactory;
import org.olat.core.id.context.ContextEntry;
import org.olat.core.id.context.StateEntry;
import org.olat.core.logging.activity.ThreadLocalUserActivityLogger;
import org.olat.core.util.mail.MailContext;
import org.olat.core.util.mail.MailContextImpl;
import org.olat.core.util.mail.MailHelper;
import org.olat.core.util.mail.MailTemplate;
import org.olat.core.util.mail.MailerResult;
import org.olat.core.util.mail.MailerWithTemplate;
import org.olat.core.util.resource.OresHelper;
import org.olat.course.member.wizard.ImportMember_1a_LoginListStep;
import org.olat.course.member.wizard.ImportMember_1b_ChooseMemberStep;
import org.olat.group.BusinessGroupService;
import org.olat.group.model.BusinessGroupMembershipChange;
import org.olat.repository.RepositoryEntry;
import org.olat.repository.RepositoryManager;
import org.olat.repository.model.RepositoryEntryPermissionChangeEvent;
import org.olat.util.logging.activity.LoggingResourceable;

/**
 * 
 * The members overview.
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class MembersOverviewController extends BasicController implements Activateable2 {
	
	private static final String SEG_ALL_MEMBERS = "All";
	private static final String SEG_OWNERS_MEMBERS = "Owners";
	private static final String SEG_TUTORS_MEMBERS = "Coaches";
	private static final String SEG_PARTICIPANTS_MEMBERS = "Participants";
	private static final String SEG_WAITING_MEMBERS = "Waiting";
	private static final String SEG_SEARCH_MEMBERS = "Search";
	
	private final Link allMembersLink, ownersLink, tutorsLink, participantsLink, waitingListLink, searchLink;
	private final SegmentViewComponent segmentView;
	private final VelocityContainer mainVC;
	
	private AbstractMemberListController allMemberListCtrl;
	private AbstractMemberListController ownersCtrl;
	private AbstractMemberListController tutorsCtrl;
	private AbstractMemberListController participantsCtrl;
	private AbstractMemberListController waitingCtrl;
	private AbstractMemberListController selectedCtrl;
	private AbstractMemberListController searchCtrl;
	private final Link importMemberLink, addMemberLink;
	
	private StepsMainRunController importMembersWizard;
	
	private final RepositoryEntry repoEntry;
	private final RepositoryManager repositoryManager;
	private final BusinessGroupService businessGroupService;
	
	public MembersOverviewController(UserRequest ureq, WindowControl wControl, RepositoryEntry repoEntry) {
		super(ureq, wControl);
		this.repoEntry = repoEntry;
		repositoryManager = CoreSpringFactory.getImpl(RepositoryManager.class);
		businessGroupService = CoreSpringFactory.getImpl(BusinessGroupService.class);

		mainVC = createVelocityContainer("members_overview");
		segmentView = SegmentViewFactory.createSegmentView("segments", mainVC, this);
		
		allMembersLink = LinkFactory.createLink("members.all", mainVC, this);
		segmentView.addSegment(allMembersLink, true);
		ownersLink = LinkFactory.createLink("owners", mainVC, this);
		segmentView.addSegment(ownersLink, false);
		tutorsLink = LinkFactory.createLink("tutors", mainVC, this);
		segmentView.addSegment(tutorsLink, false);
		participantsLink = LinkFactory.createLink("participants", mainVC, this);
		segmentView.addSegment(participantsLink, false);
		waitingListLink = LinkFactory.createLink("waitinglist", mainVC, this);
		segmentView.addSegment(waitingListLink, false);
		searchLink = LinkFactory.createLink("search", mainVC, this);
		segmentView.addSegment(searchLink, false);
		
		updateAllMembers(ureq);
		
		addMemberLink = LinkFactory.createButton("add.member", mainVC, this);
		mainVC.put("addMembers", addMemberLink);
		importMemberLink = LinkFactory.createButton("import.member", mainVC, this);
		mainVC.put("importMembers", importMemberLink);
		
		putInitialPanel(mainVC);
	}
	
	@Override
	protected void doDispose() {
		//
	}

	@Override
	public void activate(UserRequest ureq, List<ContextEntry> entries, StateEntry state) {
		if(entries == null || entries.isEmpty()) return;
		
		ContextEntry currentEntry = entries.get(0);
		String segment = currentEntry.getOLATResourceable().getResourceableTypeName();
		List<ContextEntry> subEntries = entries.subList(1, entries.size());
		if(SEG_ALL_MEMBERS.equals(segment)) {
			updateAllMembers(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(allMembersLink);
		} else if(SEG_OWNERS_MEMBERS.equals(segment)) {
			updateOwners(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(ownersLink);
		} else if(SEG_TUTORS_MEMBERS.equals(segment)) {
			updateTutors(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(tutorsLink);
		} else if(SEG_PARTICIPANTS_MEMBERS.equals(segment)) {
			updateParticipants(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(participantsLink);
		} else if(SEG_WAITING_MEMBERS.equals(segment)) {
			updateWaitingList(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(waitingListLink);
		} else if(SEG_SEARCH_MEMBERS.equals(segment)) {
			updateSearch(ureq).activate(ureq, subEntries, currentEntry.getTransientState());
			segmentView.select(searchLink);
		}
	}

	@Override
	protected void event(UserRequest ureq, Component source, Event event) {
		if(event instanceof SegmentViewEvent) {
			SegmentViewEvent sve = (SegmentViewEvent)event;
			String segmentCName = sve.getComponentName();
			Component clickedLink = mainVC.getComponent(segmentCName);
			if (clickedLink == allMembersLink) {
				selectedCtrl = updateAllMembers(ureq);
			} else if (clickedLink == ownersLink){
				selectedCtrl = updateOwners(ureq);
			} else if (clickedLink == tutorsLink){
				selectedCtrl = updateTutors(ureq);
			} else if (clickedLink == participantsLink) {
				selectedCtrl = updateParticipants(ureq);
			} else if (clickedLink == waitingListLink) {
				selectedCtrl = updateWaitingList(ureq);
			} else if (clickedLink == searchLink) {
				updateSearch(ureq);
				selectedCtrl = null;
			}
		} else if (source == addMemberLink) {
			doChooseMembers(ureq);
		} else if (source == importMemberLink) {
			doImportMembers(ureq);
		}
	}

	@Override
	protected void event(UserRequest ureq, Controller source, Event event) {
		if(source == importMembersWizard) {
			if(event == Event.CANCELLED_EVENT || event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
				getWindowControl().pop();
				removeAsListenerAndDispose(importMembersWizard);
				importMembersWizard = null;
				if(event == Event.DONE_EVENT || event == Event.CHANGED_EVENT) {
					if(selectedCtrl != null) {
						selectedCtrl.reloadModel();
					}
				}
			}
		}
		super.event(ureq, source, event);
	}

	private void doChooseMembers(UserRequest ureq) {
		removeAsListenerAndDispose(importMembersWizard);

		Step start = new ImportMember_1b_ChooseMemberStep(ureq, repoEntry);
		StepRunnerCallback finish = new StepRunnerCallback() {
			@Override
			public Step execute(UserRequest ureq, WindowControl wControl, StepsRunContext runContext) {
				addMembers(ureq, runContext);
				return StepsMainRunController.DONE_MODIFIED;
			}
		};
		
		importMembersWizard = new StepsMainRunController(ureq, getWindowControl(), start, finish, null,
				translate("add.member"), "o_sel_course_member_import_1_wizard");
		listenTo(importMembersWizard);
		getWindowControl().pushAsModalDialog(importMembersWizard.getInitialComponent());
	}
	
	private void doImportMembers(UserRequest ureq) {
		removeAsListenerAndDispose(importMembersWizard);

		Step start = new ImportMember_1a_LoginListStep(ureq, repoEntry);
		StepRunnerCallback finish = new StepRunnerCallback() {
			@Override
			public Step execute(UserRequest ureq, WindowControl wControl, StepsRunContext runContext) {
				addMembers(ureq, runContext);
				return StepsMainRunController.DONE_MODIFIED;
			}
		};
		
		importMembersWizard = new StepsMainRunController(ureq, getWindowControl(), start, finish, null,
				translate("import.member"), "o_sel_course_member_import_logins_wizard");
		listenTo(importMembersWizard);
		getWindowControl().pushAsModalDialog(importMembersWizard.getInitialComponent());
	}
	
	protected void addMembers(UserRequest ureq, StepsRunContext runContext) {
		@SuppressWarnings("unchecked")
		List<Identity> members = (List<Identity>)runContext.get("members");
		
		MemberPermissionChangeEvent changes = (MemberPermissionChangeEvent)runContext.get("permissions");
		//commit changes to the repository entry
		List<RepositoryEntryPermissionChangeEvent> repoChanges = changes.generateRepositoryChanges(members);
		repositoryManager.updateRepositoryEntryMembership(getIdentity(), repoEntry, repoChanges);

		//commit all changes to the group memberships
		List<BusinessGroupMembershipChange> allModifications = changes.generateBusinessGroupMembershipChange(members);
		businessGroupService.updateMemberships(getIdentity(), allModifications);
		
		MailTemplate template = (MailTemplate)runContext.get("mailTemplate");
		if (template != null && !members.isEmpty()) {
			MailerWithTemplate mailer = MailerWithTemplate.getInstance();
			MailContext context = new MailContextImpl(null, null, getWindowControl().getBusinessControl().getAsString());
			MailerResult mailerResult = mailer.sendMailAsSeparateMails(context, members, null, null, template, getIdentity());
			MailHelper.printErrorsAndWarnings(mailerResult, getWindowControl(), getLocale());
		}
		
		switchToAllMembers(ureq);
	}
	
	private void switchToAllMembers(UserRequest ureq) {
		DBFactory.getInstance().commit();//make sure all is on the DB before reloading
		if(selectedCtrl != null && selectedCtrl == allMemberListCtrl) {
			allMemberListCtrl.reloadModel();
		} else {
			selectedCtrl = updateAllMembers(ureq);
			segmentView.select(allMembersLink);
		}
	}
	
	private AbstractMemberListController updateAllMembers(UserRequest ureq) {
		if(allMemberListCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_ALL_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			SearchMembersParams searchParams = new SearchMembersParams(true, true, true, true, true, false);
			allMemberListCtrl = new MemberListWithOriginFilterController(ureq, bwControl, repoEntry, searchParams, null);
			listenTo(allMemberListCtrl);
		}
		
		allMemberListCtrl.reloadModel();
		mainVC.put("membersCmp", allMemberListCtrl.getInitialComponent());
		addToHistory(ureq, allMemberListCtrl);
		return allMemberListCtrl;
	}
	
	private AbstractMemberListController updateOwners(UserRequest ureq) {
		if(ownersCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_OWNERS_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			SearchMembersParams searchParams = new SearchMembersParams(true, false, false, false, false, false);
			String infos = translate("owners.infos");
			ownersCtrl = new MemberListController(ureq, bwControl, repoEntry, searchParams, infos);
			listenTo(ownersCtrl);
		}
		
		ownersCtrl.reloadModel();
		mainVC.put("membersCmp", ownersCtrl.getInitialComponent());
		addToHistory(ureq, ownersCtrl);
		return ownersCtrl;
	}

	private AbstractMemberListController updateTutors(UserRequest ureq) {
		if(tutorsCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_TUTORS_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			SearchMembersParams searchParams = new SearchMembersParams(false, true, false, true, false, false);
			String infos = translate("tutors.infos");
			tutorsCtrl = new MemberListWithOriginFilterController(ureq, bwControl, repoEntry, searchParams, infos);
			listenTo(tutorsCtrl);
		}
		
		tutorsCtrl.reloadModel();
		mainVC.put("membersCmp", tutorsCtrl.getInitialComponent());
		addToHistory(ureq, tutorsCtrl);
		return tutorsCtrl;
	}
	
	private AbstractMemberListController updateParticipants(UserRequest ureq) {
		if(participantsCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_PARTICIPANTS_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			SearchMembersParams searchParams = new SearchMembersParams(false, false, true, false, true, false);
			String infos = translate("participants.infos");
			participantsCtrl = new MemberListWithOriginFilterController(ureq, bwControl, repoEntry, searchParams, infos);
			listenTo(participantsCtrl);
		}
		
		participantsCtrl.reloadModel();
		mainVC.put("membersCmp", participantsCtrl.getInitialComponent());
		addToHistory(ureq, participantsCtrl);
		return participantsCtrl;
	}
	
	private AbstractMemberListController updateWaitingList(UserRequest ureq) {
		if(waitingCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_WAITING_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			SearchMembersParams searchParams = new SearchMembersParams(false, false, false, false, false, true);
			String infos = translate("waiting.infos");
			waitingCtrl = new MemberListController(ureq, bwControl, repoEntry, searchParams, infos);
			listenTo(waitingCtrl);
		}
		
		waitingCtrl.reloadModel();
		mainVC.put("membersCmp", waitingCtrl.getInitialComponent());
		addToHistory(ureq, waitingCtrl);
		return waitingCtrl;
	}
	
	private AbstractMemberListController updateSearch(UserRequest ureq) {
		if(searchCtrl == null) {
			OLATResourceable ores = OresHelper.createOLATResourceableInstance(SEG_SEARCH_MEMBERS, 0l);
			ThreadLocalUserActivityLogger.addLoggingResourceInfo(LoggingResourceable.wrapBusinessPath(ores));
			WindowControl bwControl = BusinessControlFactory.getInstance().createBusinessWindowControl(ores, null, getWindowControl());
			searchCtrl = new MemberSearchController(ureq, bwControl, repoEntry);
			listenTo(searchCtrl);
		}
	
		mainVC.put("membersCmp", searchCtrl.getInitialComponent());
		addToHistory(ureq, searchCtrl);
		return searchCtrl;
	}
}
