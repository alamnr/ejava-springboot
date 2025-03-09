package info.ejava.examples.svc.springdoc.contests.dto;

import java.io.IOException;
import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import info.ejava.examples.common.time.ISODateFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonXmlTest extends MarshallingTestBase {

    private XmlMapper mapper;

    @Override
    @BeforeEach
    public void init() {
        mapper = new Jackson2ObjectMapperBuilder()
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                            SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                    .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                    .dateFormat(new ISODateFormat())
                    .createXmlMapper(true)
                    .build();

    }

    @Override
    protected <T> String marshall(T object) throws IOException {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        log.info("{} toXML: {} ", object, buffer);
        return buffer.toString();
    }


    @Override
    protected <T> T unmarshal(Class<T> type, String buffer) throws Exception {
        T result = mapper.readValue(buffer, type);
        log.info("{} fromXML : {} ", buffer,result);
        return result;
    }

    
}
