package info.ejava.examples.common.dto;

import java.time.Instant;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import info.ejava.examples.common.dto.adapters.JaxbTimeAdapters;
import info.ejava.examples.common.dto.adapters.JsonbTimeDeserializers;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@XmlRootElement(name = "message", namespace = MessageDTO.NAMESPACE) // JAXB
@XmlAccessorType(XmlAccessType.FIELD) // JAXB java.util.Date and java.time adapters
@JacksonXmlRootElement(localName = "message", namespace = MessageDTO.NAMESPACE)
public class MessageDTO {

    public static final String NAMESPACE = "urn:ejava.util-dto";

    @JacksonXmlProperty(namespace = NAMESPACE) // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private String url;
    @JacksonXmlProperty(namespace = NAMESPACE)  // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private String method;
    @JacksonXmlProperty(namespace = NAMESPACE)  // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private int statusCode;
    @JacksonXmlProperty(namespace = NAMESPACE)  // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private String statusName;
    @JacksonXmlProperty(namespace = NAMESPACE)  // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private String message;
    @JacksonXmlProperty(namespace = NAMESPACE) // Jackson XML
    @XmlElement(namespace = NAMESPACE) // JAXB
    private String description;
    @JacksonXmlProperty(namespace = NAMESPACE)  // Jackson XML
    @JsonbTypeDeserializer(JsonbTimeDeserializers.InstantJsonbDeserializer.class)
    @XmlJavaTypeAdapter(JaxbTimeAdapters.InstantJaxbAdapter.class)
    @XmlElement(namespace = NAMESPACE) // JAXB
    private Instant timestamp;


}
