package com.wlminus.ufp.web.rest;

import com.wlminus.ufp.ListclassApp;
import com.wlminus.ufp.RedisTestContainerExtension;
import com.wlminus.ufp.domain.Subject;
import com.wlminus.ufp.repository.SubjectRepository;
import com.wlminus.ufp.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.wlminus.ufp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link SubjectResource} REST controller.
 */
@SpringBootTest(classes = ListclassApp.class)
@ExtendWith(RedisTestContainerExtension.class)
public class SubjectResourceIT {

    private static final String DEFAULT_SUBJECT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CREDIT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_CREDIT_VALUE = "BBBBBBBBBB";

    private static final String DEFAULT_DESC = "AAAAAAAAAA";
    private static final String UPDATED_DESC = "BBBBBBBBBB";

    private static final String DEFAULT_DEPARTMENT = "AAAAAAAAAA";
    private static final String UPDATED_DEPARTMENT = "BBBBBBBBBB";

    private static final String DEFAULT_STATUS = "AAAAAAAAAA";
    private static final String UPDATED_STATUS = "BBBBBBBBBB";

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restSubjectMockMvc;

    private Subject subject;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createEntity(EntityManager em) {
        Subject subject = new Subject()
            .subjectCode(DEFAULT_SUBJECT_CODE)
            .subjectName(DEFAULT_SUBJECT_NAME)
            .subjectType(DEFAULT_SUBJECT_TYPE)
            .creditValue(DEFAULT_CREDIT_VALUE)
            .desc(DEFAULT_DESC)
            .department(DEFAULT_DEPARTMENT)
            .status(DEFAULT_STATUS);
        return subject;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Subject createUpdatedEntity(EntityManager em) {
        Subject subject = new Subject()
            .subjectCode(UPDATED_SUBJECT_CODE)
            .subjectName(UPDATED_SUBJECT_NAME)
            .subjectType(UPDATED_SUBJECT_TYPE)
            .creditValue(UPDATED_CREDIT_VALUE)
            .desc(UPDATED_DESC)
            .department(UPDATED_DEPARTMENT)
            .status(UPDATED_STATUS);
        return subject;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final SubjectResource subjectResource = new SubjectResource(subjectRepository);
        this.restSubjectMockMvc = MockMvcBuilders.standaloneSetup(subjectResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    @BeforeEach
    public void initTest() {
        subject = createEntity(em);
    }

    @Test
    @Transactional
    public void createSubject() throws Exception {
        int databaseSizeBeforeCreate = subjectRepository.findAll().size();

        // Create the Subject
        restSubjectMockMvc.perform(post("/api/subjects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subject)))
            .andExpect(status().isCreated());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate + 1);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getSubjectCode()).isEqualTo(DEFAULT_SUBJECT_CODE);
        assertThat(testSubject.getSubjectName()).isEqualTo(DEFAULT_SUBJECT_NAME);
        assertThat(testSubject.getSubjectType()).isEqualTo(DEFAULT_SUBJECT_TYPE);
        assertThat(testSubject.getCreditValue()).isEqualTo(DEFAULT_CREDIT_VALUE);
        assertThat(testSubject.getDesc()).isEqualTo(DEFAULT_DESC);
        assertThat(testSubject.getDepartment()).isEqualTo(DEFAULT_DEPARTMENT);
        assertThat(testSubject.getStatus()).isEqualTo(DEFAULT_STATUS);
    }

    @Test
    @Transactional
    public void createSubjectWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = subjectRepository.findAll().size();

        // Create the Subject with an existing ID
        subject.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restSubjectMockMvc.perform(post("/api/subjects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subject)))
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllSubjects() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get all the subjectList
        restSubjectMockMvc.perform(get("/api/subjects?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(subject.getId().intValue())))
            .andExpect(jsonPath("$.[*].subjectCode").value(hasItem(DEFAULT_SUBJECT_CODE)))
            .andExpect(jsonPath("$.[*].subjectName").value(hasItem(DEFAULT_SUBJECT_NAME)))
            .andExpect(jsonPath("$.[*].subjectType").value(hasItem(DEFAULT_SUBJECT_TYPE)))
            .andExpect(jsonPath("$.[*].creditValue").value(hasItem(DEFAULT_CREDIT_VALUE)))
            .andExpect(jsonPath("$.[*].desc").value(hasItem(DEFAULT_DESC)))
            .andExpect(jsonPath("$.[*].department").value(hasItem(DEFAULT_DEPARTMENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS)));
    }

    @Test
    @Transactional
    public void getSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        // Get the subject
        restSubjectMockMvc.perform(get("/api/subjects/{id}", subject.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(subject.getId().intValue()))
            .andExpect(jsonPath("$.subjectCode").value(DEFAULT_SUBJECT_CODE))
            .andExpect(jsonPath("$.subjectName").value(DEFAULT_SUBJECT_NAME))
            .andExpect(jsonPath("$.subjectType").value(DEFAULT_SUBJECT_TYPE))
            .andExpect(jsonPath("$.creditValue").value(DEFAULT_CREDIT_VALUE))
            .andExpect(jsonPath("$.desc").value(DEFAULT_DESC))
            .andExpect(jsonPath("$.department").value(DEFAULT_DEPARTMENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS));
    }

    @Test
    @Transactional
    public void getNonExistingSubject() throws Exception {
        // Get the subject
        restSubjectMockMvc.perform(get("/api/subjects/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Update the subject
        Subject updatedSubject = subjectRepository.findById(subject.getId()).get();
        // Disconnect from session so that the updates on updatedSubject are not directly saved in db
        em.detach(updatedSubject);
        updatedSubject
            .subjectCode(UPDATED_SUBJECT_CODE)
            .subjectName(UPDATED_SUBJECT_NAME)
            .subjectType(UPDATED_SUBJECT_TYPE)
            .creditValue(UPDATED_CREDIT_VALUE)
            .desc(UPDATED_DESC)
            .department(UPDATED_DEPARTMENT)
            .status(UPDATED_STATUS);

        restSubjectMockMvc.perform(put("/api/subjects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedSubject)))
            .andExpect(status().isOk());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
        Subject testSubject = subjectList.get(subjectList.size() - 1);
        assertThat(testSubject.getSubjectCode()).isEqualTo(UPDATED_SUBJECT_CODE);
        assertThat(testSubject.getSubjectName()).isEqualTo(UPDATED_SUBJECT_NAME);
        assertThat(testSubject.getSubjectType()).isEqualTo(UPDATED_SUBJECT_TYPE);
        assertThat(testSubject.getCreditValue()).isEqualTo(UPDATED_CREDIT_VALUE);
        assertThat(testSubject.getDesc()).isEqualTo(UPDATED_DESC);
        assertThat(testSubject.getDepartment()).isEqualTo(UPDATED_DEPARTMENT);
        assertThat(testSubject.getStatus()).isEqualTo(UPDATED_STATUS);
    }

    @Test
    @Transactional
    public void updateNonExistingSubject() throws Exception {
        int databaseSizeBeforeUpdate = subjectRepository.findAll().size();

        // Create the Subject

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restSubjectMockMvc.perform(put("/api/subjects")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(subject)))
            .andExpect(status().isBadRequest());

        // Validate the Subject in the database
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteSubject() throws Exception {
        // Initialize the database
        subjectRepository.saveAndFlush(subject);

        int databaseSizeBeforeDelete = subjectRepository.findAll().size();

        // Delete the subject
        restSubjectMockMvc.perform(delete("/api/subjects/{id}", subject.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Subject> subjectList = subjectRepository.findAll();
        assertThat(subjectList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
