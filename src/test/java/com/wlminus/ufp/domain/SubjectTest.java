package com.wlminus.ufp.domain;

import com.wlminus.ufp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SubjectTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Subject.class);
        Subject subject1 = new Subject();
        subject1.setId(1L);
        Subject subject2 = new Subject();
        subject2.setId(subject1.getId());
        assertThat(subject1).isEqualTo(subject2);
        subject2.setId(2L);
        assertThat(subject1).isNotEqualTo(subject2);
        subject1.setId(null);
        assertThat(subject1).isNotEqualTo(subject2);
    }
}
