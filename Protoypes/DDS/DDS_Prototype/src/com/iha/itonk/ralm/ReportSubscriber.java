/* 
 * Made by:
 * 	10796 - Anders Kielsholm
 *  10063 - Lasse Hansen
 *  10893 - Rasmus Bækgaard
 *  10959 - Mia Louise Leth Sørensen
 *  
 *  Aarhus School of Engineering - www.iha.dk
 */

package com.iha.itonk.ralm;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.topic.Topic;

public class ReportSubscriber {
	
	// Main method. Arguments: 1) Name of station 2-?) Topics to listen on
	public static final void main(String[] args) {
		
		if(args.length < 2) {
			System.out.println("This station does not have any topics to receive." +
					"\nThere's no need for it!!!" +
					"\nSee you...");
			return;
		}
		
		// Get domain
		DomainParticipant domain = RTIHelper.getDomain();
        if (domain == null) {
            System.err.println("Could not create new instance of domain participant");
            return;
        }

		System.out.println("Welcome to the " + (args.length > 0 ? args[0] : "Unknown station"));
        System.out.println("Will listen for:");
        
        // Create one listener and make it subscribe for the topics defined in argument 2-?
        ReportListener listener = new ReportListener();
        for(int i = 1; i < args.length; i++) {
        	Topic topic = RTIHelper.createTopic(domain, args[i]);
        	RTIHelper.createKeyedStringReader(domain, listener, topic);
        	
        	// Print that we will listen for this topic
        	System.out.println(" - " + args[i]);
        }
        
        while(true){}
        
        // Normally we should clean up the domain before leaving,
        // but we've left it out since this is just prototyping 
	}
}
