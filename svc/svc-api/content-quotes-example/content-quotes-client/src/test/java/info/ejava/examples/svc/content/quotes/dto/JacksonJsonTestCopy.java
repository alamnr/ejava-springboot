package info.ejava.examples.svc.content.quotes.dto;

import java.io.IOException;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JacksonJsonTestCopy extends MarshallingTestBaseCopy {

    private ObjectMapper mapper;
    
    
 
    @Override
    @BeforeEach
    public void init() {
        mapper = new Jackson2ObjectMapperBuilder()
                        .featuresToEnable(SerializationFeature.INDENT_OUTPUT)
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .dateFormat(new ISODateFormat())
                        .createXmlMapper(false)
                        .build();
                    
    }

    @Override
    public <T> String marshal(T object) throws IOException {
        StringWriter buffer = new StringWriter();
        mapper.writeValue(buffer, object);
        log.info("{} toJSON : {} ", object, buffer);
        return buffer.toString();
    }

    @Override
    public <T> T unmarshal(Class<T> type, String buffer) throws IOException {
        T result = mapper.readValue(buffer, type);
        log.info("{} JsonToObject : {} ",buffer, result);
        return result;
    }

    protected String  ADATE_JSON = "{\n"+ "\"date\" : \"%s\"\n" + "}";

    @Override
    protected String get_marshalled_adate(String dateText){
        return String.format(ADATE_JSON,dateText);
    }

    @Override
    public String get_date(String marshalledDates) {
        Pattern pattern = Pattern.compile(".*\"date\" : \"(.+)\".*}.*", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(marshalledDates);

        if (matcher.matches()) {
            String date = matcher.group(1);
            return date;
        }
        return null;


    }

    @Override
    protected boolean canParseFormat(String format, ZoneOffset tzo) {
        return tzo == ZoneOffset.UTC;
    }
    
}
