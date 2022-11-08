package org.s3s3l.matrix.utils.metric;

import org.s3s3l.matrix.utils.metric.config.MetricCongregateType;

import lombok.Getter;

@Getter
public class MetricData {
    private double last;
    private double first;
    private long count;
    private double sum;
    private double max;
    private double min;

    public MetricData(double init) {
        this.first = init;
        this.last = init;
        this.max = init;
        this.min = init;
    }

    /**
     * 聚合数据
     * 
     * @param d
     * @return
     */
    public MetricData append(double d) {
        this.last = d;
        this.count++;
        this.sum += d;
        this.max = Math.max(this.max, d);
        this.min = Math.min(this.min, d);

        return this;
    }

    /**
     * 聚合数据
     * 
     * @param md
     * @return
     */
    public MetricData append(MetricData md) {
        this.last = md.last;
        this.count += md.count;
        this.sum += md.sum;
        this.max = Math.max(this.max, md.max);
        this.min = Math.min(this.min, md.min);
        return this;
    }

    /**
     * 获取聚合后的指标数据
     * 
     * @param congregateType 聚合类型
     * @return
     * @see MetricCongregateType
     */
    public double get(MetricCongregateType congregateType) {
        switch (congregateType) {
            case AVG:
                return this.sum / this.count;
            case COUNT:
                return this.count;
            case FIRST:
                return this.first;
            case MAX:
                return this.max;
            case MIN:
                return this.min;
            case SUM:
                return this.sum;
            case LAST:
            default:
                return this.last;
        }
    }
}
