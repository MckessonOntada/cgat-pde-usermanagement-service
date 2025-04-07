package com.mckesson.cgat.pde.usermanagement.model;

import java.util.EnumSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Permission {

	MANAGE_SP_ACCOUNT(true, false), ADD_SP_ACCOUNT(true, false), MANAGE_USERS_SCREEN(true, true), ADD_USERS(true, true),
	VIEW_USERS(true, true), EDIT_USERS(true, true), UPLOAD_USERS(true, true),
	CREATE_USER_FOR_PENDING_USER_REQUEST(true, true), MANAGE_SYSTEM_ADMINISTRATOR_SCREEN(true, false),
	ADD_SYSTEM_ADMINISTRATOR(true, false), VIEW_SYSTEM_ADMINISTRATOR(true, false),
	EDIT_SYSTEM_ADMINISTRATOR(true, false), MANAGE_ROLES_SCREEN(true, true), VIEW_ROLES(true, true),
	ADD_ROLES(true, true), EDIT_ROLES(true, true), ON_BEHALF_OF_USER(true, true),
	MANAGE_ORGANIZATION_SCREEN(true, false), VIEW_ORGANIZATION(true, false), ADD_ORGANIZATION(true, false),
	EDIT_ORGANIZATION(true, false), MANAGE_ORDERS(false, true), VIEW_ORDERS(false, true),
	VIEW_PENDING_ORDERS(false, true), ADD_ORDERS(false, true), EDIT_ORDERS(false, true), APPROVE_ORDERS(false, true),
	CANCEL_ORDER(false, true), REJECT_ORDER(false, true), ADD_APPROVER_GROUP(false, true),
	VIEW_APPROVER_GROUP(false, true), MANAGE_APPROVER_GROUP(false, true), EDIT_APPROVER_GROUP(false, true),
	VIEW_QUESTIONNAIRE(true, false), CONFIGURE_QUESTIONNAIRE(true, false), DEACTIVATE_QUESTIONNAIRE(true, false),
	MANAGE_MATERIAL(true, false), VIEW_MATERIAL(true, false), VIEW_CATALOG(true, false), PAY_INVOICES(false, true),
	MANAGE_CUSTOMER_STATEMENT(false, true), MANAGE_CATALOG(true, false), ADD_QUESTIONNAIRE(true, false),
	VIEW_INVOICES(false, true), MANAGE_INVOICES(false, true), VIEW_PAYMENT_HISTORY(false, true),
	VIEW_CUSTOMER_STATEMENT(false, true), VIEW_PAYMENT_METHOD_CARD(false, true), ADD_PAYMENT_METHOD_CARD(false, true),
	MANAGE_PAYMENT_METHOD_CARD(false, true), DELETE_PAYMENT_METHOD_CARD(false, true),
	VIEW_PAYMENT_METHOD_BANK(false, true), ADD_PAYMENT_METHOD_BANK(false, true),
	MANAGE_PAYMENT_METHOD_BANK(false, true), DELETE_PAYMENT_METHOD_BANK(false, true), CANCEL_PAYMENT(false, true),
	VIEW_CREDIT_LISTING(false, true), MANAGE_SYSTEM_CONFIGURATIONS(true, false), VIEW_ACCOUNTS(true, true),
	ADD_PROVIDER(false, true), EDIT_PROVIDER(false, true), VIEW_PROVIDER(false, true),
	MANAGE_PROVIDERS_SCREEN(false, true), VIEW_SP_MATERIAL(true, false);

	boolean isActiveForSystemAdmin;

	boolean isActiveForOthers;

	public static Set<Permission> getPermissions(UserType userType) {
		Set<Permission> permissions = EnumSet.noneOf(Permission.class);
		for (Permission permission : Permission.values()) {
			if (userType.equals(UserType.ADMIN) && permission.isActiveForSystemAdmin()) {
				permissions.add(permission);
			}
			else if (userType.equals(UserType.GENERAL) && permission.isActiveForOthers()) {
				permissions.add(permission);
			}
		}
		return permissions;
	}

}