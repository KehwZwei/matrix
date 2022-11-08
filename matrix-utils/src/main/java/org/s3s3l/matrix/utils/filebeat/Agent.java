package org.s3s3l.matrix.utils.filebeat;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    private String version;
    private String hostname;
    @JsonProperty("ephemeral_id")
    private String ephemeralId;
    private String id;
    private String name;
    private String type;
}
