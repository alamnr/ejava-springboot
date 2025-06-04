package info.ejava.examples.common.dto.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class IntegerAdapter extends XmlAdapter<String, Integer> {
    @Override
    public Integer unmarshal(String v) {
        return (v == null || v.trim().isEmpty()) ? null : Integer.valueOf(v);
    }

    @Override
    public String marshal(Integer v) {
        return (v == null) ? "" : String.valueOf(v);
    }
}