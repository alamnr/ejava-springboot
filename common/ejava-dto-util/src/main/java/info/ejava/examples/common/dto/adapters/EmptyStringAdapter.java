package info.ejava.examples.common.dto.adapters;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class EmptyStringAdapter extends XmlAdapter<String, String> {
    @Override
    public String unmarshal(String v) {
        return (v == null || v.trim().isEmpty()) ? null : v;
    }

    @Override
    public String marshal(String v) {
        return (v == null) ? "" : v;
    }
}