/*
Create a database storing the NVD data
Author(s) : Su Zhang, Xinming Ou
Copyright (C) 2011, Argus Cybersecurity Lab, Kansas State University

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.CharBuffer;
import java.sql.*;

import java.time.Year;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.sql.*;



import java.io.BufferedReader;

import java.io.File;

import java.io.FileNotFoundException;

import java.io.FileReader;

import java.io.FileWriter;

import java.io.IOException;

import java.util.ArrayList;

import java.util.Iterator;

import java.util.List;



import org.dom4j.Attribute;

import org.dom4j.Document;

import org.dom4j.DocumentException;

import org.dom4j.Element;

import org.dom4j.io.SAXReader;

import org.dom4j.io.XMLWriter;



public class InitializeDB {

	public static String TOTAL_RESULTS = "totalResults";
	public static String START_INDEX = "startIndex";
	public static int PAGE_LIMIT = 2000;
	public static int OCT = 10;
	public static int JAN = 1;

	private static int MONTH_INCREMENT = 3;
	private static int READ_LIMIT = "{\"resultsPerPage\":xxxx,\"startIndex\":xxxx,\"totalResults\":xxxx,".length() + 10;

	/*public static Connection getConnection() throws SQLException,

			java.lang.ClassNotFoundException {

		String url = "jdbc:mysql://localhost:3306/mulvalDB";

		Class.forName("com.mysql.jdbc.Driver");

		String userName = "root";

		String password = "";



		Connection con = DriverManager.getConnection(url, userName, password);

		return con;

	}*/

	public static Connection getConnection() throws SQLException,

	java.lang.ClassNotFoundException, IOException {

//String url = "jdbc:mysql://localhost:3306/mulvalDB";

Class.forName("com.mysql.jdbc.Driver");

//String userName = "root";

//String password = "";

String url="";

String userName="";

String password="";
String MulvalRootEnv = System.getenv("MULVALROOT");

//System.out.println(MulvalRootEnv);

//File f = new File(MulvalRootEnv + "/src/dataPreProcessing/translator/config.txt");

File f= new File("config.txt");

String path = f.getPath();



	

	BufferedReader breader= new BufferedReader(new FileReader(path));

	

	url=breader.readLine();

	userName=breader.readLine();

	password=breader.readLine();

	Connection con = DriverManager.getConnection(url, userName, password);

	return con;	

}



	public static void main(String[] args) {

		

//		setupDB(Integer.parseInt(args[0]));
			setupDbWithJSON(Integer.parseInt(args[0]));
		

	}

	// Prefixing zero for MM format
	private static String padNumber(int n) {
		if (n > 9)
			return "" + n;
		return "0" + n;
	}

	// getting the result offset if results more than PAGE_LIMIT
	// otherwise returns -1
	public static int moreResultsOffset(InputStream in) {

		try {
			in.mark(READ_LIMIT); // marking so can be reset later
			InputStreamReader reader = new InputStreamReader(in);
			Map<String, Integer> resultsMetaData = getMetaData(reader);
			in.reset(); // resetting so that JSON parser can work with entire response

			// if results are more than possible on the latest page, return updated start index
			if (resultsMetaData.get(TOTAL_RESULTS) > resultsMetaData.get(START_INDEX) + PAGE_LIMIT) {
				return resultsMetaData.get(START_INDEX) + PAGE_LIMIT;
			}
			return -1;
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	// gets meta data from the api request to check number of results and pages
	private static Map<String, Integer> getMetaData(InputStreamReader reader) throws IOException {
		char[] buffer = new char[READ_LIMIT];

		reader.read(buffer, 0, READ_LIMIT);
		String line = String.valueOf(buffer);

		// storing total results and start index
		Map<String, Integer> resultsMetaData = new HashMap<>();
		resultsMetaData.put(TOTAL_RESULTS, 0);
		resultsMetaData.put(START_INDEX, 0);

		for (String s : resultsMetaData.keySet()) {
			// find the value:
			int index = line.indexOf(s);
			index += (s + "\":").length();
			String valueStr = line.substring(index);
			valueStr = valueStr.substring(0, valueStr.indexOf(','));
			int value = Integer.parseInt(valueStr);

			// store it in the map
			resultsMetaData.put(s, value);
		}
		return resultsMetaData;
	}

	// makes HTTP connection over API and returns input stream
	public static InputStream getStream(int year, int month, int startIndex) throws MalformedURLException, IOException {

		// getting months in the MM format
		String monthStr = padNumber(month);
		String nextMonthStr = padNumber(month + MONTH_INCREMENT);
		String nextDayStr = "01";
		// special case for end of the year
		if (month == OCT) {
			nextMonthStr = padNumber(month + MONTH_INCREMENT - 1);
			nextDayStr = "31";
		}

		// establishing API connection over HTTP
		HttpURLConnection connection = (HttpURLConnection)
				new URL("https://services.nvd.nist.gov/rest/json/cves/2.0/?pubStartDate=" +
						year + "-" + monthStr + "-01T00:00:00.000-05:00&pubEndDate=" + year + "-" +
						nextMonthStr + "-"+ nextDayStr + "T23:59:59.999-05:00&" + START_INDEX + "=" + startIndex)
						.openConnection();
		return connection.getInputStream();
	}

	// setting up nvd db with the new NVD API
	public static void setupDbWithJSON(int year) {
		try {
			Connection con = getConnection();

			Statement sql = con.createStatement();
			sql.execute("drop table if exists nvd");                                                                                                                                                                                                        //,primary key(id)
			sql.execute("create table nvd(id varchar(20) not null," + 
			"soft varchar(256) not null default 'ndefined'," + 
			"rng varchar(100) not null default 'undefined'," + 
			"lose_types varchar(100) not null default 'undefind'," + 
			"severity varchar(20) not null default 'unefined'," + 
			"access varchar(20) not null default 'unefined'," + 
			"exploit float(0) not null default -1.0," + 
			"impact float(0) not null default -1.0);");

			for (int y = year; y <= Year.now().getValue(); y++) {
				System.out.println("Getting vulnerabilities for " + y + ":");

				int startIndex = 0;
				boolean printed = false;

				// only going till OCT since 3 month increments
				for (int m = JAN; m <= OCT; m += MONTH_INCREMENT) {
					if (!printed) {
						System.out.print(" - Months " + m + " to " + (m + MONTH_INCREMENT - 1) + "...");
						printed = true;
					}

					// calling NVD API
					BufferedInputStream bin = new BufferedInputStream(getStream(y, m, startIndex));
					InputStreamReader reader  = new InputStreamReader(bin);

					// checking if there are more results (>1 pages)
					int moreResultsOffset = moreResultsOffset(bin);

					List<VulnerabilityParser.Vulnerability> vuls = VulnerabilityParser.parse(reader);

					reader.close();

					// populating the nvd mysql db:
					for(VulnerabilityParser.Vulnerability vul : vuls) {
						if (!vul.id.equals("NULL")) {
							String insert = "insert nvd values('" + vul.id + "','"
									+ vul.software + "','" + vul.rge + "','" + vul.lose_types + "','" + vul.sev
									+ "','" + vul.access +"'," + vul.exploitabilityScore + "," + vul.impactScore + ")";
							sql.execute(insert);
						}
					}

					// if there is more than 1 page, call the same link with offset
					if (moreResultsOffset != -1) {
						startIndex = moreResultsOffset; // start offset
						m -= MONTH_INCREMENT; // same month
						continue;
					}
					System.out.println("Done!");
					startIndex = 0; // reset offset
					printed = false;
				}
			}

			sql.close();
			con.close();

		} catch (java.lang.ClassNotFoundException e) {
			System.err.println("ClassNotFoundException:" + e.getMessage());
		} catch (SQLException ex) {
			System.err.println("SQLException:" + ex.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setupDB(int year) {



		try {

			//String filename = "nvdcve-2008.xml";



			Connection con = getConnection();

			Statement sql = con.createStatement();

			sql.execute("drop table if exists nvd");                                                                                                                                                                                                        //,primary key(id)

			sql.execute("create table nvd(id varchar(20) not null,soft varchar(160) not null default 'ndefined',rng varchar(100) not null default 'undefined',lose_types varchar(100) not null default 'undefind',severity varchar(20) not null default 'unefined',access varchar(20) not null default 'unefined');");

	

			SAXReader saxReader = new SAXReader();

			

			

			for(int ct=2002;ct<=year;ct++){

			    //String fname="/transient/mulval/oval/nvd/nvdcve-"+Integer.toString(ct)+".xml";
				String fname="nvd_xml_files/nvdcve-"+Integer.toString(ct)+".xml";

			

			Document document = saxReader.read(fname);



			List entry = document.selectNodes("/*[local-name(.)='nvd']/*[local-name(.)='entry']");

			Iterator ent = entry.iterator();

               int act=0;

					while (ent.hasNext()) { // varchar(20) not null default 'name',

				Element id = (Element) ent.next();



				String cveid = id.attributeValue("name");
				
				String cvss = "";
				
				
				
				
				
				String access = "";
				
				
			//	System.out.println(cveid + access);

				String sev = "";

				String host = "localhost";

				String sftw = "";

				String rge = "";

				String rge_tmp = "";

				String lose_tmp = "";

				String lose_types = "";

				ArrayList<String> subele = new ArrayList<String>();

				ArrayList<String> attr = new ArrayList<String>();

				Iterator ei = id.elementIterator();

				while (ei.hasNext()) { // put all of the subelements'

										// names(subelement of entry) to the

										// array list



					Element sube = (Element) ei.next();

					subele.add(sube.getName());



				}

				//System.out.println(id.getText());

				Iterator i = id.attributeIterator();

				while (i.hasNext()) { // put the attributes of the entries to

										// the arraylist



					Attribute att = (Attribute) i.next();

					attr.add(att.getName());



				}

				if (subele.contains("vuln_soft")) {

					Element vs = (Element) id.element("vuln_soft");

					Iterator itr = vs.elementIterator("prod");

					while (itr.hasNext()) { // record all of the softwares

						Element n = (Element) itr.next();



						//sftw = sftw + n.attributeValue("name") + ",";

						sftw =n.attributeValue("name");

					if(sftw.contains("'")){

							

							sftw=sftw.replace("'", "''");

						}	

						break;

					}

					//int lsf = sftw.length();

					//sftw = sftw.substring(0, lsf - 1);// delete the last comma



				}

				if (attr.contains("severity")) {



					sev = id.attributeValue("severity");



				}
				
				if (attr.contains("CVSS_vector")) 
				{
					
					cvss = id.attributeValue("CVSS_vector");
					char ac = cvss.charAt(9);
					if (ac=='L')
						access="l";
					else if (ac =='M')
						access="m";
					else if (ac=='H')
						access="h";
					else ;
					
				}



				if (subele.contains("range")) { // to get the range as a array

					Element vs = (Element) id.element("range");

					Iterator rgi = vs.elementIterator();

					while (rgi.hasNext()) { // record all of the softwares

						Element rg = (Element) rgi.next();

						if (rg.getName().equals("user_init"))

							rge_tmp = "user_action_req";

						else if (rg.getName().equals("local_network"))

							rge_tmp = "lan";

						else if (rg.getName().equals("network"))

							rge_tmp = "remoteExploit";

						else if (rg.getName().equals("local"))

							rge_tmp = "local";

						else

							rge_tmp = "other";



						rge = rge + "''"+rge_tmp + "'',";

					}

					int lr = rge.length();

					rge = rge.substring(0, lr - 1);// delete the last comma



				}

				if (subele.contains("loss_types")) {



					Element lt = (Element) id.element("loss_types");

					Iterator lti = lt.elementIterator();

					while (lti.hasNext()) {

						ArrayList<String> isecat = new ArrayList<String>();

						Element ls = (Element) lti.next();



						if (ls.getName().equals("avail"))

							lose_tmp = "availability_loss";

						else if (ls.getName().equals("conf"))

							lose_tmp = "data_loss";



						else if (ls.getName().equals("int"))

							lose_tmp = "data_modification";



						else

							lose_tmp = "other";

						lose_types = lose_types +"''"+ lose_tmp + "'',";

					}

					int ltp = lose_types.length();

					lose_types = lose_types.substring(0, ltp - 1);// delete the

																	// last

																	// comma



				}

				//System.out.println(cveid + lose_types + rge + sftw + sev + access);

				String insert = "insert nvd values('" + cveid + "','"

						+ sftw + "','" + rge + "','" + lose_types + "','" + sev

						+ "','" + access+"')";

				sql.execute(insert);



			}

			}

			sql.close();

			con.close();

			

		} catch (java.lang.ClassNotFoundException e) {

			System.err.println("ClassNotFoundException:" + e.getMessage());

		} catch (SQLException ex) {

			System.err.println("SQLException:" + ex.getMessage());

		} catch (DocumentException e) {



			e.printStackTrace();

		} catch (IOException e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}



	}



	public static void clearEntryWithVulsoft(String filename) {



		try {



			SAXReader saxReader = new SAXReader();

			Document document = saxReader.read(filename);

			



			List soft = document

					.selectNodes("/*[local-name(.)='nvd']/*[local-name(.)='entry']/*[local-name(.)='vuln_soft']");

			Iterator sft = soft.iterator(); 

			Element nvd = (Element) document

					.selectSingleNode("/*[local-name(.)='nvd']");



			while (sft.hasNext()) {



				Element vsft = (Element) sft.next();

				nvd.remove(vsft.getParent());

				XMLWriter output = new XMLWriter(new FileWriter(filename));//

				output.write(document);

				output.flush();

				output.close();



			}



		} catch (Exception e) {



			e.printStackTrace();

		}

	}

}

