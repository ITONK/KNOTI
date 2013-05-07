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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataWriter;

public class CityPublisher {
	
	// Main method. Arguments: 1) Name of city
	public static final void main(String[] args) {
				
		// Get domain
		DomainParticipant domain = RTIHelper.getDomain();
        if (domain == null) {
            System.err.println("Could not create new instance of domain participant");
            return;
        }
        
		System.out.println("Welcome to the city of " + (args.length > 0 ? args[0] : "Unknown"));
        System.out.println("Available categories (topics):");
        
        // Create a DataWriter for each of the topics
        ArrayList<KeyedStringDataWriter> writers = new ArrayList<KeyedStringDataWriter>();
        int i = 0;
        for(String topic_str : RTIHelper.TOPICS){
        	Topic topic = RTIHelper.createTopic(domain, topic_str);
        	KeyedStringDataWriter writer = RTIHelper.createKeyStringWriter(domain, topic);
        	writers.add(writer);
        	
        	System.out.println("  "+ i + ": " + topic_str);
        	i++;
        }
                
        // Based on input, send to one of the DataWriters
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while(true) {
        	try{
        		System.out.print("\nChoose a category (number): ");
        		int topic_number = Integer.parseInt(reader.readLine());
        		
        		System.out.print("Where are you reporting from? (key): ");
        		String key = reader.readLine();
        		
        		System.out.print("What's wrong? (value): ");
        		String value = reader.readLine();	
        		
        		// Send data
        		KeyedString data = new KeyedString(key, value);
        		KeyedStringDataWriter writer = writers.get(topic_number);
        		writer.write(data, InstanceHandle_t.HANDLE_NIL);
        		System.out.println("Your report have been send...");
        	} catch(Throwable t)
        	{
        		System.out.println("Something went wrong.. Try again!");
        	}
        }
        
        // Normally we should clean up the domain before leaving,
        // but we've left it out since this is just prototyping 
	}
}
