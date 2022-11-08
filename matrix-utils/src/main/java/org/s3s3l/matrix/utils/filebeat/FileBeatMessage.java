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
public class FileBeatMessage {
    @JsonProperty("@timestamp")
    private String _timestamp;
    @JsonProperty("@metadata")
    private Metadata _metadata;

    private Input input;
    private Event event;
    private Fileset fileset;
    private Agent agent;
    private Host host;
    private Log log;
    private Service service;
    private Ecs ecs;

    /**
     * 主要的消息体
     */
    private String message;
}
