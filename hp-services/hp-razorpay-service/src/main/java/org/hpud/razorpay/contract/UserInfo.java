package org.hpud.razorpay.contract;

import java.util.List;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This is acting ID token of the authenticated user on the server. Any value
 * provided by the clients will be ignored and actual user based on authtoken
 * will be used on the server. Author : Narendra
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
	
private Long id;

@Size(max = 180)
private String userName;

@Size(max = 250)
private String name;

@Size(max = 50)
private String type;

@Size(max = 150)
private String mobileNumber;

@Size(max = 300)
private String emailId;

private List<Role> roles;

@Size(max = 256)
private String tenantId;

@Size(max = 36)
private String uuid;}
