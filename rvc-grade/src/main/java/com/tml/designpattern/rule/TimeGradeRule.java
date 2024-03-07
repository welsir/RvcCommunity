package com.tml.designpattern.rule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tml.domain.dto.MqConsumerTaskDto;
import io.github.common.logger.CommonLogger;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Scope("prototype")
/**
 * 解析字符串参考: [HH:mm~HH:mm]
 * 时间格式 HH:mm
 * 时间区间要求 HH:mm ~ HH:mm 用 ~ 字符串关联
 * 多个时间区即表示[HH:mm~HH:mm,HH:mm~HH:mm]
 */
public class TimeGradeRule extends AbstractGradeRule{

    @Resource
    private CommonLogger logger;

    private List<String> timeLine;
    @Override
    public String getRuleId() {
        return "002";
    }

    @Override
    public String getRuleName() {
        return "时间规则";
    }

    @Override
    protected boolean ruleParser0(JSONObject originData) {
        timeLine = new ArrayList<>();
        JSONArray timeLineJson = originData.getJSONArray("timeLine");
        timeLine.addAll(timeLineJson.stream().map(Object::toString).collect(Collectors.toList()));
        return true;
    }


    @Override
    public boolean check() {
        return isTimeInRange(LocalDateTime.now(), getRange());
    }

    @Override
    public void lastWord() {
        logger.error("%s 的 %s 任务时间规则校验失败",taskDto.getUserId(),taskDto.getPath());
    }

    private List<TimeRange> getRange(){
        List<TimeRange> timeRanges = new ArrayList<>();
        for (String time : timeLine) {
            String[] timeRangeArray = time.split("~");
            String startTimeString = timeRangeArray[0].trim();
            String endTimeString = timeRangeArray[1].trim();

            LocalDateTime currentDate = LocalDateTime.now();

            // 将时间字符串转换为 LocalTime
            LocalTime startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm"));
            if (endTime.isBefore(startTime)) {
                // 如果结束时间在开始时间之前，表示跨越了午夜，加一天
                currentDate = currentDate.plusDays(1);
            }
            // 使用当前日期和解析得到的时间信息创建 LocalDateTime
            LocalDateTime startDateTime = LocalDateTime.of(currentDate.toLocalDate(), startTime);
            LocalDateTime endDateTime = LocalDateTime.of(currentDate.toLocalDate(), endTime);
            timeRanges.add(new TimeRange(startDateTime, endDateTime));
        }
        return timeRanges;
    }


    private boolean isTimeInRange(LocalDateTime time, List<TimeRange> timeRanges) {
        return timeRanges.stream().anyMatch(range -> range.getStartTime().isBefore(time) && range.getEndTime().isAfter(time));
    }

    @Data
    @AllArgsConstructor
    class TimeRange{
        private LocalDateTime startTime;
        private LocalDateTime endTime;
    }

}
