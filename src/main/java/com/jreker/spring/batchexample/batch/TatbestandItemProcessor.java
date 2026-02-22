package com.jreker.spring.batchexample.batch;

import com.jreker.spring.batchexample.model.Tatbestand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.ItemProcessor;

public class TatbestandItemProcessor implements ItemProcessor<Tatbestand, Tatbestand> {
    private static final Logger logger = LoggerFactory.getLogger(BatchConfiguration.class);
    @Override
    public Tatbestand process(Tatbestand tatbestand) throws Exception {
        logger.info("item processed");
        if(tatbestand.getBetrag() >= 100)
        {
            return tatbestand;
        } else {
            return null;
        }
    }
}
