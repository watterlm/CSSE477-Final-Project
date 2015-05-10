/*
 * TestCrawler.java
 * Oct 29, 2012
 *
 * FriendnFoe Attacker
 * 
 * Copyright (C) 2012 Chandan Raj Rupakheti
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License 
 * as published by the Free Software Foundation, either 
 * version 3 of the License, or any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/lgpl.html>.
 * 
 */

package friend;

/**
 * 
 * 
 * @author Chandan Raj Rupakheti (rupakhet@rose-hulman.edu)
 */
public class TestCrawler {
	public static void main(String[] args) {
		Crawler crawler = new Crawler("localhost", 8080, "/HomePageApplication/HomePage", Runtime.getRuntime().availableProcessors());
		crawler.addCrawlListener(new CrawlListener() {
			@Override
			public void directoryAdded(Object source, String directory) {
				System.out.println("Directory Added: " + directory);
			}

			@Override
			public void exceptionDetected(Object source, Exception e) {
				System.out.println("Exception: " + e.getMessage());
			}
		});
		
		try {
			crawler.crawl();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
}
