package org.s3s3l.matrix.utils.metric;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import lombok.Getter;

public class MetricMeta implements Comparable<MetricMeta> {
    @Getter
    private final Map<String, String> tags = new HashMap<>();

    public MetricMeta addTag(String tagKey, String tagValue) {
        tags.put(tagKey, tagValue);
        return this;
    }

    public static void main(String[] args) {
        MetricMeta d1 = new MetricMeta();
        MetricMeta d2 = new MetricMeta();
        System.out.println(d1.compareTo(d2));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((tags == null) ? 0 : tags.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MetricMeta other = (MetricMeta) obj;
        if (tags == null) {
            if (other.tags != null)
                return false;
        } else if (this.compareTo(other) != 0)
            return false;
        return true;
    }

    @Override
    public int compareTo(MetricMeta o) {
        if (o == null) {
            return 1;
        }

        if (tags.size() > o.tags.size()) {
            return 1;
        } else if (tags.size() < o.tags.size()) {
            return -1;
        }

        for (Entry<String, String> entry : o.tags.entrySet()) {
            String oKey = entry.getKey();
            if (!tags.containsKey(oKey)) {
                return -1;
            }

            if (!Objects.equals(tags.get(oKey), entry.getValue())) {
                return -1;
            }
        }
        return 0;
    }

}
