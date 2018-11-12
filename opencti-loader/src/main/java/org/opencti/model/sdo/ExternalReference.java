package org.opencti.model.sdo;

import org.opencti.model.StixBase;
import org.opencti.model.StixElement;
import org.opencti.model.database.LoaderDriver;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;
import static org.opencti.model.database.BaseQuery.from;
import static org.opencti.model.utils.StixUtils.prepare;

public class ExternalReference implements StixElement {

    private String external_id;
    private String url;
    private String source_name;
    private String description;

    @Override
    public String getEntityName() {
        return "External-Reference";
    }

    @Override
    public boolean isImplemented() {
        return true;
    }

    @Override
    public void grakn(LoaderDriver driver, Map<String, StixBase> stixElements) {
        //Must have same external_id / url  and source_name
        StringBuilder externalIdQuery = new StringBuilder("$ref isa External-Reference ");
        if (getExternal_id() != null)
            externalIdQuery.append(format("has external_id %s ", prepare(getExternal_id())));
        externalIdQuery.append(format("has source_name %s ", prepare(getSource_name())));
        externalIdQuery.append(format("has url %s ", prepare(getUrl())));

        Object externalRef = driver.execute(from("match " + externalIdQuery.toString() + "; get;"));

        if (externalRef == null) {
            StringBuilder refBuilder = new StringBuilder();
            refBuilder.append("insert $ref isa External-Reference")
                    .append(" has stix_id ").append(prepare(getId()));
            if (getExternal_id() != null)
                refBuilder.append(" has external_id ").append(prepare(getExternal_id()));
            refBuilder.append(" has source_name ").append(prepare(getSource_name()));
            if (getDescription() != null)
                refBuilder.append(" has description ").append(prepare(getDescription()));
            refBuilder.append(" has url ").append(prepare(getUrl()));
            refBuilder.append(";");
            driver.execute(from(refBuilder.toString()));
        }
    }

    @Override
    public String getId() {
        String key = getSource_name() + "-" + getUrl();
        return "external-reference--" + UUID.nameUUIDFromBytes(key.getBytes());
    }

    public String getExternal_id() {
        return external_id;
    }

    public void setExternal_id(String external_id) {
        this.external_id = external_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}