// ****************************************************************************
//         (c) Copyright, Real-Time Innovations, All rights reserved.       
//                                                                          
//         Permission to modify and use for internal purposes granted.      
// This software is provided "as is", without warranty, express or implied. 
//                                                                          
// ****************************************************************************

package com.iha.itonk.ralm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataReader;
import com.rti.dds.type.builtin.KeyedStringTypeSupport;
import com.rti.dds.type.builtin.StringDataReader;
import com.rti.dds.type.builtin.StringTypeSupport;

//****************************************************************************
public class HelloSubscriber extends DataReaderAdapter {

    // For clean shutdown sequence
    private static boolean shutdown_flag = false;

    public static final void main(String[] args) {
        // Create the DDS Domain participant on domain ID 0
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(
                0, // Domain ID = 0
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (participant == null) {
            System.err.println("Unable to create domain participant");
            return;
        }
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String toReadFrom = "Hello Wrong";
        try {
			toReadFrom = reader.readLine();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
        String[] topics_str = toReadFrom.split(",");
        
        HelloSubscriber helloSubscriber = new HelloSubscriber();

        for(String topic_str : topics_str)
        {
        	Topic topic = createTopic(participant, topic_str);
            if (topic == null) {
                System.err.println("Unable to create topic.");
                return;
            }
        	
            // Create the data reader using the default publisher
			DataReader dataReader = participant.create_datareader(
                    topic, 
                    Subscriber.DATAREADER_QOS_DEFAULT,
                    helloSubscriber,         // Listener
                    StatusKind.DATA_AVAILABLE_STATUS);
            if (dataReader == null) {
                System.err.println("Unable to create DDS Data Reader");
                return;
            }	
        }

        System.out.println("Ready to read data.");
        System.out.println("Press CTRL+C to terminate.");
        for (;;) {
            try {
                Thread.sleep(2000);
                if(shutdown_flag) break;
            } catch (InterruptedException e) {
                // Nothing to do...
            }
        }
	
        System.out.println("Shutting down...");
        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }

	private static Topic createTopic(DomainParticipant participant
			, String toReadFrom) {
		// Create the topic "Hello World" for the String type
        Topic topic = participant.create_topic(
        		toReadFrom, 
        		KeyedStringTypeSupport.get_type_name(), 
                DomainParticipant.TOPIC_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
		return topic;
	}

    /*
     * This method gets called back by DDS when one or more data samples have
     * been received.
     */
    public void on_data_available(DataReader reader) {
        KeyedStringDataReader stringReader = (KeyedStringDataReader) reader;
        SampleInfo info = new SampleInfo();
        for (;;) {
            try {
            	KeyedString sample = new KeyedString();
                stringReader.take_next_sample(sample,info);
                if (info.valid_data) {
                	System.out.println("\nNEW INFORMATION RECEIVED:");
                	System.out.println("  Key:   "+sample.key);
                	System.out.println("  Value: "+sample.value);
                    if (sample.equals("")) {
                        shutdown_flag = true;
                    }
                }
            } catch (RETCODE_NO_DATA noData) {
                // No more data to read
                break;
            } catch (RETCODE_ERROR e) {
                // An error occurred
                e.printStackTrace();
            }
        }
    }
}
