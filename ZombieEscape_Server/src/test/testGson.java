package test;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.internal.StringMap;

public class testGson {
	public static void main(String[] args){
		Gson gson = new Gson();
		int[] values = {1, 2, 3};
		testMessage t1 = new testMessage("bla", new testMessage("bla2", values));
		
		String json = gson.toJson(t1);
		System.out.println(json);
		
		testMessage t2 = gson.fromJson(json, testMessage.class);
		System.out.println(t2);
		StringMap t2value = (StringMap) t2.value;
		testMessage t3 = gson.fromJson(t2value.toString(), testMessage.class);
		System.out.println(t3);
		System.out.println(t3.getClass());
		ArrayList<Integer> valuesDe = (ArrayList<Integer>) t3.value;
		System.out.println(valuesDe.get(0));

		
	}
	


}
