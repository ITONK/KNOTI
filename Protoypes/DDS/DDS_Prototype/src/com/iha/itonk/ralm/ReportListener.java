package com.iha.itonk.ralm;

import com.rti.dds.infrastructure.RETCODE_ERROR;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderAdapter;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.type.builtin.KeyedString;
import com.rti.dds.type.builtin.KeyedStringDataReader;

public class ReportListener extends DataReaderAdapter {
    /*
     * This method gets called back by DDS when one or more data samples have
     * been received.
     */
    public void on_data_available(DataReader reader) {
        KeyedStringDataReader stringReader = (KeyedStringDataReader) reader;
        SampleInfo info = new SampleInfo();
        
        while(true) {
            try {
            	KeyedString sample = new KeyedString();
                stringReader.take_next_sample(sample,info);
                if (info.valid_data) {
                	System.out.println("\nNEW INFORMATION RECEIVED:");
                	System.out.println("  Type/topic: " + stringReader.get_topicdescription().get_name());
                	System.out.println("  Key:        " + sample.key);
                	System.out.println("  Value:      " + sample.value);
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
