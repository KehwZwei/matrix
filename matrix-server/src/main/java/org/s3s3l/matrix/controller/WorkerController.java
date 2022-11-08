package org.s3s3l.matrix.controller;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.s3s3l.matrix.api.worker.WorkerDTO;
import org.s3s3l.matrix.api.worker.condition.WorkerCondition;
import org.s3s3l.matrix.component.WorkerManager;
import org.s3s3l.matrix.utils.bean.web.JsonResult;
import org.s3s3l.matrix.utils.stuctural.jackson.JacksonUtils;
import org.s3s3l.matrix.utils.web.ResultHelper;
import org.s3s3l.matrix.utils.worker.Worker;
import org.s3s3l.matrix.utils.worker.assembler.WorkerAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("worker")
public class WorkerController {

    @Autowired
    private WorkerManager workerManager;

    @GetMapping("get")
    public JsonResult<Set<WorkerDTO>> get(@RequestParam Map<String, String> params) {
        WorkerCondition condition = JacksonUtils.NON_NULL.convert(params, WorkerCondition.class);
        if (condition.getWorkType() != null) {
            return ResultHelper.success(workerManager.getWorkers().get(condition.getWorkType()).values().stream().map(
                    WorkerAssembler::toDto).collect(Collectors.toSet()));
        }

        return ResultHelper.success(workerManager.getWorkers().values().stream().flatMap(map -> map.values().stream())
                .map(WorkerAssembler::toDto)
                .sorted((w1, w2) -> w1.getWorkType().compareTo(w2.getWorkType())).collect(Collectors.toSet()));
    }

    @PostMapping("stop")
    public JsonResult<Boolean> stop(@RequestBody WorkerCondition condition) {
        Worker<?> worker = workerManager.getWorkers().get(condition.getWorkType()).get(condition.getId());
        if (worker == null) {
            return ResultHelper.fail("未找到worker");
        }
        worker.stop();
        return ResultHelper.success(true);
    }
}
