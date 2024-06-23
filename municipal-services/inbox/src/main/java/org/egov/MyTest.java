package org.egov;

public class MyTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String smsBody= "Dear Citizen, Your mSeva Punjab Login OTP is 221222. Now you can log a complaint from WhatsApp, give a missed call on 8750975975|1301157492438182299|1407160818093170395";
		String [] smsparam= smsBody.split("\\|");
		for(String str:smsparam) {
			System.out.println(str);
		}
	}

}
