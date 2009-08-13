project-name: zk_sample_gui needs project zk_sample_db (backend)
author: Stephan Gerth
project-typ: Eclipse 3.4
-------------------------------------------------------------------------------


Packages / Folders / Files:
===========================

de.forsthaus.sampledata
  --> createSampleData.sql 
      Sql script for creating the tables and demo data for the used H2 database.

de.forsthaus.sampledata.statistic
  --> Statistic.java
      Modification of the zkoss statistic class for getting some information about
      the time of application is running, active desktops i.e.
  
de.forsthaus.zksample
  --> ApplicationWorkspace.java (not used )
  --> IndexCtrl.java 
      The Controller for the main zul-file 'WebContent/pages/index.zul' that 
      holds the menu and content area.
  --> InitApplication.java
      The Controller for the zul-file 'WebContent/index.zul' that is the first
      page that is shown. It handles the showing of the statistic data / table records.
  --> My_H2_DBStarter.java
      Called from the web.xml. This class do start the H2 database server and
      creates the tables and the demodata from the file  
      de.forsthaus.sampledata.createSampleData.sql     
  --> ObjectMaschine.java
      This class creates the demodata randomly from txt-files resides under 
      /WebContent/res/*.txt     
  --> UserWorkspace.java
      This class creates ONE UserWorkspace for every logged in User.
      Over this class we get access to the grantedRights for the user.       
  --> ZKLoginDialog.java
      The controller class for the Login zul-file 'WebContent/zkloginDialog.zul' that 
      handles the login procedure.
      
de.forsthaus.zksample.common.menu
  --> MainMenuCtrl.java
      Controller for the mainmenu.zul in 'WebContent/WEB-INF/pages/mainmenu.zul'.
      Here are the menu are generated from several helper classes.
       
de.forsthaus.zksample.common.menu.sub
  --> AdministrationMenuTree.java 
      builds the administration menu tree.
  --> HomeMenuTree.java
      builds the home menu tree
  --> MainDataMenuTree.java
      builds the menu tree for the main data
  --> OfficeDataMenuTree.java
      build the menu tree for the office/business data
      
de.forsthaus.zksample.common.menu.util
  --> DefaultTreecell.java
  --> DefaultTreechildren.java
  --> DefaultTreeitem.java
  --> TreeValue.java
  --> ZKossMenuUtil.java
      Helper classes for building the menu tree from java.
      
de.forsthaus.zksample.policy
  --> LoginLoggingPolicyService.java
      This class is called from spring aop as an aspect and is for logging the Login
      of a user. It is configurated in the '/WebContent/WEB-INF/SpringSecurityContext.xml'  
      Logs success and fails, sessionID, timestamp and remoteIP.      
  --> PolicyManager.java
      Implementation of the spring-security UserdetailService.          
      
de.forsthaus.zksample.policy.model
  --> UserImpl.java
      The User implementation of spring-security framework user class.

####################################      
#   de.forsthaus.zksample.webui    #
####################################
All webui-classes are seperated in 
...webui.modulName and ...webui.modulName.Model
Where .modulname holds the Controllers for the Lists and Dialogs 
and .model holds the renderers and helper classes.

de.forsthaus.zksample.webui.article
  --> ArticleDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/article/articleDialog.zul'
  --> ArticleListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/article/articleList.zul'

de.forsthaus.zksample.webui.article.model
  --> ArticleListModelItemRenderer.java
      ListItem renderer for the article list.

de.forsthaus.zksample.webui.branch
  --> BranchDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/branch/branchDialog.zul'
  --> BranchListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/branch/branchList.zul'

de.forsthaus.zksample.webui.branch.model
  --> BranchListModelItemRenderer.java
      ListItem renderer for the article list.

de.forsthaus.zksample.webui.chat
  --> ChatRoom.java (ZKoss chat sample)
  --> Chater.java (ZKoss chat sample)
  --> ChatWindow.java (ZKoss chat sample)

de.forsthaus.zksample.webui.customer
  --> CustomerDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/customer/customerDialog.zul'
  --> CustomerListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/customer/customerList.zul'
  --> CustomerSearchCtrl.java
      Controller for '/WebContent/WEB-INF/pages/customer/customerSearchDialog.zul'
      
      
de.forsthaus.zksample.webui.customer.model
  --> CustomerBrancheListModelItemRenderer.java
      Listbox Listitem renderer
  --> CustomerListModelItemRenderer.java
      Listbox Listitem renderer

de.forsthaus.zksample.webui.guestbook
  --> GuestBookDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/guestbook/guestBookDialog.zul'
  --> GuestBookListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/guestbook/guestBookList.zul'
 
de.forsthaus.zksample.webui.guestbook.model
  --> GuestBookListtemRenderer.java 
      Listbox Listitem renderer

de.forsthaus.zksample.webui.logging.loginlog
  --> SecLoginlogListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_loginlog/secLoginLog.zul'
      
de.forsthaus.zksample.webui.logging.loginlog.model
  --> SecLoginlogListModelItemRenderer.java
      Listbox Listitem renderer
  --> WorkingThreadLoginList.java
      Thread for demonstrate the serverPush. 

de.forsthaus.zksample.webui.login
  --> ZkLoginDialogCtrl.java
      Controller for '/WebContent/ZKLoginDialog.zul'

de.forsthaus.zksample.webui.order
  --> OrderDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/order/orderDialog.zul'
  --> OrderListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/order/orderList.zul'
  --> OrderPositionDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/order/orderPositionDialog.zul'
 
de.forsthaus.zksample.webui.order.model
  --> OrderListModelItemRenderer.java
      Listbox Listitem renderer
  --> OrderSearchCustomerListModelItemRenderer.java
      Listbox Listitem renderer
  --> SearchArticleListModelItemRenderer.java
      Listbox Listitem renderer

de.forsthaus.zksample.webui.orderposition.model
  --> OrderpositionListModelItemRenderer
      Listbox Listitem renderer
      
de.forsthaus.zksample.webui.reports
de.forsthaus.zksample.webui.reports.util
  --> JRreportWindow.java
      Capsulates the JasperReport Component in a modal window

#############################################      
#   de.forsthaus.zksample.webui.security    #
#############################################
Management pages for the security

de.forsthaus.zksample.webui.security.group
  --> SecGroupDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_group/secGroupDialog.zul'
  --> SecGroupListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_group/secGroupList.zul'

de.forsthaus.zksample.webui.security.group.model
  --> AppRolesListModelItemRenderer.java
      Listbox Listitem renderer
  --> SecGroupComparator.java
      manually Comparator class for the Sorting
  --> SecGroupListModelItemRenderer.java
      Listbox Listitem renderer

de.forsthaus.zksample.webui.security.groupright
  --> AddGrouprightDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_groupright/addGroupRightDialog.zul'
  --> SecGrouprightCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_groupright/secGroupRight.zul'

de.forsthaus.zksample.webui.security.groupright.model

de.forsthaus.zksample.webui.security.right
de.forsthaus.zksample.webui.security.right.model

de.forsthaus.zksample.webui.security.role
de.forsthaus.zksample.webui.security.role.model

de.forsthaus.zksample.webui.security.rolegroup
de.forsthaus.zksample.webui.security.rolgroup.model

de.forsthaus.zksample.webui.security.userrole
de.forsthaus.zksample.webui.security.userrole.model

de.forsthaus.zksample.webui.user
de.forsthaus.zksample.webui.user.model

de.forsthaus.zksample.webui.util
de.forsthaus.zksample.webui.util.pagging
de.forsthaus.zksample.webui.util.searching


  
      
      
      
      
  
           

