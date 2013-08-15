package com.enonic.cms.web.status;

import java.util.Collections;
import java.util.List;

import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public final class StatusController
{
    private List<StatusInfoBuilder> infoBuilders;

    @ResponseBody
    @RequestMapping(value = "/status", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public String serveStatus()
    {
        final ObjectNode json = createStatus();
        return serializeJson( json );
    }

    private ObjectNode createStatus()
    {
        final ObjectNode node = JsonNodeFactory.instance.objectNode();
        for ( final StatusInfoBuilder builder : this.infoBuilders )
        {
            builder.addInfo( node );
        }

        return node;
    }

    private String serializeJson( final ObjectNode json )
    {
        return json.toString();
    }

    @Autowired
    public void setInfoBuilders( final List<StatusInfoBuilder> infoBuilders )
    {
        this.infoBuilders = infoBuilders;
        Collections.sort( this.infoBuilders );
    }
}
