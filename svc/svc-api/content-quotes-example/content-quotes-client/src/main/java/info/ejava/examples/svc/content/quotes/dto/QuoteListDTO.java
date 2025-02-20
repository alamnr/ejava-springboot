package info.ejava.examples.svc.content.quotes.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@XmlRootElement(name = "quotes", namespace = "urn:ejava.svc-controllers.quotes")
@XmlType()
@XmlAccessorType(XmlAccessType.NONE)

@JacksonXmlRootElement(localName = "quotes", namespace = "urn:ejava.svc-controllers.quotes")
public class QuoteListDTO {

    @XmlAttribute(required = false)
    private Integer offset;

    @XmlAttribute(required = false)
    private Integer limit;

    @XmlAttribute(required = false)
    private Integer total;

    @XmlAttribute(required = false)
    private String keywords;

    @XmlElementWrapper(name = "quotes")
    @XmlElement(name = "quote")

    @JacksonXmlElementWrapper(localName = "quotes")
    @JacksonXmlProperty(localName = "quote")
    private List<QuoteDTO> quotes;

    @XmlAttribute(required = false)
    public int getCount(){
        return quotes == null ? 0 : quotes.size(); 
    }

    public void setCount(Integer count){
        // ignored - count is determined from quotes.size
    }



}
