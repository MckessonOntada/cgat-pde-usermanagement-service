DELETE FROM usermanagement.user_permission_mapping upm 
where user_id 
IN ( 
	select u.id
	FROM usermanagement.user u
	WHERE u.role_id = '1' ) 
AND upm.permission NOT IN (
          'MANAGE_USERS_SCREEN',
          'ADD_USERS',
          'VIEW_USERS',
          'EDIT_USERS',
          'UPLOAD_USERS',
          'CREATE_USER_FOR_PENDING_USER_REQUEST',
          'MANAGE_SYSTEM_ADMINISTRATOR_SCREEN',
          'ADD_SYSTEM_ADMINISTRATOR',
          'VIEW_SYSTEM_ADMINISTRATOR',
          'EDIT_SYSTEM_ADMINISTRATOR',
          'MANAGE_ROLES_SCREEN',
          'VIEW_ROLES',
          'ADD_ROLES',
          'EDIT_ROLES',
          'ON_BEHALF_OF_USER',
          'MANAGE_ORGANIZATION_SCREEN',
          'VIEW_ORGANIZATION',
          'ADD_ORGANIZATION',
          'EDIT_ORGANIZATION',
          'VIEW_QUESTIONIARE',
          'CONFIGURE_QUESTIONNIARE',
          'DEACTIVATE_QUESTIONNIARE',
          'MANAGE_MATERIAL',
          'VIEW_MATERIAL',
          'VIEW_CATALOG',
          'MANAGE_CATALOG',
          'ADD_QUESTIONNIARE',
          'MANAGE_SYSTEM_CONFIGURATIONS',
          'VIEW_ACCOUNTS',
          'APPROVE_PENDING_USER_REQUEST'
      )
