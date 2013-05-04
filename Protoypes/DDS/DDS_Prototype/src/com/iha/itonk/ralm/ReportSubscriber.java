package com.iha.itonk.ralm;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataWriter;

public class ReportSubscriber {
	public static final void main(String[] args) {
		
		System.out.println("Welcome to the " + (args.length > 0 ? args[0] : "Unknown station"));
		
		if(args.length < 2) {
			System.out.println("This station does not have any topics to receive." +
					"\nThere's no need for it!!!" +
					"\nSee you...");
			return;
		}
		
		/* GET DOMAIN */
		DomainParticipant domain = RTIHelper.getDomain();
        if (domain == null) {
            System.err.println("Could not create new instance of domain participant");
            return;
        }
        
        System.out.println("Will listen for:");
        
        ReportListener listener = new ReportListener();
        for(int i = 1; i < args.length; i++) {
        	Topic topic = RTIHelper.createTopic(domain, args[i]);
        	domain.create_datareader(
        			topic, 
                    Subscriber.DATAREADER_QOS_DEFAULT,
                    listener,         // Listener
                    StatusKind.DATA_AVAILABLE_STATUS);
        	
        	System.out.println(" - " + args[i]);
        }
        
        while(true){}
	}
}
