project-name: zk_sample_gui 
needed projects:  zk_sample_db (backend)
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
      Called from the web.xml. This class starts the H2 database server and
      creates the tables and the demo data from the text-file  
      'de.forsthaus.sampledata.createSampleData.sql'     
  --> ObjectMaschine.java
      This class creates additionally customer records randomly from txt-files 
      resides under '/WebContent/res/*.txt'     
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

#######################################################################      
#                      de.forsthaus.zksample.webui                    #
#######################################################################
#      All webui-classes are seperated in                             #
#    ...webui.modulName and ...webui.modulName.Model                  #
#   Where .modulname holds the Controllers for the Lists and Dialogs  # 
#   and .model holds the renderers and helper classes.                #
#######################################################################

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

#######################################################################      
#                 de.forsthaus.zksample.webui.security                #
#######################################################################      
#       Controllers for the management pages of the security.         #
#       User roles/groups ...                                         #
#######################################################################      

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
  --> SecGrouprightDialogGroupListModelItemRenderer.java
      Listbox ListItem renderer
  --> SecGrouprightListModelItemRenderer.java
      Listbox ListItem renderer
  --> SecGrouprightRightComparator.java
      Comparator for the sorting the listbox data
  --> SecGrouprightRightListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.security.right
  --> SecRightDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_right/secRightDialog.zul'
  --> SecRightListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_right/secRightList.zul'
      
de.forsthaus.zksample.webui.security.right.model
  --> SecRightComparator.java
      Comparator for the sorting the listbox data
  --> SecRightListModelItemRenderer.java
      Listbox ListItem renderer
  --> SecRightSecTypListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.security.role
  --> SecRoleDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_role/secRoleDialog.zul'
  --> SecRoleListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_role/secRoleList.zul'

de.forsthaus.zksample.webui.security.role.model
  --> SecRoleComparator.java
      Comparator for the sorting the listbox data
  --> SecRoleListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.security.rolegroup
  --> SecRolegroupCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_rolegroup/secRolegroup.zul'

de.forsthaus.zksample.webui.security.rolgroup.model
  --> SecRolegroupGroupComparator.java
      Comparator for the sorting the listbox data
  --> SecRolegroupGroupListModelItemRenderer.java
      Listbox ListItem renderer
  --> SecRolegroupRoleComparator.java
      Comparator for the sorting the listbox data
  --> SecRolegroupRoleListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.security.userrole
  --> SecUserroleCtrl.java
      Controller for '/WebContent/WEB-INF/pages/sec_userrole/secUserrole.zul'

de.forsthaus.zksample.webui.security.userrole.model
  --> SecUserroleRoleComparator.java
      Comparator for the sorting the listbox data
  --> SecUserroleRoleListModelItemRenderer.java
      Listbox ListItem renderer
  --> SecUserroleUserComparator.java
      Comparator for the sorting the listbox data
  --> SecUserroleUserListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.user
  --> UserDialogCtrl.java
      Controller for '/WebContent/WEB-INF/pages/user/userDialog.zul'
  --> UserListCtrl.java
      Controller for '/WebContent/WEB-INF/pages/user/userList.zul'

de.forsthaus.zksample.webui.user.model
  --> LanguageListModelItemRenderer.java
      Listbox ListItem renderer
  --> UserComparator.java
      Comparator for the sorting the listbox data
  --> UserListModelItemRenderer.java
      Listbox ListItem renderer
  --> UserRolesListModelItemRenderer.java
      Listbox ListItem renderer

de.forsthaus.zksample.webui.util
  --> BaseCtrl.java
      Base controller for all zul-files that will extends the window
      component. This controller does the autowireing/binding/forwarding stuff
  --> ButtonStatusCtrl.java
      Button controller for the CRUD buttons in the dialog windows in
      the south area of the borderlayout.
  --> MultiLineMessageBox.java
      Extended messagebox that can show multilined messages.
      Used a messagebox zul-template at '/WEB-INF/pages/util/multiLineMessageBox.zul'
  --> NoEmptyAndEqualStringsConstraint.java
      Constraint for compareing the value-strings from a textbox with a string.
      Used for compare the password with the re-typed password in the userDialog.

de.forsthaus.zksample.webui.util.pagging
  --> PagedListWrapper.java
      Helper class for allow the sorting of listheaders by paged records.

de.forsthaus.zksample.webui.util.searching
  --> SearchOperatorListModelItemRenderer.java
      Listbox ListItem renderer
  --> SearchOperators.java
      this class represents a few types of search operators corresponding to 
      the com.trg.search.Search.java class from the 
      Hibernate-Generic-DAO framework. 
 
#######################################################################      
#                               /src                                  #
#######################################################################      

  --> applicationContext-db.xml
      Spring framework configuration file. 
      Configures the Transaction Manager. 
      - All dao-methods are single transacted.
      - Service methods can contain several dao methods and all together 
        are handled as one transaction.
      Configures the path to the jdbc.properties   
      Configures the datasource   
      Remarked: Jindi-Datasource, that point to the entries in the
                server.xml of tomcat, if using a connection from tomcat
                to the database.
  
  --> jdbc.properties
      Holds the vars for configuring the jdbc properties.
  
  --> log4j.xml
      Configures the logging parameters.
   
 
 
#######################################################################      
#                             /WebContent                             #
#######################################################################      
 
  --> index.zul
      The entry zul-page.
      Controller: 'de.forsthaus.zksample.InitApplication.java'
      Shows the grid for the count of table records and statistic data.
  --> error.zul
      The error zul-page
  --> zkloginDialog.zul
      The login zul-page        
      Controller: 'de.forsthaus.zksample.webui.login.ZkLoginDialogCtrl.java'
  
      
#######################################################################      
#                         /WebContent/images/icons                    #
#######################################################################      
      
      Used images and icons.
      
      
#######################################################################      
#                         /WebContent/pages                           #
#######################################################################      
      
  --> index.zul
      Controller: 'de.forsthaus.zksample.IndexCtrl.java'
      The index zul-page for the application that holds the borderlayout
      for the areas menu, content, statusBar ..
      
#######################################################################      
#                         /WebContent/res                             #
#######################################################################      
            
      Textfiles that holds sample data. These datas are used by
      randomly creating sample customer records.
      Files are for city, last-name, surname, blobs, ...
      Controller: 'de.forsthaus.zksample.ObjectMaschine.java      

  
#######################################################################      
#                         /WebContent/WEB-INF                         #
#######################################################################

  --> i3-label_de_DE.properties
      german language file for localisation
  --> i3-label.properties 
      default language (english) file for localisation
  --> lang-addon.xml
      ZK configuration file for setting the font-size
  --> SpringSecurityContext.xml
      Spring-Security framework configuration file.
  --> web.xml
      The configuration file for the servlet container (tomcat).
  --> zk.xml
      ZK configuration file              
           

#######################################################################      
#                         /WebContent/WEB-INF/pages                   #
#######################################################################

  --> mainmenu.zul
      Controller: 'de.forsthaus.zksample.common.menu.MainMenuCtrl.java'
      Holds the window component. The menu is created dynamically by
      java code.
  --> welcome.zul
      Holds the welcome content that is showing on application start
      or click the Start button in the menu

  --> /article/articleDialog.zul
  --> /article/articleList.zul
  --> /branch/branchDialog.zul
  --> /branch/branchList.zul
  --> /chat/chat.zul
  --> /customer/customerDialog.zul
  --> /customer/customerList.zul
  --> /customer/customerSearchDialog.zul
  --> /guestbook/guestBookDialog.zul
  --> /guestbook/guestBookList.zul
  --> /order/orderDialog.zul
  --> /order/orderList.zul
  --> /order/orderPositionDialog.zul
  --> /sec_group/secGroupDialog.zul
  --> /sec_group/secGroupList.zul
  --> /sec_groupright/addGrouprightDialog.zul
  --> /sec_groupright/secGroupright.zul
  --> /sec_loginlog/secLoginLogList.zul
  --> /sec_right/secRightDialog.zul
  --> /sec_right/secRightList.zul
  --> /sec_role/secRoleDialog.zul
  --> /sec_role/secRoleList.zul
  --> /sec_rolegroup/secRolegroup.zul
  --> /sec_user/userDialog.zul
  --> /sec_user/userList.zul
  --> /sec_userrole/secUserrole.zul
  --> /util/multiLineMessageBox.zul
  
 
#######################################################################      
#                         /WebContent/WEB-INF/reports                 #
#######################################################################

     Jasper report files for a test report that includes two subreports.
     Started from: 'de.forsthaus.zksample.webui.order.OrderDialogCtrl.doPrintReport()'
  

#######################################################################      
#                         /WebContent/WEB-INF/tld                     #
#######################################################################

    Needed ZK stuff. Copy these folder from the zk-distibution.
    

#######################################################################      
#                         /WebContent/WEB-INF/xsd                     #
#######################################################################

    Needed ZK stuff. Copy these folder from the zk-distibution.
  
  
  

Security-Concept:
=================
- The Administration menu point includes the whole security administration, the users and Login log list.
- The security is build on top of the spring-security framework and is extended for groups and group-rights. 
- In the most simple examples the roles are defined for ADMIN_ROLE, USER_ROLE or GUEST_ROLE.

   This is to small for us. 
   Because what is a user ??? 
   A user in a bigger business firm can works in a department of 
   booking, invoicing, inventory, production, sales, promotion ...
   And these categories can have sub categories. So in practice we need a finer 
   granulated access for these users.
   Secondly we go away from the xml configuration for assigning the roles to a page or event. 
   We do it in database tables and do extend the security for a group and group-right. 
   Further we need not a new tomcat start to consider changes in the scurity of pages or events.

- At last: A right in the world of spring-security is only a STRING 
 While we secure the application in the CODE and manage these in tables WE and OUR CUSTOMERS would 
 have the abbility to customize the access/right. 
 While we do not secure with a ROLE-String either we secure with a RIGHT-String we can assign 
 a group of right-strings to a logically closed GROUP. 
 So RIGHTS, GROUPS and GROUP-RIGHTS are defined by the developer who knows what compoents 
 are in the application and are working together.
 All others like ROLES and ROLE-GROUPS can be modified/extended by the customer if needed/allowed.


User (have)
        \-- Roles (have)
                     \-- Role-Groups (have) extended for my needs
                                        \-- Group-Rights (have) extended for my needs
                                                            \-- Rights

Users
- All users have the right to edit her own peronal data like the name, password.
- User Roles are assigned in the administration area [User Roles].
  
User-Roles
- Here are the roles assigned to the Users. It's possible to assign several Roles to a user.  
  
Roles
- Predefined Roles. 

Role-Groups
- Predefined Role-Groups.      

Groups
- Predefined Groups.

Group-Rights
- Predefined Group-Rights.

Rights
- Predefined Rights. Each used component can have a right as a String. 

  We can secure a component in code by two methods:
  1. setting it visible/unvisible
  2. setting it readonly 

  For setting the right we call in the users Workspace the isAllowed(String right) method. 
  This methode do only searching the String in the list of the users 'grantedRights'  
  and results a  true/false.
      btnSave.setVisible(workspace.isAllowed("button_BranchDialog_btnSave"));

Technical:
==========
So, we are using a borderlayout in the 'index.zul' (the main page) and put all other called 
zul-pages in the center area of it, we have an other working like the normal jsf/jsp apps. 
As a result we always have the same URL: ~/zk_sample_gui/index.zul
Therefore we cannot work with an url-based security management like the most 'spring-security' 
samples shows for this.







 