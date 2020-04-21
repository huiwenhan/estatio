package org.estatio.module.application.app;

import java.sql.Timestamp;

import org.assertj.core.api.Assertions;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Test;

public class EstatioClockTest {

    @Test
    public void time() {

        // given
        EstatioClock clock = new EstatioClock();
        // when
        final long time = clock.time();
        final LocalDateTime timeAsLocalDateTime = EstatioClock.getTimeAsLocalDateTime();
        final LocalDate localDate = EstatioClock.getTimeAsLocalDate();
        final DateTime dateTime = EstatioClock.getTimeAsDateTime();
        final Timestamp timeAsJavaSqlTimestamp = EstatioClock.getTimeAsJavaSqlTimestamp();

        // then
        Assertions.assertThat(time).isNotZero();
        Assertions.assertThat(timeAsLocalDateTime).isNotNull();
        Assertions.assertThat(localDate).isNotNull();
        Assertions.assertThat(dateTime).isNotNull();
        Assertions.assertThat(timeAsJavaSqlTimestamp).isNotNull();

    }
}