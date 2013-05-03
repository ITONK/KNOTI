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

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataWriter;
import com.rti.dds.type.builtin.KeyedStringTypeSupport;
import com.rti.dds.type.builtin.StringTypeSupport;




//****************************************************************************
public class HelloPublisher {
    public static void main(String[] args) {
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

        // Create the topic "A Topic" for the String type
        Topic topic_A = participant.create_topic(
                "A Topic", 
                KeyedStringTypeSupport.get_type_name(), 
                DomainParticipant.TOPIC_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (topic_A == null) {
            System.err.println("Unable to create topic.");
            return;
        }
        
        // Create the topic "B World" for the String type
        Topic topic_B = participant.create_topic(
                "B Topic", 
                KeyedStringTypeSupport.get_type_name(), 
                DomainParticipant.TOPIC_QOS_DEFAULT, 
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (topic_B == null) {
            System.err.println("Unable to create topic.");
            return;
        }

        // Create the data writer using the default publisher - A
        KeyedStringDataWriter dataWriter_A =
            (KeyedStringDataWriter) participant.create_datawriter(
            	topic_A, 
                Publisher.DATAWRITER_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (dataWriter_A == null) {
            System.err.println("Unable to create data writer\n");
            return;
        }
        
        // Create the data writer using the default publisher - BLOCKED
        KeyedStringDataWriter dataWriter_B =
            (KeyedStringDataWriter) participant.create_datawriter(
            	topic_B, 
                Publisher.DATAWRITER_QOS_DEFAULT,
                null, // listener
                StatusKind.STATUS_MASK_NONE);
        if (dataWriter_B == null) {
            System.err.println("Unable to create data writer\n");
            return;
        }

        System.out.println("Ready to write data.");
        System.out.println("When the subscriber is ready, you can start writing.");
        System.out.print("Press CTRL+C to terminate or enter an empty line to do a clean shutdown.\n\n");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                System.out.print("Please type a message> ");
                String toWrite = reader.readLine();
                if (toWrite == null) break;     // shouldn't happen
                
                KeyedString keyedToWrite = new KeyedString("Finlandsgade",toWrite);
                if(toWrite.startsWith("A "))
                {
                	dataWriter_A.write(keyedToWrite, InstanceHandle_t.HANDLE_NIL);
                } else if(toWrite.startsWith("B "))
                {
                	dataWriter_B.write(keyedToWrite, InstanceHandle_t.HANDLE_NIL);
                }
                if (toWrite.equals("")) break;
            }
        } catch (IOException e) {
            // This exception can be thrown from the BufferedReader class
            e.printStackTrace();
        } catch (RETCODE_ERROR e) {
            // This exception can be thrown from DDS write operation
            e.printStackTrace();
        }

        System.out.println("Exiting...");
        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }
}
