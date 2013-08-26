/*
 * @(#)InterfaceCreationAction.java
 *
 * Copyright 2009 Instituto Superior Tecnico
 * Founding Authors: Luis Cruz, Nuno Ochoa, Paulo Abrantes
 * 
 *      https://fenix-ashes.ist.utl.pt/
 * 
 *   This file is part of the Expenditure Tracking Module.
 *
 *   The Expenditure Tracking Module is free software: you can
 *   redistribute it and/or modify it under the terms of the GNU Lesser General
 *   Public License as published by the Free Software Foundation, either version 
 *   3 of the License, or (at your option) any later version.
 *
 *   The Expenditure Tracking Module is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *   GNU Lesser General Public License for more details.
 *
 *   You should have received a copy of the GNU Lesser General Public License
 *   along with the Expenditure Tracking Module. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package pt.ist.expenditureTrackingSystem.presentationTier.actions;

import pt.ist.bennu.core.presentationTier.actions.ContextBaseAction;
import pt.ist.fenixWebFramework.struts.annotations.Mapping;

/**
 * 
 * @author Paulo Abrantes
 * @author Luis Cruz
 * 
 */
@Mapping(path = "/expendituresInterfaceCreationAction")
public class InterfaceCreationAction extends ContextBaseAction {

//    @CreateNodeAction(bundle = "EXPENDITURE_RESOURCES", key = "add.node.expenditure-tracking.interface",
//            groupKey = "label.module.expenditure-tracking")
//    public final ActionForward createExpenditureNodes(final ActionMapping mapping, final ActionForm form,
//            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
//        final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
//        final Node node = getDomainObject(request, "parentOfNodesToManageId");
//
//        final ExpenditureTrackingSystem expenditureTrackingSystem = ExpenditureTrackingSystem.getInstance();
//        if (expenditureTrackingSystem != null) {
//            final PersistentGroup acquisitionCentralGroup = expenditureTrackingSystem.getAcquisitionCentralGroup();
//            final PersistentGroup acquisitionCentralManagerGroup = expenditureTrackingSystem.getAcquisitionCentralManagerGroup();
//            final PersistentGroup accountingManagerGroup = expenditureTrackingSystem.getAccountingManagerGroup();
//            final Node aquisitionProcessNode =
//                    ActionNode.createActionNode(virtualHost, node, "/search", "search", "resources.ExpenditureResources",
//                            "link.topBar.acquisitionProcesses", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/wizard", "newAcquisitionWizard",
//                    "resources.ExpenditureResources", "link.sideBar.process.create", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/search", "search",
//                    "resources.ExpenditureResources", "link.sideBar.acquisitionProcess.search", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/wizard", "afterTheFactOperationsWizard",
//                    "resources.ExpenditureResources", "link.register", acquisitionCentralGroup);
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/acquisitionProcess", "checkFundAllocations",
//                    "resources.ExpenditureResources", "link.fundAllocations", accountingManagerGroup);
//
////	final Node organizationNode = ActionNode.createActionNode(virtualHost, node, "/expenditureTrackingOrganization",
////		"viewLoggedPerson", "resources.ExpenditureResources", "link.topBar.organization", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/expenditureTrackingOrganization",
//                    "viewOrganization", "resources.ExpenditureResources", "link.viewOrganization", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/expenditureTrackingOrganization", "searchUsers",
//                    "resources.ExpenditureResources", "search.link.users", UserGroup.getInstance());
//            ActionNode.createActionNode(virtualHost, aquisitionProcessNode, "/expenditureTrackingOrganization",
//                    "manageSuppliers", "resources.ExpenditureOrganizationResources", "supplier.link.manage",
//                    UserGroup.getInstance());
//
//            final PersistentGroup statisticsGroup = expenditureTrackingSystem.getStatisticsViewerGroup();
//            final UnionGroup statisticsOrAcquisitionCentralManagerGroup =
//                    UnionGroup.createUnionGroup(statisticsGroup, acquisitionCentralManagerGroup);
//
//            final Node statisticsNode =
//                    ActionNode.createActionNode(virtualHost, node, "/statistics", "showStatisticsReports",
//                            "resources.ExpenditureResources", "link.topBar.statistics",
//                            statisticsOrAcquisitionCentralManagerGroup);
//            ActionNode.createActionNode(virtualHost, statisticsNode, "/statistics", "showSimplifiedProcessStatistics",
//                    "resources.StatisticsResources", "label.statistics.process.simplified", statisticsGroup);
//            ActionNode.createActionNode(virtualHost, statisticsNode, "/statistics", "showRefundProcessStatistics",
//                    "resources.StatisticsResources", "label.statistics.process.refund", statisticsGroup);
//            ActionNode.createActionNode(virtualHost, statisticsNode, "/statistics", "showStatisticsReports",
//                    "resources.StatisticsResources", "label.statistics.reports", statisticsOrAcquisitionCentralManagerGroup);
//
////	final Node connectUnitsNode = ActionNode.createActionNode(virtualHost, node, "/connectUnits", "showUnits",
////		"resources.ExpenditureOrganizationResources", "link.topBar.connectUnits", Role.getRole(RoleType.MANAGER));
////	ActionNode.createActionNode(virtualHost, connectUnitsNode, "/connectUnits", "listUnconnectedUnits",
////		"resources.ExpenditureOrganizationResources", "label.listUnconnectedUnits", statisticsGroup);
//
//        }
//
//        return forwardToMuneConfiguration(request, virtualHost, node);
//    }
//
//    @CreateNodeAction(bundle = "EXPENDITURE_RESOURCES", key = "add.node.expenditure-tracking.interface.config",
//            groupKey = "label.module.expenditure-tracking")
//    public final ActionForward createExpenditureConfigurationInterface(final ActionMapping mapping, final ActionForm form,
//            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
//        final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
//        final Node node = getDomainObject(request, "parentOfNodesToManageId");
//
//        ActionNode.createActionNode(virtualHost, node, "/expenditureConfiguration", "viewConfiguration",
//                "resources.ExpenditureResources", "link.topBar.configuration", Role.getRole(RoleType.MANAGER));
//
//        return forwardToMuneConfiguration(request, virtualHost, node);
//    }
//
//    @CreateNodeAction(bundle = "EXPENDITURE_RESOURCES", key = "add.node.expenditure-tracking.interface.announcements",
//            groupKey = "label.module.expenditure-tracking")
//    public final ActionForward createAnnouncmentNodes(final ActionMapping mapping, final ActionForm form,
//            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
//        final VirtualHost virtualHost = getDomainObject(request, "virtualHostToManageId");
//        final Node node = getDomainObject(request, "parentOfNodesToManageId");
//
//        final ExpenditureTrackingSystem expenditureTrackingSystem = ExpenditureTrackingSystem.getInstance();
//        final PersistentGroup acquisitionCentralGroup = expenditureTrackingSystem.getAcquisitionCentralGroup();
//        final Node announcementsnNode =
//                createNodeForPage(virtualHost, node, "resources.ExpenditureResources", "link.topBar.announcements",
//                        acquisitionCentralGroup);
//        ActionNode.createActionNode(virtualHost, announcementsnNode, "/announcementProcess", "prepareCreateAnnouncement",
//                "resources.ExpenditureResources", "link.sideBar.announcementProcess.createAnnouncement", acquisitionCentralGroup);
//        ActionNode.createActionNode(virtualHost, announcementsnNode, "/announcementProcess", "searchAnnouncementProcess",
//                "resources.ExpenditureResources", "link.sideBar.announcementProcess.searchProcesses", acquisitionCentralGroup);
//        ActionNode.createActionNode(virtualHost, announcementsnNode, "/announcementProcess", "showMyProcesses",
//                "resources.ExpenditureResources", "link.sideBar.announcementProcess.myProcesses", acquisitionCentralGroup);
//        ActionNode.createActionNode(virtualHost, announcementsnNode, "/announcementProcess", "showPendingProcesses",
//                "resources.ExpenditureResources", "link.sideBar.announcementProcess.pendingProcesses", acquisitionCentralGroup);
//
//        return forwardToMuneConfiguration(request, virtualHost, node);
//    }
//
//    protected Node createNodeForPage(final VirtualHost virtualHost, final Node node, final String bundle, final String key,
//            PersistentGroup userGroup) {
//        /**
//         * final PageBean pageBean = new PageBean(virtualHost, node, userGroup);
//         * final MultiLanguageString statisticsLabel = LegacyBundleUtil.getMultilanguageString(bundle, key);
//         * pageBean.setLink(statisticsLabel);
//         * pageBean.setTitle(statisticsLabel);
//         * return (Node) Page.createNewPage(pageBean);
//         */
//        throw new Error("Not implemented");
//        // TODO : reimplement this.
//    }

}
