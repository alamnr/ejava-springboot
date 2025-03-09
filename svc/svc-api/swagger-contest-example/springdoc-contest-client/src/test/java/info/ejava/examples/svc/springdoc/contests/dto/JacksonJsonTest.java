package info.ejava.examples.svc.springdoc.contests.dto;

import java.io.StringWriter;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import info.ejava.examples.common.time.ISODateFormat;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonJsonTest extends MarshallingTestBase { 

    private ObjectMapper mapper;

    @Override
    @BeforeEach
    public void init( ) {
        mapper = new Jackson2ObjectMapperBuilder()
                    .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                    .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                            SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                    .dateFormat(new ISODateFormat())
                    .createXmlMapper(false)
                    .build();
    }

    @Override
    protected <T> String marshall(T object) throws Exception {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        log.info("{} toJSON: {} ", object, buffer);
        return buffer.toString();
    }

    @Override
    protected <T> T unmarshal(Class<T> type, String buffer) throws Exception {
        T result = mapper.readValue(buffer, type);
        log.info("{} fromJSON: {}", buffer,result);
        return result;
    }

    
    
}
