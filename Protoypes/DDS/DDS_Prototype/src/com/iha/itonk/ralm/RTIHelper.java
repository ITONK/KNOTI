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
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.type.builtin.KeyedStringDataReader;
import com.rti.dds.type.builtin.KeyedStringDataWriter;
import com.rti.dds.type.builtin.KeyedStringTypeSupport;

public final class RTIHelper {
	
	static final String[] TOPICS = {
		"Paper cut",
		"Bar fight",
		"Heart attack",
		"Robery"
	};
	
	static DomainParticipant getDomain() {
        return DomainParticipantFactory.get_instance().create_participant(
                0, // Domain ID = 0
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, 
                null, // no listener
                StatusKind.STATUS_MASK_NONE);
	}
	
	static Topic createTopic(DomainParticipant domain, String topicStr) {
        // Create the topic "A Topic" for the String type
        return domain.create_topic(
                topicStr, 
                KeyedStringTypeSupport.get_type_name(), 
                DomainParticipant.TOPIC_QOS_DEFAULT, 
                null, // no listener
                StatusKind.STATUS_MASK_NONE);
	}
	
	static KeyedStringDataWriter createKeyedStringWriter(DomainParticipant domain, Topic topic) {
        DataWriter writer = domain.create_datawriter(
                	topic, 
                    Publisher.DATAWRITER_QOS_DEFAULT,
                    null, // no listener
                    StatusKind.STATUS_MASK_NONE);
        return (KeyedStringDataWriter) writer;
	}

	static KeyedStringDataReader createKeyedStringReader(DomainParticipant domain, 
															ReportListener listener, Topic topic) {
		DataReader reader = domain.create_datareader(
					topic, 
			        Subscriber.DATAREADER_QOS_DEFAULT,
			        listener,         // Listener
			        StatusKind.DATA_AVAILABLE_STATUS);
		return (KeyedStringDataReader) reader;
	} 
}
