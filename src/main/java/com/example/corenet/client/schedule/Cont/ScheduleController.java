package com.example.corenet.client.schedule.Cont;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.corenet.client.schedule.Serv.ScheduleService;
import com.example.corenet.common.dto.ScheduleDto;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    // GET /api/schedules?userId=1&start=2025-12-01&end=2025-12-31
    @GetMapping
    public List<ScheduleDto> getSchedules(
            @RequestParam("userId") Long userId,
            @RequestParam("start") String start,
            @RequestParam("end") String end) {
        return scheduleService.getSchedules(userId, start, end);
    }

    // POST /api/schedules
    @PostMapping
    public ScheduleDto addSchedule(@RequestBody ScheduleDto schedule) {
        return scheduleService.addSchedule(schedule);
    }

    // PUT /api/schedules/{id}
    @PutMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable("id") Long id, @RequestBody ScheduleDto schedule) {
        return scheduleService.updateSchedule(id, schedule);
    }

    // DELETE /api/schedules/{id}
    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable("id") Long id) {
        scheduleService.deleteSchedule(id);
    }
}