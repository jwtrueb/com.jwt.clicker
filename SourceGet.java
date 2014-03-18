/**
 * File: SourceGet.java
 * 
 * @author Jacob Description: Gets the HTML source code from a website
 */

import java.io.IOException;
import java.util.ArrayList;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class SourceGet {

	private static String htmlSource;
	private static ArrayList<String> htmlGrades;
	private static String[] stringGrades;
	private static ArrayList<ArrayList<ArrayList<Object>>> grades;

	public SourceGet(String string) throws IOException {
		WebDriver web = new HtmlUnitDriver();

		web.get("http://tigerview.ecusd7.org/homeaccess");
		System.out.println("Page title is: " + web.getTitle());

		System.out.println("username element");
		WebElement username = web.findElement(By.id("ctl00_plnMain_txtLogin"));
		System.out.println("password element");
		WebElement password = web.findElement(By
				.id("ctl00_plnMain_txtPassword"));

		username.sendKeys("t.trueb");
		password.sendKeys("14850000");

		System.out.println("log in element");
		WebElement logIn = web.findElement(By.id("ctl00_plnMain_Submit1"));
		logIn.click();

		System.out.println("Page title is: " + web.getTitle());
		
		int schoolIDnum = 937;
		String schoolID = ""+schoolIDnum;
		web.get("http://tigerview.ecusd7.org/homeaccess/Student/DailySummary.aspx?student_id=" + schoolID);
		System.out.println("Page title is: " + web.getTitle());
		
		web.get("http://tigerview.ecusd7.org/homeaccess/Student/Assignments.aspx");
		System.out.println("Page title is: " + web.getTitle());
		
		htmlSource = web.getPageSource();
	}
	
	public static ArrayList<String> getClasses(){
		//get the class list
		System.out.println("////////////////////////////////////////////");
		String classes = htmlSource.substring(htmlSource.indexOf("(All Classes)"));
		classes = classes.substring(50);
		classes = classes.substring(classes.indexOf("(All Classes)"));
		classes = classes.substring(0, classes.indexOf("</select>"));  //this gets desired bit
		
		/*
		  (All Classes)
                            </option>
                            <option value="115408|1">
                              23230 - 40   Hon English Lit
                            </option>
                            <option value="115218|1">
                              33430 - 20   AP Calculus BC
                            </option>
                            <option value="115209|1">
                              34330 - 10   AP Comp Science
                            </option>
                            <option value="115173|1">
                              54430 - 10   AP Physics
                            </option>
                            <option value="115768|1">
                              63333 - 10   Hon Anthropolgy
                            </option>
                            <option value="115762|1">
                              63433 - 50   Hon Psychology
                            </option>
                            <option value="115522|1">
                              84230 - 40   Hon Spanish 4
                            </option>
		 */
		
		String[] classesArray = classes.split("[<>]"); //put classes into an array
		for(int i = 0; i < classesArray.length; i++)
			classesArray[i] = cleanUp(classesArray[i]);
		
		ArrayList<String> classList = new ArrayList<String>(); //pull out only the classes
		for(int i = 0; i < classesArray.length; i++){
			if(!classesArray[i].contains("option") && !classesArray[i].contains("All Classes") &&  classesArray[i].length() != 0)
				classList.add(classesArray[i]);
		}
		
//		for(String clas : classList)
//			System.out.println(clas + " is this long " + clas.length());
		
		return classList;
	}
	
	/**
	 * find each class division and pull out the grades
	 * @return [ class1 , class2, ... ]
	 * class1 = assignment1, assignment2, assignment3, ...
	 * assignment = [assignmentName, score, outOf]
	 */
	public static ArrayList<ArrayList<ArrayList<Object>>> getClassGrades() {
		ArrayList<String> classes = getClasses();
		if (htmlGrades == null) {
			stringGrades = htmlSource.split("div");
			htmlGrades = new ArrayList<String>();

			for (int i = 0; i < stringGrades.length; i++) {
				// has a class
				for (String clas : classes)
					if (stringGrades[i].contains(clas) && !htmlGrades.contains(stringGrades[i]))
						htmlGrades.add(stringGrades[i]);
			}
		}
		
		ArrayList<ArrayList<ArrayList<Object>>> grades = new ArrayList<ArrayList<ArrayList<Object>>>();
		//grades for each classes
		for(int i = 0; i < classes.size(); i++){
			/*
			 * 			  <td>
                            Date Due
                          </td>
                          <td>
                            Date Assigned
                          </td>
                          <td>
                            Assignment
                          </td>
                          <td>
                            Category
                          </td>
                          <td>
                            Score
                          </td>
                          <td>
                            Weight
                          </td>
                          <td>
                            Weighted Score
                          </td>
                          <td>
                            Total Points
                          </td>
                          <td>
                            Weighted Total Points
                          </td>
                          <td>
                            Percentage
                          </td>
			 */
			String clazz = classes.get(i);
			ArrayList<String> gradeRows = null;
			
			for(String chunkOfHtml: htmlGrades)
			{
				if(chunkOfHtml.contains(clazz) && !chunkOfHtml.contains("<option value=\"ALL\">")){ //this is the corresponding html to class
					chunkOfHtml.replaceAll("AlternateItemRow", "ItemRow");
					chunkOfHtml.replaceAll("TableHeader", "ItemRow");
					String[] tableRows = chunkOfHtml.split("ItemRow");
					gradeRows = new ArrayList<String>();
					for(int j = 0; j < tableRows.length; j++)
					{  //checkForNumbers(tableRows[j])
						if(tableRows[j].contains("td") && !tableRows[j].contains("Percentage")){
							gradeRows.add(tableRows[j]);
						}
					}
					
					System.out.println("\nPrinting grade rows of " + clazz);
				}
			}

			if (gradeRows != null) {
				grades.add(new ArrayList<ArrayList<Object>>());
				for (String row : gradeRows) {
					ArrayList<Object> assignment = gradesFromRow(row);
					ArrayList<ArrayList<Object>> temp = grades.get(grades.size() - 1);
					temp.add(assignment);
				}
			}
		}
		
		return grades;
	}
	
	/**
	 * gets the assignment grade from the individual Row
	 * returns in an ArrayList (name of assignment, score, out of)
	 * (String, Double, Double)
	 */
	private static ArrayList<Object> gradesFromRow(String row){
		String assignmentName = "";
		Double score = null;
		Double outOf = null;
		String delim = "OpenAssignmentPopUp";
		
		if (row.indexOf(delim) != -1 && row.indexOf("</a>") != -1) {
			String temp = row
					.substring(row.indexOf(delim), row.indexOf("</a>"));
			temp = temp.substring(temp.indexOf("\">"));
			temp = cleanUp(temp);
			System.out.println(temp);
			assignmentName = temp;
		}
		
		String[] splitByTd = row.split("<td>");
//		System.out.println("This row is split into this many pieces " + splitByTd.length);
		for(int i = 0; i < splitByTd.length; i++){
			if(checkForNumbers(splitByTd[i]) && splitByTd[i].length() < 110){
				String piece = splitByTd[i].substring(0, splitByTd[i].indexOf("</td>"));
				piece = piece.replaceAll("\\s+","");
				if(i == 1)
					System.out.println("Date Due: "+piece);
				else if(i == 2)
					System.out.println("Date Assigned: "+piece);
				else if(i == 7){
					System.out.println("Score: "+piece);
					score = Double.parseDouble(piece);
				}
				else if(i == 9){
					System.out.println("OutOf: "+piece);
					outOf = Double.parseDouble(piece);
				}
				else
					System.out.println(piece);
			}
		}
		
		ArrayList<Object> assignment = new ArrayList<Object>();
		assignment.add(assignmentName);
		assignment.add(score);
		assignment.add(outOf);
		if(score != null && outOf != null)
			assignment.add((new Double(score / outOf * 100.0) + "%"));
		return assignment;
	}
	
	/**
	 * @param check
	 * @return whether or not there are any numbers
	 */
	private static boolean checkForNumbers(String check)
	{
		boolean bool = false;
		for(int i = 0; i < check.length(); i++)
			if(Character.isDigit(check.charAt(i)))
					bool = true;
		return bool;
	}

	/*
	 * helper method that removes all the extra spacing of the String
	 */
	private static String cleanUp(String dirty)
	{
		String clean = "";
		for(int i = 0; i < dirty.length(); i++){
			if(Character.isLetter(dirty.charAt(i)) || dirty.charAt(i) == '/')
					clean += dirty.charAt(i);
			else if(Character.isWhitespace(dirty.charAt(i))){
				if(i - 1 > -1)
					if(i + 1 < dirty.length())
						if((Character.isLetter(dirty.charAt(i-1)) || dirty.charAt(i-1) == '/') && (Character.isLetter(dirty.charAt(i+1)) || dirty.charAt(i+1) == '/'))
								clean += dirty.charAt(i); //there is not a space on either side
			}
		}
		return clean;
	}
	
	public static void main(String[] args) throws IOException {
		@SuppressWarnings("unused")
		SourceGet tigerviewSource = new SourceGet("");
		System.out.println("///////////////////////////////////////////////");
		
		grades = getClassGrades();
		System.out.println(grades.get(0).get(0).get(0));
		System.out.println(grades.get(0).get(0).get(1));
		System.out.println(grades.get(0).get(0).get(2));
		System.out.println(grades.get(0).get(0).get(3));
		
	}
}
