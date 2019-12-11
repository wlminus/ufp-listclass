package com.wlminus.ufp.web.rest;

import com.wlminus.ufp.domain.Course;
import com.wlminus.ufp.domain.Schedule;
import com.wlminus.ufp.domain.Subject;
import com.wlminus.ufp.repository.CourseRepository;
import com.wlminus.ufp.repository.ScheduleRepository;
import com.wlminus.ufp.repository.SubjectRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@RestController
@RequestMapping("/migrate")
public class MigrateResource {
    private SubjectRepository subjectRepository;
    private CourseRepository courseRepository;
    private ScheduleRepository scheduleRepository;

    public MigrateResource(SubjectRepository subjectRepository, CourseRepository courseRepository, ScheduleRepository scheduleRepository) {
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.scheduleRepository = scheduleRepository;
    }

    static String formatWeekValue(String weekValue) {
        weekValue.replaceAll("\\s+", "");

        if (weekValue.equals("NULL")) {
            return weekValue;
        } else {
            if (!weekValue.contains("-")) {
                return weekValue;
            } else {

                String weekValueTmp = weekValue;
                do {
                    int indexOfHyphen = weekValueTmp.indexOf('-');
                    String firstValue, secondValue;
                    if (indexOfHyphen <= 2) {
                        firstValue = weekValueTmp.substring(0, indexOfHyphen);
                    } else {
                        String tmp = weekValueTmp.substring(indexOfHyphen - 2, indexOfHyphen);
                        if (tmp.contains(",")) {
                            firstValue = weekValueTmp.substring(indexOfHyphen - 1, indexOfHyphen);
                        } else {
                            firstValue = tmp;
                        }
                    }

                    if ((indexOfHyphen + 2) >= (weekValueTmp.length() - 1)) {
                        secondValue = weekValueTmp.substring(indexOfHyphen + 1);
                    } else {
                        String tmp = weekValueTmp.substring(indexOfHyphen + 1, indexOfHyphen + 3);
                        if (tmp.contains(",")) {
                            secondValue = weekValueTmp.substring(indexOfHyphen + 1, indexOfHyphen + 2);
                        } else {
                            secondValue = tmp;
                        }
                    }

                    int firstValueNumber = Integer.parseInt(firstValue);
                    int secondValueNumber = Integer.parseInt(secondValue);

                    String replaceStr = "";
                    for (int i = firstValueNumber; i <= secondValueNumber; i++) {
                        replaceStr = replaceStr + i + ",";
                    }
                    replaceStr = replaceStr.substring(0, replaceStr.length() - 1);
                    weekValueTmp = weekValueTmp.substring(0, indexOfHyphen - firstValue.length()) + replaceStr + weekValueTmp.substring(indexOfHyphen + 1 + secondValue.length());

                } while (weekValueTmp.contains("-"));

                return weekValueTmp;
            }
        }
    }

    @GetMapping("/class")
    public String migrateClass() throws IOException {
        System.out.println("Start Read TKB");

        FileInputStream inputStream = new FileInputStream(new File("dataClass.xlsx"));
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rowIterator = sheet.iterator();

        String Current_MaHocPhan = "";
        double Current_MaLop = 0;

        Course currentCourse = new Course();
        Set<Schedule> currentScheduleSet = new HashSet<>();
        Subject currentSubject = new Subject();

        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            if (row.getRowNum() <= 2) {
                continue;
            }
            System.out.println("Row --- " + row.getRowNum());

            String MaHocPhan = row.getCell(5).getStringCellValue();
            double MaLop = row.getCell(3).getNumericCellValue();


            if (!MaHocPhan.equals(Current_MaHocPhan)) {
                // Neu la hoc phan moi
                currentCourse.setSchedules(currentScheduleSet);
                courseRepository.save(currentCourse);
                scheduleRepository.saveAll(currentScheduleSet);

                currentCourse = new Course();
                currentSubject = new Subject();
                currentScheduleSet.clear();

                // Neu ma hoc phan khac hien tai -> mon moi
                Current_MaHocPhan = MaHocPhan;

                String SubjectCode = row.getCell(5).getStringCellValue();
                String SubjectName = row.getCell(6).getStringCellValue();
                String SubjectType = row.getCell(16).getStringCellValue();
                String CreditValue = row.getCell(2).getStringCellValue();
                String desc = row.getCell(7).getStringCellValue();
                String Department = row.getCell(1).getStringCellValue();

                Subject newSubject = new Subject(SubjectCode, SubjectName, SubjectType, CreditValue, desc, Department, "active");
                currentSubject = this.subjectRepository.save(newSubject);

                Current_MaLop = MaLop;

                Double courseCodeNumber = row.getCell(3).getNumericCellValue();
                String courseCode = courseCodeNumber.toString();
                courseCode = courseCode.substring(0, courseCode.length() - 2);
                Long maxSlot = Long.parseLong(row.getCell(20).getStringCellValue());
                String status = row.getCell(21).getStringCellValue();

                currentCourse.setCourseCode(courseCode);
                currentCourse.setMaxSlot(maxSlot);
                currentCourse.setStatus(status);
                currentCourse.setSubject(currentSubject);

                String weekValue = formatWeekValue(row.getCell(14).getStringCellValue());
                String weekDayValue = row.getCell(9).getStringCellValue();
                String periodValue = "";
                if (row.getCell(11).getStringCellValue().equals("NULL")) {
                    periodValue = "NULL";
                } else {
                    int tietBatDau = Integer.parseInt(row.getCell(11).getStringCellValue());
                    System.out.println(tietBatDau);
                    if (tietBatDau > 13) {
                        periodValue = row.getCell(11).getStringCellValue() + "-" + row.getCell(12).getStringCellValue();
                    } else {
                        int tietKetThuc = Integer.parseInt(row.getCell(12).getStringCellValue());
                        for (int i = tietBatDau; i <= tietKetThuc; i++) {
                            periodValue = periodValue + i + ",";
                        }
                        periodValue = periodValue.substring(0, periodValue.length() - 1);
                    }
                }

                String location = row.getCell(15).getStringCellValue();

                Schedule tmpSchedule = new Schedule(weekValue, weekDayValue, periodValue, location);
                tmpSchedule.setCourse(currentCourse);
                currentScheduleSet.add(tmpSchedule);

            } else {
                // Tiep tuc mon dang xet
                if (MaLop != Current_MaLop) {
                    // Neu la lop moi -> Luu lop cu vao va bat dau lop moi
                    currentCourse.setSchedules(currentScheduleSet);
                    courseRepository.save(currentCourse);
                    scheduleRepository.saveAll(currentScheduleSet);

                    currentCourse = new Course();
                    currentScheduleSet.clear();

                    // Bat dau mot lop moi
                    Current_MaLop = MaLop;

                    Double courseCodeNumber = row.getCell(3).getNumericCellValue();
                    String courseCode = courseCodeNumber.toString();
                    courseCode = courseCode.substring(0, courseCode.length() - 2);
                    Long maxSlot = Long.parseLong(row.getCell(20).getStringCellValue());
                    String status = row.getCell(21).getStringCellValue();

                    currentCourse.setCourseCode(courseCode);
                    currentCourse.setMaxSlot(maxSlot);
                    currentCourse.setStatus(status);
                    currentCourse.setSubject(currentSubject);

                    String weekValue = formatWeekValue(row.getCell(14).getStringCellValue());
                    String weekDayValue = row.getCell(9).getStringCellValue();
                    String periodValue = "";
                    if (row.getCell(11).getStringCellValue().equals("NULL")) {
                        periodValue = "NULL";
                    } else {
                        int tietBatDau = Integer.parseInt(row.getCell(11).getStringCellValue());
                        System.out.println(tietBatDau);
                        if (tietBatDau > 13) {
                            periodValue = row.getCell(11).getStringCellValue() + "-" + row.getCell(12).getStringCellValue();
                        } else {
                            int tietKetThuc = Integer.parseInt(row.getCell(12).getStringCellValue());
                            for (int i = tietBatDau; i <= tietKetThuc; i++) {
                                periodValue = periodValue + i + ",";
                            }
                            periodValue = periodValue.substring(0, periodValue.length() - 1);
                        }
                    }

                    String location = row.getCell(15).getStringCellValue();

                    Schedule tmpSchedule = new Schedule(weekValue, weekDayValue, periodValue, location);
                    tmpSchedule.setCourse(currentCourse);
                    currentScheduleSet.add(tmpSchedule);
                } else {
                    String weekValue = formatWeekValue(row.getCell(14).getStringCellValue());
                    String weekDayValue = row.getCell(9).getStringCellValue();
                    String periodValue = "";
                    if (row.getCell(11).getStringCellValue().equals("NULL")) {
                        periodValue = "NULL";
                    } else {
                        int tietBatDau = Integer.parseInt(row.getCell(11).getStringCellValue());
                        System.out.println(tietBatDau);
                        if (tietBatDau > 13) {
                            periodValue = row.getCell(11).getStringCellValue() + "-" + row.getCell(12).getStringCellValue();
                        } else {
                            int tietKetThuc = Integer.parseInt(row.getCell(12).getStringCellValue());
                            for (int i = tietBatDau; i <= tietKetThuc; i++) {
                                periodValue = periodValue + i + ",";
                            }
                            periodValue = periodValue.substring(0, periodValue.length() - 1);
                        }
                    }

                    String location = row.getCell(15).getStringCellValue();

                    Schedule tmpSchedule = new Schedule(weekValue, weekDayValue, periodValue, location);
                    tmpSchedule.setCourse(currentCourse);
                    currentScheduleSet.add(tmpSchedule);
                }
            }


        }

        return "Done";
    }
}
